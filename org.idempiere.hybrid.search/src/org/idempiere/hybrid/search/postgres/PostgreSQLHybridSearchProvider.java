/***********************************************************************
 * This file is part of iDempiere ERP Open Source                      *
 * http://www.idempiere.org                                            *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - hengsin                         								   *
 **********************************************************************/
package org.idempiere.hybrid.search.postgres;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.adempiere.base.search.ISearchProvider;
import org.adempiere.base.search.SearchResult;
import org.compiere.model.MSearchDefinition;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTable;
import org.compiere.model.MWindow;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.idempiere.hybrid.search.IEmbeddingService;
import org.idempiere.hybrid.search.IHybridSearchProvider;
import org.idempiere.hybrid.search.model.MSearchIndex;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.langchain4j.data.segment.TextSegment;

import static org.adempiere.base.markdown.IMarkdownRenderer.MARKDOWN_CLOSING_TAG;
import static org.adempiere.base.markdown.IMarkdownRenderer.MARKDOWN_OPENING_TAG;

@Component(service = { ISearchProvider.class, IHybridSearchProvider.class }, immediate = true)
public class PostgreSQLHybridSearchProvider implements IHybridSearchProvider {

    private static final CLogger log = CLogger.getCLogger(PostgreSQLHybridSearchProvider.class);

    private IEmbeddingService embeddingService;

    private static final String FTS_INDEX_COLUMN = "FTSIndex";

    private static final String EMBEDDING_COLUMN = "EmbeddingVector";

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, target = "(|(database=PostgreSQL)(database=All))")
    public void bindEmbeddingService(IEmbeddingService service) {
        this.embeddingService = service;
    }

    public void unbindEmbeddingService(IEmbeddingService service) {
        this.embeddingService = null;
    }

    @Override
    public boolean accept(MSearchDefinition msd) {
        return DB.isPostgreSQL()
                && MSearchIndex.SEARCHTYPE_HYBRID.equals(msd.getSearchType())
                && msd.getAD_Table_ID() > 0;
    }

    @Override
    public List<SearchResult> search(MSearchDefinition msd, String searchString, int maxResults, int page) {
        List<SearchResult> list = new ArrayList<>();

        maxResults = MSysConfig.getIntValue(MAX_RESULT_HYBRID_SEARCH, maxResults);
        
        MTable table = MTable.get(Env.getCtx(), msd.getAD_Table_ID());
        if (table == null)
            return list;

        MWindow window = null;
        if (msd.getAD_Window_ID() > 0)
            window = MWindow.get(Env.getCtx(), msd.getAD_Window_ID());

        if (window == null && msd.getPO_Window_ID() > 0)
            window = MWindow.get(Env.getCtx(), msd.getPO_Window_ID());

        String adLanguage = Env.getAD_Language(Env.getCtx());
        String language = toTsVectorLanguage(adLanguage);

        float[] embedding = null;
        if (embeddingService != null) {
            try {
            	String formattedQuery = "task: retrieval | query: " + searchString;
            	embedding = embeddingService.generateEmbedding(formattedQuery);
            } catch (Exception e) {
                log.log(Level.WARNING, "Failed to generate embedding: " + e.getMessage(), e);
            }
        }

        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        if (embedding != null) {
            // Hybrid Search with RRF
            sql.append("WITH text_search AS (")
               .append(" SELECT Record_ID as id,")
               .append(" RANK() OVER (ORDER BY ts_rank_cd(").append(FTS_INDEX_COLUMN) //rank, lower is better
               .append(", plainto_tsquery('").append(language)
               .append("', ?)) DESC) as rank, ContentText")
               .append(" FROM HYS_SearchIndex")
               .append(" WHERE ").append(FTS_INDEX_COLUMN)
               .append(" @@ plainto_tsquery('").append(language).append("', ?)")
               .append(" AND AD_Table_ID=? AND AD_Client_ID IN (0,?) AND AD_Language=? AND HYS_IndexStatus='I' AND AD_SearchDefinition_ID=?")
               .append(" ORDER BY ts_rank_cd(").append(FTS_INDEX_COLUMN).append(", plainto_tsquery('").append(language).append("', ?)) DESC")
               .append(" LIMIT ?),");

            sql.append(" vector_search AS (")
	           .append(" SELECT Record_ID as id, ")
	           .append(" 1 - (").append(EMBEDDING_COLUMN).append(" <=> ").append("?::vector").append(") as similarity, HYS_SearchIndex.ContentText") //higher = more similarity
	           .append(" FROM HYS_SearchIndex_Embedding JOIN HYS_SearchIndex USING (HYS_SearchIndex_ID)")
	           .append(" WHERE AD_Table_ID=? AND AD_Client_ID IN (0, ?) AND AD_Language=? AND HYS_IndexStatus='I' AND AD_SearchDefinition_ID=?")	  
	           .append(" ORDER BY similarity DESC LIMIT ?),")
	           .append(" vector_score AS (SELECT id, Sum(similarity) as similarity, ContentText")
	           .append(" FROM vector_search")
	           .append(" GROUP BY id, ContentText")
	           .append(" LIMIT ?),")
	           .append(" vector_rank as (SELECT vector_score.id,")
	           .append(" Rank() Over (Order By vector_score.similarity Desc) as rank, vector_score.ContentText") //rank, lower is better
	           .append(" FROM vector_score")
	           .append(" ORDER BY rank ASC LIMIT ?),");
	            
            // convert rank to relevance score (higher is better)
            sql.append(" rrf AS (SELECT text_search.id, 1.0 / (60+text_search.rank) as score, text_search.ContentText as ContentText FROM text_search") 
                .append(" UNION ALL")
                .append(" SELECT vector_rank.id, 1.0 / (60+vector_rank.rank) as score, vector_rank.ContentText as ContentText FROM vector_rank)");
            
            // combine full text and vector search results
            sql.append(" SELECT rrf.id as id, Sum(rrf.score) as score, rrf.ContentText")
               .append(" FROM rrf")
               .append(" GROUP BY rrf.id, rrf.ContentText")
               .append(" ORDER BY score DESC LIMIT ?");

            // Params for Text Search
            params.add(searchString);
            params.add(searchString);
            params.add(table.get_ID());
            params.add(Env.getAD_Client_ID(Env.getCtx()));
            params.add(adLanguage);
            params.add(msd.getAD_SearchDefinition_ID());
            params.add(searchString);
            params.add(maxResults * (page + 1) * 2);

            // Params for Vector Search
            String vectorString = toVectorString(embedding);
            params.add(vectorString);
            params.add(table.get_ID());
            params.add(Env.getAD_Client_ID(Env.getCtx()));
            params.add(adLanguage);
            params.add(msd.getAD_SearchDefinition_ID());
            params.add(maxResults * (page + 1) * 3);
            params.add(maxResults * (page + 1) * 2);
            params.add(maxResults * (page + 1) * 2);

            // Params for Final Limit
            params.add(maxResults);
        } else {
            // Standard Full Text on HYS_SearchIndex
            sql = new StringBuilder("SELECT s.Record_ID, ")
                    .append("ts_rank_cd(s.").append(FTS_INDEX_COLUMN)
                    .append(", plainto_tsquery('").append(language).append("', ?)) as rank, s.ContentText");

            sql.append(" FROM HYS_SearchIndex s ");
            sql.append(" WHERE s.").append(FTS_INDEX_COLUMN)
                    .append(" @@ plainto_tsquery('").append(language).append("', ?)");

            sql.append(" AND s.AD_Table_ID=? AND s.AD_Client_ID=? AND s.AD_Language=? AND HYS_IndexStatus='I' AND AD_SearchDefinition_ID=?");
            sql.append(" ORDER BY rank DESC LIMIT ?");

            params.add(searchString);
            params.add(searchString);
            params.add(table.get_ID());
            params.add(Env.getAD_Client_ID(Env.getCtx()));
            params.add(adLanguage);
            params.add(msd.getAD_SearchDefinition_ID());
            params.add(maxResults);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = DB.prepareStatement(sql.toString(), null);
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                double rank = rs.getDouble(2);
                SearchResult result = new SearchResult();
                StringBuilder contentText = new StringBuilder(MARKDOWN_OPENING_TAG)
                		.append(rs.getString(3))
                		.append(MARKDOWN_CLOSING_TAG);
                result.setLabel(contentText.toString());
                result.setRecordId(id);
                if (window != null) {
                    result.setWindowName(window.get_Translation("Name"));
                    result.setWindowId(window.getAD_Window_ID());
                }
                result.setTableName(table.getTableName());

                Map<String, Object> valueMap = new HashMap<String, Object>();
                valueMap.put("score", rank);
                result.setValueMap(valueMap);
                result.setRelevanceScore(rank);

                list.add(result);
            }
        } catch (SQLException e) {
            SearchResult result = new SearchResult();
            result.setRecordId(-1);
            result.setLabel(Msg.getMsg(Env.getCtx(), "DBExecuteError") + ": " + e.getLocalizedMessage());
            list.add(result);
            log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            DB.close(rs, pstmt);
        }

        return list;
    }

    @Override
    public void updateIndex(MSearchIndex index) {
        String contentText = index.getContentText();
        if (contentText == null || contentText.trim().isEmpty()) {
            return;
        }

        String adLanguage = index.getAD_Language();
        String language = toTsVectorLanguage(adLanguage);

        StringBuilder sql = new StringBuilder("UPDATE HYS_SearchIndex SET ")
                .append(FTS_INDEX_COLUMN).append(" = to_tsvector('")
                .append(language).append("', ContentText)")
                .append(" WHERE HYS_SearchIndex_ID = ?");
        DB.executeUpdateEx(sql.toString(), new Object[] {index.getHYS_SearchIndex_ID()}, index.get_TrxName());

        DB.executeUpdateEx("DELETE FROM HYS_SearchIndex_Embedding WHERE HYS_SearchIndex_ID = ?", new Object[] {index.getHYS_SearchIndex_ID()}, index.get_TrxName());
        if (embeddingService != null) {
        	String insertSQL = """
        			INSERT INTO HYS_SearchIndex_Embedding (HYS_SearchIndex_ID, EmbeddingVector, ContentText, Updated)
        			VALUES (?, ?::vector, ?, ?)
        			""";
        	JsonObject json = JsonParser.parseString(index.getJsonData()).getAsJsonObject();
        	for(String key : json.keySet()) {
        		JsonElement element = json.get(key);
        		String segment = null;
        		if (element.isJsonPrimitive()) {
        			segment = key + ": " + element.getAsString();
        		} else if (element.isJsonArray()) {
        			String csv = StreamSupport.stream(element.getAsJsonArray().spliterator(), false)
                            .map(JsonElement::getAsString)
                            .collect(Collectors.joining(","));
        			segment = key + ": " + csv;
        		} else {
        			segment = key + ": " + element.toString();
        		}
        		
        		List<TextSegment> segments = embeddingService.splitToSegments(segment);
        		for(TextSegment textSegment : segments) {
	        		try {
	        			String t = textSegment.text();
						DB.executeUpdateEx(insertSQL, new Object[] {
								index.getHYS_SearchIndex_ID(),
								toVectorString(embeddingService.generateEmbedding("task: retrieval | document: " + t)), t, new Timestamp(System.currentTimeMillis())
						}, index.get_TrxName());
					} catch (Exception e) {
						log.log(Level.SEVERE, e.getLocalizedMessage(), e);
					}
        		}
        	}
        }        
    }

	private String toTsVectorLanguage(String adLanguage) {
		String language = "english";        
        if (adLanguage != null) {
            if (adLanguage.startsWith("es"))
                language = "spanish";
            else if (adLanguage.startsWith("fr"))
                language = "french";
            else if (adLanguage.startsWith("de"))
                language = "german";
            else if (adLanguage.startsWith("it"))
                language = "italian";
            else if (adLanguage.startsWith("pt"))
                language = "portuguese";
            else if (adLanguage.startsWith("zh"))
				language = "chinese";
			else if (adLanguage.startsWith("nl"))
				language = "dutch";
			else if (adLanguage.startsWith("in"))
				language = "indonesian";
			else if (adLanguage.startsWith("ja"))
				language = "japanese";
			else if (adLanguage.startsWith("ko"))
				language = "korean";
			else if (adLanguage.startsWith("fi"))
				language = "finnish";
			else if (adLanguage.startsWith("da"))
				language = "danish";
			else if (adLanguage.startsWith("ms"))
				language = "malay";
			else if (adLanguage.startsWith("sk"))
				language = "slovak";
			else if (adLanguage.startsWith("th"))
				language = "thai";
			else if (adLanguage.startsWith("hi"))
				language = "hindi";
			else if (adLanguage.startsWith("no"))
				language = "norwegian";
			else if (adLanguage.startsWith("tr"))
				language = "turkish";
			else if (adLanguage.startsWith("sv"))
				language = "swedish";
			else if (adLanguage.startsWith("ru"))
				language = "russian";
			else if (adLanguage.startsWith("el"))
				language = "greek";
			else if (adLanguage.startsWith("ar"))
				language = "arabic";
        }
		return language;
	}

    private String toVectorString(float[] embedding) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0)
                sb.append(",");
            sb.append(embedding[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}

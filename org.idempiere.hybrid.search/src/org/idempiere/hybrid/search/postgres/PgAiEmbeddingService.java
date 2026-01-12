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
import java.util.List;

import org.compiere.model.MSysConfig;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.idempiere.hybrid.search.IEmbeddingService;
import org.osgi.service.component.annotations.Component;

import dev.langchain4j.data.segment.TextSegment;

@Component(service = IEmbeddingService.class, immediate = true, property = {"service.ranking:Integer=0", "database=PostgreSQL"})
public class PgAiEmbeddingService implements IEmbeddingService {

    private static final String OLLAMA = "ollama";
    private static final String OPENAI = "openai";
    private static final String DEFAULT_PROVIDER = OLLAMA;
    private static final String DEFAULT_OPENAI_MODEL = "text-embedding-3-small";
    private static final String DEFAULT_OLLAMA_MODEL = "nomic-embed-text";

    public static final String SYSCONFIG_PGAI_PROVIDER = "PGAI_PROVIDER";
    public static final String SYSCONFIG_PGAI_MODEL = "PGAI_MODEL";

    @Override
    public float[] generateEmbedding(String text) throws Exception {
        String provider = MSysConfig.getValue(SYSCONFIG_PGAI_PROVIDER, DEFAULT_PROVIDER,
                Env.getAD_Client_ID(Env.getCtx()));
        String model = OLLAMA.equalsIgnoreCase(provider)
                ? MSysConfig.getValue(SYSCONFIG_PGAI_MODEL, DEFAULT_OLLAMA_MODEL, Env.getAD_Client_ID(Env.getCtx()))
                : MSysConfig.getValue(SYSCONFIG_PGAI_MODEL, DEFAULT_OPENAI_MODEL, Env.getAD_Client_ID(Env.getCtx()));

        StringBuilder sql = new StringBuilder();
        if (OPENAI.equalsIgnoreCase(provider)) {
            sql.append("SELECT openai_embed(?, ?)");
        } else if (OLLAMA.equalsIgnoreCase(provider)) {
            sql.append("SELECT ollama_embed(?, ?)");
        } else {
            sql.append("SELECT pgai_embed(?, ?)");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = DB.prepareStatement(sql.toString(), null);
            pstmt.setString(1, model);
            pstmt.setString(2, text);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Object vecObj = rs.getObject(1);
                if (vecObj != null) {
                    return parseVector(vecObj.toString());
                }
            }
        } catch (SQLException e) {
            throw new Exception("Failed to generate embedding from DB: " + e.getMessage(), e);
        } finally {
            DB.close(rs, pstmt);
        }
        return null;
    }

    private float[] parseVector(String vecString) {
        if (Util.isEmpty(vecString) || "[]".equals(vecString))
            return new float[0];

        // Remove brackets
        String clean = vecString.replace("[", "").replace("]", "");
        String[] parts = clean.split(",");
        float[] vector = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try {
                vector[i] = Float.parseFloat(parts[i].trim());
            } catch (NumberFormatException e) {
                vector[i] = 0.0f;
            }
        }
        return vector;
    }

    @Override
    public String getName() {
        return "pgai";
    }
    
    @Override
	public List<TextSegment> splitToSegments(String content) {
		return List.of(TextSegment.from(content));
	}
}

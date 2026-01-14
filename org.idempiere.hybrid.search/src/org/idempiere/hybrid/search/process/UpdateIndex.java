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
package org.idempiere.hybrid.search.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.base.Service;
import org.compiere.model.MColumn;
import org.compiere.model.MLookup;
import org.compiere.model.MRefList;
import org.compiere.model.MSearchDefinition;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.idempiere.hybrid.search.IHybridSearchProvider;
import org.idempiere.hybrid.search.model.I_HYS_SearchIndex;
import org.idempiere.hybrid.search.model.MSearchColumn;
import org.idempiere.hybrid.search.model.MSearchIndex;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Process to update hybrid search index.
 * @author hengsin
 */
public class UpdateIndex extends SvrProcess {

	@Override
	protected void prepare() {
	}

	@Override
	protected String doIt() throws Exception {
		int count = 0;
		int error = 0;
		List<MSearchIndex> list = new Query(getCtx(), I_HYS_SearchIndex.Table_Name,
				I_HYS_SearchIndex.COLUMNNAME_HYS_IndexStatus + "=?", get_TrxName())
				.setParameters(MSearchIndex.INDEXSTATUS_Pending)
				.setOnlyActiveRecords(true)
				.list();

		for (MSearchIndex index : list) {
			try {
				if (updateIndex(index)) {
					count++;
				} else {
					error++;
				}
			} catch (Exception e) {
				log.log(Level.SEVERE, "Error indexing record " + index.getRecord_ID(), e);
				index.setHYS_IndexStatus(MSearchIndex.INDEXSTATUS_Error);
				index.saveCrossTenantSafeEx();
				error++;
			}
		}

		return "Indexed: " + count + (error > 0 ? ", Errors: " + error : "");
	}

	private boolean updateIndex(MSearchIndex index) {
		MSearchDefinition msd = new MSearchDefinition(getCtx(), index.getAD_SearchDefinition_ID(), get_TrxName());
		MSearchColumn[] columns = MSearchColumn.getColumns(msd.getAD_SearchDefinition_ID(), get_TrxName());
		if (columns.length == 0)
			return false;

		MTable table = MTable.get(getCtx(), index.getAD_Table_ID());
		PO po = table.getPO(index.getRecord_ID(), get_TrxName());
		if (po == null) {
			index.setHYS_IndexStatus(MSearchIndex.INDEXSTATUS_Error);
			index.saveCrossTenantSafeEx();
			return false;
		}

		StringBuilder sb = new StringBuilder();
		JsonObject json = new JsonObject();
		for (MSearchColumn col : columns) {
			MColumn column = MColumn.get(getCtx(), col.getAD_Column_ID());
			if (Util.isEmpty(col.getColumnSQL(), true)) {
				appendColumnValue(index, po, column, sb, json);
			} else {
				appendColumnQueryValues(index, po, column, col.getColumnSQL(), sb, json);
			}
		}

		index.setContentText(sb.toString());
		index.setJsonData(json.toString());
		index.saveCrossTenantSafeEx();

		IHybridSearchProvider provider = findProvider(msd);
		if (provider != null) {
			provider.updateIndex(index);
			index.setHYS_IndexStatus(MSearchIndex.INDEXSTATUS_Indexed);
			index.saveCrossTenantSafeEx();
			return true;
		} else {
			log.log(Level.WARNING, "No hybrid search provider found for definition: " + msd.getName());
			index.setHYS_IndexStatus(MSearchIndex.INDEXSTATUS_Error);
			index.saveCrossTenantSafeEx();
		}

		return false;
	}

	private void appendColumnQueryValues(MSearchIndex index, PO po, MColumn column, String columnSQL, StringBuilder content,
			JsonObject json) {
		
		int paramCount = 0;
		for (int i = 0; i < columnSQL.length(); i++) {
			if (columnSQL.charAt(i) == '?')
				paramCount++;
		}

		// ? 1 is column value, ? 2 is language
		if (paramCount != 1 && paramCount != 2) {
			log.log(Level.WARNING, "Unsupported number of parameters in ColumnSQL: " + columnSQL);
			return;
		}
		
		Object val = po.get_Value(column.getColumnName());
		if (val != null) {
			String sql = DB.getDatabase().addPagingSQL(columnSQL, 1, 5);
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(sql, get_TrxName());
				pstmt.setObject(1, val);
				if (paramCount == 2)
					pstmt.setString(2, index.getAD_Language());
				rs = pstmt.executeQuery();
				
				ResultSetMetaData md = rs.getMetaData();
				int colCount = md.getColumnCount();
				
				while (rs.next()) {
					for (int i = 1; i <= colCount; i++) {
						String colName = md.getColumnLabel(i);
						Object colVal = rs.getObject(i);
						if (colVal == null) continue;
						
						if (content.length() > 0)
							content.append("<br/>");
						content.append("**").append(colName).append(":** ").append(colVal.toString());
						
						JsonElement newVal = null;
						if (colVal instanceof Number n)
							newVal = new JsonPrimitive(n);
						else if (colVal instanceof Boolean b)
							newVal = new JsonPrimitive(b);
						else if (colVal instanceof java.sql.Timestamp ts)
							newVal = new JsonPrimitive(ts.toInstant().toString());
						else
							newVal = new JsonPrimitive(colVal.toString());
						
						if (json.has(colName)) {
							JsonElement existing = json.get(colName);
							if (existing.isJsonArray()) {
								existing.getAsJsonArray().add(newVal);
							} else {
								JsonArray arr = new JsonArray();
								arr.add(existing);
								arr.add(newVal);
								json.add(colName, arr);
							}
						} else {
							json.add(colName, newVal);
						}
					}
				}
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Error executing columnSQL: " + columnSQL, e);
			} finally {
				DB.close(rs, pstmt);
			}
		}
	}

	private void appendColumnValue(MSearchIndex index, PO po, MColumn column, StringBuilder content, JsonObject json) {
		Object val = null;
		if (DisplayType.isText(column.getAD_Reference_ID()) && column.isTranslated()) {
			val = po.get_Translation(column.getColumnName(), index.getAD_Language());
		} else {
			val = po.get_Value(column.getColumnName());
		}
		if (val != null) {
			if (content.length() > 0)
				content.append("<br/>");
			String label = column.get_Translation("Name", index.getAD_Language());
			String text = null;
			if (val instanceof Integer id && DisplayType.isLookup(column.getAD_Reference_ID())) {
				text = MLookup.getIdentifier(MTable.getTable_ID(column.getReferenceTableName()), id);
			} else if (val instanceof String strVal && DisplayType.isList(column.getAD_Reference_ID()) && column.getAD_Reference_Value_ID() > 0) {
				text = MRefList.get(Env.getCtx(), column.getAD_Reference_Value_ID(), strVal, get_TrxName()).getName();
			} else {
				text = val.toString();
			}
			content.append("**").append(label).append(":** ").append(text);
			if (DisplayType.isNumeric(column.getAD_Reference_ID()) && val instanceof Number numberVal)
				json.addProperty(label, numberVal);
			else if (DisplayType.isDate(column.getAD_Reference_ID()) && val instanceof java.sql.Timestamp ts)
				json.addProperty(label, ts.toInstant().toString());
			else
				json.addProperty(label, text);
		}
	}

	private IHybridSearchProvider findProvider(MSearchDefinition msd) {
		List<IHybridSearchProvider> providers = Service.locator().list(IHybridSearchProvider.class).getServices();
		for (IHybridSearchProvider provider : providers) {
			if (provider.accept(msd)) {
				return provider;
			}
		}
		return null;
	}
}

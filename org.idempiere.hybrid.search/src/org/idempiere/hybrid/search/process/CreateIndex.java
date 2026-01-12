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
import java.sql.Timestamp;

import org.compiere.model.MSearchDefinition;
import org.compiere.model.MSequence;
import org.compiere.model.MTable;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.idempiere.hybrid.search.model.MSearchColumn;
import org.idempiere.hybrid.search.model.MSearchIndex;

public class CreateIndex extends SvrProcess {

	public CreateIndex() {
	}

	@Override
	protected void prepare() {
	}

	@Override
	protected String doIt() throws Exception {
		MSearchDefinition msd = new MSearchDefinition(getCtx(), getRecord_ID(), get_TrxName());
		MTable table = MTable.get(getCtx(), msd.getAD_Table_ID());
		MSearchColumn[] columns = MSearchColumn.getColumns(msd.getAD_SearchDefinition_ID(), get_TrxName());
		if (columns.length == 0)
			return null;
		
		StringBuilder insertBuilder = 
				new StringBuilder("INSERT INTO HYS_SearchIndex (AD_Client_ID, AD_Org_ID, HYS_SearchIndex_ID, HYS_SearchIndex_UU,");
		insertBuilder.append("AD_SearchDefinition_ID, AD_Table_ID, Record_ID, AD_Language, HYS_IndexStatus, Created, CreatedBy, Updated, UpdatedBy, IsActive) ")
			.append("VALUES (?,?,nextidfunc(?,'N'),generate_uuid(), ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Y')");
		String insertSQL = insertBuilder.toString();
		int sequenceId = MSequence.get(getCtx(), "HYS_SearchIndex").get_ID();

		StringBuilder sqlBuilder = new StringBuilder("SELECT AD_Client_ID, AD_Org_ID");
		sqlBuilder.append(",").append(table.getTableName()).append("_ID");
		sqlBuilder.append(" FROM ").append(table.getTableName());
		sqlBuilder.append(" WHERE IsActive='Y'");
		sqlBuilder.append(" AND ").append(table.getTableName())
			.append("_ID NOT IN (SELECT Record_ID FROM HYS_SearchIndex WHERE AD_SearchDefinition_ID=?")
			.append(" AND AD_Table_ID=? AND AD_Language=?").append(")");
		String sql = sqlBuilder.toString();
		sql = DB.getDatabase().addPagingSQL(sql, 1, 1001);
		int inserted = 0;
		int error = 0;
		boolean more = false;
		for (var lang : Env.getLoginLanguages()) {
			try (PreparedStatement stmt = DB.prepareStatement(sql, get_TrxName())) {
				stmt.setInt(1, msd.getAD_SearchDefinition_ID());
				stmt.setInt(2, msd.getAD_Table_ID());
				stmt.setString(3, lang);
				var rs = stmt.executeQuery();
				while (rs.next()) {
					if (inserted + error == 1000) {
						more = true;
						break;						
					}
					int AD_Client_ID = rs.getInt(1);
					int AD_Org_ID = rs.getInt(2);
					int Record_ID = rs.getInt(3);
					Object[] params = new Object[] {
							AD_Client_ID,
							AD_Org_ID,
							sequenceId,
							msd.getAD_SearchDefinition_ID(),
							msd.getAD_Table_ID(),
							Record_ID,
							lang,
							MSearchIndex.INDEXSTATUS_Pending,
							new Timestamp(System.currentTimeMillis()),
							Env.getAD_User_ID(getCtx()),
							new Timestamp(System.currentTimeMillis()),
							Env.getAD_User_ID(getCtx())
					};
					try {
						DB.executeUpdateEx(insertSQL, params, get_TrxName());
						inserted++;
					} catch (Exception e) {
						log.severe(e.getMessage());
						error++;
					}					
				}
			} catch (Exception e) {
				log.severe(e.getMessage());
			}
			if (inserted + error == 1000) {
				break;
			}
		}

		String result = "Created: " + inserted + ", Errors: " + error;
		if (more) {
			result += " (More records to process than the limit, please run again)";
		}
		return result;
	}

}

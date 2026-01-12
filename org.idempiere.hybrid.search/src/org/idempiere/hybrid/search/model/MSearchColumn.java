package org.idempiere.hybrid.search.model;

import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import org.compiere.model.Query;
import org.compiere.util.Env;

public class MSearchColumn extends X_HYS_SearchColumn {

	private static final long serialVersionUID = -5374033153676860405L;

	public MSearchColumn(Properties ctx, int AD_SearchColumn_ID, String trxName) {
		super(ctx, AD_SearchColumn_ID, trxName);
	}

	public MSearchColumn(Properties ctx, int AD_SearchColumn_ID, String trxName, String... virtualColumns) {
		super(ctx, AD_SearchColumn_ID, trxName, virtualColumns);
	}

	public MSearchColumn(Properties ctx, String AD_SearchColumn_UU, String trxName) {
		super(ctx, AD_SearchColumn_UU, trxName);
	}

	public MSearchColumn(Properties ctx, String AD_SearchColumn_UU, String trxName, String... virtualColumns) {
		super(ctx, AD_SearchColumn_UU, trxName, virtualColumns);
	}

	public MSearchColumn(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**
	 * Get Search Columns
	 * 
	 * @param AD_SearchDefinition_ID search definition
	 * @param trxName                transaction
	 * @return array of search columns
	 */
	public static MSearchColumn[] getColumns(int AD_SearchDefinition_ID, String trxName) {
		List<MSearchColumn> list = new Query(Env.getCtx(), I_HYS_SearchColumn.Table_Name, "AD_SearchDefinition_ID=?",
				trxName)
				.setParameters(AD_SearchDefinition_ID)
				.setOnlyActiveRecords(true)
				.setOrderBy(I_HYS_SearchColumn.COLUMNNAME_HYS_SearchWeight)
				.list();

		return list.toArray(new MSearchColumn[list.size()]);
	}

}

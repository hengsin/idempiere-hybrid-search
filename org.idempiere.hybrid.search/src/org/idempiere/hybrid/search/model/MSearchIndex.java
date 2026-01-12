package org.idempiere.hybrid.search.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MSearchIndex extends X_HYS_SearchIndex {

	private static final long serialVersionUID = -5524649452546133926L;

	/** Constant for the searchtype Hybrid */
	public static final String SEARCHTYPE_HYBRID = "HybridSearch";

	/** Index Status Pending */
	public static final String INDEXSTATUS_Pending = "P";
	/** Index Status Indexed */
	public static final String INDEXSTATUS_Indexed = "I";
	/** Index Status Error */
	public static final String INDEXSTATUS_Error = "E";

	public MSearchIndex(Properties ctx, int AD_SearchIndex_ID, String trxName) {
		super(ctx, AD_SearchIndex_ID, trxName);
	}

	public MSearchIndex(Properties ctx, int AD_SearchIndex_ID, String trxName, String... virtualColumns) {
		super(ctx, AD_SearchIndex_ID, trxName, virtualColumns);
	}

	public MSearchIndex(Properties ctx, String AD_SearchIndex_UU, String trxName) {
		super(ctx, AD_SearchIndex_UU, trxName);
	}

	public MSearchIndex(Properties ctx, String AD_SearchIndex_UU, String trxName, String... virtualColumns) {
		super(ctx, AD_SearchIndex_UU, trxName, virtualColumns);
	}

	public MSearchIndex(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

}

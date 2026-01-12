/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
package org.idempiere.hybrid.search.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for HYS_SearchColumn
 *  @author iDempiere (generated) 
 *  @version Release 13
 */
@SuppressWarnings("all")
public interface I_HYS_SearchColumn 
{

    /** TableName=HYS_SearchColumn */
    public static final String Table_Name = "HYS_SearchColumn";

    /** AD_Table_ID=1000000 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 4 - System 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(4);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Tenant.
	  * Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Column_ID */
    public static final String COLUMNNAME_AD_Column_ID = "AD_Column_ID";

	/** Set Column.
	  * Column in the table
	  */
	public void setAD_Column_ID (int AD_Column_ID);

	/** Get Column.
	  * Column in the table
	  */
	public int getAD_Column_ID();

	@Deprecated(since="13") // use better methods with cache
	public org.compiere.model.I_AD_Column getAD_Column() throws RuntimeException;

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within tenant
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within tenant
	  */
	public int getAD_Org_ID();

    /** Column name AD_SearchDefinition_ID */
    public static final String COLUMNNAME_AD_SearchDefinition_ID = "AD_SearchDefinition_ID";

	/** Set Search Definition	  */
	public void setAD_SearchDefinition_ID (int AD_SearchDefinition_ID);

	/** Get Search Definition	  */
	public int getAD_SearchDefinition_ID();

	@Deprecated(since="13") // use better methods with cache
	public org.compiere.model.I_AD_SearchDefinition getAD_SearchDefinition() throws RuntimeException;

    /** Column name ColumnSQL */
    public static final String COLUMNNAME_ColumnSQL = "ColumnSQL";

	/** Set Column SQL.
	  * Virtual Column (r/o)
	  */
	public void setColumnSQL (String ColumnSQL);

	/** Get Column SQL.
	  * Virtual Column (r/o)
	  */
	public String getColumnSQL();

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name HYS_SearchColumn_ID */
    public static final String COLUMNNAME_HYS_SearchColumn_ID = "HYS_SearchColumn_ID";

	/** Set Search Column.
	  * Columns for hybrid search definition
	  */
	public void setHYS_SearchColumn_ID (int HYS_SearchColumn_ID);

	/** Get Search Column.
	  * Columns for hybrid search definition
	  */
	public int getHYS_SearchColumn_ID();

    /** Column name HYS_SearchColumn_UU */
    public static final String COLUMNNAME_HYS_SearchColumn_UU = "HYS_SearchColumn_UU";

	/** Set HYS_SearchColumn_UU	  */
	public void setHYS_SearchColumn_UU (String HYS_SearchColumn_UU);

	/** Get HYS_SearchColumn_UU	  */
	public String getHYS_SearchColumn_UU();

    /** Column name HYS_SearchWeight */
    public static final String COLUMNNAME_HYS_SearchWeight = "HYS_SearchWeight";

	/** Set Search Weight.
	  * Control rank of search result
	  */
	public void setHYS_SearchWeight (String HYS_SearchWeight);

	/** Get Search Weight.
	  * Control rank of search result
	  */
	public String getHYS_SearchWeight();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();
}

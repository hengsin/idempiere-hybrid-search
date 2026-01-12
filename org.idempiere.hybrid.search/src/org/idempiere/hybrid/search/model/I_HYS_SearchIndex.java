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

/** Generated Interface for HYS_SearchIndex
 *  @author iDempiere (generated) 
 *  @version Release 13
 */
@SuppressWarnings("all")
public interface I_HYS_SearchIndex 
{

    /** TableName=HYS_SearchIndex */
    public static final String Table_Name = "HYS_SearchIndex";

    /** AD_Table_ID=1000001 */
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

    /** Column name AD_Language */
    public static final String COLUMNNAME_AD_Language = "AD_Language";

	/** Set Language.
	  * Language for this entity
	  */
	public void setAD_Language (String AD_Language);

	/** Get Language.
	  * Language for this entity
	  */
	public String getAD_Language();

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

    /** Column name AD_Table_ID */
    public static final String COLUMNNAME_AD_Table_ID = "AD_Table_ID";

	/** Set Table.
	  * Database Table information
	  */
	public void setAD_Table_ID (int AD_Table_ID);

	/** Get Table.
	  * Database Table information
	  */
	public int getAD_Table_ID();

	@Deprecated(since="13") // use better methods with cache
	public org.compiere.model.I_AD_Table getAD_Table() throws RuntimeException;

    /** Column name ContentText */
    public static final String COLUMNNAME_ContentText = "ContentText";

	/** Set Content	  */
	public void setContentText (String ContentText);

	/** Get Content	  */
	public String getContentText();

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

    /** Column name HYS_IndexStatus */
    public static final String COLUMNNAME_HYS_IndexStatus = "HYS_IndexStatus";

	/** Set Index Status	  */
	public void setHYS_IndexStatus (String HYS_IndexStatus);

	/** Get Index Status	  */
	public String getHYS_IndexStatus();

    /** Column name HYS_SearchIndex_ID */
    public static final String COLUMNNAME_HYS_SearchIndex_ID = "HYS_SearchIndex_ID";

	/** Set Search Index.
	  * Hybrid search index
	  */
	public void setHYS_SearchIndex_ID (int HYS_SearchIndex_ID);

	/** Get Search Index.
	  * Hybrid search index
	  */
	public int getHYS_SearchIndex_ID();

    /** Column name HYS_SearchIndex_UU */
    public static final String COLUMNNAME_HYS_SearchIndex_UU = "HYS_SearchIndex_UU";

	/** Set HYS_SearchIndex_UU	  */
	public void setHYS_SearchIndex_UU (String HYS_SearchIndex_UU);

	/** Get HYS_SearchIndex_UU	  */
	public String getHYS_SearchIndex_UU();

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

    /** Column name JsonData */
    public static final String COLUMNNAME_JsonData = "JsonData";

	/** Set JSON Data.
	  * The json field stores json data.
	  */
	public void setJsonData (String JsonData);

	/** Get JSON Data.
	  * The json field stores json data.
	  */
	public String getJsonData();

    /** Column name Record_ID */
    public static final String COLUMNNAME_Record_ID = "Record_ID";

	/** Set Record ID.
	  * Direct internal record ID
	  */
	public void setRecord_ID (int Record_ID);

	/** Get Record ID.
	  * Direct internal record ID
	  */
	public int getRecord_ID();

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

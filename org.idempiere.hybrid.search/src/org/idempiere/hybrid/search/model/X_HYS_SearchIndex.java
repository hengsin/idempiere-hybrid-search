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
/** Generated Model - DO NOT CHANGE */
package org.idempiere.hybrid.search.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for HYS_SearchIndex
 *  @author iDempiere (generated)
 *  @version Release 13 - $Id$ */
@org.adempiere.base.Model(table="HYS_SearchIndex")
public class X_HYS_SearchIndex extends PO implements I_HYS_SearchIndex, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20260111L;

    /** Standard Constructor */
    public X_HYS_SearchIndex (Properties ctx, int HYS_SearchIndex_ID, String trxName)
    {
      super (ctx, HYS_SearchIndex_ID, trxName);
      /** if (HYS_SearchIndex_ID == 0)
        {
			setAD_Language (null);
			setAD_SearchDefinition_ID (0);
			setAD_Table_ID (0);
			setHYS_IndexStatus (null);
			setHYS_SearchIndex_ID (0);
			setRecord_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_HYS_SearchIndex (Properties ctx, int HYS_SearchIndex_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, HYS_SearchIndex_ID, trxName, virtualColumns);
      /** if (HYS_SearchIndex_ID == 0)
        {
			setAD_Language (null);
			setAD_SearchDefinition_ID (0);
			setAD_Table_ID (0);
			setHYS_IndexStatus (null);
			setHYS_SearchIndex_ID (0);
			setRecord_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_HYS_SearchIndex (Properties ctx, String HYS_SearchIndex_UU, String trxName)
    {
      super (ctx, HYS_SearchIndex_UU, trxName);
      /** if (HYS_SearchIndex_UU == null)
        {
			setAD_Language (null);
			setAD_SearchDefinition_ID (0);
			setAD_Table_ID (0);
			setHYS_IndexStatus (null);
			setHYS_SearchIndex_ID (0);
			setRecord_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_HYS_SearchIndex (Properties ctx, String HYS_SearchIndex_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, HYS_SearchIndex_UU, trxName, virtualColumns);
      /** if (HYS_SearchIndex_UU == null)
        {
			setAD_Language (null);
			setAD_SearchDefinition_ID (0);
			setAD_Table_ID (0);
			setHYS_IndexStatus (null);
			setHYS_SearchIndex_ID (0);
			setRecord_ID (0);
        } */
    }

    /** Load Constructor */
    public X_HYS_SearchIndex (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 4 - System
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_HYS_SearchIndex[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** AD_Language AD_Reference_ID=106 */
	public static final int AD_LANGUAGE_AD_Reference_ID=106;
	/** Set Language.
		@param AD_Language Language for this entity
	*/
	public void setAD_Language (String AD_Language)
	{

		set_ValueNoCheck (COLUMNNAME_AD_Language, AD_Language);
	}

	/** Get Language.
		@return Language for this entity
	  */
	public String getAD_Language()
	{
		return (String)get_Value(COLUMNNAME_AD_Language);
	}

	@Deprecated(since="13") // use better methods with cache
	public org.compiere.model.I_AD_SearchDefinition getAD_SearchDefinition() throws RuntimeException
	{
		return (org.compiere.model.I_AD_SearchDefinition)MTable.get(getCtx(), org.compiere.model.I_AD_SearchDefinition.Table_ID)
			.getPO(getAD_SearchDefinition_ID(), get_TrxName());
	}

	/** Set Search Definition.
		@param AD_SearchDefinition_ID Search Definition
	*/
	public void setAD_SearchDefinition_ID (int AD_SearchDefinition_ID)
	{
		if (AD_SearchDefinition_ID < 1)
			set_ValueNoCheck (COLUMNNAME_AD_SearchDefinition_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_AD_SearchDefinition_ID, Integer.valueOf(AD_SearchDefinition_ID));
	}

	/** Get Search Definition.
		@return Search Definition	  */
	public int getAD_SearchDefinition_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_SearchDefinition_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	@Deprecated(since="13") // use better methods with cache
	public org.compiere.model.I_AD_Table getAD_Table() throws RuntimeException
	{
		return (org.compiere.model.I_AD_Table)MTable.get(getCtx(), org.compiere.model.I_AD_Table.Table_ID)
			.getPO(getAD_Table_ID(), get_TrxName());
	}

	/** Set Table.
		@param AD_Table_ID Database Table information
	*/
	public void setAD_Table_ID (int AD_Table_ID)
	{
		if (AD_Table_ID < 1)
			set_ValueNoCheck (COLUMNNAME_AD_Table_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_AD_Table_ID, Integer.valueOf(AD_Table_ID));
	}

	/** Get Table.
		@return Database Table information
	  */
	public int getAD_Table_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Table_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Content.
		@param ContentText Content
	*/
	public void setContentText (String ContentText)
	{
		set_Value (COLUMNNAME_ContentText, ContentText);
	}

	/** Get Content.
		@return Content	  */
	public String getContentText()
	{
		return (String)get_Value(COLUMNNAME_ContentText);
	}

	/** Error = E */
	public static final String HYS_INDEXSTATUS_Error = "E";
	/** Indexed = I */
	public static final String HYS_INDEXSTATUS_Indexed = "I";
	/** Pending = P */
	public static final String HYS_INDEXSTATUS_Pending = "P";
	/** Set Index Status.
		@param HYS_IndexStatus Index Status
	*/
	public void setHYS_IndexStatus (String HYS_IndexStatus)
	{

		set_Value (COLUMNNAME_HYS_IndexStatus, HYS_IndexStatus);
	}

	/** Get Index Status.
		@return Index Status	  */
	public String getHYS_IndexStatus()
	{
		return (String)get_Value(COLUMNNAME_HYS_IndexStatus);
	}

	/** Set Search Index.
		@param HYS_SearchIndex_ID Hybrid search index
	*/
	public void setHYS_SearchIndex_ID (int HYS_SearchIndex_ID)
	{
		if (HYS_SearchIndex_ID < 1)
			set_ValueNoCheck (COLUMNNAME_HYS_SearchIndex_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_HYS_SearchIndex_ID, Integer.valueOf(HYS_SearchIndex_ID));
	}

	/** Get Search Index.
		@return Hybrid search index
	  */
	public int getHYS_SearchIndex_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_HYS_SearchIndex_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set HYS_SearchIndex_UU.
		@param HYS_SearchIndex_UU HYS_SearchIndex_UU
	*/
	public void setHYS_SearchIndex_UU (String HYS_SearchIndex_UU)
	{
		set_Value (COLUMNNAME_HYS_SearchIndex_UU, HYS_SearchIndex_UU);
	}

	/** Get HYS_SearchIndex_UU.
		@return HYS_SearchIndex_UU	  */
	public String getHYS_SearchIndex_UU()
	{
		return (String)get_Value(COLUMNNAME_HYS_SearchIndex_UU);
	}

	/** Set JSON Data.
		@param JsonData The json field stores json data.
	*/
	public void setJsonData (String JsonData)
	{
		set_Value (COLUMNNAME_JsonData, JsonData);
	}

	/** Get JSON Data.
		@return The json field stores json data.
	  */
	public String getJsonData()
	{
		return (String)get_Value(COLUMNNAME_JsonData);
	}

	/** Set Record ID.
		@param Record_ID Direct internal record ID
	*/
	public void setRecord_ID (int Record_ID)
	{
		if (Record_ID < 0)
			set_ValueNoCheck (COLUMNNAME_Record_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_Record_ID, Integer.valueOf(Record_ID));
	}

	/** Get Record ID.
		@return Direct internal record ID
	  */
	public int getRecord_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Record_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}
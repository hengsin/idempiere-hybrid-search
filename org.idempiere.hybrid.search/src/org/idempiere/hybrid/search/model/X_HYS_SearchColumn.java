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

/** Generated Model for HYS_SearchColumn
 *  @author iDempiere (generated)
 *  @version Release 13 - $Id$ */
@org.adempiere.base.Model(table="HYS_SearchColumn")
public class X_HYS_SearchColumn extends PO implements I_HYS_SearchColumn, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20260111L;

    /** Standard Constructor */
    public X_HYS_SearchColumn (Properties ctx, int HYS_SearchColumn_ID, String trxName)
    {
      super (ctx, HYS_SearchColumn_ID, trxName);
      /** if (HYS_SearchColumn_ID == 0)
        {
			setAD_Column_ID (0);
			setAD_SearchDefinition_ID (0);
			setHYS_SearchColumn_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_HYS_SearchColumn (Properties ctx, int HYS_SearchColumn_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, HYS_SearchColumn_ID, trxName, virtualColumns);
      /** if (HYS_SearchColumn_ID == 0)
        {
			setAD_Column_ID (0);
			setAD_SearchDefinition_ID (0);
			setHYS_SearchColumn_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_HYS_SearchColumn (Properties ctx, String HYS_SearchColumn_UU, String trxName)
    {
      super (ctx, HYS_SearchColumn_UU, trxName);
      /** if (HYS_SearchColumn_UU == null)
        {
			setAD_Column_ID (0);
			setAD_SearchDefinition_ID (0);
			setHYS_SearchColumn_ID (0);
        } */
    }

    /** Standard Constructor */
    public X_HYS_SearchColumn (Properties ctx, String HYS_SearchColumn_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, HYS_SearchColumn_UU, trxName, virtualColumns);
      /** if (HYS_SearchColumn_UU == null)
        {
			setAD_Column_ID (0);
			setAD_SearchDefinition_ID (0);
			setHYS_SearchColumn_ID (0);
        } */
    }

    /** Load Constructor */
    public X_HYS_SearchColumn (Properties ctx, ResultSet rs, String trxName)
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
      StringBuilder sb = new StringBuilder ("X_HYS_SearchColumn[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	@Deprecated(since="13") // use better methods with cache
	public org.compiere.model.I_AD_Column getAD_Column() throws RuntimeException
	{
		return (org.compiere.model.I_AD_Column)MTable.get(getCtx(), org.compiere.model.I_AD_Column.Table_ID)
			.getPO(getAD_Column_ID(), get_TrxName());
	}

	/** Set Column.
		@param AD_Column_ID Column in the table
	*/
	public void setAD_Column_ID (int AD_Column_ID)
	{
		if (AD_Column_ID < 1)
			set_Value (COLUMNNAME_AD_Column_ID, null);
		else
			set_Value (COLUMNNAME_AD_Column_ID, Integer.valueOf(AD_Column_ID));
	}

	/** Get Column.
		@return Column in the table
	  */
	public int getAD_Column_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Column_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Column SQL.
		@param ColumnSQL Virtual Column (r/o)
	*/
	public void setColumnSQL (String ColumnSQL)
	{
		set_Value (COLUMNNAME_ColumnSQL, ColumnSQL);
	}

	/** Get Column SQL.
		@return Virtual Column (r/o)
	  */
	public String getColumnSQL()
	{
		return (String)get_Value(COLUMNNAME_ColumnSQL);
	}

	/** Set Search Column.
		@param HYS_SearchColumn_ID Columns for hybrid search definition
	*/
	public void setHYS_SearchColumn_ID (int HYS_SearchColumn_ID)
	{
		if (HYS_SearchColumn_ID < 1)
			set_ValueNoCheck (COLUMNNAME_HYS_SearchColumn_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_HYS_SearchColumn_ID, Integer.valueOf(HYS_SearchColumn_ID));
	}

	/** Get Search Column.
		@return Columns for hybrid search definition
	  */
	public int getHYS_SearchColumn_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_HYS_SearchColumn_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set HYS_SearchColumn_UU.
		@param HYS_SearchColumn_UU HYS_SearchColumn_UU
	*/
	public void setHYS_SearchColumn_UU (String HYS_SearchColumn_UU)
	{
		set_Value (COLUMNNAME_HYS_SearchColumn_UU, HYS_SearchColumn_UU);
	}

	/** Get HYS_SearchColumn_UU.
		@return HYS_SearchColumn_UU	  */
	public String getHYS_SearchColumn_UU()
	{
		return (String)get_Value(COLUMNNAME_HYS_SearchColumn_UU);
	}

	/** A = A */
	public static final String HYS_SEARCHWEIGHT_A = "A";
	/** B = B */
	public static final String HYS_SEARCHWEIGHT_B = "B";
	/** C = C */
	public static final String HYS_SEARCHWEIGHT_C = "C";
	/** Set Search Weight.
		@param HYS_SearchWeight Control rank of search result
	*/
	public void setHYS_SearchWeight (String HYS_SearchWeight)
	{

		set_Value (COLUMNNAME_HYS_SearchWeight, HYS_SearchWeight);
	}

	/** Get Search Weight.
		@return Control rank of search result
	  */
	public String getHYS_SearchWeight()
	{
		return (String)get_Value(COLUMNNAME_HYS_SearchWeight);
	}
}
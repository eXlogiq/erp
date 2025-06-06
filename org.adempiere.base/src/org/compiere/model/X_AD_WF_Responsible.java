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
package org.compiere.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.util.KeyNamePair;

/** Generated Model for AD_WF_Responsible
 *  @author iDempiere (generated)
 *  @version Release 12 - $Id$ */
@org.adempiere.base.Model(table="AD_WF_Responsible")
public class X_AD_WF_Responsible extends PO implements I_AD_WF_Responsible, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20241121L;

    /** Standard Constructor */
    public X_AD_WF_Responsible (Properties ctx, int AD_WF_Responsible_ID, String trxName)
    {
      super (ctx, AD_WF_Responsible_ID, trxName);
      /** if (AD_WF_Responsible_ID == 0)
        {
			setAD_Role_ID (0);
			setAD_WF_Responsible_ID (0);
			setEntityType (null);
// @SQL=SELECT CASE WHEN '@P|AdempiereSys:N@'='Y' THEN 'D' ELSE get_sysconfig('DEFAULT_ENTITYTYPE','U',0,0) END FROM Dual
			setName (null);
			setResponsibleType (null);
        } */
    }

    /** Standard Constructor */
    public X_AD_WF_Responsible (Properties ctx, int AD_WF_Responsible_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, AD_WF_Responsible_ID, trxName, virtualColumns);
      /** if (AD_WF_Responsible_ID == 0)
        {
			setAD_Role_ID (0);
			setAD_WF_Responsible_ID (0);
			setEntityType (null);
// @SQL=SELECT CASE WHEN '@P|AdempiereSys:N@'='Y' THEN 'D' ELSE get_sysconfig('DEFAULT_ENTITYTYPE','U',0,0) END FROM Dual
			setName (null);
			setResponsibleType (null);
        } */
    }

    /** Standard Constructor */
    public X_AD_WF_Responsible (Properties ctx, String AD_WF_Responsible_UU, String trxName)
    {
      super (ctx, AD_WF_Responsible_UU, trxName);
      /** if (AD_WF_Responsible_UU == null)
        {
			setAD_Role_ID (0);
			setAD_WF_Responsible_ID (0);
			setEntityType (null);
// @SQL=SELECT CASE WHEN '@P|AdempiereSys:N@'='Y' THEN 'D' ELSE get_sysconfig('DEFAULT_ENTITYTYPE','U',0,0) END FROM Dual
			setName (null);
			setResponsibleType (null);
        } */
    }

    /** Standard Constructor */
    public X_AD_WF_Responsible (Properties ctx, String AD_WF_Responsible_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, AD_WF_Responsible_UU, trxName, virtualColumns);
      /** if (AD_WF_Responsible_UU == null)
        {
			setAD_Role_ID (0);
			setAD_WF_Responsible_ID (0);
			setEntityType (null);
// @SQL=SELECT CASE WHEN '@P|AdempiereSys:N@'='Y' THEN 'D' ELSE get_sysconfig('DEFAULT_ENTITYTYPE','U',0,0) END FROM Dual
			setName (null);
			setResponsibleType (null);
        } */
    }

    /** Load Constructor */
    public X_AD_WF_Responsible (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 6 - System - Client
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
      StringBuilder sb = new StringBuilder ("X_AD_WF_Responsible[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_Role getAD_Role() throws RuntimeException
	{
		return (org.compiere.model.I_AD_Role)MTable.get(getCtx(), org.compiere.model.I_AD_Role.Table_ID)
			.getPO(getAD_Role_ID(), get_TrxName());
	}

	/** Set Role.
		@param AD_Role_ID Responsibility Role
	*/
	public void setAD_Role_ID (int AD_Role_ID)
	{
		if (AD_Role_ID < 0)
			set_Value (COLUMNNAME_AD_Role_ID, null);
		else
			set_Value (COLUMNNAME_AD_Role_ID, Integer.valueOf(AD_Role_ID));
	}

	/** Get Role.
		@return Responsibility Role
	  */
	public int getAD_Role_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Role_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_User getAD_User() throws RuntimeException
	{
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_ID)
			.getPO(getAD_User_ID(), get_TrxName());
	}

	/** Set User/Contact.
		@param AD_User_ID User within the system - Internal or Business Partner Contact
	*/
	public void setAD_User_ID (int AD_User_ID)
	{
		if (AD_User_ID < 1)
			set_Value (COLUMNNAME_AD_User_ID, null);
		else
			set_Value (COLUMNNAME_AD_User_ID, Integer.valueOf(AD_User_ID));
	}

	/** Get User/Contact.
		@return User within the system - Internal or Business Partner Contact
	  */
	public int getAD_User_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_User_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Workflow Responsible.
		@param AD_WF_Responsible_ID Responsible for Workflow Execution
	*/
	public void setAD_WF_Responsible_ID (int AD_WF_Responsible_ID)
	{
		if (AD_WF_Responsible_ID < 1)
			set_ValueNoCheck (COLUMNNAME_AD_WF_Responsible_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_AD_WF_Responsible_ID, Integer.valueOf(AD_WF_Responsible_ID));
	}

	/** Get Workflow Responsible.
		@return Responsible for Workflow Execution
	  */
	public int getAD_WF_Responsible_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_WF_Responsible_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set AD_WF_Responsible_UU.
		@param AD_WF_Responsible_UU AD_WF_Responsible_UU
	*/
	public void setAD_WF_Responsible_UU (String AD_WF_Responsible_UU)
	{
		set_Value (COLUMNNAME_AD_WF_Responsible_UU, AD_WF_Responsible_UU);
	}

	/** Get AD_WF_Responsible_UU.
		@return AD_WF_Responsible_UU	  */
	public String getAD_WF_Responsible_UU()
	{
		return (String)get_Value(COLUMNNAME_AD_WF_Responsible_UU);
	}

	/** Set Description.
		@param Description Optional short description of the record
	*/
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription()
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** EntityType AD_Reference_ID=389 */
	public static final int ENTITYTYPE_AD_Reference_ID=389;
	/** Set Entity Type.
		@param EntityType Dictionary Entity Type; Determines ownership and synchronization
	*/
	public void setEntityType (String EntityType)
	{

		set_Value (COLUMNNAME_EntityType, EntityType);
	}

	/** Get Entity Type.
		@return Dictionary Entity Type; Determines ownership and synchronization
	  */
	public String getEntityType()
	{
		return (String)get_Value(COLUMNNAME_EntityType);
	}

	/** Set Name.
		@param Name Alphanumeric identifier of the entity
	*/
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName()
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair()
    {
        return new KeyNamePair(get_ID(), getName());
    }

	public org.compiere.model.I_AD_WF_Responsible getOverride() throws RuntimeException
	{
		return (org.compiere.model.I_AD_WF_Responsible)MTable.get(getCtx(), org.compiere.model.I_AD_WF_Responsible.Table_ID)
			.getPO(getOverride_ID(), get_TrxName());
	}

	/** Set Overridden WF Responsible.
		@param Override_ID Overridden WF Responsible
	*/
	public void setOverride_ID (int Override_ID)
	{
		if (Override_ID < 1)
			set_Value (COLUMNNAME_Override_ID, null);
		else
			set_Value (COLUMNNAME_Override_ID, Integer.valueOf(Override_ID));
	}

	/** Get Overridden WF Responsible.
		@return Overridden WF Responsible	  */
	public int getOverride_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Override_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** ResponsibleType AD_Reference_ID=304 */
	public static final int RESPONSIBLETYPE_AD_Reference_ID=304;
	/** Human = H */
	public static final String RESPONSIBLETYPE_Human = "H";
	/** Manual = M */
	public static final String RESPONSIBLETYPE_Manual = "M";
	/** Organization = O */
	public static final String RESPONSIBLETYPE_Organization = "O";
	/** Role = R */
	public static final String RESPONSIBLETYPE_Role = "R";
	/** System Resource = S */
	public static final String RESPONSIBLETYPE_SystemResource = "S";
	/** Set Responsible Type.
		@param ResponsibleType Type of the Responsibility for a workflow
	*/
	public void setResponsibleType (String ResponsibleType)
	{

		set_Value (COLUMNNAME_ResponsibleType, ResponsibleType);
	}

	/** Get Responsible Type.
		@return Type of the Responsibility for a workflow
	  */
	public String getResponsibleType()
	{
		return (String)get_Value(COLUMNNAME_ResponsibleType);
	}
}
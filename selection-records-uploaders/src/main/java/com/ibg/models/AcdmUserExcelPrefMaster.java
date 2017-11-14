package com.ibg.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdGenerator;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table(value = "ACDM_EXCEL_MASTER_PREF")
@IdName("PREF_ID")
public class AcdmUserExcelPrefMaster extends Model {

	static {
		validatePresenceOf("PREF_ID", "PREF_NAME", "PREF_VALUE", "PREF_TYPE", "PREF_DISPLAY_ORDER");
	}

}

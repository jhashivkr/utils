package com.ibg.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdGenerator;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table(value = "ACDM_EXCEL_USER_PREF")
@IdName("USER_PREF_ID")
@IdGenerator("ACDM_EXCEL_USER_PREF_SEQ.nextval")
public class AcdmUserExcelPref extends Model {

	static {
		validatePresenceOf("USER_PREF_ID", "PREF_ID", "USER_OWNR_ID", "SELECTED_VAL");
	}

}

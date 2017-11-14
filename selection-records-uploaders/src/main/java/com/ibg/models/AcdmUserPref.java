package com.ibg.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table(value = "ACDM_USER_PREF")
public class AcdmUserPref extends Model {

	static {
		validatePresenceOf("USER_ID", "PREF_ID", "PREF_VALUE");
	}

}

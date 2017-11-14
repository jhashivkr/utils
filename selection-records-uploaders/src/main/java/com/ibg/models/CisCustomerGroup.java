package com.ibg.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table(value = "CIS_CUST_GROUP")
@IdName("CUST_GROUP")
public class CisCustomerGroup extends Model {

	static {
		validatePresenceOf("ALLOWSTOCKRUSH", "CCGROUPSOPTIN", "COUNTRY", "CUST_GROUP", "DISPLAYEBOOKESTNET", "DISPLAYESTNET", "DISPLAYREVIEWS",
				"IFOUND", "LIBRARY_GROUP", "MAXIMUMORDERQUANTITY", "MULTILINE", "ILS_SYSTEM", "OPENURL", "REPORT_NAME", "RESTRICTINITIALS",
				"RETAINSELECTOR", "SHOWFUND", "SHOWLOCATION", "SHOWPROFILE", "SHIBBLOLETHENTITYID", "SHIBBOLETHIDP", "SHIBBOLETHURL", "CIS_BP_ID");

	}

}

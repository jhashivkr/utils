package com.ibg.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table(value = "CIS_CUSTOMER")
@IdName("CUSTOMER_NO")
public class CisCustomer extends Model {

	static {
		validatePresenceOf("CURRENCY", "CUST_GROUP", "CUSTOMER_NO", "DIVISION", "REPORT_NAME", "YEAR_END");

	}

}

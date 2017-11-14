package com.ibg.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table(value = "CIS_USER")
@IdName("USER_ID")
@BelongsTo(parent = CisCustomer.class, foreignKeyName = "CUSTOMER_NO")
public class CisUser extends Model {

	static {
		validatePresenceOf("USER_ID", "ADMINISTRATOR", "CHECKOUT_TYPE", "CHECKOUT_VALUE", "CONT_CANCEL_ORDER", "CONT_CLAIM_ORDER",
				"CONT_PLACE_ORDER", "CUSTOMER_NO", "DEFAULT_BUDGET", "DEFAULT_LOAN_TYPE", "DEFAULT_LOCATION_CODE", "DEFAULT_PURCHASE", "DEPARTMENT",
				"DISPLAY_REVIEWS", "FIRM_CANCEL_ORDER", "FIRM_CLAIM_ORDER", "FIRM_PLACE_ORDER", "INITIALS", "INSTITUTION", "ISPUBLIC", "LITE",
				"OIDCUSTOMER", "POSITION", "PROVISIONAL_ACCESS", "RATIFIER", "RATIFIER_PRIORITY", "RECIPIENT_LISTING", "REQ_RECIPIENT",
				"REQUEST_METHOD", "SHIBBOLETHID", "SOLUTION_ACCOUNTS", "WEBSERVICE_CLIENT");

	}	

}

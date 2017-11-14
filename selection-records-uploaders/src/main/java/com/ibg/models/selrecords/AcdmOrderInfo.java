package com.ibg.models.selrecords;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.Table;

@Table(value = "ACDM_LIST_ORDER_INFO")
@BelongsTo(parent = AcdmList.class, foreignKeyName = "LIST_ID")
public class AcdmOrderInfo extends Model {
	
	static {
		validatePresenceOf("OPR_ID", "LIST_ID", "FIELD_KEY", "FIELD_VALUE", "LINE_NO");
	}

}

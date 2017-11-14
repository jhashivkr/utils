package com.ibg.models.selrecords;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.Table;

@Table(value = "ACDM_ORDER_INFO_TEMPLATE_DATA")
@BelongsTo(parent = AcdmOrderInfoTemplate.class, foreignKeyName = "TEMPLATE_ID")
public class AcdmOrderInfoTemplateData extends Model {
	static {
		validatePresenceOf("TEMPLATE_ID", "FIELD_KEY", "FIELD_VALUE", "LINE_NO");
	}

}

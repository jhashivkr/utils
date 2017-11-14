package com.ibg.models.selrecords;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdGenerator;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table(value = "ACDM_ORDER_INFO_TEMPLATE")
@IdName("TEMPLATE_ID")
@IdGenerator("ACDM_ORDER_INFO_TEMPLATE_SEQ.nextval")
public class AcdmOrderInfoTemplate extends Model {

	static {
		validatePresenceOf("TEMPLATE_NAME", "USER_OWNER_ID", "TEMPLATE_VIEW_MODE", "IS_DEFAULT_TEMPLATE");
	}

}

package com.ibg.models.selrecords;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdGenerator;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

import com.ibg.data.upload.exceptions.JsonLoadExceptions;
import com.ibg.data.upload.exceptions.UploadExceptionService;

@Table(value = "ACDM_LIST")
@IdName("LIST_ID")
@IdGenerator("ACDM_LIST_SEQ.nextval")
public class AcdmList extends Model {

	static {
		validatePresenceOf("LIST_TP_ID", "USER_OWNR_ID", "ACTIVE", "OLD_UD_ID", "LIB_GRP");
	}

	@Override
	public String toString() {
		
		StringBuilder bldr = new StringBuilder();
		
		bldr.append((null != get("old_ud_id") ? get("old_ud_id").toString() : get("list_tp_id").toString())).append('\t');
		
		bldr.append((null != get("description") ? get("description").toString() : "")).append('\t');
		bldr.append((null != get("days_history_stored") ? get("days_history_stored").toString() : "")).append('\t');
		bldr.append((null != get("notification_email") ? get("notification_email").toString() : "") ).append('\t');
		bldr.append((0 == getInteger("notification_email_type") ? "PLAIN" : "HTML") ).append('\t');
		bldr.append((0 == getInteger("notification_email_detailed") ? "FALSE" : "TRUE")).append('\t');
		bldr.append((null != get("notification_schedule") ? get("notification_schedule").toString() : "")).append('\t');
		bldr.append((null != get("user_ownr_id") ? get("user_ownr_id").toString() : "")).append('\t');
		bldr.append((0 == getInteger("forward_list") ? "FALSE" : "TRUE")).append('\n');
		
		return bldr.toString();
	}

}

package com.ibg.models.selrecords;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.BelongsTo;
import org.javalite.activejdbc.annotations.IdGenerator;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table(value = "ACDM_LIST_ITEM")
@IdName("OPR_ID")
@IdGenerator("ACDM_LIST_ITEM_SEQ.nextval")
@BelongsTo(parent = AcdmList.class, foreignKeyName = "LIST_ID")
public class AcdmListItem extends Model {

	static {
		validatePresenceOf("LIST_ID", "EAN");
	}

	
}

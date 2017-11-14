package com.ibg.models.selrecords;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdGenerator;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

import com.ibg.data.upload.exceptions.JsonLoadExceptions;
import com.ibg.data.upload.exceptions.UploadExceptionService;

@Table(value = "APLC_USER")
@IdName("USER_ID")
public class AplcUser extends Model {

	static {
		validatePresenceOf("USER_ID");
	}

}

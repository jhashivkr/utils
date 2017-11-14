package com.ibg.data.upload.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibg.db.ServiceLocator;

public interface ExceptionObjects {

	public String getMsg();
	public ObjectMapper jsonObjectMapper = (ObjectMapper) ServiceLocator.getBean("jsonObjMapper");
}

package com.ibg.data.upload.exceptions;

public class JsonExceptionObj implements ExceptionObjects{

	private String json;

	public JsonExceptionObj(String json) {
		super();
		this.json = json;
	}

	public String getMsg() {
		return json;
	}

	@Override
	public String toString() {
		return "ExceptionObj [mongo_data=" + json + "]";
	}

}

package com.ibg.data.upload.exceptions;

public class JsonLoadExceptions implements ExceptionObjects{

	private String mongo_id;
	private String mongo_data;
	private String msg;

	public JsonLoadExceptions(String mongo_id, String mongo_data, String msg) {
		super();
		this.mongo_id = mongo_id;
		this.mongo_data = mongo_data;
		this.msg = msg;
	}

	public String getId() {
		return mongo_id;
	}

	public String getData() {
		return mongo_data;
	}

	public String getMsg() {
		return msg;
	}

	@Override
	public String toString() {
		return "ExceptionObj [mongo_id=" + mongo_id + ", mongo_data=" + mongo_data + ", msg=" + msg + "]";
	}

}

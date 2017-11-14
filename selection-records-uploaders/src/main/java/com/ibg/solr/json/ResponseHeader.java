package com.ibg.solr.json;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHeader {
	private String status;
	private String QTime;
	private Map<String, String> params;

	public ResponseHeader() {
		setParams(new LinkedHashMap<String, String>());
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getQTime() {
		return QTime;
	}

	public void setQTime(String qTime) {
		QTime = qTime;
	}

	@Override
	public String toString() {
		return "ResponseHeader [status=" + status + ", QTime=" + QTime + ", Params=" + params + "]";
	}

}

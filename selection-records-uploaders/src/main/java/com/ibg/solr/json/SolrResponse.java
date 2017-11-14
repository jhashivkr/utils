package com.ibg.solr.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class SolrResponse {

	private boolean respError = false;
	private String headerinfo;
	private String status;
	
	private List<Map<String, String>> datalist;

	public SolrResponse() {
		datalist = new ArrayList<Map<String, String>>();
		headerinfo = "";
		status = "";
	}

	public String getHeaderinfo() {
		return headerinfo;
	}

	public void setHeaderinfo(String headerinfo) {
		this.headerinfo = headerinfo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isRespError() {
		return respError;
	}

	protected void setRespError(boolean respError) {
		this.respError = respError;
	}

	public List<Map<String, String>> getDatalist() {
		return datalist;
	}

	public void setDatalist(List<Map<String, String>> datalist) {
		this.datalist = datalist;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((datalist == null) ? 0 : datalist.hashCode());
		result = prime * result + ((headerinfo == null) ? 0 : headerinfo.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SolrResponse other = (SolrResponse) obj;
		if (datalist == null) {
			if (other.datalist != null)
				return false;
		} else if (!datalist.equals(other.datalist))
			return false;
		if (headerinfo == null) {
			if (other.headerinfo != null)
				return false;
		} else if (!headerinfo.equals(other.headerinfo))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}
	@Override
	public String toString(){
		JSONObject json = new JSONObject();
		JSONArray jsonArr = new JSONArray(datalist);
		try {
			json.put("headerinfo", this.headerinfo);
			json.put("status", this.status);
			json.put("jsonArr", jsonArr);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json.toString();
	}

}

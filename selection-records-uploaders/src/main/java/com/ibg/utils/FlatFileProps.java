package com.ibg.utils;

import java.util.Map;

public class FlatFileProps {

	private Map<String, String> fldsList;

	public Map<String, String> getFldsList() {
		return fldsList;
	}

	public void setFldsList(Map<String, String> fldsList) {
		this.fldsList = fldsList;
	}
	
	public String getFields(String key){
		return fldsList.get(key);
	}

}

package com.ibg.parsers.json;

import java.util.LinkedHashMap;
import java.util.Map;

public class SelectionLines {

	private Map<String, String> Params;

	public SelectionLines() {
		setParams(new LinkedHashMap<String, String>());
	}

	public Map<String, String> getParams() {
		return Params;
	}

	public void setParams(Map<String, String> params) {
		Params = params;
	}

	@Override
	public String toString() {
		return "SelectionLines [Params=" + Params + "]";
	}

}

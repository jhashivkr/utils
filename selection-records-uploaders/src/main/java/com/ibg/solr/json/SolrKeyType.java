package com.ibg.solr.json;

import com.fasterxml.jackson.databind.node.JsonNodeType;

public final class SolrKeyType {

	private String key;
	private JsonNodeType dataType;

	public SolrKeyType(String key, JsonNodeType jsonNodeType) {
		super();
		this.key = key;
		this.dataType = jsonNodeType;
	}

	public String getKey() {
		return key;
	}

	public JsonNodeType getDataType() {
		return dataType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
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
		SolrKeyType other = (SolrKeyType) obj;
		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SolrKeyType [key=" + key + ", dataType=" + dataType + "]";
	}

}

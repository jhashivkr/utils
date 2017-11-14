package com.ibg.solr.json;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SolrRecord<T> {

	//@JsonIgnore
	private ResponseHeader responseHeader;
	private Response<T> response;

	public ResponseHeader getResponseHeader() {
		return responseHeader;
	}

	public void setResponseHeader(ResponseHeader responseHeader) {
		this.responseHeader = responseHeader;
	}

	public Response<T> getResponse() {
		return response;
	}

	public void setResponse(Response<T> response) {
		this.response = response;
	}

	@Override
	public String toString() {
		// return "SolrRecord [responseHeader=" + responseHeader + ", response="
		// + response + "]";
		return "SolrRecord [response=" + response + "]";
	}

}

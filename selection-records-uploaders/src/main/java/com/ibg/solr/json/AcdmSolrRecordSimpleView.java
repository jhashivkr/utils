package com.ibg.solr.json;

public class AcdmSolrRecordSimpleView {

	private ResponseHeader responseHeader;
	private AcdmResponseSimpleView response;

	public ResponseHeader getResponseHeader() {
		return responseHeader;
	}

	public void setResponseHeader(ResponseHeader responseHeader) {
		this.responseHeader = responseHeader;
	}

	public AcdmResponseSimpleView getResponse() {
		return response;
	}

	public void setResponse(AcdmResponseSimpleView response) {
		this.response = response;
	}

	@Override
	public String toString() {
		return "SolrRecord [response=" + response + "]";
	}

}

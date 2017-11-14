package com.ibg.solr.json;

public class AcdmSolrRecordExpandedView {

	private ResponseHeader responseHeader;
	private AcdmResponseExpandedView response;

	public ResponseHeader getResponseHeader() {
		return responseHeader;
	}

	public void setResponseHeader(ResponseHeader responseHeader) {
		this.responseHeader = responseHeader;
	}

	public  AcdmResponseExpandedView getResponse() {
		return response;
	}

	public void setResponse( AcdmResponseExpandedView response) {
		this.response = response;
	}

	@Override
	public String toString() {
		return "SolrRecord [response=" + response + "]";
	}

}

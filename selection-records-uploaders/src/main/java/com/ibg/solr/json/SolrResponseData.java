package com.ibg.solr.json;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SolrResponseData {
	private List<Map<String, List<String>>> docs;
	private Set<SolrKeyType> solrRespKeySet;

	public SolrResponseData(List<Map<String, List<String>>> docs, Set<SolrKeyType> solrRespKeySet) {
		super();
		this.docs = docs;
		this.solrRespKeySet = solrRespKeySet;
	}

	public List<Map<String, List<String>>> getDocs() {
		return docs;
	}

	public Set<SolrKeyType> getSolrRespKeySet() {
		return solrRespKeySet;
	}

}

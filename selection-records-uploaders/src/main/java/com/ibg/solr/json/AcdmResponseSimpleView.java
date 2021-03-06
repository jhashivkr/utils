package com.ibg.solr.json;

import java.util.List;
import java.util.Map;

public class AcdmResponseSimpleView {

	private String numFound;
	private String start;
	private List<AcdmSimpleViewDoc> docs;

	public String getNumFound() {
		return numFound;
	}

	public void setNumFound(String numFound) {
		this.numFound = numFound;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public List<AcdmSimpleViewDoc> getDocs() {
		return (List<AcdmSimpleViewDoc>) docs;
	}

	public void setDocs(List<AcdmSimpleViewDoc> docs) {
		this.docs = docs;
	}

	@Override
	public String toString() {
		return "Response [numFound=" + numFound + ", start=" + start + ", docs=" + docs.toString() + "]";
	}

}

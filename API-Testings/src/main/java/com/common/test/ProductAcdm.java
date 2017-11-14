package com.common.test;

import ibg.product.information.Product;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;


public final class ProductAcdm<T extends Product> implements Serializable {

	private static final long serialVersionUID = -2396932634239483640L;

	private final SolrDocumentList doclist;

	private List<T> solrSrchProdList;

	public ProductAcdm(SolrDocumentList doclist) {

		this.doclist = doclist;
		solrSrchProdList = new LinkedList<T>();

	}

	private void mapSolrRespToProductFlds() {
		for (SolrDocument solrDoc : doclist) {

			Product product = new Product();
			product.setEan(solrDoc.get("EAN").toString());
			solrSrchProdList.add((T) product);
		}
	}

}

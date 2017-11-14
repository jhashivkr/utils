package com.common.test;

import ibg.academics.service.AcademicServiceLocator;
import ibg.academics.service.AcademicServiceLocator.BeanName;
import ibg.academics.service.AcademicServiceLocator.ServiceName;
import ibg.academics.service.SolrUtilityService;
import ibg.administration.CISUserLoginData;
import ibg.ganimede.service.GanimedeResponse;
import ibg.ganimede.service.GanimedeWorker;
import ibg.ganimede.service.GanimedeWorkerImpl;
import ibg.lib.activity.browserowp.db.response.ObjectMapperWraper;
import ibg.product.ebook.TitleUserEbookInfoCmprtr;
import ibg.product.information.EBPDataObject;
import ibg.product.information.Product;
import ibg.product.search.UserEbookDispOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import solr.api.SolrServerFactory;
import solr.api.acdm.SolrJServerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;

public class TestProductMultiCurrency {

	private static List<Product> productsList;
	private static CISUserLoginData cisUserLoginData = new CISUserLoginData();
	private static CISUserLoginData cisUserLoginDataCopy;

	public TestProductMultiCurrency() {
		fetchLoginCallData();
		QueryResponse response = getProductList();
		printSolrDetails(response);
	}

	private static void printSolrDetails(QueryResponse response) {

		String solr_cur_name = "";
		// USD,CAD,GBP,EUR
		System.out.println("Login user data ");

		System.out.println(cisUserLoginData.getCurrencyPreferences());
		System.out.println("------------------------------------------------------------------------");

		SolrDocumentList solrList = response.getResults();
		Iterator<SolrDocument> docItr = solrList.iterator();

		for (; docItr.hasNext();) {
			SolrDocument doc = docItr.next();

			System.out.println("available solr field Names: " + doc.getFieldNames());
			System.out.print("EAN: " + doc.get("EAN") + ", ");
			// get diff currency prices
			for (String cur : cisUserLoginData.getCurrencyPreferences()) {
				solr_cur_name = "PRICE_" + cur.substring(0, cur.length() - 1) + "_MS";
				System.out.print(solr_cur_name + ":" + doc.get(solr_cur_name) + ", ");
			}

			System.out.println();
		}

	}

	private static QueryResponse getProductList() {

		SolrQuery solrQry = new SolrQuery();
		solrQry.set("q", "under the blue sky");
		solrQry.set("qt", "Title_SI");
		solrQry.set("fq", "ITEM_OHND:Y");
		solrQry.setFields("EAN", "PRICE_*");
		solrQry.setRows(10);

		QueryResponse response = null;
		SolrServer server = null;
		try {

			server = ((SolrJServerFactory) AcademicServiceLocator.getBean(BeanName.SOLR_RESP_WORKER)).getDefaultInstance();

			solrQry.setTimeAllowed(SolrServerFactory.getMaximumQueryMs());
			response = server.query(solrQry, METHOD.POST);

			return response;

		} catch (Exception e) {
			System.out.println("error: " + e);
			return null;
		} finally {
			server.shutdown();
		}

	}

	private static void fetchLoginCallData() {
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sessionID=CouttsGanimedeRequest&sandbox=kboudre" + "&program=ipage/login.p&user_id=crossin";

		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);

		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();

		for (Map<String, String> object : ganimadeResponseList) {
			cisUserLoginData.setApprovalCenter(object.get("approvalCenter"));
			cisUserLoginData.setAlibrisSupplier(object.get("alibrisSupplier"));
			cisUserLoginData.setAlibrisPartnerID(object.get("alibrisPartnerID"));
			cisUserLoginData.setMarcOutputMode(object.get("marcOutputMode"));
			cisUserLoginData.setBdsAgreement(object.get("bdsAgreement"));

			cisUserLoginData.setConsortiaLibraries(object.get("consortiaLibraries"));

			cisUserLoginData.setCatPartner(object.get("catPartner"));
			cisUserLoginData.setCatPartnerDescription(object.get("catPartnerDescription"));
			cisUserLoginData.setLicence(object.get("licence"));
			cisUserLoginData.setLicenceDescription(object.get("licenceDescription"));

			cisUserLoginData.setEbscoCategory(object.get("ebscoCategory"));
			cisUserLoginData.setEbsoLevel(object.get("ebsoLevel"));
			cisUserLoginData.setMilDownloadAgreement(object.get("milDownloadAgreement"));
			cisUserLoginData.setCurrencyPreferences(object.get("currencyPref"));
			cisUserLoginData.setApprovalPlans(object.get("approvalPlans"));
		}

		cisUserLoginDataCopy = CISUserLoginData.newInstance(cisUserLoginData);
		Map<String, Boolean> ebookOption = new UserEbookDispOptions().getUserEbookDispOptions("ACDM", "CROSSIN");

		// remove those ebpdataobject from cisuserlogindata which is not in user selection
		for (String key : ebookOption.keySet()) {
			if (!ebookOption.get(key)) {
				cisUserLoginDataCopy.getEbpDataObjectMap().remove(key);
			}
		}
		//System.out.println("ebookOption: " + ebookOption);
		//System.out.println("cisUserLoginData: " + cisUserLoginData.getEbpDataObjectMap());
		//System.out.println("cisUserLoginDataCopy: " + cisUserLoginDataCopy.getEbpDataObjectMap());

	}

}

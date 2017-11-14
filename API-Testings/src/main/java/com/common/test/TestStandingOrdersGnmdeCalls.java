package com.common.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import ibg.administration.CISUserLoginData;
import ibg.ganimede.service.GanimedeResponse;
import ibg.ganimede.service.GanimedeWorker;
import ibg.ganimede.service.GanimedeWorkerImpl;
import ibg.lib.activity.HttpCallUrl;
import ibg.product.information.EBPDataObject;
import ibg.product.information.ProductCisDetails;
import ibg.product.search.UserEbookDispOptions;

public class TestStandingOrdersGnmdeCalls {

	public static void soSearchTest() {
		
		HttpCallUrl gnmdUrl = new HttpCallUrl("search.orders");
		
		gnmdUrl.setString("sandbox", "kboudre");
		gnmdUrl.setString("cust-group","PRINCETO");
		gnmdUrl.setString("customer-no", "70200A00");
		gnmdUrl.setString("keywords", "books");
		gnmdUrl.setString("cust-po-no", "1ACP8499");
		
		//String url = gnmdUrl.getGnmdUrl(true);
		String url = gnmdUrl.getGanimedeUrl(true);
		
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		printGanimedeResponseData(ganimedeResponse);
	}

	public static void loginCallEBPDataTest() {
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sessionID=CouttsGanimedeRequest&sandbox=kboudre"
				+ "&program=ipage/login.p&user_id=crossin";

		Map<String, Boolean> userEbookDispOptions = new UserEbookDispOptions().getUserEbookDispOptions("ACDM", "CROSSIN");
		System.out.println("user ebook settings - " + userEbookDispOptions);

		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);

		System.out.println("keys: " + ganimedeResponse.getDatalist().get(0).keySet());

		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();
		Collection<Object> catPartner = null;
		Collection<Object> catPartnerDescription = null;
		Collection<Object> licence = null;
		Collection<Object> licenceDescription = null;

		String[] data = StringUtils.split(ganimadeResponseList.get(0).get("catPartner"), ",");
		catPartner = Arrays.asList(Arrays.copyOf(data, data.length, Object[].class));

		data = StringUtils.split(ganimadeResponseList.get(0).get("catPartnerDescription"), ",");
		catPartnerDescription = Arrays.asList(Arrays.copyOf(data, data.length, Object[].class));

		data = StringUtils.split(ganimadeResponseList.get(0).get("licence"), ",");
		licence = Arrays.asList(Arrays.copyOf(data, data.length, Object[].class));

		data = StringUtils.split(ganimadeResponseList.get(0).get("licenceDescription"), ",");
		licenceDescription = Arrays.asList(Arrays.copyOf(data, data.length, Object[].class));

		// System.out.println(catPartner);
		// System.out.println(catPartnerDescription);

		Set<EBPDataObject> cisEBPData = new ProductCisDetails.EBPBuilder(catPartner).cisEbpCatpartnerDesc(catPartnerDescription).cisEbpLicence(licence).cisEbpLicenceDesc(licenceDescription)
				.getCisEBPData();

		// System.out.println("cisEBPData: " + cisEBPData);

		System.out.println("creating CISUserLoginData");
		CISUserLoginData cisUserLoginData = new CISUserLoginData();
		cisUserLoginData.setCatPartner(ganimadeResponseList.get(0).get("catPartner"));
		cisUserLoginData.setCatPartnerDescription(ganimadeResponseList.get(0).get("catPartnerDescription"));
		cisUserLoginData.setLicence(ganimadeResponseList.get(0).get("licence"));
		cisUserLoginData.setLicenceDescription(ganimadeResponseList.get(0).get("licenceDescription"));

		// for(EBPDataObject obj: cisUserLoginData.getEbpDataObjectList()){
		// System.out.println("EBPDataObject: " + obj);
		// }

		System.out.println("before removing");
		for (String key : cisUserLoginData.getEbpDataObjectMap().keySet()) {
			System.out.println(key + " - " + cisUserLoginData.getEbpDataObjectMap().get(key));
		}

		System.out.println("removing those ebpdataobject from cisuserlogindata which is not selected by user");

		for (String key : userEbookDispOptions.keySet()) {
			if (!userEbookDispOptions.get(key)) {
				cisUserLoginData.getEbpDataObjectMap().remove(key);
			}
		}

		System.out.println("after removing");
		for (String key : cisUserLoginData.getEbpDataObjectMap().keySet()) {
			System.out.println(key + " - " + cisUserLoginData.getEbpDataObjectMap().get(key));
		}

		// printGanimedeResponseData(ganimedeResponse);
	}

	public static void loginCallTest() {
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sessionID=CouttsGanimedeRequest&sandbox=kboudre"
				+ "&program=ipage/login.p&user_id=theusp";
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);

		printGanimedeResponseData(ganimedeResponse);
	}

	public static void loginCallTestValues() {
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest"
				+ "&program=ipage/ip-fields&cust-group=11100&user_id=crossin&mode=VALUES";
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		System.out.println("ip-fields - mode=VALUES");

		Set<String> srParams = new HashSet<String>();
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();

		for (Map<String, String> object : ganimadeResponseList) {
			srParams.add(object.get("srParameter"));
		}
		System.out.println("srParameters :" + srParams.toString());

		printGanimedeResponseData(ganimedeResponse);
	}

	public static void loginCallTestSummary() {
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest"
				+ "&program=ipage/ip-fields&cust-group=11100&user_id=crossin&mode=SUMMARY";
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		System.out.println("ip-fields - mode=SUMMARY");

		Set<String> srParams = new HashSet<String>();
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();

		for (Map<String, String> object : ganimadeResponseList) {
			srParams.add(object.get("srParameter"));
		}
		System.out.println("srParameters :" + srParams.toString());

		printGanimedeResponseData(ganimedeResponse);
	}

	public static void loginCallTestDependencies() {
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest"
				+ "&program=ipage/ip-fields&cust-group=11100&user_id=crossin&mode=DEPENDENCIES";
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		System.out.println("ip-fields - mode=DEPENDENCIES");

		Set<String> srParams = new HashSet<String>();
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();

		for (Map<String, String> object : ganimadeResponseList) {
			srParams.add(object.get("srParameter"));
		}
		System.out.println("srParameters :" + srParams.toString());

		printGanimedeResponseData(ganimedeResponse);
	}

	private static void printGanimedeResponseData(GanimedeResponse ganimedeResponse) {
		System.out.println("Ganimade Header :" + ganimedeResponse.getHeaderinfo());
		System.out.println("-----------");
		System.out.println("Key : Value");
		System.out.println("-----------");
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();

		for (Map<String, String> object : ganimadeResponseList) {
			for (String key : object.keySet()) {
				System.out.println(key + ":" + object.get(key));
			}
			System.out.println();
		}
		System.out.println("Ganimade Footer :" + ganimedeResponse.getStatus());
	}
	
	private static void testFiscalPeriodDetailGnmdeCall() {
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		HttpCallUrl gnmdUrl = new HttpCallUrl("fiscal.period.search");

		gnmdUrl.setString("sandbox", "kboudre");
		gnmdUrl.setString("cust-group", "PRINCETO");
		gnmdUrl.setString("customer-no", "70200A00");
		gnmdUrl.setString("fiscalperiod", "201410");
		String url = gnmdUrl.getGanimedeUrl(true);
		url = url.replaceAll(" ", "%20");

		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		printGanimedeResponseData(ganimedeResponse);
	}

	private static void testFiscalPeriodGnmdeCall() {
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		HttpCallUrl gnmdUrl = new HttpCallUrl("fiscal.period");
		gnmdUrl.setString("sandbox", "kboudre");
		gnmdUrl.setString("cust-group", "PRINCETO");
		gnmdUrl.setString("customer-no", "70200A00");

		String url = gnmdUrl.getGanimedeUrl(true);
		url = url.replaceAll(" ", "%20");

		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		printGanimedeResponseData(ganimedeResponse);
	}

	private static void testMonthlyListGnmdeCall() {
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		HttpCallUrl gnmdUrl = new HttpCallUrl("monthly.list");
		gnmdUrl.setString("sandbox", "kboudre");
		gnmdUrl.setString("cust-group", "PRINCETO");
		gnmdUrl.setString("customer-no", "70200A00");
		gnmdUrl.setString("fiscalyear", "next12");

		String url = gnmdUrl.getGanimedeUrl(true);
		url = url.replaceAll(" ", "%20");

		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		printGanimedeResponseData(ganimedeResponse);
	}

}

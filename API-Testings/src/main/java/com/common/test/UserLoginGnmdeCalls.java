package com.common.test;

import ibg.academics.service.AcademicServiceLocator;
import ibg.academics.service.AcademicServiceLocator.BeanName;
import ibg.administration.CISUserLoginData;
import ibg.ganimede.service.GanimedeResponse;
import ibg.ganimede.service.GanimedeWorker;
import ibg.ganimede.service.GanimedeWorkerImpl;
import ibg.lib.activity.browserowp.db.response.ObjectMapperWraper;
import ibg.product.information.EBPDataObject;
import ibg.product.information.ProductCisDetails;
import ibg.product.search.UserEbookDispOptions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.api.test.utils.VariableData;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class UserLoginGnmdeCalls {

	static String ganimedeBaseURL = VariableData.getProperty("ganimedeBaseURL");
	public static List<Map<String, String>> loginCallExchangeRateTest(String custNo) {

		// http://gnm.ingramcontent.com/gnm_cgi/gnmipage/runner?sessionID=CouttsGanimedeRequest&program=
		String url = ganimedeBaseURL + "ipage/getexchrates.p&customer-no=" + custNo; // 11100A00
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();

		return ganimadeResponseList;
		
		//System.out.println("ipage/getexchrates.p");
		//printGanimedeResponseData(ganimedeResponse);
	}

	public static CISUserLoginData loginCallEBPDataTest(String userId) {
		String url = ganimedeBaseURL + "ipage/login.p&user_id=" + userId;

		Map<String, Boolean> userEbookDispOptions = new UserEbookDispOptions().getUserEbookDispOptions("ACDM", userId);
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

		Set<EBPDataObject> cisEBPData = new ProductCisDetails.EBPBuilder(catPartner).cisEbpCatpartnerDesc(catPartnerDescription)
		        .cisEbpLicence(licence).cisEbpLicenceDesc(licenceDescription).getCisEBPData();

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

		return cisUserLoginData;
	}

	public static List<Map<String, String>> loginCallTest(String userId, String custNo) {
		String url = ganimedeBaseURL + "ipage/login.p&user_id=" + userId;
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);

		CISUserLoginData cisUserLoginData = new CISUserLoginData();
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();
		
		//printJsonResponse(ganimadeResponseList);

		// userLoginData.setCurrencyPreferences(ganimadeResponseList.get(0).get("currencyPref"));
		// System.out.println("currencyPref: " +
		// userLoginData.getCurrencyPreferences());

		/*
		try {

			if (ganimedeResponse.isRespError()) {
				return null; // Need to Logout Academic User for any Ganimede
				        // error (callGanimede() error OR CIS Down)
			}

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
				cisUserLoginData.setLegacyPricing(object.get("legacyPricing"));
			}

			loadCurrencyExchangeFromCIS(cisUserLoginData, custNo);
		} catch (Exception e) {
			System.out.println("Error in getUserLoginInfoFromCIS() method for userID=" + userId);
			e.printStackTrace();
		}
		*/
		
		return ganimadeResponseList;

		//System.out.println("cisUserLoginData: " + cisUserLoginData);
		// printGanimedeResponseData(ganimedeResponse);
	}

	public static List<Map<String, String>> loginCallTestValues(String userId) {
		String url = ganimedeBaseURL + "ipage/ip-fields&cust-group=11100&user_id=" + userId + "&mode=VALUES";
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		//System.out.println("ip-fields - mode=VALUES");

		Set<String> srParams = new HashSet<String>();
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();
		
		return ganimadeResponseList;

		//for (Map<String, String> object : ganimadeResponseList) {
		//	srParams.add(object.get("srParameter"));
		//}
		//System.out.println("srParameters :" + srParams.toString());

		//printGanimedeResponseData(ganimedeResponse);
	}

	public static List<Map<String, String>> loginCallTestSummary(String userId) {
		String url = ganimedeBaseURL + "ipage/ip-fields&cust-group=11100&user_id=" + userId + "&mode=SUMMARY";
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		//System.out.println("ip-fields - mode=SUMMARY");

		Set<String> srParams = new HashSet<String>();
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();
		
		return ganimadeResponseList;

		//for (Map<String, String> object : ganimadeResponseList) {
		//	srParams.add(object.get("srParameter"));
		//}
		//System.out.println("srParameters :" + srParams.toString());

		//printGanimedeResponseData(ganimedeResponse);
	}

	public static List<Map<String, String>> loginCallTestDependencies(String userId) {
		String url = ganimedeBaseURL + "ipage/ip-fields&cust-group=11100&user_id=" + userId + "&mode=DEPENDENCIES";
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		//System.out.println("ip-fields - mode=DEPENDENCIES");

		Map<String, Map<String, LinkedHashSet<String>>> depMap = new LinkedHashMap<String, Map<String, LinkedHashSet<String>>>();

		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();
		return ganimadeResponseList;

		/*
		for (Map<String, String> object : ganimadeResponseList) {
			Map<String, LinkedHashSet<String>> paramMap = null;
			for (String key : object.keySet()) {

				if ("platform".equalsIgnoreCase(object.get("srParam1"))) {
					if (depMap.containsKey(object.get("allowDeny"))) {
						paramMap = depMap.get(object.get("allowDeny"));
					} else {
						paramMap = new HashMap<String, LinkedHashSet<String>>();
						LinkedHashSet<String> valList = new LinkedHashSet<String>();
						paramMap.put(object.get("srData1"), valList);
						depMap.put(object.get("allowDeny"), paramMap);
					}

					if (paramMap.containsKey(object.get("srData1"))) {
						paramMap.get(object.get("srData1")).add(object.get("srData2"));
					} else {
						LinkedHashSet<String> valList = new LinkedHashSet<String>();
						valList.add(object.get("srData2"));
						paramMap.put(object.get("srData1"), valList);
					}
				}

			}// for
		}// for
		 */
		//System.out.println("depMap: " + depMap);

		//printGanimedeResponseData(ganimedeResponse);
	}

	private static void loadCurrencyExchangeFromCIS(CISUserLoginData cisUserLoginData, String customer_no) {

		String ganimedeUrl = ganimedeBaseURL + "ipage/getexchrates.p&customer-no=" + customer_no;
		ganimedeUrl = ganimedeUrl.replaceAll(" ", "%20");

		Map<String, Double> currencyExchangeMap = new HashMap<String, Double>();
		try {
			GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
			GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(ganimedeUrl);
			List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();
			
			
			for (Map<String, String> object : ganimadeResponseList) {
				try {
					currencyExchangeMap.put(object.get("buying-currency"), Double.parseDouble(object.get("exch-rate")));
				} catch (Exception e) {
					currencyExchangeMap.put(object.get("buying-currency"), 0d);
				}
			}
			

		} catch (Exception e) {
			System.out.println("Error in loadCurrencyExchangeFromCIS() method for userID=" + customer_no);
			e.printStackTrace();
			
		}
		cisUserLoginData.setCurrencyExchangeMap(currencyExchangeMap);
		
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

	private static void printJsonResponse(List<Map<String, String>> ganimedeResponse) {

		try {

			if (null != ganimedeResponse && !ganimedeResponse.isEmpty()) {

				ObjectMapperWraper mapper = (ObjectMapperWraper) AcademicServiceLocator.getBean(BeanName.JSON_MAPPER);
				String jsonResp = mapper.write(ganimedeResponse);

				System.out.println("jsonResp: " + jsonResp);

			}

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

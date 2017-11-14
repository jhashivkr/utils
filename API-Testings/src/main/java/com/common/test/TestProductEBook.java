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
import ibg.product.search.EbookInfoCmprtrHelper;
import ibg.product.search.UserEbookDispOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;

public class TestProductEBook {

	private static List<String> eans = new LinkedList<String>(Arrays.asList(new String[] { "9780553574470", "9780687048915", "9780080471716", "9781282064065", "9781283000000", "9781283001328",
			"9781283001489", "9780313038136", "9789042920958", "9789991115610", "9781402035678", "9781741143331", "9781568028507", "9780674022379", "9780872864795", "9781592370795", "9781400052455",
			"9780802094575", "9781565933200", "9789264009424", "9780802094551", "9780787690533","9781280596162" }));
	private static List<Product> productsList;
	private static List<String> fieldsList = Arrays.asList(new String[] { "EAN", "Title", "ALT_EAN", "ISSN_ID", "COUTTS_DIVISION_CODE", "COUTTS_DIVISION_AVAILABILITY_STATUS",
			"COUTTS_DIVISION_APPROVAL_DATE", "COUTTS_DIVISION_PUB_DATE", "CIS_BOOK_TYPE", "CIS_AUTHAFFILDESC", "CIS_COUTTSNUMBER", "CIS_DATEADDED", "CIS_EBP_CATPARTNER", "CIS_EBP_CURRENCY",
			"CIS_EBP_DOWNLOADABLE", "CIS_EBP_DOWNLOADDETAILS", "CIS_EBP_EBSCOCATEGORY", "CIS_EBP_EBSCOLEVEL", "CIS_EBP_LICENSE", "CIS_EBP_MILACCESS", "CIS_EBP_PRICE", "CIS_EBP_TITLEID",
			"CIS_FORMAT_CODE", "CIS_FORMAT_DESC", "CIS_APPROVAL_FORMAT", "CIS_GEOGRAPHIC_AREA_NAME", "CIS_GEOGRAPHIC_AREA_CODE", "CIS_ILLUSTRATIONS", "CIS_IAPPROVE", "CIS_NOTES",
			"CIS_COUNTRIES_INCLUDED", "CIS_COUNTRIES_EXCLUDED", "CIS_NATCURRICULUM", "CIS_OUT_OF_PRINT", "CIS_DIGITAL_REPRINT" });

	private static CISUserLoginData cisUserLoginData = new CISUserLoginData();
	private static CISUserLoginData cisUserLoginDataCopy;

	public TestProductEBook() {
		fetchLoginCallData();
		getProductList();
		printProducts();
	}

	private static void printProducts() {

		TitleUserEbookInfoCmprtr<CISUserLoginData, List<EBPDataObject>> ebpComp = new TitleUserEbookInfoCmprtr<CISUserLoginData, List<EBPDataObject>>();
		ObjectMapperWraper mapper = (ObjectMapperWraper) AcademicServiceLocator.getBean(BeanName.JSON_MAPPER);
				
		ebpComp.setUserLoginData(cisUserLoginDataCopy);
		

		String data = "";
		System.out.println("Login user data ");

		//for (EBPDataObject obj : cisUserLoginData.getEbpDataObjectList()) {
		//	System.out.println("EBP Data: " + obj.toString());
		//}
		System.out.println(cisUserLoginData.getEbscoCategory() + ", " + cisUserLoginData.getEbsoLevel() + ", " + cisUserLoginData.getMilDownloadAgreement());
		System.out.println("------------------------------------------------------------------------");

		for (Product solrProduct : productsList) {

			if (null != solrProduct.getCisEBPData()) {
				
				ebpComp.setCisEBPMap(solrProduct.getCisEBPDataMap());
				
				System.out.println("------------------------------------------------------------------------");
				ebpComp.setCisEBPMap(solrProduct.getCisEBPDataMap());
				// ebpComp.checkAndAssignProdSetDetails(solrProduct.getEan());

				// ebpComp.analyzeEbpData(cisUserLoginDataCopy, solrProduct.getCisEBPData());
				if (!ebpComp.checkAndAssignProdSetDetails(solrProduct.getEan())) {
					ebpComp.analyzeEbpData();
				}

				System.out.println("Product EAN: " + solrProduct.getEan());
				
				/*if(ebpComp.isEblEligible()){
					try {
						String jdata =  mapper.write(ebpComp.getAvailableEblList());
						JsonNode node = mapper.readTree(jdata);
						
						//JsonNode node = mapper.readTree(data);
						JavaType ebpData = mapper.getTypeFactory().constructCollectionType(ArrayList.class, EBPDataObject.class);
						List<EBPDataObject> dataList = mapper.readValue(node.toString(), ebpData);
						for(EBPDataObject obj: dataList){
							System.out.println(obj.getCisEbpCatpartner() + " - " +  obj.toString());
						}
						
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(ebpComp.isEbraryEligible()){
					data = "[" + StringUtils.join(ebpComp.getAvailableEbraryList(), ',') + "]";
					//System.out.println("ERARY: " + data);
				}
				if(ebpComp.isEbscoEligible()){
					data = "[" + StringUtils.join(ebpComp.getAvailableEbscoList(), ',') + "]";
					//System.out.println("EBSCO: " + data);
				}
				if(ebpComp.isMilEligible()){
					data = "[" + StringUtils.join(ebpComp.getAvailableMilList(), ',') + "]";
					//System.out.println("MIL: " + data);
				}
				*/
				
				System.out.println("ebookDownloadable: " + ebpComp.getBestMatchEbook().getCisEbpDisplayText());
				
				System.out.println("------------------------------------------------------------------------");

			}

		}
	}

	private static void getProductList() {
		SolrUtilityService solrService = (SolrUtilityService) AcademicServiceLocator.getService(ServiceName.SOLR_SERVICE);
		solrService.setSearchEans(StringUtils.join(eans, ','), eans.size());
		solrService.setSearchFields(fieldsList);
		productsList = solrService.fetchProductDetatilsFromSolr();
	}

	private static void fetchLoginCallData() {
		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sessionID=CouttsGanimedeRequest&sandbox=kboudre" + "&program=ipage/login.p&user_id=crossin";

		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);

		if (ganimedeResponse.isRespError()) {
			System.out.println("Ganimede call failure");
			System.out.println("Header Info: " + ganimedeResponse.getHeaderinfo());
			System.out.println("status: " + ganimedeResponse.getStatus());
			System.out.println("Error: " + ganimedeResponse.isRespError());
			System.exit(1);
		}

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

		
		//remove those ebpdataobject from cisuserlogindata which is not in user selection
		for (String key : ebookOption.keySet()) {
			if (!ebookOption.get(key)) {
				cisUserLoginDataCopy.getEbpDataObjectMap().remove(key);
			}
		}
		System.out.println("ebookOption: " + ebookOption);
		System.out.println("cisUserLoginData: " + cisUserLoginData.getEbpDataObjectMap());
		System.out.println("cisUserLoginDataCopy: " + cisUserLoginDataCopy.getEbpDataObjectMap());

	}

}

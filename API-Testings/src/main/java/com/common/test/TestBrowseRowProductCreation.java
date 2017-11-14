package com.common.test;

import ibg.academics.service.AcademicServiceLocator;
import ibg.academics.service.SolrUtilityService;
import ibg.academics.service.AcademicServiceLocator.ServiceName;
import ibg.browserow.dto.AcademicItemData;
import ibg.product.information.Product;
import ibg.product.search.SearchResultDataBean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class TestBrowseRowProductCreation {

	private static Map<String, List<Long>> academicListItem = new HashMap<String, List<Long>>();
	private static Map<Long, AcademicItemData> academicItemDataMap = new HashMap<Long, AcademicItemData>();
	private static List<String> eans = new LinkedList<String>(Arrays.asList(new String[] { "9781241791797", "9781452299600" }));
	private static List<SearchResultDataBean<Product>> searchResult = new LinkedList<SearchResultDataBean<Product>>();
	private static List<Product> productsList;

	public TestBrowseRowProductCreation() {
		createAcademicListItem();
		createAcademicItemDataMap();
		getProductList();
		matchAndCreateFullList();
		printProducts();
	}

	private static void printProducts() {

		for (SearchResultDataBean<Product> prodBean : searchResult) {

			Product solrProduct = prodBean.getSolrProduct();
			System.out.println("------------------------------------------------------------------------");

			System.out.println("Product Title: " + solrProduct.getTitle());
			AcademicItemData academicItemData = prodBean.getAcademicData();
			
			System.out.println("op_id: " + academicItemData.getOprId());
			System.out.println("title_id: " + academicItemData.getTitleId());
			System.out.println("location: " + academicItemData.getLocationCode());
			System.out.println("fund: " + academicItemData.getFund());
			System.out.println("quantity: " + academicItemData.getQuantity());
			System.out.println("profile: " + academicItemData.getProfileCode());
			System.out.println("customer_demand: " + academicItemData.getCustomerDemand());
			System.out.println("patron_selection: " + academicItemData.getPatronSelectionDate());
			System.out.println("alibris_copy: " + academicItemData.getAlibrisCopyPrice());

			System.out.println("CIS_FORMAT_CODE: " + (null != solrProduct.getCisFormatCode() ? solrProduct.getCisFormatCode() : "EMPTY"));

			System.out.println("------------------------------------------------------------------------");

		}
	}

	private static void matchAndCreateFullList() {

		for (Product product : productsList) {

			SearchResultDataBean<Product> productDetail = new SearchResultDataBean<Product>(product);

			if (null != academicListItem && academicListItem.containsKey(product.getEan())) {

				if (academicListItem.get(product.getEan()).size() > 1) {
					// create copies
					for (Long oprId : academicListItem.get(product.getEan())) {
						Product productCopy = product; 
						SearchResultDataBean<Product> productDetailCopy = new SearchResultDataBean<Product>(productCopy);
						productDetailCopy.setOprId(oprId);
						if (academicItemDataMap.containsKey(oprId)) {
							AcademicItemData academicItemData = academicItemDataMap.get(oprId);
							productDetailCopy.setAcademicData(academicItemData);
						}

						searchResult.add(productDetailCopy);
					}
				}else {
					Long oprId = academicListItem.get(product.getEan()).get(0);
					productDetail.setOprId(oprId);
					if (academicItemDataMap.containsKey(oprId)) {
						AcademicItemData academicItemData = academicItemDataMap.get(oprId);
						productDetail.setAcademicData(academicItemData);
					}
					
					searchResult.add(productDetail);
				}
			}
		}
	}

	private static void getProductList() {

		SolrUtilityService solrService = (SolrUtilityService) AcademicServiceLocator.getService(ServiceName.SOLR_SERVICE);
		solrService.setSearchEans(StringUtils.join(eans, ','), eans.size());

		solrService.setSearchFields(null);
		productsList = solrService.fetchProductDetatilsFromSolr();
	}

	private static void createAcademicListItem() {

		academicListItem.put("9781241791797", Arrays.asList(new Long[] { 5205403l, 5205404l }));
		academicListItem.put("9781452299600", Arrays.asList(new Long[] { 5205322l, 5205323l }));
	}

	private static void createAcademicItemDataMap() {
		AcademicItemData acdmItmData = new AcademicItemData();
		acdmItmData.setOprId(5205403l);
		acdmItmData.setTitleId("9780415892506");
		acdmItmData.setLocationCode("loc-1");
		acdmItmData.setFund("fund-1");
		acdmItmData.setQuantity(10);
		acdmItmData.setProfileCode("abcd");
		acdmItmData.setCustomerDemand(10);
		acdmItmData.setPatronSelectionDate(null);
		acdmItmData.setAlibrisCopyPrice(0.0f);
		academicItemDataMap.put(5205403l, acdmItmData);

		AcademicItemData acdmItmData1 = new AcademicItemData();
		acdmItmData1.setOprId(5205404l);
		acdmItmData1.setTitleId("9780687048915");
		acdmItmData1.setLocationCode("loc-2");
		acdmItmData1.setFund("fund-2");
		acdmItmData1.setQuantity(20);
		acdmItmData1.setProfileCode("efgh");
		acdmItmData1.setCustomerDemand(20);
		acdmItmData1.setPatronSelectionDate(null);
		acdmItmData1.setAlibrisCopyPrice(0.0f);
		academicItemDataMap.put(5205404l, acdmItmData1);

		AcademicItemData acdmItmData2 = new AcademicItemData();
		acdmItmData2.setOprId(5205322l);
		acdmItmData2.setTitleId("9780080471716");
		acdmItmData2.setLocationCode("loc-3");
		acdmItmData2.setFund("fund-3");
		acdmItmData2.setQuantity(30);
		acdmItmData2.setProfileCode("ijkl");
		acdmItmData2.setCustomerDemand(30);
		acdmItmData2.setPatronSelectionDate(null);
		acdmItmData2.setAlibrisCopyPrice(0.0f);
		academicItemDataMap.put(5205322l, acdmItmData2);

		AcademicItemData acdmItmData3 = new AcademicItemData();
		acdmItmData3.setOprId(5205323l);
		acdmItmData3.setTitleId("9781282064065");
		acdmItmData3.setLocationCode("loc-4");
		acdmItmData3.setFund("fund-4");
		acdmItmData3.setQuantity(40);
		acdmItmData3.setProfileCode("mnop");
		acdmItmData3.setCustomerDemand(40);
		acdmItmData3.setPatronSelectionDate(null);
		acdmItmData3.setAlibrisCopyPrice(0.0f);
		academicItemDataMap.put(5205323l, acdmItmData3);

	}

}

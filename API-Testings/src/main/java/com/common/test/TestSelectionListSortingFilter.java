package com.common.test;

import ibg.academics.administration.dto.AcdmUserPref;
import ibg.academics.administration.dto.AcdmUserPrefId;
import ibg.academics.administration.service.UserConfigService;
import ibg.academics.dto.AcademicAlibrisItem;
import ibg.academics.dto.AcademicList;
import ibg.academics.dto.AcademicListItem;
import ibg.academics.dto.AcademicListType;
import ibg.academics.enums.SortByFields;
import ibg.academics.service.AcademicFilterListService;
import ibg.academics.service.AcademicListService;
import ibg.academics.service.AcademicServiceLocator;
import ibg.academics.service.AcademicServiceLocator.BeanName;
import ibg.academics.service.AcademicServiceLocator.ServiceName;
import ibg.academics.service.SolrUtilityService;
import ibg.administration.CISUserLoginData;
import ibg.browserow.dto.AcademicItemData;
import ibg.browserow.service.BrowseRowService;
import ibg.common.AcdmSortType;
import ibg.common.utility.StringUtility;
import ibg.ganimede.service.GanimedeResponse;
import ibg.ganimede.service.GanimedeWorker;
import ibg.ganimede.service.GanimedeWorkerImpl;
import ibg.product.information.EBPDataObject;
import ibg.product.information.Product;
import ibg.product.search.AcdmFilterHelper;
import ibg.product.search.Br1101Helper;
import ibg.product.search.Br8000ProdSolrDO;
import ibg.product.search.ListAcdmDataMap;
import ibg.product.search.ListDataFilter;
import ibg.product.search.ListSortFilterHelper;
import ibg.product.search.SearchResultDataBean;
import ibg.product.search.SearchUtility;
import ibg.product.search.acdm.SearchConstants;
import ibg.product.search.acdm.SolrSearchFieldsService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import com.endeca.navigation.Navigation;
import com.endeca.navigation.UrlGen;

public class TestSelectionListSortingFilter {

	public static void fetchLoginCallExchangeRateTest(String custNo, CISUserLoginData cisUserLoginData) {

		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmipage/runner?sessionID=CouttsGanimedeRequest&sandbox=kboudre" + "&program=ipage/getexchrates.p&customer-no=" + custNo; // 11100A00
		url = url.replaceAll(" ", "%20");
		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(url);
		List<Map<String, String>> ganimadeResponseList = ganimedeResponse.getDatalist();

		Map<String, Double> curExchMap = new HashMap<String, Double>();

		for (Map<String, String> object : ganimadeResponseList) {
			try {
				curExchMap.put(object.get("buying-currency"), Double.parseDouble(object.get("exch-rate")));
			} catch (Exception e) {
				curExchMap.put(object.get("buying-currency"), 0d);
			}
		}

		cisUserLoginData.setCurrencyExchangeMap(curExchMap);
	}

	private static CISUserLoginData fetchLoginCallData(String userId) {

		CISUserLoginData cisUserLoginData = new CISUserLoginData();

		String url = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sessionID=CouttsGanimedeRequest&sandbox=kboudre&program=ipage/login.p&user_id=" + userId;

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

		return cisUserLoginData;

	}

	private static Navigation getProductList(Set<String> eanSet) {

		// ;
		// get the possible search results from solr
		SolrUtilityService solrService = (SolrUtilityService) AcademicServiceLocator.getService(ServiceName.SOLR_SERVICE);
		solrService.setSearchEans(StringUtils.join(eanSet, ','), eanSet.size());
		solrService.setSearchFields(SolrSearchFieldsService.getAcdmList());

		// List<Product> productList = solrService.fetchProductDetatilsFromSolr();

		return solrService.getNavigation();

		//

	}

	private static Navigation getProductList(String searchTerm) {

		// get the possible search results from solr
		SolrUtilityService solrService = (SolrUtilityService) AcademicServiceLocator.getService(ServiceName.SOLR_SERVICE);
		solrService.setSearchTerm(searchTerm);
		solrService.setSearchFields(SolrSearchFieldsService.getAcdmList());
		solrService.setSearchSize(3000);
		return solrService.getNavigation();

	}

	public static void testAvailableFormats(String userId, String listType, String libGrp) {

		CISUserLoginData cisUserLoginData = fetchLoginCallData(userId);

		AcademicListService service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);
		AcademicList academicList = null;
		ListSortFilterHelper sortFilterHelper = new ListSortFilterHelper();

		academicList = service.findAcademicListByCustGroup(userId, listType, libGrp);

		List<AcademicListItem> academicItemList = service.getAllBylistId(academicList.getListId());

		Map<String, List<Long>> eanOprMap = null;
		Map<Long, AcademicItemData> academicItemDataMap = null;

		Set<String> eanSet = null;

		if ("SR".equalsIgnoreCase(listType)) {

			// eanOprMap = Br1101Helper.prepareSRListData(10, academicList, "", "Title", "1",
			// "9780900384868 OR 9781501004261", cisUserLoginData);
			// 9781322081854,9781783509836
			// non ebook - 9781783509843

			String qry = "9780839826057 OR 9781575677644 OR 9780900384868 OR 9780802470348 OR 9780613235693 OR 9781424043811 OR 9781424008452 OR 9781424010875 OR 9781424021796 OR 9781424022854 OR 9783791318110 OR 9781466914230 OR 9781466914247 OR 9781418900939 OR 9781502717382 OR 9781501004261 OR 9780977096473 OR 9780977096480 OR 9780174027669 OR 9780757812231 OR 9780757830273 OR 9781418967475 OR 9780802448224&qt=ISBN_SI";

			// eanOprMap = Br1101Helper.prepareSRListData(cisUserLoginData, 10, academicList, "", "Title", "1",
			// "9781783509843 OR 9781322081854 OR 9781783509836&qt=ISBN_SI","Book");
			eanOprMap = Br1101Helper.prepareSRListData(cisUserLoginData, 10, academicList, "", "Title", "1", qry, "Book");

			academicItemDataMap = ListAcdmDataMap.getAcademicItemDataMap(academicList);
			eanSet = new HashSet<String>();
			// eanSet.add("9781461402787");// 9780300206104, 9781306179492
			// eanSet.add("9781461402787 OR 9780300206104 OR 9781306179492");// 9780300206104, 9781306179492
			// eanSet.add("9780674728134");
			// eanSet.add("9780674049147");
			// eanSet.add("9780900384868");
			for (String isbn : eanOprMap.keySet()) {
				eanSet.add(isbn);
			}
			System.out.println("eanSet: " + eanSet);
		} else {
			eanOprMap = sortFilterHelper.getEanOprIdMap(academicItemList);
			academicItemDataMap = ListAcdmDataMap.getSortedAcademicOrderInfoItemData(25, 0, academicList, "oointnote", "");
			// get all eans involved
			eanSet = new HashSet<String>();
			if (null != academicItemDataMap && !academicItemDataMap.isEmpty()) {
				for (AcademicItemData item : academicItemDataMap.values()) {
					eanSet.add(item.getTitleId());
				}
			} else {
				return;
			}
		}

		ListSortFilterHelper sortHlpr = ((ListSortFilterHelper) AcademicServiceLocator.getBean(BeanName.SORT_HLPR));

		QueryResponse response = sortHlpr.getIsbnDetailsFromSolr(eanSet.size(), new LinkedList<Object>(eanSet));

		// map out the solr document list in the response by the passed ISBN key
		Map<String, SolrDocument> solrDataMap = new LinkedHashMap<String, SolrDocument>();
		for (SolrDocument rec : response.getResults()) {
			solrDataMap.put((String) rec.get("EAN"), rec);
		}

		// System.out.println(response.getQueryResponse());
		for (FacetField facet : response.getFacetFields()) {
			System.out.println("facet: " + facet.getName() + " = " + facet.getValueCount() + ", " + facet.getValues());
			for (Count count : facet.getValues()) {
				System.out.println("count: " + count.getName() + "=" + count.getCount());
			}
		}

		// Navigation nav = getProductList(eanSet);
		// Br8000ProductDOTest br8000ProductListTest = new Br8000ProductDOTest(eanOprMap, cisUserLoginData, nav, userId,
		// academicItemDataMap, academicList);
		// testAlternateProducts(cisUserLoginData, br8000ProductListTest);

		Br8000ProdSolrTest br8000ProdSolrTest = new Br8000ProdSolrTest(eanOprMap, cisUserLoginData, solrDataMap, userId, academicItemDataMap, academicList);
		testAlternateProductsNoEndeca(cisUserLoginData, br8000ProdSolrTest);

	}

	public static void testListPrepFlow(List<Object> titleIds, String... params) {

		String userId = null;
		String listTypeId = null;
		String libGrp = null;
		String listName = null;
		String sortKey = null;
		String dobj = null;
		String filterString = null;
		String sortSrcType = null;
		String ntt = null;
		String startUrl = null;
		String searchTerm = null;
		String customerNo = null;
		String prmrySolrFld = null;

		if (null != params) {

			try {
				if (null != params[0]) {
					userId = params[0];
				}
			} catch (Exception e) {
			}

			try {
				if (null != params[1]) {
					listTypeId = params[1];
					if (null != listTypeId) {
						if (null != params[9]) {
							searchTerm = params[9];
						}
					}
				}
			} catch (Exception e) {
			}

			try {
				if (null != params[2]) {
					libGrp = params[2];
				}
			} catch (Exception e) {
			}

			try {
				if (null != params[3]) {
					listName = params[3];
				}
			} catch (Exception e) {
			}

			try {
				if (null != params[4]) {
					sortKey = params[4];
				}
			} catch (Exception e) {
			}

			try {
				if (null != params[5]) {
					filterString = params[5];
				}
			} catch (Exception e) {
			}

			try {
				if (null != params[6]) {
					sortSrcType = params[6];
				}
			} catch (Exception e) {
			}

			try {
				if (null != params[7]) {
					prmrySolrFld = params[7];
				}
			} catch (Exception e) {
			}

			try {
				if (null != params[8]) {
					startUrl = params[8];
				}
			} catch (Exception e) {
			}

			try {
				if (null != params[10]) {
					customerNo = params[10];
				}
			} catch (Exception e) {
			}

		}

		CISUserLoginData cisUserLoginData = new CISUserLoginData();

		// CISUserLoginData cisUserLoginData = fetchLoginCallData(userId);
		// fetchLoginCallExchangeRateTest(customerNo, cisUserLoginData);

		AcademicListService service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);
		AcademicList academicList = null;

		academicList = service.findAcademicListByCustGroup(userId, listTypeId, libGrp);

		System.out.println("academicList:" + academicList);
		Map<String, Object> mapObject = new LinkedHashMap<String, Object>();

		// System.out.println("cisUserLoginData: " + cisUserLoginData);
		if ("SR".equalsIgnoreCase(listTypeId)) {
			testSRListPrepFlow(userId, cisUserLoginData, academicList, filterString, sortKey, sortSrcType, searchTerm);
		} else {

			//list of ttl_id, sortkey, sort type, product type, filterstring
			//Set<Object> searchEansSet = Br1101Helper.prepareNonAcdmSelectionListSearchData(titleIds, sortKey, sortSrcType, "All", null, prmrySolrFld);

			//if(null == searchEansSet){
			//	return;
			//}
			//System.out.println("searchEansSet - " + searchEansSet);
			/*
			try {
				List<Long> oprIdList = new LinkedList<Long>();
				for (String ean : searchEansSet.keySet()) {
					oprIdList.addAll(searchEansSet.get(ean));
				}

				int thisPageRecStart = 0;
				int pageSize = 25;
				// extract the opr id for this page
				int end = thisPageRecStart + pageSize;
				end = end > oprIdList.size() ? oprIdList.size() : end;

				List<Long> oprIdListThisPage = oprIdList.subList(thisPageRecStart, end);

				System.out.println("opr list: " + StringUtils.join(oprIdListThisPage, ','));
				Map<Long, AcademicItemData> academicItemDataMapTmp = ListAcdmDataMap.getAcademicItemDataMap(pageSize, thisPageRecStart, academicList, StringUtils.join(oprIdListThisPage, ','));

				Map<Long, AcademicItemData> academicItemDataMap = new LinkedHashMap<Long, AcademicItemData>();
				for (Long opr : oprIdListThisPage) {
					academicItemDataMap.put(opr, academicItemDataMapTmp.get(opr));
				}
				academicItemDataMapTmp = null;

				for (Long opr : academicItemDataMap.keySet()) {
					System.out.print(academicItemDataMap.get(opr).getTitleId() + ", ");
				}
				System.out.println();

				System.out.println("academicItemDataMap returned opr: " + academicItemDataMap.keySet());

				List<String> thisPageEanList = new LinkedList<String>();
				StringBuilder bldr = new StringBuilder();
				// get all the eans from academicItemDataMap
				for (AcademicItemData obj : academicItemDataMap.values()) {
					bldr.append(obj.getTitleId()).append('+');
					thisPageEanList.add(obj.getTitleId());
				}

				ListSortFilterHelper sortHlpr = ((ListSortFilterHelper) AcademicServiceLocator.getBean(BeanName.SORT_HLPR));
				QueryResponse solrResponse = sortHlpr.getIsbnDetailsFromSolr(thisPageEanList.size(), thisPageEanList);
				// map out the solr document list in the solrResponse by the passed ISBN key
				Map<String, SolrDocument> solrDataMap = new LinkedHashMap<String, SolrDocument>();
				for (SolrDocument rec : solrResponse.getResults()) {
					solrDataMap.put((String) rec.get("EAN"), rec);
				}

				Br8000ProdSolrTest prodList = new Br8000ProdSolrTest(searchEansSet, cisUserLoginData, solrDataMap, userId, academicItemDataMap, academicList);

				// for(SearchResultDataBean<Product> product: prodList.getSearchResult()){
				// System.out.println("EAN:TTL_Id: " + product.getSolrProduct().getEan() + ":" +
				// product.getSolrProduct().getTitleID());
				// }

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			*/
		}// else
	}

	public static void testSRListPrepFlow(String userId, CISUserLoginData cisUserLoginData, AcademicList academicList, String filterString, String sortKey, String sortSrcType, String searchTerm) {

		AcdmUserPrefId userPrefId = new AcdmUserPrefId();
		userPrefId.setPrefId(SearchConstants.maxSrchRslt);
		userPrefId.setUserId(userId);
		AcdmUserPref userPref = ((UserConfigService) AcademicServiceLocator.getService(ServiceName.USER_CONFIG_SERVICE)).getAcdmUserPrefByUser(userPrefId);

		int maxResultsSelect = 0;

		if (null != userPref) {
			maxResultsSelect = (null != userPref.getPrefValue() && !userPref.getPrefValue().isEmpty()) ? Integer.parseInt(userPref.getPrefValue()) : 0;
		}
		if (0 == maxResultsSelect) {
			maxResultsSelect = 100;
		}

		System.out.println("maxResultsSelect:sortSrcType => " + maxResultsSelect + ":" + sortSrcType);
		Map<String, List<Long>> eanOprMap = Br1101Helper.prepareSRListData(cisUserLoginData, maxResultsSelect, academicList, filterString, sortKey, sortSrcType, searchTerm, "Book");
		// System.out.println("eanOprMap: " + eanOprMap.keySet().size() + "\n" + eanOprMap.keySet() + "\n" + eanOprMap);
		System.out.println("eanOprMap: " + eanOprMap.keySet().size() + "\n" + eanOprMap.keySet());

		/*
		 * if (nav.getERecs().size() > 0) { Br8000ProductDOTest br8000ProductListTest = new
		 * Br8000ProductDOTest(eanOprMap, cisUserLoginData, nav, userId, academicItemDataMap, academicList);
		 * 
		 * for (SearchResultDataBean<Product> item : br8000ProductListTest.getSearchResult()) {
		 * 
		 * System.out.println(item.getSolrProduct().getEan() + ", " + item.getSolrProduct().getRelatedProducts()); }
		 * 
		 * // testAlternateProducts(cisUserLoginData, br8000ProductListTest); } else {
		 * 
		 * }
		 */

	}

	public static void testSRListPrepFlow(String userId, String listType, String libGrp, String searchTerm) {

		CISUserLoginData cisUserLoginData = fetchLoginCallData(userId);

		AcademicListService service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);
		AcademicList academicList = null;
		ListSortFilterHelper sortFilterHelper = new ListSortFilterHelper();

		academicList = service.findAcademicListByCustGroup(userId, listType, libGrp);

		List<AcademicListItem> academicItemList = service.getAllBylistId(academicList.getListId());

		Map<String, List<Long>> eanOprMap = sortFilterHelper.getEanOprIdMap(academicItemList);

		Map<Long, AcademicItemData> academicItemDataMap = ListAcdmDataMap.getAcademicItemDataMap(academicList);

		Navigation nav = getProductList(searchTerm);

		if (nav.getERecs().size() > 0) {
			Br8000ProductDOTest br8000ProductListTest = new Br8000ProductDOTest(eanOprMap, cisUserLoginData, nav, userId, academicItemDataMap, academicList);

			for (SearchResultDataBean<Product> item : br8000ProductListTest.getSearchResult()) {

				System.out.println(item.getSolrProduct().getEan() + ", " + item.getSolrProduct().getRelatedProducts());
			}

			// testAlternateProducts(cisUserLoginData, br8000ProductListTest);
		} else {

		}

	}

	private static void testAlternateProducts(CISUserLoginData cisUserLoginData, Br8000ProductDOTest productList) {

		// check alternate products
		for (SearchResultDataBean<Product> prod : productList.getSearchResult()) {
			try {

				System.out.println("show this titles details");
				System.out.println("search Ean: " + prod.getSolrProduct().getEan());

				// if (true == prod.isMilEligible() || true == prod.isEbscoEligible() || true == prod.isEblEligible() ||
				// true == prod.isEbraryEligible()) {
				// System.out.println("Sneekpeek eligible");
				// }

				// if (true == prod.isMilEligible() && true == productList.getEbookOption().get("MIL")) {
				// System.out.println("Show MIL ICON");
				// }
				// if (true == prod.isEbscoEligible() && true == productList.getEbookOption().get("EBSCO")) {
				// System.out.println("Show EBSCO ICON");
				// }
				// if (true == prod.isEblEligible() && true == productList.getEbookOption().get("EBL")) {
				// System.out.println("Show EBL ICON");
				// }
				// if (true == prod.isEbraryEligible() && true == productList.getEbookOption().get("EBRARY")) {
				// System.out.println("Show EBRARY ICON");
				// }

				if (null != prod.getEbookTextDisplay() && !prod.getEbookTextDisplay().isEmpty()) {
					System.out.println("Ebook text display: " + prod.getEbookTextDisplay());
				}
				if (null != prod.getDispEbookLicense() && !prod.getDispEbookLicense().isEmpty()) {
					System.out.println("Ebook License: " + prod.getDispEbookLicense());
				}

				if (null != prod.getDispEbookPrice() && !prod.getDispEbookPrice().isEmpty()) {
					System.out.println("Title Ebook Price:" + prod.getDispEbookPrice());
				} else if (null != prod.getAvailPriceKey() && null != prod.getProdMltiCurPrice(prod.getAvailPriceKey())) {
					System.out.println("Publisher List Price:" + prod.getProdMltiCurPrice(prod.getAvailPriceKey()));
				} else if (null != prod.getPowerSearchCurrency() && null != prod.getProdMltiCurPrice(prod.getPowerSearchCurrency())) {
					System.out.println("Publisher List Price - power search case:" + prod.getProdMltiCurPrice(prod.getPowerSearchCurrency()));
				}

				if (null != prod.getAltEbookSeeInsideDetails()) {
					System.out.println("Alternate See Inside the Book Details:" + prod.getAltEbookSeeInsideDetails());
				}

				System.out.println("<!-- show the others available for this title first -->");
				if (null != prod.getOthersAvailable() && !prod.getOthersAvailable().isEmpty()) {
					for (EBPDataObject thisEan : prod.getOthersAvailable()) {
						System.out.println(prod.getSolrProduct().getEan() + " : " + thisEan);
					}
					System.out.println("plat/lic json: " + prod.getEligiblePlatLicJson());
				}

				System.out.println("-------------------------------------------------");

				System.out.println("<!-- show the also available for alternates title next -->");

				if (null != prod.getSolrProduct().getRelatedProducts()) {
					for (Product altProd : prod.getSolrProduct().getRelatedProducts()) {

						System.out.println("altProd: " + altProd.getEan());
						if (null != altProd.getCisEBPData() && !altProd.getCisEBPData().isEmpty()) {
							// System.out.println("altProd.getCisEBPData(): " + altProd.getCisEBPData());
							for (EBPDataObject ebpObj : altProd.getCisEBPData()) {
								if (null == ebpObj.getCisEbpCatpartner() || ebpObj.getCisEbpCatpartner().isEmpty()) {
									continue;
								}
								for (EBPDataObject userCisEbpData : cisUserLoginData.getEbpDataObjectList()) {
									if (userCisEbpData.getCisEbpCatpartner().equalsIgnoreCase(ebpObj.getCisEbpCatpartner())) {
										if (null != productList.getEbookOption() && productList.getEbookOption().get(ebpObj.getCisEbpCatpartner())) {
											if (userCisEbpData.getCisEbpLicence().equalsIgnoreCase(ebpObj.getCisEbpLicence())) {

												System.out.println("Ean | Alt Ean | Platfrom / License / Description | Price / Currency | TitleId");
												System.out.println(prod.getSolrProduct().getEan() + " | " + altProd.getEan() + " | " + ebpObj.getCisEbpCatpartner() + " / " + ebpObj.getCisEbpLicence()
														+ " / " + productList.getCisEbpCatpartnerDescMap().get(ebpObj.getCisEbpCatpartner()) + " | " + ebpObj.getCisEbpPrice() + " / "
														+ ebpObj.getCisEbpCurrency() + " | " + ebpObj.getCisEbpTitleid());
												System.out.println("-------------------------------------------------");

											}

										}
									}
								}
							}

						} else {
							// System.out.println("in else: " + prod.getSolrProduct().getEan() + ": " + altProd.getEan()
							// + ", " + altProd.getCisFormatDesc());

							if (null != altProd.getAvailPriceKey() && null != altProd.getProdMltiCurPrices().get(altProd.getAvailPriceKey())) {
								System.out.println("Ean | Alt Ean | Platfrom / License / Description | Price / Currency");
								System.out.println(prod.getSolrProduct().getEan() + " | " + altProd.getEan() + " | " + altProd.getCisFormatCode() + " / " + altProd.getCisFormatDesc() + " | "
										+ altProd.getProdMltiCurPrices().get(altProd.getAvailPriceKey()) + " / " + cisUserLoginData.getFirstCurrencyValue());
								System.out.println("-------------------------------------------------");
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

	}

	private static void testAlternateProductsNoEndeca(CISUserLoginData cisUserLoginData, Br8000ProdSolrTest productList) {

		// check alternate products
		for (SearchResultDataBean<Product> prod : productList.getSearchResult()) {
			try {

				System.out.println("show this titles details");
				System.out.println("search Ean: " + prod.getSolrProduct().getEan());

				// if (true == prod.isMilEligible() || true == prod.isEbscoEligible() || true == prod.isEblEligible() ||
				// true == prod.isEbraryEligible()) {
				// System.out.println("Sneekpeek eligible");
				// }

				// if (true == prod.isMilEligible() && true == productList.getEbookOption().get("MIL")) {
				// System.out.println("Show MIL ICON");
				// }
				// if (true == prod.isEbscoEligible() && true == productList.getEbookOption().get("EBSCO")) {
				// System.out.println("Show EBSCO ICON");
				// }
				// if (true == prod.isEblEligible() && true == productList.getEbookOption().get("EBL")) {
				// System.out.println("Show EBL ICON");
				// }
				// if (true == prod.isEbraryEligible() && true == productList.getEbookOption().get("EBRARY")) {
				// System.out.println("Show EBRARY ICON");
				// }

				if (null != prod.getEbookTextDisplay() && !prod.getEbookTextDisplay().isEmpty()) {
					System.out.println("Ebook text display: " + prod.getEbookTextDisplay());
				}
				if (null != prod.getDispEbookLicense() && !prod.getDispEbookLicense().isEmpty()) {
					System.out.println("Ebook License: " + prod.getDispEbookLicense());
				}

				if (null != prod.getDispEbookPrice() && !prod.getDispEbookPrice().isEmpty()) {
					System.out.println("Title Ebook Price:" + prod.getDispEbookPrice());
				} else if (null != prod.getAvailPriceKey() && null != prod.getProdMltiCurPrice(prod.getAvailPriceKey())) {
					System.out.println("Publisher List Price:" + prod.getProdMltiCurPrice(prod.getAvailPriceKey()));
				} else if (null != prod.getPowerSearchCurrency() && null != prod.getProdMltiCurPrice(prod.getPowerSearchCurrency())) {
					System.out.println("Publisher List Price - power search case:" + prod.getProdMltiCurPrice(prod.getPowerSearchCurrency()));
				}

				if (null != prod.getAltEbookSeeInsideDetails()) {
					System.out.println("Alternate See Inside the Book Details:" + prod.getAltEbookSeeInsideDetails());
				}

				System.out.println("<!-- show the others available for this title first -->");
				if (null != prod.getOthersAvailable() && !prod.getOthersAvailable().isEmpty()) {
					for (EBPDataObject thisEan : prod.getOthersAvailable()) {
						System.out.println(prod.getSolrProduct().getEan() + " : " + thisEan);
					}
					System.out.println("plat/lic json: " + prod.getEligiblePlatLicJson());
				}

				System.out.println("-------------------------------------------------");

				System.out.println("<!-- show the also available for alternates title next -->");

				if (null != prod.getSolrProduct().getRelatedProducts()) {
					for (Product altProd : prod.getSolrProduct().getRelatedProducts()) {

						System.out.println("altProd: " + altProd.getEan());
						if (null != altProd.getCisEBPData() && !altProd.getCisEBPData().isEmpty()) {
							// System.out.println("altProd.getCisEBPData(): " + altProd.getCisEBPData());
							for (EBPDataObject ebpObj : altProd.getCisEBPData()) {
								if (null == ebpObj.getCisEbpCatpartner() || ebpObj.getCisEbpCatpartner().isEmpty()) {
									continue;
								}
								for (EBPDataObject userCisEbpData : cisUserLoginData.getEbpDataObjectList()) {
									if (userCisEbpData.getCisEbpCatpartner().equalsIgnoreCase(ebpObj.getCisEbpCatpartner())) {
										if (null != productList.getEbookOption() && productList.getEbookOption().get(ebpObj.getCisEbpCatpartner())) {
											if (userCisEbpData.getCisEbpLicence().equalsIgnoreCase(ebpObj.getCisEbpLicence())) {

												System.out.println("Ean | Alt Ean | Platfrom / License / Description | Price / Currency | TitleId");
												System.out.println(prod.getSolrProduct().getEan() + " | " + altProd.getEan() + " | " + ebpObj.getCisEbpCatpartner() + " / " + ebpObj.getCisEbpLicence()
														+ " / " + productList.getCisEbpCatpartnerDescMap().get(ebpObj.getCisEbpCatpartner()) + " | " + ebpObj.getCisEbpPrice() + " / "
														+ ebpObj.getCisEbpCurrency() + " | " + ebpObj.getCisEbpTitleid());
												System.out.println("-------------------------------------------------");

											}

										}
									}
								}
							}

						} else {
							// System.out.println("in else: " + prod.getSolrProduct().getEan() + ": " + altProd.getEan()
							// + ", " + altProd.getCisFormatDesc());

							if (null != altProd.getAvailPriceKey() && null != altProd.getProdMltiCurPrices().get(altProd.getAvailPriceKey())) {
								System.out.println("Ean | Alt Ean | Platfrom / License / Description | Price / Currency");
								System.out.println(prod.getSolrProduct().getEan() + " | " + altProd.getEan() + " | " + altProd.getCisFormatCode() + " / " + altProd.getCisFormatDesc() + " | "
										+ altProd.getProdMltiCurPrices().get(altProd.getAvailPriceKey()) + " / " + cisUserLoginData.getFirstCurrencyValue());
								System.out.println("-------------------------------------------------");
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

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

	private static void printMapData(Map<?, ?> data) {
		System.out.println("printing map data");
		System.out.println("-----------");

		for (Object key : data.keySet()) {

			System.out.println(key + ":" + data.get(key));

		}
	}

}

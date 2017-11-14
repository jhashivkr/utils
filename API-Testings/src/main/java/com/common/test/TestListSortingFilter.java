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

public class TestListSortingFilter {

	public static void testGetAcademicItemDataMapByItemList() {
		long listId = 910220L;
		String userId = "NATALIEB";
		String listTypeId = "IN";
		String custGroup = "10537";
		int rsltSrchMaxPageCnt = 50;
		int thisPageRecStart = 0;

		AcademicListService service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);

		AcademicList academicList = null;

		try {

			academicList = service.findAcademicListByCustGroup(userId, listTypeId, custGroup);

			List<String> searchTermList = new ArrayList<String>();
			if (null != academicList) {
				listId = academicList.getListId(); // put this in request scope

				// Map<Long, AcademicItemData> academicItemDataMap =
				// ListAcdmDataMap.getAcademicItemDataMapByItemList(listId, listTypeId, rsltSrchMaxPageCnt,
				// thisPageRecStart, academicList, "action");
				Map<Long, AcademicItemData> academicItemDataMap = ListAcdmDataMap.getAcademicItemDataMap(rsltSrchMaxPageCnt, thisPageRecStart, academicList, null);
				// System.out.println("academicItemDataMap: " + academicItemDataMap);
				printMapData(academicItemDataMap);

			}// ifbr8000

		} catch (Exception e) {

			System.out.println("Error from testGetAcademicItemDataMapByItemList: " + e.getMessage());
			e.printStackTrace();

		}

	}

	public static void testGetMultiLineOrderAcademicItemDataMap() {
		long listId = 885836L;
		String userId = "TEST01";
		String listTypeId = "SA";
		String custGroup = "0001";
		int rsltSrchMaxPageCnt = 50;
		int thisPageRecStart = 0;

		AcademicListService service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);
		AcademicList academicList = null;

		FileWriter writer = null;
		BufferedWriter bufWriter = null;

		try {
			writer = new FileWriter(new File("E:/ingram/Tasks/Academics-OASIS-Migration/ACDM-Tables/academicItemDataMap.txt"), true);
			bufWriter = new BufferedWriter(writer);

			academicList = service.findAcademicListByCustGroup(userId, listTypeId, custGroup);

			if (null != academicList) {
				listId = academicList.getListId(); // put this in request scope

				// Map<Long, AcademicItemData> academicItemDataMap = ListAcdmDataMap.getAcademicItemDataMap(listId,
				// listTypeId, academicList);
				Map<Long, AcademicItemData> academicItemDataMap = ListAcdmDataMap.getSortedAcademicOrderInfoItemData(rsltSrchMaxPageCnt, thisPageRecStart, academicList, "ooqty", null);

				printMapData(academicItemDataMap);
				// for (Long key : academicItemDataMap.keySet()) {
				// bufWriter.write(key + ":" + academicItemDataMap.get(key));

				// bufWriter.write("\n");
				// }
			}

			bufWriter.close();
			writer.close();

		} catch (IOException e) {
			System.out.println("Error from DataLoadScheduler: " + e);
			e.printStackTrace();
		} finally {
			try {
				bufWriter.close();
				writer.close();
			} catch (IOException e) {

			}

		}

		try {

		} catch (Exception e) {

			System.out.println("Error from testGetAcademicItemDataMap: " + e.getMessage());
			e.printStackTrace();

		}

	}

	public static void testGetAcademicItemDataMap() {
		long listId = 910220L; // 917447L;
		String userId = "NATALIEB"; // "CROSSIN" ;
		String listTypeId = "IN"; // "SR"; //910220, 917311
		String custGroup = "10537"; // "11100";
		String searchTerm = "";
		int rsltSrchMaxPageCnt = 50;
		int thisPageRecStart = 0;
		String sortKey = "budget";

		AcademicListService service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);

		AcademicList academicList = null;
		try {

			academicList = service.findAcademicListByCustGroup(userId, listTypeId, custGroup);

			if (null != academicList) {
				listId = academicList.getListId(); // put this in request scope

				// Map<Long, AcademicItemData> academicItemDataMap = ListAcdmDataMap.getAcademicItemDataMap(listId,
				// listTypeId, academicList);
				Map<Long, AcademicItemData> academicItemDataMap = ListAcdmDataMap.getAcademicItemDataMap(rsltSrchMaxPageCnt, thisPageRecStart, academicList, null);
				System.out.println("academicItemDataMap: " + academicItemDataMap);

				Map<Long, AcademicAlibrisItem> alibrisItemDataMap = ListAcdmDataMap.getSortedAlibrisItemDataMap(listId, 0, 0, "");

				// For Alibris List
				for (Long oprId : alibrisItemDataMap.keySet()) {
					AcademicAlibrisItem alibrisItem = alibrisItemDataMap.get(oprId);
					System.out.println("academicItemDataMap.get(oprId): " + academicItemDataMap.get(oprId));
					if (null != alibrisItem.getPrice()) {
						academicItemDataMap.get(oprId).setAlibrisCopyPrice(alibrisItem.getPrice().floatValue());
					} else {
						academicItemDataMap.get(oprId).setAlibrisCopyPrice(0F);
					}
				}

				for (Long key : academicItemDataMap.keySet()) {
					System.out.println(key + " : " + academicItemDataMap.get(key).getTitleId() + " : " + academicItemDataMap.get(key).getAlibrisCopyPrice());
				}

				for (Long key : alibrisItemDataMap.keySet()) {
					System.out.println(key + " : " + alibrisItemDataMap.get(key).getEan() + " : " + alibrisItemDataMap.get(key).getPrice().floatValue());
				}

			}

		} catch (Exception e) {

			System.out.println("Error from testGetAcademicItemDataMap: " + e.getMessage());
			e.printStackTrace();

		}

	}

	public static void testListDataPreparation() {
		long listId = 910220L;
		String userId = "NATALIEB";
		String listTypeId = "IN"; // 910220
		String custGroup = "10537";
		String searchTerm = "";
		int rsltSrchMaxPageCnt = 50;
		int thisPageRecStart = 0;

		AcademicListService service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);

		AcademicList academicList = null;
		Map<String, List<Long>> academicListItem;
		List<AcademicListItem> academicItemList = null;
		List<String> searchEans = new LinkedList<String>();
		List<String> searchEansOrderDetails = new LinkedList<String>();

		try {

			academicList = service.findAcademicListByCustGroup(userId, listTypeId, custGroup);

			List<String> searchTermList = new ArrayList<String>();
			if (null != academicList) {
				listId = academicList.getListId(); // put this in request scope

				academicItemList = service.getAllBylistId(listId);

				// Fetch AcademicItemData for All Academic List types
				// Map<Long, AcademicItemData> academicItemDataMap = service.getAcademicItemDataByListId(listId);
				// Map<Long, AcademicItemData> academicItemDataMap = service.getAcademicItemDataByListId(listId,
				// rsltSrchMaxPageCnt, thisPageRecStart);

				Map<Long, AcademicItemData> academicItemDataMap = ListAcdmDataMap.getSortedAcademicItemDataMapByItemList(rsltSrchMaxPageCnt, thisPageRecStart, academicList, "action", null);
				System.out.println("academicItemDataMap: " + academicItemDataMap);

				academicListItem = new HashMap<String, List<Long>>(academicItemList.size());
				int recCursor = 1;
				for (AcademicListItem item : academicItemList) {
					if (null != item.getEan() && recCursor <= 1024) {
						searchEans.add(item.getEan());
						searchEansOrderDetails.add((null == item.getSalesOrderdetl() || item.getSalesOrderdetl().isEmpty() || "null".equalsIgnoreCase(item.getSalesOrderdetl())) ? "" : item
								.getSalesOrderdetl());
						searchTermList.add(item.getEan());

						if (academicListItem.containsKey(item.getEan())) {
							academicListItem.get(item.getEan()).add(item.getOprId());
						} else {
							List<Long> oprs = new LinkedList<Long>();
							oprs.add(item.getOprId());
							academicListItem.put(item.getEan(), oprs);
						}

						recCursor++;
					}

				}// for
			}// ifbr8000

			if (null != searchEans && !searchEans.isEmpty()) {
				System.out.println("searchEans: " + searchEans);
				searchTerm = StringUtils.join(searchEans, ',');
			}

			System.out.println("searchTerm: " + searchTerm);

		} catch (Exception e) {

			System.out.println("Error from testListDataPreparation: " + e.getMessage());
			e.printStackTrace();

		}

	}

	@SuppressWarnings("unchecked")
	public static void testListFilter(String userId, String listType, String libGrp) {

		int rsltSrchMaxPageCnt = 25;
		int thisPageRecStart = 0;

		// String solrFilterStr =
		// "Availability/Stock Statusfilter_data_sepGreater Thanfilter_data_sep10filter_rec_sepAvailability/Stock Statusfilter_data_sepLess Thanfilter_data_sep100filter_rec_sepFundfilter_data_sepEqualsfilter_data_sepabcdfilter_rec_sepUser Actionfilter_data_sepEqualsfilter_data_sepCreatedfilter_rec_sepFundfilter_data_sepEqualsfilter_data_sepabcdfilter_rec_sepFundfilter_data_sepEqualsfilter_data_sepefghfilter_rec_sepFundfilter_data_sepContainsfilter_data_sepA123filter_rec_sepFundfilter_data_sepContainsfilter_data_sepB345filter_rec_sepLocationfilter_data_sepContainsfilter_data_seploc1filter_rec_sepUser Actionfilter_data_sepContainsfilter_data_sepCreatedfilter_rec_sepUser Actionfilter_data_sepContainsfilter_data_sepSelected";
		// String solrFilterStr = "Fundfilter_data_sepExcludesfilter_data_sepGTHM"; //GTHMG
		// String solrFilterStr = "Quantityfilter_data_sepGreater Thanfilter_data_sep5";

		// String solrFilterStr =
		// "Fundfilter_data_sepExcludesfilter_data_sepGTHMfilter_rec_sepUser Actionfilter_data_sepContainsfilter_data_sepCreatedfilter_rec_sepUser Actionfilter_data_sepContainsfilter_data_sepSelected";
		// String solrFilterStr = "Initial Selectorfilter_data_sepExcludesfilter_data_sepBorisovets";

		// String solrFilterStr =
		// "User Actionfilter_data_sepContainsfilter_data_sepOrderfilter_rec_sepUser Actionfilter_data_sepContainsfilter_data_sepSelected";

		// String solrFilterStr = "Availability/Stock Statusfilter_data_sepGreater Thanfilter_data_sep3";

		String solrFilterStr = "Initial Selectorfilter_data_sepContainsfilter_data_sepZylstra";

		// filter_rec_sepUser
		// Actionfilter_data_sepEqualsfilter_data_sepCreatedfilter_rec_sepFundfilter_data_sepEqualsfilter_data_sepabcdfilter_rec_sepFundfilter_data_sepEqualsfilter_data_sepefghfilter_rec_sepFundfilter_data_sepContainsfilter_data_sepA123filter_rec_sepFundfilter_data_sepContainsfilter_data_sepB345filter_rec_sepLocationfilter_data_sepContainsfilter_data_seploc1filter_rec_sepUser
		// Actionfilter_data_sepContainsfilter_data_sepCreatedfilter_rec_sepUser
		// Actionfilter_data_sepContainsfilter_data_sepSelected";

		// this will get from session in br1101 / br8000viewaction

		AcdmFilterHelper hlpr = ((AcdmFilterHelper) AcademicServiceLocator.getBean(BeanName.ACDM_FILTER_HELPER));
		Map<String, List<String>> map = hlpr.filterQueries(solrFilterStr);

		System.out.println("Solr qry: " + map.get("solr"));
		System.out.println("List table qry: " + map.get("listQry"));
		System.out.println("Order table qry: " + map.get("orderQry"));

		AcademicListService service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);

		AcademicList academicList = null;
		academicList = service.findAcademicListByCustGroup(userId, listType, libGrp);

		//

		// test for dobj
		// String dobj = "10/24/2014_10/30/2014";
		String dobj = null;
		Map<String, Object> mapObject = new LinkedHashMap<String, Object>();
		mapObject.put("listId", academicList.getListId());
		mapObject.put("listDropdown", dobj);

		// String byDayWeekCriteria = ListSortFilterHelper.createDobjMap(mapObject, dobj);

		List<AcademicListItem> academicItemList = null;

		if (null != dobj) {
			academicItemList = service.getAllBylistId(mapObject);
		} else {
			academicItemList = service.getAllBylistId(academicList.getListId());
		}

		Map<String, List<Long>> academicListItem = null;
		if (null != academicItemList) {
			academicListItem = new HashMap<String, List<Long>>(academicItemList.size());
			for (AcademicListItem item : academicItemList) {
				if (null != item.getEan()) {

					if (academicListItem.containsKey(item.getEan())) {
						academicListItem.get(item.getEan()).add(item.getOprId());
					} else {
						List<Long> oprs = new LinkedList<Long>();
						oprs.add(item.getOprId());
						academicListItem.put(item.getEan(), oprs);
					}
				}// if

			}// for
		}

		List<Long> oprIdList = new LinkedList<Long>();
		for (String ean : academicListItem.keySet()) {
			oprIdList.addAll(academicListItem.get(ean));
		}
		String byDayWeekCriteria = StringUtils.join(oprIdList, ',');

		System.out.println("byDayWeekCriteria: " + byDayWeekCriteria);

		System.out.println("academicItemList.size(): " + academicItemList.size());
		academicList.setItemCount(academicItemList.size());

		System.out.println("academicList.getListId(): " + academicList.getListId());

		Map<Long, AcademicItemData> academicItemDataMap = ListAcdmDataMap.geFilteredtAcademicItemDataMap(academicList, map.get("listQry"), (Map<String, Map<String, String>>) map.get("orderQry"),
				rsltSrchMaxPageCnt, thisPageRecStart, byDayWeekCriteria);

		if (null != academicItemDataMap && !academicItemDataMap.isEmpty()) {

			printMapData(academicItemDataMap);
		} else {

			System.out.println("Empty map");
		}

	}

	@SuppressWarnings("unchecked")
	public static void testListSortType1(String userId, String listType, String libGrp) {

		int rsltSrchMaxPageCnt = 25;
		int thisPageRecStart = 0;

		AcademicListService service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);

		AcademicList academicList = null;
		academicList = service.findAcademicListByCustGroup(userId, listType, libGrp);

		// test for dobj
		// String dobj = "10/24/2014_10/30/2014";
		String dobj = null;
		Map<String, Object> mapObject = new LinkedHashMap<String, Object>();
		mapObject.put("listId", academicList.getListId());
		mapObject.put("listDropdown", dobj);

		// String solrFilterStr = "Availability/Stock Statusfilter_data_sepGreater Thanfilter_data_sep3";
		String solrFilterStr = "Internal Notefilter_data_sepGreater Thanfilter_data_sep3";
		// String solrFilterStr = "";
		Map<String, List<String>> filterMap = null;

		if (null != solrFilterStr) {
			AcdmFilterHelper hlpr = ((AcdmFilterHelper) AcademicServiceLocator.getBean(BeanName.ACDM_FILTER_HELPER));
			filterMap = hlpr.filterQueries(solrFilterStr);
		}

		if (null != filterMap && !filterMap.isEmpty()) {
			// solr keys are taken care in
			// so only needs to take care of the non solr keys
			Map<String, Map<String, String>> orderQry = (Map<String, Map<String, String>>) filterMap.get("orderQry");
			if ((null != filterMap.get("listQry") && !filterMap.get("listQry").isEmpty()) || (null != orderQry && !orderQry.isEmpty())) {
				System.out.println("here - 13:" + filterMap.get("listQry") + ", " + filterMap.get("orderQry"));
			}
		}

		List<AcademicListItem> academicItemList = null;
		Map<String, List<Long>> academicListItem = null;
		String extraCriteria = null;

		if (null != dobj) {
			academicItemList = service.getAllBylistId(mapObject);

			if (null != academicItemList) {
				academicListItem = new HashMap<String, List<Long>>(academicItemList.size());
				for (AcademicListItem item : academicItemList) {
					if (null != item.getEan()) {

						if (academicListItem.containsKey(item.getEan())) {
							academicListItem.get(item.getEan()).add(item.getOprId());
						} else {
							List<Long> oprs = new LinkedList<Long>();
							oprs.add(item.getOprId());
							academicListItem.put(item.getEan(), oprs);
						}
					}// if

				}// for
			}

			if (null != academicListItem && !academicListItem.isEmpty()) {
				List<Long> oprIdList = new LinkedList<Long>();
				for (String ean : academicListItem.keySet()) {
					oprIdList.addAll(academicListItem.get(ean));
				}

				extraCriteria = StringUtils.join(oprIdList, ',');
			}
		} else {
			academicItemList = service.getAllBylistId(academicList.getListId());
		}

		if (null != filterMap.get("listQry") && !filterMap.get("listQry").isEmpty()) {
			System.out.println("listQry: " + filterMap.get("listQry"));
			StringBuilder bldr = new StringBuilder(extraCriteria);
			bldr.append(" and (").append(StringUtils.join(filterMap.get("listQry"), " OR ")).append(" ) ");
			extraCriteria = bldr.toString();
		} else {

		}

		System.out.println("extraCriteria: " + extraCriteria);

		System.out.println("academicItemList.size(): " + academicItemList.size());
		academicList.setItemCount(academicItemList.size());

		System.out.println("academicList.getListId(): " + academicList.getListId());

		Map<Long, AcademicItemData> academicItemDataMap = ListAcdmDataMap.getAcademicItemDataMap(rsltSrchMaxPageCnt, thisPageRecStart, academicList, extraCriteria);

		if (null != academicItemDataMap && !academicItemDataMap.isEmpty()) {

			printMapData(academicItemDataMap);
		} else {

			System.out.println("Empty map");
		}

	}

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

	// userid, list_tp_id,lib_grp,list name - optional, sort key
	public static void testSorting(String... params) {

		String userId = params[0];
		String listType = params[1];
		String libGrp = params[2];
		String listName = params[3];
		String sortKey = params[4];

		CISUserLoginData cisUserLoginData = fetchLoginCallData(userId);

		AcademicListService service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);

		AcademicList academicList = null;
		ListSortFilterHelper sortFilterHelper = new ListSortFilterHelper();

		if (null != listName && !listName.isEmpty()) {
			academicList = service.findAcademicListByCustGroup(userId, listType, libGrp, listName);
		} else {
			academicList = service.findAcademicListByCustGroup(userId, listType, libGrp);
		}
		System.out.println("AcademicLsit: " + academicList);
		List<AcademicListItem> academicItemList = service.getAllBylistId(academicList.getListId());

		Map<String, List<Long>> eanOprMap = sortFilterHelper.getEanOprIdMap(academicItemList);

		Map<Long, AcademicItemData> academicItemDataMap = ListAcdmDataMap.getSortedAcademicOrderInfoItemData(25, 0, academicList, sortKey, null);

		// get all eans involved
		Set<String> eanSet = new HashSet<String>();
		if (null != academicItemDataMap && !academicItemDataMap.isEmpty()) {
			for (AcademicItemData item : academicItemDataMap.values()) {
				eanSet.add(item.getTitleId());
			}
		} else {
			return;
		}

		Navigation nav = getProductList(eanSet);
		Br8000ProductDOTest br8000ProductListTest = new Br8000ProductDOTest(eanOprMap, cisUserLoginData, nav, userId, academicItemDataMap, academicList);

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
			//9781322081854,9781783509836
			//non ebook - 9781783509843
			
			String qry = "9780839826057 OR 9781575677644 OR 9780900384868 OR 9780802470348 OR 9780613235693 OR 9781424043811 OR 9781424008452 OR 9781424010875 OR 9781424021796 OR 9781424022854 OR 9783791318110 OR 9781466914230 OR 9781466914247 OR 9781418900939 OR 9781502717382 OR 9781501004261 OR 9780977096473 OR 9780977096480 OR 9780174027669 OR 9780757812231 OR 9780757830273 OR 9781418967475 OR 9780802448224&qt=ISBN_SI";
					
			//eanOprMap = Br1101Helper.prepareSRListData(cisUserLoginData, 10, academicList, "", "Title", "1", "9781783509843 OR 9781322081854 OR 9781783509836&qt=ISBN_SI","Book");
			eanOprMap = Br1101Helper.prepareSRListData(cisUserLoginData, 10, academicList, "", "Title", "1", qry,"Book");
			
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
		
		//map out the solr document list in the response by the passed ISBN key
		Map<String, SolrDocument> solrDataMap = new LinkedHashMap<String, SolrDocument>();
		for (SolrDocument rec : response.getResults()) {
			solrDataMap.put((String) rec.get("EAN"), rec);
		}

		//System.out.println(response.getQueryResponse());
		for (FacetField facet : response.getFacetFields()) {
			System.out.println("facet: " + facet.getName() + " = " + facet.getValueCount() + ", " + facet.getValues());
			for (Count count : facet.getValues()) {
				System.out.println("count: " + count.getName() + "=" + count.getCount());
			}
		}
		
		//Navigation nav = getProductList(eanSet);
		//Br8000ProductDOTest br8000ProductListTest = new Br8000ProductDOTest(eanOprMap, cisUserLoginData, nav, userId, academicItemDataMap, academicList);
		//testAlternateProducts(cisUserLoginData, br8000ProductListTest);
		
		Br8000ProdSolrTest br8000ProdSolrTest = new Br8000ProdSolrTest(eanOprMap, cisUserLoginData, solrDataMap, userId, academicItemDataMap, academicList);
		testAlternateProductsNoEndeca(cisUserLoginData, br8000ProdSolrTest);
		
	}

	public static void testListPrepFlow(String... params) {

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

		if (null != params) {
			if (null != params[0]) {
				userId = params[0];
			}
			if (null != params[1]) {
				listTypeId = params[1];
				if (null != listTypeId) {
					searchTerm = params[9];
				}
			}
			if (null != params[2]) {
				libGrp = params[2];
			}
			if (null != params[3]) {
				listName = params[3];
			}
			if (null != params[4]) {
				sortKey = params[4];
			}
			if (null != params[5]) {
				filterString = params[5];
			}
			if (null != params[6]) {
				sortSrcType = params[6];
			}
			if (null != params[7]) {
				dobj = params[7];
			}

			if (null != params[8]) {
				startUrl = params[8];
			}

			if (null != params[10]) {
				customerNo = params[10];
			}

		}

		CISUserLoginData cisUserLoginData = fetchLoginCallData(userId);
		fetchLoginCallExchangeRateTest(customerNo, cisUserLoginData);

		// extra check for sort by pub date
		// COUTTS_DIVISION_PUB_DATE_SEARCH_*&Nso=0
		if (SortByFields.PUB_DATE.getDisplayName().contains(sortKey)) {
			// get the approval center of this user and replace * in the date with this approval center
			sortKey = sortKey.replace("*", cisUserLoginData.getApprovalCenter().trim());
		}

		AcademicListService service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);
		AcademicList academicList = null;

		academicList = service.findAcademicListByCustGroup(userId, listTypeId, libGrp);

		System.out.println("academicList:" + academicList);
		Map<String, Object> mapObject = new LinkedHashMap<String, Object>();

		// System.out.println("cisUserLoginData: " + cisUserLoginData);
		if ("SR".equalsIgnoreCase(listTypeId)) {
			testSRListPrepFlow(userId, cisUserLoginData, academicList, filterString, sortKey, sortSrcType, searchTerm);
		} else {
			try {
				long listId = 0l;
				if (null != academicList) {
					listId = academicList.getListId();

					if (null != dobj) { // for week by lists
						mapObject.put("listId", listId);
						mapObject.put("listDropdown", dobj);
					}

					else if (null == dobj) { // for week by lists

						try {
							// get the last week by default selectedIndex
							AcademicFilterListService filterService = (AcademicFilterListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_LIST_FILTER_SERVICE);
							BrowseRowService browseRowService = new BrowseRowService();
							List<Map<String, Object>> list = null;
							if (AcademicListType.SHOPPING_CART.getListTypeId().equalsIgnoreCase(listTypeId)) {

								Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
								paramMap.put("isByWeekchoose", false);
								paramMap.put("userOwnerId", userId);
								paramMap.put("listId", listId);
								list = filterService.getByDays(paramMap);
								browseRowService.setAllDaysForByDay(list);
								dobj = (String) list.get(0).get("dateobj");

								// if (request.getParameter("byDaySCIndex") != null) {
								// String index = request.getParameter("byDaySCIndex");
								// request.setAttribute("byDaySCIndex", index);
								// }

							} else {
								list = filterService.getByWeeks(academicList.getListId(), userId);
								dobj = (String) list.get(1).get("dateobj");
							}
							// request.setAttribute("dobj", dobj);

							mapObject.put("listId", listId);
							mapObject.put("listDropdown", dobj);
						} catch (Exception e) {
							dobj = null;
						}
					}
				}
			} catch (Exception e) {

			}

			Map<String, List<Long>> eanOprMap = Br1101Helper.prepareAcdmListSearchData(cisUserLoginData, academicList, dobj, filterString, sortKey, sortSrcType, "All");

			System.out.println("eanOprMap - keys: (" + eanOprMap.keySet().size() + "), " + eanOprMap.keySet());
			try {
				List<Long> oprIdList = new LinkedList<Long>();
				for (String ean : eanOprMap.keySet()) {
					oprIdList.addAll(eanOprMap.get(ean));
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
				
				Br8000ProdSolrTest prodList = new Br8000ProdSolrTest(eanOprMap, cisUserLoginData, solrDataMap, userId, academicItemDataMap, academicList);
				
				//for(SearchResultDataBean<Product> product: prodList.getSearchResult()){
				//	System.out.println("EAN:TTL_Id: " + product.getSolrProduct().getEan() + ":" + product.getSolrProduct().getTitleID());
				//}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
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

	// handle academic list view data population
	private static Map<String, List<Long>> prepareAcdmListSearch(String userId, Long listId, String listTypeId, String custGroup, AcademicList academicList, CISUserLoginData cisUserLoginData,
			String dobj, String filterString, String sortKey, String sortSrcType, String startUrl) {

		AcademicListService service;

		service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);

		Map<Long, AcademicAlibrisItem> alibrisMap = null;
		List<AcademicListItem> academicItemList = null;
		List<String> searchEans = new LinkedList<String>();
		List<String> searchEansOrderDetails = new LinkedList<String>();

		try {
			// Handle separately for UD list
			if (AcademicListType.USER_DEFINED_LIST.getListTypeId().equals(listTypeId)) {
				try {

					academicList = service.getAcademicList(listId);
					academicItemList = service.getAllBylistId(listId);

				} catch (Exception e) {

				}
			} else {
				academicList = service.findAcademicListByCustGroup(userId, listTypeId, custGroup);
				try {
					Map<String, Object> mapObject = new LinkedHashMap<String, Object>();

					if (null != dobj) { // for week by lists
						mapObject.put("listId", listId);
						mapObject.put("listDropdown", dobj);
						academicItemList = service.getAllBylistId(mapObject);
					}

					// this should be the default
					else if (null == dobj) { // for week by lists
						System.out.println("here");

						// bypassing - for testing
						// academicItemList = service.getAllBylistId(listId);

						try {

							AcademicFilterListService filterService = (AcademicFilterListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_LIST_FILTER_SERVICE);
							List<Map<String, Object>> list = null;

							if (AcademicListType.SHOPPING_CART.getListTypeId().equalsIgnoreCase(listTypeId)) {

								Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
								paramMap.put("isByWeekchoose", false);
								paramMap.put("userOwnerId", userId);
								paramMap.put("listId", listId);
								list = filterService.getByDays(paramMap);
								dobj = (String) list.get(0).get("dateobj");
							} else {
								list = filterService.getByWeeks(listId, userId);
								dobj = (String) list.get(0).get("dateobj");
							}
							// get the last week by default
							mapObject.put("listId", listId);
							mapObject.put("listDropdown", dobj);
							academicItemList = service.getAllBylistId(mapObject);
						} catch (Exception e) {
							dobj = null;
							academicItemList = service.getAllBylistId(listId);
						}

						System.out.println("listId: " + listId);
						academicItemList = service.getAllBylistId(listId);

					}

				} catch (Exception e) {

				}
			}

			if (null != academicList && null != academicItemList) {

				UrlGen urlg = new UrlGen(startUrl, "UTF-8");

				ListSortFilterHelper sortFilterHelper = new ListSortFilterHelper();
				Map<String, List<Long>> eanOprMap = null;

				if (null != urlg.getParam("Ns") && !urlg.getParam("Ns").isEmpty()) {
					sortKey = urlg.getParam("Ns");
					AcdmSortType sortType = AcdmSortType.valueOf(sortKey);
					sortKey = sortType.getFieldValue();
				}

				eanOprMap = sortFilterHelper.getEanOprIdMap(academicItemList, filterString, sortKey, sortSrcType);

				System.out.println("eanOprMap - keys: (" + eanOprMap.keySet().size() + "), " + eanOprMap.keySet());

				searchEans = new LinkedList<String>(eanOprMap.keySet());

				// get data and initiate check status
				populateAcdmItemDataMap(userId, custGroup, academicList, academicItemList, eanOprMap, cisUserLoginData, urlg, filterString, sortKey, sortSrcType, dobj, 0, 25);

				return eanOprMap;

			}// if (null != academicList)
			else if (null == academicList) {
				academicList = new AcademicList();
				academicList.setListId(0l);
				academicList.setListType(listTypeId);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	@SuppressWarnings("unchecked")
	private static final void populateAcdmItemDataMap(String userId, String custGroup, AcademicList academicList, List<AcademicListItem> academicItemList, Map<String, List<Long>> eanOprMap,
			CISUserLoginData cisUserLoginData, UrlGen urlg, String filterString, String sortKey, String sortSrcType, String dobj, int thisPageRecStart, int pageSize) {

		Map<Long, AcademicItemData> academicItemDataMap = new LinkedHashMap<Long, AcademicItemData>();
		int sortType = 0;

		// to be changed and aligned with pagination style
		if (AcademicListType.ALIBRIS_LIST.getListTypeId().equals(academicList.getListType()) || AcademicListType.SEARCH_RESULTS.getListTypeId().equals(academicList.getListType())) {
			academicItemDataMap = ListAcdmDataMap.getAcademicItemDataMap(academicList);
		} else {
			List<String> byDayWeekCriteria = null;
			List<Long> oprIdList = null;

			// if (!AcademicListType.USER_DEFINED_LIST.getListTypeId().equals(academicList.getListType())) {

			// if (null != dobj) {
			// get the academicItemList

			// contains the search opr_id in the case of dobj
			oprIdList = new LinkedList<Long>();
			for (String ean : eanOprMap.keySet()) {
				oprIdList.addAll(eanOprMap.get(ean));
			}

			// get only that count which is required - but this will not sort the complete result
			// only the page will be sorted
			// thisPageRecStart, int pageSize
			// pageSize = ((pageSize + thisPageRecStart) <= oprIdList.size()) ? pageSize : (oprIdList.size() -
			// thisPageRecStart);
			// System.out.println("pageSize: " + pageSize);
			// List<Long> oprIdListThisReq = oprIdList.subList(thisPageRecStart, thisPageRecStart + (pageSize));

			List<Long> oprIdListThisReq = oprIdList;

			System.out.println("size of oprIdList, size of oprIdListThisReq : " + oprIdList.size() + ", " + oprIdListThisReq.size());

			// add the list item in the critera
			if (oprIdListThisReq.size() > 1000) {
				byDayWeekCriteria = breakOprIdList(oprIdListThisReq);
			} else {
				byDayWeekCriteria = new LinkedList<String>();
				byDayWeekCriteria.add(StringUtils.join(oprIdListThisReq, ','));
			}

			// thisPageRecStart = 0;

			// }
			// }

			try {
				// thisPageRecStart = Integer.parseInt(request.getParameter("No"));
			} catch (Exception e) {
				thisPageRecStart = 0;
			}

			Map<String, List<String>> filterMap = null;

			if (null != filterString) {
				AcdmFilterHelper hlpr = ((AcdmFilterHelper) AcademicServiceLocator.getBean(BeanName.ACDM_FILTER_HELPER));
				filterMap = hlpr.filterQueries(filterString);

			}
			// sortOrder
			// if (null != sortSrcType && !AcademicListType.SEARCH_ORDERS.getListTypeId().equals(listTypeId)) {
			if (null != sortSrcType) {

				try {
					sortType = Integer.parseInt(sortSrcType);
				} catch (Exception e) {
					sortType = 0;
				}

				List<String> extraCriteria = new LinkedList<String>();

				if ((null != filterMap && !filterMap.isEmpty()) && (null != filterMap.get("listQry") && !filterMap.get("listQry").isEmpty())) {
					StringBuilder bldr = new StringBuilder();
					for (String criteria : byDayWeekCriteria) {
						bldr.append(criteria).append(" and (").append(StringUtils.join(filterMap.get("listQry"), " OR ")).append(" ) ");
						extraCriteria.add(bldr.toString());
						bldr.delete(0, bldr.length());
					}

				} else {
					extraCriteria = byDayWeekCriteria;
				}

				for (String criteria : extraCriteria) {
					if (1 == sortType || 5 == sortType) {
						academicItemDataMap.putAll(ListAcdmDataMap.getAcademicItemDataMap(pageSize, thisPageRecStart, academicList, criteria));
					} else if (2 == sortType) {
						academicItemDataMap.putAll(ListAcdmDataMap.getSortedAcademicOrderInfoItemData(pageSize, thisPageRecStart, academicList, sortKey, criteria));
					} else if (3 == sortType) {
						academicItemDataMap.putAll(ListAcdmDataMap.getSortedAcademicItemDataMapByItemList(pageSize, thisPageRecStart, academicList, sortKey, criteria));
					}
				}

				if ((null != filterMap && !filterMap.isEmpty())) {
					Map<String, Map<String, String>> orderQry = (Map<String, Map<String, String>>) filterMap.get("orderQry");

					if (null != orderQry && !orderQry.isEmpty()) {
						Map<String, Map<String, String>> filterDataMap = (Map<String, Map<String, String>>) filterMap.get("orderQry");

						if ((null != academicItemDataMap && !academicItemDataMap.isEmpty()) && (null != filterDataMap && !filterDataMap.isEmpty())) {
							// filter out the result
							for (String criteria : filterDataMap.keySet()) {

								Map<String, String> tmpMap = filterDataMap.get(criteria);

								for (String inKey : tmpMap.keySet()) {
									academicItemDataMap = ListDataFilter.filterDataFromDataObject(criteria, inKey, tmpMap.get(inKey), academicItemDataMap);
								}
							}
						}
					}// if

				}

			} else if (null != filterMap && !filterMap.isEmpty()) {
				// solr keys are taken care in
				// so only needs to take care of the non solr keys
				Map<String, Map<String, String>> orderQry = (Map<String, Map<String, String>>) filterMap.get("orderQry");
				if ((null != filterMap.get("listQry") && !filterMap.get("listQry").isEmpty()) || (null != orderQry && !orderQry.isEmpty())) {
					for (String criteria : byDayWeekCriteria) {
						academicItemDataMap.putAll(ListAcdmDataMap.geFilteredtAcademicItemDataMap(academicList, filterMap.get("listQry"), orderQry, pageSize, thisPageRecStart, criteria));
					}

				} else {
					// byDayWeekCriteria should be
					for (String criteria : byDayWeekCriteria) {
						academicItemDataMap.putAll(ListAcdmDataMap.getAcademicItemDataMap(pageSize, thisPageRecStart, academicList, criteria));
					}
				}
			} else {
				if (null != byDayWeekCriteria) {
					for (String criteria : byDayWeekCriteria) {
						System.out.println("criteria: " + Arrays.asList(criteria.split("\\,")).size());
						System.out.println("criteria: " + criteria);
						academicItemDataMap.putAll(ListAcdmDataMap.getAcademicItemDataMap(pageSize, thisPageRecStart, academicList, criteria));
					}
				}
			}

		}// else

		System.out.println("academicItemDataMap: " + academicItemDataMap);
		if (2 == sortType || 3 == sortType || 5 == sortType) {
			// these two values must not go to solr if sortSrcType is 2
			urlg.removeParam("Ns");
			urlg.removeParam("sortOrder");
		}

		System.out.println("eanOprMap size , academicItemDataMap size: " + eanOprMap.size() + ", " + academicItemDataMap.size());
		initiateCheckStatusCall(urlg, academicList, academicItemDataMap, academicItemList, cisUserLoginData);

	}

	private static void initiateCheckStatusCall(UrlGen urlg, AcademicList academicList, Map<Long, AcademicItemData> academicItemDataMap, List<AcademicListItem> academicItemList,
			CISUserLoginData cisUserLoginData) {

		// this part is only applicable if and only if
		// user has selected a non solr sorting key
		if (null != academicItemDataMap && !academicItemDataMap.isEmpty()) {
			// bcos the only x number of eans is provided for search to solr
			urlg.removeParam("No");
			urlg.addParam("No", "0");

			List<String> searchEansList = new LinkedList<String>();
			List<String> searchEansOrderDetails = new LinkedList<String>();

			// get all eans involved
			Set<String> eanSet = new HashSet<String>();
			StringBuilder bldr = new StringBuilder();
			// get all the eans from academicItemDataMap
			for (AcademicItemData obj : academicItemDataMap.values()) {
				bldr.append(obj.getTitleId()).append('+');
				searchEansList.add(obj.getTitleId());
				eanSet.add(obj.getTitleId());
			}
			try {

				String searchEans = URLDecoder.decode(bldr.toString(), "UTF-8");
				String[] termArr = SearchConstants.pipePattern.split(URLDecoder.decode(StringUtility.replaceAllSubstrings(urlg.getParam("Ntt"), "%25", "%"), "UTF-8"));

				if (termArr.length > 1) {
					termArr[0] = searchEans.trim();
					searchEans = StringUtils.join(termArr, '|');
				}

				urlg.removeParam("Ntt");
				urlg.addParam("Ntt", SearchUtility.disallowHighResourceKeywords(searchEans));

				ListSortFilterHelper sortFilterHelper = new ListSortFilterHelper();
				Map<String, List<Long>> eanOprMap = null;
				eanOprMap = sortFilterHelper.getEanOprIdMap(academicItemList);
				searchEansOrderDetails = sortFilterHelper.getSearchEansOrderDetails();

				Navigation nav = getProductList(eanSet);

				Br8000ProductDOTest br8000ProductListTest = new Br8000ProductDOTest(eanOprMap, cisUserLoginData, nav, academicList.getUserOwnerId(), academicItemDataMap, academicList);

				// fetch the logged in users cis customer group (library group)
				// String libraryGrp = (String) session.get("cisCustGroup");
				// String acdmUserId = (String) session.get("sessionUserID");

				// String sandbox = (String) session.get("sandbox");
				// if (null == sandbox || sandbox.isEmpty()) {
				String sandbox = "kboudre";
				// }

				// make the ganimede calls to get the check status data
				// new BrowseRowGnmdUrls.Builder(searchEansList,
				// academicList.getListId()).salesOrderDetails(searchEansOrderDetails).idList(academicList.getListType())
				// .userLibGrp(academicList.getLibGrp()).acdmUserId(academicList.getUserOwnerId()).sandbox(sandbox).buildBrowseRowUrl();

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}// if
	}

	private final static String parseFilter(String filterStr) {
		filterStr = filterStr.replaceAll("%20", " ");
		// append the filter query in the solrQuery

		AcdmFilterHelper hlpr = ((AcdmFilterHelper) AcademicServiceLocator.getBean(BeanName.ACDM_FILTER_HELPER));
		Map<String, List<String>> map = hlpr.filterQueries(filterStr);

		List<String> fQryList = map.get("solr");

		if (null != fQryList) {
			return StringUtils.join(fQryList, " AND ");
		}
		return "";

	}

	private static final List<String> breakOprIdList(List<Long> oprIdList) {
		List<String> byDayWeekCriteria = new LinkedList<String>();
		int limit = 1000;
		int extra = oprIdList.size() % limit;
		int loops = (extra > 0) ? (oprIdList.size() / limit) + 1 : (oprIdList.size() / limit);
		int ctr = 0;
		int fromIndex = 0;
		int toIndex = 0;

		for (; ctr < loops; ctr++) {
			fromIndex = (ctr * limit);
			toIndex = ((fromIndex + limit) < oprIdList.size()) ? (fromIndex + limit) : (oprIdList.size());

			byDayWeekCriteria.add(StringUtils.join(oprIdList.subList(fromIndex, toIndex), ','));

		}
		return byDayWeekCriteria;
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

package com.common.test;

import ibg.academics.cis.h.pojos.AcdmListOrderInfo;
import ibg.academics.dto.AcademicList;
import ibg.academics.dto.AcademicListItem;
import ibg.academics.service.AcademicListService;
import ibg.academics.service.AcademicServiceLocator;
import ibg.academics.service.AcademicServiceLocator.ServiceName;
import ibg.browserow.dto.AcademicItemData;
import ibg.browserow.service.OrderInfoService;
import ibg.product.information.Product;
import ibg.product.search.SearchResultDataBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TestBrowseRowProductNonSolrSorting {

	private static Map<String, List<Long>> academicListItem = new HashMap<String, List<Long>>();
	private static Map<Long, AcademicItemData> academicItemDataMap = new HashMap<Long, AcademicItemData>();
	private static List<String> eans = new LinkedList<String>(Arrays.asList(new String[] { "9781241791797", "9781452299600" }));
	private static List<SearchResultDataBean<Product>> searchResult = new LinkedList<SearchResultDataBean<Product>>();
	private static List<Product> productsList;

	private static AcademicList academicList = null;
	private static List<AcademicListItem> academicItemList = null;
	private static List<String> searchEans = new LinkedList<String>();
	private static List<String> searchEansOrderDetails = new LinkedList<String>();

	private static long listId = 910800L;
	private static String userId = "MAXYMUK";
	private static String listTypeId = "SN";
	private static String custGroup = "10537";
	private static String searchTerm = "";

	public TestBrowseRowProductNonSolrSorting() {

		// getAcademicItemDataByListId();

		createBrowseRowData();
		printProducts();
	}

	private static void createBrowseRowData() {

		AcademicListService service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);

		try {

			academicList = service.findAcademicListByCustGroup(userId, listTypeId, custGroup);
			System.out.println("academicList: " + academicList);
			List<String> searchTermList = new ArrayList<String>();
			if (null != academicList) {
				listId = academicList.getListId(); // put this in request scope

				academicItemList = service.getAllBylistId(listId);

				// Fetch AcademicItemData for All Academic List types
				academicItemDataMap = service.getAcademicItemDataByListId(listId);

				// to get data only required for this page
				// Map<Long, AcademicItemData> academicItemDataMap = service.getAcademicItemDataByListId(listId, 25, 1);

				// academicListItem = new HashMap<String, List<Long>>(academicItemList.size());
				// int recCursor = 1;
				// for (AcademicListItem item : academicItemList) {
				// if (null != item.getEan() && recCursor <= 1024) {
				// searchEans.add(item.getEan());
				// searchEansOrderDetails.add((null == item.getSalesOrderdetl() || item.getSalesOrderdetl().isEmpty() ||
				// "null".equalsIgnoreCase(item.getSalesOrderdetl())) ? "" : item
				// .getSalesOrderdetl());
				// searchTermList.add(item.getEan());

				// if (academicListItem.containsKey(item.getEan())) {
				// academicListItem.get(item.getEan()).add(item.getOprId());
				// } else {
				// List<Long> oprs = new LinkedList<Long>();
				// oprs.add(item.getOprId());
				// academicListItem.put(item.getEan(), oprs);
				// }

				// recCursor++;
				// }

				// }// for
			}// if (null != academicList)

			// if (null != searchEans && !searchEans.isEmpty()) {
			// System.out.println("searchEans: " + searchEans);
			// searchTerm = StringUtils.join(searchEans, ',');
			// }

			// System.out.println("searchTerm: " + searchTerm);
		} catch (Exception e) {

			System.out.println("Error from createBrowseRowData: TestBrowseRowProductNonSolrSorting: " + e.getMessage());
			e.printStackTrace();

		}

	}

	private void getAcademicItemDataByListId() {

		OrderInfoService service = (OrderInfoService) AcademicServiceLocator.getService(ServiceName.ORDER_INFO_SERVICE);

		// Criteria criteria = getCurrentSession().createCriteria(AcdmListOrderInfoId.class, "orderInfoId");
		// criteriaDetails = getCurrentSession().createCriteria(AcdmListOrderInfo.class, "orderInfo");
		// criteria.addOrder(Order.asc("fieldValue"));

		List<AcdmListOrderInfo> orderInfoList = service.getAcademicItemDataByListId(listId);

		for (AcdmListOrderInfo info : orderInfoList) {
			System.out.println("info: " + info);
		}

		/*
		 * final String OPR_ID = "opr_id"; final String QUANTITY = "quantity"; final String DATE_TO_EXPIRE =
		 * "date_to_expire"; final String SELECTOR_ID = "selector_id"; final String SRC_LIST_ID = "src_list_id"; final
		 * String APPROVAL_PLAN = "approval_plan"; // Approval Plan as Profile Code
		 * 
		 * Map<Long, AcademicItemData> academicItemDataMap = new HashMap<Long, AcademicItemData>(); // Fetch Academic
		 * List Item List<Map<String, Object>> dbResultSet = getJdbcTemplate().queryForList(academicItemDataQuery); for
		 * (Map<String, Object> dbResult : dbResultSet) { Long oprId = ((Number) dbResult.get(OPR_ID)).longValue(); Long
		 * quantity = null; Object qtyObj = dbResult.get(QUANTITY); Date dateToExpire = (Date)
		 * dbResult.get(DATE_TO_EXPIRE); AcademicItemData itemData = new AcademicItemData(); itemData.setOprId(oprId);
		 * if (qtyObj != null) { quantity = ((Number) qtyObj).longValue(); } else { quantity = 0L; }
		 * 
		 * itemData.setQuantity(quantity); itemData.setPatronSelectionDate(dateToExpire); // Set approval plan as
		 * Profile code itemData.setProfileCode((String) dbResult.get(APPROVAL_PLAN));
		 * 
		 * // get cis user details for selector String selectorId = (String) dbResult.get(SELECTOR_ID); long srcListId =
		 * 0L; if (null != dbResult.get(SRC_LIST_ID)) { srcListId = ((Number) dbResult.get(SRC_LIST_ID)).longValue(); }
		 * AcademicListService academicListService = (AcademicListService)
		 * AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE); Map<String, String> cisDetails =
		 * academicListService.getCisUserDetails(srcListId, selectorId); setInitialSelector(cisDetails, itemData);
		 * 
		 * academicItemDataMap.put(oprId, itemData); }// for
		 * 
		 * return academicItemDataMap;
		 */
	}

	private static void sortBrowseRowData(Map<Long, AcademicItemData> academicItemDataMap) {

	}

	private static void printProducts() {

		System.out.println("------------------------------------------------------------------------");
		for (Long key : academicItemDataMap.keySet()) {

			System.out.println("Item: " + academicItemDataMap.get(key));

			System.out.println("------------------------------------------------------------------------");

		}
	}

}

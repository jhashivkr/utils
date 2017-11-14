package com.common.test;

import ibg.academics.dto.AcademicList;
import ibg.academics.dto.AcademicListItem;
import ibg.academics.service.AcademicListService;
import ibg.academics.service.AcademicServiceLocator;
import ibg.academics.service.AcademicServiceLocator.ServiceName;
import ibg.common.product.ProductInformation;
import ibg.common.utility.StringUtility;
import ibg.ganimede.service.GanimedeResponse;
import ibg.ganimede.service.GanimedeWorker;
import ibg.ganimede.service.GanimedeWorkerImpl;
import ibg.lib.activity.BrowseRowUserInfoSrvc;
import ibg.lib.activity.EanDBStatus;
import ibg.lib.activity.HttpCallUrl;
import ibg.lib.activity.browserowp.db.objects.AcdmUserObject;
import ibg.lib.activity.browserowp.db.objects.AcdmUserObjectImpl;
import ibg.lib.activity.browserowp.db.objects.BrowseRowDBProcessObj;
import ibg.lib.activity.browserowp.db.objects.ItemActionInfoObj;
import ibg.lib.activity.browserowp.db.objects.ItemBrowseRowTabDataObj;
import ibg.product.information.service.RecordFilterCriteria;
import ibg.product.information.service.RecordFilterService;
import ibg.product.information.service.RecordFilterServiceAcdmImpl;
import ibg.product.search.AcdmFilterHelper;
import ibg.product.search.SearchUtility;
import ibg.product.search.acdm.SearchConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestQPut {

	private static int ctr = 0;
	private static StringBuilder strBldr = new StringBuilder();
	static int ean_no_limit = 10;

	private static Queue<Long> listQ = new LinkedList<Long>();
	private static ClassPathXmlApplicationContext context = null;

	static {

		try {

			context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private static void checkStatus() {

		AcademicListService service = null;

		service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);

		AcademicList academicList = null;
		Map<String, Long> academicListItem = null;
		List<AcademicListItem> academicItemList = null;
		StringBuilder srchIsbnString = new StringBuilder();

		try {
			// academicList = service.getAcademicListByUser(userId, listTypeId);
			academicItemList = service.getAllBylistId(18l);
			academicListItem = new HashMap<String, Long>(academicItemList.size());

			for (AcademicListItem item : academicItemList)
				System.out.println("ean, oprid, date_created, date_modified: " + item.getEan() + ", " + item.getOprId() + ", " + item.getDateCreated() + ", " + item.getDateModified());

			context.close();
		} catch (Exception e) {

			System.out.println(e.getMessage()); // to be removed
		}

	}

	private static void checkStatusDb() {

		AcademicListService service = null;

		service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);

		Map<String, Set<EanDBStatus>> eanStatus = null;

		List<String> eanList = Arrays.asList("9780439050005", "9780470281680", "9780553275728", "9780830837007");

		try {

			eanStatus = service.findEanStatus("0001", eanList);

			for (String item : eanStatus.keySet()) {
				System.out.println("item: " + item + ": " + eanStatus.get(item));
			}

			context.close();
		} catch (Exception e) {

			System.out.println(e.getMessage()); // to be removed
		}

	}

	private static void checkTabStatusDb() {

		AcademicListService service = null;

		service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);

		Set<ItemBrowseRowTabDataObj> eanStatus = null;

		try {

			eanStatus = service.findEanBrowseRowStatus("0001", "9780439050005", "rma8de");

			for (ItemBrowseRowTabDataObj item : eanStatus) {
				System.out.println("item: " + item.toString());
			}

			context.close();
		} catch (Exception e) {

			System.out.println(e.getMessage()); // to be removed
		}

	}

	public static void testIterator() {
		ConcurrentMap<Long, String> respObjMap = new ConcurrentHashMap<Long, String>();

		respObjMap.put(1l, "shiv");
		respObjMap.put(2l, "shiv1");
		respObjMap.put(3l, "shiv2");
		respObjMap.put(4l, "shiv3");
		respObjMap.put(5l, "shiv4");
		respObjMap.put(3l, "shiv10");

		Iterator<Long> itr = respObjMap.keySet().iterator();

		System.out.println("before: " + respObjMap.toString());

		while (itr.hasNext()) {
			Long key = itr.next();
			if (2l == key) {
				System.out.println(respObjMap.get(key));
				itr.remove();
			}
		}

		System.out.println("after: " + respObjMap.toString());

	}

	private static void printProd() {

		// JdbcProductDAO service = (JdbcProductDAO) AcademicServiceLocator.getBean("productDAO");

		// service.printAll();

	}

	private static void testGnmdComps() {

		System.out.println("-----------------------------");

		String BASE_URL = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program=";
		String BROWSE_ROW_CHECK_STATUS = "ipage/browserow.p&IDList=cart&user_id=?&cust-group=?&EANList=?&oidOrderList=?&eBookDetailList=?";

		HttpCallUrl gnmdUrl = new HttpCallUrl(true, BROWSE_ROW_CHECK_STATUS);

		gnmdUrl.setString(3, "martin");
		gnmdUrl.setString(4, "11100");
		gnmdUrl.setString(5, "9781401237462,9781299187764,9783836549042,9781282292062");
		gnmdUrl.setString(6, ",,,");
		gnmdUrl.setString(7, ":,MIL:3,:,:");

		HttpCallUrl gnmdUrl1 = new HttpCallUrl(false, BROWSE_ROW_CHECK_STATUS);

		gnmdUrl1.setString(2, "smorgan");
		gnmdUrl1.setString(3, "WAG320");
		gnmdUrl1.setString(4, "9781401237462,9781299187764,9783836549042,9781282292062");
		gnmdUrl1.setString(5, ",,,");
		gnmdUrl1.setString(6, ":,MIL:3,MIL:4,:");

		HttpCallUrl gnmdUrl2 = new HttpCallUrl("BROWSE_ROW_CHECK_STATUS");
		gnmdUrl2.setString(3, "pHenry");
		gnmdUrl2.setString(4, "81200");
		gnmdUrl2.setString(5, "9781401237462,9781299187764,9783836549042,9781282292062");
		gnmdUrl2.setString(6, ",,,");
		gnmdUrl2.setString(7, "MIL:2,MIL:3,MIL:4,:");

		HttpCallUrl gnmdUrl3 = new HttpCallUrl("EAN_CHECK_STATUS");
		gnmdUrl3.setString(3, "53068000");
		gnmdUrl3.setString(4, "9781401237462");

		System.out.printf("gnmdUrl: %s\n\n", gnmdUrl.getGnmdUrl());
		System.out.printf("gnmdUrl1: %s\n\n", gnmdUrl1.getGnmdUrl());
		System.out.printf("gnmdUrl2: %s\n\n", gnmdUrl2.getGnmdUrl());
		System.out.printf("gnmdUrl3: %s\n\n", gnmdUrl3.getGnmdUrl());

	}

	public static void ganimedeCheckStatusTester() {

		String eanList = "9781848729483,9780500093559,9781439817087,9780393073775,9780224080699,9780415770347,9781848855519,9781848729308,9781848853249,9780470658512,9781849660679,9780415381420,9780199734283,9780434019410,9781576471487,9781442610903,9781442611085,9781442641426,9780844743585,9780415612999,9780415613026,9781595586391,9780415587396,9781442641884,9781441108470,9780415669764,9780415669870,9780415670821,9780415454353,9780415481113,9780415465878,9781848853720,9780465003259,9781441104984,9780521889599,9780415674140,9780415674195,9780195176667,9780415771924,9780143118350,9781849201001,9781848856042,9781441184016,9781906540937,9781848857353,9781848858183,9781848858855,9781848607163,9781606350942,9781847885647"
				+ ",9781580461948,9781403969804,9781597972024,9780313359576,9780415616065,9781848858084,9781853997327,9780415782890,9780415885614,9780415894388,9780231136167,9780300100891,9780300108231,9780415449229,9781845192945,9780802038623,9780802094278,9781441191410,9780745329628,9780415780452,9781439821510,9781557134073,9781559363792,9781439871430,9780415675727,9780415894906,9780415894944,9780826111074,9781558499928,9780465008926,9781933382401,9780746311882,9780140447743,9780745645711,9780415485876,9780415494472,9781848854802,9780521119627,9780521192392,9780415895255,9780415676205,9780691096537,9780224062626,9781405117104,9780871549334,9781576471906,9780745644585,9781444339314,9781439858028,9781439861691";

		// browserow.p
		String gnmdeUrl = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program="
				+ "ipage/browserow.p&IDList=IN&user_id=clewis&cust-group=11100" + "&EANList=" + eanList + "&oidOrderList=" + BrowseRowUserInfoSrvc.fetchOidOrderList(Arrays.asList(eanList.split(",")))
				+ "&eBookDetailList=" + BrowseRowUserInfoSrvc.fetchEBookDetailsList(Arrays.asList(eanList.split(",")));

		// checkstatus.p
		String gnmdeCheckStatTabUrl = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program="
				+ "ipage/checkstatus.p&mode=tab&cust-group=11100&ean=9781848729483,9780500093559";

		ganimedeTester(gnmdeUrl);

	}

	public static void ganimedeTester(String gnmdeUrl) {

		GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();
		GanimedeResponse ganimedeResponse = null;
		try {
			gnmdeUrl = gnmdeUrl.replaceAll(" ", "%20");
			ganimedeResponse = ganimedeWorker.getGanimedeResponse(gnmdeUrl);
			List<Map<String, String>> browseRowResp = ganimedeResponse.getDatalist();

			System.out.println("url: " + gnmdeUrl);
			System.out.println("Print Data: ");
			for (Map<String, String> data : browseRowResp)
				System.out.println(data);

			System.out.println("Stop");

		} catch (Exception ex) {

		}
	}

	private static void testOrderInfoGnmd() {

		String orderInfoSummary = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program="
				+ "ipage/ip-fields&cust-group=11100&user_id=martin&mode=SUMMARY";

		String orderInfoValues = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program="
				+ "ipage/ip-fields&cust-group=11100&user_id=martin&mode=VALUES";

		String orderInfoDep = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program="
				+ "ipage/ip-fields&cust-group=11100&user_id=martin&mode=DEPENDENCIES";

		ganimedeTester(orderInfoSummary);
		ganimedeTester(orderInfoValues);
		ganimedeTester(orderInfoDep);
	}

	private static void testMgmtReps() {

		// String repUrl =
		// "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program="
		// +
		// "ipage/programs/otunnel&statement=FIND arlg WHERE arlg.company = 'CO' AND arlg.cust-group= '26000' NO-LOCK. FOR EACH arcust OF arlg NO-LOCK, EACH opappm WHERE opappm.oidCustomer = arcust.oidCustomer NO-LOCK, EACH opappmh WHERE opappmh.oidApprovalPlan = opappm.oidApprovalPlan AND opappmh.profileVersion = 0 AND LOOKUP(opappmh.selection,'B') > 0 NO-LOCK BY opappm.approvalPlan: RUN outputRow. END.&rowText=STRING(opappmh.description) + '\\t' + STRING(opappm.approvalPlan)&columnText='opappmh.description\\topappm.approvalPlan'";

		String repUrl = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre&sessionID=CouttsGanimedeRequest&program="
				+ "ipage/programs/otunnel&statement=FIND arlg WHERE arlg.company='CO' AND arlg.cust-group='11100' NO-LOCK. FOR EACH arcust OF arlg NO-LOCK, EACH opappm WHERE opappm.oidCustomer = arcust.oidCustomer NO-LOCK, EACH opappmh WHERE opappmh.oidApprovalPlan = opappm.oidApprovalPlan AND opappmh.profileVersion = 0 AND LOOKUP(opappmh.selection,'B,F,N,M') > 0 NO-LOCK BY opappm.approvalPlan: RUN outputRow. END.&rowText=STRING(opappmh.description)\\tSTRING(opappm.approvalPlan)&columnText='opappmh.description\\topappm.approvalPlan'";

		ganimedeTester(repUrl);
	}

	private static void testDbProcessObjects() {

		AcdmUserObjectImpl reqObj = new AcdmUserObjectImpl(100l, "abcd");
		reqObj.setAcdmUserId("rma8de");
		reqObj.setLibGrp("WAG320");

		ItemActionInfoObj obj = new ItemActionInfoObj("123");

		BrowseRowDBProcessObj<AcdmUserObject> dbObj = new BrowseRowDBProcessObj<AcdmUserObject>(reqObj, obj);

		System.out.println("test: " + dbObj.getUserLibGrp() + ", " + dbObj.getUserId());
		System.out.println("test: " + dbObj.getItem());
	}

	private static void writeData() {
		try {
			FileWriter writer = new FileWriter(new File("C:/ingram/Tasks/chkjson.json"));

			BufferedWriter bufWriter = new BufferedWriter(writer);
			bufWriter.write(strBldr.toString());
			bufWriter.close();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	private static void testMapDelete() {
		ConcurrentMap<String, List<List<String>>> respObjMap = new ConcurrentHashMap<String, List<List<String>>>();
		List<List<String>> respObjValues;

		String str = "1,2,3,4,5,6";
		String str1 = "7,8,9,10,11";
		String str3 = "a,b,c,d,e";
		String str4 = "f,g,h,j,i";

		respObjValues = new LinkedList<List<String>>();
		respObjValues.add(Arrays.asList(str.split(",")));
		respObjMap.put("k1", (List<List<String>>) respObjValues);

		respObjValues = new LinkedList<List<String>>();
		respObjValues.add(Arrays.asList(str1.split(",")));
		respObjMap.put("k2", (List<List<String>>) respObjValues);

		if (respObjMap.containsKey("k1")) {
			respObjMap.get("k1").add(Arrays.asList(str3.split(",")));
		}

		if (respObjMap.containsKey("k2")) {
			respObjMap.get("k2").add(Arrays.asList(str4.split(",")));
		}

		System.out.println("Before removal");
		System.out.println(respObjMap + ", " + respObjMap.size() + ", " + Collections.frequency(respObjMap.values(), String.class));
		// for(int ctr = respObjMap.size();ctr > 0; respObjMap.)

		Iterator<String> objItr = respObjMap.keySet().iterator();

		while (objItr.hasNext()) {
			String curId = objItr.next();
			if (curId.equalsIgnoreCase("k1")) {
				// respObjValues = new LinkedList<List<String>>(respObjMap.get("k1"));
				// respObjMap.get("k1").remove(1);
				respObjMap.get("k1").clear();
			}
		}

		System.out.println("saved: " + respObjValues);

		System.out.println("after removal");

		System.out.println(respObjMap);

	}

	private static void testListItemFetch() {
		String custGroup = "WAG320";
		String userId = "rma8de";
		String listTypeId = "IN";

		AcademicListService service;
		Long listId = null;
		AcademicList academicList = null;
		Map<String, Long> academicListItem = new HashMap<String, Long>();
		List<AcademicListItem> academicItemList = null;
		List<String> searchEans = new LinkedList<String>();
		List<String> searchEansOrderDetails = new LinkedList<String>();

		service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);

		academicList = service.findAcademicListByCustGroup(userId, listTypeId, custGroup);
		if (null != academicList) {
			academicItemList = service.getAllBylistId(academicList.getListId());
			academicListItem = new HashMap<String, Long>(academicItemList.size());
			int recCursor = 1;
			for (AcademicListItem item : academicItemList) {
				if (null != item.getEan() && recCursor <= 1024) {
					searchEans.add(item.getEan());
					searchEansOrderDetails.add((null == item.getSalesOrderdetl() || item.getSalesOrderdetl().isEmpty() || "null".equalsIgnoreCase(item.getSalesOrderdetl())) ? "" : item
							.getSalesOrderdetl());
					// searchEansOrderDetails.add(""); //to test empty list of sales order details
					academicListItem.put(item.getEan(), item.getOprId());
					recCursor++;
				}

			}

			// to test 1 ean in the last url
			searchEans.add("9781853997327");
			searchEansOrderDetails.add("");

			listId = academicList.getListId();
			System.out.println("listId: " + listId);
			System.out.println("searchTerm = " + StringUtils.join(searchEans, ','));
			System.out.println("salesOrderDetails = " + StringUtils.join(searchEansOrderDetails, ','));

			// searchEans = populateRequestQ(searchEans);
			// searchEansOrderDetails = populateRequestQ(searchEansOrderDetails);

			// System.out.println("searchTerm = " + searchEans.size() + " - " + searchEans.toString());
			// System.out.println("salesOrderDetails = " + searchEansOrderDetails.size() + " - " +
			// searchEansOrderDetails.toString());

			HttpCallUrl gnmdUrl = new HttpCallUrl("BROWSE_ROW_CHECK_STATUS");
			gnmdUrl.setString(2, "kboudre");
			gnmdUrl.setString(3, listTypeId);
			gnmdUrl.setString(4, userId);
			gnmdUrl.setString(5, custGroup);

			if (searchEans.size() > ean_no_limit) {
				Map<String, browseRowGnmdParams> compMap = populateRequestQ(searchEans, searchEansOrderDetails);
				// searchEans = populateRequestQ(searchEans);
				// searchEansOrderDetails = populateRequestQ(searchEansOrderDetails);
				if (null != compMap) {
					for (String eans : compMap.keySet()) {
						gnmdUrl.setString(6, eans);
						gnmdUrl.setString(7, compMap.get(eans).getSalesOrderDetails());
						gnmdUrl.setString(8, compMap.get(eans).geteBookDetailList());
						System.out.println("url: " + gnmdUrl.getGnmdUrl());

					}
				}

			} else {
				String eans = StringUtils.join(searchEans, ',');
				searchEans.set(0, eans);
				gnmdUrl.setString(6, eans);
				gnmdUrl.setString(7, StringUtils.join(searchEansOrderDetails, ','));
				gnmdUrl.setString(8, BrowseRowUserInfoSrvc.fetchEBookDetailsList(searchEans));

				System.out.println("single url: " + gnmdUrl.getGnmdUrl());

			}
		}

	}

	private static List<String> populateRequestQ(List<String> srcList) {

		// break the ean list to be passed for ganimede calls into chunks of <find from the config file>
		// roughly 143 eans makes 2kb size. Different browsers puts limit ranging from 2kb to 8kb on the client side
		// servers has a limit on get request as well
		// so it is safe to be within 2kb limit

		List<String> brokenLists = new LinkedList<String>();
		int extra = srcList.size() % ean_no_limit;
		int loops = (extra > 0) ? (srcList.size() / ean_no_limit) + 1 : (srcList.size() / ean_no_limit);

		int ctr = 0;
		int fromIndex = 0;
		int toIndex = 0;

		for (; ctr < loops; ctr++) {
			fromIndex = (ctr * ean_no_limit);
			toIndex = ((fromIndex + ean_no_limit) < srcList.size()) ? (fromIndex + ean_no_limit) : (srcList.size());

			brokenLists.add(StringUtils.join(srcList.subList(fromIndex, toIndex), ','));

		}

		return brokenLists;

	}

	private static Map<String, browseRowGnmdParams> populateRequestQ(List<String> srcList, List<String> srcList1) {

		// break the ean list to be passed for ganimede calls into chunks of <find from the config file>
		// roughly 143 eans makes 2kb size. Different browsers puts limit ranging from 2kb to 8kb on the client side
		// servers has a limit on get request as well
		// so it is safe to be within 2kb limit

		Map<String, browseRowGnmdParams> compMap = new LinkedHashMap<String, browseRowGnmdParams>();

		int extra = srcList.size() % ean_no_limit;

		int loops = (extra > 0) ? (srcList.size() / ean_no_limit) + 1 : (srcList.size() / ean_no_limit);

		int ctr = 0;
		int fromIndex = 0;
		int toIndex = 0;

		for (; ctr < loops; ctr++) {
			fromIndex = (ctr * ean_no_limit);
			toIndex = ((fromIndex + ean_no_limit) < srcList.size()) ? (fromIndex + ean_no_limit) : (srcList.size());

			compMap.put(StringUtils.join(srcList.subList(fromIndex, toIndex), ','),
					new browseRowGnmdParams(StringUtils.join(srcList1.subList(fromIndex, toIndex), ','), BrowseRowUserInfoSrvc.fetchEBookDetailsList(srcList.subList(fromIndex, toIndex))));

		}

		return compMap;

	}

	private static List<Map<String, String>> testGnmdCalls(String gnmdeUrl) {

		try {

			GanimedeWorker ganimedeWorker = new GanimedeWorkerImpl();

			gnmdeUrl.replaceAll(" ", "%20");
			GanimedeResponse ganimedeResponse = ganimedeWorker.getGanimedeResponse(gnmdeUrl);
			return ganimedeResponse.getDatalist();

		} catch (Exception ex) {

		}
		return null;
	}

	private static void printGnmdData() {
		String brUrl = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre" + "&sessionID=CouttsGanimedeRequest&program=ipage/browserow.p" + "&EANList=9781401237462"
				+ "&oidOrderList=&eBookDetailList=:&IDList=IN&cust-group=WAG320&user_id=rma8de";

		String brTabUrl = "http://gnmalpha.ingramcontent.com/gnm_cgi/gnmtest/runner?sandbox=kboudre"
				+ "&sessionID=CouttsGanimedeRequest&program=ipage/checkstatus.p&mode=tab&cust-group=WAG320&ean=9781282292062";

		List<Map<String, String>> checkStatusResp = testGnmdCalls(brUrl);
		for (Map<String, String> obj : checkStatusResp) {
			System.out.println("keys: " + obj.keySet());
			System.out.println(obj.toString());
		}
	}

	final static class browseRowGnmdParams {
		private String salesOrderDetails;
		private String eBookDetailList;

		public browseRowGnmdParams(String salesOrderDetails, String eBookDetailList) {
			super();
			this.salesOrderDetails = salesOrderDetails;
			this.eBookDetailList = eBookDetailList;
		}

		public String getSalesOrderDetails() {
			return salesOrderDetails;
		}

		public String geteBookDetailList() {
			return eBookDetailList;
		}

	}

	private static void testListItemFetchOnly() {
		String custGroup = "NOVA";
		String userId = "fNegron";
		String listTypeId = "IN";

		AcademicListService service;
		Long listId = null;
		AcademicList academicList = null;
		Map<String, List<Long>> academicListItem;
		List<AcademicListItem> academicItemList = null;
		List<String> searchEans = new LinkedList<String>();
		List<String> searchEansOrderDetails = new LinkedList<String>();

		service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);

		academicList = service.findAcademicListByCustGroup(userId, listTypeId, custGroup);
		if (null != academicList) {
			academicItemList = service.getAllBylistId(academicList.getListId());
			academicListItem = new LinkedHashMap<String, List<Long>>(academicItemList.size());

			int recCursor = 1;
			for (AcademicListItem item : academicItemList) {
				if (null != item.getEan() && recCursor <= 1024) {
					searchEans.add(item.getEan());
					searchEansOrderDetails.add((null == item.getSalesOrderdetl() || item.getSalesOrderdetl().isEmpty() || "null".equalsIgnoreCase(item.getSalesOrderdetl())) ? "" : item
							.getSalesOrderdetl());

					if (academicListItem.containsKey(item.getEan())) {
						academicListItem.get(item.getEan()).add(item.getOprId());
					} else {
						List<Long> oprs = new LinkedList<Long>();
						oprs.add(item.getOprId());
						academicListItem.put(item.getEan(), oprs);
					}
					recCursor++;
				}

			}

			listId = academicList.getListId();
			System.out.println("listId: " + listId);
			// Collections.sort(searchEans);
			for (String ean : searchEans) {
				System.out.printf("Ean:%s | Oprs:%s\n", ean, academicListItem.get(ean));
			}

			System.out.println("*********************************************");
			System.out.printf("after sorting - searchTerm (%d):  %s\n", searchEans.size(), searchEans.toString());
			System.out.printf("academicListItem - keyset - map (%d):  %s\n", academicListItem.keySet().size(), academicListItem.keySet().toString());
			System.out.printf("academicListItem - valueset - map (%d):  %s\n", academicListItem.values().size(), academicListItem.values().toString());

			// System.out.println("salesOrderDetails = " + searchEansOrderDetails.toString());

		}

	}

	private void testCollection() {

	}

	public static void main(String[] args) {

		// ganimedeTester();

		// testGnmdComps();

		// System.out.println("BASE_URL: " + AcademicServiceLocator.getApplEnv().getProperty("BASE_URL"));
		// System.out.println("Test: " + AcademicServiceLocator.getApplEnv().getProperty("BASE_URL1"));

		// test currency
		// System.out.println("usd with symbol: " + Currency.USD.localeFormatCurValWithSymbol(Locale.US, 45678.98f));
		// System.out.println("usd with code: " + Currency.USD.localeFormatCurValWithCode(Locale.US, 45678.98f));

		// System.out.println("usd with symbol: " + Currency.USD.localeFormatCurValWithSymbol(Locale.CANADA,
		// 45678.98f));
		// System.out.println("usd with code: " + Currency.USD.localeFormatCurValWithCode(Locale.CANADA, 45678.98f));

		// checkStatus();

		// String listTypeId = AcademicListType.SEARCH_RESULTS.getListTypeId(); // default list to be displayed i.e
		// search results

		// System.out.println("listTypeId: " + listTypeId);
		// System.out.println(AcademicListType.getListTypeNameById("IN"));
		// if (null != request.getParameter("listTypeId") && !request.getParameter("listTypeId").isEmpty())
		// listTypeId = request.getParameter("listTypeId");

		// testIterator();

		// checkStatusDb();

		// checkTabStatusDb();

		// printProd();

		// populateRequestQ();

		/*
		 * String eanList = "9781401237462,9781299187764,9783836549042,9781282292062"; String user = "martin";
		 * 
		 * System.out.println(BrowseRowUserInfoSrvc.fetchOidOrderList(eanList, user));
		 * System.out.println(BrowseRowUserInfoSrvc.fetchOidOrderList(eanList, user));
		 * 
		 * System.out.println(BrowseRowUserInfoSrvc.fetchEBookDetailsList(eanList, user));
		 * System.out.println(BrowseRowUserInfoSrvc.fetchEBookDetailsList(eanList, user));
		 */

		/*
		 * String eans = "9781848729483,9780500093559,9781439817087,9780393073775,9780224080699,9780415770347";
		 * List<String> eanList = Arrays.asList(eans.split(","));
		 * 
		 * StringBuilder sb = new StringBuilder(); StringBuilder sb1 = new StringBuilder(); for (String ean : eanList) {
		 * 
		 * sb1.append(":,"); }
		 * 
		 * sb1.deleteCharAt(sb1.length() - 1);
		 * 
		 * while ((sb.append(",")).length() < eanList.size() - 1);
		 * 
		 * System.out.println(eanList.toString()); System.out.println(sb.toString());
		 * System.out.println(sb1.toString());
		 */

		// testOrderInfoGnmd();
		// testMgmtReps();
		// testMapDelete();

		// testDbProcessObjects();

		// testListItemFetch();

		// printGnmdData();

		// testListItemFetchOnly();
		// loadSchema();
		// randomTests();
		// testFilter();
		/*
		 * testSearchMode("Keyword_Title", "9780231530330", true); testSearchMode("Keyword_Title", "red-dragon", true);
		 * testSearchMode("Title", "red", true); testSearchMode("Phrase_Title", "red", true);
		 * testSearchMode("Exact_Title", "red", true); testSearchMode("Author", "red", true); testSearchMode("ISBN",
		 * "9780231530330 - 9780226923123", true); testSearchMode("Keyword", "red", true); testSearchMode("Series",
		 * "red", true); testSearchMode("Dewey", "red", true); testSearchMode("OCLC_NUM_ID", "red", true);
		 * testSearchMode("LC_Call_Number", "red", true); testSearchMode("Author4_Title4", "red", true);
		 * testSearchMode("Keyword_w_TOC", "red", true);
		 */

		/*
		 * List<String> list1 = new LinkedList<String>(); list1.add("123"); list1.add("456"); list1.add("789,sk,bk");
		 * list1.add("gugu"); SearchParamsList<String> list = new SearchParamsList<String>(); list.add("abc");
		 * list.add("efg"); list.add("ijk,lmn,opq"); list.addAll(list1); list.addAll("today,tommorow,yesterday");
		 * 
		 * System.out.println("all vals as string: " + list.getParams()); for(String val: list.getParamsList())
		 * System.out.println("value: " + val);
		 */

		// SearchInfoObject searchInfoObj = SelectionListCacheManager.initilizeCache("shiv");
		// searchInfoObj.addSearchFilter("red");

		// System.out.println("all vals as string: " + searchInfoObj.getAllSearchFilter());

		/*
		 * 
		 * String cis_dt = "02/12/13"; String db_dt = "2014-05-16 09:17:42.391";
		 * 
		 * SimpleDateFormat cis_df = new SimpleDateFormat("MM/dd/yy"); SimpleDateFormat db_df = new
		 * SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
		 * 
		 * Date dt = null;
		 * 
		 * try { dt = db_df.parse(cis_dt); } catch (ParseException e1) {
		 * 
		 * try { dt = cis_df.parse(cis_dt); } catch (ParseException e) { dt = null; e.printStackTrace(); } }
		 * 
		 * if (null != dt) { System.out.println("output date: " + df.format(dt)); }
		 */
		// System.out.println("Field list: " + SolrSearchFieldsService.getAcdmList());
		// System.out.println("Field String: " + SolrSearchFieldsService.getAcdmFields());

		// System.out.println(fixPossiblyAppendedParameter("SR, IN") + ", " + fixPossiblyAppendedParameter("INGM") +
		// ", " + fixPossiblyAppendedParameter("Book") + ", "
		// + fixPossiblyAppendedParameter("Keyword_Title, ISBN_SI") + ", " + fixPossiblyAppendedParameter("TTL"));

		// String[] str = SearchConstants.COMMA_PATTERN.split("abc");
		// for(String s: str){
		// System.out.println(s);
		// }

		// String queryUrl =
		// "listTypeId=IN&listTypeId=IN&simpleSearchType=ISBN&sortSrcType=1&Ntk=ISBN&sortOrder=EAN&sortSelected=true&N=0&listTypeId=IN&No=25&Ntt=acdm_list_disp&productType=Book&Nty=1&Ntx=mode+matchany&dsplSearchTerm=Inbox&sortOrder=ITEM_CST_RTL_AMT&sortSrcType=1&sortSelected=true";

		// UrlGen urlg = new UrlGen(queryUrl, "UTF-8");
		// String s1 = urlg.getParam("sortOrder");
		// String s2 = urlg.getParam("sortSelected");
		// String s3 = urlg.getParam("sortSrcType");

		// urlg.removeParam("sortOrder");
		// urlg.removeParam("sortSelected");
		// urlg.removeParam("sortSrcType");
		// urlg.removeParam("listTypeId");

		// urlg.addParam("sortOrder", s1);
		// urlg.addParam("sortSelected", s2);
		// urlg.addParam("sortSrcType", s3);

		// System.out.println("sortOrder: " + urlg.getParam("sortOrder"));
		// System.out.println("sortSelected: " + urlg.getParam("sortSelected"));
		// System.out.println("sortSrcType: " + urlg.getParam("sortSrcType"));
		// System.out.println("listTypeId: " + urlg.getParam("listTypeId"));

		// String queryURL = StringUtility.replaceAllSubstrings(urlg.toString().trim(), "%25CE%25BE", "%CE%BE");
		// System.out.println(queryURL);

		String filter = "Advance";
		// String Ntt = "*acdm_list_disp*|Advances|EFGH";
		String Ntt = "EFGH";
		// String str[] = SearchConstants.acdmListPattern.split(filter);
		// if (null != str) {
		// if (str.length > 1) {
		// System.out.println(str[0] + ", " + str[1]);
		// } else {
		// System.out.println(str[0]);
		// }
		// }

		System.out.println(SearchConstants.acdmListPattern.matcher(Ntt).find());

		if (SearchConstants.acdmListPattern.matcher(Ntt).find()) {
			Ntt = SearchConstants.acdmListPattern.matcher(Ntt).replaceFirst(filter);
		} else {
			Ntt = filter + "|" + Ntt;
		}

		System.out.println("Ntt: " + Ntt);

		// String author = organizeAuthorTerms("Kiran bedi");
		// String author = organizeAuthorTerms("kiran*+bedi*");
		// String author = organizeAuthorTerms("kiran* bedi*");
		String author = organizeAuthorTerms("Thomas, Graham Stuart");
		author = organizeAuthorTerms("Thomas%2C+Graham+Stuart");

		try {

			author = URLDecoder.decode(author, "UTF-8");
			System.out.println("decode author: " + author);
			// System.out.println("encode author: " +
			// URLEncoder.encode(SearchUtility.disallowHighResourceKeywords(author), "UTF-8"));

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("author: " + SearchUtility.disallowHighResourceKeywords(author));

		
		
		testNonSolrFilter();

	}

	private static void testNonSolrFilter() {
		

		//String afilter = "Contains$x1f$(Author:* AND Editor:*)$x1f$1$x1f$abcd efgh&#x1eExcludes$x1f$(Binding:*)"
		//		+ "$x1f$1$x1f$Hardcover&#x1eEquals$x1f$(CIS_EBP_CURRENCY:*)"
		//		+ "$x1f$1$x1f$USD&#x1eStarts With$x1f$(CIS_FORMAT:*)"
		//		+ "$x1f$1$x1f$Hard&#x1eEnds With$x1f$(IMPT_NM_SEARCH:* OR Publisher:*)"
		//		+ "$x1f$1$x1f$McGraw Hill&#x1eGreater Than"
		//		+ "$x1f$(ITEM_OHND:Y AND (ITEM_OHND_QTY_CI:[* TO *] OR ITEM_OHND_QTY_DD:[* TO *] "
		//		+ "OR ITEM_OHND_QTY_EE:[* TO *] OR ITEM_OHND_QTY_NV:[* TO *]))$x1f$1$x1f$10&#x1eLess Than"
		//		+ "$x1f$(ITEM_OHND:Y AND (ITEM_OHND_QTY_CI:[* TO *] OR ITEM_OHND_QTY_DD:[* TO *] OR "
		//		+ "ITEM_OHND_QTY_EE:[* TO *] OR ITEM_OHND_QTY_NV:[* TO *]))$x1f$1$x1f$50";
		
		String afilter = "Author/Editor Affiliationfilter_data_sepContainsfilter_data_sepKiranfilter_rec_sep"
				+ "Availability/Stock Statusfilter_data_sepGreater Thanfilter_data_sep5filter_rec_sep"
				+ "Binding Formatfilter_data_sepExcludesfilter_data_sepHardcoverfilter_rec_sepBook Typefilter_data_sep"
				+ "Equalsfilter_data_sepBiblefilter_rec_sepCopyright Yearfilter_data_sep"
				+ "Starts Withfilter_data_sep20filter_rec_sepCountry of Publicationfilter_data_sep"
				+ "Ends Withfilter_data_sepca";
		
		
		//String afilter = "Availability/Stock Statusfilter_data_sepGreater Thanfilter_data_sep5";

		AcdmFilterHelper hlpr = new AcdmFilterHelper(afilter);
		Map<String, List<String>> fQryMap = hlpr.filterQueries();
		System.out.println("solr filters: " + fQryMap.get("solr"));
		System.out.println("non solr filters: " + fQryMap.get("nsolr"));
		
		afilter = "Binding Formatfilter_data_sepEqualsfilter_data_sepHardcover";
		
		hlpr = new AcdmFilterHelper(afilter);
		fQryMap = hlpr.filterQueries();
		System.out.println("solr filters: " + fQryMap.get("solr"));
		System.out.println("non solr filters: " + fQryMap.get("nsolr"));

	}
	
	private static void metrics(){
		float weight = 0.69f;
		// float weightMetrics = (float) ( Math.round(( (weight * 0.453592) * 100)) / 100);
		double weightMetrics = (double) (Math.round(((weight * 0.453592) * 100))) / 100;

		System.out.printf("weight:weightMetrics => %f:%2f ", weight, weightMetrics);
	}

	private static String fixPossiblyAppendedParameter(String parameterValue) {
		if (parameterValue == null) {
			return null;
		}
		int separatorIndex = parameterValue.indexOf(", ");
		if (separatorIndex == 0) {
			return "";
		} else if (separatorIndex > 0) {

			return parameterValue.substring(0, separatorIndex);
		} else {
			return parameterValue;
		}
	}

	private static void randomTests() {
		System.out.println("A == a is " + ("A" == "A"));

		Map<String, String> keyVal = new LinkedHashMap<String, String>();
		keyVal.put("9781848729483", "ean1");
		keyVal.put("9780500093559", "ean2");
		keyVal.put("9781439817087", "ean3");
		keyVal.put("9780393073775", "ean4");
		keyVal.put("9780224080699", "ean4");
		keyVal.put("9780415770347", "ean4");

		List<String> eanList = new LinkedList<String>(keyVal.keySet());
		System.out.println(StringUtils.join(eanList.subList(0, 2), ",") + "%2B" + StringUtils.join(eanList.subList(2, 4), "%2B") + "," + StringUtils.join(eanList.subList(4, 6), "%2B"));

		String name = "shiv kumar jha";
		System.out.println(StringUtils.capitalize(name));
		System.out.println(WordUtils.capitalize(name));

		System.out.println(SearchUtility.disallowHighResourceKeywords("red%20blue@9green"));

		System.out.println("TitleStartOf: " + Arrays.asList(SearchUtility.getTitleStartOfEndecaParams("india")).toString());
		System.out.println("TitlePhrase: " + Arrays.asList(SearchUtility.getTitlePhraseEndecaParams("taking bull by horn")).toString());
		System.out.println("TitleExact: " + Arrays.asList(SearchUtility.getTitleExactEndecaParams("heroes of mahabharata")).toString());
	}

	private static void testQFilters() {
		Pattern pipePat = Pattern.compile("\\|");
		Pattern rangeFilterPattern = Pattern.compile("(?i)^(.*)[\\|](lt|gt|lteq|gteq|btwn)\\s?[\\+|\\s]([a-z0-9.]+)(?:\\s?[\\+|\\s]([a-z0-9.]+))?(?:\\|)?$");

		String filterQuery = "TOT_DSRB_CNT|GTEQ 564";
		// String filterQuery = "TOT_DSRB_CNT%7CGTEQ+564";
		String[] filters = pipePat.split(filterQuery);
		List<String> rfl = new LinkedList<String>();
		System.out.println(filters.length);
		for (int i = 0; i < filters.length; i = i + 2) {
			String filter = null;
			if (filters.length > i + 1) {
				filter = filters[i] + "|" + filters[i + 1];
			} else {
				filter = filters[i];
			}
			rfl.add(filter);
		}
		System.out.println(rfl.toString());

		for (String filter : rfl) {

			System.out.println(filter);
			Matcher m = rangeFilterPattern.matcher(filter);
			boolean valid = true;
			String from = "*";
			String to = "*";
			String operator = null;
			String operand = null;
			String field = null;

			System.out.println(m.find() + " - " + m.groupCount());

			if (m.find() && m.groupCount() >= 3) {
				field = m.group(1);
				operator = m.group(2).toLowerCase();
				operand = m.group(3);
			}
			System.out.println(operator + "," + operand + "," + field);
		}
		// setRangeFilters(rfl);
	}

	private static void testFilter() {
		try {

			// RecordFilterCriteria criteria = SearchServletUtility.getRecordFilterCriteria(request);

			RecordFilterCriteria criteria = new RecordFilterCriteria();

			// sample criteria
			ibg.product.information.service.RecordFilterService.ProductType[] rfsProdType = new ibg.product.information.service.RecordFilterService.ProductType[1];
			rfsProdType[0] = RecordFilterService.ProductType.BOOK;
			criteria.setProductTypes(rfsProdType);
			criteria.setProductLimit(RecordFilterService.ProductLimit.INGRAM);
			criteria.setMarketSegment("PUBL");
			criteria.setHasBookAccess(true);
			criteria.setHasDigitalAccess(true);
			criteria.setHasGiftAccess(true);
			criteria.setHasMusicAccess(true);
			criteria.setHasVideoAccess(true);
			criteria.setHasVideoGameAccess(true);
			criteria.setHasPeriodicalAccess(false);
			criteria.setHasIPSAccess(true);
			criteria.setHasPubSourceAccess(false);
			criteria.setLibraryOrSchool(false);
			criteria.setHasIKIDSAccess(true);
			criteria.setHasRentalOnlyAccess(false);
			criteria.setHasRetailOnlyAccess(true);

			RecordFilterService recordFilterGen = new RecordFilterServiceAcdmImpl();
			String recordFilter = recordFilterGen.generateRecordFilterSearchParameter(criteria);

			System.out.println("productCriteriaStr: " + recordFilter);

			// String recordFilter =
			// "AND(15000647,NOT(RENTAL_ONLY:Y),NOT(ILS_ONLY_IND:Y),OR(15000665,15000664,PUB_SRC:Y))";
			recordFilter = URLDecoder.decode(recordFilter, "UTF-8");
			System.out.println("productCriteriaStr: " + recordFilter);

			// RecordFilterExpressionParser parser = new RecordFilterExpressionParser();
			// String solrRecordFilter = parser.decode(parser.parse(recordFilter));
			// System.out.println("solrRecordFilter: " + solrRecordFilter);
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("UTF-8 ought to be supported.");
		}
	}

	// Ntx - searchMode
	private static void testSearchMode(String searchBy, String searchTerm, boolean searchOptionsOn) {

		String N = "0"; // ENEQuery.DimValIdList navDescriptors
		String Ntk = null; // ENEQuery.String searchFields
		String Ntt = null; // ENEQuery.String searchTerms
		String Nty = "1";// ENEQuery.boolean didYouMean
		String Ntx = "mode matchall"; // ENEQuery.String searchModes
		String productType = null;
		String dsplSearchTerm = null;
		String productLimit = null;

		if (!searchOptionsOn) {
			searchBy = "Keyword";
			productType = "All";
			productLimit = "INGM";
		}

		// If First search term is ISBN or UPC then set search by = ISBN.
		if (!"ISBN".equals(searchBy)) {
			String temp = StringUtility.removeChars(searchTerm, "-");
			int delimiter = temp.indexOf(' ');
			if (delimiter > -1) // a space is present
			{
				int delimiter2 = temp.indexOf(',');
				if (delimiter2 > -1) // delimiter = first space or comma
					delimiter = Math.min(delimiter, delimiter2);
			} else
				delimiter = temp.indexOf(',');
			String temp2 = null;
			if (delimiter < 0) // single ISBN or UPC entered
				temp2 = temp;
			else
				temp2 = temp.substring(0, delimiter);

			if (ProductInformation.identifyEntry(temp2) > 0) {
				searchTerm = temp;
				searchBy = "ISBN";
				productType = "All";
			}
		} else
			searchTerm = StringUtility.removeChars(searchTerm, "-");

		dsplSearchTerm = searchTerm;

		// SET SEARCH BY
		if ("Keyword_Title".equals(searchBy)) {
			Ntk = "Title";
		} else if ("Title".equals(searchBy)) {
			String[] startOfCriteria = SearchUtility.getTitleStartOfEndecaParams(searchTerm);
			searchTerm = startOfCriteria[0];
			Ntx = startOfCriteria[1];
			Ntk = startOfCriteria[2];
		} else if ("Phrase_Title".equals(searchBy)) {
			String[] startOfCriteria = SearchUtility.getTitlePhraseEndecaParams(searchTerm);
			searchTerm = startOfCriteria[0];
			Ntx = startOfCriteria[1];
			Ntk = startOfCriteria[2];
		} else if ("Exact_Title".equals(searchBy)) {
			String[] exactTitle = SearchUtility.getTitleExactEndecaParams(searchTerm);
			searchTerm = exactTitle[0];
			Ntx = exactTitle[1];
			Ntk = exactTitle[2];
		} else if ("Author".equals(searchBy) || "Director".equals(searchBy)) {
			Ntk = "Contrib";
			if ("Director".equals(searchBy))
				Ntk = "Director";
			searchTerm = organizeAuthorTerms(searchTerm);
		} else if ("ISBN".equals(searchBy)) {
			Ntk = "ISBN";
			productType = "All";

			searchTerm = searchTerm.replace(',', ' ');

			if (searchTerm.indexOf(".") != -1)
				searchTerm = StringUtility.removeChars(searchTerm, "."); // Remove "." from multiple sku_vend_id
			// terms.
			// If 1 term is entered then search with wild card.
			if (searchTerm.indexOf(' ') < 0 && !searchTerm.endsWith("*") && searchTerm.length() >= 6)
				searchTerm = searchTerm + "*";
			else {
				Ntx = "mode matchany";
				StringBuilder isbnTerms = new StringBuilder();
				String[] terms = SearchConstants.spacePattern.split(searchTerm);

				// If the user enters more than 20 terms, make sure the 21st+ term
				// is a likely isbn, ean or upc (and not a bunch of garbage).
				// This is a performance safeguard.
				int i = 0;
				boolean appendASpace = false;
				for (String aTerm : terms) {
					if (appendASpace) {
						isbnTerms.append(" ");
					}
					if (i < 20 || ((aTerm.length() == 10 || aTerm.length() == 12 || aTerm.length() == 13 || aTerm.length() == 17) && !SearchConstants.nonDigitPattern.matcher(aTerm).find())) {
						isbnTerms.append(aTerm);
						appendASpace = true;
					} else {
						appendASpace = false;
					}
					i++;
				}
				searchTerm = isbnTerms.toString();
			}
		} else if ("Keyword".equals(searchBy)) {
			Ntk = "Keyword";
		} else if ("Series".equals(searchBy)) {
			Ntk = "Series";
		} else if ("OCLC_NUM_ID".equals(searchBy)) {
			Ntk = "OCLC_NUM_ID";
		} else if ("Keyword_Song".equals(searchBy)) {
			Ntk = "SongList";
		} else if ("Song".equals(searchBy)) {
			Ntk = "SongList";
			searchTerm = "\"" + searchTerm + "\"";
		} else if ("Dewey".equals(searchBy)) {
			Ntk = "SJCG_DEWY_DEC_NBR";
			// replace "," with space and search with wild card if 1 term before Endeca functionality. "-" could be
			// in the middle of Dewey. don't remove "-"
			if (searchTerm.indexOf(",") != -1)
				searchTerm = searchTerm.replace(',', ' ');

			// If only 1 term is entered, then wild card search
			if (searchTerm.indexOf(' ') < 0 && !searchTerm.endsWith("*"))
				searchTerm = searchTerm + "*";
			else
				Ntx = "mode matchany";
		} else if ("LC_Call_Number".equals(searchBy)) {
			// The LC_CL_SEARCH Endeca Property contains the Call Number with all non-alphanumeric characters
			// removed.
			Ntk = "LC_CL_SEARCH";

			// if the user entered a comma-separated list of call numbers, tokenize the input on comma (followed by
			// optional whitespace)
			String[] callNumbers = searchTerm.split(",\\s*");

			// Build the search string by all non alpha-numeric characters from each call number.
			// String together each call number, delimit them with a space.
			StringBuilder nttParam = new StringBuilder();
			for (int i = 0; i < callNumbers.length; i++) {
				if (i > 0) {
					nttParam.append(' ');
				}
				nttParam.append(SearchUtility.removeNonAlphaNumericCharacters(callNumbers[i]));
			}

			// If only one call number, add a wildcard to match partial entries.
			// If multiple call numbers, change match mode to "any". Partial call numbers are not allowed if there
			// are multiple.
			if (callNumbers.length > 1) {
				Ntx = "mode matchany";
			} else {
				Ntx = "mode matchall";
				nttParam.append("*");
			}
			searchTerm = nttParam.toString();
		} else if ("Rating".equals(searchBy)) {
			Ntk = "Rating";
			searchTerm = StringUtility.removeChars(searchTerm, "- ");
		} else if ("Author4_Title4".equals(searchBy)) {
			Ntk = "Author/Title(4,4)";
			// The user is supposed to enter a comma to separate the
			// contributor
			// and title, but a slash is allowed and converted to a comma.
			searchTerm = searchTerm.replace('/', ',');
			// The contributor name is stored with a length of four. Padding
			// with
			// spaces is necessary for names having lengths 1-3.
			int commaIndex = searchTerm.indexOf(',');
			if (commaIndex < 4 && commaIndex > 0) {
				String name = searchTerm.substring(0, commaIndex);
				searchTerm = name + "    ".substring(0, 4 - name.length()) + searchTerm.substring(commaIndex);
			}
		} else if ("Keyword_w_TOC".equals(searchBy)) {
			Ntk = "TOC";
		} else if ("BISAC_Categories".equals(searchBy)) {
			Ntk = "BISAC_Categories";
		}

		Ntt = SearchUtility.disallowHighResourceKeywords(searchTerm);

		System.out.printf("searchBy: %s, searchTerm: %s, searchOptionsOn: %s\n", searchBy, searchTerm, searchOptionsOn);
		System.out.printf("N: %s, Ntk: %s, Ntt: %s, Nty: %s, Ntx: %s, productType: %s, dsplSearchTerm: %s, productLimit: %s\n\n", N, Ntk, Ntt, Nty, Ntx, productType, dsplSearchTerm, productLimit);

	}

	private static String organizeAuthorTerms(String name) {
		StringBuffer returnBuffer = new StringBuffer();

		// remove trailing "."
		while (name.charAt(name.length() - 1) == '.')
			name = name.substring(0, name.length() - 1);
		// Identify ".", "," and replace them with space to make wild card words.
		StringTokenizer tokenizer = new StringTokenizer(name, ". ,");
		String token = null;
		while (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			if (!token.endsWith("*"))
				returnBuffer.append(token + "* ");
			else
				returnBuffer.append(token + " ");
		}
		return returnBuffer.toString();
	}

}

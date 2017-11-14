package com.common.test;


import ibg.academics.dto.AcademicList;
import ibg.academics.dto.AcademicListItem;
import ibg.academics.service.AcademicListService;
import ibg.academics.service.AcademicServiceLocator;
import ibg.academics.service.AcademicServiceLocator.ServiceName;
import ibg.lib.activity.BrowseRowGnmdUrls;
import ibg.lib.activity.GanimedeThreadExecutor;
import ibg.lib.activity.browserowp.db.objects.ItemActionInfoObj;
import ibg.lib.activity.browserowp.db.objects.JsonCheckStatusObj;
import ibg.lib.activity.browserowp.db.response.ResponseMapService;
import ibg.lib.activity.browserowp.db.response.ResponsePurgeList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestBrowseRowStatus {

	private static List<Long> listIdArr = new LinkedList<Long>();
	private static int ctr = 0;
	private static boolean kill_eligible = false;
	private static int no_poll_attmpts = 0;
	private static int max_try = 10;

	private static List<String> red_keys = Arrays.asList("Accepted,Rejected,Selected for Library,Selected by Library,On Hold For,for Review By",
			"Approval Book,Approval eBook,Awaiting Ratification,Denied,Approval Claim,Order,Holdings,Block");
	private static Map<Long, List<String>> page_eans_list = new ConcurrentHashMap<Long, List<String>>();
	private static StringBuilder strBldr = new StringBuilder();

	private static Queue<Long> listQ = new LinkedList<Long>();
	private static ClassPathXmlApplicationContext context = null;

	private static String custGroup = "WAG320";
	private static String userId = "rma8de";
	private static String listTypeId = "IN";

	private static String custGroup1 = "NOVA";
	private static String userId1 = "fNegron";
	private static String listTypeId1 = "IN";

	static {

		try {

			context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void execute() {

		List<String> page_ean_arr;
		ExecutorService executor = Executors.newFixedThreadPool(10);

		try {

			executor.execute(new TestQPopulation(custGroup, userId, listTypeId));
			executor.execute(new TestQPopulation(custGroup1, userId1, listTypeId1));

			Calendar cldr = new GregorianCalendar();
			Calendar cldr1;

			for (; no_poll_attmpts < max_try;) {

				cldr1 = new GregorianCalendar();
				if ((cldr1.getTimeInMillis() - cldr.getTimeInMillis()) >= 1000) {
					cldr = new GregorianCalendar();
					no_poll_attmpts++;
					ctr = 0;
					for (Long listIds : listIdArr) {
						page_ean_arr = page_eans_list.get(listIds);
						if (!page_ean_arr.isEmpty()) {
							//kill_eligible = true;
							new TestResponseMap(listIds).run();
						}// if
						//else {
						//	if (kill_eligible) {
						//		no_poll_attmpts = max_try;
						//	}
						//}// else
					}
				}// if

				if (no_poll_attmpts == max_try) {
					System.out.println("Starting all over again");
					no_poll_attmpts = 0;
					max_try = 10;
					executor.execute(new TestQPopulation(custGroup, userId, listTypeId));
					//System.out.println("Search complete - quitting");
					//writeData();
					//System.exit(0);
				}

			}// for

			writeData();

		} catch (Exception ex) {
			System.out.println("from execute: " + ex.toString());

		}

	}

	static class TestQPopulation implements Runnable {

		String custGroup;
		String userId;
		String listTypeId;
		Long listId;

		TestQPopulation(String custGroup, String userId, String listTypeId) {
			this.custGroup = custGroup;
			this.userId = userId;
			this.listTypeId = listTypeId;
		}

		public void run() {

			AcademicListService service;
			AcademicList academicList = null;
			List<AcademicListItem> academicItemList = null;
			List<String> searchEans = new LinkedList<String>();
			List<String> searchEansOrderDetails = new LinkedList<String>();

			service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);

			academicList = service.findAcademicListByCustGroup(userId, listTypeId, custGroup);
			if (null != academicList) {
				academicItemList = service.getAllBylistId(academicList.getListId());
				for (AcademicListItem item : academicItemList) {
					if (null != item.getEan()) {
						searchEans.add(item.getEan());
						searchEansOrderDetails.add((null == item.getSalesOrderdetl() || item.getSalesOrderdetl().isEmpty() || "null".equalsIgnoreCase(item.getSalesOrderdetl())) ? "" : item
								.getSalesOrderdetl());
						// searchEansOrderDetails.add(""); //to test empty list of sales order details
					}

				}
			}

			listId = academicList.getListId();
			synchronized (listIdArr) {
				listIdArr.add(listId);
			}

			page_eans_list.put(listId, searchEans);

			BrowseRowGnmdUrls url = new BrowseRowGnmdUrls.Builder(searchEans, listId).salesOrderDetails(searchEansOrderDetails).idList(listTypeId).userLibGrp(custGroup).acdmUserId(userId)
					.sandbox("kboudre").buildBrowseRowUrl();

			// sampling the ganimede url formation
			System.out.println(url.getBrowseRowUrl().get(0));

			System.out.println("Calling Thread Executor");

			new GanimedeThreadExecutor(10, 0, 10).start();
			System.out.println("Executor called");

		}

	}

	static class TestResponseMap implements Runnable {

		Long listId;
		List<String> page_ean_arr;

		TestResponseMap(Long listId) {
			this.listId = listId;
		}

		public void run() {

			try {

				if (!ResponseMapService.isEmpty()) {

					@SuppressWarnings("unchecked")
					List<ItemActionInfoObj> data = (List<ItemActionInfoObj>) ResponseMapService.getListData(listId);

					if (null == data || data.isEmpty() || data.size() <= 0)
						return;

					List<JsonCheckStatusObj> uiJsonData = new ArrayList<JsonCheckStatusObj>();
					ctr++;

					page_ean_arr = page_eans_list.get(listId);

					System.out.printf("Outstanding search: %d - Try no: %d -  For listId (%s):,%d\n", page_ean_arr.size(), ctr, listId, data.size());

					synchronized (page_ean_arr) {

						for (ItemActionInfoObj dataObj : data) {

							if (null != dataObj) {
								if (null != dataObj.getEAN()) {

									if (page_ean_arr.contains(dataObj.getEAN())) {
										JsonCheckStatusObj jsonObj = new JsonCheckStatusObj();
										jsonObj.setBrowseRowMessage(dataObj.getBrowseRowMessage());
										jsonObj.setCommunityGroup(dataObj.getCommunityGroup());
										jsonObj.setConsortiaShortName(dataObj.getConsortiaShortName());
										jsonObj.setEan(dataObj.getEAN());
										jsonObj.setListName(dataObj.getListName());
										jsonObj.setListOwner(dataObj.getListOwner());
										jsonObj.setListType(dataObj.getListType());
										jsonObj.setUserID(dataObj.getUserID());

										uiJsonData.add(jsonObj);
										ResponsePurgeList.putObj(listId, Integer.valueOf(dataObj.hashCode()));
										//System.out.println("to purgelist: " + listId);
										page_ean_arr.remove(dataObj.getEAN());
										System.out.println("ItemActionInfoObj: " + dataObj.toString());
									}// if
								}// if
							}// if
						}// for

						page_eans_list.put(listId, page_ean_arr);
						page_ean_arr = null;

						/*
						try {
							ObjectMapperWraper mapper = (ObjectMapperWraper) context.getBean("jsonObjMapper");
							synchronized (strBldr) {
								strBldr.append(mapper.write(uiJsonData));
							}

						} catch (JsonGenerationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (JsonMappingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						*/

					}// synchronized

				}// if

			} catch (Exception ex) {
				System.out.println("exception :" + ex);
			}
		}

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

	public static void main(String[] args) {

		execute();

	}

}

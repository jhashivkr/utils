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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestBrowseRowStatusSingleUser {

	private static Long listId = 0l;
	private static int ctr = 0;
	private static boolean kill_eligible = false;
	private static int no_poll_attmpts = 0;
	private static int max_try = 10;

	private static List<String> page_ean_arr;
	private static StringBuilder strBldr = new StringBuilder();

	private static ClassPathXmlApplicationContext context = null;

	//user_id=STONE5OC&cust-group=81200&EANList=9780415994637,9780805846546&oidOrderList=,&eBookDetailList=:,:

	private static String custGroup = "81200";
	private static String userId = "STONE5OC";
	private static String listTypeId = "SR";

	static {

		try {

			context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void execute() {

		ExecutorService executor = Executors.newFixedThreadPool(10);
		boolean tripover = true;

		try {

			Calendar cldr = new GregorianCalendar();
			Calendar cldr1;

			for (int ct = 0; ct < 3; ct++) {
				if (tripover) {

					no_poll_attmpts = 0;
					max_try = 10;
					ctr = 0;
					page_ean_arr = null;
					tripover = false;

					System.out.println("Starting all over again");
					executor.execute(new TestQPopulation(custGroup, userId, listTypeId));

					for (; no_poll_attmpts < max_try;) {
						cldr1 = new GregorianCalendar();
						if ((cldr1.getTimeInMillis() - cldr.getTimeInMillis()) >= 1000) {
							cldr = new GregorianCalendar();
							no_poll_attmpts++;
							if (!page_ean_arr.isEmpty()) {
								//kill_eligible = true;
								new TestResponseMap(listId).run();
							}// if
							else {
								//if (kill_eligible) {
									no_poll_attmpts = max_try;
								//}
							}// else
						}// if

						if (no_poll_attmpts == max_try) {
							System.out.println("Search complete - quitting");
							tripover = true;
							break;
						}

					}// for				

				}

			}
			System.exit(0);

		} catch (Exception ex) {
			System.out.println("from execute: " + ex.toString());

		}

	}

	static class TestQPopulation implements Runnable {

		String custGroup;
		String userId;
		String listTypeId;

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
			page_ean_arr = searchEans;
			//BrowseRowGnmdUrls url = new BrowseRowGnmdUrls.Builder(searchEans, listId).salesOrderDetails(searchEansOrderDetails).idList(listTypeId).userLibGrp(custGroup).acdmUserId(userId)
			//		.sandbox("kboudre").buildBrowseRowUrl();
			
			searchEans.clear();
			searchEansOrderDetails.clear();
			searchEans.add("9780805846546");
			searchEansOrderDetails.add("");
			BrowseRowGnmdUrls url = new BrowseRowGnmdUrls.Builder(searchEans, listId).salesOrderDetails(searchEansOrderDetails).idList(listTypeId).userLibGrp(custGroup).acdmUserId(userId)
							.sandbox("kboudre").buildBrowseRowUrl();
			
			// sampling the ganimede url formation
			//System.out.println(url.getBrowseRowUrl().get(0));

			//System.out.println("Calling Thread Executor");

			new GanimedeThreadExecutor(10, 0, 10).start();
			System.out.println("Executor called");

		}

	}

	static class TestResponseMap {

		Long listId;

		TestResponseMap(Long listId) {
			this.listId = listId;
		}

		public boolean run() {

			try {

				System.out.println("is empty: " + ResponseMapService.isEmpty());
				if (!ResponseMapService.isEmpty()) {

					@SuppressWarnings("unchecked")
					List<ItemActionInfoObj> data = (List<ItemActionInfoObj>) ResponseMapService.getListData(listId);

					if (null == data || data.isEmpty() || data.size() <= 0)
						return true;

					synchronized (page_ean_arr) {

						List<JsonCheckStatusObj> uiJsonData = new ArrayList<JsonCheckStatusObj>();
						ctr++;
						System.out.printf("Outstanding search: %d - Try no: %d -  For listId (%s):,%d\n", page_ean_arr.size(), ctr, listId, data.size());

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

										// uiJsonData.add(jsonObj);
										ResponsePurgeList.putObj(listId, Integer.valueOf(dataObj.hashCode()));
										page_ean_arr.remove(dataObj.getEAN());
										System.out.println("ItemActionInfoObj: " + dataObj.toString());
									}// if
								}// if
							}// if
						}// for

						/*
						 * 
						 * try { ObjectMapperWraper mapper = (ObjectMapperWraper) context.getBean("jsonMapper");
						 * synchronized (strBldr) { strBldr.append(mapper.write(uiJsonData)); }
						 * 
						 * } catch (JsonGenerationException e) { // TODO Auto-generated catch block e.printStackTrace();
						 * } catch (JsonMappingException e) { // TODO Auto-generated catch block e.printStackTrace(); }
						 * catch (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
						 */

					}// synchronized

				}// if

			} catch (Exception ex) {
				System.out.println("exception :" + ex);
			}

			return true;
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

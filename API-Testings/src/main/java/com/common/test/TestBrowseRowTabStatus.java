package com.common.test;


import ibg.academics.dto.AcademicList;
import ibg.academics.dto.AcademicListItem;
import ibg.academics.service.AcademicListService;
import ibg.academics.service.AcademicServiceLocator;
import ibg.academics.service.AcademicServiceLocator.ServiceName;
import ibg.lib.activity.EanCheckStatusGnmdUrls;
import ibg.lib.activity.browserowp.db.objects.ItemBrowseRowTabDataObj;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestBrowseRowTabStatus {

	private static Long listId = new Long(1);
	private static Queue<Long> listQ = new LinkedList<Long>();
	private static ClassPathXmlApplicationContext context = null;

	private static String custGroup = "WAG320"; // "NOVA";
	private static String userId = "rma8de"; // "fNegron";
	private static String listTypeId = "IN";

	static {

		try {

			context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void execute() {

		listQ.add(listId);

		// executor pool
		ExecutorService executor = Executors.newFixedThreadPool(2);

		try {

			executor.execute(new TestQPopulation());

		} catch (Exception ex) {
			System.out.println(ex.toString());

		}

	}

	static class TestQPopulation implements Runnable {

		public void run() {

			AcademicListService service;
			AcademicList academicList = null;
			List<AcademicListItem> academicItemList = null;
			List<String> searchEans = new LinkedList<String>();

			service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);

			academicList = service.findAcademicListByCustGroup(userId, listTypeId, custGroup);
			if (null != academicList) {
				academicItemList = service.getAllBylistId(academicList.getListId());
				listId = academicList.getListId();
				for (AcademicListItem item : academicItemList) {
					if (null != item.getEan()) {

						// make the ganimede calls to get the check status data
						EanCheckStatusGnmdUrls tabBuilder = new EanCheckStatusGnmdUrls.Builder(item.getEan(), listId).userLibGrp(custGroup).acdmUserId(userId).sandbox("kboudre").buildCheckStatusUrl();
						Set<ItemBrowseRowTabDataObj> tabCheckStatus = tabBuilder.getTabCheckStatus();

						System.out.println("*********************************************");
						// System.out.println(tabCheckStatus);
						for (ItemBrowseRowTabDataObj obj : tabCheckStatus)
							System.out.println("ItemBrowseRowTabObj: " + obj.toString());
						System.out.println("*********************************************");

						searchEans.add(item.getEan());
					}// if

				}// for
			}// if

		}

	}

	public static void main(String[] args) {

		execute();

	}

}

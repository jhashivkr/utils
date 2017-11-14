package com.lib.activity;

import ibg.academics.service.AcademicListService;
import ibg.academics.service.AcademicServiceLocator;
import ibg.academics.service.AcademicServiceLocator.ServiceName;
import ibg.lib.activity.browserowp.db.objects.BrowseRowDBProcessObj;
import ibg.lib.activity.browserowp.db.objects.ItemBrowseRowTabDataObj;
import ibg.lib.activity.browserowp.db.objects.LibraryActivityValueHolder;
import ibg.lib.activity.browserowp.db.response.ResponseMapService;

import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("rawtypes")
public final class CheckStatusDBServiceTest<E extends BrowseRowDBProcessObj> implements Runnable {

	private E dbObj = null;

	public CheckStatusDBServiceTest(E dbObj) {
		this.dbObj = dbObj;
	}

	public void run() {
		AcademicListService service = null;

		service = (AcademicListService) AcademicServiceLocator.getService(ServiceName.ACADEMIC_SERVICE);

		// TreeSet<EanDBStatus> eanStatus = null;
		Set<ItemBrowseRowTabDataObj> eanStatus = null;

		try {

			eanStatus = (TreeSet<ItemBrowseRowTabDataObj>) service.findEanBrowseRowStatus(dbObj.getReqObj().getLibGrp(), dbObj.getItem().getEAN(), dbObj.getReqObj().getAcdmUserId());

			// if priority >= 50 && priority <= 70 then -
			// check for the status in the selection records
			// apply the sorting logic and get the highest priority status
			int priority = Integer.parseInt(dbObj.getItem().getPriority());
			
			// put this object into the set to get the sorting logic applied			
			if (priority >= LibraryActivityValueHolder.chkStatdbLowRange() && priority <= LibraryActivityValueHolder.chkStatDbHighRange()) {
				ItemBrowseRowTabDataObj ibrObj = new ItemBrowseRowTabDataObj(dbObj.getReqObj().getAcdmUserId());
				
				ibrObj.setUserID(dbObj.getReqObj().getAcdmUserId());
				ibrObj.setPriority(dbObj.getItem().getPriority());
				ibrObj.setConsortiaShortName(dbObj.getItem().getConsortiaShortName());
				ibrObj.setTabMessage(dbObj.getItem().getBrowseRowMessage());
				ibrObj.setSelectionEAN(dbObj.getItem().getEAN());
				ibrObj.setTabEAN(dbObj.getItem().getEAN());

				eanStatus.add(ibrObj);
			}
				
			if (null != eanStatus) {

				ItemBrowseRowTabDataObj eanStat = ((TreeSet<ItemBrowseRowTabDataObj>) eanStatus).first();				
								
				dbObj.getItem().setPriority(eanStat.getPriority());
				dbObj.getItem().setBrowseRowMessage(eanStat.getTabMessage());
				dbObj.getItem().setUserID(eanStat.getUserID());
				dbObj.getItem().setListName(eanStat.getListName());
				dbObj.getItem().setListOwner(eanStat.getListOwner());
				dbObj.getItem().setListType(eanStat.getListType());
				dbObj.getItem().setOidSalesOrderDetail(eanStat.getOidSalesOrderDetail());
				dbObj.getItem().setCouttsDemand(eanStat.getQty());
				dbObj.getItem().setCommunityGroup("true");

				ResponseMapService.addResData(dbObj.getReqObj().getRegId(), dbObj.getItem());
				eanStatus = null;
			}

		} catch (Exception e) {

			// System.out.println("error message: " + e); // to be removed
		}

	}

}

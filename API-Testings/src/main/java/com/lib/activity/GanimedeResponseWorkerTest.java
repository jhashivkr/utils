package com.lib.activity;

import ibg.academics.dto.AcademicListType;
import ibg.ganimede.service.GanimedeResponse;
import ibg.ganimede.service.GanimedeWorker;
import ibg.ganimede.service.GanimedeWorkerImpl;
import ibg.lib.activity.browserowp.db.DBProcessQService;
import ibg.lib.activity.browserowp.db.objects.AcdmUserObject;
import ibg.lib.activity.browserowp.db.objects.BrowseRowDBProcessObj;
import ibg.lib.activity.browserowp.db.objects.ItemActionInfoObj;
import ibg.lib.activity.browserowp.db.response.ResponseMapService;
import ibg.lib.activity.browserowp.db.response.ResponseMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GanimedeResponseWorkerTest<E extends AcdmUserObject> implements Runnable {

	private List<Map<String, String>> respObj = null;
	private E reqObj = null;
	private GanimedeWorker ganimedeWorker;
	private GanimedeResponse ganimedeResponse;
	
	public GanimedeResponseWorkerTest(E reqObj) {
		this.reqObj = reqObj;
	}

	private List<Map<String, String>> getGnmdeResponse(String gnmdeUrl) {

		try {
			gnmdeUrl.replaceAll(" ", "%20");
			ganimedeResponse = ganimedeWorker.getGanimedeResponse(gnmdeUrl);
			return ganimedeResponse.getDatalist();

		} catch (Exception ex) {

		}

		return null;

	}

	public final List<Map<String, String>> getResponse() {
		return respObj;
	}

	public void run() {
		try {
			ganimedeWorker = new GanimedeWorkerImpl();
			List<Map<String, String>> browseRowResp = getGnmdeResponse(reqObj.getGnmdUrl());
			ArrayList<String> srchEanList = new ArrayList<String>(reqObj.getSearchEans());
			
			List<ItemActionInfoObj> mappedObjList = ResponseMapper.mapResponseToActionInfo(browseRowResp);
			
			// check - if the priority of the response is greater than 30 - before adding to the
			Iterator<ItemActionInfoObj> listItr = mappedObjList.iterator();
			while (listItr.hasNext()) {
				ItemActionInfoObj obj = listItr.next();

				if (null != obj) {
					
					int priority = Integer.parseInt(obj.getPriority());
				
					ItemActionInfoObj objClone = ItemActionInfoObj.newInstance(obj);
					
					// if priority is > 30 the status comes from the selection records
					if (priority == -1 || priority > 30) {
						DBProcessQService.addReq(new BrowseRowDBProcessObj<AcdmUserObject>(reqObj, objClone));
					}// if

					// if priority <= 30 then this CIS status directly goes to the view
					else {
						// add to the response list
						objClone.setUserID(reqObj.getAcdmUserId());
						objClone.setListOwner(reqObj.getAcdmUserId());						
						objClone.setListType(reqObj.getListTpId());
						objClone.setListName(AcademicListType.getListTypeNameById(reqObj.getListTpId()));
						ResponseMapService.addResData(reqObj.getRegId(), objClone);
					}
					listItr.remove();

					// find all those eans for which the response was not returned in the ganimede call
					// remove this ean from the searchEans
					synchronized (srchEanList) {
						if (srchEanList.contains(obj.getEAN())) {
							srchEanList.remove(obj.getEAN());
						}
					}
				}
			}// while

			// for those eans for which the status was not returned in the ganimede response
			if (null != srchEanList && !srchEanList.isEmpty()) {
				for (String ean : srchEanList) {
					DBProcessQService.addReq(new BrowseRowDBProcessObj<AcdmUserObject>(reqObj, new ItemActionInfoObj(ean)));
				}
			}
			mappedObjList = null;

		} catch (Exception e) {
			//logger.debug(e.getStackTrace().toString());
		}
	}
}

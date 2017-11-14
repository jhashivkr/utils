package com.ibg.data.uploaders;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibg.data.upload.exceptions.SelRecExceptionService;
import com.ibg.data.upload.exceptions.UploadMiscLogs;
import com.ibg.datasource.ActiveJdbcCon;
import com.ibg.models.selrecords.AcdmItemHistory;
import com.ibg.parsers.json.History;
import com.ibg.parsers.json.SelectionRecord;

public class AcdmItemHistoryUploader extends ActiveJdbcCon {

	private static SelectionRecord selectionRecord = null;
	private static Map<String, Map<String, String>> libGrpUserListDet = null;

	public AcdmItemHistoryUploader() {
	}

	public static void setSelectionRecord(SelectionRecord selectionRecord) {
		AcdmItemHistoryUploader.selectionRecord = selectionRecord;
	}

	public static void setLibGrpUserListDet(Map<String, Map<String, String>> libGrpUserListDet) {
		AcdmItemHistoryUploader.libGrpUserListDet = libGrpUserListDet;
	}

	public static void startDataUpload() {

		AcdmItemHistory history;
		String destListId = null;
		List<History> historyList = new LinkedList<History>();

		try {

			for (History obj : selectionRecord.getHistory()) {

				if (null != libGrpUserListDet.get(obj.getOidContact().trim())) {
					if (null != libGrpUserListDet.get(obj.getOidContact().trim()).get(obj.getOidIDList().trim())) {
						destListId = libGrpUserListDet.get(obj.getOidContact().trim()).get(obj.getOidIDList().trim());
					}
				}

				if (null != destListId) {
					history = new AcdmItemHistory();
					history.set("DEST_LIST_ID", destListId);
					history.set("EAN", selectionRecord.getOidPartNo());
					history.set("EVENT", obj.getEvent().toString());
					history.setTimestamp("DATE_TIME", obj.getDateTime());
					history.set("DETAILS", obj.getDetail());
					history.set("EVENT_INITIATOR_ID", obj.getOidContact());

					if (!history.saveIt()){
						UploadMiscLogs.writeBuffer("Exception from AcdmItemHistoryUploader for: " + selectionRecord.get_id() + ": save failed");
						System.out.println("history item not saved");
					}

				}// if
				else {
					obj.setOidIDList(obj.getOidIDList() + "|" + obj.getOidContact());
					historyList.add(obj);

				}
			}// for

			if (!historyList.isEmpty()) {
				selectionRecord.setUploadType("ACDM_ITEM_HISTORY");
				selectionRecord.setOidIDList(selectionRecord.getOidIDList() + "|" + selectionRecord.getOidContact());
				selectionRecord.setHistory(historyList);
				SelRecExceptionService.exceptionList().add(selectionRecord);
				UploadMiscLogs.writeBuffer("Exception from AcdmItemHistoryUploader for empty history for : " + selectionRecord.get_id());
			}

		} catch (Exception e) {
			System.out.println("Exception: " + e);
			UploadMiscLogs.writeBuffer("Exception from AcdmItemHistoryUploader for: " + selectionRecord.get_id() + ": " + e);
		}
	}

	public static void startDataUpdate() {

		AcdmItemHistory history;
		String destListId = null;
		List<History> historyList = new LinkedList<History>();

		try {

			for (History obj : selectionRecord.getHistory()) {

				if (null != libGrpUserListDet.get(obj.getOidContact().trim())) {
					if (null != libGrpUserListDet.get(obj.getOidContact().trim()).get(obj.getOidIDList().trim())) {
						destListId = libGrpUserListDet.get(obj.getOidContact().trim()).get(obj.getOidIDList().trim());
					}
				}

				if (null != destListId) {

					AcdmItemHistory chkObj = AcdmItemHistory.first("DEST_LIST_ID=? and EAN=? and EVENT=? and DETAILS=? and EVENT_INITIATOR_ID=?",
							destListId, selectionRecord.getOidPartNo(), obj.getEvent().toString(), obj.getDetail(), obj.getOidContact());

					if (null != chkObj) {
						AcdmItemHistory.update("DATE_TIME=?", "HIST_ID=?", (new AcdmItemHistory().setTimestamp("DATE_TIME", obj.getDateTime()))
								.getTimestamp("DATE_TIME"), chkObj.getId());
					}

				}// if
			}// for

		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

}

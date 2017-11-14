package com.ibg.data.uploaders;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.ibg.data.upload.exceptions.SelRecExceptionService;
import com.ibg.datasource.ActiveJdbcCon;
import com.ibg.parsers.json.SelectionRecord;

public class AcdmSelectionRecordUploader {

	private static List<SelectionRecord> selectionList;
	private static Map<String, Map<String, String>> libGrpUserListDet;

	public AcdmSelectionRecordUploader() {
	}

	public static void setSelectionRecord(List<SelectionRecord> selectionList) {
		AcdmSelectionRecordUploader.selectionList = selectionList;
	}

	public static void setLibGrpUserListDet(Map<String, Map<String, String>> libGrpUserListDet) {
		AcdmSelectionRecordUploader.libGrpUserListDet = libGrpUserListDet;
	}

	public static void startDataUpload(DataSource dataSource) {

		String listId = null;
		int thisOprId = 0;

		try {
			ActiveJdbcCon.getSelRecConnection(dataSource);

			if (null == selectionList || selectionList.isEmpty())
				return;

			for (SelectionRecord selectionRecord : selectionList) {

				listId = null;

				if (null != libGrpUserListDet.get(selectionRecord.getOidContact().trim())) {
					if (null != libGrpUserListDet.get(selectionRecord.getOidContact().trim()).get(selectionRecord.getOidIDList().trim())) {
						listId = libGrpUserListDet.get(selectionRecord.getOidContact().trim()).get(selectionRecord.getOidIDList().trim());
					}
				}

				if (null != listId && !listId.isEmpty()) {

					// if oidPartNo is null then it is a original order info
					if (null != selectionRecord.getOidPartNo() && !selectionRecord.getOidPartNo().isEmpty()
							&& !("NA".equalsIgnoreCase(selectionRecord.getOidPartNo())))
						thisOprId = AcdmListItemUploader.startDataUpload(selectionRecord, listId);

					if (thisOprId > 0) {
						//if there are no lines for the item loaded above
						//add at one order info record for the item
						
						// process for line no
						if (null != selectionRecord.getLines() && !selectionRecord.getLines().isEmpty()){
							AcdmOrderInfoLinesUploader.startDataUpload(selectionRecord, thisOprId, listId);
						}else{
							AcdmOrderInfoLinesUploader.startDataUploadDummy(selectionRecord, thisOprId, listId);
						}

						// process for header parameters
						if (null != selectionRecord.getHeaderParameters() && !selectionRecord.getHeaderParameters().isEmpty()){
							AcdmOrderInfoHeadersUploader.startDataUpload(selectionRecord, thisOprId, listId);
						}
					}

					// process for History
					if (null != selectionRecord.getHistory() && !selectionRecord.getHistory().isEmpty()) {
						AcdmItemHistoryUploader.setSelectionRecord(selectionRecord);
						AcdmItemHistoryUploader.setLibGrpUserListDet(libGrpUserListDet);
						AcdmItemHistoryUploader.startDataUpload();
					}

					// process for original order info
					if (null != selectionRecord.getOriginalOrderInfo())
						AcdmOriginalOrderInfoUploader.startDataUpload(selectionRecord, listId);

					// process for external part reservation
					if (null != selectionRecord.getExternalPartReservation())
						AcdmExternalPartRervationUploader.startDataUpload(selectionRecord, listId);

				} else {
					selectionRecord.setUploadType("List_Id is NULL");
					selectionRecord.setOidIDList(selectionRecord.getOidIDList() + "|" + selectionRecord.getOidContact());
					SelRecExceptionService.exceptionList().add(selectionRecord);
				}

			}// for

			ActiveJdbcCon.closeConnection();
		} catch (Exception e) {
			System.out.println("Exception from AcdmSelectionRecordUploader: " + e);
		} finally {
			ActiveJdbcCon.closeConnection();
		}
	}

	// process for History
	public static void startHistoryUpdate() {

		String listId = null;
		try {

			for (SelectionRecord selectionRecord : selectionList) {

				listId = null;

				if (null != libGrpUserListDet.get(selectionRecord.getOidContact().trim())) {
					if (null != libGrpUserListDet.get(selectionRecord.getOidContact().trim()).get(selectionRecord.getOidIDList().trim())) {
						listId = libGrpUserListDet.get(selectionRecord.getOidContact().trim()).get(selectionRecord.getOidIDList().trim());
					}
				}

				if (null != listId && !listId.isEmpty()) {

					if (null != selectionRecord.getHistory() && !selectionRecord.getHistory().isEmpty()) {
						AcdmItemHistoryUploader.setSelectionRecord(selectionRecord);
						AcdmItemHistoryUploader.setLibGrpUserListDet(libGrpUserListDet);
						AcdmItemHistoryUploader.startDataUpdate();
					}

				} else {

					selectionRecord.setOidIDList(selectionRecord.getOidIDList() + "|" + selectionRecord.getOidContact());
					SelRecExceptionService.exceptionList().add(selectionRecord);
				}

			}// for
		} catch (Exception e) {
			System.out.println("Exception from AcdmSelectionRecordUploaderActiveJdbc: " + e);
		}
	}

}

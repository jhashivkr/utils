package com.ibg.data.uploaders;

import java.util.List;

import com.ibg.datasource.ActiveJdbcCon;
import com.ibg.models.selrecords.AcdmListItem;
import com.ibg.parsers.json.SelectionRecord;

public class AcdmItemUpdate extends ActiveJdbcCon {

	private static List<SelectionRecord> selectionRecordList;

	public AcdmItemUpdate() {
	}

	public static void setSelecionRecordList(List<SelectionRecord> selectionRecordList) {
		AcdmItemUpdate.selectionRecordList = selectionRecordList;
	}

	public static void updateItem() {

		AcdmListItem itemList;

		try {
			
			if(null == selectionRecordList || selectionRecordList.isEmpty())
				return;

			for (SelectionRecord selectionRecord : selectionRecordList) {

				AcdmListItem chkObj = AcdmListItem
						.first("OIDIDLIST=? and EAN=? and ACTION=? and CUSTOMER=? and APPROVAL_CENTER=? and APPROVAL_PLAN = ? and RATIFIER_ID = ? and SELECTOR_ID = ? and REQ_ID = ?",
								selectionRecord.getOidIDList(), selectionRecord.getOidPartNo(), selectionRecord.getAction().toString(),
								selectionRecord.getOidCustomer(), selectionRecord.getOidApprovalCenter(), selectionRecord.getOidApprovalPlan(),
								selectionRecord.getRatifierID(), selectionRecord.getSelectorID(), selectionRecord.getRequestID());

				if (null != chkObj) {

					itemList = new AcdmListItem();
					itemList.setTimestamp("DATE_CREATED", selectionRecord.getDateCreated());
					itemList.setTimestamp("DATE_MODIFIED", selectionRecord.getDateModified());
					itemList.setTimestamp("DATE_TO_EXPIRE", selectionRecord.getDateToExpire());
					
					System.out.println("updating : " + chkObj.getId());

					AcdmListItem.update("DATE_CREATED=?, DATE_MODIFIED = ?, DATE_TO_EXPIRE = ?", "OPR_ID=?", itemList.get("DATE_CREATED"), itemList
							.get("DATE_MODIFIED"), itemList.get("DATE_TO_EXPIRE"), chkObj.getId());
				}
			}

		} catch (Exception e) {
			System.out.println("Exception from AcdmItemUpdate: " + e);
		}

	}

}

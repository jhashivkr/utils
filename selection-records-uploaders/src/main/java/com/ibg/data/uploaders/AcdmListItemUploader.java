package com.ibg.data.uploaders;

import com.ibg.data.upload.exceptions.SelRecExceptionService;
import com.ibg.data.upload.exceptions.UploadMiscLogs;
import com.ibg.datasource.ActiveJdbcCon;
import com.ibg.models.selrecords.AcdmListItem;
import com.ibg.parsers.json.SelectionRecord;

public class AcdmListItemUploader extends ActiveJdbcCon{

	public AcdmListItemUploader() {
	}

	public static int startDataUpload(SelectionRecord selectionRecord, String listId) {

		AcdmListItem itemList;
		int thisOprId = 0;

		try {

			itemList = new AcdmListItem();

			itemList.set("LIST_ID", listId);
			itemList.set("EAN", selectionRecord.getOidPartNo());

			itemList.set("ACTION", selectionRecord.getAction().toString());
			itemList.set("ACTIVE", ("true".equalsIgnoreCase(selectionRecord.getActive().toString()) ? "1" : "0"));
			itemList.setTimestamp("DATE_CREATED", selectionRecord.getDateCreated());
			itemList.setTimestamp("DATE_MODIFIED", selectionRecord.getDateModified());
			//itemList.setTimestamp("DATE_TO_EXPIRE", selectionRecord.getDateToExpire());			
			itemList.setTimestamp("DATE_TO_EXPIRE", selectionRecord.getExpirationDate());
			itemList.set("APPROVAL_CENTER", selectionRecord.getOidApprovalCenter());
			itemList.set("APPROVAL_PLAN", selectionRecord.getOidApprovalPlan());
			itemList.set("CUSTOMER", selectionRecord.getOidCustomer());			
			itemList.set("RATIFIER_ID", selectionRecord.getRatifierID());
			itemList.set("SELECTOR_ID", selectionRecord.getSelectorID());
			itemList.set("REQ_ID", selectionRecord.getRequestID());
			
			//not required
			//itemList.set("FAIL_REASON", selectionRecord.getFailReason());
			//itemList.set("SUPLR", selectionRecord.getOidSupplier());
			//itemList.set("OIDIDLIST", selectionRecord.getOidIDList());
			
			if (!itemList.saveIt()) {
				selectionRecord.setOidIDList(selectionRecord.getOidIDList() + "|" + selectionRecord.getOidContact());
				SelRecExceptionService.exceptionList().add(selectionRecord);
				selectionRecord.setUploadType("ACDM_LIST_ITEM");
				UploadMiscLogs.writeBuffer("Exception from AcdmListItemUploader for: " + selectionRecord.get_id() + ": save failed");
				System.out.println("From AcdmListItemUploader: " + selectionRecord.get_id() + " Not saved");
				
				return thisOprId;
			}
			
			thisOprId = itemList.getInteger("OPR_ID");
			
		} catch (Exception e) {
			selectionRecord.setOidIDList(selectionRecord.getOidIDList() + "|" + selectionRecord.getOidContact());
			SelRecExceptionService.exceptionList().add(selectionRecord);
			UploadMiscLogs.writeBuffer("Exception from AcdmListItemUploader for: " + selectionRecord.get_id() + ": " + e);
			e.printStackTrace();
		}

		return thisOprId;
	}


}

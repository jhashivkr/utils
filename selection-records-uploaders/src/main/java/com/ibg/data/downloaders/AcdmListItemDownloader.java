package com.ibg.data.downloaders;

import java.util.List;

import com.ibg.datasource.ActiveJdbcCon;
import com.ibg.db.ServiceLocator;
import com.ibg.models.selrecords.AcdmList;
import com.ibg.models.selrecords.AcdmListItem;
import com.ibg.utils.PropertyReader;

public class AcdmListItemDownloader extends ActiveJdbcCon {

	public AcdmListItemDownloader() {
	}

	public static void startDownload(String custGrp, List<AcdmList> acdmList) {

		StringBuilder bldr = new StringBuilder();
		BufferDataWriter bufWriter = null;
		int totRecordsdownloaded = 0;

		try {

			// open file
			bufWriter = new BufferDataWriter(custGrp, "_listitem",
					((PropertyReader) ServiceLocator.getBean("propertyReader")).getCisCustDataFileName());

			// List<AcdmList> acdmList =
			// AcdmList.where("LIB_GRP=? order by user_ownr_id,list_tp_id",
			// custGrp);

			for (AcdmList list : acdmList) {
				List<AcdmListItem> itemLists = list.getAll(AcdmListItem.class);

				if (null != itemLists && !itemLists.isEmpty()) {
					for (AcdmListItem item : itemLists) {

						// USER_ID|LIST_ID|OPR_ID|EAN|ACTION|ACTIVE|DATE_CREATED|DATE_MODIFIED|DATE_TO_EXPIRE|APPROVAL_CENTER|APPROVAL_PLAN|CUSTOMER|RATIFIER_ID|SELECTOR_ID|REQ_ID
						bldr.append((null != list.get("USER_OWNR_ID") ? list.getString("USER_OWNR_ID") : "")).append('|');
						bldr.append((null != list.get("LIST_TP_ID") ? list.getString("LIST_TP_ID") : "")).append('|');
						bldr.append((null != item.get("OPR_ID") ? item.getString("OPR_ID") : "")).append('|');
						bldr.append((null != item.get("EAN") ? item.getString("EAN") : "")).append('|');
						bldr.append((null != item.get("ACTION") ? item.getString("ACTION") : "")).append('|');
						bldr.append((0 == item.getInteger("ACTIVE") ? "false" : "true")).append('|');
						bldr.append((null != item.get("DATE_CREATED") ? item.getString("DATE_CREATED") : "")).append('|');
						bldr.append((null != item.get("DATE_MODIFIED") ? item.getString("DATE_MODIFIED") : "")).append('|');
						bldr.append((null != item.get("DATE_MODIFIED") ? item.getString("DATE_MODIFIED") : "")).append('|');
						bldr.append((null != item.get("APPROVAL_CENTER") ? item.getString("APPROVAL_CENTER") : "")).append('|');
						bldr.append((null != item.get("APPROVAL_PLAN") ? item.getString("APPROVAL_PLAN") : "")).append('|');
						bldr.append((null != item.get("CUSTOMER") ? item.getString("CUSTOMER") : "")).append('|');
						bldr.append((null != item.get("RATIFIER_ID") ? item.getString("RATIFIER_ID") : "")).append('|');
						bldr.append((null != item.get("SELECTOR_ID") ? item.getString("SELECTOR_ID") : "")).append('|');
						bldr.append((null != item.get("REQ_ID") ? item.getString("REQ_ID") : "")).append('\n');

						totRecordsdownloaded++;
						bufWriter.writeBufferedData(bldr.toString());
						bldr.delete(0, bldr.length());
					}
				}

			}

			if (totRecordsdownloaded <= 0) {
				// write blank line
				bufWriter.writeBufferedData("");
			}
			bufWriter.closeWriter();
			System.out.println("Total List records downloaded: " + totRecordsdownloaded);
			System.out.println();

			// not required
			// itemList.set("FAIL_REASON", selectionRecord.getFailReason());
			// itemList.set("SUPLR", selectionRecord.getOidSupplier());
			// itemList.set("OIDIDLIST", selectionRecord.getOidIDList());

		} catch (Exception e) {
			bufWriter.closeWriter();
			e.printStackTrace();
		}
	}

}

package com.ibg.data.uploaders;

import java.util.List;
import java.util.Map;

import com.ibg.datasource.ActiveJdbcCon;
import com.ibg.models.selrecords.AcdmOrderInfo;
import com.ibg.parsers.json.SelectionLines;
import com.ibg.parsers.json.SelectionRecord;

public class AcdmOrderInfoLinesUploader extends ActiveJdbcCon {

	public AcdmOrderInfoLinesUploader() {
	}

	public static void startDataUpload(SelectionRecord selectionRecord, int oprId, String listId) {

		AcdmOrderInfo orderInfo;
		List<SelectionLines> orderInfoLinesList = selectionRecord.getLines();
		Map<String, String> params;
		int ctr = 0;
		boolean stat = true;

		try {

			for (SelectionLines orderInfoLine : orderInfoLinesList) {
				params = orderInfoLine.getParams();
				ctr += stat ? 1 : 0;

				for (String key : params.keySet()) {
					orderInfo = new AcdmOrderInfo();

					orderInfo.set("OPR_ID", oprId);
					orderInfo.set("LIST_ID", listId);
					orderInfo.set("FIELD_KEY", key);

					if (params.get(key).length() >= 256) {
						orderInfo.set("FIELD_VALUE", params.get(key).substring(0, 255));
					} else {
						orderInfo.set("FIELD_VALUE", params.get(key));
					}

					orderInfo.set("LINE_NO", ctr);

					try {
						if (!orderInfo.insert()) {
							System.out.println("Lines for " + selectionRecord.get_id() + " Not saved");
							stat = false;
						}
					} catch (Exception e) {
						System.out.println("Exception - inserting order info lines: " + e);
						stat = false;
					}
				}// for

			}// for

		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

	public static void startDataUploadDummy(SelectionRecord selectionRecord, int oprId, String listId) {

		AcdmOrderInfo orderInfo;

		try {

			orderInfo = new AcdmOrderInfo();

			orderInfo.set("OPR_ID", oprId);
			orderInfo.set("LIST_ID", listId);
			orderInfo.set("FIELD_KEY", "ooqty");
			orderInfo.set("FIELD_VALUE", "1");
			orderInfo.set("LINE_NO", 1);

			if (!orderInfo.insert()) {
				System.out.println("Lines for " + selectionRecord.get_id() + " Not saved");
			}
		} catch (Exception e) {
			System.out.println("Exception - inserting order info lines: " + e);
		}

	}

}

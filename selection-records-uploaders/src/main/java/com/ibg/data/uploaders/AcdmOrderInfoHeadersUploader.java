package com.ibg.data.uploaders;

import java.util.Map;

import com.ibg.datasource.ActiveJdbcCon;
import com.ibg.models.selrecords.AcdmOrderInfo;
import com.ibg.parsers.json.SelectionRecord;

public class AcdmOrderInfoHeadersUploader extends ActiveJdbcCon {

	public AcdmOrderInfoHeadersUploader() {
	}

	public static void startDataUpload(SelectionRecord selectionRecord, int oprId, String listId) {

		AcdmOrderInfo orderInfo;
		Map<String, String> orderInfoHeadersList = selectionRecord.getHeaderParameters();

		try {

			for (String key : orderInfoHeadersList.keySet()) {
				orderInfo = new AcdmOrderInfo();

				orderInfo.set("OPR_ID", oprId);
				orderInfo.set("LIST_ID", listId);
				orderInfo.set("FIELD_KEY", key);

				if (orderInfoHeadersList.get(key).length() >= 256) {
					orderInfo.set("FIELD_VALUE", orderInfoHeadersList.get(key).substring(0, 255));
				} else {
					orderInfo.set("FIELD_VALUE", orderInfoHeadersList.get(key));
				}

				orderInfo.set("LINE_NO", 0);

				if (!orderInfo.insert())
					System.out.println("Lines for " + selectionRecord.get_id() + " Not saved");
			}// for

		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

}

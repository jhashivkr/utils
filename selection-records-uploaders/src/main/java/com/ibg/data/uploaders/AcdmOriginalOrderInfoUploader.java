package com.ibg.data.uploaders;

import com.ibg.datasource.ActiveJdbcCon;
import com.ibg.models.selrecords.AcdmOriginalOrderInfo;
import com.ibg.parsers.json.OriginalOrderInfoDetail;
import com.ibg.parsers.json.SelectionRecord;

public class AcdmOriginalOrderInfoUploader extends ActiveJdbcCon{

	public AcdmOriginalOrderInfoUploader() {
	}

	public static void startDataUpload(SelectionRecord selectionRecord, String listId) {

		OriginalOrderInfoDetail selRecorderInfo;
		AcdmOriginalOrderInfo orderInfo;

		try {
			selRecorderInfo = selectionRecord.getOriginalOrderInfo();

			orderInfo = new AcdmOriginalOrderInfo();
			orderInfo.set("OWNER_ID", selectionRecord.getOidContact());
			orderInfo.set("LIST_ID", listId);
			
			orderInfo.set("Title", selRecorderInfo.getTitle());
			orderInfo.set("Author", selRecorderInfo.getAuthor());
			orderInfo.set("Publisher", selRecorderInfo.getPublisher());
			orderInfo.set("PubYear", selRecorderInfo.getPubYear());
			orderInfo.set("Edition", selRecorderInfo.getEdition());
			orderInfo.set("PartNo", selRecorderInfo.getPartNo());
			orderInfo.set("Format", selRecorderInfo.getFormat());
			orderInfo.set("Price", selRecorderInfo.getPrice());
			orderInfo.set("Currency", selRecorderInfo.getCurrency());
			orderInfo.set("Volume", selRecorderInfo.getVolume());
			
			if (!orderInfo.saveIt())
				System.out.println("original order info not saved");

		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
	}

}

package com.ibg.data.downloaders;

import java.util.List;

import com.ibg.datasource.ActiveJdbcCon;
import com.ibg.db.ServiceLocator;
import com.ibg.models.selrecords.AcdmItemHistory;
import com.ibg.models.selrecords.AcdmList;
import com.ibg.utils.PropertyReader;

public class AcdmListHistoryDownloader extends ActiveJdbcCon {

	public AcdmListHistoryDownloader() {
	}

	public static void startDownload(String custGrp, List<AcdmList> acdmList) {

		StringBuilder bldr = new StringBuilder();
		BufferDataWriter bufWriter = null;
		int totRecordsdownloaded = 0;

		try {

			// open file
			bufWriter = new BufferDataWriter(custGrp, "_listitemhistory",
					((PropertyReader) ServiceLocator.getBean("propertyReader")).getCisCustDataFileName());

			for (AcdmList list : acdmList) {
				List<AcdmItemHistory> itemHistories = AcdmItemHistory.where("DEST_LIST_ID=? ORDER BY OPR_ID", list.getString("LIST_ID"));

				if (null != itemHistories && !itemHistories.isEmpty()) {
					for (AcdmItemHistory hist : itemHistories) {

						// USER_ID|LIST_ID|OPR_ID|EAN|EVENT|DATE_TIME|DETAILS|EVENT_INITIATOR_ID
						bldr.append((null != list.get("USER_OWNR_ID") ? list.getString("USER_OWNR_ID") : "")).append('|');
						bldr.append((null != list.get("LIST_TP_ID") ? list.getString("LIST_TP_ID") : "")).append('|');
						bldr.append((null != hist.get("OPR_ID") ? hist.getString("OPR_ID") : "")).append('|');
						bldr.append((null != hist.get("EAN") ? hist.getString("EAN") : "")).append('|');
						bldr.append((null != hist.get("EVENT") ? hist.getString("EVENT") : "")).append('|');
						bldr.append((null != hist.get("DATE_TIME") ? hist.getString("DATE_TIME") : "")).append('|');
						bldr.append((null != hist.get("DETAILS") ? hist.getString("DETAILS") : "")).append('|');
						bldr.append((null != hist.get("EVENT_INITIATOR_ID") ? hist.getString("EVENT_INITIATOR_ID") : "")).append('\n');

						totRecordsdownloaded++;
						bufWriter.writeBufferedData(bldr.toString());
						bldr.delete(0, bldr.length());
					}

				}// if
			}

			if (totRecordsdownloaded <= 0) {
				// write blank line
				bufWriter.writeBufferedData("");
			}
			bufWriter.closeWriter();
			System.out.println("Total item history records downloaded: " + totRecordsdownloaded);
			System.out.println();

		} catch (Exception e) {
			bufWriter.closeWriter();
			e.printStackTrace();
		}
	}

}

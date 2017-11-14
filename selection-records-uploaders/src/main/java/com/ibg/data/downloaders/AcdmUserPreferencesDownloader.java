package com.ibg.data.downloaders;

import java.util.Date;
import java.util.List;

import com.ibg.db.ServiceLocator;
import com.ibg.models.AcdmUserPref;
import com.ibg.utils.PropertyReader;

public class AcdmUserPreferencesDownloader {

	public AcdmUserPreferencesDownloader() {
	}

	public static void downloadDataFromTable(String custGrp, List<String> userIds) {

		int totRecordsdownloaded = 0;
		BufferDataWriter bufWriter = null;

		try {

			// open file
			bufWriter = new BufferDataWriter(custGrp, "pref", ((PropertyReader) ServiceLocator.getBean("propertyReader")).getCisCustDataFileName());

			System.out.println("Downloading User Pref Template Data - start at " + new Date());

			StringBuilder bldr = new StringBuilder();
			for (String userId : userIds) {

				List<AcdmUserPref> userPrefs = AcdmUserPref.where("USER_ID=?", userId);

				if (null != userPrefs && !userPrefs.isEmpty()) {
					for (AcdmUserPref pref : userPrefs) {

						bldr.append((null != pref.get("USER_ID") ? pref.get("USER_ID").toString() : "")).append('\t');
						bldr.append((null != pref.get("PREF_ID") ? pref.get("PREF_ID").toString() : "")).append('\t');
						bldr.append((null != pref.get("PREF_VALUE") ? pref.get("PREF_VALUE").toString() : "")).append('\n');

						totRecordsdownloaded++;
						bufWriter.writeBufferedData(bldr.toString());

						bldr.delete(0, bldr.length());
					}

				}
			}// for

			if (totRecordsdownloaded <= 0) {
				// write blank line
				bufWriter.writeBufferedData("");
			}
			bufWriter.closeWriter();
			System.out.println("Total User Pref records downloaded: " + totRecordsdownloaded);
			System.out.println();

		} catch (Exception e) {
			bufWriter.closeWriter();
			e.printStackTrace();
		}

	}

}

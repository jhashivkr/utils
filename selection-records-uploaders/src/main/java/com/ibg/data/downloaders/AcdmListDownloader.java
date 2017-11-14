package com.ibg.data.downloaders;

import java.util.Date;
import java.util.List;

import com.ibg.db.ServiceLocator;
import com.ibg.models.selrecords.AcdmList;
import com.ibg.utils.PropertyReader;

public class AcdmListDownloader {

	public AcdmListDownloader() {

	}

	public static void downloadDataFromTable(String custGrp) {

		int totRecordsdownloaded = 0;
		BufferDataWriter bufWriter = null;

		try {

			// open file
			bufWriter = new BufferDataWriter(custGrp, "opaplst",
					((PropertyReader) ServiceLocator.getBean("propertyReader")).getCisCustDataFileName());
			System.out.println("Downloading List Data - start at " + new Date());

			List<AcdmList> userLists = AcdmList.where("LIB_GRP=?", custGrp);

			for (AcdmList listRec : userLists) {
				bufWriter.writeBufferedData(listRec.toString());
				totRecordsdownloaded++;
			}

			if(totRecordsdownloaded <= 0){
				//write blank line
				bufWriter.writeBufferedData("");
			}
			bufWriter.closeWriter();
			System.out.println("Total List records downloaded: " + totRecordsdownloaded);
			System.out.println();
		} catch (Exception e) {
			bufWriter.closeWriter();
			e.printStackTrace();
		}

	}

}

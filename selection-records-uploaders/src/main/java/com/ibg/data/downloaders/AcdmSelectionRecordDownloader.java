package com.ibg.data.downloaders;

import java.util.List;

import javax.sql.DataSource;

import com.ibg.datasource.ActiveJdbcCon;
import com.ibg.models.selrecords.AcdmList;

public class AcdmSelectionRecordDownloader {

	public AcdmSelectionRecordDownloader() {
	}

	public static void startDownload(DataSource dataSource, String custGrp) {

		try {

			ActiveJdbcCon.getSelRecConnection(dataSource);

			List<AcdmList> acdmList = AcdmList.where("LIB_GRP=? order by user_ownr_id,list_tp_id", custGrp);
			if (null != acdmList && !acdmList.isEmpty()) {
				AcdmListItemDownloader.startDownload(custGrp, acdmList);
				AcdmListOrderInfoDownloader.startDownload(custGrp, acdmList);
				AcdmListHistoryDownloader.startDownload(custGrp, acdmList);
			}

			ActiveJdbcCon.closeConnection();
		} catch (Exception e) {
			System.out.println("Exception from AcdmSelectionRecordUploader: " + e);
		} finally {
			ActiveJdbcCon.closeConnection();
		}
	}

}

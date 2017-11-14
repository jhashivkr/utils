package com.ibg.data.uploaders;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.ibg.datasource.ActiveJdbcCon;
import com.ibg.models.AcdmUserPref;
import com.ibg.utils.PropertyReader;

public class AcdmUserPreferencesUploader extends ActiveJdbcCon implements AcdmDataUploaders {

	private List<Map<String, String>> data;
	private AcdmUserPref prefData;
	private PropertyReader propertyReader;
	private BufferedWriter bufferWriter;
	private DataSource dataSource;

	public AcdmUserPreferencesUploader() {
		getConnection();
	}

	public void setData(List<Map<String, String>> data) {
		this.data = data;
	}

	public void setPropertyReader(PropertyReader propertyReader) {
		this.propertyReader = propertyReader;
	}

	public void setBufferWriter(BufferedWriter bufferWriter) {
		this.bufferWriter = bufferWriter;
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;

	}

	public void startDataUpload(String [] dataFlds) {
		getThisConnection(dataSource);
		uploadDataToTable();

	}

	private void uploadDataToTable() {

		int totRecordsUploaded = 0;
		boolean saveRec = false;

		try {

			for (Map<String, String> rowData : data) {

				if (null != rowData && !rowData.isEmpty()) {
					saveRec = false;
					prefData = new AcdmUserPref();
					
					if (null != rowData.get("USER_ID") && !rowData.get("USER_ID").isEmpty()) {
						prefData.set("USER_ID", rowData.get("USER_ID"));

						prefData.set("PREF_ID", (null != rowData.get("PREF_ID") && !rowData.get("PREF_ID").isEmpty()) ? rowData.get("PREF_ID") : "-");
						prefData.set("PREF_VALUE", (null != rowData.get("PREF_VALUE") && !rowData.get("PREF_VALUE").isEmpty()) ? rowData
								.get("PREF_VALUE") : "-");
						saveRec = true;
					} else {
						bufferWriter.write("user id missing (" + totRecordsUploaded + "): " + rowData.get("PREF_ID") + " not saved.\n");
						saveRec = false;
					}

					if (saveRec) {
						try {
							if (!prefData.insert()) {
								bufferWriter.write(rowData.get("USER_ID") + " / " + rowData.get("PREF_ID") + " not saved");
							} else {
								totRecordsUploaded++;
							}
						} catch (Exception e) {
							bufferWriter.write(rowData.get("USER_ID") + " / " + rowData.get("PREF_ID") + " not saved.\nError:" + e.toString() + "\n");
						}
					}// if
				}// if

			}

			bufferWriter.write("Total records uploaded: " + totRecordsUploaded + "\n");
		} catch (Exception e) {
			try {
				bufferWriter.write("Exception: " + e.toString() + "\n");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

}

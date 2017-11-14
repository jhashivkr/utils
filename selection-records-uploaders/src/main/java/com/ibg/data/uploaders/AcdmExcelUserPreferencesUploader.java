package com.ibg.data.uploaders;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.ibg.datasource.ActiveJdbcCon;
import com.ibg.models.AcdmUserPref;
import com.ibg.utils.PropertyReader;

public class AcdmExcelUserPreferencesUploader extends ActiveJdbcCon implements AcdmDataUploaders {

	private List<Map<String, String>> data;
	private AcdmUserPref prefData;
	private PropertyReader propertyReader;
	private BufferedWriter bufferWriter;
	private DataSource dataSource;
	private String [] dataFlds;
	
	public AcdmExcelUserPreferencesUploader() {
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
		this.dataFlds = dataFlds;
		getThisConnection(dataSource);
		uploadDataToTable();

	}

	private void uploadDataToTable() {

		int totRecordsUploaded = 0;
		boolean saveRec = false;

		try {

			//ID,USER_ID,GROUP,FIELD,SELECTED
			for (Map<String, String> rowData : data) {

				if (null != rowData && !rowData.isEmpty()) {
					saveRec = false;
					prefData = new AcdmUserPref();

					//"USER_ID"
					if (null != rowData.get(dataFlds[1]) && !rowData.get(dataFlds[1]).isEmpty()) {
						prefData.set("USER_ID", rowData.get(dataFlds[1]));

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
							if (!prefData.saveIt()) {
								System.out.println(rowData.get("USER_ID") + " / " + rowData.get("PREF_ID") + " not saved");
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
			System.out.println("Total records uploaded: " + totRecordsUploaded);
		} catch (Exception e) {
			System.out.println("Exception: " + e.toString());
			try {
				bufferWriter.write("Exception: " + e.toString() + "\n");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

}

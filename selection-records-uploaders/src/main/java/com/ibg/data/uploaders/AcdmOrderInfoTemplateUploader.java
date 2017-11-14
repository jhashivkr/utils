package com.ibg.data.uploaders;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.ibg.datasource.ActiveJdbcCon;
import com.ibg.models.selrecords.AcdmOrderInfoTemplate;
import com.ibg.models.selrecords.AcdmOrderInfoTemplateData;
import com.ibg.utils.PropertyReader;
import com.ibg.utils.SearchConstants;

public class AcdmOrderInfoTemplateUploader extends ActiveJdbcCon implements AcdmDataUploaders {

	private List<Map<String, String>> data;
	private AcdmOrderInfoTemplateData templateData;
	private AcdmOrderInfoTemplate template;
	private PropertyReader propertyReader;
	private BufferedWriter bufferWriter;
	private Connection connection;

	public AcdmOrderInfoTemplateUploader() {
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

	public void setDbConnection(Connection connection) {
		this.connection = connection;
	}

	public void setDataSource(DataSource dataSource) {
		// TODO Auto-generated method stub

	}

	public void startDataUpload(String [] dataFlds) {
		uploadDataToTable();

		try {
			if (null != connection) {
				connection.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != connection) {
					connection.close();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	private void uploadDataToTable() {

		int totRecordsUploaded = 0;
		int rowCtr = 1;
		int thisTemplateId = 0;
		boolean saveRec = false;

		try {

			for (Map<String, String> rowData : data) {

				if (null != rowData && !rowData.isEmpty()) {
					saveRec = false;

					template = new AcdmOrderInfoTemplate();
					if (null != rowData.get("USER_ID") && !rowData.get("USER_ID").isEmpty()) {

						template.set("USER_OWNER_ID", rowData.get("USER_ID"));
						template.set("TEMPLATE_NAME", (null != rowData.get("TMPLT_NAME") && !rowData.get("TMPLT_NAME").isEmpty()) ? rowData
								.get("TMPLT_NAME") : "-");
						if (null != rowData.get("COPIES")) {
							try {
								template.set("TEMPLATE_VIEW_MODE", Integer.parseInt(rowData.get("COPIES")) > 0 ? "MULTI" : "SINGLE");
							} catch (Exception e) {
								template.set("TEMPLATE_VIEW_MODE", "SINGLE");
							}
						} else {
							template.set("TEMPLATE_VIEW_MODE", "SINGLE");
						}

						if (null != rowData.get("DEF_TMPLT")) {
							template.set("IS_DEFAULT_TEMPLATE", "true".equalsIgnoreCase(rowData.get("DEF_TMPLT")) ? "1" : "0");
						} else {
							template.set("IS_DEFAULT_TEMPLATE", "0");
						}

						saveRec = true;
					}// if
					else {
						bufferWriter.write("user id missing (" + totRecordsUploaded + "): " + rowData.get("TEMPLATE_NAME") + " not saved.\n");
						saveRec = false;
					}

					if (saveRec) {

						try {

							if (!template.saveIt()) {
								System.out.println(rowData.get("TMPLT_NAME") + " not saved");
							} else {
								totRecordsUploaded++;
							}
						} catch (Exception e) {
							bufferWriter.write("Template Name " + rowData.get("USER_ID") + " / " + rowData.get("TMPLT_NAME") + " not saved.\nError:"
									+ e.toString() + "\n");
						}

						thisTemplateId = template.getInteger("TEMPLATE_ID");

						templateData = new AcdmOrderInfoTemplateData();

						// blow up the params value
						if (null != rowData.get("PARAMS") && !rowData.get("PARAMS").isEmpty()) {
							String[] params = SearchConstants.oneTildePattern.split(rowData.get("PARAMS"));
							if (params.length > 0) {
								rowCtr = 0;
								for (String val : params) {
									String[] param = SearchConstants.COMMA_PATTERN.split(val);
									if (param.length > 0) {
										templateData.set("TEMPLATE_ID", thisTemplateId);
										templateData.set("FIELD_KEY", param[0]);
										templateData.set("FIELD_VALUE", param[1]);
										templateData.set("LINE_NO", rowCtr);
									} else {
										continue;
									}
								}
							}// if
							rowCtr++;

							try {
								if (!templateData.saveIt()) {
									System.out.println(rowData.get("TMPLT_NAME") + " data not saved");
								}
							} catch (Exception e) {
								bufferWriter.write("Template Data: " + rowData.get("USER_ID") + " / " + rowData.get("TMPLT_NAME")
										+ " not saved.\nError:" + e.toString() + "\n");
							}
						}

					}// if(saverec)

				}// if
			}// for

			bufferWriter.write("Total records uploaded: " + totRecordsUploaded + "\n");
			System.out.println("Total records uploaded: " + totRecordsUploaded);
		} catch (Exception e) {
			try {
				bufferWriter.write("Exception: " + e.toString() + "\n");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

}

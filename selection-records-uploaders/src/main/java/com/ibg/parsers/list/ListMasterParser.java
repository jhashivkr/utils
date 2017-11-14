package com.ibg.parsers.list;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.WordUtils;

import com.ibg.models.selrecords.AcademicListType;
import com.ibg.models.selrecords.AcdmList;

public class ListMasterParser<T> {

	private List<T> listMaster;
	private Set<String> users;
	private BufferedReader bufferReader;
	private List<Map<String, String>> data;

	public ListMasterParser() {

	}

	public ListMasterParser(List<Map<String, String>> data) {
		this.data = data;

	}

	public ListMasterData<T> getListMasterData() {
		ListMasterData<T> listData = new ListMasterData<T>();
		readTextFileAndLoadData();
		listData.setListMaster(listMaster);
		listData.setUsers(users);

		return listData;

	}

	public BufferedReader getBufferReader() {
		return bufferReader;
	}

	public void setBufferReader(BufferedReader bufferReader) {
		this.bufferReader = bufferReader;
	}

	@SuppressWarnings("unchecked")
	public ListMasterData<T> createAcdmListObjects() {

		ListMasterData<T> listData = new ListMasterData<T>();
		List<T> listMaster = new ArrayList<T>();
		Set<String> users = new HashSet<String>();

		
		String newListTpId = "";

		for (Map<String, String> rowData : data) {
			if (null != rowData && !rowData.isEmpty()) {
				AcdmList acdmObj = new AcdmList();

				// old List ID (String)
				if (null != rowData.get("OLD_UD_ID") && !rowData.get("OLD_UD_ID").isEmpty()) {
					acdmObj.set("OLD_UD_ID", rowData.get("OLD_UD_ID"));
				} else {
					acdmObj.set("OLD_UD_ID", "NA");
				}

				// list type Id
				newListTpId = null;
				newListTpId = AcademicListType.getListId(rowData.get("OLD_UD_ID"));
				acdmObj.set("LIST_TP_ID", newListTpId);
				acdmObj.set("ACTIVE", "1");

				// List Name (String)
				if (null != newListTpId) {
					if ("UD".equalsIgnoreCase(newListTpId))
						acdmObj.set("DESCRIPTION", rowData.get("DESCRIPTION").trim());
					else
						acdmObj.set("DESCRIPTION", AcademicListType.getListTypeNameById(newListTpId));
				}

				// Days History Stored (Integer)
				if (null != rowData.get("DAYS_HISTORY_STORED") && !rowData.get("DAYS_HISTORY_STORED").isEmpty()) {
					long histStored = Long.parseLong(rowData.get("DAYS_HISTORY_STORED"));
					acdmObj.set("DAYS_HISTORY_STORED", BigDecimal.valueOf(histStored));
				}

				// Notification Email Addresses (String)
				acdmObj.set("NOTIFICATION_EMAIL", rowData.get("NOTIFICATION_EMAIL"));

				// Notification Email Type (String)
				if (null != rowData.get("NOTIFICATION_EMAIL_TYPE") && !rowData.get("NOTIFICATION_EMAIL_TYPE").isEmpty()) {
					acdmObj.set("NOTIFICATION_EMAIL_TYPE", "plain".equalsIgnoreCase(rowData.get("NOTIFICATION_EMAIL_TYPE")) ? BigDecimal.ZERO
							: BigDecimal.ONE);
				}

				// Notification Detailed Email (Logical)
				if (null != rowData.get("NOTIFICATION_EMAIL_DETAILED") && !rowData.get("NOTIFICATION_EMAIL_DETAILED").isEmpty()) {
					acdmObj.set("NOTIFICATION_EMAIL_DETAILED", "true".equalsIgnoreCase(rowData.get("NOTIFICATION_EMAIL_DETAILED")) ? "1" : "0");
				}

				// Notification Schedule (String)
				acdmObj.set("NOTIFICATION_SCHEDULE", WordUtils.capitalize(rowData.get("NOTIFICATION_SCHEDULE")));

				// User ID (String)
				acdmObj.set("USER_OWNR_ID", rowData.get("USER_OWNR_ID"));
				users.add(rowData.get("USER_OWNR_ID"));

				// Forward List (Logical)
				// Notification Detailed Email (Logical)
				if (null != rowData.get("FORWARD_LIST") && !rowData.get("FORWARD_LIST").isEmpty()) {
					acdmObj.set("FORWARD_LIST", "true".equalsIgnoreCase(rowData.get("FORWARD_LIST")) ? "1" : "0");
				}

				listMaster.add((T) acdmObj);
			}
		}
		
		listData.setListMaster(listMaster);
		listData.setUsers(users);
		
		return listData;
	}

	@SuppressWarnings("unchecked")
	private void readTextFileAndLoadData() {
		int rowNumber = 0;
		int insertdRows = 0;
		listMaster = new ArrayList<T>();
		users = new HashSet<String>();
		System.out.println(new Date() + " ===== START Reading List Master from File=");

		try {

			String sCurrentLine;
			String[] result = null;
			String newListTpId = "";

			while ((sCurrentLine = bufferReader.readLine()) != null) {
				rowNumber++;
				if (sCurrentLine.endsWith("\t"))
					sCurrentLine = sCurrentLine + " ";
				result = sCurrentLine.split("\\t");

				AcdmList acdmObj = new AcdmList();

				// old List ID (String)
				if (null != result[0] && !result[0].isEmpty())
					acdmObj.set("OLD_UD_ID", removeQuotes(result[0]));
				else
					acdmObj.set("OLD_UD_ID", "NA");

				// list type Id
				// acdmObj.set("LIST_TP_ID",
				// AcademicListType.getListIdByName(removeQuotes(result[1])));
				newListTpId = null;
				newListTpId = AcademicListType.getListId(removeQuotes(result[0]));
				acdmObj.set("LIST_TP_ID", newListTpId);

				acdmObj.set("ACTIVE", "1");

				// List Name (String)
				if (null != newListTpId) {
					if ("UD".equalsIgnoreCase(newListTpId))
						acdmObj.set("DESCRIPTION", removeQuotes(result[1]).trim());
					else
						acdmObj.set("DESCRIPTION", AcademicListType.getListTypeNameById(newListTpId));
				}

				// Days History Stored (Integer)
				if (null != result[2] && !result[2].isEmpty()) {
					long histStored = Long.parseLong(removeQuotes(result[2]));
					acdmObj.set("DAYS_HISTORY_STORED", BigDecimal.valueOf(histStored));
				}
				acdmObj.set("NOTIFICATION_EMAIL", removeQuotes(result[3]));// Notification
																			// Email
																			// Addresses
																			// (String)

				// Notification Email Type (String)
				if (null != result[4] && !result[4].isEmpty()) {
					acdmObj.set("NOTIFICATION_EMAIL_TYPE", "plain".equalsIgnoreCase(removeQuotes(result[4])) ? BigDecimal.ZERO : BigDecimal.ONE);
				}

				// Notification Detailed Email (Logical)
				if (null != result[5] && !result[5].isEmpty()) {
					acdmObj.set("NOTIFICATION_EMAIL_DETAILED", "true".equalsIgnoreCase(removeQuotes(result[5])) ? "1" : "0");
				}

				acdmObj.set("NOTIFICATION_SCHEDULE", WordUtils.capitalize(removeQuotes(result[6])));// Notification
																									// Schedule
																									// (String)
				acdmObj.set("USER_OWNR_ID", removeQuotes(result[7]));// User ID
																		// (String)
				users.add(removeQuotes(result[7]));

				// Forward List (Logical)
				if (null != result[8] && !result[8].isEmpty()) {
					acdmObj.set("FORWARD_LIST", "true".equalsIgnoreCase(removeQuotes(result[8])) ? "1" : "0"); // Notification
																												// Detailed
																												// Email
																												// (Logical)
				}

				listMaster.add((T) acdmObj);

			}

			System.out.println(new Date() + " ===== END Loading TABLE. TOTAL ROWS INSERTED =" + insertdRows);

		} catch (Exception e) {
			System.err.println("ERROR occured at line Number=" + rowNumber + ", Error=" + e.toString());
		} finally {
			try {
				if (bufferReader != null)
					bufferReader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	} // End Method

	private String removeQuotes(String input) {
		if (input != null) {
			if (input.startsWith("\"") && input.endsWith("\""))
				input = input.substring(1, input.length() - 1);
			return input.trim();
		}
		return null;
	}

}

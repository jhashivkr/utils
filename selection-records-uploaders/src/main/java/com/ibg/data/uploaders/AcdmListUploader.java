package com.ibg.data.uploaders;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.ibg.datasource.ActiveJdbcCon;
import com.ibg.models.CisCustomer;
import com.ibg.models.CisUser;
import com.ibg.models.selrecords.AcdmList;
import com.ibg.parsers.list.ListMasterData;
import com.ibg.parsers.list.ListMasterParser;
import com.ibg.utils.PropertyReader;

public class AcdmListUploader extends ActiveJdbcCon implements AcdmDataUploaders {

	private ListMasterData<AcdmList> listMasterData;
	private Map<String, String> user_custGrp_map = new HashMap<String, String>();

	private List<Map<String, String>> data;
	private PropertyReader propertyReader;
	private BufferedWriter bufferWriter;
	private DataSource dataSource;
	private Long lastListId;
	private String [] dataFlds;

	public AcdmListUploader() {

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

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;

	}

	public void startDataUpload(String [] dataFlds) {
		this.dataFlds = dataFlds;
		getThisConnection(dataSource);
		ListMasterParser<AcdmList> listParser = new ListMasterParser<AcdmList>(data);
		listMasterData = listParser.createAcdmListObjects();
		mapUsersCustGroup();
		uploadDataToTable();
		uploadSearchResultListDetails();
		ActiveJdbcCon.closeConnection();
	}

	private void mapUsersCustGroup() {
		System.out.println("Mapping customer group - start at " + new Date());
		System.out.println("total users: " + listMasterData.getUsers().size());
		Set<String> nonExistUsers = new HashSet<String>();
		try {
			bufferWriter.write("\n*****Mapping customer group - start at ******\n" + new Date() + "\n");
		} catch (IOException e) {
		}

		CisUser user = null;
		CisCustomer customer = null;

		for (String userid : listMasterData.getUsers()) {
			// get the library group from the cis and cis customer tables

			// user = CisUser.findById(userid);
			// for the time being
			// the academics logged in user may have the id a <user_id>_1 but
			// the list master will have <user_id>
			user = CisUser.findFirst("lower(user_id) = lower(?) or lower(user_id) = lower(?) order by length(user_id)", userid, (userid + "_1"));

			if (null != user) {
				customer = CisCustomer.findById(user.get("customer_no"));

				if (null != customer) {
					user_custGrp_map.put(userid, customer.get("cust_group").toString());
				} else {
					user_custGrp_map.put(userid, "NA");
					nonExistUsers.add(userid);
				}
			} else {
				user_custGrp_map.put(userid, "UNA");
				nonExistUsers.add(userid);
			}
		}

		System.out.println("Mapping customer group - over at " + new Date());
		try {
			bufferWriter.write("\n*****Users missing customer group ******\n" + "\n");

			for (String usr : nonExistUsers) {
				bufferWriter.write("\n*****Customer Group not found for user:" + usr + "\n");
			}
			nonExistUsers = null;

		} catch (IOException e) {
		}
	}

	private void uploadDataToTable() {

		int totRecordsUploaded = 0;

		try {

			System.out.println("Uploading Data - start at " + new Date());
			bufferWriter.write("\n*****Uploading Data - start at******\n" + new Date() + "\n");

			for (AcdmList obj : listMasterData.getListMaster()) {

				if (null != user_custGrp_map.get(obj.get("USER_OWNR_ID")) && !("NA".equalsIgnoreCase(user_custGrp_map.get(obj.get("USER_OWNR_ID"))))
						&& !("UNA".equalsIgnoreCase(user_custGrp_map.get(obj.get("USER_OWNR_ID"))))) {

					obj.set("LIB_GRP", user_custGrp_map.get(obj.get("USER_OWNR_ID")));

					try {

						if (!recordExists(obj)) {
							if (!obj.saveIt()) {
								System.out.println(obj.get("OLD_UD_ID") + " not saved");
							} else {
								totRecordsUploaded++;
							}
						} // else {
							// System.out.println("lastListId: " + lastListId);
							// updateThisRecord(obj);
						// }
					} catch (Exception e) {
						bufferWriter.write("Not saved.\nError:" + e.toString() + "\n");
					}

				}// if

			}// for

			System.out.println("Total records uploaded: " + totRecordsUploaded);
			bufferWriter.write("\n*****Total records uploaded******\n" + totRecordsUploaded + "\n");
			System.out.println("Uploading Done - end at " + new Date());
			bufferWriter.write("\n*****Uploading Done - end at******\n" + new Date() + "\n");
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}

	}

	public boolean recordExists(AcdmList obj) {

		lastListId = null;
		AcdmList chkObj = AcdmList.first("USER_OWNR_ID=? and OLD_UD_ID=?", obj.get("USER_OWNR_ID"), obj.get("OLD_UD_ID"));

		if (null != chkObj) {
			lastListId = chkObj.getLongId();

			try {
				bufferWriter.write("\n" + "List already exists for: " + obj.get("USER_OWNR_ID") + "," + obj.get("OLD_UD_ID") + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}

		return false;
	}

	public boolean updateThisRecord(AcdmList obj) {

		Object chkObj = AcdmList.update("LIST_TP_ID=?, ACTIVE=?, DATE_TIME_NOTIFIED=?, DAYS_HISTORY_STORED==?, "
				+ "DESCRIPTION=?, NOTIFICATION_EMAIL=?, " + "NOTIFICATION_EMAIL_DETAILED=?, NOTIFICATION_EMAIL_TYPE=?, NOTIFICATION_SCHEDULE=?, "
				+ "FORWARD_LIST=?, LIB_GRP=?", "LIST_ID=?", obj.get("LIST_TP_ID"), obj.get("ACTIVE"), obj.get("DATE_TIME_NOTIFIED"), obj
				.get("DAYS_HISTORY_STORED"), obj.get("DESCRIPTION"), obj.get("NOTIFICATION_EMAIL"), obj.get("NOTIFICATION_EMAIL_DETAILED"), obj
				.get("NOTIFICATION_EMAIL_TYPE"), obj.get("NOTIFICATION_SCHEDULE"), obj.get("FORWARD_LIST"), obj.get("LIB_GRP"), lastListId);

		if (null != chkObj) {

			try {
				bufferWriter.write("\n" + "List already exists for: " + obj.get("USER_OWNR_ID") + "," + obj.get("OLD_UD_ID") + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}

		return false;
	}

	private void uploadSearchResultListDetails() {
		System.out.println("Inserting Search Result list details for users - start");

		for (String userid : listMasterData.getUsers()) {
			AcdmList acdmObj = new AcdmList();

			acdmObj.set("OLD_UD_ID", "SR");// OLD_UD_ID - set manually - no
											// match with the old mongo data
			acdmObj.set("LIST_TP_ID", "SR");// list type Id
			acdmObj.set("ACTIVE", "1");
			acdmObj.set("DESCRIPTION", "Search Results");// List Name (String)
			acdmObj.set("DAYS_HISTORY_STORED", BigDecimal.ZERO);
			acdmObj.set("NOTIFICATION_EMAIL", "");// Notification Email
													// Addresses (String)
			acdmObj.set("NOTIFICATION_EMAIL_TYPE", BigDecimal.ZERO);// Notification
																	// Email
																	// Type
																	// (String)
			acdmObj.set("NOTIFICATION_EMAIL_DETAILED", "1");// Notification
															// Detailed Email
															// (Logical)
			acdmObj.set("NOTIFICATION_SCHEDULE", "");// Notification Schedule
														// (String)
			acdmObj.set("USER_OWNR_ID", userid);// User ID (String)
			acdmObj.set("FORWARD_LIST", "1"); // Forward List (Logical)
			acdmObj.set("LIB_GRP", user_custGrp_map.get(userid));

			if (!recordExists(acdmObj)) {
				if (!acdmObj.saveIt()) {
					System.out.println("Search result for " + userid + " Not saved");
				}
			}
		}

		System.out.println("Inserting Search Result list details for users - over");
	}

}

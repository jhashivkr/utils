// _sfcontact.txt
package com.ibg.data.downloaders;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import com.ibg.datasource.ActiveJdbcCon;
import com.ibg.db.ServiceLocator;
import com.ibg.models.CisCustomer;
import com.ibg.models.CisUser;
import com.ibg.models.selrecords.AplcUser;
import com.ibg.utils.PropertyReader;

public class AcdmUsersDownloader extends ActiveJdbcCon implements AcdmDataDownloaders {

	private String custGrp;

	private DataSource dataSource;

	public AcdmUsersDownloader() {
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;

	}

	@Override
	public void startDataDownload(String customerGrp) {
		this.custGrp = customerGrp;
		getThisConnection(dataSource);
		downloadDataFromTable();
		ActiveJdbcCon.closeConnection();

	}

	private void downloadDataFromTable() {

		int totRecordsdownloaded = 0;
		BufferDataWriter bufWriter = null;

		try {

			// open file
			bufWriter = new BufferDataWriter(custGrp, "sfcontact", ((PropertyReader) ServiceLocator.getBean("propertyReader")).getCisCustDataFileName());
			
			System.out.println("Downloading User Data - start at " + new Date());

			List<String> userIds = new LinkedList<>();
			// get all the customer no for this customer group
			List<CisCustomer> customerLists = CisCustomer.where("CUST_GROUP=?", custGrp);

			if (null != customerLists && !customerLists.isEmpty()) {
				for (CisCustomer customer : customerLists) {
					// get the user info from cis user and aplc user
					List<CisUser> userList = CisUser.where("CUSTOMER_NO=?", customer.get("CUSTOMER_NO"));

					if (null != userList && !userList.isEmpty()) {

						for (CisUser contact : userList) {

							StringBuilder bldr = new StringBuilder();

							AplcUser aplcUser = AplcUser.findById(contact.get("USER_ID"));
							// ("SELECT USER_PWRD_DN, ADDR_INET_DN, CNCT_FST_NM, CNCT_LST_NM FROM APLC_USER where USER_ID=?",contact.get("USER_ID"));

							bldr.append((null != contact.get("ADMINISTRATOR") ? contact.get("ADMINISTRATOR").toString().toUpperCase() : "")).append(
									'\t');
							bldr.append((null != contact.get("CHECKOUT_TYPE") ? contact.get("CHECKOUT_TYPE").toString() : "")).append('\t');
							bldr.append((null != contact.get("CHECKOUT_VALUE") ? contact.get("CHECKOUT_VALUE").toString() : "")).append('\t');

							bldr.append((null != contact.get("RATIFIERID") ? contact.get("RATIFIERID").toString() : "")).append('\t');

							bldr.append((0 == contact.getInteger("CONT_CANCEL_ORDER") ? "FALSE" : "TRUE")).append('\t');
							bldr.append((0 == contact.getInteger("CONT_CLAIM_ORDER") ? "FALSE" : "TRUE")).append('\t');
							bldr.append((0 == contact.getInteger("CONT_PLACE_ORDER") ? "FALSE" : "TRUE")).append('\t');

							bldr.append((null != aplcUser.get("USER_PWRD_DN") ? aplcUser.get("USER_PWRD_DN").toString() : "")).append('\t');
							bldr.append((null != contact.get("USER_ID") ? contact.get("USER_ID").toString() : "")).append('\t');
							bldr.append((null != contact.get("CUSTOMER_NO") ? contact.get("CUSTOMER_NO").toString() : "")).append('\t');
							bldr.append((null != contact.get("DEFAULT_BUDGET") ? contact.get("DEFAULT_BUDGET").toString() : "")).append('\t');
							bldr.append((null != contact.get("DEFAULT_LOAN_TYPE") ? contact.get("DEFAULT_LOAN_TYPE").toString() : "")).append('\t');
							bldr.append((null != contact.get("DEFAULT_LOCATION_CODE") ? contact.get("DEFAULT_LOCATION_CODE").toString() : ""))
									.append('\t');
							bldr.append((null != contact.get("DEFAULT_PURCHASE") ? contact.get("DEFAULT_PURCHASE").toString() : "")).append('\t');
							bldr.append((null != contact.get("DISPLAY_REVIEWS") ? contact.get("DISPLAY_REVIEWS").toString() : "")).append('\t');
							bldr.append((null != aplcUser.get("ADDR_INET_DN") ? aplcUser.get("ADDR_INET_DN").toString() : "")).append('\t');
							bldr.append((null != contact.get("EMAIL_TYPE") ? contact.get("EMAIL_TYPE").toString() : "")).append('\t');

							bldr.append((0 == contact.getInteger("FIRM_CANCEL_ORDER") ? "FALSE" : "TRUE")).append('\t');
							bldr.append((0 == contact.getInteger("FIRM_CLAIM_ORDER") ? "FALSE" : "TRUE")).append('\t');
							bldr.append((0 == contact.getInteger("FIRM_PLACE_ORDER") ? "FALSE" : "TRUE")).append('\t');

							bldr.append((null != aplcUser.get("CNCT_FST_NM") ? aplcUser.get("CNCT_FST_NM").toString() : "")).append('\t');
							bldr.append((null != contact.get("INITIALS") ? contact.get("INITIALS").toString() : "")).append('\t');
							bldr.append((null != contact.get("ISPUBLIC") ? contact.get("ISPUBLIC").toString().toUpperCase() : "")).append('\t');
							bldr.append((null != contact.get("LITE") ? contact.get("LITE").toString().toUpperCase() : "")).append('\t');

							bldr.append((null != contact.get("PROVISIONAL_ACCESS") ? contact.get("PROVISIONAL_ACCESS").toString() : "")).append('\t');

							bldr.append((null != contact.get("RATIFIER") ? contact.get("RATIFIER").toString().toUpperCase() : "")).append('\t');

							bldr.append((null != contact.get("RATIFIER_PRIORITY") ? contact.get("RATIFIER_PRIORITY").toString() : "")).append('\t');
							bldr.append((null != contact.get("RECIPIENT_LISTING") ? contact.get("RECIPIENT_LISTING").toString() : "")).append('\t');

							bldr.append((null != contact.get("REQ_RECIPIENT") ? contact.get("REQ_RECIPIENT").toString().toUpperCase() : "")).append(
									'\t');

							bldr.append((null != contact.get("REQUEST_METHOD") ? contact.get("REQUEST_METHOD").toString() : "")).append('\t');
							bldr.append((null != contact.get("SHIBBOLETHID") ? contact.get("SHIBBOLETHID").toString() : "")).append('\t');

							bldr.append((null != contact.get("SOLUTION_ACCOUNTS") ? contact.get("SOLUTION_ACCOUNTS").toString().toUpperCase() : ""))
									.append('\t');

							bldr.append((null != aplcUser.get("CNCT_LST_NM") ? aplcUser.get("CNCT_LST_NM").toString() : "")).append('\t');

							bldr.append((null != contact.get("WEBSERVICE_CLIENT") ? contact.get("WEBSERVICE_CLIENT").toString().toUpperCase() : ""))
									.append('\n');
							bufWriter.writeBufferedData(bldr.toString());

							totRecordsdownloaded++;

							userIds.add(contact.get("USER_ID").toString());

						}// for
					}// if

				}// for
			}// if

			System.out.println("Total User records downloaded: " + totRecordsdownloaded);
			bufWriter.closeWriter();

			if (null != userIds && !userIds.isEmpty()) {
				AcdmListDownloader.downloadDataFromTable(custGrp);
				AcdmOrderInfoTemplateDownloader.downloadDataFromTable(custGrp, userIds);
				AcdmUserPreferencesDownloader.downloadDataFromTable(custGrp, userIds);
			}

			System.out.println("Downloading Done - end at " + new Date());
			System.out.println();
		} catch (Exception e) {
			bufWriter.closeWriter();
			e.printStackTrace();
			
		}

	} // End Method

} // END CLASS

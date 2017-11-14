// _sfcontact.txt
package com.ibg.data.uploaders;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.ibg.utils.DBUtility;
import com.ibg.utils.PropertyReader;

public class AcdmUsersUploader implements AcdmDataUploaders {

	private String load_db_name;
	private BufferedWriter bufferWriter;
	private Connection connection;
	private String[] dataFlds;

	private List<Map<String, String>> data;
	private PropertyReader propertyReader;

	private StringBuilder modifiedUserIDs = new StringBuilder("");

	static final String ADDRESS1 = "ADDRESS1";
	static final String ADDRESS2 = "ADDRESS2";
	static final String ADDRESS3 = "ADDRESS3";

	private String aplcUserCnt = "select count(1) from aplc_user where lower(USER_ID) = ?";
	private String aplcUserCnt1 = "select count(1) from aplc_user where lower(USER_ID) = ? and upper(cis_user_ind)='Y'";
	private String cisUserInsert = "insert into CIS_USER values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private String aplcUserInsert = "insert into aplc_user(user_id, cnct_fst_nm, cnct_lst_nm, cnct_ph_nbr, addr_inet_dn, lin1_opt_addr, lin2_opt_addr, lin3_opt_addr, zip_cd, user_pwrd_dn, USER_ACSS_FST_DT, user_add_id, add_dt, user_ii_ind,srvc_lvl_acss_ind, lock_ind,user_id_uc, cis_user_ind) values(?,?,?,?,?,?,?,?,?,?, sysdate,'ipage', sysdate, 'N', 'N', 'N', ?, 'Y')";
	private String aplcUserRoleInsert = "insert into aplc_user_role(user_id, role_nbr, mntc_lst_dt, mntc_user_id) values(?,?,sysdate, 'ipage')";

	public AcdmUsersUploader() {
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		try {
			this.connection = dataSource.getConnection();
		} catch (SQLException e) {
		}

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

	public void startDataUpload(String[] dataFlds) {
		this.dataFlds = dataFlds;
		load_db_name = this.propertyReader.getLoadDbEnv();
		uploadDataToTable();
	}

	private void uploadDataToTable() {

		int insertdRows = 0;
		int rowNumber = 0;
		System.out.println(new Date() + " END Reading FILE. TOTAL ROWS in File=" + rowNumber + ", Rows Added=" + data.size());

		insertdRows = loadTable(data);
		if (modifiedUserIDs.length() > 0) {
			System.out.println("---Following userIDs have been MODIFIED.--- \r\n" + modifiedUserIDs.toString());

		}
		System.out.println(new Date() + " ===== END Loading TABLE. TOTAL ROWS INSERTED =" + insertdRows);

		try {

			bufferWriter.write("---Following userIDs have been MODIFIED.--- \r\n");
			bufferWriter.write(modifiedUserIDs.toString());
			if (null != connection) {
				connection.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
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

	} // End Method

	private int loadTable(List<Map<String, String>> tableData) {

		PreparedStatement pCisUser = null;
		PreparedStatement pselect = null;
		PreparedStatement pselect2 = null;
		PreparedStatement pAplcUser = null;
		PreparedStatement pAplcUserRole = null;
		int insertRows = 0;
		if (tableData != null && tableData.size() > 0) {
			int rowCount = tableData.size();
			System.out.println("Number of rows to Load =" + rowCount);

			try {
				// When change to production, use correct password and email.
				// (remove 2 hard coded lines)
				connection.setAutoCommit(false);
				pselect = connection.prepareStatement(aplcUserCnt);
				pselect2 = connection.prepareStatement(aplcUserCnt1);
				pCisUser = connection.prepareStatement(cisUserInsert);
				pAplcUser = connection.prepareStatement(aplcUserInsert);
				pAplcUserRole = connection.prepareStatement(aplcUserRoleInsert);

				for (Map<String, String> rowData : tableData) {

					String origUserID = ((String) rowData.get(dataFlds[8])).toLowerCase();

					// clean up the old existing data

					if (!checkIfRowExists(connection, pselect, origUserID)) {
						insertRows = insertRows + insertRow(connection, pCisUser, rowData, pAplcUser, pAplcUserRole);
					} else {
						boolean isThisUserCISUser = checkIfRowExists(connection, pselect2, origUserID);
						if (isThisUserCISUser) {
							System.out.println(rowData.get(dataFlds[8]) + " --> USER(CIS USER) Already Exists in APLC_USER. NOT Adding again.");
						} else {
							modifiedUserIDs.append(origUserID).append(" modified to ").append(origUserID + "_1\r\n");
							boolean userNewuser = checkIfRowExists(connection, pselect2, origUserID + "_1");
							if (userNewuser) {
								System.out.println(origUserID + "_1" + " --> USER(CIS USER) Already Exists in APLC_USER. NOT Adding again.");
							} else {
								String newID = (String) rowData.get(dataFlds[8]) + "_1";
								rowData.put(dataFlds[8], newID);
								insertRows = insertRows + insertRow(connection, pCisUser, rowData, pAplcUser, pAplcUserRole);
							}
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtility.close(pselect);
				DBUtility.close(pselect2);
				DBUtility.close(pAplcUser);
				DBUtility.close(pAplcUserRole);
				DBUtility.close(pCisUser);
			}

		}
		return insertRows;
	}

	private boolean checkIfRowExists(Connection c, PreparedStatement pselect, String userID) {
		boolean rowExists = true;
		ResultSet rs = null;
		try {
			pselect.setString(1, userID);
			rs = pselect.executeQuery();
			if (rs.next())
				rowExists = rs.getInt(1) > 0;

			// if(rowExists)
			// System.out.println("user_id user already Exists for userID=" +
			// userID);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtility.close(rs);
		}
		return rowExists;
	}

	private int insertRow(Connection c, PreparedStatement pCisUser, Map<String, String> rowData, PreparedStatement pAplcUser,
			PreparedStatement pAplcUserRole) {
		int insertRows = 0;
		try {

			c.setAutoCommit(false);
			String userID = (String) rowData.get(dataFlds[8]);// "COUTTSUSERID"

			pAplcUser.setString(1, userID);
			pAplcUser.setString(2, (String) rowData.get(dataFlds[20]));// "FIRST_NAMES"
			pAplcUser.setString(3, (String) rowData.get(dataFlds[32]));// "SURNAME"
			pAplcUser.setString(4, "");
			String email = (String) rowData.get(dataFlds[15]);// "EMAIL"

			if (!"ORP".equalsIgnoreCase(load_db_name)) {
				email = "ebizoncall@ingramcontent.com"; // REMOVE THIS LINE FOR
														// PRODUCTION
			}

			pAplcUser.setString(5, email);
			pAplcUser.setString(6, ADDRESS1);
			pAplcUser.setString(7, ADDRESS2);
			pAplcUser.setString(8, ADDRESS3);
			String zip = "";
			if (zip != null && zip.length() > 9) {
				// System.out.println(zip +
				// " ZIP will be truncated to 9 chars.");
				zip = zip.substring(0, 9); // Need to increase the zip size.
											// Kennedy will give the zip.
			}
			pAplcUser.setString(9, zip);
			String password = (String) rowData.get(dataFlds[7]);// "COUTTSPASSWORD"
			if (!"ORP".equalsIgnoreCase(load_db_name)) {
				password = "java123";
			} else {
				if (null == password || password.isEmpty()) {
					password = "ingram1";
				}
			}
			pAplcUser.setString(10, password == null ? "ipage123" : password);
			pAplcUser.setString(11, userID.toUpperCase());
			insertRows = pAplcUser.executeUpdate();

			// insert in CIS_USER
			insertCisUserRow(c, pCisUser, rowData);

			// "insert into aplc_user_role(user_id, role_nbr, mntc_lst_dt, mntc_user_id) values(?,?,sysdate, 'ipage')"
			// Insert operations role(3) for every one and 1 for Admins
			pAplcUserRole.setString(1, userID);
			// Add Operations Role for everyone so that users get other basic
			// AUTH Tags true.
			pAplcUserRole.setInt(2, 3);
			pAplcUserRole.addBatch();

			String admin = (String) rowData.get(dataFlds[0]);// "ADMINISTRATOR"
			if ("true".equals(admin)) {
				pAplcUserRole.setInt(2, 1); // Admin Role
				pAplcUserRole.addBatch();
			}
			String basic = (String) rowData.get(dataFlds[23]);// "LITE"
			if (!"true".equals(admin) && "true".equals(basic)) {
				pAplcUserRole.setInt(2, 45); // Basic Role
				pAplcUserRole.addBatch();
			}
			if (!"true".equals(basic) || "true".equals(admin)) {
				String ratifier = (String) rowData.get(dataFlds[25]);// "RATIFIER"
				if ("true".equals(ratifier)) {
					pAplcUserRole.setInt(2, 43); // Acqusitions Role
					pAplcUserRole.addBatch();
				} else {// If not Acqusitions Role, then Selector
					pAplcUserRole.setInt(2, 44);
					pAplcUserRole.addBatch();

				}

			}

			pAplcUserRole.executeBatch();
			insertRows = 1;
			c.commit();
			// c.rollback();
		} catch (Exception e) {
			try {
				c.rollback();
			} catch (Exception e2) {
			}
			e.printStackTrace();
			System.err.print(" insertRow() method Exception for data =" + rowData.toString());

		}
		return insertRows;
	}

	private int insertCisUserRow(Connection c, PreparedStatement p, Map<String, String> rowData) throws Exception {
		int insertRows = 0;
		// insert into CIS_USER
		// values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		try {

			String userID = (String) rowData.get(dataFlds[8]);// "COUTTSUSERID"
			p.setString(1, userID);
			String admin = (String) rowData.get(dataFlds[0]);// "ADMINISTRATOR"
			p.setString(2, "true".equals(admin) ? "true" : "false");
			p.setString(3, (String) rowData.get(dataFlds[1]));// CHECKOUTTYPE
			p.setString(4, (String) rowData.get(dataFlds[2]));// CHECKOUTVALUE

			p.setInt(5, booleanToIntVal((String) rowData.get(dataFlds[4])));// CONT_CANCEL_ORDER
			p.setInt(6, booleanToIntVal((String) rowData.get(dataFlds[5])));// CONT_CLAIM_ORDER
			p.setInt(7, booleanToIntVal((String) rowData.get(dataFlds[6])));// CONT_PLACE_ORDER

			String custno = (String) rowData.get(dataFlds[9]);// CUSTOMER_NO");
			p.setString(8, custno);
			p.setString(9, (String) rowData.get(dataFlds[10]));// DEFAULTBUDGET
			p.setString(10, (String) rowData.get(dataFlds[11]));// DEFAULTLOANTYPE
			p.setString(11, (String) rowData.get(dataFlds[12]));// DEFAULTLOCATIONCODE
			p.setString(12, (String) rowData.get(dataFlds[13]));// DEFAULTPURCHASE
			p.setString(13, "");
			p.setString(14, (String) rowData.get(dataFlds[14]));// DISPLAYREVIEWS

			p.setInt(15, booleanToIntVal((String) rowData.get(dataFlds[17])));// FIRM_CANCEL_ORDER
			p.setInt(16, booleanToIntVal((String) rowData.get(dataFlds[18])));// FIRM_CLAIM_ORDER
			p.setInt(17, booleanToIntVal((String) rowData.get(dataFlds[19])));// FIRM_PLACE_ORDER

			p.setString(18, (String) rowData.get(dataFlds[21]));// INITIALS
			p.setString(19, "");
			p.setString(20, (String) rowData.get(dataFlds[22]));// ISPUBLIC
			p.setString(21, (String) rowData.get(dataFlds[23]));// LITE
			p.setString(22, "");
			p.setString(23, "");
			p.setString(24, (String) rowData.get(dataFlds[24]));// PROVISIONAL_ACCESS
			p.setString(25, (String) rowData.get(dataFlds[25]));// RATIFIER
			p.setString(26, (String) rowData.get(dataFlds[26]));// RATIFIERPRIORITY
			p.setString(27, (String) rowData.get(dataFlds[27]));// RECIPIENTLISTING
			p.setString(28, (String) rowData.get(dataFlds[28]));// REQRECIPIENT
			p.setString(29, (String) rowData.get(dataFlds[29]));// REQUESTMETHOD
			p.setString(30, (String) rowData.get(dataFlds[30]));// SHIBBOLETHID
			p.setString(31, (String) rowData.get(dataFlds[31]));// SOLUTION_ACCOUNTS
			p.setString(32, (String) rowData.get(dataFlds[33]));// WEBSERVICE_CLIENT
			p.setString(33, (String) rowData.get(dataFlds[16]));// EMAIL_TYPE
			p.setString(34, (String) rowData.get(dataFlds[3]));// RATIFIERID

			insertRows = p.executeUpdate();

			if ("true".equals(admin)) {
				updateAplcAcct(c, userID, custno);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.print(" insertCisUserRow() method Exception for data =" + rowData.toString());
			throw e;
		}
		return insertRows;
	}

	private void updateAplcAcct(Connection c, String USER_OWNR_ID, String customer_no) {
		Statement s = null;
		try {
			s = c.createStatement();
			String query = "update aplc_acct set USER_OWNR_ID = '" + USER_OWNR_ID
					+ "' where CUST_GROUP is NOT null and cust_group=(select cust_group from cis_customer where customer_no='" + customer_no + "')";
			// System.out.println(query);
			s.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.print(" updateAplcAcct() method Exception for Admin User =" + USER_OWNR_ID);
		} finally {
			DBUtility.close(s);
		}
	}

	private int insertAplcUserRole(Connection c, PreparedStatement pAplcUserRole, String userID) {
		int insertRows = 0;
		try {
			pAplcUserRole.setString(1, userID);
			insertRows = pAplcUserRole.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			System.err.print(" insertAplcUserRole() method Exception for User =" + userID);
		}
		return insertRows;
	}

	private int getIntVal(String qty) {
		if (qty == null || "".equals(qty.trim()))
			return 0;
		else
			return new Integer(qty);
	}

	private int booleanToIntVal(String val) {
		if (null != val) {
			return ("true".equalsIgnoreCase(val) ? 1 : 0);
		}
		return 0;
	}

} // END CLASS

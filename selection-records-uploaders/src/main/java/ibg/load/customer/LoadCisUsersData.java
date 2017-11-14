/*
 * _sfcontact.txt
 */
package ibg.load.customer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibg.file.loader.DataLoader;
import com.ibg.utils.DBUtility;

public class LoadCisUsersData implements DataLoader {

	private String fileName = null;
	private String dbName = null;

	private StringBuilder modifiedUserIDs = new StringBuilder("");

	static final String LOAD_TABLE_NAME = "CIS_USER";

	static final String ADDRESS1 = "ADDRESS1";
	static final String ADDRESS2 = "ADDRESS2";
	static final String ADDRESS3 = "ADDRESS3";
	static final String ADMINISTRATOR = "ADMINISTRATOR";
	static final String CHECKOUTTYPE = "CHECKOUTTYPE";
	static final String CHECKOUTVALUE = "CHECKOUTVALUE";
	static final String CONT_CANCEL_ORDER = "CONT_CANCEL_ORDER";
	static final String CONT_CLAIM_ORDER = "CONT_CLAIM_ORDER";
	static final String CONT_PLACE_ORDER = "CONT_PLACE_ORDER";
	static final String COUTTSPASSWORD = "COUTTSPASSWORD";
	static final String COUTTSUSERID = "COUTTSUSERID";
	static final String CUSTOMER_NO = "CUSTOMER_NO";
	static final String DEFAULTBUDGET = "DEFAULTBUDGET";
	static final String DEFAULTLOANTYPE = "DEFAULTLOANTYPE";
	static final String DEFAULTLOCATIONCODE = "DEFAULTLOCATIONCODE";
	static final String DEFAULTPURCHASE = "DEFAULTPURCHASE";
	static final String DEPARTMENT = "DEPARTMENT";
	static final String DISPLAYREVIEWS = "DISPLAYREVIEWS";
	static final String EMAIL = "EMAIL";
	static final String EMAIL_TYPE = "EMAIL_TYPE";
	static final String FIRM_CANCEL_ORDER = "FIRM_CANCEL_ORDER";
	static final String FIRM_CLAIM_ORDER = "FIRM_CLAIM_ORDER";
	static final String FIRM_PLACE_ORDER = "FIRM_PLACE_ORDER";
	static final String FIRST_NAMES = "FIRST_NAMES";
	static final String INITIALS = "INITIALS";
	static final String INSTITUTION = "INSTITUTION";
	static final String ISPUBLIC = "ISPUBLIC";
	static final String LITE = "LITE";
	static final String PHONE_NO = "PHONE_NO";
	static final String POSITION = "POSITION";
	static final String POSTAL_ZIP = "POSTAL_ZIP";
	static final String PROVISIONAL_ACCESS = "PROVISIONAL_ACCESS";
	static final String RATIFIER = "RATIFIER";
	static final String RATIFIERPRIORITY = "RATIFIERPRIORITY";
	static final String RECIPIENTLISTING = "RECIPIENTLISTING";
	static final String REQRECIPIENT = "REQRECIPIENT";
	static final String REQUESTMETHOD = "REQUESTMETHOD";
	static final String SHIBBOLETHID = "SHIBBOLETHID";
	static final String SOLUTION_ACCOUNTS = "SOLUTION_ACCOUNTS";
	static final String SURNAME = "SURNAME";
	static final String WEBSERVICE_CLIENT = "WEBSERVICE_CLIENT";

	public LoadCisUsersData() {
	}

	public LoadCisUsersData(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void setDataFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	@Override
	public boolean loadData() {
		try {
			readTextFileAndLoadData();
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static void main(String[] args) {
		try {
			LoadCisUsersData classObj = new LoadCisUsersData();
			classObj.readTextFileAndLoadData();
		} catch (Exception e) {
		}
	}

	private void readTextFileAndLoadData() {

		BufferedReader br = null;
		int rowNumber = 0;
		int insertdRows = 0;
		Map<String, String> rowData = null;
		List<Map<String, String>> tableData = new LinkedList<Map<String, String>>();

		System.out.println(new Date() + " ===== START LOADING Table APLC_USER and " + LOAD_TABLE_NAME + " from File=" + fileName);

		try {
			FileReader fr = new FileReader(fileName);
			br = new BufferedReader(fr);
			String sCurrentLine;
			String[] result = null;

			while ((sCurrentLine = br.readLine()) != null) {
				rowNumber++;
				if (sCurrentLine.endsWith("\t"))
					sCurrentLine = sCurrentLine + " ";
				result = sCurrentLine.split("\\t");
				if (result.length == 33) {
					rowData = new LinkedHashMap<String, String>();
					int position = 0;
					rowData.put(LoadCisUsersData.ADDRESS1, ""); // rowData.put(LoadCIS_USER.ADDRESS1,
																// removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.ADDRESS2, ""); // rowData.put(LoadCIS_USER.ADDRESS2,
																// removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.ADDRESS3, ""); // rowData.put(LoadCIS_USER.ADDRESS3,
																// removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.ADMINISTRATOR, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.CHECKOUTTYPE, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.CHECKOUTVALUE, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.CONT_CANCEL_ORDER, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.CONT_CLAIM_ORDER, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.CONT_PLACE_ORDER, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.COUTTSPASSWORD, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.COUTTSUSERID, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.CUSTOMER_NO, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.DEFAULTBUDGET, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.DEFAULTLOANTYPE, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.DEFAULTLOCATIONCODE, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.DEFAULTPURCHASE, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.DEPARTMENT, ""); // rowData.put(LoadCIS_USER.DEPARTMENT,
																	// removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.DISPLAYREVIEWS, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.EMAIL, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.EMAIL_TYPE, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.FIRM_CANCEL_ORDER, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.FIRM_CLAIM_ORDER, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.FIRM_PLACE_ORDER, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.FIRST_NAMES, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.INITIALS, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.INSTITUTION, ""); // rowData.put(LoadCIS_USER.INSTITUTION,
																	// removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.ISPUBLIC, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.LITE, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.PHONE_NO, ""); // rowData.put(LoadCIS_USER.PHONE_NO,
																// removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.POSITION, ""); // rowData.put(LoadCIS_USER.POSITION,
																// removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.POSTAL_ZIP, ""); // rowData.put(LoadCIS_USER.POSTAL_ZIP,
																	// removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.PROVISIONAL_ACCESS, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.RATIFIER, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.RATIFIERPRIORITY, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.RECIPIENTLISTING, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.REQRECIPIENT, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.REQUESTMETHOD, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.SHIBBOLETHID, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.SOLUTION_ACCOUNTS, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.SURNAME, removeQuotes(result[position++]));
					rowData.put(LoadCisUsersData.WEBSERVICE_CLIENT, removeQuotes(result[position++]));
					tableData.add(rowData);

				} else {
					System.err.println(rowNumber + " ROWNUMBER. COLUMNS SHOULD BE 33 BUT this row contains " + result.length
							+ " NOT matching. Complete row=" + sCurrentLine);

				}
			}
			System.out.println(new Date() + " END Reading FILE. TOTAL ROWS in File=" + rowNumber + ", Rows Added=" + tableData.size());
			
			insertdRows = loadTable(tableData);
			if (modifiedUserIDs.length() > 0)
				System.out.println("---Following userIDs have been MODIFIED.--- \r\n" + modifiedUserIDs.toString());
			System.out.println(new Date() + " ===== END Loading TABLE. TOTAL ROWS INSERTED =" + insertdRows);

		} catch (Exception e) {
			System.err.println("ERROR occured at line Number=" + rowNumber + ", Error=" + e.toString());
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	} // End Method

	private int loadTable(List<Map<String, String>> tableData) {
		Connection c = null;
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
				c = getNonPooledEBOracleConnectionWithRetries(dbName); 
				c.setAutoCommit(false);
				pselect = c.prepareStatement("select count(1) from aplc_user where lower(USER_ID) = ?");
				pselect2 = c.prepareStatement("select count(1) from aplc_user where lower(USER_ID) = ? and upper(cis_user_ind)='Y'");
				pCisUser = c.prepareStatement("insert into CIS_USER values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				pAplcUser = c
						.prepareStatement("insert into aplc_user(user_id, cnct_fst_nm, cnct_lst_nm, cnct_ph_nbr, addr_inet_dn, lin1_opt_addr, lin2_opt_addr, lin3_opt_addr, zip_cd, user_pwrd_dn, USER_ACSS_FST_DT, user_add_id, add_dt, user_ii_ind,srvc_lvl_acss_ind, lock_ind,user_id_uc, cis_user_ind) values(?,?,?,?,?,?,?,?,?,?, sysdate,'ipage', sysdate, 'N', 'N', 'N', ?, 'Y')");
				pAplcUserRole = c
						.prepareStatement("insert into aplc_user_role(user_id, role_nbr, mntc_lst_dt, mntc_user_id) values(?,?,sysdate, 'ipage')");

				for (int i = 0; i < rowCount; i++) {
					HashMap<String, String> rowData = (HashMap<String, String>) tableData.get(i);
					String origUserID = ((String) rowData.get("COUTTSUSERID")).toLowerCase();
					if (!checkIfRowExists(c, pselect, origUserID))
						insertRows = insertRows + insertRow(c, pCisUser, rowData, pAplcUser, pAplcUserRole);
					else {
						boolean isThisUserCISUser = checkIfRowExists(c, pselect2, origUserID);
						if (isThisUserCISUser)
							System.out.println(rowData.get("COUTTSUSERID") + " --> USER(CIS USER) Already Exists in APLC_USER. NOT Adding again.");
						else {
							// System.err.println(rowData.get("COUTTSUSERID") +
							// " --> USER(ipage user) Already Exists for ipage in APLC_USER. CHANGING userID to: "
							// + origUserID + "_1");
							modifiedUserIDs.append(origUserID + "\r\n");
							// rowData.put("COUTTSUSERID", origUserID + "_1");
							boolean userNewuser = checkIfRowExists(c, pselect2, origUserID + "_1");
							if (userNewuser)
								System.out.println(origUserID + "_1" + " --> USER(CIS USER) Already Exists in APLC_USER. NOT Adding again.");
							else {
								String newID = (String) rowData.get("COUTTSUSERID") + "_1";
								rowData.put("COUTTSUSERID", newID);
								insertRows = insertRows + insertRow(c, pCisUser, rowData, pAplcUser, pAplcUserRole);
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
				DBUtility.close(pCisUser, c);
			}

		}
		return insertRows;
	}

	private String removeQuotes(String input) {
		if (input != null) {
			if (input.startsWith("\"") && input.endsWith("\""))
				input = input.substring(1, input.length() - 1);
			return input.trim();
		}
		return null;
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

	private int insertRow(Connection c, PreparedStatement pCisUser, HashMap<String, String> rowData, PreparedStatement pAplcUser,
			PreparedStatement pAplcUserRole) {
		int insertRows = 0;
		try {
			c.setAutoCommit(false);
			String userID = (String) rowData.get(LoadCisUsersData.COUTTSUSERID);

			// Insert in APLC_USER
			pAplcUser.setString(1, userID);
			pAplcUser.setString(2, (String) rowData.get(LoadCisUsersData.FIRST_NAMES));
			pAplcUser.setString(3, (String) rowData.get(LoadCisUsersData.SURNAME));
			pAplcUser.setString(4, (String) rowData.get(LoadCisUsersData.PHONE_NO));
			String email = (String) rowData.get(LoadCisUsersData.EMAIL);

			if (!"ORP".equals(dbName)) {
				email = "ebizoncall@ingramcontent.com"; // REMOVE THIS LINE FOR
														// PRODUCTION
			}

			pAplcUser.setString(5, email);
			pAplcUser.setString(6, (String) rowData.get(LoadCisUsersData.ADDRESS1));
			pAplcUser.setString(7, (String) rowData.get(LoadCisUsersData.ADDRESS2));
			pAplcUser.setString(8, (String) rowData.get(LoadCisUsersData.ADDRESS3));
			String zip = (String) rowData.get(LoadCisUsersData.POSTAL_ZIP);
			if (zip != null && zip.length() > 9) {
				System.out.println(zip + " ZIP will be truncated to 9 chars.");
				zip = zip.substring(0, 9); // Need to increase the zip size.
											// Kennedy will give the zip.
			}
			pAplcUser.setString(9, zip);
			String password = (String) rowData.get(LoadCisUsersData.COUTTSPASSWORD);
			if (!"ORP".equals(dbName))
				password = "java123"; // Change this for production (REMOVE THIS
										// LINE)

			pAplcUser.setString(10, password == null ? "ipage123" : password);
			pAplcUser.setString(11, userID.toUpperCase());
			insertRows = pAplcUser.executeUpdate();

			// insert in CIS_USER
			insertCisUserRow(c, pCisUser, rowData);

			// Insert operations role(3) for every one and 1 for Admins
			pAplcUserRole.setString(1, userID);
			// Add Operations Role for everyone so that users get other basic
			// AUTH Tags true.
			pAplcUserRole.setInt(2, 3);
			pAplcUserRole.addBatch();

			String admin = (String) rowData.get(LoadCisUsersData.ADMINISTRATOR);
			if ("true".equals(admin)) {
				pAplcUserRole.setInt(2, 1); // Admin Role
				pAplcUserRole.addBatch();
			}
			String basic = (String) rowData.get(LoadCisUsersData.LITE);
			if (!"true".equals(admin) && "true".equals(basic)) {
				pAplcUserRole.setInt(2, 45); // Admin Role
				pAplcUserRole.addBatch();
			}
			if (!"true".equals(basic) || "true".equals(admin)) {
				String ratifier = (String) rowData.get(LoadCisUsersData.RATIFIER);
				if ("true".equals(ratifier))
					pAplcUserRole.setInt(2, 43); // Acqusitions Role
				else
					pAplcUserRole.setInt(2, 44); // If not Acqusitions Role,
													// then Selector
			}
			pAplcUserRole.addBatch();
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

	private int insertCisUserRow(Connection c, PreparedStatement p, HashMap<String, String> rowData) throws Exception {
		int insertRows = 0;
		try {
			String userID = (String) rowData.get(LoadCisUsersData.COUTTSUSERID);
			p.setString(1, userID);
			String admin = (String) rowData.get(LoadCisUsersData.ADMINISTRATOR);
			p.setString(2, "true".equals(admin) ? "true" : "false");
			p.setString(3, (String) rowData.get(LoadCisUsersData.CHECKOUTTYPE));
			p.setString(4, (String) rowData.get(LoadCisUsersData.CHECKOUTVALUE));
			
			//p.setInt(5, getIntVal((String) rowData.get(LoadCisUsersData.CONT_CANCEL_ORDER)));
			//p.setInt(6, getIntVal((String) rowData.get(LoadCisUsersData.CONT_CLAIM_ORDER)));
			//p.setInt(7, getIntVal((String) rowData.get(LoadCisUsersData.CONT_PLACE_ORDER)));
			
			p.setInt(5, booleanToIntVal((String) rowData.get(LoadCisUsersData.CONT_CANCEL_ORDER)));
			p.setInt(6, booleanToIntVal((String) rowData.get(LoadCisUsersData.CONT_CLAIM_ORDER)));
			p.setInt(7, booleanToIntVal((String) rowData.get(LoadCisUsersData.CONT_PLACE_ORDER)));
			
			
			
			String custno = (String) rowData.get(LoadCisUsersData.CUSTOMER_NO);
			p.setString(8, custno);
			p.setString(9, (String) rowData.get(LoadCisUsersData.DEFAULTBUDGET));
			p.setString(10, (String) rowData.get(LoadCisUsersData.DEFAULTLOANTYPE));
			p.setString(11, (String) rowData.get(LoadCisUsersData.DEFAULTLOCATIONCODE));
			p.setString(12, (String) rowData.get(LoadCisUsersData.DEFAULTPURCHASE));
			p.setString(13, (String) rowData.get(LoadCisUsersData.DEPARTMENT));
			p.setString(14, (String) rowData.get(LoadCisUsersData.DISPLAYREVIEWS));
			
			//p.setInt(15, getIntVal((String) rowData.get(LoadCisUsersData.FIRM_CANCEL_ORDER)));
			//p.setInt(16, getIntVal((String) rowData.get(LoadCisUsersData.FIRM_CLAIM_ORDER)));
			//p.setInt(17, getIntVal((String) rowData.get(LoadCisUsersData.FIRM_PLACE_ORDER)));
			
			p.setInt(15, booleanToIntVal((String) rowData.get(LoadCisUsersData.FIRM_CANCEL_ORDER)));
			p.setInt(16, booleanToIntVal((String) rowData.get(LoadCisUsersData.FIRM_CLAIM_ORDER)));
			p.setInt(17, booleanToIntVal((String) rowData.get(LoadCisUsersData.FIRM_PLACE_ORDER)));
			
			p.setString(18, (String) rowData.get(LoadCisUsersData.INITIALS));
			p.setString(19, (String) rowData.get(LoadCisUsersData.INSTITUTION));
			p.setString(20, (String) rowData.get(LoadCisUsersData.ISPUBLIC));
			p.setString(21, (String) rowData.get(LoadCisUsersData.LITE));
			p.setString(22, "");
			p.setString(23, (String) rowData.get(LoadCisUsersData.POSITION));
			p.setString(24, (String) rowData.get(LoadCisUsersData.PROVISIONAL_ACCESS));
			p.setString(25, (String) rowData.get(LoadCisUsersData.RATIFIER));
			p.setString(26, (String) rowData.get(LoadCisUsersData.RATIFIERPRIORITY));
			p.setString(27, (String) rowData.get(LoadCisUsersData.RECIPIENTLISTING));
			p.setString(28, (String) rowData.get(LoadCisUsersData.REQRECIPIENT));
			p.setString(29, (String) rowData.get(LoadCisUsersData.REQUESTMETHOD));
			p.setString(30, (String) rowData.get(LoadCisUsersData.SHIBBOLETHID));
			p.setString(31, (String) rowData.get(LoadCisUsersData.SOLUTION_ACCOUNTS));
			p.setString(32, (String) rowData.get(LoadCisUsersData.WEBSERVICE_CLIENT));
			p.setString(33, (String) rowData.get(LoadCisUsersData.EMAIL_TYPE));
			insertRows = p.executeUpdate();

			if ("true".equals(admin))
				updateAplcAcct(c, userID, custno);

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
					+ "' where USER_OWNR_ID is null and cust_group=(select cust_group from cis_customer where customer_no='" + customer_no + "')";
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
		if (null != val){
			return ("true".equalsIgnoreCase(val) ? 1 : 0);
		}
		return 0;
	}

	private Connection getNonPooledEBOracleConnectionWithRetries(String env) throws SQLException {
		Connection connection;
		String oracleURL = null;
		if ("ORDEV".equals(env))
			oracleURL = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=lvicoraracd01-vip.ingramcontent.com)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=lvicoraracd01-vip.ingramcontent.com)(PORT=1521))(LOAD_BALANCE=yes)(FAILOVER=ON))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=ORDEV_WAS_TAF.ingramtest.com)))";
		else if ("ORSTAGE".equals(env))
			oracleURL = "jdbc:oracle:thin:@(DESCRIPTION=(FAILOVER=ON)(ADDRESS_LIST=(LOAD_BALANCE=ON)(ADDRESS = (PROTOCOL = TCP)(HOST = lvicoraracq01-vip.ingramcontent.com)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = lvicoraracq02-vip.ingramcontent.com)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = lvicoraracq03-vip.ingramcontent.com)(PORT = 1521)))(CONNECT_DATA=(SERVICE_NAME= ORSTAGE_WAS_TAF.ingrambook.com)))";
		else if ("ORQA".equals(env))
			oracleURL = "jdbc:oracle:thin:@(DESCRIPTION=(FAILOVER=ON)(ADDRESS_LIST=(LOAD_BALANCE=ON)(ADDRESS = (PROTOCOL = TCP)(HOST = lvicoraracq01-vip.ingramcontent.com)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = lvicoraracq02-vip.ingramcontent.com)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = lvicoraracq03-vip.ingramcontent.com)(PORT = 1521)))(CONNECT_DATA=(SERVICE_NAME= ORQA_WAS_TAF.ingrambook.com)))";
		else if ("ORP".equals(env))
			oracleURL = "jdbc:oracle:thin:@(DESCRIPTION=(FAILOVER=ON)(ADDRESS_LIST=(LOAD_BALANCE=ON)(ADDRESS = (PROTOCOL = TCP)(HOST = lvicoraracp01-vip.ingramcontent.com)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = lvicoraracp02-vip.ingramcontent.com)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = lvicoraracp03-vip.ingramcontent.com)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = lvicoraracp04-vip.ingramcontent.com)(PORT = 1521)))(CONNECT_DATA=(SERVICE_NAME= ORP_WAS_TAF.ingrambook.com)))";

		try {
			Class.forName("oracle.jdbc.OracleDriver");
			DriverManager.setLogWriter(null);
			connection = DriverManager.getConnection(oracleURL, "EB_USER", "EB_USER");
		} catch (ClassNotFoundException e) {
			System.out.println(e);
			throw new SQLException("Unable to load Oracle driver: " + e.getMessage());
		}
		return connection;
	}

} // END CLASS

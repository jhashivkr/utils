/*
 * _arlg.txt
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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibg.file.loader.DataLoader;
import com.ibg.utils.DBUtility;

public class LoadCisCustomerGroupData implements DataLoader {

	private String fileName = null;
	private String dbName = null;

	static final String LOAD_TABLE_NAME = "CIS_CUST_GROUP";

	static final String ALLOWSTOCKRUSH = "ALLOWSTOCKRUSH";
	static final String CCGROUPSOPTIN = "CCGROUPSOPTIN";
	static final String COUNTRY = "COUNTRY";
	static final String CUST_GROUP = "CUST_GROUP";
	static final String DISPLAYEBOOKESTNET = "DISPLAYEBOOKESTNET";
	static final String DISPLAYESTNET = "DISPLAYESTNET";
	static final String DISPLAYREVIEWS = "DISPLAYREVIEWS";

	static final String IFOUND = "IFOUND";
	static final String LIBRARY_GROUP = "LIBRARY_GROUP";
	static final String MAXIMUMORDERQUANTITY = "MAXIMUMORDERQUANTITY"; // Integer
	static final String MULTILINE = "MULTILINE";
	static final String ILS_SYSTEM = "ILS_SYSTEM";
	static final String OPENURL = "OPENURL";

	static final String REPORT_NAME = "REPORT_NAME";
	static final String RESTRICTINITIALS = "RESTRICTINITIALS";
	static final String RETAINSELECTOR = "RETAINSELECTOR";
	static final String SHOWFUND = "SHOWFUND";
	static final String SHOWLOCATION = "SHOWLOCATION";
	static final String SHOWPROFILE = "SHOWPROFILE";
	static final String SHIBBLOLETHENTITYID = "SHIBBLOLETHENTITYID";
	static final String SHIBBOLETHIDP = "SHIBBOLETHIDP";
	static final String SHIBBOLETHURL = "SHIBBOLETHURL";
	static final String DATEFORMAT = "DATE_FORMAT";
	static final String APIKEY = "APIKEY";
	static final String APIIP = "APIIP";

	static final int COLUMN_COUNT = 25;

	static final String INSERT_APLCACCT_QUERY = "insert into aplc_acct(APLC_ACCT_ID, MKT_SGMT_CD, APLC_SVC_LVL_NBR, LOCK_DT, ADD_DT, LOCK_IND, USER_ADD_ID, PRGM_SRVC_ACCT_ID, USER_OWNR_ID, APLC_ACCT_GRTS_IND, CNCR_USE_ENBL_IND, CNCR_USER_MAX_CNT, APLC_ACCT_TRIL_IND,"
			+ "BOOK_IND, MUSC_IND, GIFT_IND, VI_IND, AQTN_SYS_VEND_CD, PRDL_IND, MUSC_ORD_DSPL_CD, GIFT_ORD_DSPL_CD, PRDL_ORD_DSPL_CD, VI_ORD_DSPL_CD, OPAC_URL, BOOK_ORD_DSPL_CD, KI_ENBL_IND, PRCE_SUPRS_IND, DIGI_IND, VG_ORD_DSPL_CD, VG_IND, CUST_GROUP)"
			+ "values(aplc_acct_id_seq.nextval, 'ACDM', 24, null, sysdate, 'N', 'ipage', null, ("
			+ LoadCisCustomerGroupData.USER_OWNRID_QUERY
			+ "), 'N', 'N', -1, 'N', 'Y','Y','Y','Y', null, 'N', 'EAN_ID', 'EAN_ID', null, 'EAN_ID',null, 'EAN_ID', 'N', 'N', 'Y', 'EAN_ID', 'Y', ?)";

	static final String USER_OWNRID_QUERY = "select user_id from cis_user where ADMINISTRATOR = 'true' and customer_no in(select CUSTOMER_NO from cis_customer where cust_group=?) and rownum < 2";

	public LoadCisCustomerGroupData() {
	}

	public LoadCisCustomerGroupData(String fileName) {
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
			LoadCisCustomerGroupData classObj = new LoadCisCustomerGroupData();
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

		System.out.println(new Date() + " ===== START LOADING Table " + LOAD_TABLE_NAME + " from File=" + fileName);

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
				if (result.length == COLUMN_COUNT) {
					rowData = new LinkedHashMap<String, String>();

					int position = 0;
					rowData.put(LoadCisCustomerGroupData.ALLOWSTOCKRUSH, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.CCGROUPSOPTIN, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.COUNTRY, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.CUST_GROUP, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.DISPLAYEBOOKESTNET, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.DISPLAYESTNET, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.DISPLAYREVIEWS, removeQuotes(result[position++]));

					rowData.put(LoadCisCustomerGroupData.IFOUND, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.LIBRARY_GROUP, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.MAXIMUMORDERQUANTITY, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.MULTILINE, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.ILS_SYSTEM, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.OPENURL, removeQuotes(result[position++]));

					rowData.put(LoadCisCustomerGroupData.REPORT_NAME, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.RESTRICTINITIALS, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.RETAINSELECTOR, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.SHOWFUND, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.SHOWLOCATION, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.SHOWPROFILE, removeQuotes(result[position++]));

					rowData.put(LoadCisCustomerGroupData.SHIBBLOLETHENTITYID, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.SHIBBOLETHIDP, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.SHIBBOLETHURL, removeQuotes(result[position++]));

					rowData.put(LoadCisCustomerGroupData.DATEFORMAT, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.APIKEY, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerGroupData.APIIP, removeQuotes(result[position++]));
					tableData.add(rowData);

				} else {
					System.err.println(rowNumber + " ROWNUMBER. COLUMNS SHOULD BE " + COLUMN_COUNT + " BUT this row contains " + result.length
							+ " NOT matching. Complete row=" + sCurrentLine);

				}

			}// while ((sCurrentLine = br.readLine()) != null)
			System.out.println(new Date() + " END Reading FILE. TOTAL ROWS in File=" + rowNumber + ", Rows Added=" + tableData.size());

			insertdRows = loadTable(tableData);
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
		PreparedStatement p = null;
		PreparedStatement pselect = null;
		PreparedStatement pAplcAcctSelect = null;
		PreparedStatement pAplcAcct = null;
		int insertRows = 0;
		if (tableData != null && tableData.size() > 0) {
			int rowCount = tableData.size();
			System.out.println("Number of rows to Load =" + rowCount);

			try {
				// c = getNonPooledEBOracleConnectionWithRetries(DATABASE_NAME);
				c = getNonPooledEBOracleConnectionWithRetries(dbName);
				c.setAutoCommit(false);
				pselect = c.prepareStatement("select count(1) from " + LOAD_TABLE_NAME + " where CUST_GROUP = ?");
				pAplcAcctSelect = c.prepareStatement("select count(1) from aplc_acct where cust_group=?");
				String insQuery = "insert into CIS_CUST_GROUP(allowstockrush, ccgroupsoptin, country, cust_group, displayebookestnet, displayestnet, displayreviews, ifound, library_group, maximumorderquantity, multiline, ils_system, openurl, report_name, restrictinitials, retainselector, showfund, showlocation, showprofile, shibblolethentityid, shibbolethidp, shibbolethurl, date_format, apikey, apiip) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				p = c.prepareStatement(insQuery);
				pAplcAcct = c.prepareStatement(INSERT_APLCACCT_QUERY);
				for (int i = 0; i < rowCount; i++) {
					HashMap<String, String> rowData = (HashMap<String, String>) tableData.get(i);
					if (!checkIfRowExists(c, pselect, rowData))
						insertRows = insertRows + insertRow(c, p, rowData, pAplcAcct, pAplcAcctSelect);

				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtility.close(pAplcAcctSelect);
				DBUtility.close(pselect);
				DBUtility.close(p, c);
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

	private boolean checkIfRowExists(Connection c, PreparedStatement select, HashMap<String, String> rowData) {
		boolean rowExists = true;
		ResultSet rs = null;
		try {
			select.setString(1, (String) rowData.get("CUST_GROUP"));
			rs = select.executeQuery();
			if (rs.next())
				rowExists = rs.getInt(1) > 0;

			if (rowExists)
				System.out.println("CUST_GROUP already Exists. " + (String) rowData.get("CUST_GROUP"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtility.close(rs);
		}
		return rowExists;
	}

	private int insertRow(Connection c, PreparedStatement p, HashMap<String, String> rowData, PreparedStatement pAplcAcct,
			PreparedStatement pSelectAplcAcct) {
		int insertRows = 0;
		try {
			c.setAutoCommit(false);
			p.setString(1, (String) rowData.get(LoadCisCustomerGroupData.ALLOWSTOCKRUSH));
			p.setString(2, (String) rowData.get(LoadCisCustomerGroupData.CCGROUPSOPTIN));
			p.setString(3, (String) rowData.get(LoadCisCustomerGroupData.COUNTRY));
			p.setString(4, (String) rowData.get(LoadCisCustomerGroupData.CUST_GROUP));
			p.setString(5, (String) rowData.get(LoadCisCustomerGroupData.DISPLAYEBOOKESTNET));
			p.setString(6, (String) rowData.get(LoadCisCustomerGroupData.DISPLAYESTNET));
			p.setString(7, (String) rowData.get(LoadCisCustomerGroupData.DISPLAYREVIEWS));
			p.setString(8, (String) rowData.get(LoadCisCustomerGroupData.IFOUND));
			p.setString(9, (String) rowData.get(LoadCisCustomerGroupData.LIBRARY_GROUP));
			String qty = (String) rowData.get(LoadCisCustomerGroupData.MAXIMUMORDERQUANTITY);
			if (qty == null || "".equals(qty.trim()))
				p.setInt(10, 0);
			else
				p.setInt(10, new Integer(qty));
			p.setString(11, (String) rowData.get(LoadCisCustomerGroupData.MULTILINE));
			p.setString(12, (String) rowData.get(LoadCisCustomerGroupData.ILS_SYSTEM));
			p.setString(13, (String) rowData.get(LoadCisCustomerGroupData.OPENURL));
			p.setString(14, (String) rowData.get(LoadCisCustomerGroupData.REPORT_NAME));
			p.setString(15, (String) rowData.get(LoadCisCustomerGroupData.RESTRICTINITIALS));
			p.setString(16, (String) rowData.get(LoadCisCustomerGroupData.RETAINSELECTOR));
			p.setString(17, (String) rowData.get(LoadCisCustomerGroupData.SHOWFUND));
			p.setString(18, (String) rowData.get(LoadCisCustomerGroupData.SHOWLOCATION));
			p.setString(19, (String) rowData.get(LoadCisCustomerGroupData.SHOWPROFILE));

			p.setString(20, (String) rowData.get(LoadCisCustomerGroupData.SHIBBLOLETHENTITYID));
			p.setString(21, (String) rowData.get(LoadCisCustomerGroupData.SHIBBOLETHIDP));
			p.setString(22, (String) rowData.get(LoadCisCustomerGroupData.SHIBBOLETHURL));

			p.setString(23, (String) rowData.get(LoadCisCustomerGroupData.DATEFORMAT));
			p.setString(24, (String) rowData.get(LoadCisCustomerGroupData.APIKEY));
			p.setString(25, (String) rowData.get(LoadCisCustomerGroupData.APIIP));

			insertRows = p.executeUpdate();

			// insert in APLC_ACCT Table.
			if (!checkIfRowExists(c, pSelectAplcAcct, rowData)) {
				pAplcAcct.setString(1, (String) rowData.get(LoadCisCustomerGroupData.CUST_GROUP));
				pAplcAcct.setString(2, (String) rowData.get(LoadCisCustomerGroupData.CUST_GROUP));
				pAplcAcct.executeUpdate();
			} else
				System.out.println("ROW Already exists in APLC_ACT for CUST_GROUP=" + (String) rowData.get(LoadCisCustomerGroupData.CUST_GROUP)
						+ ". But the row inserted in CIS_CUST_GROUP Table");
			c.commit();

		} catch (Exception e) {
			try {
				c.rollback();
			} catch (Exception e2) {
			}
			System.out.println((String) rowData.get(LoadCisCustomerGroupData.CUST_GROUP) + " , query=" + INSERT_APLCACCT_QUERY);
			e.printStackTrace();
			System.err.print(insertRows + " insertRow() method Exception for data =" + rowData.toString());

		}
		return insertRows;
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

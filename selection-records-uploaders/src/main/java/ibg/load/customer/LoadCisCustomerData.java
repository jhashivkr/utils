/*
 * _arcust.txt
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

public class LoadCisCustomerData implements DataLoader {

	private String fileName = null;
	private String dbName = null;

	static final String LOAD_TABLE_NAME = "CIS_CUSTOMER";
	static final String CURRENCY = "CURRENCY";
	static final String CUST_GROUP = "CUST_GROUP";
	static final String CUSTOMER_NO = "CUSTOMER_NO";
	static final String DIVISION = "DIVISION";
	static final String REPORT_NAME = "REPORT_NAME";
	static final String YEAR_END = "YEAR_END";

	public LoadCisCustomerData() {

	}

	public LoadCisCustomerData(String fileName) {
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
			LoadCisCustomerData classObj = new LoadCisCustomerData();
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
				if (result.length == 6) {
					int position = 0;
					rowData = new LinkedHashMap<String, String>();
					rowData.put(LoadCisCustomerData.CURRENCY, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerData.CUST_GROUP, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerData.CUSTOMER_NO, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerData.DIVISION, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerData.REPORT_NAME, removeQuotes(result[position++]));
					rowData.put(LoadCisCustomerData.YEAR_END, removeQuotes(result[position++]));
					tableData.add(rowData);

				} else {
					System.err.println(rowNumber + " ROWNUMBER. COLUMNS SHOULD BE 6 BUT row contains " + result.length + ". NOT matching="
							+ sCurrentLine);
				}
			}
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
		int insertRows = 0;
		if (tableData != null && tableData.size() > 0) {
			int rowCount = tableData.size();
			System.out.println("Number of rows to Load =" + rowCount);

			try {
				c = getNonPooledEBOracleConnectionWithRetries(dbName);
				pselect = c.prepareStatement("select count(1) from " + LOAD_TABLE_NAME + " where CUSTOMER_NO = ?");
				p = c.prepareStatement("insert into " + LOAD_TABLE_NAME + " values(?,?,?,?,?,?)");
				for (int i = 0; i < rowCount; i++) {
					HashMap<String, String> rowData = (HashMap<String, String>) tableData.get(i);
					if (!checkIfRowExists(c, pselect, rowData))
						insertRows = insertRows + insertRow(c, p, rowData);

				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
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
			return input;
		}
		return null;
	}

	private boolean checkIfRowExists(Connection c, PreparedStatement pselect, HashMap<String, String> rowData) {
		boolean rowExists = true;
		ResultSet rs = null;
		try {
			pselect.setString(1, (String) rowData.get("CUSTOMER_NO"));
			rs = pselect.executeQuery();
			if (rs.next())
				rowExists = rs.getInt(1) > 0;

			if (rowExists)
				System.out.println("Customer already Exists. " + (String) rowData.get("CUSTOMER_NO"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtility.close(rs);
		}
		return rowExists;
	}

	private int insertRow(Connection c, PreparedStatement p, HashMap<String, String> rowData) {
		int insertRows = 0;
		try {
			p.setString(1, (String) rowData.get(LoadCisCustomerData.CURRENCY));
			p.setString(2, (String) rowData.get(LoadCisCustomerData.CUST_GROUP));
			p.setString(3, (String) rowData.get(LoadCisCustomerData.CUSTOMER_NO));
			p.setString(4, (String) rowData.get(LoadCisCustomerData.DIVISION));
			p.setString(5, (String) rowData.get(LoadCisCustomerData.REPORT_NAME));
			String year = (String) rowData.get(LoadCisCustomerData.YEAR_END);
			if (year == null || "".equals(year.trim()))
				p.setNull(6, java.sql.Types.INTEGER);
			else
				p.setInt(6, new Integer(year));
			insertRows = p.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			System.err.print(LoadCisCustomerData.CURRENCY + "=" + rowData.get(LoadCisCustomerData.CURRENCY));
			System.err.print(LoadCisCustomerData.CUST_GROUP + "=" + rowData.get(LoadCisCustomerData.CUST_GROUP));
			System.err.print(LoadCisCustomerData.CUSTOMER_NO + "=" + rowData.get(LoadCisCustomerData.CUSTOMER_NO));
			System.err.print(LoadCisCustomerData.DIVISION + "=" + rowData.get(LoadCisCustomerData.DIVISION));
			System.err.print(LoadCisCustomerData.REPORT_NAME + "=" + rowData.get(LoadCisCustomerData.REPORT_NAME));
			System.err.println(LoadCisCustomerData.YEAR_END + "=" + rowData.get(LoadCisCustomerData.YEAR_END));
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

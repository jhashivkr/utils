/*
 * _arlg.txt
 */
package com.ibg.data.uploaders;

import java.io.BufferedWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.ibg.utils.DBUtility;
import com.ibg.utils.PropertyReader;

public class AcdmCustomerGroupUploader implements AcdmDataUploaders {

	private String fileName = null;
	private BufferedWriter bufferWriter;
	private Connection connection;
	private String[] dataFlds;

	private List<Map<String, String>> data;
	private PropertyReader propertyReader;

	static final int COLUMN_COUNT = 25;

	static final String INSERT_APLCACCT_QUERY = "insert into aplc_acct(APLC_ACCT_ID, MKT_SGMT_CD, APLC_SVC_LVL_NBR, LOCK_DT, ADD_DT, LOCK_IND, USER_ADD_ID, PRGM_SRVC_ACCT_ID, USER_OWNR_ID, APLC_ACCT_GRTS_IND, CNCR_USE_ENBL_IND, CNCR_USER_MAX_CNT, APLC_ACCT_TRIL_IND,"
			+ "BOOK_IND, MUSC_IND, GIFT_IND, VI_IND, AQTN_SYS_VEND_CD, PRDL_IND, MUSC_ORD_DSPL_CD, GIFT_ORD_DSPL_CD, PRDL_ORD_DSPL_CD, VI_ORD_DSPL_CD, OPAC_URL, BOOK_ORD_DSPL_CD, KI_ENBL_IND, PRCE_SUPRS_IND, DIGI_IND, VG_ORD_DSPL_CD, VG_IND, CUST_GROUP)"
			+ "values(aplc_acct_id_seq.nextval, 'ACDM', 24, null, sysdate, 'N', 'ipage', null, ("
			+ AcdmCustomerGroupUploader.USER_OWNRID_QUERY
			+ "), 'N', 'N', -1, 'N', 'Y','Y','Y','Y', null, 'N', 'EAN_ID', 'EAN_ID', null, 'EAN_ID',null, 'EAN_ID', 'N', 'N', 'Y', 'EAN_ID', 'Y', ?)";

	static final String USER_OWNRID_QUERY = "select user_id from cis_user where ADMINISTRATOR = 'true' and customer_no in(select CUSTOMER_NO from cis_customer where cust_group=?) and rownum < 2";

	public AcdmCustomerGroupUploader() {
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		try {
			this.connection = dataSource.getConnection();
		} catch (SQLException e) {
		}

	}

	public AcdmCustomerGroupUploader(String fileName) {
		this.fileName = fileName;
	}

	public void setData(List<Map<String, String>> data) {
		this.data = data;
	}

	public void setPropertyReader(PropertyReader propertyReader) {
		this.propertyReader = propertyReader;
	}

	public void startDataUpload(String[] dataFlds) {
		this.dataFlds = dataFlds;
		uploadDataToTable();
	}

	public void setBufferWriter(BufferedWriter bufferWriter) {
		this.bufferWriter = bufferWriter;
	}

	private void uploadDataToTable() {

		int insertdRows = 0;
		System.out.println(new Date() + " ===== START LOADING Table CIS_CUST_GROUP");

		insertdRows = loadTable(data);
		System.out.println(new Date() + " ===== END Loading TABLE. TOTAL ROWS INSERTED =" + insertdRows);

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

	} // End Method

	private int loadTable(List<Map<String, String>> tableData) {

		PreparedStatement p = null;
		PreparedStatement pselect = null;
		PreparedStatement pAplcAcctSelect = null;
		PreparedStatement pAplcAcct = null;
		int insertRows = 0;
		if (null != tableData && !tableData.isEmpty()) {
			int rowCount = tableData.size();
			System.out.println("Number of rows to Load =" + rowCount);

			try {

				connection.setAutoCommit(false);
				pselect = connection.prepareStatement("select count(1) from CIS_CUST_GROUP where CUST_GROUP = ?");
				pAplcAcctSelect = connection.prepareStatement("select count(1) from aplc_acct where cust_group=?");
				String insQuery = "insert into CIS_CUST_GROUP(allowstockrush, ccgroupsoptin, country, cust_group, displayebookestnet, displayestnet, displayreviews, ifound, library_group, maximumorderquantity, multiline, ils_system, openurl, report_name, restrictinitials, retainselector, showfund, showlocation, showprofile, shibblolethentityid, shibbolethidp, shibbolethurl, date_format, apikey, apiip) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				p = connection.prepareStatement(insQuery);
				pAplcAcct = connection.prepareStatement(INSERT_APLCACCT_QUERY);

				for (Map<String, String> rowData : tableData) {
					if (!checkIfRowExists(connection, pselect, rowData)) {
						insertRows = insertRows + insertRow(connection, p, rowData, pAplcAcct, pAplcAcctSelect);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtility.close(pAplcAcctSelect);
				DBUtility.close(pselect);
			}

		}
		return insertRows;
	}

	private boolean checkIfRowExists(Connection c, PreparedStatement select, Map<String, String> rowData) {
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

	private int insertRow(Connection c, PreparedStatement p, Map<String, String> rowData, PreparedStatement pAplcAcct,
			PreparedStatement pSelectAplcAcct) {
		int insertRows = 0;
		try {

					
			
			c.setAutoCommit(false);
			p.setString(1, (String) rowData.get(dataFlds[0]));// "ALLOWSTOCKRUSH"
			p.setString(2, (String) rowData.get(dataFlds[1]));// "CCGROUPSOPTIN"
			p.setString(3, (String) rowData.get(dataFlds[2]));// "COUNTRY"
			p.setString(4, (String) rowData.get(dataFlds[3]));// "CUST_GROUP"
			p.setString(5, (String) rowData.get(dataFlds[4]));// "DISPLAYEBOOKESTNET"
			p.setString(6, (String) rowData.get(dataFlds[5]));// "DISPLAYESTNET"
			p.setString(7, (String) rowData.get(dataFlds[6]));// "DISPLAYREVIEWS"
			p.setString(8, (String) rowData.get(dataFlds[7]));// "IFOUND"
			p.setString(9, (String) rowData.get(dataFlds[8]));// "LIBRARY_GROUP"

			// "MAXIMUMORDERQUANTITY"
			p.setInt(10, (null != rowData.get(dataFlds[9]) && !rowData.get(dataFlds[9]).isEmpty()) ? new Integer(rowData.get(dataFlds[9])) : 0);

			// String qty = (String)
			// rowData.get(dataFlds[0]);//"MAXIMUMORDERQUANTITY"
			// if (null == qty || "".equals(qty.trim()))
			// p.setInt(10, 0);
			// else
			// p.setInt(10, new Integer(qty));

			p.setString(11, (String) rowData.get(dataFlds[10]));// "MULTILINE"
			p.setString(12, (String) rowData.get(dataFlds[11]));// "ILS_SYSTEM"
			p.setString(13, (String) rowData.get(dataFlds[12]));// "OPENURL"
			p.setString(14, (String) rowData.get(dataFlds[13]));// "REPORT_NAME"
			p.setString(15, (String) rowData.get(dataFlds[14]));// "RESTRICTINITIALS"
			p.setString(16, (String) rowData.get(dataFlds[15]));// "RETAINSELECTOR"
			p.setString(17, (String) rowData.get(dataFlds[16]));// "SHOWFUND"
			p.setString(18, (String) rowData.get(dataFlds[17]));// "SHOWLOCATION"
			p.setString(19, (String) rowData.get(dataFlds[18]));// "SHOWPROFILE"

			p.setString(20, (String) rowData.get(dataFlds[19]));// "SHIBBLOLETHENTITYID"
			p.setString(21, (String) rowData.get(dataFlds[20]));// "SHIBBOLETHIDP"
			p.setString(22, (String) rowData.get(dataFlds[21]));// "SHIBBOLETHURL"

			p.setString(23, (String) rowData.get(dataFlds[22]));// "DATE_FORMAT"
			p.setString(24, (String) rowData.get(dataFlds[23]));// "APIKEY"
			p.setString(25, (String) rowData.get(dataFlds[24]));// "APIIP"

			insertRows = p.executeUpdate();

			// insert in APLC_ACCT Table.
			if (!checkIfRowExists(c, pSelectAplcAcct, rowData)) {
				pAplcAcct.setString(1, (String) rowData.get("CUST_GROUP"));
				pAplcAcct.setString(2, (String) rowData.get("CUST_GROUP"));
				pAplcAcct.executeUpdate();
			} else
				System.out.println("ROW Already exists in APLC_ACT for CUST_GROUP=" + (String) rowData.get("CUST_GROUP")
						+ ". But the row inserted in CIS_CUST_GROUP Table");
			c.commit();

		} catch (Exception e) {
			try {
				c.rollback();
			} catch (Exception e2) {
			}
			System.out.println((String) rowData.get("CUST_GROUP") + " , query=" + INSERT_APLCACCT_QUERY);
			e.printStackTrace();
			System.err.print(insertRows + " insertRow() method Exception for data =" + rowData.toString());

		}
		return insertRows;
	}

} // END CLASS

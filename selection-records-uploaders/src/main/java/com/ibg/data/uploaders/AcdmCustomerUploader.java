/*
 * _arcust.txt
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

public class AcdmCustomerUploader implements AcdmDataUploaders {

	private String fileName = null;
	private BufferedWriter bufferWriter;
	private Connection connection;
	private String [] dataFlds;

	private List<Map<String, String>> data;
	private PropertyReader propertyReader;

	public AcdmCustomerUploader() {

	}

	public AcdmCustomerUploader(String fileName) {
		this.fileName = fileName;
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

	public void startDataUpload(String [] dataFlds) {
		this.dataFlds = dataFlds;
		uploadDataToTable();
	}

	private void uploadDataToTable() {

		int insertdRows = 0;
		System.out.println(new Date() + " ===== START LOADING Table CIS_CUSTOMER");

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
		int insertRows = 0;
		if (tableData != null && tableData.size() > 0) {
			int rowCount = tableData.size();
			System.out.println("Number of rows to Load =" + rowCount);

			try {

				pselect = connection.prepareStatement("select count(1) from CIS_CUSTOMER where CUSTOMER_NO = ?");
				p = connection.prepareStatement("insert into CIS_CUSTOMER values(?,?,?,?,?,?)");
				for (Map<String, String> rowData: tableData) {
					if (!checkIfRowExists(connection, pselect, rowData))
						insertRows = insertRows + insertRow(connection, p, rowData);

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtility.close(p);
				DBUtility.close(pselect);
			}

		}
		return insertRows;
	}

	private boolean checkIfRowExists(Connection c, PreparedStatement pselect, Map<String, String> rowData) {
		boolean rowExists = true;
		ResultSet rs = null;
		try {
			pselect.setString(1, (String) rowData.get(dataFlds[2]));
			rs = pselect.executeQuery();
			if (rs.next())
				rowExists = rs.getInt(1) > 0;

			if (rowExists)
				System.out.println("Customer already Exists. " + (String) rowData.get(dataFlds[2]));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtility.close(rs);
		}
		return rowExists;
	}

	private int insertRow(Connection c, PreparedStatement p, Map<String, String> rowData) {
		int insertRows = 0;
		try {
			c.setAutoCommit(false);
			p.setString(1, (String) rowData.get(dataFlds[0]));//"CURRENCY"
			p.setString(2, (String) rowData.get(dataFlds[1]));//"CUST_GROUP"
			p.setString(3, (String) rowData.get(dataFlds[2]));//"CUSTOMER_NO"
			p.setString(4, (String) rowData.get(dataFlds[3]));//"DIVISION"
			p.setString(5, (String) rowData.get(dataFlds[4]));//"REPORT_NAME"
			p.setInt(6, (null != rowData.get(dataFlds[5]) && !rowData.get(dataFlds[5]).isEmpty()) ? new Integer(rowData.get(dataFlds[5])) : java.sql.Types.INTEGER);
			
			insertRows = p.executeUpdate();
			c.commit();

		} catch (Exception e) {
			try {
				c.rollback();
			} catch (Exception e2) {
			}
			e.printStackTrace();
			System.err.print("data in error: " + rowData);
			System.err.print("CURRENCY =" + rowData.get(dataFlds[0]));//"CURRENCY"
			System.err.print("CUST_GROUP =" + rowData.get(dataFlds[1]));//"CUST_GROUP"
			System.err.print("CUSTOMER_NO =" + rowData.get(dataFlds[2]));//"CUSTOMER_NO"
			System.err.print("DIVISION =" + rowData.get(dataFlds[3]));//"DIVISION"
			System.err.print("REPORT_NAME =" + rowData.get(dataFlds[4]));//"REPORT_NAME"
			System.err.println("YEAR_END =" + rowData.get(dataFlds[5]));//"YEAR_END"
		}
		return insertRows;
	}

} // END CLASS

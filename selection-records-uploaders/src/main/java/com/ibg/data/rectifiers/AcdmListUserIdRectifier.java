package com.ibg.data.rectifiers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibg.datasource.DBEnvConnections;
import com.ibg.db.ServiceLocator;
import com.ibg.utils.PropertyReader;

public class AcdmListUserIdRectifier {
	
	private Connection connection = null;

	static {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");
	}

	private List<String> cisUsers;

	protected void startRectifyProcess() {

		loadAllNonMatchingCISUsers();
		updateUsers();
	}

	private void loadAllNonMatchingCISUsers() {

		PreparedStatement pcisusers = null;
		ResultSet rs = null;

		System.out.println("Loading distinct non existing cis users");

		try {

			createDBConn();

			connection.setAutoCommit(false);
			//pcisusers = connection.prepareStatement("select distinct c.user_id from cis_user c where c.user_id not in (select distinct l.user_ownr_id from acdm_list l)");
			//pcisusers = connection.prepareStatement("select distinct l.user_ownr_id from acdm_list l where l.user_ownr_id not in (select distinct c.user_id from cis_user c)");
			
			pcisusers = connection.prepareStatement("select distinct c.user_id from cis_user c, acdm_list l where c.user_id = upper(l.user_ownr_id)");

			rs = pcisusers.executeQuery();
			cisUsers = new LinkedList<String>();
			while (rs.next()) {
				cisUsers.add(rs.getString(1));
			}

			rs.close();
			pcisusers.close();
			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != rs) {
					rs.close();
				}
				if (null != pcisusers) {
					pcisusers.close();
				}
				if (null != connection) {
					connection.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		System.out.println("users: " + cisUsers);

		System.out.println("All distinct non existing cis users loaded");

	}

	private void updateUsers() {

		if (null != cisUsers && !cisUsers.isEmpty()) {

			PreparedStatement pcisusers = null;

			try {
				createDBConn();
				
				System.out.println("----------------------------------------------------");
				System.out.printf("processing starts \n");

				for (String key : cisUsers) {

					// System.out.println("update acdm_list set user_ownr_id = '"
					// + key + "' where user_ownr_id = lower('" + key +
					// "') or user_ownr_id = upper('" + key + "')");
					

					//pcisusers = connection.prepareStatement("update acdm_list set user_ownr_id = '" + key + "' where user_ownr_id = lower('" + key + "') or user_ownr_id = upper('" + key + "')");
					pcisusers = connection.prepareStatement("update acdm_list set user_ownr_id = '" + key + 
							"' where user_ownr_id = lower('" + key + "') or user_ownr_id = upper('" + key + "')");
					
					pcisusers.execute();
					pcisusers.close();

				}// for

				System.out.printf("processing ends\n");
				System.out.println("----------------------------------------------------");

				pcisusers.close();
				connection.close();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {

					if (null != pcisusers) {
						pcisusers.close();
					}
					if (null != connection) {
						connection.close();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}
	
	private DataSource createDBConn() {

		DataSource dataSource = null;

		try {
			
			PropertyReader prop = ((PropertyReader) ServiceLocator.getBean("propertyReader"));
			String loadEnv = prop.getLoadDbEnv();

			DBEnvConnections env = ((DBEnvConnections) ServiceLocator.getBean("dbEnv"));
			connection = env.getConn(loadEnv);
			dataSource = env.getDataSource(loadEnv);

		} catch (Exception e) {
			System.err.println("ERROR :" + e.toString());
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException ex) {
			}
		} 

		return dataSource;
	}

	

	public static void main(String[] args) {
		
		new AcdmListUserIdRectifier().startRectifyProcess();
	}

}

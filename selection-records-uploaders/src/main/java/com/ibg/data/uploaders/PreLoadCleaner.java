package com.ibg.data.uploaders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

public class PreLoadCleaner {

	public PreLoadCleaner() {
		qryMap.put("aplc_acct", "delete from aplc_acct where MKT_SGMT_CD='ACDM' and CUST_GROUP='?'");
		qryMap.put("acdm_user_pref", "delete from ACDM_USER_PREF where USER_ID in (select USER_OWNR_ID from acdm_list where lib_grp='?')");
		qryMap.put("acdm_hist", "delete from ACDM_ITEM_HISTORY where SRC_LIST_ID in (select list_id from acdm_list where lib_grp='?')");
		qryMap.put("acdm_hist1", "delete from ACDM_ITEM_HISTORY where DEST_LIST_ID in (select list_id from acdm_list where lib_grp='?')");
		qryMap.put("acdm_ext_part", "delete from ACDM_EXTERNAL_PART_RESERVATION where list_id in (select list_id from acdm_list where lib_grp='?')");
		qryMap.put("acdm_ordr_info", "delete from ACDM_LIST_ORDER_INFO where list_id in (select list_id from acdm_list where lib_grp='?')");
		qryMap.put("acdm_mark_all", "delete from ACDM_MARK_ALL where list_id in (select list_id from acdm_list where lib_grp='?')");
		qryMap.put("acdm_orig_ordr", "delete from ACDM_ORIGINAL_ORDER_INFO where list_id in (select list_id from acdm_list where lib_grp='?')");
		qryMap.put("acdm_list_item", "delete from acdm_list_item where list_id in (select list_id from acdm_list where lib_grp='?')");
		qryMap.put("acdm_list", "delete from acdm_list where lib_grp='?'");
		qryMap.put("acdm_order_info_template",
				"delete from ACDM_ORDER_INFO_TEMPLATE where USER_OWNER_ID in (select USER_OWNR_ID from acdm_list where lib_grp='?')");
		qryMap.put(
				"aplc_user_role",
				"delete from APLC_USER_ROLE WHERE user_id in(select user_id from cis_user where customer_no in (select customer_no from CIS_CUSTOMER where CUST_GROUP='?'))");
		qryMap.put(
				"aplc_user",
				"delete from APLC_USER WHERE CIS_user_ind = 'Y' and USER_ID in (select user_id from cis_user where customer_no in (select customer_no from CIS_CUSTOMER where CUST_GROUP='?'))");
		qryMap.put("cis_user", "delete from cis_user where customer_no in (select customer_no from CIS_CUSTOMER where CUST_GROUP='?')");
		qryMap.put("cust", "delete from CIS_CUSTOMER where CUST_GROUP='?'");
		qryMap.put("cust_grp", "delete from CIS_CUST_GROUP where CUST_GROUP='?'");
	}

	private boolean cleanExistingData(String custGrp, Connection con) {
		String dqry = null;

		for (String qry : qryMap.keySet()) {
			dqry = qryMap.get(qry);
			dqry = StringUtils.replace(dqry, "?", custGrp);
			System.out.println("executing dqry: " + dqry);

			// try (PreparedStatement statement = con.prepareStatement(dqry)) {
			try {
				PreparedStatement statement = con.prepareStatement(dqry);
				con.setAutoCommit(false);
				statement.executeUpdate();
				con.commit();
				statement.close();
			} catch (SQLException sqle) {
				System.out.println(dqry + " - delete failed because: ");
				sqle.printStackTrace();
				try {
					con.rollback();
					return false;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}// for

		closeDBConn(con);
		return true;

	}

	public boolean cleanOldUserData(String custGrp) {

		if (null != custGrp && !custGrp.isEmpty()) {

			// try (Connection con = createDBConn()) {
			try {
				Connection con = createDBConn();
				return cleanExistingData(custGrp, con);
			//} catch (SQLException e) {
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}
	private Connection createDBConn() {

		Connection con = null;
		// try {

		// PropertyReader prop = ((PropertyReader)
		// ServiceLocator.getBean("propertyReader"));
		// String loadEnv = prop.getLoadDbEnv();

		// DBEnvConnections env = ((DBEnvConnections)
		// ServiceLocator.getBean("dbEnv"));
		// con = env.getConn(loadEnv);

		// } catch (Exception e) {
		// System.err.println("ERROR :" + e.toString());
		// e.printStackTrace();
		// return null;
		// }

		return con;
	}

	private void closeDBConn(Connection con) {

		try {
			if (null != con) {
				con.close();
			}

		} catch (Exception e) {
			System.err.println("ERROR :" + e.toString());
			e.printStackTrace();
		} finally {
			try {
				if (null != con) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return;
	}

	private Map<String, String> qryMap = new LinkedHashMap<String, String>();

}

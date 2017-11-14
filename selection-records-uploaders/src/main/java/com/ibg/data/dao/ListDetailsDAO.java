package com.ibg.data.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import com.ibg.data.upload.exceptions.UploadMiscLogs;
import com.ibg.datasource.ActiveJdbcCon;
import com.ibg.models.selrecords.AcdmList;

public class ListDetailsDAO extends ActiveJdbcCon {

	public ListDetailsDAO() {
		getConnection();
	}

	public static Map<String, Set<String>> findAllList(String libGrp) {

		Set<String> listIds = new HashSet<String>();
		Map<String, Set<String>> libGrpUserListDet = new LinkedHashMap<String, Set<String>>();
		String listKey = null;

		List<AcdmList> acdmList = AcdmList.findBySQL(
				"select user_ownr_id, old_ud_id from acdm_list where lib_grp=? order by user_ownr_id, old_ud_id", libGrp);

		listKey = acdmList.get(0).get("user_ownr_id").toString();
		listIds.add(acdmList.get(0).get("old_ud_id").toString());

		libGrpUserListDet.put(listKey, listIds);

		for (AcdmList list : acdmList) {
			listKey = list.get("user_ownr_id").toString();
			if (!libGrpUserListDet.containsKey(listKey)) {
				listIds = new HashSet<String>();
				listIds.add(list.get("old_ud_id").toString());
				libGrpUserListDet.put(list.get("user_ownr_id").toString(), listIds);
			} else {
				libGrpUserListDet.get(listKey).add(list.get("old_ud_id").toString());
			}
		}

		return libGrpUserListDet;
	}

	public static Map<String, Map<String, String>> findAllListId(String libGrp, DataSource dataSource) {

		Map<String, String> listIds = new HashMap<String, String>();
		Map<String, Map<String, String>> libGrpUserListDet = new LinkedHashMap<String, Map<String, String>>();

		try {
			ActiveJdbcCon.getSelRecConnection(dataSource);

			String listKey = null;

			List<AcdmList> acdmList = AcdmList
					.findBySQL(
							"select user_ownr_id, old_ud_id, list_id from acdm_list where lib_grp = lower(?) or lib_grp=upper(?) order by user_ownr_id, old_ud_id, list_id",
							libGrp, libGrp);

			listKey = acdmList.get(0).get("user_ownr_id").toString();
			listIds.put(acdmList.get(0).get("old_ud_id").toString(), acdmList.get(0).get("list_id").toString());

			libGrpUserListDet.put(listKey, listIds);

			for (AcdmList list : acdmList) {
				listKey = list.get("user_ownr_id").toString();
				if (!libGrpUserListDet.containsKey(listKey)) {
					listIds = new HashMap<String, String>();

					if ((null != list.get("old_ud_id")) && (null != list.get("list_id"))) {

						listIds.put(list.get("old_ud_id").toString(), list.get("list_id").toString());
						libGrpUserListDet.put(list.get("user_ownr_id").toString(), listIds);
					}
				} else {
					if ((null != list.get("old_ud_id")) && (null != list.get("list_id"))) {
						libGrpUserListDet.get(listKey).put(list.get("old_ud_id").toString(), list.get("list_id").toString());
					}
				}
			}// for

			ActiveJdbcCon.closeConnection();

		} catch (Exception e) {
			UploadMiscLogs.writeBuffer("Exception from ListDetailsDAO for libGrp: " + e + ":" + libGrp);
			System.out.println("Exception from ListDetailsDAO for libGrp: " + e + ":" + libGrp);
			e.printStackTrace();
		} finally {
			ActiveJdbcCon.closeConnection();
		}
		return libGrpUserListDet;
	}

}

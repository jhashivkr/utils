package com.common.test;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class DBUtilityJDBCTmpltTest {

	@Autowired
	private NamedParameterJdbcTemplate ebTmplt;

	private static String qry1 = "SELECT * from ACDM_LIST WHERE USER_OWNR_ID = :userOwnrId";

	public List<Map<String, Object>> getAllList(String userOwnrId) {

		try {
			MapSqlParameterSource parameters = new MapSqlParameterSource();
			parameters.addValue("userOwnrId", userOwnrId);
		
			List<Map<String, Object>> response = ebTmplt.queryForList(qry1, parameters);

			return response;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}

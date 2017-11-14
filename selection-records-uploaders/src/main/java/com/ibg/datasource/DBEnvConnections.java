/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ibg.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

/**
 *
 * @author sjha
 */
public class DBEnvConnections {

    private Map<String, BasicDataSource> connList;

    public Map<String, BasicDataSource> getConnList() {
        return connList;
    }

    public void setConnList(Map<String, BasicDataSource> connList) {
        this.connList = connList;
    }
    
    public DataSource getDataSource(String key){
    	return connList.get(key);
    }

    public Connection getConn(String key) {
        try {
            return connList.get(key).getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(DBEnvConnections.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}

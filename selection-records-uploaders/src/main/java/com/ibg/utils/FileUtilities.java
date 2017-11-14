package com.ibg.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibg.datasource.DBEnvConnections;
import com.ibg.db.ServiceLocator;

public class FileUtilities {
    
    static {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");
	}

    private void readTextFileAndLoadData(String fileName) {
        BufferedReader br = null;
        int rowNumber = 0;

        try {
            FileReader fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            String sCurrentLine;
            String[] result = null;

            while ((sCurrentLine = br.readLine()) != null) {

                rowNumber++;
                if (sCurrentLine.endsWith("\t")) {
                    sCurrentLine = sCurrentLine + " ";
                }
                result = sCurrentLine.split("\\t");

                System.out.println(result.length + "->" + sCurrentLine);
                sCurrentLine = null;
                // System.exit(1);

            }// while ((sCurrentLine = br.readLine()) != null)

        } catch (Exception e) {
            System.err.println("ERROR occured at line Number=" + rowNumber + ", Error=" + e.toString());
        }
    } // End Method

    private Connection getConnection() throws SQLException {
        DBEnvConnections devEnv = (DBEnvConnections) ServiceLocator.getBean("dbEnv");
        
        Connection connection = devEnv.getConn("stage");

        return connection;
    }

    private void testDbCon() {
        Connection c = null;
        PreparedStatement p = null;
        PreparedStatement pselect = null;
        PreparedStatement pAplcAcctSelect = null;
        PreparedStatement pAplcAcct = null;
        int insertRows = 0;

        try {
            c = getConnection();
            c.setAutoCommit(false);
            pselect = c.prepareStatement("select country,library_group,apikey,apiip from CIS_CUST_GROUP where CUST_GROUP = '74874000'");
            ResultSet rs = null;
            rs = pselect.executeQuery();
            while (rs.next()) {

                System.out.printf("\n\n%s,%s,%s,%s\n", rs.getString("country"), rs.getString("library_group"), rs.getString("apikey"), rs
                        .getString("apiip"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtility.close(pselect);
            DBUtility.close(p, c);

        }

    }

    public static void main(String[] args) {
		new FileUtilities().testDbCon();
    }
}

package com.ibg.ftp;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPFileFilters;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibg.db.ServiceLocator;
import com.ibg.utils.FileFilterUtil;
import com.ibg.utils.PropertyReader;

public class FTPConnector {

	private Map<String, Integer> doneFileStat = new LinkedHashMap<String, Integer>();

	static {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");
	}
	private static PropertyReader prop = ((PropertyReader) ServiceLocator.getBean("propertyReader"));

	private static void showServerReply(FTPClient ftpClient) {
		String[] replies = ftpClient.getReplyStrings();
		if (replies != null && replies.length > 0) {
			for (String aReply : replies) {
				System.out.println("SERVER: " + aReply);
			}
		}
	}

	private boolean tryFtpConnection(FTPClient ftpClient) {

		try {
			ftpClient.connect(prop.getFtpLoc(), prop.getFtpPort());
			showServerReply(ftpClient);
			int replyCode = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(replyCode)) {
				System.out.println("Operation failed. Server reply code: " + replyCode);
				return false;
			}
			boolean success = ftpClient.login(prop.getFtpUid(), prop.getFtpPass());
			showServerReply(ftpClient);
			if (!success) {
				System.out.println("Could not login to the server");
				return false;
			} else {
				System.out.println("LOGGED IN SERVER");
			}

		} catch (IOException ex) {
			System.out.println("Oops! Something wrong happened");
			ex.printStackTrace();
			return false;
		}

		return true;
	}

	private List<String> getAllDoneFiles(FTPClient ftpClient) {

		List<String> dataFiles = null;

		System.out.println("searching all .done files");
		try {
			FTPFile[] files = ftpClient.listFiles(prop.getFtpDumpLoc(), FTPFileFilters.NON_NULL);
			if (null != files) {
				dataFiles = new LinkedList<String>();
				for (FTPFile file : files) {
					if (file.isFile()) {
						if(FileFilterUtil.isDoneFile(file.getName())){
							doneFileStat.put(file.getName(), 0);
						}
					}
				}
			} else {
				System.out.println("no file found");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataFiles;

	}

	private List<String> getAllDataFiles(FTPClient ftpClient) {

		List<String> dataFiles = null;

		System.out.println("searching all files");
		try {
			FTPFile[] files = ftpClient.listFiles(prop.getFtpDumpLoc());
			if (null != files) {
				dataFiles = new LinkedList<String>();
				for (FTPFile file : files) {
					if (file.isFile()) {
						String extension = FileFilterUtil.getExtension(file.getName());
						if (null != extension) {

							if (extension.equalsIgnoreCase(FileFilterUtil.done)) {
								doneFileStat.put(file.getName(), 0);
							} else if (extension.equalsIgnoreCase(FileFilterUtil.json) || extension.equalsIgnoreCase(FileFilterUtil.txt)) {
								dataFiles.add(file.getName());
							}
						}
					}
				}
			} else {
				System.out.println("no file found");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataFiles;

	}


	public static void main(String[] args) {

		FTPClient ftpClient = new FTPClient();
		FTPConnector ftpConnect = new FTPConnector();
		if (ftpConnect.tryFtpConnection(ftpClient)) {
			
			List<String> filesList = ftpConnect.getAllDataFiles(ftpClient);

			System.out.println(".done files: " + ftpConnect.doneFileStat);

			for (String file : filesList) {
				System.out.println("file name: " + file + " - " + FileFilterUtil.getNameWithoutExtension(file));
			}
		}
	}

}

package com.ibg.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibg.data.uploaders.AcdmListUploader;
import com.ibg.db.ServiceLocator;

public class UploadListMaster {
	static {
		new ClassPathXmlApplicationContext("academic-applicationContext.xml");
	}
	final static Logger logger = LoggerFactory.getLogger(Test.class);

	public static void main(String[] args) {
		AcdmListUploader listUploaderAj = (AcdmListUploader) ServiceLocator.getBean("_opaplst");

		listUploaderAj.startDataUpload(args);

	}

}

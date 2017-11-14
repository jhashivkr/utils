package com.ibg.file.loader;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibg.data.uploaders.AcdmAdviceTest;

public class AspectTester {	

	public static void main(String[] args) {

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("academic-applicationContext.xml");
		AcdmAdviceTest adviceTest = context.getBean("acdmAdviceTest", AcdmAdviceTest.class);
		
		adviceTest.startDataUpload();
		adviceTest.testAfterLoad();
		
		context.close();
		
	}

}

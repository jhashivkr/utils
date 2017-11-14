package com.spring.jms;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ibg.cc.mq.writers.RequestQMessageWriter;

public class TestMessageWriter {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestMessageWriter app = new TestMessageWriter();
		app.run();
	}

	/**
	 * Run the migration
	 */
	public void run() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("creditcard-application-context.xml");
								
		System.out.println("writing message - browser1");
		RequestQMessageWriter jmsWriter = (RequestQMessageWriter) ctx.getBean("testMessageWriter");
		jmsWriter.send();
		System.out.println("end writing messages - browser1");
		
	}
}

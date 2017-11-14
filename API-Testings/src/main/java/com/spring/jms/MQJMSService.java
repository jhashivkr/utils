package com.spring.jms;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ibg.cc.mq.readers.MessageBrowser;

public class MQJMSService {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MQJMSService app = new MQJMSService();
		app.run();
	}

	/**
	 * Run the migration
	 */
	public void run() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("creditcard-application-context.xml");
				
		System.out.println("start reading messages - browser1");
		MessageBrowser jmsBrowser1 = (MessageBrowser) ctx.getBean("mqBrowser1");
		jmsBrowser1.browseQueue();
		System.out.println("end reading messages - browser1");
		
		System.out.println("start reading messages - browser2");
		MessageBrowser jmsBrowser2 = (MessageBrowser) ctx.getBean("mqBrowser2");
		jmsBrowser2.browseQueue();
		System.out.println("end reading messages - browser2");
		
		System.out.println("Message sent");
	}
}

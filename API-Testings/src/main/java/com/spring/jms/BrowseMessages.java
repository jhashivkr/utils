package com.spring.jms;

import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ibg.cybersource.creditcard.customer.service.CreditCardCustomerProfileService;
import ibg.cybersource.service.CreditCardServiceLocator;

public class BrowseMessages {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BrowseMessages app = new BrowseMessages();
		app.run();
	}

	/**
	 * Run the migration
	 */
	public void run() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("creditcard-application-context.xml");
		
		System.out.println("starting at: " + new Date());
		System.out.println("start reading messages - browser1");
		MessageReader jmsBrowser1 = (MessageReader) ctx.getBean("mqReader1");
		jmsBrowser1.browseQueue();
		System.out.println("end reading messages - browser1");
		
		System.out.println("start reading messages - browser2");
		MessageReader jmsBrowser2 = (MessageReader) ctx.getBean("mqReader2");
		jmsBrowser2.browseQueue();
		System.out.println("end reading messages - browser2");
		
		System.out.println("ending at: " + new Date());
	}
}

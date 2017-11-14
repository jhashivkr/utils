package com.spring.jms;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ListenMessages {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ListenMessages app = new ListenMessages();
		app.run();
	}

	/**
	 * Run the migration
	 */
	public void run() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("creditcard-application-context.xml");

		System.out.println("started");
		while (true) {

		}

	}
}

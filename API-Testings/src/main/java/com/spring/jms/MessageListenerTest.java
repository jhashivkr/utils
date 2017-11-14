package com.spring.jms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import ibg.cc.mq.listener.MessageListener;

public class MessageListenerTest {
	
	private DefaultMessageListenerContainer listenerContainer1;
	private DefaultMessageListenerContainer listenerContainer2;
	
	private static ApplicationContext ctx;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		
		MessageListenerTest app = new MessageListenerTest();
		app.run();
	}

	/**
	 * Run the migration
	 */
	public void run() {
				
		System.out.println("turning listeners on");
		listenerContainer1 = (DefaultMessageListenerContainer) ctx.getBean("listenerContainer1");
		listenerContainer2 = (DefaultMessageListenerContainer) ctx.getBean("listenerContainer2");
		listenerContainer1.setMessageListener(new MessageListener());
		listenerContainer2.setMessageListener(new MessageListener());

		listenerContainer1.setAcceptMessagesWhileStopping(false);
		listenerContainer2.setAcceptMessagesWhileStopping(false);

		listenerContainer1.start();
		listenerContainer2.start();

		System.out.println("listeners started");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		System.out.println("start at: " + df.format(new Date()));
		
		
	}
	

	public boolean isListener1Running() {
		return this.listenerContainer1.isRunning();
	}

	public boolean isListener2Running() {
		return this.listenerContainer2.isRunning();
	}

	public boolean isListener1Active() {
		return this.listenerContainer1.isActive();
	}

	public boolean isListener2Active() {
		return this.listenerContainer2.isActive();
	}
}

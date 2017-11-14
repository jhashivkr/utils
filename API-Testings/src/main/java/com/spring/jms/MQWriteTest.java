package com.spring.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class MQWriteTest {

	private JmsTemplate settlementWriteJmsTemplate1;

	public void sendMsg(String msg) {
		
		System.out.println("writing message - browser1");
		settlementWriteJmsTemplate1 = (JmsTemplate) ctx.getBean("settlementWriteJmsTemplate2");
		settlementWriteJmsTemplate1.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) {

				try {
					Message message = session.createTextMessage(msg);
					message.setStringProperty("JMS_IBM_Character_Set", "500");

					return message;
				} catch (JMSException e) {
					e.printStackTrace();
					return null;
				}

			}

		});

	}

	public static void main(String[] args) {
		ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		System.out.println("Attempting write");
		String message = "01DD20H496358L4S  DAACAuthchargsINTL            A                          100       ACCEPT                        4473034659725000001357                                                            MAST      ";
		//String message = "01CI20P27851NGDT  DAACSale      INTL            127208267                     VISA                                000041.21000000.00                                                                                                                                                                                                                                   20P2785     000010.16                                          3830815182470176056425                  ";
		new MQWriteTest().sendMsg(message);
		System.out.println("write done");
	}
	
	private static ApplicationContext ctx = null;
}

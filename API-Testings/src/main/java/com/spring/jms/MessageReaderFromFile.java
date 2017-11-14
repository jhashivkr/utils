package com.spring.jms;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import ibg.cc.mq.listener.MessageObject;
import ibg.cc.mq.message.ResponseMessage;
import ibg.cc.mq.writers.StringPadder;
import ibg.cybersource.creditcard.maintenance.service.MQTransactionHistoryService;
import ibg.cybersource.service.CreditCardServiceLocator;

public class MessageReaderFromFile {

	private static ApplicationContext ctx;
	
	private void readMessageFromFile() {

		//preAuth-testMsg.txt
		//settlement-11-11-2015.txt
		//settlement-testMsg.txt
		int counter = 1;
		try (ObjectInputStream reader = new ObjectInputStream(
				new FileInputStream("D:\\ipage-workspace\\test-mq-messages\\preAuth-testMsg.txt"))) {

			Message message = null;

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
			System.out.println("start at: " + df.format(new Date()));
			while (true) {
				try {
					message = (Message) reader.readObject();	
					MessageObject msgObject = new MessageObject(counter++, message);					
					msgObject.builder();
					
					if(null != msgObject){
						MQTransactionHistoryService mqTransactionHistoryService = (MQTransactionHistoryService) CreditCardServiceLocator
							.getService(CreditCardServiceLocator.ServiceName.MQ_TRANSACTION_HISTORY_SERVICE);
						String tranId = mqTransactionHistoryService.recordNewMessage(msgObject);
						msgObject.setTranId(tranId);
					}
															
					RequestService.addReq(msgObject);
					
				} catch (EOFException eof) {
					break;
				}

			}
			

			reader.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ctx = new ClassPathXmlApplicationContext("creditcard-application-context.xml");
		//new MessageReaderFromFile().readMessageFromFile();
		new MessageReaderFromFile().writeMessageFromFile();
	}
	
	private void writeMessageFromFile() {

		//preAuth-testMsg.txt, settlement-11-11-2015.txt, settlement-testMsg.txt
		int counter = 1;
		try (ObjectInputStream reader = new ObjectInputStream(
				new FileInputStream("D:\\ipage-workspace\\test-mq-messages\\settlement-testMsg.txt"))) {

			Message message = null;
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
			System.out.println("start at: " + df.format(new Date()));
			while (true) {
				try {
					message = (Message) reader.readObject();	
					MessageObject msgObject = new MessageObject(counter++, message);					
					msgObject.builder();
					
					send(msgObject, counter++);
							
					
				} catch (EOFException eof) {
					break;
				}

			}
			
			reader.close();
			System.out.println("end at: " + df.format(new Date()));

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private void send(MessageObject responseMsg, int msgNo) {

		JmsTemplate writeTemplate = (JmsTemplate) ctx.getBean("mqTestMsgWriteTemplate");
		
		writeTemplate.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) {

				try {
					System.out.println("writing message start");
					Message message = session.createTextMessage(new String(responseMsg.getMsgBody().array(), java.nio.charset.StandardCharsets.UTF_8));
					message.setJMSCorrelationID(responseMsg.getCorrelId());
					message.setJMSMessageID(StringPadder.leftPad(Integer.toString(msgNo), "0", 5));
					message.setStringProperty("JMS_IBM_Character_Set", "500");

					System.out.println("writing message end");
					return message;
					
				} catch (JMSException e) {
					e.printStackTrace();		
					return null;
				}

			}

		});

	}
}

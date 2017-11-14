package com.spring.jms;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;

import ibg.cc.mq.listener.MessageObject;
import ibg.cc.mq.writers.RequestQMessageWriter;
import ibg.common.cybersource.emsqueue.CreditCardInfo;
import ibg.common.emsqueue.dto.MQReqField;

public class MessageReader {
	private JmsTemplate jmsTemplate;
	private RequestQMessageWriter requestQMessageWriter;
	private List<Message> messageList = new LinkedList<>();

	public void browseQueue() {

		int totMsgs = jmsTemplate.browse(new BrowserCallback<Integer>() {

			@SuppressWarnings("unchecked")
			public Integer doInJms(final Session session, final QueueBrowser browser) {

				int counter = 0;
				try {

					Enumeration<?> enumeration = browser.getEnumeration();
					while (enumeration.hasMoreElements()) {
						Message message = (Message) enumeration.nextElement();

						System.out.println("reply to: " + message.getJMSReplyTo());
						counter++;
						//System.out.println("message => " + message);
						//messageList.add(message);
						//MessageObject msgObject = new MessageObject(counter, message);
						//msgObject.builder();
						//RequestService.addReq(msgObject);

						
					}
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return counter;

			}

		});

		System.out.println("totMsgs to write: " + totMsgs);
		
		if(totMsgs > 0){
			//writeMessageToFile("D:\\ipage-workspace\\test-mq-messages\\settlement-11-11-2015.txt");
			//writeMessageDetailsToFile("D:\\ipage-workspace\\mq-logs\\settlement-11-11-2015.txt");
			//writeMessageDetailsToFile("D:\\ipage-workspace\\mq-logs\\preauth-11-11-2015.txt");
		}

	}

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	@Autowired
	public void setRequestQMessageWriter(RequestQMessageWriter requestQMessageWriter) {
		this.requestQMessageWriter = requestQMessageWriter;

	}

	private void writeMessageToFile(String fileName) {

		try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(fileName, true))) {

			for(Message message: messageList){
				writer.writeObject(message);
			}
			writer.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private void readMessageFromFile(String fileName) {

		int counter = 1;
		try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(fileName))) {
			
			Message message = null;

			while(true){
				try{
					message = (Message) reader.readObject();
					MessageObject msgObject = new MessageObject(counter, message);
					msgObject.builder();
				
					System.out.println("msgObject: " + msgObject);
				}catch(EOFException eof){
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
	
	private void writeMessageDetailsToFile(String fileName) {
		
		MessageObject msgObj;
		
		
		try (BufferedWriter writer = Files.newBufferedWriter(
				FileSystems.getDefault().getPath(fileName),
				Charset.forName("US-ASCII"), StandardOpenOption.CREATE)) {

			int counter = 1;
			String data = "";
			CreditCardInfo ccInfo = null;
			for(Message message: messageList){
				MessageObject msgObject = new MessageObject(counter++, message);
				msgObject.builder();
				
				//data = new String(msgObject.getMsgBody().array(), java.nio.charset.StandardCharsets.UTF_8);
				ccInfo = new CreditCardInfo(msgObject.getMsgBody());				
				if("PreAuth".equalsIgnoreCase(ccInfo.getRequestField(MQReqField.TranType))){
					
					data = ccInfo.getRequestField(MQReqField.TieBack) + " | " + ccInfo.getRequestField(MQReqField.TranType) + " | " + 
					ccInfo.getRequestField(MQReqField.Card_Bill_To) + " | " + ccInfo.getRequestField(MQReqField.Cust_PO) + " | " + 
					ccInfo.getRequestField(MQReqField.Card_Amount) + " | " + ccInfo.getRequestField(MQReqField.Division) + "\n";
					
					writer.write(data, 0, data.length());
					
					
				}
			}
			writer.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	

	public static void main(String [] args){
		new MessageReader().readMessageFromFile("D:\\ipage-workspace\\test-mq-messages\\preAuth-testMsg.txt");
	}
}

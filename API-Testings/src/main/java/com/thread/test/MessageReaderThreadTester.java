package com.thread.test;

import java.util.LinkedList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ibg.cc.mq.listener.MessageObject;

public class MessageReaderThreadTester {

	private static boolean done = false;
	private List<MessageObject> messages = new LinkedList<>();
	private void browseQueue() {

		try {

			for(MessageObject msgObj: messages){
				RequestService.addReq(msgObj);
			}
			done = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void dummyLoader(){
		int ctr = 1;
		for(;ctr <= 3000;ctr++){
			MessageObject msgObject = new MessageObject(ctr, null);
			msgObject.setProcessTries(1);
			messages.add(msgObject);
		}
		
	}

	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("threadtest-application-context.xml");
		MessageReaderThreadTester tester = new MessageReaderThreadTester();
		tester.dummyLoader();
		tester.browseQueue();
		
		while(true){
			if(done){
				System.out.println("is request queue empty: " + RequestService.isEmpty());
				System.out.println("size of store: " + DataStore.size());
				break;
			}
		}
	}

}

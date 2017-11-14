package com.spring.jms;

import java.nio.ByteBuffer;

import javax.jms.Message;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ibg.cc.mq.listener.MessageObject;
import ibg.cc.mq.message.RequestMessage;
import ibg.cc.mq.message.ResponseMessage;
import ibg.common.cybersource.emsqueue.EMSCybersourceService;

public class TestMessageFlow {

	private static void testFlow() {
		//failed cases
		//String str = "01NV9RXJR      PreAuth   INTL            78889                                                             000041.98                                                                                                                                                                                                                                            20D5911                                                                                                ";		
		String str = "01NV9RXRB20D5911      PreAuth   INTL            78889                                                             000154.68                                                                                                                                                                                                                                            20D5911                                                                                                ";
		
		//success
		//String str = "01NV9RXRB20D5911      PreAuth   INTL            78889                         VISA                                000154.68                                                                                                                                                                                                                                            20D5911                                                        4465623808375000001518                  ";
		
		RequestMessage requestMessage = new RequestMessage();
		ResponseMessage responseMessage = null;
		EMSCybersourceService service = null;
		int msgNo = 0;
		requestMessage.setMsgBody(ByteBuffer.wrap(str.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
		requestMessage.setCorrelId("d54040404040404040404040404040404040404040404040");

		// System.out.println("CyberSource Call Starts. ");
		service = new EMSCybersourceService();
		responseMessage = service.getRequestProcessed(requestMessage);
		
	}
	
	public static void main(String [] args){
		ApplicationContext ctx = new ClassPathXmlApplicationContext("creditcard-application-context.xml");	
		
		TestMessageFlow msgFlow = new TestMessageFlow();
		msgFlow.testFlow();
	}
}

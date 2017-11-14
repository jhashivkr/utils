package com.thread.test;

import javax.jms.Message;

import ibg.cc.mq.listener.MessageObject;
import ibg.cc.mq.listener.RequestService;
import ibg.cc.mq.message.RequestMessage;
import ibg.cc.mq.message.ResponseMessage;
import ibg.cc.mq.message.TransactionMessage;
import ibg.cc.mq.writers.MessageWriter;
import ibg.common.cybersource.emsqueue.CreditCardInfo;
import ibg.common.cybersource.emsqueue.EMSCybersourceService;
import ibg.common.emsqueue.dto.MQReqField;
import ibg.cybersource.creditcard.customer.service.CreditCardCustomerProfileService;
import ibg.cybersource.creditcard.maintenance.service.MQTransactionHistoryService;
import ibg.cybersource.creditcard.services.CyberSourceCommonService;
import ibg.cybersource.service.CreditCardServiceLocator;

public class Processor<E> implements Runnable {

	private MessageObject msgObj;

	public Processor() {
	}

	public Processor(E obj) {
		msgObj = (MessageObject) obj;
	}

	public void doProcess(Message message) {
		processMessage();
	}

	public void run() {
		processMessage();
	}

	private void processMessage() {

		synchronized (this) {
			int msgNo = msgObj.getMessageSeqNo();
			
			DataStore.addObj(msgObj);

			//System.out.println("msgNo => " + msgNo);

		}

	}
}

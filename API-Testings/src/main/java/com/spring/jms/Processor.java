package com.spring.jms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import ibg.common.emsqueue.dto.MQRspField;
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
		

			ResponseMessage responseMessage = null;
			EMSCybersourceService service = null;
			int msgNo = 0;
			
			try{
				msgNo = msgObj.getMessageSeqNo();
				msgObj.setProcessTries(1);
				//msgObj.builder();

				System.out.println("CyberSource Call Starts. ");
				service = new EMSCybersourceService();				
				responseMessage = service.getRequestProcessed(msgObj);				
						
				System.out.println(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|CorrelID|Request object=>"  + msgObj.getCorrelId() + " | " + new String(msgObj.getMsgBody().array(), java.nio.charset.StandardCharsets.UTF_8));				
				System.out.println(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|CyberSource Call.ErrorList=>" + service.getResponse().getErrorMessageList());
				System.out.println(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|CyberSource Call.Status=>" + service.getResponse().getStatusCode()
						+ '|' + service.getResponse().getStatus() + '|' + service.getResponse().getStatusMessage());

			}catch(Exception e){
				//do not do anything
			}
			
			try {
				System.out.println(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|Logging into DB");
				// log the message
				TransactionMessage transactionMessage = new TransactionMessage().builder(service.getMyCCinfo(),
						service.getResponse());
				transactionMessage.setTranId(msgObj.getTranId());

				MQTransactionHistoryService mqTransactionHistoryService = (MQTransactionHistoryService) CreditCardServiceLocator
						.getService(CreditCardServiceLocator.ServiceName.MQ_TRANSACTION_HISTORY_SERVICE);
				
				mqTransactionHistoryService.recordTransaction(transactionMessage);

				System.out.println(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|Db Logging Done");
				//RequestService.addDbReq(transactionMessage);
			} catch (Exception e) {
				System.out.println(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|Exception in logging to DB");
			}		
			
			//String successfullResponseWrite = "true";
			try {
				System.out.println(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|Writing Response");
				System.out.println(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|Response Type|Message=>"
						+ responseMessage.getMessageType() + '|'
						+ new String(responseMessage.getMsgBody().array(), java.nio.charset.StandardCharsets.UTF_8));

				MessageWriter messageWriter = (MessageWriter) CreditCardServiceLocator.getBean("messageWriter");
				messageWriter.setResponseMessage(responseMessage);
				messageWriter.send(msgNo);
				
				if(null != msgObj.getTranId() && !msgObj.getTranId().isEmpty()){
					MQTransactionHistoryService mqTransactionHistoryService = (MQTransactionHistoryService) CreditCardServiceLocator
						.getService(CreditCardServiceLocator.ServiceName.MQ_TRANSACTION_HISTORY_SERVICE);
					mqTransactionHistoryService.updateMsgRespData(msgObj.getTranId(), new String(responseMessage.getMsgBody().array(), java.nio.charset.StandardCharsets.UTF_8));
				}

				System.out.println(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|Writing Response Done");
			} catch (Exception e) {
				//successfullResponseWrite = "false";
				System.out.println(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|Exception in writing response");
			}
			
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
			System.out.println("ends at: " + df.format(new Date()));

		
	}
	
	private void processMessage1() {

		try {
			// Message message = msgObj.getMessage();
			int msgNo = msgObj.getMessageSeqNo();

			msgObj.setProcessTries(1);
			msgObj.builder();

			EMSCybersourceService service = new EMSCybersourceService();
			ResponseMessage responseMessage = service.getRequestProcessed(msgObj);

			Thread.sleep(100);

			// TransactionMessage transactionMessage = new
			// TransactionMessage(service.getMyCCinfo(), service.getResponse());
			TransactionMessage transactionMessage = new TransactionMessage().builder(service.getMyCCinfo(),
					service.getResponse());
			MQTransactionHistoryService mqTransactionHistoryService = (MQTransactionHistoryService) CreditCardServiceLocator
					.getService(CreditCardServiceLocator.ServiceName.MQ_TRANSACTION_HISTORY_SERVICE);
			mqTransactionHistoryService.recordTransaction(transactionMessage);
			// RequestService.addDbReq(transactionMessage);

			Thread.sleep(100);

			MessageWriter messageWriter = (MessageWriter) CreditCardServiceLocator.getBean("messageWriter");
			messageWriter.setResponseMessage(responseMessage);
			messageWriter.send(msgNo);

			
			 //if("AuthCharge".equalsIgnoreCase(service.getMyCCinfo().getRequestField(MQReqField.TranType))){

				 String response = responseMessage.responseData();
				 System.out.println("tranType | requestMessage | response => " + service.getMyCCinfo().getRequestField(MQReqField.TranType)	+ '|' + new String(service.getMyCCinfo().getRequest().array(), java.nio.charset.StandardCharsets.UTF_8) + '|' + response.toString());
			 //}
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void check00Response(String response, RequestMessage requestMessage) {
		if (response.toString().trim().equalsIgnoreCase("00")) {
			if (response.toString().trim().contains("ERROR")) {
				CreditCardInfo myCCinfo = new CreditCardInfo(requestMessage.getMsgBody());

				String inputJSONString = new CyberSourceCommonService().getJsonStringFromCCinfo(myCCinfo).toString();
				System.out.println(
						"tranType | inputJSONString | response => " + myCCinfo.getRequestField(MQReqField.TranType)
								+ " | " + inputJSONString + "|" + response.toString());

				System.out.println("CreditCardInfo.MQRspLenV1 => " + CreditCardInfo.MQRspLenV1 + " | "
						+ new String(requestMessage.getMsgBody().array(), java.nio.charset.StandardCharsets.UTF_8)
								.trim());

			}
		}
	}
}

// RequestMessage requestMessage = new RequestMessage();
// requestMessage.setCorrelId(message.getJMSCorrelationID());
// requestMessage.setMsgId(message.getJMSMessageID());
// requestMessage.setReplyTo(message.getJMSReplyTo());
// requestMessage.setMsgBody(ByteBuffer.wrap(message.getBody(String.class).getBytes(java.nio.charset.StandardCharsets.UTF_8)));

// System.out.println("Request Message:" + new
// String(requestMessage.getMsgBody().array(),
// java.nio.charset.StandardCharsets.UTF_8));
// TransactionMessage transactionMessage = new
// TransactionMessage().builder(service.getMyCCinfo(),
// service.getResponse());
// System.out.println( "tranType | transactionMessage | response =>
// " +
// myCCinfo.getRequestField(MQReqField.TranType) + " | " +
// transactionMessage.toString() + "|" + response.toString());

// System.out.println("requestMessage: " + requestMessage);
// String inputJSONString = new
// CyberSourceCommonService().getJsonStringFromCCinfo(myCCinfo).toString();
// System.out.println("tranType | inputJSONString | response => " +
// myCCinfo.getRequestField(MQReqField.TranType) + " | " +
// inputJSONString + "|" + response.toString());
// }

// System.out.println("Response Message => " + response.toString());
// System.out.println("Message No Processed=> " + msgNo);
// System.out.println("Response Message => " +
// response.trim().length()
// + ", " + response.length() + ", " + response);
// System.out.println("Message No Processed=> " + msgNo);

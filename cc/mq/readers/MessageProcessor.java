package ibg.cc.mq.readers;

import org.apache.log4j.Logger;

import ibg.cc.mq.listener.MessageObject;
import ibg.cc.mq.message.ResponseMessage;
import ibg.cc.mq.message.TransactionMessage;
import ibg.cc.mq.writers.MessageWriter;
import ibg.common.IWatchErrorRecorder;
import ibg.common.cybersource.emsqueue.EMSCybersourceService;
import ibg.common.emsqueue.dto.MQRspField;
import ibg.cybersource.creditcard.maintenance.service.MQTransactionHistoryService;
import ibg.cybersource.service.CreditCardServiceLocator;

public class MessageProcessor<E> implements Runnable, IWatchErrorRecorder {

	private static final Logger log = Logger.getLogger(MessageProcessor.class);

	private MessageObject msgObj;

	public MessageProcessor() {
	}

	public MessageProcessor(E obj) {
		msgObj = (MessageObject) obj;
	}

	public void doProcess(E obj) {
		msgObj = (MessageObject) obj;
		processMessage();

	}

	@Override
	public void run() {
		processMessage();
	}

	private void processMessage() {

		ResponseMessage responseMessage = null;
		EMSCybersourceService service = null;
		int msgNo = 0;

		try {
			msgNo = msgObj.getMessageSeqNo();
			msgObj.setProcessTries(1);
			//msgObj.builder();

			log.info("CyberSource Call Starts. ");
			service = new EMSCybersourceService();
			responseMessage = service.getRequestProcessed(msgObj);

			log.info(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|CorrelID|Request object=>"
					+ msgObj.getCorrelId() + " | "
					+ new String(msgObj.getMsgBody().array(), java.nio.charset.StandardCharsets.UTF_8));
			log.info(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|CyberSource Call.ErrorList=>"
					+ service.getResponse().getErrorMessageList());
			log.info(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|CyberSource Call.Status=>"
					+ service.getResponse().getStatusCode() + '|' + service.getResponse().getStatus() + '|'
					+ service.getResponse().getStatusMessage());

		} catch (Exception e) {
			recordError(e);
			log.error(
					service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|Exception in Cybersoure processing", e);
			return;
		}
		
		try {
			log.info(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|Logging into DB");
			TransactionMessage transactionMessage = new TransactionMessage().builder(service.getMyCCinfo(),
					service.getResponse());
			transactionMessage.setTranId(msgObj.getTranId());
			MQTransactionHistoryService mqTransactionHistoryService = (MQTransactionHistoryService) CreditCardServiceLocator
					.getService(CreditCardServiceLocator.ServiceName.MQ_TRANSACTION_HISTORY_SERVICE);
			mqTransactionHistoryService.recordTransaction(transactionMessage);

			log.info(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|Db Logging Done");
		} catch (Exception e) {
			recordError(e);
			log.error(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|Exception in logging to DB", e);
			return;
		}

		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
		}
		try {
			log.info(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|Writing Response");
			
			if (1 == responseMessage.getMessageType()) {
				log.info(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|Response Type|Message=>"
						+ responseMessage.getMessageType() + '|'
						+ responseMessage.getSettlementResponseMessage());
			}else if (2 == responseMessage.getMessageType()) {
				log.info(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|Response Type|Message=>"
					+ responseMessage.getMessageType() + '|'
					+ new String(responseMessage.getMsgBody().array(), java.nio.charset.StandardCharsets.UTF_8));
			}

			MessageWriter messageWriter = (MessageWriter) CreditCardServiceLocator.getBean("messageWriter");
			messageWriter.setResponseMessage(responseMessage);
			messageWriter.send(msgNo);
			
			if(null != msgObj.getTranId() && !msgObj.getTranId().isEmpty()){
				MQTransactionHistoryService mqTransactionHistoryService = (MQTransactionHistoryService) CreditCardServiceLocator
					.getService(CreditCardServiceLocator.ServiceName.MQ_TRANSACTION_HISTORY_SERVICE);
				
				if (1 == responseMessage.getMessageType()) {
					mqTransactionHistoryService.updateMsgRespData(msgObj.getTranId(), responseMessage.getSettlementResponseMessage());						
				} else if (2 == responseMessage.getMessageType()) {
					mqTransactionHistoryService.updateMsgRespData(msgObj.getTranId(), new String(responseMessage.getMsgBody().array(), java.nio.charset.StandardCharsets.UTF_8));
				}				
			}

			log.info(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|Writing Response Done");
		} catch (Exception e) {
			//successfulResponseWrite = "false";
			recordError(e);
			log.error(service.getMyCCinfo().getResponseField(MQRspField.TieBack) + "|Exception in writing response", e);
			return;
		}

		try {
			Thread.sleep(1000);  // Added to reduce the speed of Cybersource Calls.
		} catch (InterruptedException e1) {}
		

	}

	@Override
	public void recordError(Throwable e) {
	}
}

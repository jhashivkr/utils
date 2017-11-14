package ibg.cc.mq.listener;

import java.nio.ByteBuffer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.springframework.jms.listener.SessionAwareMessageListener;

import com.ibm.jms.JMSTextMessage;

import ibg.common.IWatchErrorRecorder;
import ibg.cybersource.creditcard.maintenance.service.MQTransactionHistoryService;
import ibg.cybersource.service.CreditCardServiceLocator;

public class MessageListener implements SessionAwareMessageListener<Message>, IWatchErrorRecorder {
	private static final Logger log = Logger.getLogger(MessageListener.class);
	private int msgSeqNo = 1;

	@Override
	public void onMessage(Message message, Session session) {

		try {
			log.info("Received message: " + message.getJMSMessageID() + ":" + message.getJMSTimestamp());
			logMessageDetails(message);

			
			MessageObject msgObject = new MessageObject(msgSeqNo, message);
			msgObject.builder();
			
			//store the message in DB and update the MessageObject with the transaction id
			if(null != msgObject){
				MQTransactionHistoryService mqTransactionHistoryService = (MQTransactionHistoryService) CreditCardServiceLocator
					.getService(CreditCardServiceLocator.ServiceName.MQ_TRANSACTION_HISTORY_SERVICE);
				String tranId = mqTransactionHistoryService.recordNewMessage(msgObject);
				msgObject.setTranId(tranId);
			}
			
			RequestService.addReq(msgObject);

			msgSeqNo++;
		} catch (Exception e) {
			log.error("Failed to process message", e);
			recordError(e);
		}

	}

	private void logMessageDetails(Message message) throws JMSException {
		TextMessage textMessage = null;
		if (message instanceof JMSTextMessage) {
			textMessage = ((JMSTextMessage) message);
		} else if (message instanceof TextMessage) {
			textMessage = ((TextMessage) message);
		}
		long msgTimeStamp = textMessage.getJMSTimestamp();
		//log.info("Received message timestamped " + msgTimeStamp + ": " + (ByteBuffer.wrap(textMessage.getText().getBytes(java.nio.charset.StandardCharsets.UTF_8))));
		log.info("Received message timestamped " + msgTimeStamp + ": " + textMessage.getText());
	}

	@Override
	public void recordError(Throwable e) {
	}

}
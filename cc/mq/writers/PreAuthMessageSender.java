package ibg.cc.mq.writers;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.ibm.mq.MQQueueManager;

import ibg.cc.mq.message.ResponseMessage;

public class PreAuthMessageSender {

	private List<JmsTemplate> writers;

	private String responseMsg;
	private static final String ZEROPAD = "0";

	private int rand = 0;

	public PreAuthMessageSender() {
	}

	public PreAuthMessageSender(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	/**
	 * @param responseMsg
	 *            the responseMsg to set
	 */
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	public void send() {
		writers.get(rand).send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(responseMsg);
			}
		});
	}

	public void send(ResponseMessage responseMsg, int msgNo) {

		try {
			rand = msgNo % 2;
		} catch (Exception e) {
			rand = 1;
		}
		
		JmsTemplate writeTemplate = writers.get(rand);
		
		writers.get(rand).send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) {

				try {
					Message message = session.createTextMessage(responseMsg.responseData());
					message.setJMSCorrelationID(responseMsg.getCorrelId());

					message.setJMSMessageID(StringPadder.leftPad(Integer.toString(msgNo), ZEROPAD, 5));

					message.setStringProperty("JMS_IBM_Character_Set", "500");

					return message;
				} catch (JMSException e) {
					e.printStackTrace();
					// try to write on the other response queue
					return tryOneMoreSend(responseMsg, rand);
				}

			}

		});

	}

	public Message tryOneMoreSend(ResponseMessage responseMsg, int msgNo) {

		Message message = null;
		rand = (msgNo == 0) ? 1 : 0;
		writers.get(rand).send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) {
				Message message = null;
				try {
					message = session.createTextMessage(responseMsg.responseData());
					message.setJMSCorrelationID(responseMsg.getCorrelId());

					message.setJMSMessageID(StringPadder.leftPad(Integer.toString(msgNo), ZEROPAD, 5));

					message.setStringProperty("JMS_IBM_Character_Set", "500");

					return message;
				} catch (JMSException e) {
					e.printStackTrace();
					return message;
				}

			}

		});
		return message;
	}

	/**
	 * @return the writers
	 */
	public List<JmsTemplate> getWriters() {
		return writers;
	}

	/**
	 * @param writers
	 *            the writers to set
	 */
	public void setWriters(List<JmsTemplate> writers) {
		this.writers = writers;
	}

}

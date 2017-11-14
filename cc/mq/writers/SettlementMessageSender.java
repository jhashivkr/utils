package ibg.cc.mq.writers;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.ibm.mq.MQMessage;

import ibg.cc.mq.message.ResponseMessage;

public class SettlementMessageSender {

	@Autowired
	private JmsTemplate settlementWriteJmsTemplate1;

	@Autowired
	private JmsTemplate settlementWriteJmsTemplate2;

	private List<JmsTemplate> writers;

	private int rand = 0;

	private static final String ZEROPAD = "0";

	private String responseMsg;

	public SettlementMessageSender() {
	}

	public SettlementMessageSender(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	/**
	 * @param responseMsg
	 *            the responseMsg to set
	 */
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	/**
	 * @param settlementWriteJmsTemplate1
	 *            the settlementWriteJmsTemplate1 to set
	 */

	public void setSettlementWriteJmsTemplate1(JmsTemplate settlementWriteJmsTemplate1) {
		this.settlementWriteJmsTemplate1 = settlementWriteJmsTemplate1;
	}

	/**
	 * @param settlementWriteJmsTemplate2
	 *            the settlementWriteJmsTemplate2 to set
	 */
	@Autowired
	public void setSettlementWriteJmsTemplate2(JmsTemplate settlementWriteJmsTemplate2) {
		this.settlementWriteJmsTemplate2 = settlementWriteJmsTemplate2;
	}

	public void send() {
		settlementWriteJmsTemplate1.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {

				return session.createTextMessage(responseMsg);
			}

		});
	}

	public void send(ResponseMessage responseMsg, int msgNo) {

		rand = msgNo % 2;
		writers.get(rand).send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) {

				try {
					Message message = session.createTextMessage(responseMsg.getSettlementResponseMessage());
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
					message = session.createTextMessage(responseMsg.getSettlementResponseMessage());
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

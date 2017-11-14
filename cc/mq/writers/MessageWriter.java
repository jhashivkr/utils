package ibg.cc.mq.writers;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import ibg.cc.mq.message.ResponseMessage;
import ibg.common.IWatchErrorRecorder;

public class MessageWriter implements IWatchErrorRecorder {

	private JmsTemplate jmsTemplate;
	private Destination replyTo;
	private String responseMsg;
	private ResponseMessage responseMessage;
	
	private PreAuthMessageSender preAuthMessageSender;
	private SettlementMessageSender settlementMessageSender;

	public MessageWriter() {
	}

	public MessageWriter(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}

	public MessageWriter(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public MessageWriter(JmsTemplate jmsTemplate, Destination replyTo) {
		this.jmsTemplate = jmsTemplate;
		this.replyTo = replyTo;
	}

	public MessageWriter(JmsTemplate jmsTemplate, Destination replyTo, String responseMsg) {
		this.jmsTemplate = jmsTemplate;
		this.replyTo = replyTo;
		this.responseMsg = responseMsg;
	}

	/**
	 * @param jmsTemplate
	 *            the jmsTemplate to set
	 */
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	/**
	 * @param replyTo
	 *            the replyTo to set
	 */
	public void setReplyTo(Destination replyTo) {
		this.replyTo = replyTo;
	}

	/**
	 * @param responseMsg
	 *            the responseMsg to set
	 */
	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
	
	/**
	 * @param responseMessage the responseMessage to set
	 */
	public void setResponseMessage(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}

	public void sendMessage() {
		try {
			this.jmsTemplate.send(new MessageCreator() {
				public Message createMessage(Session session) throws JMSException {
					try{
						session.createProducer(replyTo);
						return session.createTextMessage(responseMsg);
					} catch (RuntimeException re){
						recordError(re);
						throw re;
					}
				}
			});
		} catch (RuntimeException e){
			recordError(e);
			throw e;
		}
	}

	public void send(int msgNo) {
		try{
			if (1 == responseMessage.getMessageType()) {
				
				settlementMessageSender.send(responseMessage, msgNo);
			} else if (2 == responseMessage.getMessageType()) {
				preAuthMessageSender.send(responseMessage, msgNo);
			}
		} catch (RuntimeException e){
			recordError(e);
			throw e;
		}

	}
	
	@Autowired
	public void setPreAuthMessageSender(PreAuthMessageSender preAuthMessageSender){
		this.preAuthMessageSender = preAuthMessageSender;
		
	}
	
	@Autowired
	public void setSettlementMessageSender(SettlementMessageSender settlementMessageSender){
		this.settlementMessageSender = settlementMessageSender;
		
	}

	@Override
	public void recordError(Throwable e) {
	}
}

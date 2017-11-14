package ibg.cc.mq.listener;

import java.nio.ByteBuffer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import com.ibm.jms.JMSTextMessage;

import ibg.cc.mq.message.RequestMessage;

public class MessageObject extends RequestMessage {

	private int messageSeqNo;
	private int processTries;
	private Long msgTimeStamp;
	private Integer msgDeliveryCount;
	private String msgCharacterSet;
	private String msgCharacterEncoding;
	private String msgType;
	private String tranId;

	private Message message;

	public MessageObject(int messageSeqNo, Message message) {
		super();
		this.messageSeqNo = messageSeqNo;
		this.message = message;
	}

	public MessageObject() {
		super();
	}

	public void builder() {

		try {
			setCorrelId(message.getJMSCorrelationID());
			setMsgId(message.getJMSMessageID());
			setReplyTo(message.getJMSReplyTo());

			TextMessage textMessage = null;
			if (message instanceof JMSTextMessage) {
				textMessage = ((JMSTextMessage) message);
			} else if (message instanceof TextMessage) {
				textMessage = ((TextMessage) message);
			}

			msgTimeStamp = textMessage.getJMSTimestamp();
			msgDeliveryCount = textMessage.getIntProperty("JMSXDeliveryCount");
			msgCharacterSet = textMessage.getStringProperty("JMS_IBM_Character_Set");
			msgCharacterEncoding = textMessage.getStringProperty("JMS_IBM_Encoding");
			msgType = textMessage.getStringProperty("JMS_IBM_MsgType");
			setMsgBody(ByteBuffer.wrap(textMessage.getText().getBytes(java.nio.charset.StandardCharsets.UTF_8)));

		} catch (JMSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/**
	 * @return the messageSeqNo
	 */
	public int getMessageSeqNo() {
		return messageSeqNo;
	}

	/**
	 * @return the processTries
	 */
	public int getProcessTries() {
		return processTries;
	}

	/**
	 * @return the msgTimeStamp
	 */
	public Long getMsgTimeStamp() {
		return msgTimeStamp;
	}

	/**
	 * @return the msgDeliveryCount
	 */
	public Integer getMsgDeliveryCount() {
		return msgDeliveryCount;
	}

	/**
	 * @return the msgCharacterSet
	 */
	public String getMsgCharacterSet() {
		return msgCharacterSet;
	}

	/**
	 * @return the msgCharacterEncoding
	 */
	public String getMsgCharacterEncoding() {
		return msgCharacterEncoding;
	}

	/**
	 * @return the msgType
	 */
	public String getMsgType() {
		return msgType;
	}

	/**
	 * @param processTries
	 *            the processTries to set
	 */
	public void setProcessTries(int processTries) {
		this.processTries = processTries;
	}	

	/**
	 * @return the tranId
	 */
	public String getTranId() {
		return tranId;
	}

	/**
	 * @param tranId the tranId to set
	 */
	public void setTranId(String tranId) {
		this.tranId = tranId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getCorrelId().hashCode();
		result = prime * result + getMsgId().hashCode();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageObject other = (MessageObject) obj;

		if (null != getMsgId() && !getMsgId().equalsIgnoreCase(other.getMsgId())) {
			return false;
		}
		if (null != getCorrelId() && !getCorrelId().equalsIgnoreCase(other.getCorrelId())) {
			return false;
		}

		if (messageSeqNo != other.messageSeqNo)
			return false;
		if (processTries != other.processTries)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MessageObject [messageSeqNo=" + messageSeqNo + ", processTries=" + processTries + ", msgTimeStamp="
				+ msgTimeStamp + ", msgDeliveryCount=" + msgDeliveryCount + ", msgCharacterSet=" + msgCharacterSet
				+ ", msgCharacterEncoding=" + msgCharacterEncoding + ", msgType=" + msgType + ", getReplyTo()="
				+ getReplyTo() + ", getMsgId()=" + getMsgId() + ", getCorrelId()=" + getCorrelId() + ", getMsgBody()="
				+ getMsgBody() + ", toString()=" + super.toString() + ", getClass()=" + getClass() + "]";
	}

}

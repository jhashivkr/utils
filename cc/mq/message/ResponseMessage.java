package ibg.cc.mq.message;

import java.nio.ByteBuffer;

import javax.jms.Destination;

public class ResponseMessage {
	private Destination replyTo;
	private String msgId;
	private String correlId;
	private ByteBuffer msgBody;
	private Integer messageType;
	private String settlementResponseMessage;

	/**
	 * @return the replyTo
	 */
	public Destination getReplyTo() {
		return replyTo;
	}

	/**
	 * @param replyTo
	 *            the replyTo to set
	 */
	public void setReplyTo(Destination replyTo) {
		this.replyTo = replyTo;
	}

	/**
	 * @return the msgId
	 */
	public String getMsgId() {

		return msgId.trim();

	}

	/**
	 * @param msgId
	 *            the msgId to set
	 */
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	/**
	 * @return the correlId
	 */
	public String getCorrelId() {

		return correlId.trim();

	}

	/**
	 * @param correlId
	 *            the correlId to set
	 */
	public void setCorrelId(String correlId) {
		this.correlId = correlId;
	}

	/**
	 * @return the msgBody
	 */
	public ByteBuffer getMsgBody() {
		return msgBody;
	}

	/**
	 * @param msgBody
	 *            the msgBody to set
	 */
	public void setMsgBody(ByteBuffer msgBody) {
		this.msgBody = msgBody;
	}

	/**
	 * @return the messageType
	 */
	public Integer getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType
	 *            the messageType to set
	 */
	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}

	/**
	 * @return the settlementResponseMessage
	 */
	public String getSettlementResponseMessage() {
		return settlementResponseMessage;
	}

	/**
	 * @param settlementResponseMessage the settlementResponseMessage to set
	 */
	public void setSettlementResponseMessage(String settlementResponseMessage) {
		this.settlementResponseMessage = settlementResponseMessage;
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
		result = prime * result + ((correlId == null) ? 0 : correlId.hashCode());
		result = prime * result + ((messageType == null) ? 0 : messageType.hashCode());
		result = prime * result + ((msgId == null) ? 0 : msgId.hashCode());
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
		ResponseMessage other = (ResponseMessage) obj;
		if (correlId == null) {
			if (other.correlId != null)
				return false;
		} else if (!correlId.equals(other.correlId))
			return false;
		if (messageType == null) {
			if (other.messageType != null)
				return false;
		} else if (!messageType.equals(other.messageType))
			return false;
		if (msgId == null) {
			if (other.msgId != null)
				return false;
		} else if (!msgId.equals(other.msgId))
			return false;
		return true;
	}

	public String responseData() {

		return new String(msgBody.array(), java.nio.charset.StandardCharsets.UTF_8).trim();
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResponseMessage [replyTo=");
		builder.append(replyTo);
		builder.append(", msgId=");
		builder.append(msgId);
		builder.append(", correlId=");
		builder.append(correlId);
		
		if(null == settlementResponseMessage || settlementResponseMessage.isEmpty()){
			builder.append(", msgBody=");
			builder.append(msgBody);
		}else{
			builder.append(", msgBody=");
			builder.append(settlementResponseMessage);
		}
		builder.append(", messageType=");
		builder.append(messageType);
		builder.append(", settlementResponseMessage=");
		builder.append(settlementResponseMessage);
		builder.append("]");
		return builder.toString();
	}

	@Deprecated
	private static String asciiToHex(String asciiValue) {
		char[] chars = asciiValue.toCharArray();
		StringBuffer hex = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			hex.append(Integer.toHexString((int) chars[i]));
		}
		return hex.toString();
	}

	@Deprecated
	private static String hexToASCII(String hexValue) {
		StringBuilder output = new StringBuilder("");
		for (int i = 0; i < hexValue.length(); i += 2) {
			String str = hexValue.substring(i, i + 2);
			output.append((char) Integer.parseInt(str, 16));
		}
		return output.toString();
	}

}

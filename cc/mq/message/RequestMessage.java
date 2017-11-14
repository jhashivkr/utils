package ibg.cc.mq.message;

import java.nio.ByteBuffer;

import javax.jms.Destination;

public class RequestMessage {

	private Destination replyTo;
	private String msgId;
	private String correlId;
	private ByteBuffer msgBody;

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
		return msgId;
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
		return correlId;
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
		result = prime * result + ((msgId == null) ? 0 : msgId.hashCode());
		result = prime * result + ((replyTo == null) ? 0 : replyTo.hashCode());
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
		RequestMessage other = (RequestMessage) obj;
		if (correlId == null) {
			if (other.correlId != null)
				return false;
		} else if (!correlId.equals(other.correlId))
			return false;
		if (msgId == null) {
			if (other.msgId != null)
				return false;
		} else if (!msgId.equals(other.msgId))
			return false;
		if (replyTo == null) {
			if (other.replyTo != null)
				return false;
		} else if (!replyTo.equals(other.replyTo))
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

		return "RequestMessage [replyTo=" + replyTo + ", msgId=" + msgId + ", correlId=" + correlId + ", msgBody="
				+ new String(msgBody.array(), java.nio.charset.StandardCharsets.UTF_8) + "]";
	}

}

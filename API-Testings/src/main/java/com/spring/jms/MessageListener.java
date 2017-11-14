package com.spring.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.listener.SessionAwareMessageListener;

import com.cc.log.analyze.RequestService;

import ibg.cc.mq.listener.MessageObject;

public class MessageListener implements SessionAwareMessageListener<Message> {
	private int msgSeqNo = 1;

	@Override
	public void onMessage(Message message, Session session) throws JMSException {

		try {
			MessageObject msgObject = new MessageObject(msgSeqNo, message);
			RequestService.addReq(msgObject);

			//MQThreadExecutor.getReqFutures().add(
				//	(Future<Runnable>) MQThreadExecutor.getqExecutor().submit(new Processor<MessageObject>(msgObject)));

			msgSeqNo++;
		} catch (Exception e) {
			e.printStackTrace();
		}

		msgSeqNo++;

	}

}
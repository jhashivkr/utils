package com.cc.log.analyze;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

import com.spring.jms.Processor;

import ibg.cc.mq.listener.MQThreadExecutor;
import ibg.cc.mq.listener.MessageObject;
import ibg.lib.activity.ReqCallback;

public class LogDataQueue<E> {

	private BlockingQueue<E> dataReqQ = new ArrayBlockingQueue<E>(1000000);

	public final void addRequest(final E reqObj) {
		try {
			dataReqQ.put(reqObj);

			@SuppressWarnings("unchecked")
			ReqCallback reqCallback = () -> {

				try {
					MQThreadExecutor.getReqFutures().add((Future<Runnable>) MQThreadExecutor.getqExecutor()
							.submit(new Processor<MessageObject>((MessageObject) dataReqQ.take())));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};

			reqCallback.executeNewTask();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	protected final <E> E getRequest() {
		try {
			return (E) dataReqQ.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return (E) new Object();
	}

	protected final boolean hasRequestObj() {
		return dataReqQ.isEmpty();
	}
}

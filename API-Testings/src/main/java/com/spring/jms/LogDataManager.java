package com.spring.jms;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class LogDataManager<E> {
	private BlockingQueue<E> logLine = new ArrayBlockingQueue<E>(1000000);

	public final void addRequest(final E reqObj) {
		try {
			logLine.put(reqObj);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "hiding" })
	protected final <E> E getRequest() {
		try {
			return (E) logLine.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return (E) new Object();
		}
		
	}

	protected final boolean hasRequestObj() {
		return logLine.isEmpty();
	}

}

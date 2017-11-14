package com.thread.test;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import ibg.cc.mq.listener.MessageObject;

public enum RequestQueueInterface implements Serializable {

	MQReadQueue("reqQ") {
		@SuppressWarnings("unchecked")
		@Override
		public <T> DataQueueTest<T> getData() {
			// return (DataRequestQueue<T>)weakReqQ.get();
			return (DataQueueTest<T>) reqQ;
		}
	};

	private RequestQueueInterface(String type) {
		this.type = type;
	}

	public abstract <T> DataQueueTest<T> getData();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static DataQueueTest<? extends MessageObject> reqQ = new DataQueueTest();
	private static WeakReference<DataQueueTest<? extends MessageObject>> weakReqQ = new WeakReference<DataQueueTest<? extends MessageObject>>(
			reqQ);

	private String type;

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "TYPE CODE -> " + type;
	}

}

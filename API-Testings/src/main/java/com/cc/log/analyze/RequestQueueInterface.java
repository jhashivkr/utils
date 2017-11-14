package com.cc.log.analyze;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import com.cc.log.obj.LogMqData;

import ibg.cc.mq.listener.MessageObject;

public enum RequestQueueInterface implements Serializable {

	MQReadQueue("reqQ") {
		@SuppressWarnings("unchecked")
		@Override
		public <T> DataQueueTest<T> getData() {
			return (DataQueueTest<T>)weakReqQ.get();
			//return (DataQueueTest<T>) reqQ;
		}
		
		public <T> LogDataMap<T> getLogData(){
			return null;
		}
	},
	
	LogMap("log") {
		@SuppressWarnings("unchecked")
		@Override
		public <T> LogDataMap<T> getLogData() {
			return (LogDataMap<T>) weakLogDataMap.get();
		}
		
		public <T> DataQueueTest<T> getData() {
			return null;
		}
	};

	private RequestQueueInterface(String type) {
		this.type = type;
	}

	public abstract <T> DataQueueTest<T> getData();
	public abstract <T> LogDataMap<T> getLogData();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static DataQueueTest<? extends MessageObject> reqQ = new DataQueueTest();
	private static WeakReference<DataQueueTest<? extends MessageObject>> weakReqQ = new WeakReference<DataQueueTest<? extends MessageObject>>(reqQ);
	

	private static LogDataMap<LogMqData> logDataMap = new LogDataMap<>();
	private static WeakReference<LogDataMap<LogMqData>> weakLogDataMap = new WeakReference<LogDataMap<LogMqData>>(logDataMap);

	private String type;

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "TYPE CODE -> " + type;
	}

}

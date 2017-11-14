package com.cc.log.analyze;

import java.io.Serializable;
import java.util.Map;

import com.cc.log.analyze.RequestQueueInterface;
import com.cc.log.obj.LogMqData;

public class RequestService implements Serializable {

	private static final long serialVersionUID = 5188649331096300039L;

	public static final <E> void addReq(E req) {
		(RequestQueueInterface.MQReadQueue).getData().addRequest(req);	
	}

	public static final <E> E getReq() {
		return (RequestQueueInterface.MQReadQueue).getData().getRequest();
	}

	public static final boolean isEmpty() {
		return (RequestQueueInterface.MQReadQueue).getData().hasRequestObj();
	}
	
	
	public static final <E> void addLog(Object key, E log) {
		(RequestQueueInterface.LogMap).getLogData().putContent(key, log);
	}
	
	public static final Map<Object, LogMqData> getLog() {
		return (Map<Object, LogMqData>) RequestQueueInterface.LogMap.getLogData().getLogData();
	}

	@SuppressWarnings("unchecked")
	public static final <E> E getLogContent(Object key) {
		return (E) (RequestQueueInterface.LogMap).getLogData().getContent(key);
	}
	
	public static final boolean doesContains(Object key) {
		return (RequestQueueInterface.LogMap).getLogData().doesContains(key);
	}
	
	@SuppressWarnings("unchecked")
	public static final <E> E removeContent(Object key) {
		return (E) (RequestQueueInterface.LogMap).getLogData().removeContent(key);
	}

	public static final boolean isLogEmpty() {
		return (RequestQueueInterface.LogMap).getLogData().isLogEmpty();
	}

}

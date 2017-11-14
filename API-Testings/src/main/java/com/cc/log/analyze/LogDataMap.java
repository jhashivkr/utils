package com.cc.log.analyze;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.annotation.GuardedBy;
import org.apache.http.annotation.ThreadSafe;

import com.cc.log.obj.LogMqData;

@ThreadSafe
public class LogDataMap<T> {

	@GuardedBy("putLock")
	private Map<Object, T> ccLogData = new ConcurrentHashMap<>();

	private final Object putLock = new Object();

	public void putContent(Object key, T t) {
		synchronized (putLock) {
			ccLogData.put(key, t);
			putLock.notifyAll();
		}
	}

	public T removeContent(Object key) {
		synchronized (putLock) {
			return ccLogData.remove(key);
		}
	}

	public T getContent(Object key) {
		return ccLogData.get(key);
	}
	
	public boolean doesContains(Object key) {
		return ccLogData.containsKey(key);
	}

	public boolean isLogEmpty() {
		return ccLogData.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public Map<Object, LogMqData> getLogData() {
		
		Map<Object, LogMqData> logDataCopy = new LinkedHashMap<>();
		synchronized (putLock) {
			logDataCopy.putAll((Map<? extends Object, ? extends LogMqData>) ccLogData);
			putLock.notifyAll();
		}

		return logDataCopy;
	}
}

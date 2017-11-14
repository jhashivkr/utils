package ibg.cc.mq.listener;

import java.io.Serializable;

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
	
	public static final <E> void addDbReq(E req) {
		(RequestQueueInterface.TranDbQueue).getDbData().addRequest(req);	
	}

	public static final <E> E getDbReq() {
		return (RequestQueueInterface.TranDbQueue).getDbData().getRequest();
	}

	public static final boolean isDbQEmpty() {
		return (RequestQueueInterface.TranDbQueue).getDbData().hasRequestObj();
	}

}

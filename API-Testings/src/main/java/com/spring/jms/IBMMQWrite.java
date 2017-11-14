package com.spring.jms;

import javax.jms.DeliveryMode;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;

public class IBMMQWrite {

	private final static String username="eceditst";
	private final static String password="";
	private final static String transportType="1";

	private final static String manager1="MQISGT17";
	private final static String channel1="MQISGT17.SVRCONN";
	private final static String conn1="IBAS4017.ingramcontent.com(8950)";
	private final static String host1="IBAS4017.ingramcontent.com";

	private final static String manager2="MQISGT15";
	private final static String channel2="MQISGT15.SVRCONN";
	private final static String conn2="IBAS4015.ingramcontent.com(8950)";
	private final static String host2="IBAS4015.ingramcontent.com";

	private final static String port="8950";

	private final static String readQueue = "IBCC.OUTBOX.BASE.DESTQ1";
	private final static String preauth="IBOM.RTQ.ALIAS.DESTQ1";
	private final static String settlement="IBFN.RTQ.ALIAS.DESTQ1";

	public void sendMsg(String msg) {
		MQQueueConnectionFactory connectionFactory = null;
		QueueConnection queueConn = null;
		QueueSession queueSession = null;
		QueueSender queueSender = null;
		TextMessage message = null;

		try {
					
			// Create a connection factory

			connectionFactory = new MQQueueConnectionFactory();

			// setting up the connection factory parameters

			connectionFactory.setHostName(host2);
			connectionFactory.setPort(new Integer(port));
			connectionFactory.setTransportType(WMQConstants.WMQ_CLIENT_NONJMS_MQ);
			connectionFactory.setQueueManager(manager2);
			connectionFactory.setChannel(channel2);

			try{
				//creating connection
				queueConn = connectionFactory.createQueueConnection(username, password);
			}catch(Exception e){
				e.printStackTrace();
			}
					

			// creating session
			queueSession = queueConn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

			// creating Queue Sender
			queueSender = queueSession.createSender(queueSession.createQueue(settlement));

			// setting the delivery mode. By default Delivery Mode is persistent.
			queueSender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			// creating text message
			message = queueSession.createTextMessage(msg);
			
			// sending message
			queueSender.send(message);

			// closing connection
			queueConn.close();

		} catch (Exception je) {
			je.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		System.out.println("Attempting write");
		String message = "01DD20H496358L4S  DAACAuthchargsINTL            A                          100       ACCEPT                        4473034659725000001357                                                            MAST      ";
		//String message = "01CI20P27851NGDT  DAACSale      INTL            127208267                     VISA                                000041.21000000.00                                                                                                                                                                                                                                   20P2785     000010.16                                          3830815182470176056425                  ";
		new IBMMQWrite().sendMsg(message);
		System.out.println("write done");
	}
}

package ibg.cc.mq.readers;

import java.nio.ByteBuffer;
import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.Message;

public class MessageContentReader {

	public static void getMessageDetails(Message message, int msgCtr) {

		System.out.println("****mqMessage descriptor****\n");
		System.out.println(" mqMessage Number: " + msgCtr);

		Enumeration<?> msgProperties;
		try {
			msgProperties = message.getPropertyNames();
			// while(msgProperties.hasMoreElements()){
			// Object msgProperty = msgProperties.nextElement();
			// System.out.println(msgProperty + ":" +
			// message.getStringProperty((String) msgProperty));
			// }
			
			System.out.println("replyTo:" + message.getJMSReplyTo());
			ByteBuffer messageBody = ByteBuffer.wrap(message.getBody(String.class).getBytes(java.nio.charset.StandardCharsets.UTF_8));
			String v = new String( messageBody.array(), java.nio.charset.StandardCharsets.UTF_8);
			System.out.println("body:" + v);

			
			

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println();
		System.out.println();
	}

}

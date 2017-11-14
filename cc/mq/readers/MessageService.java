package ibg.cc.mq.readers;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;


import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * A service that sends and receives JMS messages.
 * 
 */
public class MessageService implements MessageListener {
	
	private static ClassPathXmlApplicationContext context = null;

    public void onMessage(Message message) {
       try {
          if(message instanceof TextMessage) {
             System.out.println(this + " : " + ((TextMessage) message).getText());
          }

       } catch (JMSException ex){
          throw new RuntimeException(ex);
       }
    }

	public static void main(String[] args) {

		context = new ClassPathXmlApplicationContext("spring-jms.xml");
		new MessageService();
		
	}
}
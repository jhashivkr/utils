package ibg.cc.mq.readers;

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;

import ibg.cc.mq.listener.MessageObject;
import ibg.cc.mq.listener.RequestService;

public class MessageBrowser {
	private JmsTemplate jmsTemplate;

	public void browseQueue() {

		int totMsgs = jmsTemplate.browse(new BrowserCallback<Integer>() {

			@Override
			public Integer doInJms(final Session session, final QueueBrowser browser) {

				int counter = 0;
				try {
					Enumeration<?> enumeration = browser.getEnumeration();
					while (enumeration.hasMoreElements()) {
						Message message = (Message) enumeration.nextElement();
						int msgNo = counter;
						
						MessageObject msgObject = new MessageObject(msgNo, message);
						msgObject.setProcessTries(1);
						msgObject.builder();
						
						RequestService.addReq(msgObject);
						
						//new Thread(() -> new MessageProcessor<MessageObject>().doProcess(msgObject)).start();
												
						counter++;
						
						//if(counter > 0){
						//	return counter;
						//}
					}
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return counter;

			}

		});

		System.out.println("totMsgs: " + totMsgs);

	}

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

}

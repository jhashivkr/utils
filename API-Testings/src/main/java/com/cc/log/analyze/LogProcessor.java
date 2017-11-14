package com.cc.log.analyze;

import java.util.Map;

import com.cc.log.obj.LogMqData;

public class LogProcessor implements Runnable {

	@Override
	public void run() {

		// for (;;) {
		// check if any new content is there on the map
		if (!RequestService.isLogEmpty()) {
			try {

				try {

					Map<Object, LogMqData> logData = RequestService.getLog();

					if (RequestService.isLogEmpty()) {
						System.out.println("returning");
						return;
					}
					System.out.println("logData: " + logData.size());

					for (Object key : logData.keySet()) {
						LogMqData logMqData = logData.get(key);
						if (logMqData.isDbLoggingStatus()) {
							
							//System.out.println("logMqData => " + logData.get(key));
							// remove this key from the original map
							RequestService.removeContent(key);
						} else {
							//System.out.println("logMqData => " + logMqData);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					// break;
				}
				Thread.sleep(2000);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// }

	}

	

}

/**
 * @#GanimedeExecutor.java
 */
package com.cc.log.analyze;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * This class is responsible to run threads and call ganimede service
 * 
 * @author synechron
 * 
 */
public final class LogThreadExecutor implements ApplicationListener<ContextRefreshedEvent> {

	private static ScheduledExecutorService logProcessExecutor = null;
	//private static ExecutorService logProcessExecutor = null;
	private static ExecutorService dbProcessExecutor = null;
	private static ExecutorService purgeExecutor = null;
	private final int NTHREDS;

	private int initialDelay = 10; // actual value comes from spring context
	private int period = 10; // actual value comes from spring context

	private static List<Future<Runnable>> reqFutures = new LinkedList<Future<Runnable>>();
	private static List<Future<Runnable>> dbFutures = new LinkedList<Future<Runnable>>();

	final Logger logger = LoggerFactory.getLogger(LogThreadExecutor.class);

	public LogThreadExecutor(int nthreads, int initialDelay, int period) {
		NTHREDS = nthreads;
		this.initialDelay = initialDelay;
		this.period = period;
	}

	public void start() {
		
		logProcessExecutor = Executors.newScheduledThreadPool(NTHREDS);		
		//logProcessExecutor = Executors.newFixedThreadPool(NTHREDS);
		dbProcessExecutor = Executors.newFixedThreadPool(NTHREDS);
		//purgeExecutor = Executors.newSingleThreadScheduledExecutor();
		
		
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		start();
	}

	public static ExecutorService getPurgeExecutor() {
		return purgeExecutor;
	}

	//public static ExecutorService getqExecutor() {
	//	return logProcessExecutor;
	//}
	
	public static ScheduledExecutorService getqExecutor() {
		return logProcessExecutor;
	}	

	public static void stopqExecutor() {
		logProcessExecutor.shutdown();
		try {
			if (!logProcessExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
				logProcessExecutor.shutdownNow();
				if (!logProcessExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
				}
			}
		} catch (InterruptedException ie) {
			logProcessExecutor.shutdownNow();
			Thread.currentThread().interrupt();
		} finally {
			if (!logProcessExecutor.isShutdown() || !logProcessExecutor.isTerminated()) {
				logProcessExecutor = null;
			}
		}
	}

	public static ExecutorService getDbExecutor() {
		return dbProcessExecutor;
	}

	public static void stopDbExecutor() {
		dbProcessExecutor.shutdown();
		try {
			if (!dbProcessExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
				dbProcessExecutor.shutdownNow();
				if (!dbProcessExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
				}
			}
		} catch (InterruptedException ie) {
			dbProcessExecutor.shutdownNow();
			Thread.currentThread().interrupt();
		} finally {
			if (!dbProcessExecutor.isShutdown() || !dbProcessExecutor.isTerminated()) {
				dbProcessExecutor = null;
			}
		}
	}

	public static List<Future<Runnable>> getReqFutures() {
		return reqFutures;
	}

	public static List<Future<Runnable>> getDbFutures() {
		return dbFutures;
	}

	// Active threads in pool
	void shutdownAndAwaitTermination(ExecutorService executor) {
		executor.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
				executor.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
					// logger.error("Pool1 did not terminate");
				}
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			executor.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

}

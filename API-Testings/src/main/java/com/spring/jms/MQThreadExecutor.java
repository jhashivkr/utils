/**
 * @#GanimedeExecutor.java
 */
package com.spring.jms;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import ibg.common.VariableData;

/**
 * This class is responsible to run threads and call ganimede service
 * 
 * @author synechron
 * 
 */
public final class MQThreadExecutor implements ApplicationListener<ContextRefreshedEvent> {

	private static ExecutorService qExecutor = null;
	private static ExecutorService dbExecutor = null;
	private static ExecutorService purgeExecutor = null;
	private static int threadPoolSize;
	private static final int threadBufferSize = 2;

	private static List<Future<Runnable>> reqFutures = new LinkedList<Future<Runnable>>();
	private static List<Future<Runnable>> dbFutures = new LinkedList<Future<Runnable>>();

	private static final Logger log = LoggerFactory.getLogger(MQThreadExecutor.class);

	public MQThreadExecutor(int nthreads, int initialDelay, int period) {
		threadPoolSize = nthreads;
	}

	public void start() {

		log.info("Starting CC listener thread pool");
		//qExecutor = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(threadBufferSize, true));
		
		qExecutor = Executors.newFixedThreadPool(threadPoolSize);
		dbExecutor = Executors.newFixedThreadPool(threadPoolSize);
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		start();
	}

	public static ExecutorService getPurgeExecutor() {
		return purgeExecutor;
	}

	public static ExecutorService getqExecutor() {
		return qExecutor;
	}

	public static void stopqExecutor() {
		log.info("Stopping cc thread listener pool");
		qExecutor.shutdown();
		try {
			if (!qExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
				qExecutor.shutdownNow();
				if (!qExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
				}
			}
		} catch (InterruptedException ie) {
			qExecutor.shutdownNow();
			Thread.currentThread().interrupt();
		} finally {
			if (!qExecutor.isShutdown() || !qExecutor.isTerminated()) {
				qExecutor = null;
			}
		}
	}

	public static ExecutorService getDbExecutor() {
		return dbExecutor;
	}

	public static void stopDbExecutor() {
		dbExecutor.shutdown();
		try {
			if (!dbExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
				dbExecutor.shutdownNow();
				if (!dbExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
				}
			}
		} catch (InterruptedException ie) {
			dbExecutor.shutdownNow();
			Thread.currentThread().interrupt();
		} finally {
			if (!dbExecutor.isShutdown() || !dbExecutor.isTerminated()) {
				dbExecutor = null;
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

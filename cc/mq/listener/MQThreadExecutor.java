/**
 * @#GanimedeExecutor.java
 */
package ibg.cc.mq.listener;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
public final class MQThreadExecutor implements ApplicationListener<ContextRefreshedEvent> {

	private static ExecutorService qExecutor = null;
	private static ExecutorService dbExecutor = null;
	private static ExecutorService purgeExecutor = null;
	private final int NTHREDS;

	private int initialDelay = 10; // actual value comes from spring context
	private int period = 10; // actual value comes from spring context

	private static List<Future<Runnable>> reqFutures = new LinkedList<Future<Runnable>>();
	private static List<Future<Runnable>> dbFutures = new LinkedList<Future<Runnable>>();

	final Logger logger = LoggerFactory.getLogger(MQThreadExecutor.class);

	public MQThreadExecutor(int nthreads, int initialDelay, int period) {
		NTHREDS = nthreads;
		this.initialDelay = initialDelay;
		this.period = period;
	}

	public void start() {

		qExecutor = Executors.newFixedThreadPool(NTHREDS);
		dbExecutor = Executors.newFixedThreadPool(NTHREDS);
		//purgeExecutor = Executors.newSingleThreadScheduledExecutor();
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

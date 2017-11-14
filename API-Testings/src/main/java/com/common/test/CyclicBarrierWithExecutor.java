package com.common.test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CyclicBarrierWithExecutor {

	private ExecutorService executor = null;
	private static boolean startPurging = false;
	private Thread t1;
	private Thread t2;
	private Thread t3;
	private CyclicBarrier cyclicBarrier = null;
	private Calendar cldr;

	public void start() {

		if (executor == null) {

			executor = Executors.newFixedThreadPool(3);
			
			cyclicBarrier = new CyclicBarrier(3, new PurgingCyclicBarrier());

			try {				
				
				t1 = new Thread(new QReqExecutor(), "Request Queue Listener");
				t2 = new Thread(new DbProcessQExecutor(), "DB Process Queue Listener");
				t3 = new Thread(new CBCaller(), "Purger");

				cldr = new GregorianCalendar();

				executor.execute(t1);
				executor.execute(t2);
				executor.execute(t3);

			} catch (Exception ex) {

			}
		}
	}

	class PurgingCyclicBarrier implements Runnable {

		public void run() {
			// This task will be executed once all thread reaches
			// barrier
			System.out.println("All parties are arrived at barrier, lets play");
			try {
				for (int ctr = 1; ctr <= 5; ctr++) {
					System.out.println(ctr);
					Thread.sleep(1000);
				}
				startPurging = false;

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	class QReqExecutor implements Runnable {

		// private CyclicBarrier cyclicBarrier;

		// public QReqExecutor(CyclicBarrier cyclicBarrier) {
		// this.cyclicBarrier = cyclicBarrier;
		// }

		private void execute() {

			try {
				for (;;) {
					System.out.println("from QReqExecutor: ");
					Thread.sleep(1000);
					try {
						if (startPurging) {

							System.out.println(Thread.currentThread().getName() + " is waiting on barrier");
							cyclicBarrier.await();
							System.out.println(Thread.currentThread().getName() + " has crossed the barrier");

						}// if
					} catch (InterruptedException ex) {
						Logger.getLogger(CyclicBarrierWithExecutor.class.getName()).log(Level.SEVERE, null, ex);
					} catch (BrokenBarrierException ex) {
						Logger.getLogger(CyclicBarrierWithExecutor.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			} catch (Exception ex) {
			}
		}

		public void run() {
			execute();
		}

	}// class QReqExecutor

	class DbProcessQExecutor implements Runnable {

		// private CyclicBarrier cyclicBarrier;

		// public DbProcessQExecutor(CyclicBarrier cyclicBarrier) {
		// this.cyclicBarrier = cyclicBarrier;
		// }

		private void execute() {
			try {
				for (;;) {
					System.out.println("from DbProcessQExecutor: ");
					Thread.sleep(1000);

					try {
						if (startPurging) {

							System.out.println(Thread.currentThread().getName() + " is waiting on barrier");
							cyclicBarrier.await();
							System.out.println(Thread.currentThread().getName() + " has crossed the barrier");

						}// if
					} catch (InterruptedException ex) {
						Logger.getLogger(CyclicBarrierWithExecutor.class.getName()).log(Level.SEVERE, null, ex);
					} catch (BrokenBarrierException ex) {
						Logger.getLogger(CyclicBarrierWithExecutor.class.getName()).log(Level.SEVERE, null, ex);
					}

				}
			} catch (Exception ex) {

			}

		}

		public void run() {
			execute();
		}

	}// class DBProcessQExecutor

	class CBCaller implements Runnable {

		private Calendar pcldr;

		public void run() {

			for (;;) {
				pcldr = new GregorianCalendar();

				if ((pcldr.getTimeInMillis() - cldr.getTimeInMillis()) > 12000) {
					cldr.setTimeInMillis(pcldr.getTimeInMillis());
					System.out.println("from CBCaller: ");

					try {

						startPurging = true;

						System.out.println(Thread.currentThread().getName() + " is waiting on barrier");
						cyclicBarrier.await();
						System.out.println(Thread.currentThread().getName() + " has crossed the barrier");
					} catch (InterruptedException ex) {
						Logger.getLogger(CyclicBarrierWithExecutor.class.getName()).log(Level.SEVERE, null, ex);
					} catch (BrokenBarrierException ex) {
						Logger.getLogger(CyclicBarrierWithExecutor.class.getName()).log(Level.SEVERE, null, ex);
					}

				}

			}
		}
	}// class CBCaller

	public static void main(String[] args) {
		new CyclicBarrierWithExecutor().start();
	}

}

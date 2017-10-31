package zao.dyp;

import java.util.ArrayList;

import java.util.concurrent.*;

class Jobs<E extends Job> extends ArrayList<E> {

	private final int MX;
	private final int timeout;

	Jobs(int mx, int t) {
		MX = mx;
		timeout = t;
	}


	void run() throws InterruptedException {
		ExecutorService pool = Executors.newFixedThreadPool(MX);
		pool.invokeAll(this, timeout, TimeUnit.SECONDS);
		pool.shutdownNow();
	}
}


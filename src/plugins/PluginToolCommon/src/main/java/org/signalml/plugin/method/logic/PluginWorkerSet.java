package org.signalml.plugin.method.logic;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ThreadFactory;

public class PluginWorkerSet {

	private ThreadFactory threadFactory;
	private Collection<Thread> workers;


	public PluginWorkerSet(ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
		this.workers = new LinkedList<Thread>();
	}

	public void startAll() {
		for (Thread worker : this.workers) {
			if (!worker.isAlive()) {
				worker.start();
			}
		}
	}

	public void add(Runnable worker) {
		this.workers.add(this.threadFactory.newThread(worker));
	}

	@SuppressWarnings("deprecation")
	public void terminateAll() {
		boolean loop = true;
		int retryCount = 20;

		while (loop) {
			loop = false;
			for (Thread worker : this.workers) {
				if (worker.isAlive()) {
					try {
						worker.join(1000);
					} catch (InterruptedException e) {

					}

					if (worker.isAlive()) {
						retryCount--;
						if (retryCount > 0) {
							loop = true;
							continue;
						}
						worker.interrupt();
						try {
							worker.join(1000);
						} catch (InterruptedException e) {

						}

						if (worker.isAlive()) {
							worker.stop();
						}
					}
				}
			}
		}

		this.workers.clear();
	}

}

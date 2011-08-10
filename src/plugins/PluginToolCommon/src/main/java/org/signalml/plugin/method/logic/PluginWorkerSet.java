package org.signalml.plugin.method.logic;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

public class PluginWorkerSet {

	private ThreadFactory threadFactory;
	private Collection<Thread> workers;
	private Set<Thread> startedThreads;


	public PluginWorkerSet(ThreadFactory threadFactory) {
		this.threadFactory = threadFactory;
		this.workers = new LinkedList<Thread>();
		this.startedThreads = new HashSet<Thread>();
	}

	public void startAll() {
		for (Thread worker : this.workers) {
			if (!worker.isAlive() && !this.startedThreads.contains(worker)) {
				this.startedThreads.add(worker);
				worker.start();
			}
		}
	}

	public void add(Runnable worker) {
		this.workers.add(this.threadFactory.newThread(worker));
	}

	public void submit(Runnable worker) {
		Thread t = this.threadFactory.newThread(worker);
		this.workers.add(t);
		this.startedThreads.add(t);
		t.start();
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

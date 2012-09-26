package org.signalml.plugin.method.helper;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractPluginTrackerUpdaterWithTimer {

	private class PluginTrackerTask extends TimerTask {

		volatile private int current;
		volatile private int last;

		public PluginTrackerTask() {
			this.current = 0;
			this.last = 0;
		}

		@Override
		public void run() {
			int value, last;
			synchronized (this) {
				value = this.current;
				last = this.last;
			}

			this.update(value, last);

			synchronized (this) {
				this.last = Math.max(value, this.last);
			}
		}

		public void setProgress(int currentProgress) {
			synchronized (this) {
				this.current = currentProgress;
			}
		}

		public int getProgress() {
			synchronized (this) {
				return this.last;
			}
		}

		public void update(int progress, int prevProgress) {
			// do nothing
		}

	}

	private Timer timer;
	private PluginTrackerTask task;

	public AbstractPluginTrackerUpdaterWithTimer() {
		final AbstractPluginTrackerUpdaterWithTimer parent = this;

		this.timer = new Timer();
		this.task = new PluginTrackerTask() {

			@Override
			public void update(int progress, int prevProgress) {
				parent.update(progress, prevProgress);
			}
		};
	}

	public void start(long millis) {
		this.timer.scheduleAtFixedRate(this.task, new Date(), millis);
	}

	public void stop() {
		this.timer.cancel();
	}

	public void setProgress(int currentProgress) {
		this.task.setProgress(currentProgress);
	}

	public int getProgress() {
		return this.task.getProgress();
	}

	public abstract void update(int progress, int prevProgress);
}

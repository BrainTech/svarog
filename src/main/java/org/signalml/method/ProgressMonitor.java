/**
 *
 */
package org.signalml.method;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.log4j.Logger;

/**
 * @author Oskar Kapala &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 *
 */
public class ProgressMonitor implements Runnable {

	protected static final Logger logger = Logger.getLogger(ProgressMonitor.class);

	private boolean shutdown = false;

	MethodExecutionTracker tracker = null;

	private File progressFile = null;
	private File stopFile = null;


	/**
	 * @param file, tracker
	 */
	public ProgressMonitor(File progressFile, File stopFile, final MethodExecutionTracker tracker) {
		this.progressFile = progressFile;
		this.stopFile = stopFile;
		this.tracker = tracker;

		if (progressFile.exists()) {
			progressFile.delete();
		}

		if (stopFile.exists()) {
			stopFile.delete();
		}
	}

	public void shutdown() {
		synchronized (this) {
			this.shutdown = true;
			notify();
		}
	}

	public void run() {

		String lLine = null;
		int todo = 0;
		int done = 0;

		boolean firstTime = true;

		if (shutdown) {
			return;
		}

		do {

			synchronized (this) {
				try {
					wait(1000);
				} catch (InterruptedException ex) { /* ignore */
				}

				if (shutdown) {
					logger.debug("ProgressMonitor shutting down");
					return;
				}
			}


			synchronized (tracker) {
				if (tracker.isRequestingAbort()) {

					try {
						stopFile.createNewFile();
					} catch (IOException e) {
						logger.error("Cannot create stop file for procesuj");
					}

					return;
				}
			}

			try {
				if (!progressFile.canRead()) {
					logger.debug("Cannot read file: " + progressFile.getAbsolutePath() + " (retrying)");

					Thread.sleep(50);

				} else {

					Scanner scanner = new Scanner(progressFile);
					Scanner lineScanner = null;

					while (scanner.hasNextLine()) {
						lLine = new String(scanner.nextLine());
					}

					if (lLine != null) {
						lineScanner = new Scanner(lLine);

						lineScanner.useDelimiter("/");
						if (lineScanner.hasNext()) {
							done = new Integer(lineScanner.next().trim());
							todo = new Integer(lineScanner.next().trim());
						}

						synchronized (tracker) {

							if (tracker != null) {
								synchronized (tracker) {
									if (firstTime) {
										tracker.setTickerLimit(1, todo);
										firstTime = false;
									}
									tracker.setTicker(1, done);
								}
							} else {
								logger.debug("tracker is null");
								return;
							}

							if (todo == done) {
								tracker.setTicker(1, done);
								logger.debug("ProgressMonitor fished");
								return;
							}

						}

						lineScanner.close();

					}

					scanner.close();

				}
			} catch (IOException ex) {
				logger.error("Cannot process file: " + progressFile.getAbsolutePath());
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		} while (true);

	}


}

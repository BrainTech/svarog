/* MP5RemoteController.java created 2008-02-18
 *
 */

package org.signalml.method.mp5;

import org.apache.log4j.Logger;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.method.mp5.remote.DecompositionAbortRequest;
import org.signalml.method.mp5.remote.DecompositionProgressRequest;
import org.signalml.method.mp5.remote.DecompositionProgressResponse;
import org.signalml.method.mp5.remote.MP5RemoteConnector;
import org.signalml.util.ResolvableString;

/** MP5RemoteController
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5RemoteController implements Runnable {

	protected static final Logger logger = Logger.getLogger(MP5RemoteController.class);

	public static final int PROGRESS_CHECK_INTERVAL = 10;

	private boolean shutdown = false;

	private String url;
	private String uid;

	private MethodExecutionTracker tracker;
	private MP5RemoteConnector connector;

	private DecompositionProgressRequest progressRequest;

	private volatile int progressCheckInterval = PROGRESS_CHECK_INTERVAL;

	public MP5RemoteController(String url, String uid, MethodExecutionTracker tracker, MP5RemoteConnector connector) {
		this.url = url;
		this.uid = uid;
		this.tracker = tracker;
		this.connector = connector;

		progressRequest = new DecompositionProgressRequest();
		progressRequest.setUid(uid);

	}

	public int getProgressCheckInterval() {
		return progressCheckInterval;
	}

	public void setProgressCheckInterval(int progressCheckInterval) {
		synchronized (this) {
			this.progressCheckInterval = progressCheckInterval;
		}
	}

	public void shutdown() {
		synchronized (this) {
			shutdown = true;
			notify();
		}
	}

	@Override
	public void run() {

		boolean everGotProgress = false;

		long checkTime = System.currentTimeMillis();
		long time;

		if (shutdown) {
			return;
		}

		logger.debug("Controller entering loop for uid [" + uid + "]");

		do {

			synchronized (this) {

				try {
					wait(1000);
				} catch (InterruptedException ex) {
					/* ignore */
				}

				if (shutdown) {
					logger.debug("Controller shutting down [" + uid + "]");
					return;
				}

			}

			if (tracker.isRequestingAbort() || tracker.isRequestingSuspend()) {

				logger.debug("Controller got abort/suspend request [" + uid + "]");

				DecompositionAbortRequest abortRequest = new DecompositionAbortRequest();
				abortRequest.setUid(uid);

				try {
					connector.decompositionAbort(url, abortRequest);
				} catch (Exception ex) {
					logger.error("Abort request failed [" + uid + "]", ex);
				}
				logger.debug("Controller shutting down after abort/suspend request");
				return;

			}

			time = System.currentTimeMillis();

			if ((time-checkTime) > progressCheckInterval*1000) {

				DecompositionProgressResponse progress = null;
				try {
					progress = connector.decompositionProgress(url, progressRequest);
				} catch (Exception ex) {
					if (everGotProgress) {
						logger.warn("Failed to get progress [" + uid + "] (this may be a normal situation)", ex);
					}
					continue;
				}

				everGotProgress = true;
				checkTime = time;

				synchronized (tracker) {
					int[] tickerLimits = tracker.getTickerLimits();
					int[] tickers = tracker.getTickers();

					// update at most 3 last fields, ignore the first
					int[] progressLimits = progress.getTickerLimitsArray();
					int cnt = Math.min(3, progressLimits.length);
					for (int i=0; i<cnt; i++) {
						tickerLimits[3-i] = progressLimits[progressLimits.length-(i+1)];
					}

					int[] progressTickers = progress.getTickersArray();
					cnt = Math.min(3, progressTickers.length);
					for (int i=0; i<cnt; i++) {
						tickers[3-i] = progressTickers[progressTickers.length-(i+1)];
					}
					String messageCode = progress.getMessageCode();
					if (messageCode != null && !messageCode.isEmpty()) {
						tracker.setMessage(new ResolvableString(messageCode, progress.getMessageArgumentsArray()));
					}
					tracker.setTickerLimits(tickerLimits);
					tracker.setTickers(tickers);
				}

			}

		} while (true);

	}

}

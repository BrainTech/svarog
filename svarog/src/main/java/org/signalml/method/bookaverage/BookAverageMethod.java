/* BookAverageMethod.java created 2008-03-22
 *
 */
package org.signalml.method.bookaverage;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.util.LinkedHashSet;

import org.apache.log4j.Logger;
import org.signalml.domain.book.StandardBook;
import org.signalml.domain.book.StandardBookSegment;
import org.signalml.domain.book.WignerMapProvider;
import org.signalml.method.AbstractMethod;
import org.signalml.method.ComputationException;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.method.TrackableMethod;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.method.BaseMethodData;

import org.springframework.validation.Errors;

/** BookAverageMethod
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookAverageMethod extends AbstractMethod implements TrackableMethod {

	protected static final Logger logger = Logger.getLogger(BookAverageMethod.class);

	private static final String UID = "705783f7-1cf8-43e5-83d8-c02fed683e7e";
	private static final String NAME = "bookAverage";
	private static final int[] VERSION = new int[] {1,0};

	public BookAverageMethod() throws SignalMLException {
		super();
	}

	public String getUID() {
		return UID;
	}

	public String getName() {
		return NAME;
	}

	public int[] getVersion() {
		return VERSION;
	}

	@Override
	public BaseMethodData createData() {
		return new BookAverageData();
	}

	public boolean supportsDataClass(Class<?> clazz) {
		return BookAverageData.class.isAssignableFrom(clazz);
	}

	public Class<?> getResultClass() {
		return BookAverageResult.class;
	}

	@Override
	public void validate(Object data, Errors errors) {
		super.validate(data, errors);

		// TODO validation

	}

	@Override
	public Object doComputation(Object dataObj, final MethodExecutionTracker tracker) throws ComputationException {

		BookAverageData data = (BookAverageData) dataObj;

		tracker.resetTickers();

		StandardBook book = data.getBook();

		WignerMapProvider provider = new WignerMapProvider(book.getSamplingFrequency());
		int width = data.getWidth();
		int height = data.getHeight();
		provider.setWidth(width);
		provider.setHeight(height);
		provider.setMinFrequency(data.getMinFrequency());
		provider.setMaxFrequency(data.getMaxFrequency());
		provider.setMinPosition(data.getMinPosition());
		provider.setMaxPosition(data.getMaxPosition());

		LinkedHashSet<Integer> channels = data.getChannels();
		int[] channelArr = new int[channels.size()];
		int channelCnt = 0;
		for (int i : channels) {
			channelArr[channelCnt] = i;
			channelCnt++;
		}

		int minSegment = data.getMinSegment();
		int maxSegment = data.getMaxSegment();

		int stepCount = (maxSegment+1-minSegment) * channelArr.length;
		tracker.setTickerLimits(new int[] {stepCount});

		int x;
		int y;

		float[] averageSignal = null;
		boolean averagingNotPossible = false;
		double[][] averageMap = new double[width][height];
		double[][] normalMap;

		int cnt = 0;

		StandardBookSegment segment;
		float[] signalSamples;
		int j;

		for (int e=0; e<channelArr.length; e++) {
			for (int i=minSegment; i<=maxSegment; i++) {
				segment = book.getSegmentAt(i,channelArr[e]);
				provider.setSegment(segment);
				normalMap = provider.getMap();
				for (x=0; x<width; x++) {
					for (y=0; y<height; y++) {
						averageMap[x][y] += normalMap[x][y];
					}
				}

				if (!averagingNotPossible) {

					if (!segment.hasSignal()) {
						averagingNotPossible = true;
						averageSignal = null;
					} else {

						signalSamples = segment.getSignalSamples();
						if (averageSignal == null) {
							averageSignal = new float[signalSamples.length];
						} else {
							if (averageSignal.length != signalSamples.length) {
								averagingNotPossible = true;
								averageSignal = null;
							}
						}

						for (j=0; j<signalSamples.length; j++) {
							averageSignal[j] += signalSamples[j];
						}

					}

				}

				cnt++;
				tracker.setTicker(0, cnt);
			}
		}

		if (cnt > 0) {

			for (x=0; x<width; x++) {
				for (y=0; y<height; y++) {
					averageMap[x][y] /= cnt;
				}
			}

			averageMap = provider.scaleMap(null, averageMap, width, height, data.getScaleType());

			if (!averagingNotPossible && averageSignal != null) {
				for (j=0; j<averageSignal.length; j++) {
					averageSignal[j] /= cnt;
				}
			}

		}


		BookAverageResult result = new BookAverageResult();

		// getMap is where real processing happens
		result.setMap(averageMap);
		result.setSignal(averageSignal);
		// FIXME do
		result.setReconstruction(null);

		return result;

	}

	@Override
	public int getTickerCount() {
		return 1;
	}

	@Override
	public String getTickerLabel(int ticker) {
		return _("Segments processed");
	}


}

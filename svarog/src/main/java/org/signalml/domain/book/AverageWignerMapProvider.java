/* AverageWignerMapProvider.java created 2008-03-11
 *
 */

package org.signalml.domain.book;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

// XXX code to remove

/** AverageWignerMapProvider
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * 			(based on code sent by Dobieslaw Ircha)
 */
public class AverageWignerMapProvider extends AbstractWignerMapProvider {

	public static final String PROGRESS_PROPERTY = "progress";

	private PropertyChangeSupport pcSupport;

	private StandardBook book;

	private int minSegment;
	private int maxSegment;

	private int[] channels;

	private float[] averageSignal;
	private boolean averagingNotPossible;

	private int progress;

	public AverageWignerMapProvider(float samplingFrequency) {
		super(samplingFrequency);
		pcSupport = new PropertyChangeSupport(this);
	}

	public StandardBook getBook() {
		return book;
	}

	public void setBook(StandardBook book) {
		if (this.book != book) {
			this.book = book;
			minSegment = 0;
			maxSegment = book.getSegmentCount()-1;
			mapDirty = true;
			normalMapDirty = true;
			averageSignal = null;
			averagingNotPossible = false;
		}
	}

	public int getMinSegment() {
		return minSegment;
	}

	public void setMinSegment(int minSegment) {
		if (this.minSegment != minSegment) {
			this.minSegment = minSegment;
			if (maxSegment < minSegment) {
				maxSegment = minSegment;
			}
			mapDirty = true;
			normalMapDirty = true;
			averageSignal = null;
			averagingNotPossible = false;
		}
	}

	public int getMaxSegment() {
		return maxSegment;
	}

	public void setMaxSegment(int maxSegment) {
		if (this.maxSegment != maxSegment) {
			this.maxSegment = maxSegment;
			if (minSegment > maxSegment) {
				minSegment = maxSegment;
			}
			mapDirty = true;
			normalMapDirty = true;
			averageSignal = null;
			averagingNotPossible = false;
		}
	}

	public int[] getChannels() {
		return channels;
	}

	public void setChannels(int[] channels) {
		if (this.channels != channels) {
			this.channels = channels;
			mapDirty = true;
			normalMapDirty = true;
			averageSignal = null;
			averagingNotPossible = false;
		}
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		if (this.progress != progress) {
			int oldProgress = this.progress;
			this.progress = progress;
			pcSupport.firePropertyChange(PROGRESS_PROPERTY, oldProgress, progress);
		}
	}

	@Override
	public double[][] getNormalMap() {
		if (normalMap == null) {
			normalMap = new double[width][height];
			normalMapDirty = true;
		}
		if (normalMapDirty) {

			int x;
			int y;

			averageSignal = null;
			averagingNotPossible = false;

			for (x=0; x<width; x++) {
				for (y=0; y<height; y++) {
					normalMap[x][y] = 0;
				}
			}

			double[][] tempMap = new double[width][height];
			int cnt = 0;

			int[] channels = this.channels;

			if (channels == null) {
				int channelCount = book.getChannelCount();
				channels = new int[channelCount];
				for (int e=0; e<channelCount; e++) {
					channels[e] = e;
				}
			}

			StandardBookSegment segment;
			float[] signalSamples;
			int j;

			for (int e=0; e<channels.length; e++) {
				for (int i=minSegment; i<=maxSegment; i++) {
					segment = book.getSegmentAt(i,channels[e]);
					calculateNormalMap(segment, tempMap);
					for (x=0; x<width; x++) {
						for (y=0; y<height; y++) {
							normalMap[x][y] += tempMap[x][y];
						}
					}

					if (!averagingNotPossible) {

						if (segment.hasSignal()) {
							averagingNotPossible = true;
							averageSignal = null;
						}

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

					cnt++;
					setProgress(cnt);
				}
			}

			if (cnt > 0) {

				for (x=0; x<width; x++) {
					for (y=0; y<height; y++) {
						normalMap[x][y] /= cnt;
					}
				}

				if (!averagingNotPossible && averageSignal != null) {
					for (j=0; j<averageSignal.length; j++) {
						averageSignal[j] /= cnt;
					}
				}

			}

		}
		return normalMap;
	}

	public float[] getAverageSignal() {
		if (averagingNotPossible) {
			return null;
		}
		if (averageSignal == null) {
			getNormalMap();
		}
		return averageSignal;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcSupport.removePropertyChangeListener(propertyName, listener);
	}

}

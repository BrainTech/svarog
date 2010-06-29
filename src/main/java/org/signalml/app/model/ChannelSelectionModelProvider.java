/* ChannelSelectionModelProvider.java created 2007-10-04
 *
 */

package org.signalml.app.model;

import javax.swing.AbstractSpinnerModel;
import javax.swing.DefaultComboBoxModel;

/** ChannelSelectionModelProvider
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelSelectionModelProvider {

	float maxTime;
	float minTime;
	float currentTime;
	float currentLength;
	protected String[] labels;

	private StartTimeSpinnerModel startTimeSpinnerModel;
	protected LengthSpinnerModel lengthSpinnerModel;
	private ChannelComboBoxModel channelComboBoxModel;

	public ChannelSelectionModelProvider(float maxTime, float samplingFrequency, String[] labels, float currentTime, float currentLength, int currentLabel) {

		this.maxTime = maxTime;
		this.minTime = Math.round(1000F / samplingFrequency) / 1000F;
		this.currentTime = currentTime;
		this.currentLength = currentLength;
		this.labels = labels;

		startTimeSpinnerModel = new StartTimeSpinnerModel();
		lengthSpinnerModel = new LengthSpinnerModel();
		channelComboBoxModel = new ChannelComboBoxModel();

		if (currentLabel >= 0) {
			channelComboBoxModel.setSelectedItem(labels[currentLabel]);
		}

	}

	public float getMaxTime() {
		return maxTime;
	}

	public float getCurrentTime() {
		return currentTime;
	}

	public float getCurrentLength() {
		return currentLength;
	}

	public String[] getLabels() {
		return labels;
	}

	public int getCurrentLabel() {
		String currentLabel = (String) channelComboBoxModel.getSelectedItem();
		if (currentLabel != null) {
			for (int i=0; i<labels.length; i++) {
				if (labels[i].equals(currentLabel)) {
					return i;
				}
			}
		}
		return -1;
	}

	public StartTimeSpinnerModel getStartTimeSpinnerModel() {
		return startTimeSpinnerModel;
	}

	public LengthSpinnerModel getLengthSpinnerModel() {
		return lengthSpinnerModel;
	}

	public ChannelComboBoxModel getChannelComboBoxModel() {
		return channelComboBoxModel;
	}

	protected class StartTimeSpinnerModel extends AbstractSpinnerModel implements BoundedSpinnerModel {

		@Override
		public Object getNextValue() {
			if (currentTime >= maxTime) {
				return null;
			}
			float newTime = Math.min(maxTime, currentTime+1);
			return newTime;
		}

		@Override
		public Object getPreviousValue() {
			if (currentTime <= 0) {
				return null;
			}
			float newTime = Math.max(0, currentTime-1);
			return newTime;
		}

		@Override
		public Object getValue() {
			return new Float(currentTime);
		}

		@Override
		public void setValue(Object value) throws IllegalArgumentException {
			float time = ((Float) value).floatValue();
			if (time < 0 || time > maxTime) {
				throw new IllegalArgumentException();
			}
			if (time != currentTime) {
				currentTime = time;
				fireStateChanged();
				lengthSpinnerModel.update();
			}
		}

		@Override
		public Comparable<? extends Number> getMaximum() {
			return new Float(maxTime);
		}

		@Override
		public Comparable<? extends Number> getMinimum() {
			return new Float(0);
		}

	}

	protected class LengthSpinnerModel extends AbstractSpinnerModel implements BoundedSpinnerModel {

		@Override
		public Object getNextValue() {
			if (currentTime + currentLength >= maxTime) {
				return null;
			}
			float newLength = Math.min(maxTime-currentTime, currentLength+1);
			return newLength;
		}

		@Override
		public Object getPreviousValue() {
			if (currentLength <= minTime) {
				return null;
			}
			float newLength = Math.max(minTime, currentLength-1);
			return newLength;
		}

		@Override
		public Object getValue() {
			return new Float(currentLength);
		}

		@Override
		public void setValue(Object value) {
			float length = ((Float) value).floatValue();
			if (length < minTime || length > (maxTime+1-currentTime)) {
				throw new IllegalArgumentException();
			}
			if (length != currentLength) {
				currentLength = length;
				fireStateChanged();
			}
		}

		public void update() {
			if (currentLength > (maxTime-currentTime)) {
				setValue(new Float(maxTime-currentTime));
			}
			fireStateChanged();
		}

		@Override
		public Comparable<? extends Number> getMaximum() {
			return new Float(maxTime-currentTime);
		}

		@Override
		public Comparable<? extends Number> getMinimum() {
			return new Float(minTime);
		}

	}

	private class ChannelComboBoxModel extends DefaultComboBoxModel {

		private static final long serialVersionUID = 1L;

		public ChannelComboBoxModel() {
			super(labels);
		}

	}

}

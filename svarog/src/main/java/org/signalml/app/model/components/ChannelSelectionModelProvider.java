/* ChannelSelectionModelProvider.java created 2007-10-04
 *
 */

package org.signalml.app.model.components;

import javax.swing.AbstractSpinnerModel;
import javax.swing.DefaultComboBoxModel;

/** ChannelSelectionModelProvider
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelSelectionModelProvider {

	double maxTime;
	double minTime;
	double currentTime;
	double currentLength;
	protected String[] labels;

	private StartTimeSpinnerModel startTimeSpinnerModel;
	protected LengthSpinnerModel lengthSpinnerModel;
	private ChannelComboBoxModel channelComboBoxModel;

	public ChannelSelectionModelProvider(double maxTime, float samplingFrequency, String[] labels, double currentTime, double currentLength, int currentLabel) {

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

	public double getMaxTime() {
		return maxTime;
	}

	public double getCurrentTime() {
		return currentTime;
	}

	public double getCurrentLength() {
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
		public Double getNextValue() {
			if (currentTime >= maxTime) {
				return null;
			}
			double newTime = Math.min(maxTime, currentTime+1);
			return newTime;
		}

		@Override
		public Double getPreviousValue() {
			if (currentTime <= 0) {
				return null;
			}
			double newTime = Math.max(0, currentTime-1);
			return newTime;
		}

		@Override
		public Double getValue() {
			return currentTime;
		}

		@Override
		public void setValue(Object value) throws IllegalArgumentException {
			double time = (Double) value;
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
		public Double getMaximum() {
			return maxTime;
		}

		@Override
		public Double getMinimum() {
			return 0.0;
		}

	}

	protected class LengthSpinnerModel extends AbstractSpinnerModel implements BoundedSpinnerModel {

		@Override
		public Double getNextValue() {
			if (currentTime + currentLength >= maxTime) {
				return null;
			}
			double newLength = Math.min(maxTime-currentTime, currentLength+1);
			return newLength;
		}

		@Override
		public Double getPreviousValue() {
			if (currentLength <= minTime) {
				return null;
			}
			double newLength = Math.max(minTime, currentLength-1);
			return newLength;
		}

		@Override
		public Double getValue() {
			return currentLength;
		}

		@Override
		public void setValue(Object value) {
			double length = (Double) value;
			if (length < minTime || length > (maxTime+1-currentTime)) {
				throw new IllegalArgumentException();
			}
			if (length != currentLength) {
				currentLength = length;
				fireStateChanged();
			}
		}

		public void update() {
			if (currentLength > maxTime-currentTime) {
				setValue(maxTime-currentTime);
			}
			fireStateChanged();
		}

		@Override
		public Double getMaximum() {
			return maxTime-currentTime;
		}

		@Override
		public Double getMinimum() {
			return minTime;
		}

	}

	private class ChannelComboBoxModel extends DefaultComboBoxModel {

		private static final long serialVersionUID = 1L;

		public ChannelComboBoxModel() {
			super(labels);
		}

	}

}

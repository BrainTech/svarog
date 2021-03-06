package org.signalml.app.model.components;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.domain.montage.Montage;

/**
 * A model aggregating single channel's display options
 *
 * @author Mateusz Kruszyński &copy; 2011 CC Titanis
 */
public class ChannelsPlotOptionsModel implements ChangeListener {

	/**
	 * aggregated channel options models
	 */
	private ChannelPlotOptionsModel[] channelsOptions;
	/**
	 * parent plot
	 */
	private SignalPlot plot;

	/**
	 * Creates an empty instance of model (returned object needs to be
	 * initialized later)
	 *
	 * @param p parent plot
	 */
	public ChannelsPlotOptionsModel(SignalPlot p) {
		this.plot = p;
	}

	/**
	 * Initializes model for every channel
	 *
	 * @param numOfChannels number of channels models to be initialized
	 */
	public void reset(int numOfChannels) {
		channelsOptions = new ChannelPlotOptionsModel[numOfChannels];
		for (int i = 0; i < channelsOptions.length; i++) {
			double scale_value = this.plot.getValueScaleRangeModel().getRealValue();
			channelsOptions[i] = new ChannelPlotOptionsModel(this, scale_value);
		}
	}

	/**
	 * Initializes model only for channels that it is necessary. If a channel
	 * can be found in the oldMontage and in the previous channelOptions, it is
	 * copied from there.
	 *
	 * @param numOfChannels number of channels models to be initialized.
	 */
	public void resetOnlyWhatsNecessary(Montage oldMontage, Montage newMontage) {

		ChannelPlotOptionsModel[] newChannelsOptions = new ChannelPlotOptionsModel[newMontage
				.getMontageChannelCount()];
		for (int i = 0; i < newChannelsOptions.length; i++) {
			int previousIndex = oldMontage.getMontageChannelIndex(newMontage
					.getMontageChannelLabelAt(i));
			if (previousIndex == -1 || channelsOptions.length <= previousIndex) {
				newChannelsOptions[i] = new ChannelPlotOptionsModel(this,
						this.plot.getValueScaleRangeModel().getRealValue());
			} else {
				newChannelsOptions[i] = channelsOptions[previousIndex];
			}
		}
		channelsOptions = newChannelsOptions;
	}

	/**
	 * Updates child models with given scale value.
	 *
	 * @param scaleValue parent plot's scale value
	 */
	public void globalScaleChanged(double scaleValue) {
		for (ChannelPlotOptionsModel channelOptions : channelsOptions) {
			channelOptions.globalScaleChanged(scaleValue);
		}
	}

	/**
	 * Fired by child models. Sets local montage to parent plot regarding child
	 * models.
	 */
	public void modelChanged() {
		plot.reset();
	}

	/**
	 * Returns channel model for given index
	 *
	 * @param index channel's index for which the model will be returned
	 * @returns channel options model
	 */
	public ChannelPlotOptionsModel getModelAt(int index) {
		return this.channelsOptions[index];
	}

	/**
	 * Returns the number of channels.
	 *
	 * @return the number of channels.
	 */
	public int getChannelCount() {
		return channelsOptions == null ? 0 : channelsOptions.length;
	}

	/**
	 * Calculate and return number of channels that are visible
	 *
	 * @returns a number of visible channels
	 */
	public int getVisibleChannelsCount() {
		int cnt = 0;
		for (ChannelPlotOptionsModel channelOptions : this.channelsOptions) {
			if (channelOptions.getVisible()) {
				cnt++;
			}
		}
		return cnt;
	}

	/**
	 * Fired by parent plot's value scale model. Updates child models.
	 *
	 * @see
	 * javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source == plot.getValueScaleRangeModel()) {
			double scale_value = plot.getValueScaleRangeModel().getRealValue();
			this.globalScaleChanged(scale_value);
		}

	}

	/**
	 * Returns the number of pixels for a specified channel. If this channel
	 * uses local scale, the value calculated
	 *
	 * @param channel channel number
	 * @return pixels per value for the given channel
	 */
	public double getPixelsPerValue(int channel) {
		ChannelPlotOptionsModel channelOptions = getModelAt(channel);
		double channelsPixelPerValue;
		if (channelOptions.isUseLocalScale()) {
			double localScale = channelOptions.getVoltageScale();
			double localVoltageZoomFactor = (localScale * plot
					.getVoltageZoomFactorRatioFor(channel));
			channelsPixelPerValue = plot.getPixelPerChannel()
					* localVoltageZoomFactor;
		} else {
			channelsPixelPerValue = plot.getPixelPerValue();
		}
		return channelsPixelPerValue;
	}

}

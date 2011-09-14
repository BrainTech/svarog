package org.signalml.app.model;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.signalml.app.view.signal.SignalPlot;
import org.signalml.domain.montage.Montage;
/*
 * A model aggregating single channel`s display options
 * @author Mateusz Kruszyński &copy; 2011 CC Titanis
 */

public class ChannelsPlotOptionsModel implements ChangeListener {
	/*
	 * aggregated channel options models
	 */
	private ChannelPlotOptionsModel[] channelsOptions;
	/*
	 * parent plot
	 */
	private SignalPlot plot;
	/*
	 * Creates an empty instance of model (returned object needs to be initialized later)
	 * @param p parent plot
	 */
	public ChannelsPlotOptionsModel(SignalPlot p) {
		this.plot = p;
	}
	
	/*
	 * Initializes model for every channel
	 * @param numOfChannels number of channels models to be initialized
	 */
	public void reset(int numOfChannels) {
		channelsOptions = new ChannelPlotOptionsModel[numOfChannels];
		for (int i = 0; i < channelsOptions.length; i++) {
				channelsOptions[i] = new ChannelPlotOptionsModel(this, this.plot.getValueScaleRangeModel().getValue());
		}
	}
	
	/*
	 * Updates child models with given scale value.
	 * @param scaleValue parent plot`s scale value
	 */
	public void globalScaleChanged(int scaleValue) {
		for (int i = 0; i < channelsOptions.length; i++) {
			channelsOptions[i].globalScaleChanged(scaleValue);
		}
		this.modelChanged();
	}
	
	/*
	 * Fired by parent`s plot on its montage change event
	 */
	public void globalMontageChanged() {
		this.modelChanged();
	}
	
	/*
	 * Fired by child models. Sets local montage to parent plot regarding child models.
	 */
	public void modelChanged() {
		Montage localMontage = new Montage(plot.getDocument().getMontage()); 
		double voltageScale, globalVoltageScale, mult;
		globalVoltageScale = (double) plot.getValueScaleRangeModel().getValue();

		for (int i = 0; i < channelsOptions.length; i++) {
			voltageScale = (double) channelsOptions[i].getVoltageScale();
			//While computing multiplier we must take into consideration 'global' multiplier from
			//main plot`s ValueScaleFactor. In fact if we want to 'neutralize' this 'global' multiplier
			//we need to set mult to (my multiplier)/(global multiplier) so that in the end
			//a sample will be multiplied by ((my multiplier)/(global multiplier))*(global multiplier)
			//so as a result by (my multiplier) only.
			//
			//global multiplier = range_model_value*zoom_ratio*pixel_per_sth
			//my multiplier = my_range_model_value*zoom_ratio*pixel_per_sth
			//thats why (my multiplier)/(global multiplier) is:
		
			mult = voltageScale/globalVoltageScale;
		
			//Get references from plot`s local montage from a moment of opening current dialog
			//Do this so that we always want to apply new reference to this initial-plot montage,
			//not currentMontage
			float[] refs = localMontage.getReferenceAsFloat(i);
			String[] newRefsStr = new String[refs.length];	
		
			//we have some montage R0*ch[0] + R1*ch[1]...
			//we want to have mult*(R0*ch[0] + R1*ch[1]...) so:
			for (int j = 0; j < refs.length; j++)
				newRefsStr[j] = (new Double(refs[j]*mult)).toString();
				localMontage.setReference(i, newRefsStr);
				
		}
		this.plot.setLocalMontage(localMontage);
		
	}
	
	/*
	 * Returns channel model for given index
	 * @param index channel`s index for which the model will be returned
	 * @returns channel options model
	 */
	public ChannelPlotOptionsModel getModelAt(int index) {
		return this.channelsOptions[index];
	}

	/*
	 * Calculate and return number of channels that are visible
	 * @returns a number of visible channels
	 */
	public int getVisibleChannelsCount() {
		int cnt = 0;
		for (int i = 0; i < this.channelsOptions.length; i++)
			if (this.channelsOptions[i].getVisible())
				cnt ++;
		return cnt;
	}
	/*
	 * Fired by parent plot`s value scale model. Updates child models.
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object source = e.getSource();
		if (source == plot.getValueScaleRangeModel())
			this.globalScaleChanged(plot.getValueScaleRangeModel().getValue());
		
	}
	
	
}

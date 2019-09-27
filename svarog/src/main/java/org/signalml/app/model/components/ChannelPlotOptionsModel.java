package org.signalml.app.model.components;
/*
 * A model representing single channel's display options.
 * @author Mateusz Kruszyński &copy; 2011 CC Titanis
 */
public class ChannelPlotOptionsModel {
	/*
	 * parent model
	 */
	private ChannelsPlotOptionsModel parent;
	/*
	 * is the channel to be visible?
	 */
	private boolean visible = true;
	/*
	 * should local voltageScale be used to scale this channel?
	 */
	private boolean useLocalScale = false;
	/*
	 * channel's local voltage scale value
	 */
	private double voltageScale;

	/*
	 * Creates model for given parent and initial voltage scale value
	 * @param parent parent model
	 * @param voltageScale initial voltage scale value
	 */
	public ChannelPlotOptionsModel(ChannelsPlotOptionsModel parent, double voltageScale) {
		this.parent = parent;
		this.voltageScale = voltageScale;
	}

	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
		this.parent.modelChanged();
	}
	public boolean getVisible() {
		return this.visible;
	}

	public double getVoltageScale() {
		return voltageScale;
	}

	public void setVoltageScale(double voltageScale) {
		this.voltageScale = voltageScale;
		if (useLocalScale)
			this.parent.modelChanged();
	}

	public void setUseLocalScale(boolean value) {
		this.useLocalScale = value;
		this.parent.modelChanged();
	}
	public boolean isUseLocalScale() {
		return this.useLocalScale;
	}

	/*
	 * Sets local voltage scale value but not performs 'changed' notification
	 * @param globalScale voltage scale value to be set for this model
	 */
	public void globalScaleChanged(double globalScale) {
		if (!this.useLocalScale)
			this.voltageScale = globalScale; //Achtung - not calling 'changed' deliberately;
	}

}

package org.signalml.app.model;
/*
 * A model representing single channel`s display options.
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
	private boolean visible=true;
	/*
	 * is the channel to ignore global scale value?
	 */
	private boolean ignoreGlobalScale=false;
	/*
	 * channel`s local voltage scale value
	 */
	private int voltageScale;
	
	/*
	 * Creates model for given parent and initial voltage scale value
	 * @param parent parent model
	 * @param voltageScale initial voltage scale value
	 */
	public ChannelPlotOptionsModel(ChannelsPlotOptionsModel parent, int voltageScale) {
		this.parent = parent;
		this.voltageScale=voltageScale;
	}

	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
		this.parent.modelChanged();
	}
	public int getVoltageScale() {
		return voltageScale;
	}
	public void setVoltageScale(int volotageScale) {
		this.voltageScale = volotageScale;
		this.parent.modelChanged();
	}
	
	public void setIgnoreGlobalScale(boolean value) {
		this.ignoreGlobalScale = value;
		this.parent.modelChanged();
	}
	public boolean getIgnoreGlobalScale() {
		return this.ignoreGlobalScale;
	}
	
	/*
	 * Sets local voltage scale value but not performs 'changed' notification
	 * @param globalScale voltage scale value to be set for this model
	 */
	public void globalScaleChanged(int globalScale) {
		if (!this.ignoreGlobalScale)
			this.voltageScale = globalScale; //Achtung - not calling 'changed' deliberately;
	}
}

package org.signalml.plugin.newstager.data;

import java.io.Serializable;

import org.signalml.app.config.preset.Preset;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("stagerparameters")
public class NewStagerParametersPreset implements Serializable, Preset {

	private static final long serialVersionUID = 3226385997881292812L;

	public NewStagerParameters parameters;
	public boolean enableAdvancedParameters;

	public boolean isAutoAlphaAmplitude;
	public boolean isAutoDeltaAmplitude;
	public boolean isAutoSpindleAmplitude;

	private String name;

	public NewStagerParametersPreset() {
		this(new NewStagerParameters(), false, false, false, false);
	}

	public NewStagerParametersPreset(NewStagerParameters stagerParameters,
			boolean enableAdvancedParameters,
			boolean isAutoAlphaAmplitude,
			boolean isAutoDeltaAmplitude,
			boolean isAutoSpindleAmplitude) {
		this.parameters = stagerParameters;
		this.enableAdvancedParameters = enableAdvancedParameters;
		this.isAutoAlphaAmplitude = isAutoAlphaAmplitude;
		this.isAutoDeltaAmplitude = isAutoDeltaAmplitude;
		this.isAutoSpindleAmplitude = isAutoSpindleAmplitude;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.getName();
	}

}

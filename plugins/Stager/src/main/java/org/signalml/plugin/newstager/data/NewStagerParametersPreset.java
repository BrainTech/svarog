package org.signalml.plugin.newstager.data;

import java.io.Serializable;

import org.signalml.app.config.preset.Preset;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("stagerparameters")
public class NewStagerParametersPreset implements Serializable, Preset {

	private static final long serialVersionUID = 3226385997881292812L;
	
	public NewStagerParameters parameters;
	public boolean enableAdvancedParameters;
	
	private String name;

	public NewStagerParametersPreset() {
		this(new NewStagerParameters(), false);
	}
	
	public NewStagerParametersPreset(NewStagerParameters stagerParameters,
			boolean enableAdvancedParameters) {
		this.parameters = stagerParameters;
		this.enableAdvancedParameters = enableAdvancedParameters;
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

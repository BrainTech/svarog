package org.signalml.plugin.data.method;

import java.io.Serializable;

import org.signalml.app.config.preset.Preset;

public abstract class PluginMethodWithWorkDirConfiguration implements Serializable, Preset {

	private static final long serialVersionUID = 3315072773469773511L;

	protected String workingDirectoryPath;

	public String getWorkingDirectoryPath() {
		return workingDirectoryPath;
	}

	public void setWorkingDirectoryPath(String workingDirectoryPath) {
		this.workingDirectoryPath = workingDirectoryPath;
	}

}
package org.signalml.plugin.newstager.data.logic;

import org.signalml.plugin.data.logic.PluginMgrData;
import org.signalml.plugin.newstager.data.NewStagerConstants;
import org.signalml.plugin.newstager.data.NewStagerData;
import org.signalml.plugin.newstager.data.NewStagerFixedParameters;
import org.signalml.plugin.newstager.data.NewStagerParameters;

public class NewStagerMgrData extends PluginMgrData {
	public final NewStagerData stagerData;
	public final NewStagerConstants constants;
	public final NewStagerParameters parameters;
	public final NewStagerFixedParameters fixedParameters;

	public NewStagerMgrData(NewStagerData stagerData,
							NewStagerConstants constants, NewStagerParameters parameters,
							NewStagerFixedParameters fixedParameters) {
		this.stagerData = stagerData;
		this.constants = constants;
		this.parameters = parameters;
		this.fixedParameters = fixedParameters;
	}
}

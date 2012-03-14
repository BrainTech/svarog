package org.signalml.plugin.newstager.data.logic;

import org.signalml.plugin.data.logic.PluginMgrData;
import org.signalml.plugin.newstager.data.NewStagerConstants;
import org.signalml.plugin.newstager.data.NewStagerData;

public class NewStagerMgrData extends PluginMgrData {
	public final NewStagerData stagerData;
	public final NewStagerConstants constants;

	public NewStagerMgrData(NewStagerData stagerData,
			NewStagerConstants constants) {
		this.stagerData = stagerData;
		this.constants = constants;
	}
}

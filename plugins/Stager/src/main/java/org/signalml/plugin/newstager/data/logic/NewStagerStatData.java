package org.signalml.plugin.newstager.data.logic;

import java.util.Map;

import org.signalml.plugin.newstager.data.NewStagerConstants;
import org.signalml.plugin.newstager.data.NewStagerParameters;

public class NewStagerStatData {

	public final NewStagerConstants constants;
	public final NewStagerParameters parameters;
	public final Map<String, Integer> channels;

	public NewStagerStatData(NewStagerConstants constants,
							 NewStagerParameters parameters, Map<String, Integer> channels) {
		this.constants = constants;
		this.parameters = parameters;
		this.channels = channels;
	}

}

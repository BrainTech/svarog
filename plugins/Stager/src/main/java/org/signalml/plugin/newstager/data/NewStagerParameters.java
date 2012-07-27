package org.signalml.plugin.newstager.data;

import java.io.Serializable;

import org.signalml.app.config.preset.Preset;
import org.signalml.plugin.newstager.helper.NewStagerConfigurationDefaultsHelper;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class NewStagerParameters {

	public String bookFilePath;

	public NewStagerRules rules;

	public boolean analyseEMGChannelFlag;
	public boolean analyseEEGChannelsFlag;

	public boolean primaryHypnogramFlag;

	public NewStagerParameterThresholds thresholds;


	public NewStagerParameters() {
		this(null, NewStagerRules.RK, false, false, true,
			 new NewStagerParameterThresholds());
		NewStagerConfigurationDefaultsHelper.GetSharedInstance().setDefaults(this);
	}

	public NewStagerParameters(String bookFilePath,
							   NewStagerRules rules, boolean analyseEMGChannelFlag,
							   boolean analyseEEGChannelsFlag, boolean primaryHypnogramFlag,
							   NewStagerParameterThresholds thresholds) {
		this.bookFilePath = bookFilePath;

		this.rules = rules;

		this.analyseEMGChannelFlag = analyseEMGChannelFlag;
		this.analyseEEGChannelsFlag = analyseEEGChannelsFlag;
		this.primaryHypnogramFlag = primaryHypnogramFlag;

		this.thresholds = thresholds;
	}

}

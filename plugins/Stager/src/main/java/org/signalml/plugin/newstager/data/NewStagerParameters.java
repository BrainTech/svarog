package org.signalml.plugin.newstager.data;

import java.io.Serializable;

import org.signalml.app.config.preset.Preset;
import org.signalml.plugin.newstager.helper.NewStagerConfigurationDefaultsHelper;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("stagerparameters")
public class NewStagerParameters implements Serializable, Preset {

	private static final long serialVersionUID = 1L;

	public String bookFilePath;

	public NewStagerRules rules;

	public boolean analyseEMGChannelFlag;
	public boolean analyseEEGChannelsFlag;

	public boolean primaryHypnogramFlag;

	public NewStagerParameterThresholds thresholds;

	private String name;

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

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}

package org.signalml.plugin.newstager.data;

import java.io.Serializable;

import org.signalml.app.config.preset.Preset;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("stagerparameters")
public class NewStagerParameters implements Serializable, Preset {

	private static final long serialVersionUID = 1L;

	private String bookFilePath;

	public NewStagerRules rules;

	public boolean analyseEMGChannelFlag;
	public boolean analyseEEGChannelsFlag;

	public boolean primaryHypnogramFlag;

	public NewStagerParameterThresholds thresholds;

	private String name;

	public NewStagerParameters() {
		this(null, NewStagerRules.RK, false, false, true,
				new NewStagerParameterThresholds());
	}

	public NewStagerParameters(String bookFilePath,
			NewStagerRules rules, boolean analyseEMGChannelFlag,
			boolean analyseEEGChannelsFlag, boolean primaryHypnogramFlag,
			NewStagerParameterThresholds thresholds) {
		this.setBookFilePath(bookFilePath);

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

	public String getBookFilePath() {
		return bookFilePath;
	}

	public void setBookFilePath(String bookFilePath) {
		this.bookFilePath = bookFilePath;
	}
}

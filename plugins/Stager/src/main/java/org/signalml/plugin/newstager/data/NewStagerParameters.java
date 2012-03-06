package org.signalml.plugin.newstager.data;

import java.io.Serializable;

import org.signalml.app.config.preset.Preset;

import com.thoughtworks.xstream.annotations.XStreamAlias;


@XStreamAlias("stagerparameters")
public class NewStagerParameters implements Serializable, Preset {

	private static final long serialVersionUID = 1L;

	private String bookFilePath;
	
	public int segmentCount;
	
	public boolean analyseEMGChannelFlag;
	public boolean analyseEEGChannelsFlag;
	
	public boolean primaryHypnogramFlag;
	
	public NewStagerParameterThresholds thresholds;

	private String name;

	public NewStagerParameters() {
		this(null, 0, false, false, true, null);
	}
	
	public NewStagerParameters(
			String bookFilePath,
			int segmentCount,
			boolean analyseEMGChannelFlag,
			boolean analyseEEGChannelsFlag,
			boolean primaryHypnogramFlag,
			NewStagerParameterThresholds thresholds) {
		this.setBookFilePath(bookFilePath);
		
		this.segmentCount = segmentCount;
		
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

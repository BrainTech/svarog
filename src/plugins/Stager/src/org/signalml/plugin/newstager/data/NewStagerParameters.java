package org.signalml.plugin.newstager.data;


public class NewStagerParameters {
	public final String bookFilePath;

	public final int segmentCount;

	public final boolean analyseEMGChannelFlag;
	public final boolean analyseEEGChannelsFlag;

	public final boolean primaryHypnogramFlag;

	public final NewStagerParameterThresholds thresholds;


	public NewStagerParameters(
		String bookFilePath,
		int segmentCount,
		boolean analyseEMGChannelFlag,
		boolean analyseEEGChannelsFlag,
		boolean primaryHypnogramFlag,
		NewStagerParameterThresholds thresholds) {
		this.bookFilePath = bookFilePath;

		this.segmentCount = segmentCount;

		this.analyseEMGChannelFlag = analyseEMGChannelFlag;
		this.analyseEEGChannelsFlag = analyseEEGChannelsFlag;
		this.primaryHypnogramFlag = primaryHypnogramFlag;

		this.thresholds = thresholds;
	}
}

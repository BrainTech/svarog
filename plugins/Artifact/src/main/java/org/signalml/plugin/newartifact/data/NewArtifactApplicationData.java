/* ArtifactApplicationData.java created 2007-11-02
 *
 */

package org.signalml.plugin.newartifact.data;

import java.util.ArrayList;
import java.util.Map;

import org.signalml.app.document.SignalDocument;
import org.signalml.domain.montage.Channel;
import org.signalml.domain.montage.ChannelType;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.method.artifact.ArtifactData;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.signal.SvarogAccessSignal;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * ArtifactApplicationData
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
@XStreamAlias("artifactdata")
public class NewArtifactApplicationData extends NewArtifactData {

	private static final long serialVersionUID = 1L;

	@XStreamOmitField
	private ExportedSignalDocument signalDocument;

	@XStreamOmitField
	private boolean existingProject;

	@XStreamOmitField
	private SvarogAccessSignal signalAccess;

	private SourceMontage montage;

	public NewArtifactApplicationData() {
		super();
	}

	public ExportedSignalDocument getSignalDocument() {
		return signalDocument;
	}

	public void setSignalDocument(ExportedSignalDocument signalDocument) {
		this.signalDocument = signalDocument;

		this.setMontage(this.createMontage(signalDocument));
	}

	private SourceMontage createMontage(ExportedSignalDocument document) {
		return new SourceMontage((SignalDocument) document); // TODO FIXME
	}

	public SourceMontage getMontage() {
		return montage;
	}

	public void setMontage(SourceMontage montage) {
		this.montage = montage;
	}

	public void setSignalAccess(SvarogAccessSignal signalAccess) {
		this.signalAccess = signalAccess;
	}

	public boolean isExistingProject() {
		return existingProject;
	}

	public void setExistingProject(boolean existingProject) {
		this.existingProject = existingProject;
	}

	public void calculate() throws SignalMLException {

		int i;

		Map<String, Integer> keyChannelMap = getKeyChannelMap();
		ArrayList<Integer> eegChannels = getEegChannels();
		Map<String, Integer> channelMap = getChannelMap();

		keyChannelMap.clear();
		eegChannels.clear();
		channelMap.clear();

		int cnt = montage.getSourceChannelCount();
		Channel function;
		for (i = 0; i < cnt; i++) {
			channelMap.put(montage.getSourceChannelLabelAt(i), i);
			function = montage.getSourceChannelFunctionAt(i);
			if (function != null) {
				if (function.getType() == ChannelType.PRIMARY) {
					eegChannels.add(i);
				}
				if (ArtifactData.keyChannelSet.contains(function)) {
					keyChannelMap.put(function.getName(), i);
				}
			}
		}

		/*
		 * Map<String, Integer> keyChannelMap = getKeyChannelMap();
		 * ArrayList<Integer> eegChannels = getEegChannels(); Map<String,
		 * Integer> channelMap = getChannelMap();
		 *
		 * keyChannelMap.clear(); eegChannels.clear(); channelMap.clear();
		 *
		 * Map<String, Channel> eegChannelTypeMap = new HashMap<String,
		 * Channel>(); for (EegChannel channel : EegChannel.values()) {
		 * eegChannelTypeMap.put(channel.getName(), channel); }
		 *
		 * String channelList[] = this.signalDocument.getSourceChannelLabels()
		 * .toArray(new String[] {}); for (int i = 0; i < channelList.length;
		 * ++i) { String label = channelList[i]; Channel channel =
		 * eegChannelTypeMap.get(label); if (channel != null) { if
		 * (channel.getType() == ChannelType.PRIMARY) { eegChannels.add(i); }
		 *
		 * if (NewArtifactData.keyChannelSet.contains(channel)) {
		 * keyChannelMap.put(channel.getName(), i); } } }
		 */

		/*
		 * SignalView signalView = (SignalView)
		 * signalDocument.getDocumentView(); SignalPlot plot =
		 * signalView.getMasterPlot();
		 *
		 * MultichannelSampleSource sampleSource;
		 *
		 * SignalProcessingChain signalChain = plot.getSignalChain(); try { //
		 * SignalMLCodecSampleSource source = (SignalMLCodecSampleSource) //
		 * signalChain.getSource(); // sampleSource = new //
		 * FastMultichannelSampleSource(source.getReader()); throw new
		 * ClassCastException(); } catch (ClassCastException e) {
		 * SignalProcessingChain copyChain = signalChain
		 * .createRawLevelCopyChain();
		 *
		 * SignalSpace signalSpace = new SignalSpace();
		 * signalSpace.setChannelSpaceType(ChannelSpaceType.WHOLE_SIGNAL);
		 * signalSpace.setTimeSpaceType(TimeSpaceType.WHOLE_SIGNAL);
		 * signalSpace.setWholeSignalCompletePagesOnly(false);
		 *
		 * SegmentedSampleSourceFactory factory = SegmentedSampleSourceFactory
		 * .getSharedInstance(); sampleSource =
		 * factory.getContinuousSampleSource(copyChain, signalSpace, null,
		 * plot.getPageSize(), plot.getBlockSize()); }
		 */

		setSampleSource(new NewArtifactPluginSampleSource(signalAccess,
				signalDocument));

		// TODO rethink this cast - what is the data type in matlab code?
		setPageSize((int) signalDocument.getPageSize());
		setBlocksPerPage(signalDocument.getBlocksPerPage());

		setSignalFormat(NewArtifactSignalFormat.FLOAT);

	}

}
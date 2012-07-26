/* ArtifactApplicationData.java created 2007-11-02
 *
 */

package org.signalml.plugin.newartifact.data;

import java.util.ArrayList;
import java.util.Map;

import org.signalml.app.document.SignalDocument;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.montage.system.ChannelFunction;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.io.PluginSampleSourceAdapter;

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
	private transient ExportedSignalDocument signalDocument;

	@XStreamOmitField
	private boolean existingProject;

	@XStreamOmitField
	private transient SvarogAccessSignal signalAccess;

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

		Map<String, Integer> keyChannelMap = getKeyChannelMap();
		ArrayList<Integer> eegChannels = getEegChannels();
		Map<String, Integer> channelMap = getChannelMap();

		keyChannelMap.clear();
		eegChannels.clear();
		channelMap.clear();

		int cnt = montage.getSourceChannelCount();
		SourceChannel channel;
		for (int i = 0; i < cnt; i++) {
			channelMap.put(montage.getSourceChannelLabelAt(i), i);

			channel = montage.getSourceChannelAt(i);
			if (channel.getFunction() == ChannelFunction.EEG) {
				eegChannels.add(i);
			}
			//TODO: IMPORTANT - fill the keyChannelMap.
			/*
			if (AbstractData.keyChannelSet.contains(function)) {
				keyChannelMap.put(function.getName(), i);
			}*/
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

		this.resetSampleSource();

		this.setPageSize((int) signalDocument.getPageSize());
		this.setBlocksPerPage(signalDocument.getBlocksPerPage());
	}
	
	private void resetSampleSource() {
		this.setSampleSource(new PluginSampleSourceAdapter(signalAccess,
				signalDocument));
	}
	
	@Override
	public void dispose() {
		this.resetSampleSource();
	}

	
}
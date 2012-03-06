/* StagerApplicationData.java created 2008-02-08
 * 
 */

package org.signalml.plugin.newstager.data;

import java.util.ArrayList;
import java.util.Map;

import org.signalml.app.document.SignalDocument;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.montage.system.ChannelType;
import org.signalml.domain.montage.system.IChannelFunction;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.space.ChannelSpaceType;
import org.signalml.domain.signal.space.SegmentedSampleSourceFactory;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.TimeSpaceType;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.signal.SvarogAccessSignal;

/**
 * StagerApplicationData
 * 
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerApplicationData extends NewStagerData {

	private static final long serialVersionUID = 1L;

	private ExportedSignalDocument signalDocument;
	private float pageSize;

	private SourceMontage montage;

	private SvarogAccessSignal signalAccess;

	public NewStagerApplicationData() {
		super();
	}

	public ExportedSignalDocument getSignalDocument() {
		return signalDocument;
	}

	public void setSignalDocument(ExportedSignalDocument signalDocument) {
		this.signalDocument = signalDocument;

		this.setMontage(this.createMontage(signalDocument));
	}
	
	public void setSignalAccess(SvarogAccessSignal signalAccess) {
		this.signalAccess = signalAccess;
	}
	
	private SourceMontage createMontage(ExportedSignalDocument document) {
		return new SourceMontage((SignalDocument) document); //FIXME
	}

	public float getPageSize() {
		return pageSize;
	}

	public void setPageSize(float pageSize) {
		this.pageSize = pageSize;
	}

	public SourceMontage getMontage() {
		return montage;
	}

	public void setMontage(SourceMontage montage) {
		this.montage = montage;
	}

	public void calculate() throws SignalMLException {

		// ConfigurationDefaults.setStagerFixedParameters(getFixedParameters());
		// //FIXME

		Map<String, Integer> keyChannelMap = getKeyChannelMap();
		ArrayList<Integer> eegChannels = getEegChannels();
		Map<String, Integer> channelMap = getChannelMap();

		keyChannelMap.clear();
		eegChannels.clear();
		channelMap.clear();

		int cnt = montage.getSourceChannelCount();
		for (int i = 0; i < cnt; i++) {
			channelMap.put(montage.getSourceChannelLabelAt(i), i);
			SourceChannel channel = montage.getSourceChannelAt(i);
			if (channel == null) {
				continue;
			}
			
			IChannelFunction function = channel.getFunction();

			if (channel.isChannelType(ChannelType.PRIMARY)) {
				eegChannels.add(i);
			}
			
			if (function != null) {
				if (NewStagerData.keyChannelSet.contains(function)) {
					keyChannelMap.put(function.getName(), i);
				}
			}
		}

		SignalView signalView = (SignalView) signalDocument.getDocumentView();
		SignalPlot plot = signalView.getMasterPlot();

		SignalProcessingChain signalChain = plot.getSignalChain();
		SignalProcessingChain copyChain = signalChain.createRawLevelCopyChain();

		SignalSpace signalSpace = new SignalSpace();
		signalSpace.setChannelSpaceType(ChannelSpaceType.WHOLE_SIGNAL);
		signalSpace.setTimeSpaceType(TimeSpaceType.WHOLE_SIGNAL);
		signalSpace.setWholeSignalCompletePagesOnly(false);

		SegmentedSampleSourceFactory factory = SegmentedSampleSourceFactory
				.getSharedInstance();
		MultichannelSampleSource sampleSource = factory
				.getContinuousSampleSource(copyChain, signalSpace, null,
						plot.getPageSize(), plot.getBlockSize());

		setSampleSource(sampleSource);

	}

}

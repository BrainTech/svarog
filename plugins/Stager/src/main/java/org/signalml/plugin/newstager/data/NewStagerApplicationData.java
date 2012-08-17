/* NewStagerApplicationData.java created 2008-02-08
 *
 */

package org.signalml.plugin.newstager.data;

import java.util.ArrayList;
import java.util.Map;

import org.signalml.app.document.signal.SignalDocument;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.montage.system.ChannelType;
import org.signalml.domain.montage.system.IChannelFunction;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.io.PluginSampleSourceAdapter;

/**
 * NewStagerApplicationData
 * 
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewStagerApplicationData extends NewStagerData {

	private static final long serialVersionUID = 1L;

	private transient ExportedSignalDocument signalDocument;
	private float pageSize;

	private SourceMontage montage;

	private transient SvarogAccessSignal signalAccess;

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
		return new SourceMontage((SignalDocument) document); // FIXME
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

		this.resetSampleSource();

		this.setPageSize((int) signalDocument.getPageSize());
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

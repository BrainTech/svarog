/* EvokedPotentialApplicationData.java created 2008-01-12
 *
 */

package org.signalml.app.method.ep;

import java.util.ArrayList;
import java.util.List;
import org.signalml.app.document.TagDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.method.ep.view.tags.TagStyleGroup;
import org.signalml.app.view.signal.SignalView;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.domain.signal.space.MarkerSegmentedSampleSource;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.TimeSpaceType;
import org.signalml.method.ep.EvokedPotentialData;
import org.signalml.plugin.export.SignalMLException;

/** EvokedPotentialApplicationData
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EvokedPotentialApplicationData extends EvokedPotentialData {

	private static final long serialVersionUID = 1L;

	private SignalDocument signalDocument;
	private TagDocument tagDocument;

	public EvokedPotentialApplicationData() {
		super();
	}

	public SignalDocument getSignalDocument() {
		return signalDocument;
	}

	public void setSignalDocument(SignalDocument signalDocument) {
		this.signalDocument = signalDocument;
	}

	public TagDocument getTagDocument() {
		return tagDocument;
	}

	public void setTagDocument(TagDocument tagDocument) {
		this.tagDocument = tagDocument;
	}

	public void calculate() throws SignalMLException {

		this.setStyledTagSet(tagDocument != null ? tagDocument.getTagSet() : null);

		SignalSpace signalSpace = getParameters().getSignalSpace();

		MultichannelSampleSource sampleSource;
		if (signalDocument.getDocumentView() != null) {
			SignalView signalView = (SignalView) signalDocument.getDocumentView();

			SignalProcessingChain signalChain = signalView.getMasterPlot().getSignalChain();

			sampleSource = signalChain.createLevelCopyChain(signalSpace.getSignalSourceLevel());
		} else{
			sampleSource = signalDocument.getSampleSource();
		}

		List<String> artifactTagStyleNames = new ArrayList<>();
		for (TagStyleGroup styleGroup: getParameters().getArtifactTagStyles()) {
			artifactTagStyleNames.addAll(styleGroup.getTagStyleNames());
		}

		List<MarkerSegmentedSampleSource> averagedSampleSources = new ArrayList<>();
		List<MarkerSegmentedSampleSource> baselineSampleSources = new ArrayList<>();
		for (TagStyleGroup tagStyleGroup: getParameters().getAveragedTagStyles()) {

			List<String> styleNames = tagStyleGroup.getTagStyleNames();

			Double startAveragingTime = null, endAveragingTime = null;
			if (signalSpace.getTimeSpaceType() == TimeSpaceType.SELECTION_BASED) {
				startAveragingTime = signalSpace.getSelectionTimeSpace().getPosition();
				endAveragingTime = startAveragingTime + signalSpace.getSelectionTimeSpace().getLength();
			}

			MarkerSegmentedSampleSource segmentedSampleSource = new MarkerSegmentedSampleSource(
							sampleSource, startAveragingTime, endAveragingTime,
							getStyledTagSet(),
							styleNames, artifactTagStyleNames,
							getParameters().getAveragingStartTime(), getParameters().getAveragingTimeLength(), signalSpace.getChannelSpace());

			MarkerSegmentedSampleSource baselineSampleSource = new MarkerSegmentedSampleSource(
					sampleSource, startAveragingTime, endAveragingTime,
					getStyledTagSet(),
					styleNames, artifactTagStyleNames,
					getParameters().getBaselineTimeStart(), getParameters().getBaselineTimeLength(), signalSpace.getChannelSpace());

			averagedSampleSources.add(segmentedSampleSource);
			baselineSampleSources.add(baselineSampleSource);
		}

		setSampleSource(averagedSampleSources);
		setBaselineSampleSources(baselineSampleSources);

	}

}

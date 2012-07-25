/* EvokedPotentialApplicationData.java created 2008-01-12
 *
 */

package org.signalml.app.method.ep;

import java.util.ArrayList;
import java.util.List;

import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.method.ep.view.tags.TagStyleGroup;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.domain.signal.samplesource.MultichannelSegmentedSampleSource;
import org.signalml.domain.signal.space.MarkerTimeSpace;
import org.signalml.domain.signal.space.SegmentedSampleSourceFactory;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.TimeSpaceType;
import org.signalml.method.ep.EvokedPotentialData;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.TagStyle;

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

		SignalView signalView = (SignalView) signalDocument.getDocumentView();
		SignalPlot plot = signalView.getMasterPlot();

		SignalProcessingChain signalChain = plot.getSignalChain();
		SignalSpace signalSpace = getParameters().getWholeSignalSpace();

		SignalProcessingChain copyChain = signalChain.createLevelCopyChain(signalSpace.getSignalSourceLevel());

		SegmentedSampleSourceFactory factory = SegmentedSampleSourceFactory.getSharedInstance();

		MultichannelSampleSource sampleSource = factory.getContinuousSampleSource(copyChain, signalSpace,
				null, plot.getPageSize(), plot.getBlockSize());

		List<MultichannelSegmentedSampleSource> averagedSampleSources = new ArrayList<MultichannelSegmentedSampleSource>();
		for (TagStyleGroup tagStyleGroup: getParameters().getAveragedTagStyles()) {
			MarkerTimeSpace markerTimeSpace = new MarkerTimeSpace();

			List<String> styleNames = new ArrayList<String>();
			for (TagStyle style: tagStyleGroup.getTagStyles()) {
				styleNames.add(style.getName());
			}

			markerTimeSpace.setMarkerStyleNames(styleNames);
			markerTimeSpace.setSecondsBefore(getParameters().getAveragingTimeBefore());
			markerTimeSpace.setSecondsAfter(getParameters().getAveragingTimeAfter());

			SignalSpace averagedSignalSpace = new SignalSpace();

			averagedSignalSpace.setMarkerTimeSpace(markerTimeSpace);
			averagedSignalSpace.setTimeSpaceType(TimeSpaceType.MARKER_BASED);

			MultichannelSegmentedSampleSource segmentedSampleSource = factory.getSegmentedSampleSource(sampleSource, averagedSignalSpace,
					tagDocument != null ? tagDocument.getTagSet() : null, plot.getPageSize(), plot.getBlockSize());

			averagedSampleSources.add(segmentedSampleSource);
		}

		setSampleSource(averagedSampleSources);
	}

}

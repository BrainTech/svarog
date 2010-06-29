/* EvokedPotentialApplicationData.java created 2008-01-12
 *
 */

package org.signalml.app.method.ep;

import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.domain.signal.MultichannelSegmentedSampleSource;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.space.SegmentedSampleSourceFactory;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.exception.SignalMLException;
import org.signalml.method.ep.EvokedPotentialData;

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
		SignalSpace signalSpace = getParameters().getSignalSpace();

		SignalProcessingChain copyChain = signalChain.createLevelCopyChain(signalSpace.getSignalSourceLevel());

		SegmentedSampleSourceFactory factory = SegmentedSampleSourceFactory.getSharedInstance();
		MultichannelSegmentedSampleSource segmentedSampleSource = factory.getSegmentedSampleSource(copyChain, signalSpace, tagDocument != null ? tagDocument.getTagSet() : null, plot.getPageSize(), plot.getBlockSize());

		setSampleSource(segmentedSampleSource);

	}

}

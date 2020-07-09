/* MP5ApplicationData.java created 2007-10-30
 *
 */

package org.signalml.app.method.mp5;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.app.document.TagDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.samplesource.MultichannelSegmentedSampleSource;
import org.signalml.domain.signal.space.SegmentedSampleSourceFactory;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.method.mp5.MP5Data;
import org.signalml.plugin.export.SignalMLException;

/** MP5ApplicationData
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("mp5appdata")
public class MP5ApplicationData extends MP5Data {

	private static final long serialVersionUID = 1L;

	private transient SignalDocument signalDocument;

	public MP5ApplicationData() {
		super();
	}

	public SignalDocument getSignalDocument() {
		return signalDocument;
	}

	public void setSignalDocument(SignalDocument signalDocument) {
		this.signalDocument = signalDocument;
	}

	public void calculate() throws SignalMLException {

		SignalView signalView = (SignalView) signalDocument.getDocumentView();
		SignalPlot plot = signalView.getMasterPlot();

		SignalProcessingChain signalChain = plot.getSignalChain();
		SignalSpace signalSpace = getParameters().getSignalSpace();

		SignalProcessingChain copyChain = signalChain.createLevelCopyChain(signalSpace.getSignalSourceLevel());

		TagDocument tagDocument = signalDocument.getActiveTag();

		SegmentedSampleSourceFactory factory = SegmentedSampleSourceFactory.getSharedInstance();
		MultichannelSegmentedSampleSource segmentedSampleSource = factory.getSegmentedSampleSource(copyChain, signalSpace, tagDocument != null ? tagDocument.getTagSet() : null, plot.getPageSize(), plot.getBlockSize());


		setSampleSource(segmentedSampleSource);

		setChainDescriptor(copyChain.createDescriptor());
		setSourceDescriptor(segmentedSampleSource.createDescriptor());

	}

}

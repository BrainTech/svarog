/* AbstractReaderSignal.java created 2007-11-22
 *
 */

package org.signalml.app.document.signal;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.beans.IntrospectionException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.app.model.components.PropertyProvider;
import org.signalml.app.model.document.opensignal.elements.SignalParameters;
import org.signalml.app.view.signal.SampleSourceUtils;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.SignalConfigurer;
import org.signalml.domain.signal.samplesource.OriginalMultichannelSampleSource;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.AbstractDocument;
import org.signalml.plugin.export.signal.ExportedTagDocument;
import org.springframework.context.MessageSourceResolvable;

/**
 * Abstract implementation of a {@link SignalDocument}.
 * Only implements the methods of {@code SignalDocument}.
 * Stores the information about:
 * <ul>
 * <li>all {@link TagDocument tag documents} and the active one</li>
 * <li>the size of a block and a page</li>
 * <li>the {@link OriginalMultichannelSampleSource source} of unprocessed (raw)
 * samples</li>
 * <li>the main {@link Montage montage} for the signal</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractSignal extends AbstractDocument implements SignalDocument, MessageSourceResolvable, PropertyProvider {

	protected static final Logger logger = Logger.getLogger(AbstractSignal.class);

	/**
	 * the source of unprocessed (raw) samples
	 */
	protected OriginalMultichannelSampleSource sampleSource = null;

	/**
	 * the currently active (selected) {@link TagDocument tag document}
	 */
	protected TagDocument activeTag;

	/**
	 * list of all {@link TagDocument tag documents} for this signal
	 */
	protected List<TagDocument> tagDocuments = new LinkedList<TagDocument>();

	/**
	 * the length of the page in seconds
	 */
	protected float pageSize = SignalParameters.DEFAULT_PAGE_SIZE;

	/**
	 * the number of blocks in a page
	 */
	protected int blocksPerPage = SignalParameters.DEFAULT_BLOCKS_PER_PAGE;

	/**
	 * the length of a block in seconds
	 */
	protected float blockSize = pageSize / blocksPerPage;

	/**
	 * the main {@link Montage montage} for this signal
	 */
	protected Montage montage = null;

	/**
	 * the number of {@link TagDocument tag documents} for this signal
	 * that have no backing file {@code + 1}
	 */
	private int namelessTagCounter = 1;

	/**
	 * Constructor.
	 */
	public AbstractSignal() {
	}

	@Override
	public void closeDocument() throws SignalMLException {
		sampleSource = null;
		super.closeDocument();
	}

	@Override
	public float getSamplingFrequency() {
		return sampleSource.getSamplingFrequency();
	}

	@Override
	public int getChannelCount() {
		return sampleSource.getChannelCount();
	}

	@Override
	public float getMinSignalLength() {
		return ((float) SampleSourceUtils.getMinSampleCount(sampleSource)) / getSamplingFrequency();
	}

	@Override
	public float getMaxSignalLength() {
		return ((float) SampleSourceUtils.getMaxSampleCount(sampleSource)) / getSamplingFrequency();
	}

	@Override
	public int getPageCount() {
		return (int) Math.ceil(getMaxSignalLength() / getPageSize());
	}

	@Override
	public int getBlockCount() {
		return (int) Math.ceil(getMaxSignalLength() / getBlockSize());
	}

	@Override
	public OriginalMultichannelSampleSource getSampleSource() {
		return sampleSource;
	}

	@Override
	public String[] getCodes() {
		return new String[] { "signalDocument" };
	}

	@Override
	public Object[] getArguments() {
		return new Object[] {
				   getName()
			   };
	}

	@Override
	@Deprecated
	public String getDefaultMessage() {
		// XXX: remove this method and call getName() directly
		return getName();
	}

	@Override
	public float getPageSize() {
		if (activeTag != null) {
			return activeTag.getPageSize();
		}
		return pageSize;
	}

	@Override
	public void setPageSize(float pageSize) {
		if (this.pageSize != pageSize) {
			float last = this.pageSize;
			this.pageSize = pageSize;
			this.blockSize = pageSize / blocksPerPage;
			pcSupport.firePropertyChange(PAGE_SIZE_PROPERTY, last, pageSize);
		}
	}

	@Override
	public int getBlocksPerPage() {
		if (activeTag != null) {
			return activeTag.getBlocksPerPage();
		}
		return blocksPerPage;
	}

	@Override
	public void setBlocksPerPage(int blocksPerPage) {
		if (this.blocksPerPage != blocksPerPage) {
			int last = this.blocksPerPage;
			this.blocksPerPage = blocksPerPage;
			this.blockSize = pageSize / blocksPerPage;
			pcSupport.firePropertyChange(BLOCKS_PER_PAGE_PROPERTY, last, blocksPerPage);
		}
	}

	@Override
	public float getBlockSize() {
		if (activeTag != null) {
			return activeTag.getBlockSize();
		}
		return blockSize;
	}

	@Override
	public List<TagDocument> getTagDocuments() {
		return tagDocuments;
	}

	@Override
	public void addTagDocument(TagDocument document) {
		if (tagDocuments.contains(document)) {
			return;
		}
		if (document.getBackingFile() == null) {
			document.setFallbackName(Integer.toString(namelessTagCounter));
			namelessTagCounter++;
		}
		addDependentDocument(document);
		tagDocuments.add(document);
		pcSupport.fireIndexedPropertyChange(TAG_DOCUMENTS_PROPERTY, tagDocuments.indexOf(document), null, document);
		if (activeTag == null && tagDocuments.size() == 1) {
			setActiveTag(document);
		}
	}

	@Override
	public void removeTagDocument(TagDocument document) {
		int index = tagDocuments.indexOf(document);
		if (index < 0) {
			return;
		}
		tagDocuments.remove(index);
		if (document == activeTag) {
			if (tagDocuments.size() > 0) {
				setActiveTag((TagDocument) tagDocuments.get(0));
			} else {
				setActiveTag(null);
			}
		}
		removeDependentDocument(document);
		pcSupport.fireIndexedPropertyChange(TAG_DOCUMENTS_PROPERTY, index, document, null);
	}

	@Override
	public TagDocument getActiveTag() {
		return activeTag;
	}

	@Override
	public void setActiveTag(TagDocument document) {
		if (activeTag != document) {
			if (document != null) {
				if (!getDependentDocuments().contains(document)) {
					throw new SanityCheckException("Tag set to be active not dependent");
				}
			}
			TagDocument oldActiveTag = activeTag;
			activeTag = document;
			pcSupport.firePropertyChange(ACTIVE_TAG_PROPERTY, oldActiveTag, activeTag);
		}
	}

	@Override
	public Montage getMontage() {
		if (getSampleSource() == null) {
			return null;
		}
		if (montage == null) {
			montage = createDefaultMontage();
		}
		return montage;
	}

	@Override
	public boolean isMontageCreated() {
		if (montage == null)
			return false;
		return true;
	}

	protected Montage createDefaultMontage() {
		return SignalConfigurer.createMontage(this);
	}

	protected Montage createDefaultMontage(int numberOfChannels) {
		return SignalConfigurer.createMontage(numberOfChannels);
	}

	@Override
	public void setMontage(Montage montage) {
		if (this.montage != montage) {
			Montage oldMontage = this.montage;
			this.montage = montage;
			pcSupport.firePropertyChange(MONTAGE_PROPERTY, oldMontage, montage);
		}
	}

	@Override
	public String getMontageInfo() {

		StringBuilder sb = new StringBuilder();
		String description = getMontage().getDescription();
		if (description != null) {
			sb.append(description);
		}

		SignalView view = (SignalView) getDocumentView();
		if (view != null) {

			LinkedList<SignalPlot> plots = view.getPlots();
			Iterator<SignalPlot> it = plots.iterator();
			SignalPlot plot;
			Montage montage;
			it.next(); // skip master
			int cnt = 1;

			while (it.hasNext()) {
				plot = it.next();
				montage = plot.getLocalMontage();
				if (montage != null) {
					description = montage.getDescription();
					if (description != null) {
						sb.append("\n\nClone ").append(cnt).append("\n------------\n");
						sb.append(description);
					}
				}
				cnt++;
			}

		}

		return sb.toString();

	}

	@Override
	public List<ExportedTagDocument> getExportedTagDocuments() {
		List<ExportedTagDocument> exportedTagDocuments = new LinkedList<ExportedTagDocument>();
		List<TagDocument> tagDocuments = getTagDocuments();
		for (TagDocument tagDocument : tagDocuments)
			exportedTagDocuments.add(tagDocument);
		return exportedTagDocuments;
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		LinkedList<LabelledPropertyDescriptor> list = new LinkedList<LabelledPropertyDescriptor>();

		list.add(new LabelledPropertyDescriptor(_("sampling frequency"), "samplingFrequency", SignalMLDocument.class));
		list.add(new LabelledPropertyDescriptor(_("channel count"), "channelCount", SignalMLDocument.class));
		list.add(new LabelledPropertyDescriptor(_("page size"), "pageSize", SignalMLDocument.class));
		list.add(new LabelledPropertyDescriptor(_("blocks per page"), "blocksPerPage", SignalMLDocument.class));
		list.add(new LabelledPropertyDescriptor(_("block size"), "blockSize", SignalMLDocument.class, "getBlockSize", null));
		list.add(new LabelledPropertyDescriptor(_("minimum signal length"), "minSignalLength", SignalMLDocument.class, "getMinSignalLength", null));
		list.add(new LabelledPropertyDescriptor(_("maximum signal length"), "maxSignalLength", SignalMLDocument.class, "getMaxSignalLength", null));
		list.add(new LabelledPropertyDescriptor(_("page count"), "pageCount", SignalMLDocument.class, "getPageCount", null));
		list.add(new LabelledPropertyDescriptor(_("block count"), "blockCount", SignalMLDocument.class, "getBlockCount", null));

		return list;

	}

	@Override
	public ArrayList<String> getMontageChannelLabels() {
		Montage montage = getMontage();
		ArrayList<String> labels = new ArrayList<String>(montage.getMontageChannelCount());
		for (int i = 0; i < montage.getMontageChannelCount(); ++i) {
			labels.add(montage.getMontageChannelLabelAt(i));
		}
		return labels;
	}

	@Override
	public ArrayList<String> getSourceChannelLabels() {
		Montage montage = getMontage();
		ArrayList<String> labels = new ArrayList<String>(montage.getSourceChannelCount());
		for (int i = 0; i < montage.getSourceChannelCount(); ++i) {
			labels.add(montage.getSourceChannelLabelAt(i));
		}
		return labels;
	}

	@Override
	public SignalView getSignalView() throws InvalidClassException {
		if (!(getDocumentView() instanceof SignalView)) throw new InvalidClassException("document view for a signal document must be always of type SignalView");
		return (SignalView) getDocumentView();
	}
}

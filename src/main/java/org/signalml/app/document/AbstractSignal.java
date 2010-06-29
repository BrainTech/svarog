/* AbstractReaderSignal.java created 2007-11-22
 *
 */

package org.signalml.app.document;

import java.beans.IntrospectionException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.signalml.app.model.LabelledPropertyDescriptor;
import org.signalml.app.model.PropertyProvider;
import org.signalml.app.model.SignalParameterDescriptor;
import org.signalml.app.view.signal.SampleSourceUtils;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.signal.OriginalMultichannelSampleSource;
import org.signalml.domain.signal.SignalType;
import org.signalml.exception.SanityCheckException;
import org.signalml.exception.SignalMLException;
import org.springframework.context.MessageSourceResolvable;

/** AbstractReaderSignal
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractSignal extends AbstractDocument implements SignalDocument, MessageSourceResolvable, PropertyProvider {

	protected static final Logger logger = Logger.getLogger(AbstractSignal.class);

	protected OriginalMultichannelSampleSource sampleSource = null;

	protected TagDocument activeTag;
	protected List<TagDocument> tagDocuments = new LinkedList<TagDocument>();

	protected float pageSize = SignalParameterDescriptor.DEFAULT_PAGE_SIZE;
	protected int blocksPerPage = SignalParameterDescriptor.DEFAULT_BLOCKS_PER_PAGE;
	protected float blockSize = pageSize / blocksPerPage;

	protected SignalType type;

	protected Montage montage = null;
	private int namelessTagCounter = 1;

	public AbstractSignal(SignalType type) {
		this.type = type;
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
	public String getDefaultMessage() {
		return toString();
	}

	@Override
	public SignalType getType() {
		return type;
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
			montage = type.getConfigurer().createMontage(this);
		}
		return montage;
	}

	@Override
	public void setMontage(Montage montage) {
		if (this.montage != montage) {
			Montage oldMontage = this.montage;
			this.montage = montage;
			pcSupport.firePropertyChange(MONTAGE_PROPERTY, oldMontage, montage);
		}
	}

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
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		LinkedList<LabelledPropertyDescriptor> list = new LinkedList<LabelledPropertyDescriptor>();

		list.add(new LabelledPropertyDescriptor("property.signaldocument.samplingFrequency", "samplingFrequency", SignalMLDocument.class));
		list.add(new LabelledPropertyDescriptor("property.signaldocument.channelCount", "channelCount", SignalMLDocument.class));
		list.add(new LabelledPropertyDescriptor("property.signaldocument.pageSize", "pageSize", SignalMLDocument.class));
		list.add(new LabelledPropertyDescriptor("property.signaldocument.blocksPerPage", "blocksPerPage", SignalMLDocument.class));
		list.add(new LabelledPropertyDescriptor("property.signaldocument.blockSize", "blockSize", SignalMLDocument.class, "getBlockSize", null));
		list.add(new LabelledPropertyDescriptor("property.signaldocument.minSignalLength", "minSignalLength", SignalMLDocument.class, "getMinSignalLength", null));
		list.add(new LabelledPropertyDescriptor("property.signaldocument.maxSignalLength", "maxSignalLength", SignalMLDocument.class, "getMaxSignalLength", null));
		list.add(new LabelledPropertyDescriptor("property.signaldocument.pageCount", "pageCount", SignalMLDocument.class, "getPageCount", null));
		list.add(new LabelledPropertyDescriptor("property.signaldocument.blockCount", "blockCount", SignalMLDocument.class, "getBlockCount", null));

		return list;

	}

}

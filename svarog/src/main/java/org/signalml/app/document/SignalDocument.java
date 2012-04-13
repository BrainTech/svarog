/* SignalDocument.java created 2007-09-20
 *
 */

package org.signalml.app.document;

import java.io.InvalidClassException;
import java.util.List;

import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.signal.OriginalMultichannelSampleSource;
import org.signalml.domain.signal.SignalChecksum;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.change.listeners.PluginSignalChangeListener;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.signal.ExportedSignalDocument;

/**
 * Interface for a document with a signal.
 * Allows to access the basic data about the signal stored in this document,
 * such as:
 * <ul>
 * <li>get the {@link SignalType type} of the signal</li>
 * <li>get format in which signal was stored</li>
 * <li>get the size of the block and the page</li>
 * <li>get the number of channels</li>
 * <li>get and set all dependent tag documents and the active one</li>
 * <li>calculate and get {@link SignalChecksum checksums} of the signal</li>
 * <li>get and set the {@link Montage montage}</li>
 * <li>get the {@link #getMontageInfo() information} about montages for all
 * {@link SignalPlot plots} for this signal</li>
 * <li>get the labels of source and montage channels</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SignalDocument extends ExportedSignalDocument {

	//names of the properties
	public static final String PAGE_SIZE_PROPERTY = "pageSize";
	public static final String BLOCKS_PER_PAGE_PROPERTY = "blocksPerPage";
	public static final String MONTAGE_PROPERTY = "montage";
	public static final String ACTIVE_TAG_PROPERTY = "activeTag";
	public static final String TAG_DOCUMENTS_PROPERTY = "tagDocuments";

	@Override
	String getFormatName();

	@Override
	float getPageSize();

	@Override
	void setPageSize(float pageSize);

	@Override
	int getBlocksPerPage();

	@Override
	void setBlocksPerPage(int blocksPerPage);

	@Override
	public float getBlockSize();

	@Override
	float getSamplingFrequency();

	@Override
	public int getChannelCount();

	@Override
	public float getMinSignalLength();

	@Override
	public float getMaxSignalLength();

	@Override
	public int getPageCount();

	@Override
	public int getBlockCount();

	/**
	 * Gets the {@link OriginalMultichannelSampleSource source} of unprocessed
	 * (raw) samples for the signal stored in this document.
	 * @return the source of unprocessed (raw) samples for the signal stored
	 * in this document
	 */
	OriginalMultichannelSampleSource getSampleSource();

	/**
	 * Returns the list of all {@link TagDocument tag documents} dependent
	 * from this signal document.
	 * @return the list of all tag documents dependent from this signal document
	 */
	List<TagDocument> getTagDocuments();

	/**
	 * Adds a given {@link TagDocument tag document} to the list of
	 * {@link #addDependentDocument(Document) dependent documents} and
	 * to the list of tag documents.
	 * The added document is set as the {@link #getActiveTag() active}
	 * tag document.
	 * <p>
	 * If the document is already on the list of tag documents no action
	 * is taken.
	 * @param document the tag document to add
	 */
	void addTagDocument(TagDocument document);

	/**
	 * Removes a given {@link TagDocument tag document} from the list of
	 * {@link #addDependentDocument(Document) dependent documents} and
	 * from the list of tag documents.
	 * If the removed document is currently active the first document on
	 * the list of tag documents is set to be active.
	 * @param document the documetn to be removed
	 */
	void removeTagDocument(TagDocument document);

	/**
	 * Returns the active {@link TagDocument tag document}.
	 * @return the active tag document
	 */
	TagDocument getActiveTag();

	/**
	 * Sets the active (selected) {@link TagDocument tag document}.
	 * @param document the tag document to be set active
	 * @throws SanityCheckException if document is not on the list of
	 * dependent documents
	 */
	void setActiveTag(TagDocument document);

	/**
	 * Calculates the {@link SignalChecksum checksums} of given types.
	 * @param types an array of the names of checksum methods
	 * @param monitor the {@link SignalChecksumProgressMonitor monitor} of
	 * the progress of calculation the checksums.
	 * @return an array with calculated checksums.
	 * At index {@code i} located is a checksum of a type {@code types[i]}.
	 * @throws SignalMLException if one of the types in {@code types} is not
	 * supported or if I/O error occurs while calculation a checksum
	 */
	SignalChecksum[] getChecksums(String[] types, SignalChecksumProgressMonitor monitor) throws SignalMLException;

	/**
	 * Returns the {@link Montage montage} for the signal in this document.
	 * If it doesn't exist, it is created based on the {@link #getType() type}
	 * of the signal.
	 * @return the montage for the signal in this document
	 */
	Montage getMontage();

	boolean isMontageCreated();

	/**
	 * Sets the {@link Montage} montage for the signal in this document.
	 * @param montage montage for the signal in this document
	 */
	void setMontage(Montage montage);

	/**
	 * Returns the description of {@link Montage montages} for all
	 * {@link SignalPlot plots} associated with the {@link SignalView view}
	 * for this document.
	 * @return the description of montages for all plots associated with
	 * the view for this document
	 */
	String getMontageInfo();

	@Override
	SignalView getSignalView() throws InvalidClassException;

}

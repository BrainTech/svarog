/**
 * 
 */
package org.signalml.plugin.export.signal;

import java.io.InvalidClassException;
import java.util.List;

import org.signalml.plugin.export.view.ExportedSignalView;


/**
 * Exported part of the SignalDocument interface.
 * Allows to get the basic data about the signal stored in the document,
 * such as:
 * <ul>
 * <li>format in which signal was stored</li>
 * <li>size of the block and the page</li>
 * <li>number of channels</li>
 * <li>all dependent tag documents and the active one<li>
 * <li>labels of source and montage channels</li>
 * </ul>
 * @author Marcin Szumski
 */
public interface ExportedSignalDocument extends Document {

	String PAGE_SIZE_PROPERTY = "pageSize";
	String BLOCKS_PER_PAGE_PROPERTY = "blocksPerPage";
	String MONTAGE_PROPERTY = "montage";
	String ACTIVE_TAG_PROPERTY = "activeTag";
	String TAG_DOCUMENTS_PROPERTY = "tagDocuments";

	/**
	 * Returns the name of the format in which the signal was stored.
	 * @return the name of the format in which the signal was stored
	 */
	String getFormatName();

	/**
	 * Returns the size of the page of the signal stored in this document.
	 * @return the size of the page of the signal
	 */
	float getPageSize();

	/**
	 * Sets the size of the page of the signal stored in this document.
	 * @param pageSize the size of the page of the signal
	 */
	void setPageSize(float pageSize);

	/**
	 * Returns the number of blocks in the page for the signal stored
	 * in this document
	 * @return the number of blocks in the page
	 */
	int getBlocksPerPage();

	/**
	 * Sets the number of blocks in the page for the signal stored
	 * in this document.
	 * @param blocksPerPage the number of blocks in the page to set
	 */
	void setBlocksPerPage(int blocksPerPage);

	/**
	 * Returns the length (in seconds) of the block of the signal stored
	 * in this document
	 * @return the length (in seconds) of the block of the signal
	 */
	float getBlockSize();

	/**
	 * Returns the number of samples of the signal per second.
	 * @return the sampling frequency
	 */
	float getSamplingFrequency();

	/**
	 * Returns the number of channels in the signal stored
	 * in this document.
	 * @return the number of channels in the signal
	 */
	int getChannelCount();

	/**
	 * Returns the length of the shortest channel.
	 * @return the length of the shortest channel
	 */
	float getMinSignalLength();

	/**
	 * Returns the length of the longest channel.
	 * @return the length of the longest channel
	 */
	float getMaxSignalLength();

	/**
	 * Returns the number of whole pages of the signal stored in this document.
	 * @return the number of whole pages of the signal
	 */
	int getPageCount();

	/**
	 * Returns the number of whole blocks of the signal stored in this document.
	 * @return the number of whole blocks of the signal
	 */
	int getBlockCount();

	/**
	 * Returns the active {@link ExportedTagDocument tag document}.
	 * @return the active tag document
	 */
	ExportedTagDocument getActiveTag();
	
	/**
	 * Returns the list of all {@link ExportedTagDocument tag documents} dependent
	 * from this signal document.
	 * @return the list of all tag documents dependent from this signal document
	 */
	List<ExportedTagDocument> getExportedTagDocuments();

	/**
	 * Returns the array of labels of montage channels.
	 * At a given index there is a label for the channel of this index. 
	 * @return the array of labels of montage channels
	 */
	List<String> getMontageChannelLabels();
	
	/**
	 * Returns the array of labels of source channels.
	 * 
	 * A montage is made of 'source channels' and 'montage channels'. Source
	 * channels are channels from the source (e.g. a file). Montage
	 * channels are user defined channels. Montage channel set is a user view
	 * on source channel set. This is not a 1-1 mapping, as there can be,
	 * for instance, more montage channels than source channels and they can
	 * have different names. Montage channel can be a linear combination of a
	 * set of source channels in the same montage. In both channel collections
	 * (source channels and montage channels) labels must be unique.
	 * 
	 * This method returns the array of labels of source channels.
	 * At a given index there is a label for the channel of this index. 
	 * @return the array of labels of source channels
	 */
	List<String> getSourceChannelLabels();
	
	/**
	 * Returns a {@link ExportedSignalView signal view} for this document.
	 * @return a signal view for this document.
	 * @throws InvalidClassException if the view for this document has an invalid type
	 */
	ExportedSignalView getSignalView() throws InvalidClassException;
}
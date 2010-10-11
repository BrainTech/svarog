/**
 * 
 */
package org.signalml.plugin.export.signal;

import java.util.List;


/**
 * Exported part of the SignalDocument interface.
 * Allows to get the basic data about the signal stored in the document,
 * such as:
 * <ul>
 * <li>format in which signal was stored</li>
 * <li>size of the block and the page</li>
 * <li>number of channels</li>
 * <li>all tag documents and the active one<li>
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

}
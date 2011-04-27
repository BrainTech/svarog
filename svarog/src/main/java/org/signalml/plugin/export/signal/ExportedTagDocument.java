/**
 * 
 */
package org.signalml.plugin.export.signal;

import java.util.Set;

import org.signalml.app.document.FileBackedDocument;
import org.signalml.app.document.MutableDocument;

/**
 * This interface for a tag document allows to:
 * <ul>
 * <li>get the length of a block and a page,</li>
 * <li>get the {@link ExportedSignalDocument signal document} from which this
 * document is dependent,</li>
 * <li>get {@link ExportedTag tags} stored in this document.</li>
 * </ul>
 * @see Document
 * @author Marcin Szumski
 */
public interface ExportedTagDocument extends Document, MutableDocument, FileBackedDocument {

	/**
	 * Returns the length of a block of the signal in seconds.
	 * @return the length of a block of the signal in seconds
	 */
	float getBlockSize();

	/**
	 * Returns the number of blocks in a page of the signal.
	 * @return the number of blocks in a page of the signal
	 */
	int getBlocksPerPage();

	/**
	 * Returns the length of a page of the signal in seconds.
	 * @return the length of a page of the signal in seconds
	 */
	float getPageSize();

	/**
	 * Returns a {@link ExportedSignalDocument signal document} from which this
	 * document is dependent.
	 * @return a signal document from which this document is dependent.
	 */
	ExportedSignalDocument getParent();

	String getFallbackName();

	/**
	 * Returns the number of {@link ExportedTag tags} in this document.
	 * @return the number of tags in this document
	 */
	int getTagCount();
	
	/**
	 * Returns the set of {@link ExportedTag tags} that are stored in this document.
	 * @return the set of tags that are stored in this document
	 */
	Set<ExportedTag> getSetOfTags();
	
	/**
	 * Returns a set containing all styles in this document.
	 * @return a set containing all styles in this document
	 */
	Set<ExportedTagStyle> getTagStyles();
	

}
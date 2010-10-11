/**
 * 
 */
package org.signalml.plugin.export.view;

import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagDocument;

/**
 * Interface for a positioned tag.
 * Allows to return a {@link ExportedTag tag} and an index of
 * a {@link ExportedTagDocument document} in which this tag is stored.
 * 
 * @author Marcin Szumski
 */
public interface ExportedPositionedTag {

	/**
	 * Returns the actual {@link ExportedTag tag} associated with this
	 * positioned tag.
	 * @return the actual tag associated with this positioned
	 * tag
	 */
	ExportedTag getTag();

	/**
	 * Returns the index of a the {@link ExportedTagDocument document} in which
	 * this {@link ExportedTag tag} is stored in an array of documents dependent
	 * from a signal.
	 * @return the index of a tag document
	 */
	int getTagPositionIndex();

	/**
	 * Compares the given positioned tag to this positioned tag.
	 * @param o the positioned tag to be comapred to this one
	 * @return difference between indexes of tag documents or,
	 * if indexes are the same, the result of comparison between
	 * tags.
	 */
	int compareTo(ExportedPositionedTag o);

}
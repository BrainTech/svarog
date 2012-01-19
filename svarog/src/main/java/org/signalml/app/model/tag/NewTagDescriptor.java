/* NewTagDescriptor.java created 2007-10-14
 *
 */

package org.signalml.app.model.tag;

import java.io.File;

import org.signalml.app.model.signal.PagingParameterDescriptor;
import org.signalml.domain.tag.StyledTagSet;

/** NewTagDescriptor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NewTagDescriptor extends PagingParameterDescriptor {

	public enum NewTagTypeMode {
		EMPTY,
		DEFAULT_SLEEP,
		PRESET,
		FROM_FILE
	}

	private NewTagTypeMode mode = NewTagTypeMode.DEFAULT_SLEEP;
	private File file;

	/**
	 * Styles from this {@link StyledTagSet} are used for the new tag.
	 */
	private StyledTagSet tagStylesPreset;

	public NewTagDescriptor() {}

	public NewTagDescriptor(NewTagTypeMode mode, File file) {
		this.mode = mode;
		this.file = file;
	}

	public NewTagTypeMode getMode() {
		return mode;
	}

	public void setMode(NewTagTypeMode mode) {
		this.mode = mode;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Sets the {@link StyledTagSet} from which tag styles are copied into
	 * the new tag.
	 * @param tagStylesPreset
	 */
	public void setTagStylesPreset(StyledTagSet tagStylesPreset) {
		this.tagStylesPreset = tagStylesPreset;
	}

	/**
	 * Returns the {@link StyledTagSet} from which tag styles are copied
	 * into the new tag document.
	 * @return the StyledTagSet with styles to be copied
	 */
	public StyledTagSet getTagStylesPreset() {
		return tagStylesPreset;
	}

}

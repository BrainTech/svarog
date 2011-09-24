/* NewTagDescriptor.java created 2007-10-14
 *
 */

package org.signalml.app.model;

import java.io.File;
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

	public void setTagStylesPreset(StyledTagSet tagStylesPreset) {
		this.tagStylesPreset = tagStylesPreset;
	}

	public StyledTagSet getTagStylesPreset() {
		return tagStylesPreset;
	}

}

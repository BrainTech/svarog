/* TagStylePaletteDescriptor.java created 2007-11-10
 * 
 */

package org.signalml.app.model;

import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.TagStyle;

/** TagStylePaletteDescriptor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStylePaletteDescriptor {

	private StyledTagSet tagSet;
	private TagStyle style;
	private boolean changed = false;
	
	public TagStylePaletteDescriptor(StyledTagSet tagSet, TagStyle style) {
		this.tagSet = tagSet;
		this.style = style;
	}

	public StyledTagSet getTagSet() {
		return tagSet;
	}

	public TagStyle getStyle() {
		return style;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}	
	
}

/* TagStyleMenu.java created 2007-10-16
 *
 */

package org.signalml.app.view.tag;

import java.util.Collection;
import javax.swing.JMenu;
import org.signalml.app.action.tag.TagSelectionAction;
import org.signalml.app.util.IconUtils;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.TagStyle;

/** TagStyleMenu
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStyleMenu extends JMenu {

	private static final long serialVersionUID = 1L;

	private StyledTagSet tagSet;
	private SignalSelectionType type;
	private TagIconProducer tagIconProducer;

	private TagSelectionAction tagSelectionAction;

	public TagStyleMenu(TagSelectionAction tagSelectionAction, TagIconProducer tagIconProducer) {
		super((String) tagSelectionAction.getValue(TagSelectionAction.NAME));
		this.tagSelectionAction = tagSelectionAction;
		this.tagIconProducer = tagIconProducer;
		setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/tag.png"));
	}

	public StyledTagSet getTagSet() {
		return tagSet;
	}

	public void setTagSet(StyledTagSet tagSet) {
		if (this.tagSet != tagSet) {
			this.tagSet = tagSet;
			recreateMenu();
		}
	}

	public SignalSelectionType getType() {
		return type;
	}

	public void setType(SignalSelectionType type) {
		if (this.type != type) {
			this.type = type;
			recreateMenu();
		}
	}

	public void setParameters(StyledTagSet tagSet, SignalSelectionType type) {
		if (this.tagSet != tagSet || this.type != type) {
			this.tagSet = tagSet;
			this.type = type;
			recreateMenu();
		}
	}

	private void recreateMenu() {
		removeAll();
		add(new TagEraserMenuItem(tagSelectionAction));
		Collection<TagStyle> styles = tagSet.getStyles(type);
		for (TagStyle style : styles) {
			add(new TagStyleMenuItem(tagSelectionAction, style, tagIconProducer));
		}
	}

}

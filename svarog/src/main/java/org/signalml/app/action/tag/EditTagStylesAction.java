/* EditTagStylesAction.java created 2007-11-10
 *
 */

package org.signalml.app.action.tag;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.signalml.app.action.AbstractFocusableSignalMLAction;
import org.signalml.app.action.selector.TagStyleFocusSelector;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.tag.TagStylePaletteDescriptor;
import org.signalml.app.view.components.dialogs.TagStylePaletteDialog;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;

/** EditTagStylesAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EditTagStylesAction extends AbstractFocusableSignalMLAction<TagStyleFocusSelector> {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(EditTagStylesAction.class);

	private TagStylePaletteDialog tagStylePaletteDialog;

	public EditTagStylesAction(TagStyleFocusSelector tagStyleFocusSelector) {
		super(tagStyleFocusSelector);
		setText(_("Tag styles"));
		setToolTip(_("Edit tag styles"));
		setIconPath("org/signalml/app/icon/palette.png");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		TagStyleFocusSelector tagStyleFocusSelector = getActionFocusSelector();

		TagDocument tagDocument = tagStyleFocusSelector.getActiveTagDocument();
		TagStyle style = tagStyleFocusSelector.getActiveTagStyle();

		if (tagDocument == null) {
			logger.warn("Target tag document doesn't exist");
			return;
		}

		TagStylePaletteDescriptor descriptor = new TagStylePaletteDescriptor(tagDocument.getTagSet(), style);

		boolean ok = tagStylePaletteDialog.showDialog(descriptor, true);
		if (!ok) {
			return;
		}

		if (descriptor.isChanged()) {
			tagDocument.invalidate();
		}

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(getActionFocusSelector().getActiveTagDocument() != null);
	}

	public TagStylePaletteDialog getTagStylePaletteDialog() {
		return tagStylePaletteDialog;
	}

	public void setTagStylePaletteDialog(TagStylePaletteDialog tagStylePaletteDialog) {
		this.tagStylePaletteDialog = tagStylePaletteDialog;
	}

}

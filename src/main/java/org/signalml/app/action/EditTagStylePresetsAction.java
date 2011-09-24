/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.signalml.app.action;

import java.awt.event.ActionEvent;
import org.signalml.app.action.selector.TagStyleFocusSelector;
import org.signalml.app.model.TagStylePaletteDescriptor;
import org.signalml.app.view.dialog.TagStylePaletteDialog;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class EditTagStylePresetsAction extends AbstractFocusableSignalMLAction<TagStyleFocusSelector> {

	private TagStylePaletteDialog tagStylePaletteDialog;

	public EditTagStylePresetsAction(MessageSourceAccessor messageSource, TagStyleFocusSelector tagStyleFocusSelector) {
		super(messageSource, tagStyleFocusSelector);
		setText("action.editTagStylePresets");
		setToolTip("action.editTagStylePresetsToolTip");
		setIconPath("org/signalml/app/icon/palette.png");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		TagStylePaletteDescriptor descriptor = new TagStylePaletteDescriptor(null, null);
		tagStylePaletteDialog.showDialog(descriptor, true);
	}

	public TagStylePaletteDialog getTagStylePaletteDialog() {
		return tagStylePaletteDialog;
	}

	public void setTagStylePaletteDialog(TagStylePaletteDialog tagStylePaletteDialog) {
		this.tagStylePaletteDialog = tagStylePaletteDialog;
	}

}

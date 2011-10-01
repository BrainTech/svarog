/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.signalml.app.view.dialog;

import java.awt.Window;
import org.signalml.app.config.preset.PresetManager;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class TagStylePresetDialog extends TagStylePaletteDialog {

	/**
	 * Constructor. Sets message source.
	 * @param messageSource message source to set
	 */
	public TagStylePresetDialog(MessageSourceAccessor messageSource, PresetManager presetManager) {
		super(messageSource, presetManager);
	}

	/**
	 * Constructor. Sets message source, parent window and if this dialog
	 * blocks top-level windows.
	 * @param messageSource message source to set
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public TagStylePresetDialog(MessageSourceAccessor messageSource, PresetManager presetManager, Window w, boolean isModal) {
		super(messageSource, presetManager, w, isModal);
	}

	@Override
	public boolean arePresetsActive() {
		return true;
	}

}

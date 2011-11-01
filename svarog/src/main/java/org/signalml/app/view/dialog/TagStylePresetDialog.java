package org.signalml.app.view.dialog;

import java.awt.Window;
import org.signalml.app.config.preset.PresetManager;

/**
 * A dialog for creating/editing tag style presets.
 *
 * @author Piotr Szachewicz
 */
public class TagStylePresetDialog extends TagStylePaletteDialog {

	/**
	 * Constructor. Sets message source.
	 * @param messageSource message source to set
	 */
	public  TagStylePresetDialog( PresetManager presetManager) {
		super( presetManager);
	}

	/**
	 * Constructor. Sets message source, parent window and if this dialog
	 * blocks top-level windows.
	 * @param messageSource message source to set
	 * @param presetManager preset manager for this dialog
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public  TagStylePresetDialog( PresetManager presetManager, Window w, boolean isModal) {
		super( presetManager, w, isModal);
	}

}

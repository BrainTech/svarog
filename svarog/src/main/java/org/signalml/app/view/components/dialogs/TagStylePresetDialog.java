package org.signalml.app.view.components.dialogs;

import java.awt.Window;
import org.signalml.app.config.preset.PresetManager;

/**
 * A dialog for creating/editing tag style presets.
 *
 * @author Piotr Szachewicz
 */
public class TagStylePresetDialog extends TagStylePaletteDialog {
	/**
	 * Constructor. Sets parent window and if this dialog
	 * blocks top-level windows.
	 * @param presetManager preset manager for this dialog
	 * @param w the parent window or null if there is no parent
	 * @param dialog blocks top-level windows if true
	 */
	public TagStylePresetDialog(PresetManager presetManager, Window w, boolean isModal) {
		super(presetManager, w, isModal);
	}
}

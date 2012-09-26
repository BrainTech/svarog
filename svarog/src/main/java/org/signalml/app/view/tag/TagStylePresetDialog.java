package org.signalml.app.view.tag;

import java.awt.Window;

/**
 * A dialog for creating/editing tag style presets.
 *
 * @author Piotr Szachewicz
 */
public class TagStylePresetDialog extends TagStylePaletteDialog {
	/**
	 * Constructor. Sets parent window and if this dialog
	 * blocks top-level windows.
	 * @param w the parent window or null if there is no parent
	 * @param dialog blocks top-level windows if true
	 */
	public TagStylePresetDialog(Window w, boolean isModal) {
		super(w, isModal);
	}
}

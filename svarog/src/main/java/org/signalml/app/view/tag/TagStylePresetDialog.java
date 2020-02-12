package org.signalml.app.view.tag;

import java.awt.Window;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * A dialog for creating/editing tag style presets.
 *
 * @author Piotr Szachewicz
 */
public class TagStylePresetDialog extends TagStylePaletteDialog {

	/**
	 * Constructor. Sets parent window and if this dialog blocks top-level
	 * windows.
	 *
	 * @param w the parent window or null if there is no parent
	 * @param dialog blocks top-level windows if true
	 */
	public TagStylePresetDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	/**
	 * we don't need ok/cancel apply panel here, because we are just a tag style
	 * preset editor
	 */
	@Override
	protected JPanel createButtonPane() {
		//we need to call it to setup the dialog properly
		super.createButtonPane();
		JPanel controlPane = new JPanel();
		controlPane.setLayout(new BoxLayout(controlPane, BoxLayout.X_AXIS));
		controlPane.setBorder(new EmptyBorder(3, 0, 0, 0));
		controlPane.add(Box.createHorizontalGlue());
		if (isCancellable()) {
			controlPane.add(Box.createHorizontalStrut(3));
			JButton button = getCancelButton();
			button.setText(_("Close"));
			controlPane.add(button);
		}
		return controlPane;

	}
}

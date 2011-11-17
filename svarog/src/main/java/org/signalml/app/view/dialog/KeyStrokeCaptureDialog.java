/* KeyStrokeCaptureDialog.java created 2007-11-13
 *
 */

package org.signalml.app.view.dialog;

import static org.signalml.app.SvarogI18n._;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.SignalMLException;

/**
 * Dialog which allows to capture the key stroke (combination of
 * SHIFT/CONTROL/ALT with another key).
 * The visual part contains only one label, which tells the user to wait.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class KeyStrokeCaptureDialog extends org.signalml.app.view.dialog.AbstractSvarogDialog  {

	private static final long serialVersionUID = 1L;

	/**
	 * the last captured key stroke
	 */
	private KeyStroke currentStroke = null;

	/**
	 * Constructor. Sets parent window.
	 * @param w the parent window or null if there is no parent
	 */
	public KeyStrokeCaptureDialog(Window w) {
		super(w, true);
	}

	/**
	 * This dialog has no model.
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		// do nothing
	}

	/**
	 * This dialog has no model.
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		// do nothing
	}

	/**
	 * Creates the interface for this dialog with BoxLayout and only one label,
	 * which tells the user to wait.
	 * Sets the cursor to the {@code WAIT_CURSOR}.
	 */
	@Override
	public JComponent createInterface() {

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		CompoundBorder border = new CompoundBorder(
		        new LineBorder(Color.LIGHT_GRAY),
		        new EmptyBorder(10,10,10,10)
		);
		p.setBorder(border);

		JLabel label = new JLabel(_("Press a key..."));
		label.setIcon(IconUtils.getQuestionIcon());
		label.setAlignmentX(Component.CENTER_ALIGNMENT);

		p.add(label);

		p.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		return p;

	}

	/**
	 * This dialog has no control panel.
	 */
	@Override
	public boolean isControlPanelEquipped() {
		return false;
	}

	/**
	 * This dialog has no cancel button.
	 */
	@Override
	public boolean isCancellable() {
		return false;
	}

	/**
	 * This dialog is not canceled when escape is pressed.
	 */
	@Override
	public boolean isCancelOnEscape() {
		return false;
	}

	/**
	 * This dialog has no model so {@code class} must be {@code null}.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return (clazz == null);
	}

	/**
	 * Initializes this dialog - adds a key listener to this dialog, which
	 * (when the key is captured) remembers this key (combination of
	 * SHIFT/CONTROL/ALT with another key) and makes this dialog
	 * invisible.
	 */
	@Override
	protected void initialize() {
		setUndecorated(true);
		super.initialize();

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if (keyCode != KeyEvent.VK_SHIFT && keyCode != KeyEvent.VK_CONTROL && keyCode != KeyEvent.VK_ALT) {
					currentStroke = KeyStroke.getKeyStrokeForEvent(e);
					setVisible(false);
				}
			}
		});

	}

	/**
	 * Sets that there was no captured key stroke (combination of
	 * SHIFT/CONTROL/ALT with another key).
	 */
	@Override
	protected void resetDialog() {
		currentStroke = null;
	}

	/**
	 * Shows this dialog and returns the key stroke (combination of
	 * SHIFT/CONTROL/ALT with another key) captured by it.
	 * @return the captured key stroke
	 */
	public KeyStroke captureKeyStroke() {

		currentStroke = null;
		showDialog(null, true);

		return currentStroke;

	}

	/**
	 * Shows this dialog and returns the key stroke (combination of
	 * SHIFT/CONTROL/ALT with another key) captured by it.
	 * If the captured key stroke is escape - null is returned
	 * @return the captured key stroke or null if the captured key stroke was
	 * {@code KeyEvent#VK_ESCAPE}
	 */
	public KeyStroke captureKeyStrokeWithEscAsCancel() {

		currentStroke = null;
		showDialog(null, true);
		if (currentStroke.equals(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false))) {
			currentStroke = null;
		}

		return currentStroke;

	}

}

/* KeyStrokeCaptureDialog.java created 2007-11-13
 *
 */

package org.signalml.app.view.dialog;

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
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/** KeyStrokeCaptureDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class KeyStrokeCaptureDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private KeyStroke currentStroke = null;

	public KeyStrokeCaptureDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public KeyStrokeCaptureDialog(MessageSourceAccessor messageSource, Window w) {
		super(messageSource, w, true);
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		// do nothing
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		// do nothing
	}

	@Override
	public JComponent createInterface() {

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		CompoundBorder border = new CompoundBorder(
		        new LineBorder(Color.LIGHT_GRAY),
		        new EmptyBorder(10,10,10,10)
		);
		p.setBorder(border);

		JLabel label = new JLabel(messageSource.getMessage("pressAnyKey"));
		label.setIcon(IconUtils.getQuestionIcon());
		label.setAlignmentX(Component.CENTER_ALIGNMENT);

		p.add(label);

		p.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		return p;

	}

	@Override
	public boolean isControlPanelEquipped() {
		return false;
	}

	@Override
	public boolean isCancellable() {
		return false;
	}

	@Override
	public boolean isCancelOnEscape() {
		return false;
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return (clazz == null);
	}

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

	@Override
	protected void resetDialog() {
		currentStroke = null;
	}

	public KeyStroke captureKeyStroke() {

		currentStroke = null;
		showDialog(null, true);

		return currentStroke;

	}

	public KeyStroke captureKeyStrokeWithEscAsCancel() {

		currentStroke = null;
		showDialog(null, true);
		if (currentStroke.equals(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false))) {
			currentStroke = null;
		}

		return currentStroke;

	}

}

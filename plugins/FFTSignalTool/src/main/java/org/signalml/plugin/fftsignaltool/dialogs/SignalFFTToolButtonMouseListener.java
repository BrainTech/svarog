package org.signalml.plugin.fftsignaltool.dialogs;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JToggleButton;
import javax.swing.Timer;

import org.signalml.plugin.export.signal.SignalTool;
import org.signalml.plugin.export.signal.SignalToolButtonListener;
import org.signalml.plugin.fftsignaltool.SignalFFTTool;

/**
 * Listener which listens for actions that occur on the JToggleButton associated
 * with {@link SignalFFTTool}.
 * <p>
 * This listener, when the mouse is pressed shows the {@link
 * SignalFFTSettingsPopupDialog} after the specified time (400 ms).
 * If the user releases the mouse before that time, no action is taken.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.,
 * Marcin Szumski
 */
public class SignalFFTToolButtonMouseListener extends MouseAdapter implements SignalToolButtonListener {

	/**
	 * the timer used to wait a specified time (400 s) before the
	 * {@link SignalFFTSettingsPopupDialog dialog} with settings is shown;
	 * the timer is stopped if the user releases the mouse button
	 */
	private Timer timer;

	/**
	 * the {@link SignalFFTTool signal tool}
	 */
	private SignalFFTTool signalFFTTool;
	/**
	 * the button on which this listener listens
	 */
	private JToggleButton button;

	/**
	 * Constructor. Sets the source of messages.
	 */
	public SignalFFTToolButtonMouseListener() {
	}

	/**
	 * the listener, which shows the {@link SignalFFTSettingsPopupDialog},
	 * when the time specified in the {@link #timer timer} elapses
	 */
	ActionListener timerListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			SignalFFTSettingsPopupDialog dialog = new SignalFFTSettingsPopupDialog(null, true);
			Point location = button.getLocationOnScreen();
			dialog.setLocation(location);
			button.doClick();
			dialog.showDialog(signalFFTTool);
		}
	};

	/**
	 * Activates the timer which shows the {@link SignalFFTSettingsPopupDialog}
	 * after the specified time (400 ms)
	 */
	@Override
	public void mousePressed(MouseEvent e) {

		if (timer == null) {
			timer = new Timer(400, timerListener); // popup after 400 ms
			timer.setRepeats(false);
		}

		timer.start();

	}

	/**
	 * Stops the timer, so that the {@link SignalFFTSettingsPopupDialog}
	 * will not be shown.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (timer != null) {
			timer.stop();
		}
	}

	/**
	 * Stops the timer, so that the {@link SignalFFTSettingsPopupDialog}
	 * will not be shown.
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		if (timer != null) {
			timer.stop();
		}
	}

	/**
	 * Creates the copy of this listener and stores provided parameters in it.
	 * <p>This listener must be associated with {@link SignalFFTTool}, so that
	 * the {@code tool} must be of that type.
	 * @throws RuntimeException if the {@code tool} is not of type {@link
	 * SignalFFTTool}
	 */
	@Override
	public SignalToolButtonListener createCopy(SignalTool tool, JToggleButton button) {
		SignalFFTToolButtonMouseListener copy = new SignalFFTToolButtonMouseListener();
		copy.setButton(button);
		if (tool instanceof SignalFFTTool) copy.setSignalFFTTool((SignalFFTTool) tool);
		else throw new RuntimeException("tool should have type SignalFFTTool");
		return copy;
	}

	/**
	 * Sets the {@link SignalFFTTool tool} with which this listener is associated.
	 * @param signalFFTTool the tool with which this listener is associated
	 */
	public void setSignalFFTTool(SignalFFTTool signalFFTTool) {
		this.signalFFTTool = signalFFTTool;
	}

	/**
	 * Sets the button on which this listener listens.
	 * @param button the button on which this listener listens
	 */
	public void setButton(JToggleButton button) {
		this.button = button;
	}
}

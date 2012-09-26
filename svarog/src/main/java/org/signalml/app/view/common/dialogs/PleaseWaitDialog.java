/* PleaseWaitDialog.java created 2007-10-06
 *
 */

package org.signalml.app.view.common.dialogs;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.SignalMLException;

/**
 * Dialog shown when the user has to wait.
 * Contains 3 elements:
 * <ul> <li>the label that informs the user he should wait,</li>
 * <li>the progress bar, which may display actual progress or just
 * the animation,</li>
 * <li>the label that describes the action in progress.</li>
 * </ul>
 * This dialog can be shown after the specified time without it (so that maybe
 * the operation finishes before it is needed).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class PleaseWaitDialog extends AbstractDialog  {

	private static final long serialVersionUID = 1L;

	/**
	 * the label that describes the action in progress
	 */
	private JLabel activityLabel;
	/**
	 * the progress bar.
	 * May display actual progress or just the animation.
	 */
	private JProgressBar progressBar;

	/**
	 * the timer that shows the dialog
	 */
	private Timer showTimer;

	/**
	 * the listener for the {@link #showTimer} that shows this dialog.
	 */
	private ActionListener showListener;

	/**
	 * the object that owns this dialog - usually a {@link SwingWorker}
	 */
	private Object currentOwner;

	/**
	 * Constructor. Sets parent window.
	 * This dialog blocks top-level windows.
	 * @param w the parent window or null if there is no parent
	 */
	public PleaseWaitDialog(Window w) {
		super(w, true);
	}

	/**
	 * Does nothing.
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		// do nothing
	}

	/**
	 * Does nothing.
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		// do nothing
	}

	/**
	 * Creates the interface of this dialog.
	 * Dialog has a {@link BoxLayout} and contains (from top to bottom):
	 * <ul>
	 * <li>the label that informs the user he should wait,</li>
	 * <li>the progress bar, which may display actual progress or just
	 * the animation,</li>
	 * <li>the label that describes the action in progress.</li>
	 * </ul>
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

		JLabel label = new JLabel(_("Please wait..."));
		label.setIcon(IconUtils.getInfoIcon());
		label.setAlignmentX(Component.CENTER_ALIGNMENT);

		progressBar = new JProgressBar();
		progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
		Dimension progressSize = new Dimension(220,20);
		progressBar.setPreferredSize(progressSize);
		progressBar.setMinimumSize(progressSize);
		progressBar.setMaximumSize(progressSize);

		activityLabel = new JLabel("activity");
		activityLabel.setMinimumSize(new Dimension(250,1));
		activityLabel.setFont(activityLabel.getFont().deriveFont(Font.PLAIN, 10F));
		activityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		p.add(label);
		p.add(progressBar);
		p.add(activityLabel);

		p.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		return p;

	}

	/**
	 * Returns that there is no control panel.
	 */
	@Override
	public boolean isControlPanelEquipped() {
		return false;
	}

	/**
	 * The dialog can not be canceled.
	 */
	@Override
	public boolean isCancellable() {
		return false;
	}

	/**
	 * There is no model, so the {@code clazz} must be {@code null}.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return (clazz == null);
	}

	/**
	 * Initializes this dialog.
	 * Adds a WindowListener to this dialog.
	 * If the owner of this dialog is a {@link SwingWorker},
	 * when the worker is done makes this dialog invisible.
	 */
	@Override
	protected void initialize() {
		setUndecorated(true);
		super.initialize();

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e) {
				if (currentOwner != null) {
					if (currentOwner instanceof SwingWorker) {
						if (((SwingWorker<?,?>) currentOwner).isDone()) {
							setVisible(false);
						}
					}
				}
			}

		});

	}

	/**
	 * Sets that the progress bar should display only the animation instead
	 * of a real progress.
	 */
	public void configureForIndeterminate() {
		progressBar.setValue(0);
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(false);
	}

	/**
	 * Sets that the progress bar should display only the animation instead
	 * of a real progress.
	 */
	public void configureForIndeterminateSimulated() {

		progressBar.setValue(0);
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(false);

	}

	/**
	 * Sets that the progress bar should a real progress from a given range.
	 * Sets the value of this progress to the given value
	 * @param min the left endpoint of the interval
	 * @param max the right endpoint of the interval
	 * @param value the current value of the progress
	 */
	public void configureForDeterminate(int min, int max, int value) {
		progressBar.setMinimum(min);
		progressBar.setMaximum(max);
		progressBar.setValue(value);
		progressBar.setIndeterminate(false);
		progressBar.setStringPainted(true);
	}

	/**
	 * Sets the left endpoint of the progress interval.
	 * @param min the left endpoint of the interval
	 */
	public void setMinimum(int min) {
		progressBar.setMinimum(min);
	}

	/**
	 * Sets the right endpoint of the progress interval
	 * @param max the right endpoint of the interval
	 */
	public void setMaximum(int max) {
		progressBar.setMaximum(max);
	}

	/**
	 * Sets the current value of the progress.
	 * @param value the current value of the progress
	 */
	public void setProgress(int value) {
		progressBar.setValue(value);
	}

	/**
	 * Sets the text that describes the action in progress.
	 * @param activity the text that describes the action in progress
	 */
	public void setActivity(String activity) {
		activityLabel.setText(activity);
	}

	/**
	 * This method waits for specified amount of time while allowing the
	 * application to continue
	 * THEN locks the application on a modal dialog
	 * @param parent the window parent to this dialog
	 * @param noDialogTimeout the amount of milliseconds without the dialog;
	 * after that time passes this dialog is shown
	 */
	public void showDialogIn(final Component parent, int noDialogTimeout) {

		currentOwner = null;

		if (showListener == null) {
			showListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					centerInComponent(parent, 0.5, 0.5);
					showDialog(null, false);
				}
			};
		}

		if (showTimer == null) {
			showTimer = new Timer(0, showListener);
			showTimer.setRepeats(false);
		}
		showTimer.setInitialDelay(noDialogTimeout);
		showTimer.start();

	}

	/**
	 * Waits a given amount of time without the dialog and when that time
	 * passes this dialog is shown.
	 * The application is locked on a modal dialog that cannot be closed.
	 * During the time without the dialog the application is WAITING in
	 * event dispatching thread.
	 * @param parent the window parent to this dialog
	 * @param noDialogTimeout the amount of milliseconds without the dialog;
	 * after that time passes this dialog is shown
	 * @param worker the worker in which the computation is performed
	 */
	@SuppressWarnings("unchecked")
	public void waitAndShowDialogIn(Component parent, int noDialogTimeout, SwingWorker worker) {

		logger.debug("Start waitAndShowDialogIn for [" + worker + "] timeout [" + noDialogTimeout + "]");

		// FIXME [MD] probably should be cleared if dialog not shown after all
		currentOwner = worker;

		if (noDialogTimeout > 0) {
			boolean repeat;
			do {
				repeat = false;
				try {
					logger.debug("Entering wait");
					worker.get(noDialogTimeout, TimeUnit.MILLISECONDS);
					logger.debug("Exiting wait");
				} catch (InterruptedException ex) {
					repeat = true;
				} catch (ExecutionException ex) {
					// this is done only in order to wait for the completion, so disregard exception
					// (it was serviced in the worker's done method)
					return;
				} catch (TimeoutException ex) {
					// exit and proceed to show dialog
				}
			} while (repeat);
		}

		if (!worker.isDone()) {
			logger.debug("Showing dialog");
			centerInComponent(parent, 0.5, 0.5);
			showDialog(null, false);
		}

		logger.debug("End waitAndShowDialogIn for [" + worker + "]");

	}

	/**
	 * Shows this dialog and locks the application on it.
	 * @param parent the window parent to this dialog
	 */
	public void showDialogNow(Component parent) {

		currentOwner = null;

		centerInComponent(parent, 0.5, 0.5);
		showDialog(null, false);

	}

	/**
	 * Cancels showDialogIn, but not other ways of timed showing.
	 */
	public void cancelShowing() {
		if (showTimer != null && showTimer.isRunning()) {
			showTimer.stop();
		}
		currentOwner = null;
	}

	/**
	 * Makes this dialog invisible.
	 */
	public void release() {
		setVisible(false);
		currentOwner = null;
	}

	/**
	 * Makes the dialog invisible if the given object owns it.
	 * @param owner if the object own this dialog, this dialog is made
	 * invisible
	 */
	public void releaseIfOwnedBy(Object owner) {
		logger.debug("releaseIfOwnedBy for [" + owner + "]");
		if (currentOwner == owner) {
			logger.debug("releasing for [" + owner + "]");
			setVisible(false);
			currentOwner = null;
		}
	}

}

/* PleaseWaitDialog.java created 2007-10-06
 *
 */

package org.signalml.app.view.dialog;

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
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/** PleaseWaitDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class PleaseWaitDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private JLabel activityLabel;
	private JProgressBar progressBar;

	private Timer showTimer;
	private ActionListener showListener;

	private Object currentOwner;

	public PleaseWaitDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public PleaseWaitDialog(MessageSourceAccessor messageSource, Window w) {
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

		JLabel label = new JLabel(messageSource.getMessage("pleaseWait"));
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

	@Override
	public boolean isControlPanelEquipped() {
		return false;
	}

	@Override
	public boolean isCancellable() {
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

	public void configureForIndeterminate() {
		progressBar.setValue(0);
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(false);
	}

	public void configureForIndeterminateSimulated() {

		progressBar.setValue(0);
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(false);

	}

	public void configureForDeterminate(int min, int max, int value) {
		progressBar.setMinimum(min);
		progressBar.setMaximum(max);
		progressBar.setValue(value);
		progressBar.setIndeterminate(false);
		progressBar.setStringPainted(true);
	}

	public void setMinimum(int min) {
		progressBar.setMinimum(min);
	}

	public void setMaximum(int max) {
		progressBar.setMaximum(max);
	}

	public void setProgress(int value) {
		progressBar.setValue(value);
	}

	public void setActivity(String activity) {
		activityLabel.setText(activity);
	}

	// this method waits for specified amout of time while allowing the application to continue
	// THEN locks the application on a modal dialog
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

	// this method locks the application on a modal dialog that cannot be closed
	// the dialog is not shown until noDialogTimeout has elapsed, during which time the
	// application is WAITING in event dispatching thread
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

	// this method locks the application on a modal dialog that cannot be closed
	public void showDialogNow(Component parent) {

		currentOwner = null;

		centerInComponent(parent, 0.5, 0.5);
		showDialog(null, false);

	}

	// this cancels showDialogIn, not other ways of timed showing
	public void cancelShowing() {
		if (showTimer != null && showTimer.isRunning()) {
			showTimer.stop();
		}
		currentOwner = null;
	}

	public void release() {
		setVisible(false);
		currentOwner = null;
	}

	public void releaseIfOwnedBy(Object owner) {
		logger.debug("releaseIfOwnedBy for [" + owner + "]");
		if (currentOwner == owner) {
			logger.debug("releasing for [" + owner + "]");
			setVisible(false);
			currentOwner = null;
		}
	}

}

/* TaskStatusDialog.java created 2007-10-18
 *
 */

package org.signalml.app.view.dialog;

import static org.signalml.app.SvarogApplication._;
import static org.signalml.app.SvarogApplication._R;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.SignalMLOperationMode;
import org.signalml.app.action.AbortTaskAction;
import org.signalml.app.action.GetTaskErrorAction;
import org.signalml.app.action.GetTaskResultAction;
import org.signalml.app.action.ResumeTaskAction;
import org.signalml.app.action.SuspendTaskAction;
import org.signalml.app.action.selector.ActionFocusListener;
import org.signalml.app.action.selector.ActionFocusSupport;
import org.signalml.app.action.selector.TaskFocusSelector;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.task.ApplicationTaskManager;
import org.signalml.app.util.IconUtils;
import org.signalml.method.Method;
import org.signalml.method.SuspendableMethod;
import org.signalml.method.TrackableMethod;
import org.signalml.method.iterator.MethodIteratorMethod;
import org.signalml.plugin.export.method.SvarogTaskStatusDialog;
import org.signalml.task.Task;
import org.signalml.task.TaskEvent;
import org.signalml.task.TaskEventListener;
import org.signalml.task.TaskStatus;
import org.springframework.context.MessageSourceResolvable;

/**
 * Dialog which displays the progress of the {@link Task}.
 * Contains the label with the status of the task (icon and text) and
 * the list of progress bars with the progress and the time left.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TaskStatusDialog extends JDialog implements TaskEventListener, TaskFocusSelector, SvarogTaskStatusDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link ActionFocusSupport support} to listen for focus changes
	 */
	private ActionFocusSupport afSupport = new ActionFocusSupport(this);

	/**
	 * the boolean which tells if this dialog was initialized
	 */
	private boolean initialized = false;

	/**
	 * the {@link ApplicationTaskManager manager} of {@link Task tasks}
	 */
	private ApplicationTaskManager taskManager;
	/**
	 * the {@link ApplicationMethodManager manager} of {@link Method methods}
	 */
	private ApplicationMethodManager methodManager;

	/**
	 * the {@link Task task} to which this dialog is attached
	 */
	private Task task;

	/**
	 * the {@link ErrorsDialog dialog} in which the errors are displayed
	 */
	private ErrorsDialog errorsDialog;

	/**
	 * the {@link CloseAction action} which closes this dialog
	 */
	CloseAction closeAction;
	/**
	 * the {@link AbortTaskAction action} which aborts the task associated
	 * with this dialog
	 */
	private AbortTaskAction abortTaskAction;
	/**
	 * the {@link SuspendTaskAction action} which suspends the task associated
	 * with this dialog
	 */
	private SuspendTaskAction suspendTaskAction;
	/**
	 * the {@link ResumeTaskAction action} which resumes the task associated
	 * with this dialog
	 */
	private ResumeTaskAction resumeTaskAction;
	/**
	 * the {@link GetTaskResultAction action} which shows the result of the
	 * task associated with this dialog
	 */
	private GetTaskResultAction getTaskResultAction;
	/**
	 * the {@link GetTaskErrorAction action} which shows the errors that has
	 * occurred while the task was processed
	 */
	private GetTaskErrorAction getTaskErrorAction;

	/**
	 * the content pane which contains the label with the status of the task
	 * and the progress bars
	 */
	private JPanel contentPane;
	/**
	 * the control pane with 3 buttons:
	 * <ul>
	 * <li>the button which allows to abort a task or get the results
	 * if the task is finished,</li>
	 * <li>the button which allows to close this dialog,</li>
	 * <li>if the task is suspendable - the button which allows to
	 * suspend/resume a task</li>
	 * </ul>
	 */
	private JPanel controlPane;

	/**
	 * the button which {@link CloseAction closes} this dialog
	 */
	private JButton closeButton;
	/**
	 * the button which can be used to {@link AbortTaskAction abort} the task
	 * or show its {@link GetTaskResultAction result} (or
	 * {@link GetTaskErrorAction error} if the error occured)
	 */
	private JButton abortAndResultButton;
	/**
	 * the button which can be used to {@link SuspendTaskAction suspend} or
	 * {@link ResumeTaskAction resume} an action
	 */
	private JButton suspendAndResumeButton;

	/**
	 * boolean which tells if the task can be suspended
	 */
	private boolean suspendable = false;
	/**
	 * boolean which tells if the progress of the task can be tracked
	 */
	private boolean trackable = false;

	/**
	 * the array of booleans - value of index {@code i} tells if the progress
	 * bar of that index shouldn't be trackable
	 */
	private boolean[] forceNotTrackable = null;

	/**
	 * the label with the icon of the current {@link TaskStatus status}
	 */
	private JLabel statusIconLabel;
	/**
	 * the label with the text of the current {@link TaskStatus status}
	 */
	private JLabel statusTextLabel;
	/**
	 * 
	 */
	private JLabel messageLabel;

	/**
	 * the number of progress bars
	 */
	private int progressCnt;
	/**
	 * the array or titles of progress bars
	 */
	private String[] progressTitles;
	/**
	 * the array of labels with titles of progress bars
	 */
	private JLabel[] progressTitleLabels;
	/**
	 * the array of labels telling how much time have left until the task
	 * is finished
	 */
	private JLabel[] progressETALabels;
	/**
	 * the array of progress bars
	 */
	private JProgressBar[] progressBars;

	/**
	 * time in miliseconds of the last update of progress bars (at index
	 * {@code i} the time when progress bar of that index was updated)
	 */
	private long[] lastETAUpdateMillis;

	/**
	 * the {@link SignalMLOperationMode mode} in which Svarog is operating
	 */
	private SignalMLOperationMode mode;

	/**
	 * Constructor. Creates this dialog and sets the {@link
	 * SignalMLOperationMode mode} in which Svarog is operating and the
	 * {@link Task task} to which this dialog is attached.
	 * @param task the mode in which Svarog is operating 
	 * @param mode the task to which this dialog is attached
	 */
	public TaskStatusDialog(Task task, SignalMLOperationMode mode) {
		super(null, (mode == SignalMLOperationMode.APPLICATION ? Dialog.ModalityType.MODELESS : Dialog.ModalityType.APPLICATION_MODAL));
		this.mode = mode;
		this.task = task;
	}

	/**
	 * Initializes this dialog. Performs {@link #initializeInternal()}
	 * exclusively on {@code task}.
	 */
	public void initialize() {

		synchronized (task) {
			initializeInternal();
		}

	}

	/**
	 * Initializes this dialog:
	 * <ul>
	 * <li>sets the title and the icon,</li>
	 * <li>creates actions:
	 * <ul><li>{@link AbortTaskAction},</li>
	 * <li>{@link CloseAction},</li>
	 * <li>{@link GetTaskResultAction},</li>
	 * <li>{@link GetTaskErrorAction},</li>
	 * <li>if the method is {@link SuspendableMethod suspendable} -
	 * {@link SuspendTaskAction} and {@link ResumeTaskAction},</li>
	 * </ul></li>
	 * <li>if the method is {@link TrackableMethod trackable} creates the
	 * progress bars,</li>
	 * <li>initializes {@link #initializeContentPane() content pane} and
	 * {@link #initializeControlPane() control pane},</li>
	 * <li>sets that this dialog will be closed when escape is pressed,</li>
	 * <li>adds a window listener, which:<ul>
	 * <li>if the window is closing - removes this dialog from the list of
	 * {@link TaskEventListener task listeners},</li>
	 * <li>if the window is opening:
	 * <ul>
	 * <li>updates the {@link TaskStatus status} of the task,</li>
	 * <li>adds this dialog as the task listener,</li>
	 * </ul></li></ul></li>
	 * <li>sets the current status of the task.</li>
	 * <ul>
	 */
	private void initializeInternal() {

		Method method = task.getMethod();

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		setTitle(_R("signalml task [{0}]", method.getName()));
		if (method instanceof MethodIteratorMethod) {
			setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/iteratemethod.png"));
		} else {
			setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/runmethod.png"));
		}

		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());

		closeAction = new CloseAction();

		abortTaskAction = new AbortTaskAction(this);
		if (method instanceof SuspendableMethod) {
			suspendable = true;
			suspendTaskAction = new SuspendTaskAction(this);
			resumeTaskAction = new ResumeTaskAction(this);
			resumeTaskAction.setTaskManager(taskManager);
		}

		getTaskResultAction = new GetTaskResultAction( this, new DialogResultListener() {
			@Override
			public void dialogCompleted(boolean success) {
				if (success && closeAction != null) {
					closeAction.actionPerformed(new ActionEvent(TaskStatusDialog.this, 0, "CLOSE"));
				}
			}
		});
		getTaskResultAction.setMethodManager(methodManager);

		getTaskErrorAction = new GetTaskErrorAction(this);
		getTaskErrorAction.setErrorsDialog(errorsDialog);

		if (method instanceof TrackableMethod) {
			trackable = true;
			TrackableMethod trackableMethod = (TrackableMethod) method;
			progressCnt = trackableMethod.getTickerCount();
			progressTitles = new String[progressCnt];
			for (int i=0; i<progressCnt; i++) {
				progressTitles[i] = trackableMethod.getTickerLabel( i);
			}
			lastETAUpdateMillis = new long[progressCnt];
		} else {
			progressCnt = 1;
			progressTitles = new String[1];
			progressTitles[0] = _("No detailed progress info");
		}

		initializeControlPane();
		initializeContentPane();

		getRootPane().setContentPane(contentPane);

		KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
		getRootPane().getActionMap().put("ESCAPE", closeAction);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent arg0) {
				if (closeAction.isEnabled()) {
					closeAction.actionPerformed(new ActionEvent(this,0,"close"));
					taskManager.getEventProxyForTask(task).removeTaskEventListener(TaskStatusDialog.this);
				}
			}

			@Override
			public void windowOpened(WindowEvent e) {

				synchronized (task) {

					TaskStatus taskStatus = task.getStatus();
					setStatus(taskStatus);

					MessageSourceResolvable message = task.getMessage();
					if (message != null) {
						messageLabel.setText(getSvarogI18n().getMessage(message));
					} else {
						messageLabel.setText("");
					}

					taskManager.getEventProxyForTask(task).addTaskEventListener(TaskStatusDialog.this);

				}
			}

		});

		pack();

		setStatus(task.getStatus());

		initialized = true;

	}

	@Override
	public void addActionFocusListener(ActionFocusListener listener) {
		afSupport.addActionFocusListener(listener);
	}

	@Override
	public void removeActionFocusListener(ActionFocusListener listener) {
		afSupport.removeActionFocusListener(listener);
	}

	/**
	 * Returns the {@link Task task} associated with this dialog.
	 */
	@Override
	public Task getActiveTask() {
		return task;
	}

	/**
	 * Initializes the control pane with 3 buttons:
	 * <ul>
	 * <li>the button which allows to abort a task or get the results
	 * if the task is finished,</li>
	 * <li>the button which allows to close this dialog,</li>
	 * <li>if the task is suspendable - the button which allows to
	 * suspend/resume a task,</li>
	 * </ul>
	 */
	protected void initializeControlPane() {

		controlPane = new JPanel();
		controlPane.setLayout(new FlowLayout(FlowLayout.RIGHT,3,3));

		if (mode == SignalMLOperationMode.APPLICATION) {
			if (suspendable) {
				suspendAndResumeButton = new JButton(suspendTaskAction);
				controlPane.add(suspendAndResumeButton);
			}
		}

		abortAndResultButton = new JButton(abortTaskAction);
		controlPane.add(abortAndResultButton);

		closeButton = new JButton(closeAction);
		controlPane.add(closeButton);

		contentPane.add(controlPane,BorderLayout.SOUTH);

	}

	/**
	 * Initializes the content pane:
	 * <ul>
	 * <li>sets the sizes and alignments of labels and progress bars,</li>
	 * <li>sets the values of progress bars,</li>
	 * <li>sets the text of labels (including the titles of progress bars).
	 * </li></ul>
	 */
	protected void initializeContentPane() {

		contentPane.setBorder(new EmptyBorder(3,3,3,3));

		JComponent interfacePane = new JPanel();
		interfacePane.setLayout(new BoxLayout(interfacePane, BoxLayout.Y_AXIS));
		interfacePane.setBorder(new TitledBorder(_("Task status")));

		int i;

		Dimension progressSize = new Dimension(350,20);
		Dimension labelMinSize = new Dimension(progressSize.width, 1);

		TaskStatus taskStatus = task.getStatus();
		statusIconLabel = new JLabel(IconUtils.getLargeTaskIcon(taskStatus));
		statusIconLabel.setBorder(new EmptyBorder(0,0,0,5));

		Dimension iconRightLabelMinSize = new Dimension(labelMinSize.width-(statusIconLabel.getIcon().getIconWidth()+5), labelMinSize.height);

		statusTextLabel = new JLabel("sizeinit");
		statusTextLabel.setMinimumSize(iconRightLabelMinSize);
		statusTextLabel.setVerticalTextPosition(JLabel.TOP);
		statusTextLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		Font largeFont = statusTextLabel.getFont().deriveFont(Font.BOLD, 16F);
		statusTextLabel.setFont(largeFont);

		messageLabel = new JLabel("sizeinit");
		messageLabel.setMinimumSize(new Dimension(iconRightLabelMinSize.width, 10));
		messageLabel.setVerticalTextPosition(JLabel.BOTTOM);
		messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		Font smallFont = messageLabel.getFont().deriveFont(Font.PLAIN, 10F);
		messageLabel.setFont(smallFont);

		progressTitleLabels = new JLabel[progressCnt];
		for (i=0; i<progressCnt; i++) {
			progressTitleLabels[i] = new JLabel(progressTitles[i]);
			progressTitleLabels[i].setFont(smallFont);
			progressTitleLabels[i].setAlignmentX(Component.LEFT_ALIGNMENT);
		}

		progressBars = new JProgressBar[progressCnt];

		if (trackable) {

			progressETALabels = new JLabel[progressCnt];
			for (i=0; i<progressCnt; i++) {

				progressETALabels[i] = new JLabel("sizeinit");
				progressETALabels[i].setFont(smallFont);
				progressETALabels[i].setMinimumSize(labelMinSize);
				progressETALabels[i].setAlignmentX(Component.LEFT_ALIGNMENT);

				progressBars[i] = new JProgressBar();
				progressBars[i].setAlignmentX(Component.LEFT_ALIGNMENT);
				progressBars[i].setPreferredSize(progressSize);
				progressBars[i].setMinimumSize(progressSize);
				progressBars[i].setMaximumSize(progressSize);
				progressBars[i].setStringPainted(true);

			}

		} else {

			progressBars[0] = new JProgressBar();
			progressBars[0].setAlignmentX(Component.LEFT_ALIGNMENT);
			progressBars[0].setPreferredSize(progressSize);
			progressBars[0].setMinimumSize(progressSize);
			progressBars[0].setMaximumSize(progressSize);

		}

		JPanel labelPanel = new JPanel(new BorderLayout());
		labelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel textLabelPanel = new JPanel();
		textLabelPanel.setLayout(new BoxLayout(textLabelPanel, BoxLayout.Y_AXIS));
		textLabelPanel.add(statusTextLabel);
		textLabelPanel.add(messageLabel);

		labelPanel.add(statusIconLabel, BorderLayout.WEST);
		labelPanel.add(textLabelPanel, BorderLayout.CENTER);

		interfacePane.add(labelPanel);
		interfacePane.add(Box.createVerticalStrut(8));
		for (i=0; i<progressCnt; i++) {
			interfacePane.add(progressTitleLabels[i]);
			interfacePane.add(Box.createVerticalStrut(1));
			interfacePane.add(progressBars[i]);
			if (trackable) {
				interfacePane.add(Box.createVerticalStrut(1));
				interfacePane.add(progressETALabels[i]);
			}
			interfacePane.add(Box.createVerticalStrut(3));
		}

		contentPane.add(interfacePane,BorderLayout.CENTER);

	}

	/**
	 * Makes this dialog visible.
	 * If this dialog is not initialized initializes it.
	 * <p>If {@code centered} is set positions this dialog in the center of the
	 * screen.
	 * @param centered {@code true} if this dialog should be positioned in the
	 * center of the screen, {@code false} otherwise
	 */
	public final void showDialog(boolean centered) {
		if (!initialized) {
			initialize();
			initialized = true;
		}
		if (centered) {
			setLocationByPlatform(false);
			center();
		} else {
			setLocationByPlatform(true);
		}
		showDialog();
	}

	/**
	 * Makes this dialog visible.
	 * If this dialog is not initialized initializes it.
	 * Positions the center of this dialog at the specified position.
	 * @param xpos the proportion of width at which the center of this
	 * dialog should be located
	 * @param ypos the proportion of height at which the center of this
	 * dialog should be located
	 */
	public final void showDialog(double xpos, double ypos) {
		if (!initialized) {
			initialize();
			initialized = true;
		}
		setLocationByPlatform(false);
		center(xpos, ypos);
		showDialog();
	}

	/**
	 * Makes this dialog visible.
	 * If this dialog is not initialized initializes it.
	 */
	public final void showDialog() {

		if (!initialized) {
			initialize();
			initialized = true;
		}

		setVisible(true);

	}

	/**
	 * Makes this dialog invisible.
	 */
	public final void hideDialog() {
		setVisible(false);
	}

	/**
	 * Positions this dialog in the center of the screen.
	 */
	public void center() {
		center(0.5, 0.5);
	}

	/**
	 * Changes the location of this dialog. Moves the center of it to the
	 * point in the given proportion of the screen
	 * @param xpos the proportion of width at which the center of this
	 * dialog should be located
	 * @param ypos the proportion of height at which the center of this
	 * dialog should be located
	 */
	public void center(double xpos, double ypos) {

		final double safeXpos = xpos > 1.0 ? 1.0 : (xpos < 0.0 ? 0.0 : xpos);
		final double safeYpos = ypos > 1.0 ? 1.0 : (ypos < 0.0 ? 0.0 : ypos);

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();

		int x = (int)((d.width - getWidth()) * safeXpos);
		int y = (int)((d.height - getHeight()) * safeYpos);

		setLocation(x, y);
	}

	/**
	 * Returns the {@link ApplicationTaskManager manager} of {@link Task tasks}.
	 * @return the manager of tasks
	 */
	public ApplicationTaskManager getTaskManager() {
		return taskManager;
	}

	/**
	 * Sets the {@link ApplicationTaskManager manager} of {@link Task tasks}.
	 * @param taskManager the manager of tasks
	 */
	public void setTaskManager(ApplicationTaskManager taskManager) {
		this.taskManager = taskManager;
	}

	/**
	 * Returns the {@link ApplicationMethodManager manager} of {@link Method
	 * methods}.
	 * @return the manager of methods
	 */
	public ApplicationMethodManager getMethodManager() {
		return methodManager;
	}

	/**
	 * Sets the {@link ApplicationMethodManager manager} of {@link Method
	 * methods}.
	 * @param methodManager the manager of methods
	 */
	public void setMethodManager(ApplicationMethodManager methodManager) {
		this.methodManager = methodManager;
	}

	/**
	 * Returns the {@link ErrorsDialog dialog} which displays the list of
	 * errors.
	 * @return the dialog which displays the list of errors
	 */
	public ErrorsDialog getErrorsDialog() {
		return errorsDialog;
	}

	/**
	 * Sets the {@link ErrorsDialog dialog} which displays the list of
	 * errors.
	 * @param errorsDialog the dialog which displays the list of errors
	 */
	public void setErrorsDialog(ErrorsDialog errorsDialog) {
		this.errorsDialog = errorsDialog;
	}

	/**
	 * Called when the task has been aborted.
	 * Changes the status of progress bars and buttons:
	 * <ul>
	 * <li>for all progress bars if the bar is not trackable sets it as
	 * determinate and with value 0,</li>
	 * <li>paints the string on the status bar informing that the task was
	 * aborted.</li></ul>
	 */
	@Override
	public void taskAborted(TaskEvent ev) {
		setStatus(ev.getStatus());
	}

	/**
	 * Called when the task has been finished.
	 * Changes the status of progress bars and buttons:
	 * <ul>
	 * <li>if Svarog is running in
	 * {@link SignalMLOperationMode#APPLICATION APPLICATION}
	 * mode changes the state of {@link
	 * #suspendAndResumeButton} to suspend,</li>
	 * <li>for all progress bars if the bar is not trackable sets it as
	 * determinate and with value maximum (100),</li>
	 * <li>for all progress bars if the bar is trackable and its maximum is
	 * lower then 0 sets both its maximum and value to 1,</li>
	 * <li>paints the string on the status bar informing that the task was
	 * finished,</li></ul>
	 */
	@Override
	public void taskFinished(TaskEvent ev) {
		setStatus(ev.getStatus());
	}

	/**
	 * Called when the task has been resumed.
	 * Changes the status of progress bars and buttons:
	 * <ul>
	 * <li>changes the state of {@link #abortAndResultButton} to
	 * {@link #abortTaskAction abort},</li>
	 * <li>if Svarog is running in
	 * {@link SignalMLOperationMode#APPLICATION APPLICATION}
	 * mode and task is suspendable changes the state
	 * of {@link #suspendAndResumeButton} to suspend,</li>
	 * <li>for all progress bars if the bar is not trackable sets it as
	 * indeterminate and with value maximum,</li>
	 * <li>for all progress bars if the bar is trackable sets its value,</li>
	 * </ul>
	 */
	@Override
	public void taskResumed(TaskEvent ev) {
		setStatus(ev.getStatus());
	}

	/**
	 * Called when the task has started.
	 * Changes the status of progress bars and buttons:
	 * <ul>
	 * <li>changes the state of {@link #abortAndResultButton} to {@link
	 * #abortTaskAction abort},</li>
	 * <li>if Svarog is running in
	 * {@link SignalMLOperationMode#APPLICATION APPLICATION} mode and
	 * task is suspendable changes the state
	 * of {@link #suspendAndResumeButton} to suspend,</li>
	 * <li>for all progress bars if the bar is not trackable sets it as
	 * indeterminate and with value maximum,</li>
	 * <li>for all progress bars if the bar is trackable sets its value,</li>
	 * </ul>
	 */
	@Override
	public void taskStarted(TaskEvent ev) {
		setStatus(ev.getStatus());
	}

	/**
	 * Called when the task has been suspended.
	 * Changes the status of progress bars and buttons:
	 * <ul>
	 * <li>if Svarog is running in
	 * {@link SignalMLOperationMode#APPLICATION APPLICATION}
	 * mode changes the state of {@link
	 * #suspendAndResumeButton} to suspend,</li>
	 * <li>for all progress bars if the bar is not trackable sets it as
	 * determinate and with value 0,</li>
	 * <li>paints the string on the status bar informing that the task was
	 * suspended,</li></ul>
	 */
	@Override
	public void taskSuspended(TaskEvent ev) {
		setStatus(ev.getStatus());
	}

	/**
	 * Called when the task request has changed.
	 * Changes the status of progress bars and buttons:
	 * <ul>
	 * <li>changes the state of {@link #abortAndResultButton} to {@link
	 * #abortTaskAction abort},</li>
	 * <li>if Svarog is running in
	 * {@link SignalMLOperationMode#APPLICATION APPLICATION} mode and
	 * task is suspendable changes the state
	 * of {@link #suspendAndResumeButton} to suspend,</li>
	 * <li>for all progress bars if the bar is not trackable sets it as
	 * indeterminate and with value maximum,</li>
	 * <li>for all progress bars if the bar is trackable sets its value,</li>
	 * </ul>
	 */
	@Override
	public void taskRequestChanged(TaskEvent ev) {
		setStatus(ev.getStatus());
	}

	/**
	 * Sets the text describing the status of the task in the
	 * {@code messageLabel}.
	 * @param ev the event from which message will be obtained
	 */
	@Override
	public void taskMessageSet(TaskEvent ev) {

		MessageSourceResolvable message = ev.getMessage();
		if (message != null) {
			messageLabel.setText(getSvarogI18n().getMessage(message));
		} else {
			messageLabel.setText("");
		}

	}

	/**
	 * Updates the values of progress bars with the data from the
	 * {@link TaskEvent task event}.
	 */
	@Override
	public void taskTickerUpdated(TaskEvent ev) {

		if (trackable) {

			int[] tickerLimits = ev.getTickerLimits();
			int[] tickers = ev.getTickers();

			int lastValue;
			for (int i=0; i<progressCnt; i++) {
				lastValue = progressBars[i].getValue();
				progressBars[i].setMaximum(tickerLimits[i]);
				progressBars[i].setValue(tickers[i]);

				updateETA(i,tickerLimits[i],tickers[i],(lastValue<tickers[i]));
			}

		}

	}

	/**
	 * Calculates the time left until the task finishes and displays it on
	 * the progress bar of a given index.
	 * The update is performed only if at least one second passed since the
	 * last update.
	 * @param index the index of the progress bar
	 * @param limit TODO not used
	 * @param value TODO not used
	 * @param force {@code true} if progress bar should be updated no matter
	 * how many time elapsed
	 */
	private void updateETA(int index, int limit, int value, boolean force) {
		long millis = System.currentTimeMillis();
		if (!force && lastETAUpdateMillis[index] != 0 && ((millis-lastETAUpdateMillis[index]) < 1000))
			return;
		lastETAUpdateMillis[index] = millis;

		final Integer secondsInteger = task.getExpectedSecondsUntilComplete(index);

		final String _minutes, _seconds;
		if (secondsInteger == null) {
			_minutes = _seconds = "--";
		} else {
			_minutes = String.format("%02d", secondsInteger / 60);
			_seconds = String.format("%02d", secondsInteger % 60);
		}
		progressETALabels[index].setText(_R("Expected to end in {0}:{1} min:sec", _minutes, _seconds));
	}

	/**
	 * Using the provided {@link TaskStatus status}:
	 * <ul>
	 * <li>sets which buttons should be enabled depending on the fact if the
	 * task with this status can request the operation associated with the
	 * button,</li>
	 * <li>for status {@link TaskStatus#ABORTED ABORTED}:
	 * <ul>
	 * <li>for all progress bars if the bar is not trackable sets it as
	 * determinate and with value 0,</li>
	 * <li>paints the string on the status bar informing that the task was
	 * aborted,</li></ul></li>
	 * <li>for status {@link TaskStatus#SUSPENDED SUSPENDED}:
	 * <ul>
	 * <li>if Svarog is running in
	 * {@link SignalMLOperationMode#APPLICATION APPLICATION} mode changes
	 * the state of {@link #suspendAndResumeButton} to suspend,</li>
	 * <li>for all progress bars if the bar is not trackable sets it as
	 * determinate and with value 0,</li>
	 * <li>paints the string on the status bar informing that the task was
	 * suspended,</li></ul></li>
	 * <li>for status {@link TaskStatus#FINISHED FINISHED}:
	 * <ul>
	 * <li>if Svarog is running in
	 * {@link SignalMLOperationMode#APPLICATION APPLICATION} mode changes
	 * the state of {@link #suspendAndResumeButton} to suspend,</li>
	 * <li>for all progress bars if the bar is not trackable sets it as
	 * determinate and with value maximum (100),</li>
	 * <li>for all progress bars if the bar is trackable and its maximum is
	 * lower then 0 sets both its maximum and value to 1,</li>
	 * <li>paints the string on the status bar informing that the task was
	 * finished,</li></ul></li>
	 * <li>for status {@link TaskStatus#ERROR ERROR}:
	 * <ul>
	 * <li>if Svarog is running in
	 * {@link SignalMLOperationMode#APPLICATION APPLICATION} mode changes
	 * the state of {@link #suspendAndResumeButton} to suspend,</li>
	 * <li>changes the state of {@link #abortAndResultButton} to {@link
	 * #getTaskErrorAction error},</li>
	 * <li>for all progress bars if the bar is not trackable sets it as
	 * determinate and with value 0,</li>
	 * <li>paints the string on the status bar informing that the task was
	 * stopped with error,</li></ul></li>
	 * <li>for any other task:
	 * <ul>
	 * <li>changes the state of {@link #abortAndResultButton} to {@link
	 * #abortTaskAction abort},</li>
	 * <li>if Svarog is running in
	 * {@link SignalMLOperationMode#APPLICATION APPLICATION} mode and task
	 * is suspendable changes the state of {@link #suspendAndResumeButton}
	 * to suspend,</li>
	 * <li>for all progress bars if the bar is not trackable sets it as
	 * indeterminate and with value maximum,</li>
	 * <li>for all progress bars if the bar is trackable sets its value,</li>
	 * </ul></li></ul>
	 * @param taskStatus the status of the task
	 */
	private void setStatus(TaskStatus taskStatus) {

		statusIconLabel.setIcon(IconUtils.getLargeTaskIcon(taskStatus));
		statusTextLabel.setText(getSvarogI18n().getMessage("taskStatusLong." + taskStatus));

		abortTaskAction.setEnabled(taskStatus.isAbortable());
		getTaskResultAction.setEnabled(taskStatus.isFinished());
		getTaskErrorAction.setEnabled(taskStatus.isError());
		if (mode == SignalMLOperationMode.APPLICATION) {
			if (suspendable) {
				suspendTaskAction.setEnabled(taskStatus.isSuspendable());
				resumeTaskAction.setEnabled(taskStatus.isResumable());
			}
		}
		else if (mode == SignalMLOperationMode.APPLET) {
			closeAction.setEnabled(!taskStatus.isRunning());
		}

		switch (taskStatus) {

		case ABORTED :
			if (mode == SignalMLOperationMode.APPLICATION) {
				if (suspendable) {
					suspendAndResumeButton.setAction(suspendTaskAction);
				}
			}
			if (trackable) {
				for (int i=0; i<progressCnt; i++) {
					if (forceNotTrackable != null && forceNotTrackable[i]) {
						progressBars[i].setIndeterminate(false);
						progressBars[i].setMaximum(100);
						progressBars[i].setValue(0);
						progressBars[i].setStringPainted(true);
						progressBars[i].setString(_("Task aborted"));
					} else {
						progressETALabels[i].setText(_("Task aborted"));
					}
				}
			} else {
				progressBars[0].setIndeterminate(false);
				progressBars[0].setMaximum(100);
				progressBars[0].setValue(0);
				progressBars[0].setStringPainted(true);
				progressBars[0].setString(_("Task aborted"));
			}
			break;

		case SUSPENDED :
			if (mode == SignalMLOperationMode.APPLICATION) {
				if (suspendable) {
					suspendAndResumeButton.setAction(resumeTaskAction);
				}
			}
			if (trackable) {
				for (int i=0; i<progressCnt; i++) {
					if (forceNotTrackable != null && forceNotTrackable[i]) {
						progressBars[i].setIndeterminate(false);
						progressBars[i].setMaximum(100);
						progressBars[i].setValue(0);
						progressBars[i].setStringPainted(true);
						progressBars[i].setString(_("Task suspended"));
					} else {
						progressETALabels[i].setText(_("Task suspended"));
					}
				}
			} else {
				progressBars[0].setIndeterminate(false);
				progressBars[0].setMaximum(100);
				progressBars[0].setValue(0);
				progressBars[0].setStringPainted(true);
				progressBars[0].setString(_("Task suspended"));
			}
			break;

		case FINISHED :
			abortAndResultButton.setAction(getTaskResultAction);
			if (mode == SignalMLOperationMode.APPLICATION) {
				if (suspendable) {
					suspendAndResumeButton.setAction(suspendTaskAction);
				}
			}
			int max;
			if (trackable) {
				for (int i=0; i<progressCnt; i++) {
					if (forceNotTrackable != null && forceNotTrackable[i]) {
						progressBars[i].setIndeterminate(false);
						progressBars[i].setMaximum(100);
						progressBars[i].setValue(100);
						progressBars[i].setStringPainted(true);
						progressBars[i].setString(_("Task finished"));
					} else {
						progressETALabels[i].setText(_("Task finished"));
						max = progressBars[i].getMaximum();
						if (max <= 0) {
							progressBars[i].setMaximum(1);
							progressBars[i].setValue(1);
						} else {
							progressBars[i].setValue(max);
						}
					}
				}
			} else {
				progressBars[0].setIndeterminate(false);
				progressBars[0].setMaximum(100);
				progressBars[0].setValue(100);
				progressBars[0].setStringPainted(true);
				progressBars[0].setString(_("Task finished"));
			}
			break;

		case ERROR :
			abortAndResultButton.setAction(getTaskErrorAction);
			if (mode == SignalMLOperationMode.APPLICATION) {
				if (suspendable) {
					suspendAndResumeButton.setAction(suspendTaskAction);
				}
			}
			if (trackable) {
				for (int i=0; i<progressCnt; i++) {
					if (forceNotTrackable != null && forceNotTrackable[i]) {
						progressBars[i].setIndeterminate(false);
						progressBars[i].setMaximum(100);
						progressBars[i].setValue(0);
						progressBars[i].setStringPainted(true);
						progressBars[i].setString(_("Task finished"));
					} else {
						progressETALabels[i].setText(_("Task finished"));
					}
				}
			} else {
				progressBars[0].setIndeterminate(false);
				progressBars[0].setMaximum(100);
				progressBars[0].setValue(0);
				progressBars[0].setStringPainted(true);
				progressBars[0].setString(_("Task finished"));
			}
			break;

		default :
			abortAndResultButton.setAction(abortTaskAction);
			if (mode == SignalMLOperationMode.APPLICATION) {
				if (suspendable) {
					suspendAndResumeButton.setAction(suspendTaskAction);
				}
			}

			if (trackable) {
				int[] tickerLimits;
				int[] tickers;
				synchronized (task) {
					tickerLimits = task.getTickerLimits();
					tickers = task.getTickers();
				}
				for (int i=0; i<progressCnt; i++) {

					if (forceNotTrackable != null && forceNotTrackable[i]) {

						progressBars[i].setIndeterminate(true);
						progressBars[i].setMaximum(100);
						progressBars[i].setValue(0);
						progressBars[i].setStringPainted(false);

					} else {

						updateETA(i,tickerLimits[i],tickers[i],true);
					}
				}

			} else {
				progressBars[0].setIndeterminate(true);
				progressBars[0].setMaximum(100);
				progressBars[0].setValue(0);
				progressBars[0].setStringPainted(false);
			}


		}

	}

	/**
	 * Action which hides {@link TaskStatusDialog this dialog}.
	 * If Svarog is running in {@link SignalMLOperationMode#APPLET applet} mode
	 * also removes the task associated with this dialog.
	 */
	protected class CloseAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the icon and the description.
		 */
		public CloseAction() {
			super(_("Close"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/close.png"));
		}

		/**
		 * When this action is performed hides {@link TaskStatusDialog this
		 * dialog} and f Svarog is running in {@link SignalMLOperationMode#APPLET
		 * applet} mode also removes the task associated with this dialog.
		 */
		public void actionPerformed(ActionEvent arg0) {
			hideDialog();

			if (mode == SignalMLOperationMode.APPLET) {
				taskManager.removeTask(task);
			}

		}

	}

	/**
	 * Returns the {@link SvarogAccessI18nImpl} instance.
	 * @return the {@link SvarogAccessI18nImpl} singleton instance
	 */
	protected org.signalml.app.SvarogI18n getSvarogI18n() {
		return org.signalml.plugin.impl.SvarogAccessI18nImpl.getInstance();
	}
}

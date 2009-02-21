/* TaskStatusDialog.java created 2007-10-18
 * 
 */

package org.signalml.app.view.dialog;

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
import org.signalml.method.stager.StagerMethod;
import org.signalml.task.Task;
import org.signalml.task.TaskEvent;
import org.signalml.task.TaskEventListener;
import org.signalml.task.TaskStatus;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;

/** TaskStatusDialog
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TaskStatusDialog extends JDialog implements TaskEventListener, TaskFocusSelector {
	
	private static final long serialVersionUID = 1L;

	private ActionFocusSupport afSupport = new ActionFocusSupport(this);
	
	private boolean initialized = false;

	private MessageSourceAccessor messageSource;
	private ApplicationTaskManager taskManager;
	private ApplicationMethodManager methodManager;
	
	private Task task;
	
	private ErrorsDialog errorsDialog;
	
	CloseAction closeAction;
	private AbortTaskAction abortTaskAction;
	private SuspendTaskAction suspendTaskAction;
	private ResumeTaskAction resumeTaskAction;
	private GetTaskResultAction getTaskResultAction;
	private GetTaskErrorAction getTaskErrorAction;
	
	private JPanel contentPane;
	private JPanel controlPane;

	private JButton closeButton;
	private JButton abortAndResultButton;
	private JButton suspendAndResumeButton;
	
	private boolean suspendable = false;
	private boolean trackable = false;

	private boolean[] forceNotTrackable = null;
	
	private JLabel statusIconLabel;
	private JLabel statusTextLabel;
	private JLabel messageLabel;
	
	private int progressCnt;
	private String[] progressTitles;
	private JLabel[] progressTitleLabels;
	private JLabel[] progressETALabels;
	private JProgressBar[] progressBars;
	
	private long[] lastETAUpdateMillis;
	
	private SignalMLOperationMode mode;
	
	public TaskStatusDialog(Task task, SignalMLOperationMode mode) {
		super(null, (mode == SignalMLOperationMode.APPLICATION ? Dialog.ModalityType.MODELESS : Dialog.ModalityType.APPLICATION_MODAL ) );
		this.mode = mode;
		this.task = task;
	}

	public void initialize() {
		
		synchronized( task ) {
			initializeInternal();
		}
		
	}
	
	private void initializeInternal() {
		
		Method method = task.getMethod();
				
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				
		setTitle(messageSource.getMessage("taskStatus.title", new Object[] { method.getName() } ));
		if( method instanceof MethodIteratorMethod ) {
			setIconImage( IconUtils.loadClassPathImage("org/signalml/app/icon/iteratemethod.png"));
		} else {
			setIconImage( IconUtils.loadClassPathImage("org/signalml/app/icon/runmethod.png"));
		}
		
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());

		closeAction = new CloseAction();
				
		abortTaskAction = new AbortTaskAction(messageSource,this);
		if( method instanceof SuspendableMethod ) {
			suspendable = true;
			suspendTaskAction = new SuspendTaskAction(messageSource,this);
			resumeTaskAction = new ResumeTaskAction(messageSource,this);
			resumeTaskAction.setTaskManager(taskManager);
		}
		
		getTaskResultAction = new GetTaskResultAction(messageSource, this, new DialogResultListener() {
			@Override
			public void dialogCompleted(boolean success) {
				if (success && closeAction != null) {
					closeAction.actionPerformed(new ActionEvent(TaskStatusDialog.this, 0, "CLOSE"));
				}
			}
		});
		getTaskResultAction.setMethodManager(methodManager);

		getTaskErrorAction = new GetTaskErrorAction(messageSource, this);
		getTaskErrorAction.setErrorsDialog(errorsDialog);
		
		if( method instanceof TrackableMethod ) {
			trackable = true;
			TrackableMethod trackableMethod = (TrackableMethod) method;
			progressCnt = trackableMethod.getTickerCount();
			progressTitles = new String[progressCnt];
			for( int i=0; i<progressCnt; i++ ) {
				progressTitles[i] = trackableMethod.getTickerLabel(messageSource, i);
			}
			lastETAUpdateMillis = new long[progressCnt];
		} else {
			progressCnt = 1;
			progressTitles = new String[1];
			progressTitles[0] = messageSource.getMessage("taskStatus.noProgressInfo");
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
				if( closeAction.isEnabled() ) {
					closeAction.actionPerformed(new ActionEvent(this,0,"close"));
					taskManager.getEventProxyForTask(task).removeTaskEventListener(TaskStatusDialog.this);
				}
			}
						
			@Override
			public void windowOpened(WindowEvent e) {
				
				synchronized( task ) {
				
					TaskStatus taskStatus = task.getStatus();
					setStatus(taskStatus);
					
					MessageSourceResolvable message = task.getMessage();
					if( message != null ) {
						messageLabel.setText( messageSource.getMessage(message) );
					} else {
						messageLabel.setText( "" );
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

	@Override
	public Task getActiveTask() {
		return task;
	}

	protected void initializeControlPane() {
		
		controlPane = new JPanel();
		controlPane.setLayout(new FlowLayout(FlowLayout.RIGHT,3,3));
		
		if( mode == SignalMLOperationMode.APPLICATION ) {
			if( suspendable ) {
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
	
	protected void initializeContentPane() {
		
		contentPane.setBorder(new EmptyBorder(3,3,3,3));
		
		JComponent interfacePane = new JPanel();
		interfacePane.setLayout(new BoxLayout(interfacePane, BoxLayout.Y_AXIS));
		interfacePane.setBorder(new TitledBorder(messageSource.getMessage("taskStatus.taskStatus")));
				
		int i;
		
		Dimension progressSize = new Dimension(350,20);
		Dimension labelMinSize = new Dimension(progressSize.width, 1);
		
		TaskStatus taskStatus = task.getStatus();		
		statusIconLabel = new JLabel( IconUtils.getLargeTaskIcon(taskStatus) );
		statusIconLabel.setBorder(new EmptyBorder(0,0,0,5));

		Dimension iconRightLabelMinSize = new Dimension(labelMinSize.width-(statusIconLabel.getIcon().getIconWidth()+5), labelMinSize.height);
		
		statusTextLabel = new JLabel( "sizeinit" );
		statusTextLabel.setMinimumSize(iconRightLabelMinSize);
		statusTextLabel.setVerticalTextPosition(JLabel.TOP);
		statusTextLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		Font largeFont = statusTextLabel.getFont().deriveFont(Font.BOLD, 16F);
		statusTextLabel.setFont(largeFont);
		
		messageLabel = new JLabel( "sizeinit" );
		messageLabel.setMinimumSize(new Dimension(iconRightLabelMinSize.width, 10));
		messageLabel.setVerticalTextPosition(JLabel.BOTTOM);
		messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		Font smallFont = messageLabel.getFont().deriveFont(Font.PLAIN, 10F);
		messageLabel.setFont(smallFont);
		
		progressTitleLabels = new JLabel[progressCnt];
		for( i=0; i<progressCnt; i++ ) {
			progressTitleLabels[i] = new JLabel( progressTitles[i] );
			progressTitleLabels[i].setFont(smallFont);
			progressTitleLabels[i].setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		
		progressBars = new JProgressBar[progressCnt];
		
		if( trackable ) {
								
			progressETALabels = new JLabel[progressCnt];
			for( i=0; i<progressCnt; i++ ) {

				progressETALabels[i] = new JLabel( "sizeinit" );
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
		for( i=0; i<progressCnt; i++ ) {
			interfacePane.add(progressTitleLabels[i]);
			interfacePane.add(Box.createVerticalStrut(1));
			interfacePane.add(progressBars[i]);
			if( trackable ) {
				interfacePane.add(Box.createVerticalStrut(1));				
				interfacePane.add(progressETALabels[i]);
			}
			interfacePane.add(Box.createVerticalStrut(3));
		}
		
		contentPane.add(interfacePane,BorderLayout.CENTER);		

	}
	
	public final void showDialog( boolean centered ) {
		if( !initialized ) {
			initialize();
			initialized = true;
		}
		if( centered ) {
			setLocationByPlatform(false);
			center();
		} else {
			setLocationByPlatform(true);
		}
		showDialog();
	}

	public final void showDialog( double xpos, double ypos ) {
		if( !initialized ) {
			initialize();
			initialized = true;
		}
		setLocationByPlatform(false);
		center(xpos, ypos);
		showDialog();
	}
	
	public final void showDialog() {

		if( !initialized ) {
			initialize();
			initialized = true;
		}
				
		setVisible(true);
					
	}
	
	public final void hideDialog() {
		setVisible(false);
	}

	public void center() {
		center(0.5, 0.5);		
	}
	
	public void center(double xpos, double ypos) {
		
		final double safeXpos = xpos > 1.0 ? 1.0 : (xpos < 0.0 ? 0.0 : xpos);
		final double safeYpos = ypos > 1.0 ? 1.0 : (ypos < 0.0 ? 0.0 : ypos);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		
		int x = (int) ((d.width - getWidth()) * safeXpos);
		int y = (int) ((d.height - getHeight()) * safeYpos);
		
		setLocation(x, y); 			
	}
			
	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

	public ApplicationTaskManager getTaskManager() {
		return taskManager;
	}

	public void setTaskManager(ApplicationTaskManager taskManager) {
		this.taskManager = taskManager;
	}
	
	public ApplicationMethodManager getMethodManager() {
		return methodManager;
	}

	public void setMethodManager(ApplicationMethodManager methodManager) {
		this.methodManager = methodManager;
	}
		
	public ErrorsDialog getErrorsDialog() {
		return errorsDialog;
	}

	public void setErrorsDialog(ErrorsDialog errorsDialog) {
		this.errorsDialog = errorsDialog;
	}

	@Override
	public void taskAborted(TaskEvent ev) {
		setStatus(ev.getStatus());
	}

	@Override
	public void taskFinished(TaskEvent ev) {
		setStatus(ev.getStatus());
	}

	@Override
	public void taskResumed(TaskEvent ev) {
		setStatus(ev.getStatus());
	}

	@Override
	public void taskStarted(TaskEvent ev) {
		setStatus(ev.getStatus());
	}

	@Override
	public void taskSuspended(TaskEvent ev) {
		setStatus(ev.getStatus());
	}
	
	@Override
	public void taskRequestChanged(TaskEvent ev) {
		setStatus(ev.getStatus());
	}

	@Override
	public void taskMessageSet(TaskEvent ev) {

		MessageSourceResolvable message = ev.getMessage();
		if( message != null ) {
			messageLabel.setText( messageSource.getMessage(message) );
		} else {
			messageLabel.setText( "" );
		}
						
	}
	
	@Override
	public void taskTickerUpdated(TaskEvent ev) {

		if( trackable ) {
			
			int[] tickerLimits = ev.getTickerLimits();
			int[] tickers = ev.getTickers();
			
			int lastValue;
			for( int i=0; i<progressCnt; i++ ) {
				lastValue = progressBars[i].getValue();
				progressBars[i].setMaximum(tickerLimits[i]);
				progressBars[i].setValue(tickers[i]);
				
				updateETA(i,tickerLimits[i],tickers[i],(lastValue<tickers[i]));
			}
			
		}
		
	}
	
	private void updateETA(int index, int limit, int value, boolean force) {

		long millis = System.currentTimeMillis();
		if( !force && lastETAUpdateMillis[index] != 0 && ( (millis-lastETAUpdateMillis[index]) < 1000 ) ) {
			return;
		}
		lastETAUpdateMillis[index] = millis;
		
		Integer secondsInteger = task.getExpectedSecondsUntilComplete(index);
		if( secondsInteger == null ) {		
			progressETALabels[index].setText( messageSource.getMessage("taskStatus.ETAUnknown") );
			return;
		}
				
		int seconds = secondsInteger;
		int minutes = seconds / 60;
		seconds = seconds % 60;
		
		String minutesString = ( minutes < 10 ? "0" : "" ) + Integer.toString(minutes);
		String secondsString = ( seconds < 10 ? "0" : "" ) + Integer.toString(seconds);
		
		progressETALabels[index].setText( messageSource.getMessage("taskStatus.ETAKnown", new Object[] {minutesString,secondsString}) );
		
	}

	private void setStatus(TaskStatus taskStatus) {

		statusIconLabel.setIcon( IconUtils.getLargeTaskIcon(taskStatus) );
		statusTextLabel.setText( messageSource.getMessage("taskStatusLong." + taskStatus ));

		abortTaskAction.setEnabled( taskStatus.isAbortable() );
		getTaskResultAction.setEnabled( taskStatus.isFinished() );
		getTaskErrorAction.setEnabled( taskStatus.isError() );
		if( mode == SignalMLOperationMode.APPLICATION ) {
			if( suspendable ) {
				suspendTaskAction.setEnabled( taskStatus.isSuspendable() );
				resumeTaskAction.setEnabled( taskStatus.isResumable() );
			}
		}
		else if( mode == SignalMLOperationMode.APPLET ) {
			closeAction.setEnabled( !taskStatus.isRunning() );
		}
		
		switch( taskStatus ) {
		
		case ABORTED :
			if( mode == SignalMLOperationMode.APPLICATION ) {
				if( suspendable ) {
					suspendAndResumeButton.setAction(suspendTaskAction);
				}
			}
			if( trackable ) {
				for( int i=0; i<progressCnt; i++ ) {
					if ( forceNotTrackable != null && forceNotTrackable[i] ) { 
						progressBars[i].setIndeterminate(false);
						progressBars[i].setMaximum(100);
						progressBars[i].setValue(0);
						progressBars[i].setStringPainted(true);
						progressBars[i].setString( messageSource.getMessage("taskStatus.ETAAborted") );
					} else {
						progressETALabels[i].setText( messageSource.getMessage("taskStatus.ETAAborted") );
					}
				}
			} else {
				progressBars[0].setIndeterminate(false);
				progressBars[0].setMaximum(100);
				progressBars[0].setValue(0);
				progressBars[0].setStringPainted(true);
				progressBars[0].setString( messageSource.getMessage("taskStatus.ETAAborted") );
			}						
			break;
			
		case SUSPENDED :
			if( mode == SignalMLOperationMode.APPLICATION ) {
				if( suspendable ) {
					suspendAndResumeButton.setAction(resumeTaskAction);
				}
			}			
			if( trackable ) {
				for( int i=0; i<progressCnt; i++ ) {
					if ( forceNotTrackable != null && forceNotTrackable[i] ) {
						progressBars[i].setIndeterminate(false);
						progressBars[i].setMaximum(100);
						progressBars[i].setValue(0);
						progressBars[i].setStringPainted(true);
						progressBars[i].setString( messageSource.getMessage("taskStatus.ETASuspended") );
					} else {
						progressETALabels[i].setText( messageSource.getMessage("taskStatus.ETASuspended") );
					}
				}
			} else {
				progressBars[0].setIndeterminate(false);
				progressBars[0].setMaximum(100);
				progressBars[0].setValue(0);
				progressBars[0].setStringPainted(true);
				progressBars[0].setString( messageSource.getMessage("taskStatus.ETASuspended") );
			}
			break;
		
		case FINISHED :
			abortAndResultButton.setAction(getTaskResultAction);
			if( mode == SignalMLOperationMode.APPLICATION ) {
				if( suspendable ) {
					suspendAndResumeButton.setAction(suspendTaskAction);
				}
			}
			int max;
			if( trackable ) {
				for( int i=0; i<progressCnt; i++ ) {
					if ( forceNotTrackable != null && forceNotTrackable[i] ) { 
						progressBars[i].setIndeterminate(false);
						progressBars[i].setMaximum(100);
						progressBars[i].setValue(100);
						progressBars[i].setStringPainted(true);
						progressBars[i].setString( messageSource.getMessage("taskStatus.ETAFinished") );
					} else {
						progressETALabels[i].setText( messageSource.getMessage("taskStatus.ETAFinished") );
						max = progressBars[i].getMaximum();
						if( max <= 0 ) {
							progressBars[i].setMaximum(1);
							progressBars[i].setValue(1);					
						} else {
							progressBars[i].setValue( max );
						}
					}
				}
			} else {
				progressBars[0].setIndeterminate(false);
				progressBars[0].setMaximum(100);
				progressBars[0].setValue(100);
				progressBars[0].setStringPainted(true);
				progressBars[0].setString( messageSource.getMessage("taskStatus.ETAFinished") );
			}
			break;
			
		case ERROR :		
			abortAndResultButton.setAction(getTaskErrorAction);
			if( mode == SignalMLOperationMode.APPLICATION ) {
				if( suspendable ) {
					suspendAndResumeButton.setAction(suspendTaskAction);
				}
			}
			if( trackable ) {
				for( int i=0; i<progressCnt; i++ ) {
					if ( forceNotTrackable != null && forceNotTrackable[i] ) { 
						progressBars[i].setIndeterminate(false);
						progressBars[i].setMaximum(100);
						progressBars[i].setValue(0);
						progressBars[i].setStringPainted(true);
						progressBars[i].setString( messageSource.getMessage("taskStatus.ETAFinished") );
					} else {
						progressETALabels[i].setText( messageSource.getMessage("taskStatus.ETAFinished") );							
					}
				}
			} else {
				progressBars[0].setIndeterminate(false);
				progressBars[0].setMaximum(100);
				progressBars[0].setValue(0);
				progressBars[0].setStringPainted(true);
				progressBars[0].setString( messageSource.getMessage("taskStatus.ETAFinished") );
			}
			break;
			
		default :
			abortAndResultButton.setAction(abortTaskAction);
			if( mode == SignalMLOperationMode.APPLICATION ) {
				if( suspendable ) {
					suspendAndResumeButton.setAction(suspendTaskAction);
				}
			}
			
			if (task.getMethod() instanceof StagerMethod) {
				forceNotTrackable = ((StagerMethod)task.getMethod()).getForceNotTrackable();
			}
			
			if( trackable ) {
				int[] tickerLimits;
				int[] tickers;
				synchronized(task) {
					tickerLimits = task.getTickerLimits();
					tickers = task.getTickers();
				}
				for( int i=0; i<progressCnt; i++ ) {
					
					if ( forceNotTrackable != null && forceNotTrackable[i] ) { 

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
	
	protected class CloseAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public CloseAction() {
			super(messageSource.getMessage("close"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/close.png") );
		}
		
		public void actionPerformed(ActionEvent arg0) {					
			hideDialog();
			
			if( mode == SignalMLOperationMode.APPLET ) {
				taskManager.removeTask(task);
			}
			
		}
		
	}

}

/* ViewerMainFrame.java created 2007-09-10
 * 
 */
package org.signalml.app.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.signalml.app.SvarogApplication;
import org.signalml.app.action.selector.ActionFocusListener;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.action.selector.ViewFocusSelector;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.config.ApplicationWorkspace;
import org.signalml.app.config.ConfigurationDefaults;
import org.signalml.app.config.MainFrameConfiguration;
import org.signalml.app.document.Document;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.DocumentManager;
import org.signalml.app.document.DocumentManagerEvent;
import org.signalml.app.document.DocumentManagerListener;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.method.ApplicationMethodDescriptor;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.ApplicationSerializableMethodDescriptor;
import org.signalml.app.model.WorkspaceTreeModel;
import org.signalml.app.task.ApplicationTaskDescriptor;
import org.signalml.app.task.ApplicationTaskManager;
import org.signalml.app.task.ApplicationTaskManagerDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.util.SnapToPageRunnable;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.app.view.element.LockableJSplitPane;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.exception.SanityCheckException;
import org.signalml.exception.SignalMLException;
import org.signalml.method.CleanupMethod;
import org.signalml.method.Method;
import org.signalml.method.SerializableMethod;
import org.signalml.method.TrackableMethod;
import org.signalml.method.mp5.MP5Method;
import org.signalml.task.LocalTask;
import org.signalml.task.Task;
import org.signalml.task.TaskStatus;
import org.signalml.util.SvarogConstants;
import org.springframework.context.support.MessageSourceAccessor;

/** ViewerMainFrame
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerMainFrame extends JFrame implements View, DocumentManagerListener, ViewFocusSelector {

	private static final long serialVersionUID = 1L;
	
	protected static final Logger logger = Logger.getLogger(ViewerMainFrame.class);

	public static final int INITIALIZATION_STEP_COUNT = 5;
	
	/* Bootstrapping */
	private List<MainWindowBootstrapTask> bootstrapList = new LinkedList<MainWindowBootstrapTask>();
	
	/* Configuration */
	private MainFrameConfiguration config;
	
	/* Localization */
	private MessageSourceAccessor messageSource;
		
	/* Window interface elements */
	private JPanel contentPane;
	
	/* GUI element provider */
	private ViewerElementManager elementManager;
	
	private boolean viewMode = false;
	private boolean leftPanelVisible = true;
	private boolean bottomPanelVisible = true;
	private boolean mainToolBarVisible = true;
	private boolean statusBarVisible = true;
	
	private boolean hadRestoredTasks = false;

	public void initialize() {

		SvarogApplication.splash( messageSource.getMessage("startup.initializingMainFrame"), false );
						
		setTitle(messageSource.getMessage("viewer.title", new Object[] {SvarogConstants.VERSION}));
		setIconImage( IconUtils.loadClassPathImage("org/signalml/app/icon/mainframe.png") );
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
				
		setContentPane(contentPane);
		
		elementManager.setView(this);
		elementManager.setOptionPaneParent(getRootPane());
		elementManager.setDialogParent(this);

		SvarogApplication.splash( messageSource.getMessage("startup.creatingInterface"), true );
				
		Method method = elementManager.getMethodManager().getMethodByName("mp5");
		if( method == null || !(method instanceof MP5Method) ) {
			throw new SanityCheckException( "No mp5 method" );
		}
		((MP5Method) method).setExecutorConfigurer(elementManager.getMp5ExecutorConfigurer());
		
		setJMenuBar(elementManager.getMenuBar());
		
		contentPane.add(elementManager.getMainToolBar(), BorderLayout.NORTH);
		
		contentPane.add(elementManager.getStatusBar(), BorderLayout.SOUTH);
				
		contentPane.add(elementManager.getVerticalSplitPane(), BorderLayout.CENTER);
		
		elementManager.configureAcceletators();
					
		bindListeners();
		
		addWindowListeners();				

		SvarogApplication.splash( messageSource.getMessage("startup.restoringTasks"), true );

		restoreTasks();
		
		SvarogApplication.splash( messageSource.getMessage("startup.finishingMainFrameInitialization"), true );
		
		pack();
		
		setLocationByPlatform(true);
		
		restoreViewPreferences();
		
		logger.debug("Main window initialized");
	
		SvarogApplication.splash( null, true );
		
	}
	
	public void bootstrap() {

		SvarogApplication.splash( messageSource.getMessage("startup.restoringWorkspace"), false );
		
		if( elementManager.getApplicationConfig().isRestoreWorkspace() ) {
			restoreWorkspace();
		}
				
		Iterator<MainWindowBootstrapTask> it = bootstrapList.iterator();
		while( it.hasNext() ) {
			it.next().run();
		}
		
		if( hadRestoredTasks ) {
			
			int ans = OptionPane.showResumeRestoredTasks(this);
			if( ans == OptionPane.YES_OPTION ) {
			
				ApplicationTaskManager taskManager = elementManager.getTaskManager();
				int taskCount = taskManager.getTaskCount();
				Task task;
				
				for( int i=0; i<taskCount; i++ ) {
					task = taskManager.getTaskAt(i);
					if( task.getStatus().isResumable() ) {
						taskManager.resumeTask(task);
					}
				}
				
			}
			
			hadRestoredTasks = false;
						
		}

		SvarogApplication.splash( null, true );
				
	}
			
	private void bindListeners() {
		
		WorkspaceTreeModel workspaceTreeModel = elementManager.getWorkspaceTreeModel();
		DocumentManager documentManager = elementManager.getDocumentManager();
		ViewerDocumentTabbedPane documentTabbedPane = elementManager.getDocumentTabbedPane();
		ActionFocusManager actionFocusManager = elementManager.getActionFocusManager();

		// remember listeners are processed last to first
		
		elementManager.getMrudRegistry().addMRUDRegistryListener(workspaceTreeModel);

		// XXX listener order matters here!
		documentManager.addDocumentManagerListener(documentTabbedPane);
		documentManager.addDocumentManagerListener(workspaceTreeModel);
		
		documentManager.addDocumentManagerListener(elementManager.getSignalTreeModel());
		documentManager.addDocumentManagerListener(elementManager.getMonitorTreeModel());
		documentManager.addDocumentManagerListener(elementManager.getTagTreeModel());
		documentManager.addDocumentManagerListener(elementManager.getBookTreeModel());
		
		documentManager.addDocumentManagerListener(this);		
		
		actionFocusManager.addActionFocusListener(documentTabbedPane);
		actionFocusManager.addActionFocusListener(elementManager.getWorkspaceTree());
		
		documentTabbedPane.addChangeListener(actionFocusManager);
		
		elementManager.getTaskManager().setStatusDialogParent(this);
		
	}	
	
					
	@Override
	public DocumentView createDocumentViewPanel(Document document) throws SignalMLException {
		return elementManager.createDocumentViewPanel(document);
	}
	
	private void addWindowListeners() {
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent ev) {				
				elementManager.getCloseWindowAction().actionPerformed(new ActionEvent(ViewerMainFrame.this,0,"close"));				
			}
					
		});
	
	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}
	
	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

	public void setStatus(String status) {
		elementManager.getStatusBar().setStatus(status);
	}
		
	@Override
	public void addActionFocusListener(ActionFocusListener listener) {
		// ignored
	}

	@Override
	public void removeActionFocusListener(ActionFocusListener listener) {
		// ignored		
	}

	@Override
	public View getActiveView() {
		return this;
	}

	public void closeView() {

		logger.debug("Main window closing");

		boolean hasSerializableTasks = false;
		boolean hasUnserializableTasks = false;
		boolean abortSerializable = false;
		
		ApplicationTaskManager taskManager = elementManager.getTaskManager();		
		synchronized( taskManager ) {
		
			int cnt = taskManager.getTaskCount();
			Task task;
			TaskStatus status;
			Method method;		
			for( int i=0; i<cnt; i++ ) {
				task = taskManager.getTaskAt(i);
				synchronized( task ) {					
					status = task.getStatus();
					if( status.isRunning() ) {
						method = task.getMethod();
						if( (method instanceof SerializableMethod) && status.isSuspendable() ) {
							hasSerializableTasks = true;
						} else if( task.getStatus().isAbortable() ) {
							hasUnserializableTasks = true;
						}
					}
				}
			}
			
		}
		
		if( hasSerializableTasks ) {
			int res = OptionPane.showSerializableTaskRunning(this);
			if( res == OptionPane.CANCEL_OPTION ) {
				return;
			} else if( res == OptionPane.NO_OPTION ) {
				abortSerializable = true;
			} 
		}
		
		if( hasUnserializableTasks ) {
			int res = OptionPane.showTaskRunning(this);
			if( res != OptionPane.OK_OPTION ) {
				return;
			}
		}
		
		DocumentFlowIntegrator documentFlowIntegrator = elementManager.getDocumentFlowIntegrator();
		try {
			boolean ok = documentFlowIntegrator.checkCloseAllDocuments();
			if( !ok ) {
				return;
			}
		} catch(SignalMLException ex) {
			logger.error("Failed to check close documents", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;			
		} catch(IOException ex) {
			logger.error("Failed to check close documentst - i/o exception", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;			
		}

		// now only the saved documents are present, others had been closed
		// save workspace
		saveWorkspace();
				
		ArrayList<ApplicationTaskDescriptor> taskList = new ArrayList<ApplicationTaskDescriptor>();
					
		synchronized( taskManager ) {
			
			int taskCount = taskManager.getTaskCount();
			int i;
			Task task;
			TaskStatus status;
			Method method;
			
			for( i=0; i<taskCount; i++ ) {
				task = taskManager.getTaskAt(i);
				synchronized( task ) {
					status = task.getStatus();
					method = task.getMethod();
					if( method instanceof SerializableMethod ) {
						if( !status.isSuspended() ) {
							if( !abortSerializable && status.isSuspendable() ) {
								task.suspend(false);
							} else if( status.isAbortable() ) {
								task.abort(false);
							}
						}
					} else {
						if( status.isAbortable() ) {
							task.abort(false);
						}
					}
				}
			}
			
			ApplicationTaskDescriptor taskDescriptor;
			SerializableMethod serializableMethod;
			File file;
			boolean serialized;
			
			for( i=0; i<taskCount; i++ ) {

				task = taskManager.getTaskAt(i);
				synchronized( task ) {
					status = task.getStatus();
				}
				if( status.isRunning() ) {
					taskManager.waitForTaskToStopWorking(task);
				}
				
				serialized = false;
				
				synchronized( task ) {
					status = task.getStatus();

					method = task.getMethod();
					
					if( status.isSuspended() || status.isFinished() ) {
					
						if( method instanceof SerializableMethod ) {
							serializableMethod = (SerializableMethod) method;
							
							taskDescriptor = new ApplicationTaskDescriptor();
							taskDescriptor.setMethodUID( method.getUID() );
							taskDescriptor.setStatus(status);
							
							try {
								file = serializableMethod.writeToPersistence(task.getData());
								taskDescriptor.setSerializationPath(file.getAbsolutePath());								
								taskList.add(taskDescriptor);						
								serialized = true;
							} catch (IOException ex) {
								logger.error( "Failed to serialize task", ex );
							} catch (SignalMLException ex) {
								logger.error( "Failed to serialize task", ex );
							}
							
						}
												
					}
					
					if( !serialized ) {
						if( method instanceof CleanupMethod ) {
							((CleanupMethod) method).cleanUp(task.getData());
						}
					}
										
				}
			}
			
			
		}
						
		ApplicationTaskManagerDescriptor taskManagerDescriptor = new ApplicationTaskManagerDescriptor(taskList);
		taskManagerDescriptor.setProfileDir(elementManager.getProfileDir());
		
		try {
			taskManagerDescriptor.writeToPersistence(null);
		} catch( Exception ex) {
			logger.error("Failed to write task manager descriptor", ex);
		}		
		
		try {
			documentFlowIntegrator.closeAllDocuments();
		} catch(SignalMLException ex) {
			logger.error("Failed to close documents", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;			
		} catch(IOException ex) {
			logger.error("Failed to close documentst - i/o exception", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;			
		}
		
		saveViewPreferences();
		setVisible(false);
		dispose();
		
		SvarogApplication.exit(0);

	}
		
	private void saveWorkspace() {
		
		ApplicationWorkspace workspace = new ApplicationWorkspace();
		workspace.setProfileDir(elementManager.getProfileDir());
		
		workspace.configureFrom(elementManager.getDocumentFlowIntegrator());
		
		try {
			workspace.writeToPersistence(null);
		} catch( IOException ex) {
			logger.error("Failed to write workspace", ex);
		}		
		
	}
	
	private void restoreWorkspace() {
		
		ApplicationWorkspace workspace = new ApplicationWorkspace();
		workspace.setProfileDir(elementManager.getProfileDir());

		try {
			workspace.readFromPersistence(null);
		} catch( IOException ex ) {
			if( ex instanceof FileNotFoundException ) {
				logger.debug("Seems like workspace doesn't exist - workspace will not be restored");
			} else {
				logger.error("Failed to read workspace configuration - workspace lost", ex);			
			}
		}

		workspace.configureIntegrator(elementManager.getDocumentFlowIntegrator());
		
	}

	public boolean isViewMode() {
		return viewMode;
	}

	public void setViewMode(boolean viewMode) {		
		if( this.viewMode != viewMode ) {
			this.viewMode = viewMode;
			elementManager.getViewModeAction().putValue(AbstractAction.SELECTED_KEY, viewMode);			
			ApplicationConfiguration applicationConfig = elementManager.getApplicationConfig();
			if( applicationConfig.isViewModeHidesMainToolBar() ) {
				setMainToolBarVisible(!viewMode);
			}
			if( applicationConfig.isViewModeHidesLeftPanel() ) {
				setLeftPanelVisible(!viewMode);
			}
			if( applicationConfig.isViewModeHidesBottomPanel() ) {
				setBottomPanelVisible(!viewMode);
			}
			if( applicationConfig.isViewModeCompactsPageTagBars() || applicationConfig.isViewModeSnapsToPage() ) {
				
				DocumentManager documentManager = elementManager.getDocumentManager();
				synchronized( documentManager ) {
					int cnt = documentManager.getDocumentCount(ManagedDocumentType.SIGNAL);
					SignalDocument signalDocument;
					SignalView signalView;
					
					for( int i=0; i<cnt; i++ ) {
						signalDocument = (SignalDocument) documentManager.getDocumentAt(ManagedDocumentType.SIGNAL, i);
						signalView = (SignalView) signalDocument.getDocumentView();
						if( applicationConfig.isViewModeCompactsPageTagBars() ) {
							for( SignalPlot plot : signalView.getPlots() ) {
								plot.getSignalPlotColumnHeader().setCompact(viewMode);
							}
						}
						if( applicationConfig.isViewModeSnapsToPage() ) {
							SwingUtilities.invokeLater( new SnapToPageRunnable(signalView, viewMode) );
						}
					}
				}
				
			}			
		}
	}	
		
	@Override
	public boolean isLeftPanelVisible() {
		return leftPanelVisible;
	}

	@Override
	public void setLeftPanelVisible(boolean visible) {
		if( this.leftPanelVisible != visible ) {
			this.leftPanelVisible = visible;
			elementManager.getShowLeftPanelAction().putValue( AbstractAction.SELECTED_KEY, visible );
			elementManager.getLeftPane().setVisible(visible);
			LockableJSplitPane horizontalSplitPane = elementManager.getHorizontalSplitPane();
			if( visible ) {
				horizontalSplitPane.setDividerLocation(horizontalSplitPane.getLastDividerLocation());				
			} else {
				horizontalSplitPane.setDividerLocation(horizontalSplitPane.getMinimumDividerLocation());				
			}
			horizontalSplitPane.setOneTouchExpandable(visible);
			horizontalSplitPane.setLocked(!visible);			
		}
	}

	@Override
	public boolean isBottomPanelVisible() {
		return bottomPanelVisible;
	}
	
	@Override
	public void setBottomPanelVisible(boolean visible) {
		if( this.bottomPanelVisible != visible ) {
			this.bottomPanelVisible = visible;
			elementManager.getShowBottomPanelAction().putValue( AbstractAction.SELECTED_KEY, visible );
			elementManager.getBottomPane().setVisible(visible);
			LockableJSplitPane verticalSplitPane = elementManager.getVerticalSplitPane();
			if( visible ) {
				verticalSplitPane.setDividerLocation(verticalSplitPane.getLastDividerLocation());
			} else {
				verticalSplitPane.setDividerLocation(verticalSplitPane.getMaximumDividerLocation());
			}
			verticalSplitPane.setOneTouchExpandable(visible);
			verticalSplitPane.setLocked(!visible);
		}
	}
	
	@Override
	public boolean isMainToolBarVisible() {
		return mainToolBarVisible;
	}

	@Override
	public void setMainToolBarVisible(boolean visible) {
		if( this.mainToolBarVisible != visible ) {
			this.mainToolBarVisible = visible;
			elementManager.getMainToolBar().setVisible(visible);
			elementManager.getShowMainToolBarAction().putValue( AbstractAction.SELECTED_KEY, visible );
		}		
	}

	@Override
	public boolean isStatusBarVisible() {
		return statusBarVisible;
	}

	@Override
	public void setStatusBarVisible(boolean visible) {
		if( this.statusBarVisible != visible ) {
			this.statusBarVisible = visible;
			elementManager.getStatusBar().setVisible(visible);
			elementManager.getShowStatusBarAction().putValue( AbstractAction.SELECTED_KEY, visible );
		}
	}

	private void restoreTasks() {

		ApplicationMethodManager methodManager = elementManager.getMethodManager();
		ApplicationTaskManager taskManager = elementManager.getTaskManager();
		
		ApplicationTaskManagerDescriptor taskManagerDescriptor = new ApplicationTaskManagerDescriptor();
		taskManagerDescriptor.setProfileDir(elementManager.getProfileDir());
		
		try {
			taskManagerDescriptor.readFromPersistence(null);
		} catch( FileNotFoundException ex ) {
			logger.debug("Seems like task configuration doesn't exist - tasks will not be restored");
			return;
		} catch( Exception ex ) {
			logger.error("Failed to read task manager configuration - tasks lost", ex);			
			return;
		}

		Iterator<ApplicationTaskDescriptor> taskIterator = taskManagerDescriptor.taskIterator();
		
		ApplicationTaskDescriptor descriptor;
		Method method;
		
		while( taskIterator.hasNext() ) {
			
			descriptor = taskIterator.next();
			
			method = methodManager.getMethodByUID( descriptor.getMethodUID() );
			if( method == null ) {
				logger.warn( "Method with UID [" + descriptor.getMethodUID() + "] not found" );
				continue;
			}
			
			if( !(method instanceof SerializableMethod) ) {
				logger.warn( "Method with UID [" + descriptor.getMethodUID() + "] not serializable" );
				continue;
			}

			ApplicationMethodDescriptor methodDescriptor = methodManager.getMethodData(method);
			Object data;
			if( methodDescriptor == null || !(methodDescriptor instanceof ApplicationSerializableMethodDescriptor) ) {
				data = method.createData();
			} else {
				data = ((ApplicationSerializableMethodDescriptor) methodDescriptor).createDeserializedData(methodManager);
			}
			
			SerializableMethod serializableMethod = (SerializableMethod) method;
			
			try {
				serializableMethod.readFromPersistence(data, new File( descriptor.getSerializationPath() ) );
			} catch (IOException ex) {
				logger.error( "Failed to deserialize method data", ex );
				continue;
			} catch (SignalMLException ex) {
				logger.error( "Failed to deserialize method data", ex );
				continue;
			}

			TaskStatus status = descriptor.getStatus();
			if( status != TaskStatus.FINISHED && status != TaskStatus.SUSPENDED ) {
				logger.error( "Incorrect task status [" + status + "]" );
				continue;
			}
			
			Task task = new LocalTask(method, data, (method instanceof TrackableMethod), status);
			taskManager.addTask(task);
			
			if( status.isResumable() ) {
				hadRestoredTasks = true;
			}
									
		}
				
	}
	
	public void restoreViewPreferences() {
		
		config = new MainFrameConfiguration();
		ConfigurationDefaults.setMainFrameConfigurationDefaults(config);
		config.setProfileDir(elementManager.getProfileDir());
		config.setStreamer(elementManager.getStreamer());
		
		try {
			config.readFromPersistence(null);
		} catch( IOException ex ) {
			if( ex instanceof FileNotFoundException ) {
				logger.debug("No mainframe configuration, will use defaults");
				return;
			} else {
				logger.error("Bad mainframe configuration, will use defaults", ex);
				return;
			}
		}

		setSize(new Dimension(config.getXSize(),config.getYSize()));
		
		if( config.isMaximized() ) {
			setExtendedState(JFrame.MAXIMIZED_BOTH);
		} else {
			setExtendedState(JFrame.NORMAL);
		}
		
		setMainToolBarVisible( config.isMainToolBarVisible() );
		setStatusBarVisible( config.isStatusBarVisible() );
		
		elementManager.getHorizontalSplitPane().setDividerLocation(config.getHDividerLocation());
		elementManager.getVerticalSplitPane().setDividerLocation(config.getVDividerLocation());

		setLeftPanelVisible( config.isLeftPanelVisible() );
		setBottomPanelVisible( config.isBottomPanelVisible() );

		setViewMode( config.isViewMode() );
						
	}
	
	public void saveViewPreferences() {

		boolean maximized = ( (getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0 );
		config.setMaximized( maximized );
		if( !maximized ) {
			config.setXSize((int) getSize().width);
			config.setYSize((int) getSize().height);
		}
		
		config.setMainToolBarVisible(mainToolBarVisible);
		config.setStatusBarVisible(statusBarVisible);
		config.setLeftPanelVisible(leftPanelVisible);
		config.setBottomPanelVisible(bottomPanelVisible);
		
		if( leftPanelVisible ) {
			config.setHDividerLocation(elementManager.getHorizontalSplitPane().getDividerLocation());
		} else {
			config.setHDividerLocation(elementManager.getHorizontalSplitPane().getLastDividerLocation());
		}

		if( bottomPanelVisible ) {
			config.setVDividerLocation(elementManager.getVerticalSplitPane().getDividerLocation());
		} else {
			config.setVDividerLocation(elementManager.getVerticalSplitPane().getLastDividerLocation());
		}

		config.setViewMode(viewMode);
		
		try {
			File file = config.getStandardFile(elementManager.getProfileDir());
			config.writeToXML(file, elementManager.getStreamer());
		} catch( IOException ex ) {
			logger.error("Failed to write mainframe configuration", ex);
		}
		
	}
	
	public ViewerElementManager getElementManager() {
		return elementManager;
	}

	public void setElementManager(ViewerElementManager elementManager) {
		this.elementManager = elementManager;
	}

	@Override
	public void documentAdded(DocumentManagerEvent e) {
		elementManager.getSaveAllDocumentsAction().setEnabledAsNeeded();
	}

	@Override
	public void documentPathChanged(DocumentManagerEvent e) {
		// this is not interesting
	}

	@Override
	public void documentRemoved(DocumentManagerEvent e) {
		elementManager.getSaveAllDocumentsAction().setEnabledAsNeeded();
	}
	
	public void addBootstrap(MainWindowBootstrapTask task) {
		bootstrapList.add(task);
	}
	
	public abstract class MainWindowBootstrapTask implements Runnable {};
		
}

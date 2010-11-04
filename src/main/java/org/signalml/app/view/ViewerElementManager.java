/* ViewerElementManager.java created 2008-02-20
 *
 */

package org.signalml.app.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import multiplexer.jmx.client.JmxClient;

import org.signalml.SignalMLOperationMode;
import org.signalml.app.action.AbortAllTasksAction;
import org.signalml.app.action.ApplyDefaultMontageAction;
import org.signalml.app.action.CheckSignalAction;
import org.signalml.app.action.CloseDocumentAction;
import org.signalml.app.action.CloseTagAction;
import org.signalml.app.action.CloseWindowAction;
import org.signalml.app.action.EditPreferencesAction;
import org.signalml.app.action.EditSignalMontageAction;
import org.signalml.app.action.EditSignalParametersAction;
import org.signalml.app.action.EditStoredMontagesAction;
import org.signalml.app.action.EditTagDescriptionAction;
import org.signalml.app.action.EditTagStylesAction;
import org.signalml.app.action.ExportBookAction;
import org.signalml.app.action.ExportSignalAction;
import org.signalml.app.action.ExportTagAction;
import org.signalml.app.action.HelpContentsAction;
import org.signalml.app.action.IterateMethodAction;
import org.signalml.app.action.NewTagAction;
import org.signalml.app.action.OpenDocumentAction;
import org.signalml.app.action.OpenMonitorAction;
import org.signalml.app.action.OpenTagAction;
import org.signalml.app.action.PreciseSelectionAction;
import org.signalml.app.action.RemoveAllAbortedTasksAction;
import org.signalml.app.action.RemoveAllFailedTasksAction;
import org.signalml.app.action.RemoveAllFinishedTasksAction;
import org.signalml.app.action.RemoveAllTasksAction;
import org.signalml.app.action.ResumeAllTasksAction;
import org.signalml.app.action.RunMethodAction;
import org.signalml.app.action.SaveAllDocumentsAction;
import org.signalml.app.action.SaveDocumentAction;
import org.signalml.app.action.SaveDocumentAsAction;
import org.signalml.app.action.SaveTagAction;
import org.signalml.app.action.SaveTagAsAction;
import org.signalml.app.action.ShowBottomPanelAction;
import org.signalml.app.action.ShowLeftPanelAction;
import org.signalml.app.action.ShowMainToolBarAction;
import org.signalml.app.action.ShowStatusBarAction;
import org.signalml.app.action.SuspendAllTasksAction;
import org.signalml.app.action.UnavailableMethodAction;
import org.signalml.app.action.ViewModeAction;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.config.preset.BookFilterPresetManager;
import org.signalml.app.config.preset.FFTSampleFilterPresetManager;
import org.signalml.app.config.preset.TimeDomainSampleFilterPresetManager;
import org.signalml.app.config.preset.SignalExportPresetManager;
import org.signalml.app.document.BookDocument;
import org.signalml.app.document.DocumentDetector;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.DocumentManager;
import org.signalml.app.document.MRUDRegistry;
import org.signalml.app.document.MonitorSignalDocument;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.iterate.IterationSetupDialog;
import org.signalml.app.method.mp5.MP5ApplicationExecutorConfigurer;
import org.signalml.app.method.mp5.MP5ExecutorManager;
import org.signalml.app.method.mp5.MP5LocalExecutorDialog;
import org.signalml.app.method.mp5.MP5RemoteExecutorDialog;
import org.signalml.app.model.BookTreeModel;
import org.signalml.app.model.MonitorTreeModel;
import org.signalml.app.model.PropertySheetModel;
import org.signalml.app.model.SignalTreeModel;
import org.signalml.app.model.TableToTextExporter;
import org.signalml.app.model.TagTreeModel;
import org.signalml.app.model.TaskTableModel;
import org.signalml.app.model.WorkspaceTreeModel;
import org.signalml.app.montage.MontagePresetManager;
import org.signalml.app.task.ApplicationTaskManager;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.book.AtomTableDialog;
import org.signalml.app.view.book.BookView;
import org.signalml.app.view.book.filter.BookFilterDialog;
import org.signalml.app.view.dialog.ApplicationPreferencesDialog;
import org.signalml.app.view.dialog.DynamicCompilationWarningDialog;
import org.signalml.app.view.dialog.EditTagAnnotationDialog;
import org.signalml.app.view.dialog.EditTagDescriptionDialog;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.ExportSignalDialog;
import org.signalml.app.view.dialog.HelpDialog;
import org.signalml.app.view.dialog.NewTagDialog;
import org.signalml.app.view.dialog.OpenDocumentDialog;
import org.signalml.app.view.dialog.OpenMonitorDialog;
import org.signalml.app.view.dialog.PleaseWaitDialog;
import org.signalml.app.view.dialog.RegisterCodecDialog;
import org.signalml.app.view.dialog.SeriousWarningDialog;
import org.signalml.app.view.dialog.SignalParametersDialog;
import org.signalml.app.view.dialog.SignalSelectionDialog;
import org.signalml.app.view.dialog.TagStylePaletteDialog;
import org.signalml.app.view.element.LockableJSplitPane;
import org.signalml.app.view.monitor.CheckSignalDialog;
import org.signalml.app.view.montage.EditFFTSampleFilterDialog;
import org.signalml.app.view.montage.EditTimeDomainSampleFilterDialog;
import org.signalml.app.view.montage.SignalMontageDialog;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.signal.popup.SlavePlotSettingsPopupDialog;
import org.signalml.app.view.tag.comparison.TagComparisonDialog;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.compilation.DynamicCompilationContext;
import org.signalml.method.Method;
import org.signalml.method.iterator.IterableMethod;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.export.view.DocumentView;
import org.signalml.plugin.export.view.ViewerTreePane;
import org.signalml.util.SvarogConstants;
import org.springframework.context.support.MessageSourceAccessor;

import com.thoughtworks.xstream.XStream;
import org.signalml.app.action.StartMonitorRecordingAction;
import org.signalml.app.action.StopMonitorRecordingAction;
import org.signalml.app.view.monitor.StartMonitorRecordingDialog;



/** ViewerElementManager
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerElementManager {

	private SignalMLOperationMode mode;

	public ViewerElementManager(SignalMLOperationMode mode) {
		this.mode = mode;
	}

	/* Localization */
	private MessageSourceAccessor messageSource;

	/* XML Streamer */
	private XStream streamer;

	/* Imported logic */
	private File profileDir;
	private DocumentManager documentManager;
	private MRUDRegistry mrudRegistry;
	private SignalMLCodecManager codecManager;
	private ApplicationConfiguration applicationConfig;
	private DocumentDetector documentDetector;
	private ApplicationMethodManager methodManager;
	private ApplicationTaskManager taskManager;
	private ActionFocusManager actionFocusManager;
	private MontagePresetManager montagePresetManager;
	private BookFilterPresetManager bookFilterPresetManager;
	private SignalExportPresetManager signalExportPresetManager;
	private FFTSampleFilterPresetManager fftFilterPresetManager;
	private TimeDomainSampleFilterPresetManager timeDomainSampleFilterPresetManager;

	private MP5ExecutorManager mp5ExecutorManager;
	private Preferences preferences;

	/* Main logic */
	private DocumentFlowIntegrator documentFlowIntegrator;

	/* Models */
	private WorkspaceTreeModel workspaceTreeModel;
	private BookTreeModel bookTreeModel;
	private SignalTreeModel signalTreeModel;
	private MonitorTreeModel monitorTreeModel;
	private TagTreeModel tagTreeModel;
	private TaskTableModel taskTableModel;
	private PropertySheetModel propertySheetModel;

	/* Window interface elements */
	private Component optionPaneParent;
	private Window dialogParent;
	private View view;

	private ViewerStatusBar statusBar;
	private JMenuBar menuBar;
	private JToolBar mainToolBar;

	private LockableJSplitPane verticalSplitPane;
	private LockableJSplitPane horizontalSplitPane;

	private JPanel leftPane;
	private JPanel rightPane;
	private JPanel bottomPane;

	private ViewerTreePane workspaceTreePane;
	private ViewerTreePane bookTreePane;
	private ViewerTreePane signalTreePane;
	private ViewerTreePane monitorTreePane;
	private ViewerTreePane tagTreePane;

	private ViewerWorkspaceTree workspaceTree;
	private ViewerSignalTree signalTree;
	private ViewerMonitorTree monitorTree;
	private ViewerBookTree bookTree;
	private ViewerTagTree tagTree;

	private ViewerTaskTable taskTable;

	private ViewerConsolePane console;
	private ViewerTaskTablePane taskTablePane;
	private ViewerPropertySheetPane propertySheetPane;

	private ViewerTabbedPane treeTabbedPane;
	private ViewerDocumentTabbedPane documentTabbedPane;
	private ViewerTabbedPane propertyTabbedPane;

	/* Dialogs */
        private CheckSignalDialog checkSignalDialog;
	private ErrorsDialog errorsDialog;
	private PleaseWaitDialog pleaseWaitDialog;
	private SeriousWarningDialog seriousWarningDialog;
	private ApplicationPreferencesDialog applicationPreferencesDialog;
	private OpenDocumentDialog openDocumentDialog;
	private OpenMonitorDialog openMonitorDialog;
	private RegisterCodecDialog registerCodecDialog;
	private SignalParametersDialog signalParametersDialog;
	private SignalMontageDialog signalMontageDialog;
	private SignalSelectionDialog signalSelectionDialog;
	private NewTagDialog newTagDialog;
	private EditTagAnnotationDialog editTagAnnotationDialog;
	private SlavePlotSettingsPopupDialog slavePlotSettingsPopupDialog;
	private TagStylePaletteDialog tagStylePaletteDialog;
	private HelpDialog helpDialog;
	private TagComparisonDialog tagComparisonDialog;
	private EditTagDescriptionDialog editTagDescriptionDialog;
	private IterationSetupDialog iterationSetupDialog;
	private ExportSignalDialog exportSignalDialog;
	private EditFFTSampleFilterDialog editFFTSampleFilterDialog;
	private EditTimeDomainSampleFilterDialog editTimeDomainSampleFilterDialog;
	private StartMonitorRecordingDialog startMonitorRecordingDialog;

	private MP5LocalExecutorDialog mp5LocalExecutorDialog;
	private MP5RemoteExecutorDialog mp5RemoteExecutorDialog;
	private DynamicCompilationWarningDialog dynamicCompilationWarningDialog;
	private AtomTableDialog atomTableDialog;
	private BookFilterDialog bookFilterDialog;

	/* Actions */
        private CheckSignalAction checkSignalAction;
	private CloseWindowAction closeWindowAction;
	private EditPreferencesAction editPreferencesAction;
	private HelpContentsAction helpContentsAction;
	private ViewModeAction viewModeAction;
	private ShowMainToolBarAction showMainToolBarAction;
	private ShowStatusBarAction showStatusBarAction;
	private ShowLeftPanelAction showLeftPanelAction;
	private ShowBottomPanelAction showBottomPanelAction;
	private OpenDocumentAction openDocumentAction;
	private OpenMonitorAction openMonitorAction;
	private CloseDocumentAction closeActiveDocumentAction;
	private SaveAllDocumentsAction saveAllDocumentsAction;
	private SaveDocumentAction saveActiveDocumentAction;
	private SaveDocumentAsAction saveActiveDocumentAsAction;
	private NewTagAction newTagAction;
	private OpenTagAction openTagAction;
	private CloseTagAction closeTagAction;
	private SaveTagAction saveTagAction;
	private SaveTagAsAction saveTagAsAction;
	private OpenTagAction importTagAction;
	private ExportTagAction exportTagAction;
	private EditSignalParametersAction editSignalParametersAction;
	private EditSignalMontageAction editSignalMontageAction;
	private ApplyDefaultMontageAction applyDefaultMontageAction;
	private PreciseSelectionAction preciseSelectionAction;
	private EditTagStylesAction editTagStylesAction;
	private EditTagDescriptionAction editTagDescriptionAction;
	private EditStoredMontagesAction editStoredMontagesAction;
	private ExportSignalAction exportSignalAction;
	private ExportBookAction exportBookAction;
	private AbortAllTasksAction abortAllTasksAction;
	private SuspendAllTasksAction suspendAllTasksAction;
	private ResumeAllTasksAction resumeAllTasksAction;
	private RemoveAllTasksAction removeAllTasksAction;
	private RemoveAllFinishedTasksAction removeAllFinishedTasksAction;
	private RemoveAllAbortedTasksAction removeAllAbortedTasksAction;
	private RemoveAllFailedTasksAction removeAllFailedTasksAction;
	private StartMonitorRecordingAction startMonitorRecordingAction;
	private StopMonitorRecordingAction stopMonitorRecordingAction;

	private ArrayList<AbstractSignalMLAction> runMethodActions;
	private ArrayList<AbstractSignalMLAction> iterateMethodActions;

	/* File chooser */
	private ViewerFileChooser fileChooser;

	/* MP5 executors */

	/* Menu */
	private JMenu fileMenu;
	private JMenu editMenu;
	private JMenu viewMenu;
	private JMenu monitorMenu;
	private JMenu toolsMenu;
	private JMenu helpMenu;

	/* Other */
	private TableToTextExporter tableToTextExporter;
	private MP5ApplicationExecutorConfigurer mp5ExecutorConfigurer;
	private JmxClient jmxClient;
	private JmxClient tagClient;

	public SignalMLOperationMode getMode() {
		return mode;
	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

	public Component getOptionPaneParent() {
		return optionPaneParent;
	}

	public void setOptionPaneParent(Component optionPaneParent) {
		this.optionPaneParent = optionPaneParent;
	}

	public Window getDialogParent() {
		return dialogParent;
	}

	public void setDialogParent(Window dialogParent) {
		this.dialogParent = dialogParent;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public XStream getStreamer() {
		return streamer;
	}

	public void setStreamer(XStream streamer) {
		this.streamer = streamer;
	}

	public File getProfileDir() {
		return profileDir;
	}

	public void setProfileDir(File profileDir) {
		this.profileDir = profileDir;
	}

	public DocumentManager getDocumentManager() {
		return documentManager;
	}

	public void setDocumentManager(DocumentManager documentManager) {
		this.documentManager = documentManager;
	}

	public MRUDRegistry getMrudRegistry() {
		return mrudRegistry;
	}

	public void setMrudRegistry(MRUDRegistry mrudRegistry) {
		this.mrudRegistry = mrudRegistry;
	}

	public SignalMLCodecManager getCodecManager() {
		return codecManager;
	}

	public void setCodecManager(SignalMLCodecManager codecManager) {
		this.codecManager = codecManager;
	}

	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

	public DocumentDetector getDocumentDetector() {
		return documentDetector;
	}

	public void setDocumentDetector(DocumentDetector documentDetector) {
		this.documentDetector = documentDetector;
	}

	public ApplicationMethodManager getMethodManager() {
		return methodManager;
	}

	public void setMethodManager(ApplicationMethodManager methodManager) {
		this.methodManager = methodManager;
	}

	public ApplicationTaskManager getTaskManager() {
		return taskManager;
	}

	public void setTaskManager(ApplicationTaskManager taskManager) {
		this.taskManager = taskManager;
	}

	public ActionFocusManager getActionFocusManager() {
		return actionFocusManager;
	}

	public void setActionFocusManager(ActionFocusManager actionFocusManager) {
		this.actionFocusManager = actionFocusManager;
	}

	public MontagePresetManager getMontagePresetManager() {
		return montagePresetManager;
	}

	public void setMontagePresetManager(MontagePresetManager montagePresetManager) {
		this.montagePresetManager = montagePresetManager;
	}

	public BookFilterPresetManager getBookFilterPresetManager() {
		return bookFilterPresetManager;
	}

	public void setBookFilterPresetManager(BookFilterPresetManager bookFilterPresetManager) {
		this.bookFilterPresetManager = bookFilterPresetManager;
	}

	public SignalExportPresetManager getSignalExportPresetManager() {
		return signalExportPresetManager;
	}

	public void setSignalExportPresetManager(SignalExportPresetManager signalExportPresetManager) {
		this.signalExportPresetManager = signalExportPresetManager;
	}

	public FFTSampleFilterPresetManager getFftFilterPresetManager() {
		return fftFilterPresetManager;
	}

	public void setFftFilterPresetManager(FFTSampleFilterPresetManager fftFilterPresetManager) {
		this.fftFilterPresetManager = fftFilterPresetManager;
	}

	public TimeDomainSampleFilterPresetManager getTimeDomainSampleFilterPresetManager() {
		return timeDomainSampleFilterPresetManager;
	}

	public void setTimeDomainSampleFilterPresetManager(TimeDomainSampleFilterPresetManager timeDomainSampleFilterPresetManager) {
		this.timeDomainSampleFilterPresetManager = timeDomainSampleFilterPresetManager;
	}

	public MP5ExecutorManager getMp5ExecutorManager() {
		return mp5ExecutorManager;
	}

	public void setMp5ExecutorManager(MP5ExecutorManager mp5ExecutorManager) {
		this.mp5ExecutorManager = mp5ExecutorManager;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

	public DocumentFlowIntegrator getDocumentFlowIntegrator() {
		if (documentFlowIntegrator == null) {
			documentFlowIntegrator = new DocumentFlowIntegrator();
			documentFlowIntegrator.setMessageSource(messageSource);
			documentFlowIntegrator.setDocumentManager(getDocumentManager());
			documentFlowIntegrator.setMrudRegistry(getMrudRegistry());
			documentFlowIntegrator.setCodecManager(getCodecManager());
			documentFlowIntegrator.setActionFocusManager(getActionFocusManager());
			documentFlowIntegrator.setApplicationConfig(getApplicationConfig());
			documentFlowIntegrator.setMontagePresetManager(getMontagePresetManager());
			documentFlowIntegrator.setOptionPaneParent(getOptionPaneParent());
			documentFlowIntegrator.setFileChooser(getFileChooser());
			documentFlowIntegrator.setSignalParametersDialog(getSignalParametersDialog());
			documentFlowIntegrator.setPleaseWaitDialog(getPleaseWaitDialog());
		}
		return documentFlowIntegrator;
	}

	public WorkspaceTreeModel getWorkspaceTreeModel() {
		if (workspaceTreeModel == null) {
			workspaceTreeModel = new WorkspaceTreeModel();
			workspaceTreeModel.setDocumentManager(getDocumentManager());
			workspaceTreeModel.setMrudRegistry(getMrudRegistry());
		}
		return workspaceTreeModel;
	}

	public BookTreeModel getBookTreeModel() {
		if (bookTreeModel == null) {
			bookTreeModel = new BookTreeModel();
			bookTreeModel.setDocumentManager(getDocumentManager());
		}
		return bookTreeModel;
	}

	public SignalTreeModel getSignalTreeModel() {
		if (signalTreeModel == null) {
			signalTreeModel = new SignalTreeModel();
			signalTreeModel.setDocumentManager(getDocumentManager());
		}
		return signalTreeModel;
	}

	public MonitorTreeModel getMonitorTreeModel() {
		if (monitorTreeModel == null) {
			monitorTreeModel = new MonitorTreeModel();
			monitorTreeModel.setDocumentManager(getDocumentManager());
		}
		return monitorTreeModel;
	}

	public TagTreeModel getTagTreeModel() {
		if (tagTreeModel == null) {
			tagTreeModel = new TagTreeModel();
			tagTreeModel.setDocumentManager(getDocumentManager());
		}
		return tagTreeModel;
	}

	public TaskTableModel getTaskTableModel() {
		if (taskTableModel == null) {
			taskTableModel = new TaskTableModel();
			taskTableModel.setMessageSource(messageSource);
			taskTableModel.setTaskManager(getTaskManager());
		}
		return taskTableModel;
	}

	public PropertySheetModel getPropertySheetModel() {
		if (propertySheetModel == null) {
			propertySheetModel = new PropertySheetModel();
			propertySheetModel.setMessageSource(messageSource);
		}
		return propertySheetModel;
	}

	public ViewerStatusBar getStatusBar() {
		if (statusBar == null) {
			statusBar = new ViewerStatusBar(messageSource);
			statusBar.setMaximizeDocumentsAction(getViewModeAction());
			statusBar.setActionFocusManager(getActionFocusManager());
			statusBar.initialize();
			statusBar.setStatus(messageSource.getMessage("viewer.welcomeStatus",
			                    new Object[] {SvarogConstants.VERSION}));
			getActionFocusManager().addActionFocusListener(statusBar);
		}
		return statusBar;
	}

	public JMenu getFileMenu() {
		if (fileMenu == null) {
			JMenu importSubmenu = new JMenu(messageSource.getMessage("menu.import"));
			importSubmenu.add(getImportTagAction());

			JMenu exportSubmenu = new JMenu(messageSource.getMessage("menu.export"));
			exportSubmenu.add(getExportTagAction());
			exportSubmenu.add(getExportSignalAction());
			exportSubmenu.add(getExportBookAction());

			fileMenu = new JMenu(messageSource.getMessage("menu.file"));

			fileMenu.add(getOpenDocumentAction());
			fileMenu.add(getSaveActiveDocumentAction());
			fileMenu.add(getSaveActiveDocumentAsAction());
			fileMenu.add(getSaveAllDocumentsAction());
			fileMenu.add(getCloseActiveDocumentAction());
			fileMenu.addSeparator();
			fileMenu.add(getNewTagAction());
			fileMenu.add(getOpenTagAction());
			fileMenu.add(getSaveTagAction());
			fileMenu.add(getSaveTagAsAction());
			fileMenu.add(getCloseTagAction());
			fileMenu.addSeparator();
			fileMenu.add(importSubmenu);
			fileMenu.add(exportSubmenu);

			if (mode == SignalMLOperationMode.APPLICATION) {
				fileMenu.addSeparator();
				fileMenu.add(getCloseWindowAction());
			}
		}
		return fileMenu;
	}

	public JMenu getEditMenu() {
		if (editMenu == null) {
			editMenu = new JMenu(messageSource.getMessage("menu.edit"));

			editMenu.add(getPreciseSelectionAction());
			editMenu.addSeparator();
			editMenu.add(getEditSignalParametersAction());
			editMenu.add(getEditSignalMontageAction());
			editMenu.add(getApplyDefaultMontageAction());
			editMenu.addSeparator();
			editMenu.add(getEditTagStylesAction());
			editMenu.add(getEditTagDescriptionAction());
			editMenu.addSeparator();
			if (mode == SignalMLOperationMode.APPLICATION) {
				editMenu.add(getEditStoredMontagesAction());
			}
			editMenu.add(getEditPreferencesAction());
		}
		return editMenu;
	}

	public JMenu getViewMenu() {
		if (viewMenu == null) {
			viewMenu = new JMenu(messageSource.getMessage("menu.view"));

			viewMenu.add(new JCheckBoxMenuItem(getShowMainToolBarAction()));
			viewMenu.add(new JCheckBoxMenuItem(getShowStatusBarAction()));
			if (mode == SignalMLOperationMode.APPLICATION) {
				viewMenu.add(new JCheckBoxMenuItem(getShowLeftPanelAction()));
				viewMenu.add(new JCheckBoxMenuItem(getShowBottomPanelAction()));
			}
			viewMenu.addSeparator();
			viewMenu.add(new JCheckBoxMenuItem(getViewModeAction()));
		}
		return viewMenu;
	}

	public JMenu getMonitorMenu() {
		if (monitorMenu == null) {
			monitorMenu = new JMenu(messageSource.getMessage("menu.monitor"));
			monitorMenu.add(getOpenMonitorAction());
                        monitorMenu.add(getCheckSignalAction());
			monitorMenu.addSeparator();
			monitorMenu.add(getStartMonitorRecordingAction());
			monitorMenu.add(getStopMonitorRecordingAction());
		}
		return monitorMenu;
	}

	public JMenu getToolsMenu() {
		if (toolsMenu == null) {
			toolsMenu = new JMenu(messageSource.getMessage("menu.tools"));

			for (AbstractSignalMLAction action : getRunMethodActions()) {
				toolsMenu.add(action);
			}

			if (mode == SignalMLOperationMode.APPLICATION) {

				toolsMenu.addSeparator();

				JMenu iterationMenu = new JMenu(messageSource.getMessage("menu.tools.iterate"));
				iterationMenu.setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/iteratemethod.png"));
				for (AbstractSignalMLAction action : getIterateMethodActions()) {
					iterationMenu.add(action);
				}
				iterationMenu.setEnabled(!getIterateMethodActions().isEmpty());

				toolsMenu.add(iterationMenu);

			}
		}
		return toolsMenu;
	}

	public JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu(messageSource.getMessage("menu.help"));
			helpMenu.add(getHelpContentsAction());
		}
		return helpMenu;
	}

	public JMenuBar getMenuBar() {
		if (menuBar == null) {
			menuBar = new JMenuBar();

			menuBar.add(getFileMenu());
			menuBar.add(getEditMenu());
			menuBar.add(getViewMenu());
			menuBar.add(getMonitorMenu());
			menuBar.add(getToolsMenu());
			menuBar.add(getHelpMenu());
		}
		return menuBar;
	}

	public JToolBar getMainToolBar() {
		if (mainToolBar == null) {
			mainToolBar = new JToolBar();

			mainToolBar.setFloatable(false);

			mainToolBar.add(getOpenDocumentAction());
			mainToolBar.add(getSaveActiveDocumentAction());
			mainToolBar.add(getSaveActiveDocumentAsAction());
			mainToolBar.add(getSaveAllDocumentsAction());
			mainToolBar.add(getCloseActiveDocumentAction());

			mainToolBar.add(Box.createHorizontalGlue());

			if (mode == SignalMLOperationMode.APPLICATION) {
				mainToolBar.add(getCloseWindowAction());
			}
		}
		return mainToolBar;
	}

	public LockableJSplitPane getVerticalSplitPane() {
		if (verticalSplitPane == null) {
			verticalSplitPane = new LockableJSplitPane(JSplitPane.VERTICAL_SPLIT);
			verticalSplitPane.setOneTouchExpandable(true);
			verticalSplitPane.setTopComponent(getHorizontalSplitPane());
			verticalSplitPane.setBottomComponent(getBottomPane());
			verticalSplitPane.setResizeWeight(1);
		}
		return verticalSplitPane;
	}

	public LockableJSplitPane getHorizontalSplitPane() {
		if (horizontalSplitPane == null) {
			horizontalSplitPane = new LockableJSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			horizontalSplitPane.setOneTouchExpandable(true);
			horizontalSplitPane.setLeftComponent(getLeftPane());
			horizontalSplitPane.setRightComponent(getRightPane());
			horizontalSplitPane.setResizeWeight(0);
		}
		return horizontalSplitPane;
	}

	public JPanel getLeftPane() {
		if (leftPane == null) {
			leftPane = new JPanel();

			leftPane.setLayout(new BorderLayout());
			leftPane.add(getTreeTabbedPane(), BorderLayout.CENTER);
		}
		return leftPane;
	}

	public ViewerTabbedPane getTreeTabbedPane() {
		if (treeTabbedPane == null) {
			treeTabbedPane = new ViewerTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

			treeTabbedPane.addTab("viewer.tagTabTitle", null, getTagTreePane(), "viewer.tagTabToolTip", messageSource);
			treeTabbedPane.addTab("viewer.signalTabTitle", null, getSignalTreePane(), "viewer.signalTabToolTip", messageSource);
			treeTabbedPane.addTab("viewer.monitorTabTitle", null, getMonitorTreePane(), "viewer.monitorTabToolTip", messageSource);
			treeTabbedPane.addTab("viewer.bookTabTitle", null, getBookTreePane(), "viewer.bookTabToolTip", messageSource);
			treeTabbedPane.addTab("viewer.workspaceTabTitle", null, getWorkspaceTreePane(), "viewer.workspaceTabToolTip", messageSource);

			treeTabbedPane.setSelectedIndex(3);

			treeTabbedPane.addChangeListener(getPropertySheetModel());
		}
		return treeTabbedPane;
	}

	public JPanel getRightPane() {
		if (rightPane == null) {
			rightPane = new JPanel();

			rightPane.setLayout(new BorderLayout());
			rightPane.add(getDocumentTabbedPane(), BorderLayout.CENTER);
		}
		return rightPane;
	}

	public JPanel getBottomPane() {
		if (bottomPane == null) {
			bottomPane = new JPanel();

			bottomPane.setLayout(new BorderLayout());
			bottomPane.add(getPropertyTabbedPane(), BorderLayout.CENTER);
		}
		return bottomPane;
	}

	public ViewerTabbedPane getPropertyTabbedPane() {
		if (propertyTabbedPane == null) {
			propertyTabbedPane = new ViewerTabbedPane();

			propertyTabbedPane.addTab("viewer.consoleTabTitle", null, getConsole(), "viewer.consoleTabToolTip", messageSource);
			propertyTabbedPane.addTab("viewer.taskTableTabTitle", null, getTaskTablePane(), "viewer.taskTableTabToolTip", messageSource);
			propertyTabbedPane.addTab("viewer.propertySheetTabTitle", null, getPropertySheetPane(), "viewer.propertySheetTabToolTip", messageSource);
		}
		return propertyTabbedPane;
	}

	public ViewerTreePane getWorkspaceTreePane() {
		if (workspaceTreePane == null) {
			workspaceTreePane = new ViewerTreePane(getWorkspaceTree());
		}
		return workspaceTreePane;
	}

	public ViewerTreePane getBookTreePane() {
		if (bookTreePane == null) {
			bookTreePane = new ViewerTreePane(getBookTree());
		}
		return bookTreePane;
	}

	public ViewerTreePane getSignalTreePane() {
		if (signalTreePane == null) {
			signalTreePane = new ViewerTreePane(getSignalTree());
		}
		return signalTreePane;
	}

	public ViewerTreePane getMonitorTreePane() {
		if (monitorTreePane == null) {
			monitorTreePane = new ViewerTreePane(getSignalTree());
		}
		return monitorTreePane;
	}

	public ViewerTreePane getTagTreePane() {
		if (tagTreePane == null) {
			tagTreePane = new ViewerTreePane(getTagTree());
		}
		return tagTreePane;
	}

	public ViewerWorkspaceTree getWorkspaceTree() {
		if (workspaceTree == null) {
			workspaceTree = new ViewerWorkspaceTree(getWorkspaceTreeModel(), messageSource);
			workspaceTree.setActionFocusManager(getActionFocusManager());
			workspaceTree.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			workspaceTree.setOpenDocumentDialog(getOpenDocumentDialog());
			workspaceTree.addTreeSelectionListener(getPropertySheetModel());
		}
		return workspaceTree;
	}

	public ViewerSignalTree getSignalTree() {
		if (signalTree == null) {
			signalTree = new ViewerSignalTree(getSignalTreeModel(), messageSource);
			signalTree.setActionFocusManager(getActionFocusManager());
			signalTree.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			signalTree.addTreeSelectionListener(getPropertySheetModel());
		}
		return signalTree;
	}

	public ViewerMonitorTree getMonitorTree() {
		if (monitorTree == null) {
			monitorTree = new ViewerMonitorTree(getMonitorTreeModel(), messageSource);
			monitorTree.setActionFocusManager(getActionFocusManager());
			monitorTree.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			monitorTree.addTreeSelectionListener(getPropertySheetModel());
		}
		return monitorTree;
	}

	public ViewerBookTree getBookTree() {
		if (bookTree == null) {
			bookTree = new ViewerBookTree(getBookTreeModel(), messageSource);
			bookTree.setActionFocusManager(getActionFocusManager());
			bookTree.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			bookTree.addTreeSelectionListener(getPropertySheetModel());
		}
		return bookTree;
	}

	public ViewerTagTree getTagTree() {
		if (tagTree == null) {
			tagTree = new ViewerTagTree(getTagTreeModel(), messageSource);
			tagTree.setActionFocusManager(getActionFocusManager());
			tagTree.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			tagTree.setEditTagAnnotationDialog(getEditTagAnnotationDialog());
			tagTree.setEditTagDescriptionDialog(getEditTagDescriptionDialog());
			tagTree.setTagStylePaletteDialog(getTagStylePaletteDialog());
			tagTree.addTreeSelectionListener(getPropertySheetModel());
		}
		return tagTree;
	}

	public ViewerTaskTable getTaskTable() {
		if (taskTable == null) {
			taskTable = new ViewerTaskTable(getTaskTableModel(), messageSource);
			taskTable.setActionFocusManager(getActionFocusManager());
			taskTable.setTaskManager(getTaskManager());
			taskTable.setMethodManager(getMethodManager());
			taskTable.setErrorsDialog(getErrorsDialog());

			taskTable.setAbortAllTasksAction(getAbortAllTasksAction());
			taskTable.setSuspendAllTasksAction(getSuspendAllTasksAction());
			taskTable.setResumeAllTasksAction(getResumeAllTasksAction());
			taskTable.setRemoveAllTasksAction(getRemoveAllTasksAction());
			taskTable.setRemoveAllFinishedTasksAction(getRemoveAllFinishedTasksAction());
			taskTable.setRemoveAllAbortedTasksAction(getRemoveAllAbortedTasksAction());
			taskTable.setRemoveAllFailedTasksAction(getRemoveAllFailedTasksAction());
		}
		return taskTable;
	}

	public ViewerConsolePane getConsole() {
		if (console == null) {
			console = new ViewerConsolePane();
			console.setMessageSource(messageSource);
			console.setFileChooser(getFileChooser());
			console.initialize();
		}
		return console;
	}

	public ViewerTaskTablePane getTaskTablePane() {
		if (taskTablePane == null) {
			taskTablePane = new ViewerTaskTablePane(getTaskTable());
			taskTablePane.initialize();
		}
		return taskTablePane;
	}

	public ViewerPropertySheetPane getPropertySheetPane() {
		if (propertySheetPane == null) {
			propertySheetPane = new ViewerPropertySheetPane();
			propertySheetPane.setPropertySheetModel(getPropertySheetModel());
			propertySheetPane.initialize();
		}
		return propertySheetPane;
	}

	public ViewerDocumentTabbedPane getDocumentTabbedPane() {
		if (documentTabbedPane == null) {
			documentTabbedPane = new ViewerDocumentTabbedPane();
			documentTabbedPane.setMessageSource(messageSource);
			documentTabbedPane.setActionFocusManager(getActionFocusManager());
			documentTabbedPane.setView(getView());
			documentTabbedPane.initialize();
		}
		return documentTabbedPane;
	}

	public ErrorsDialog getErrorsDialog() {
		if (errorsDialog == null) {
			errorsDialog = new ErrorsDialog(messageSource, getDialogParent(), true);
		}
		return errorsDialog;
	}

	public PleaseWaitDialog getPleaseWaitDialog() {
		if (pleaseWaitDialog == null) {
			pleaseWaitDialog = new PleaseWaitDialog(messageSource, getDialogParent());
			pleaseWaitDialog.initializeNow();
		}
		return pleaseWaitDialog;
	}

	public SeriousWarningDialog getSeriousWarningDialog() {
		if (seriousWarningDialog == null) {
			seriousWarningDialog = new SeriousWarningDialog(messageSource, getDialogParent(), true);
			seriousWarningDialog.setApplicationConfig(getApplicationConfig());
		}
		return seriousWarningDialog;
	}

	public ApplicationPreferencesDialog getApplicationPreferencesDialog() {
		if (applicationPreferencesDialog == null) {
			applicationPreferencesDialog = new ApplicationPreferencesDialog(messageSource, mode, getDialogParent(), true);
			applicationPreferencesDialog.setProfileDir(getProfileDir());
			applicationPreferencesDialog.setFileChooser(getFileChooser());
			if (mode == SignalMLOperationMode.APPLICATION) {
				applicationPreferencesDialog.setCodecManager(getCodecManager());
				applicationPreferencesDialog.setMp5ExecutorManager(getMp5ExecutorManager());
			}
		}
		return applicationPreferencesDialog;
	}

	public OpenDocumentDialog getOpenDocumentDialog() {
		if (openDocumentDialog == null) {
			openDocumentDialog = new OpenDocumentDialog(messageSource, getDialogParent(), true);
			openDocumentDialog.setCodecManager(getCodecManager());
			openDocumentDialog.setDocumentDetector(getDocumentDetector());
			openDocumentDialog.setDocumentManager(getDocumentManager());
			openDocumentDialog.setApplicationConfig(getApplicationConfig());
			openDocumentDialog.setFileChooser(getFileChooser());
		}
		return openDocumentDialog;
	}

	public OpenMonitorDialog getOpenMonitorDialog() {
		if (openMonitorDialog == null) {
			openMonitorDialog = new OpenMonitorDialog(messageSource, this, getDialogParent(), true);
			openMonitorDialog.setApplicationConfig(getApplicationConfig());
		}
		return openMonitorDialog;
	}

        public CheckSignalDialog getCheckSignalDialog() {
		if (checkSignalDialog == null) {
			checkSignalDialog = new CheckSignalDialog(messageSource, getDialogParent(), true);
		}
		return checkSignalDialog;
	}


	public RegisterCodecDialog getRegisterCodecDialog() {
		if (registerCodecDialog == null) {
			registerCodecDialog = new RegisterCodecDialog(messageSource, getDialogParent(), true);
			registerCodecDialog.setCodecManager(getCodecManager());
			registerCodecDialog.setProfileDir(getProfileDir());
		}
		return registerCodecDialog;
	}

	public SignalParametersDialog getSignalParametersDialog() {
		if (signalParametersDialog == null) {
			signalParametersDialog = new SignalParametersDialog(messageSource, getDialogParent(), true);
		}
		return signalParametersDialog;
	}

	public SignalMontageDialog getSignalMontageDialog() {
		if (signalMontageDialog == null) {
			signalMontageDialog = new SignalMontageDialog(messageSource, getMontagePresetManager(), getDialogParent(), true);
			signalMontageDialog.setFileChooser(getFileChooser());
			signalMontageDialog.setApplicationConfig(getApplicationConfig());
			signalMontageDialog.setFftFilterPresetManager(getFftFilterPresetManager());
			signalMontageDialog.setTimeDomainSampleFilterPresetManager(getTimeDomainSampleFilterPresetManager());
		}
		return signalMontageDialog;
	}

	public SignalSelectionDialog getSignalSelectionDialog() {
		if (signalSelectionDialog == null) {
			signalSelectionDialog = new SignalSelectionDialog(messageSource, getDialogParent(), true);
		}
		return signalSelectionDialog;
	}

	public NewTagDialog getNewTagDialog() {
		if (newTagDialog == null) {
			newTagDialog = new NewTagDialog(messageSource, getDialogParent(), true);
			newTagDialog.setApplicationConfig(getApplicationConfig());
		}
		return newTagDialog;
	}

	public EditTagAnnotationDialog getEditTagAnnotationDialog() {
		if (editTagAnnotationDialog == null) {
			editTagAnnotationDialog = new EditTagAnnotationDialog(messageSource, getDialogParent(), true);
		}
		return editTagAnnotationDialog;
	}

	public SlavePlotSettingsPopupDialog getSlavePlotSettingsPopupDialog() {
		if (slavePlotSettingsPopupDialog == null) {
			slavePlotSettingsPopupDialog = new SlavePlotSettingsPopupDialog(messageSource, getDialogParent(), true);
			// XXX this dialog reuses the main window's instance of the montage dialog
			// this seems to work and since the dialog is very big we try to keep it
			// like this if it works
			slavePlotSettingsPopupDialog.setSignalMontageDialog(getSignalMontageDialog());
		}
		return slavePlotSettingsPopupDialog;
	}

	public TagStylePaletteDialog getTagStylePaletteDialog() {
		if (tagStylePaletteDialog == null) {
			tagStylePaletteDialog = new TagStylePaletteDialog(messageSource, getDialogParent(), true);
		}
		return tagStylePaletteDialog;
	}

	public HelpDialog getHelpDialog() {
		if (helpDialog == null) {
			helpDialog = new HelpDialog(messageSource, null, false);
		}
		return helpDialog;
	}

	public TagComparisonDialog getTagComparisonDialog() {
		if (tagComparisonDialog == null) {
			tagComparisonDialog = new TagComparisonDialog(messageSource, getDialogParent(), true);
			tagComparisonDialog.setTableToTextExporter(getTableToTextExporter());
			tagComparisonDialog.setFileChooser(getFileChooser());
		}
		return tagComparisonDialog;
	}

	public EditTagDescriptionDialog getEditTagDescriptionDialog() {
		if (editTagDescriptionDialog == null) {
			editTagDescriptionDialog = new EditTagDescriptionDialog(messageSource, getDialogParent(), true);
		}
		return editTagDescriptionDialog;
	}

	public IterationSetupDialog getIterationSetupDialog() {
		if (iterationSetupDialog == null) {
			iterationSetupDialog = new IterationSetupDialog(messageSource, getDialogParent(), true);
			iterationSetupDialog.setMethodManager(getMethodManager());
		}
		return iterationSetupDialog;
	}

	public ExportSignalDialog getExportSignalDialog() {
		if (exportSignalDialog == null) {
			exportSignalDialog = new ExportSignalDialog(messageSource, getSignalExportPresetManager(), getDialogParent(), true);
			exportSignalDialog.setApplicationConfig(getApplicationConfig());
		}
		return exportSignalDialog;
	}

	public EditFFTSampleFilterDialog getEditFFTSampleFilterDialog() {
		if (editFFTSampleFilterDialog == null) {
			editFFTSampleFilterDialog = new EditFFTSampleFilterDialog(messageSource, getFftFilterPresetManager(), getDialogParent(), true);
			editFFTSampleFilterDialog.setApplicationConfig(getApplicationConfig());
			editFFTSampleFilterDialog.setFileChooser(getFileChooser());
		}
		return editFFTSampleFilterDialog;
	}

	public EditTimeDomainSampleFilterDialog getEditTimeDomainSampleFilterDialog() {
		if (editTimeDomainSampleFilterDialog == null) {
			editTimeDomainSampleFilterDialog = new EditTimeDomainSampleFilterDialog(messageSource, getTimeDomainSampleFilterPresetManager(), getDialogParent(), true);
			editTimeDomainSampleFilterDialog.setApplicationConfig(getApplicationConfig());
			editTimeDomainSampleFilterDialog.setFileChooser(getFileChooser());
		}
		return editTimeDomainSampleFilterDialog;
	}

	public StartMonitorRecordingDialog getStartMonitorRecordingDialog() {
		if (startMonitorRecordingDialog == null) {
			startMonitorRecordingDialog = new StartMonitorRecordingDialog(messageSource, getDialogParent(), true);
		}
		return startMonitorRecordingDialog;
	}

	public MP5LocalExecutorDialog getMp5LocalExecutorDialog() {
		if (mp5LocalExecutorDialog == null) {
			mp5LocalExecutorDialog = new MP5LocalExecutorDialog(messageSource, getDialogParent(), true);
			mp5LocalExecutorDialog.setFileChooser(getFileChooser());
		}
		return mp5LocalExecutorDialog;
	}

	public MP5RemoteExecutorDialog getMp5RemoteExecutorDialog() {
		if (mp5RemoteExecutorDialog == null) {
			mp5RemoteExecutorDialog = new MP5RemoteExecutorDialog(messageSource, getDialogParent(), true);
		}
		return mp5RemoteExecutorDialog;
	}

	public DynamicCompilationWarningDialog getDynamicCompilationWarningDialog() {
		if (dynamicCompilationWarningDialog == null) {
			dynamicCompilationWarningDialog = new DynamicCompilationWarningDialog(messageSource, getDialogParent(), true);
			if (mode == SignalMLOperationMode.APPLICATION) {
				dynamicCompilationWarningDialog.setApplicationConfig(getApplicationConfig());
			} else {
				dynamicCompilationWarningDialog.setPreferences(getPreferences());
			}
		}
		return dynamicCompilationWarningDialog;
	}

	public AtomTableDialog getAtomTableDialog() {
		if (atomTableDialog == null) {
			atomTableDialog = new AtomTableDialog(messageSource, getDialogParent(), true);
		}
		return atomTableDialog;
	}

	public BookFilterDialog getBookFilterDialog() {
		if (bookFilterDialog == null) {
			bookFilterDialog = new BookFilterDialog(messageSource, getBookFilterPresetManager(), getDialogParent(), true);
			bookFilterDialog.setApplicationConfig(getApplicationConfig());
			bookFilterDialog.setFileChooser(getFileChooser());
		}
		return bookFilterDialog;
	}

	public ViewerFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new ViewerFileChooser();
			fileChooser.setMessageSource(messageSource);
			fileChooser.setApplicationConfig(getApplicationConfig());
			fileChooser.initialize();
		}
		return fileChooser;
	}

	public CloseWindowAction getCloseWindowAction() {
		if (closeWindowAction == null) {
			closeWindowAction = new CloseWindowAction(messageSource);
		}
		return closeWindowAction;
	}

	public EditPreferencesAction getEditPreferencesAction() {
		if (editPreferencesAction == null) {
			editPreferencesAction = new EditPreferencesAction(messageSource);
			editPreferencesAction.setPreferencesDialog(getApplicationPreferencesDialog());
			editPreferencesAction.setConfig(getApplicationConfig());
		}
		return editPreferencesAction;
	}

	public HelpContentsAction getHelpContentsAction() {
		if (helpContentsAction == null) {
			helpContentsAction = new HelpContentsAction(messageSource);
			helpContentsAction.setHelpDialog(getHelpDialog());
		}
		return helpContentsAction;
	}

	public ViewModeAction getViewModeAction() {
		if (viewModeAction == null) {
			viewModeAction = new ViewModeAction(messageSource);
		}
		return viewModeAction;
	}

	public ShowMainToolBarAction getShowMainToolBarAction() {
		if (showMainToolBarAction == null) {
			showMainToolBarAction = new ShowMainToolBarAction(messageSource);
		}
		return showMainToolBarAction;
	}

	public ShowStatusBarAction getShowStatusBarAction() {
		if (showStatusBarAction == null) {
			showStatusBarAction = new ShowStatusBarAction(messageSource);
		}
		return showStatusBarAction;
	}

	public ShowLeftPanelAction getShowLeftPanelAction() {
		if (showLeftPanelAction == null) {
			showLeftPanelAction = new ShowLeftPanelAction(messageSource);
		}
		return showLeftPanelAction;
	}

	public ShowBottomPanelAction getShowBottomPanelAction() {
		if (showBottomPanelAction == null) {
			showBottomPanelAction = new ShowBottomPanelAction(messageSource);
		}
		return showBottomPanelAction;
	}

	public OpenDocumentAction getOpenDocumentAction() {
		if (openDocumentAction == null) {
			openDocumentAction = new OpenDocumentAction(messageSource);
			openDocumentAction.setOpenDocumentDialog(getOpenDocumentDialog());
			openDocumentAction.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
		}
		return openDocumentAction;
	}

	public OpenMonitorAction getOpenMonitorAction() {
		if (openMonitorAction == null) {
			openMonitorAction = new OpenMonitorAction(this);
			openMonitorAction.setOpenMonitorDialog(getOpenMonitorDialog());
		}
		return openMonitorAction;
	}

        public CheckSignalAction getCheckSignalAction() {
		if (checkSignalAction == null) {
			checkSignalAction = new CheckSignalAction(messageSource, getActionFocusManager());
			checkSignalAction.setCheckSignalDialog(getCheckSignalDialog());
		}
		return checkSignalAction;
        }

	public StartMonitorRecordingAction getStartMonitorRecordingAction() {
		if (startMonitorRecordingAction == null) {
			startMonitorRecordingAction = new StartMonitorRecordingAction(messageSource, getActionFocusManager());
			startMonitorRecordingAction.setStartMonitorRecordingDialog(getStartMonitorRecordingDialog());
		}
		return startMonitorRecordingAction;
	}

	public StopMonitorRecordingAction getStopMonitorRecordingAction() {
		if (stopMonitorRecordingAction == null)
			stopMonitorRecordingAction = new StopMonitorRecordingAction(messageSource, getActionFocusManager());
		return stopMonitorRecordingAction;
	}


	public CloseDocumentAction getCloseActiveDocumentAction() {
		if (closeActiveDocumentAction == null) {
			closeActiveDocumentAction = new CloseDocumentAction(messageSource, getActionFocusManager());
			closeActiveDocumentAction.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
		}
		return closeActiveDocumentAction;
	}

	public SaveAllDocumentsAction getSaveAllDocumentsAction() {
		if (saveAllDocumentsAction == null) {
			saveAllDocumentsAction = new SaveAllDocumentsAction(messageSource);
			saveAllDocumentsAction.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
		}
		return saveAllDocumentsAction;
	}

	public SaveDocumentAction getSaveActiveDocumentAction() {
		if (saveActiveDocumentAction == null) {
			saveActiveDocumentAction = new SaveDocumentAction(messageSource, getActionFocusManager());
			saveActiveDocumentAction.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
		}
		return saveActiveDocumentAction;
	}

	public SaveDocumentAsAction getSaveActiveDocumentAsAction() {
		if (saveActiveDocumentAsAction == null) {
			saveActiveDocumentAsAction = new SaveDocumentAsAction(messageSource, getActionFocusManager());
			saveActiveDocumentAsAction.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
		}
		return saveActiveDocumentAsAction;
	}

	public NewTagAction getNewTagAction() {
		if (newTagAction == null) {
			newTagAction = new NewTagAction(messageSource, getActionFocusManager());
			newTagAction.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			newTagAction.setNewTagDialog(getNewTagDialog());
		}
		return newTagAction;
	}

	public OpenTagAction getOpenTagAction() {
		if (openTagAction == null) {
			openTagAction = new OpenTagAction(messageSource, getActionFocusManager());
			openTagAction.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			openTagAction.setFileChooser(getFileChooser());
			openTagAction.setOptionPaneParent(getOptionPaneParent());
		}
		return openTagAction;
	}

	public CloseTagAction getCloseTagAction() {
		if (closeTagAction == null) {
			closeTagAction = new CloseTagAction(messageSource, getActionFocusManager());
			closeTagAction.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
		}
		return closeTagAction;
	}

	public SaveTagAction getSaveTagAction() {
		if (saveTagAction == null) {
			saveTagAction = new SaveTagAction(messageSource, getActionFocusManager());
			saveTagAction.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
		}
		return saveTagAction;
	}

	public SaveTagAsAction getSaveTagAsAction() {
		if (saveTagAsAction == null) {
			saveTagAsAction = new SaveTagAsAction(messageSource, getActionFocusManager());
			saveTagAsAction.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
		}
		return saveTagAsAction;
	}

	public OpenTagAction getImportTagAction() {
		if (importTagAction == null) {
			importTagAction = new OpenTagAction(messageSource, getActionFocusManager());
			importTagAction.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			importTagAction.setFileChooser(getFileChooser());
			importTagAction.setOptionPaneParent(getOptionPaneParent());
		}
		return importTagAction;
	}

	public ExportTagAction getExportTagAction() {
		if (exportTagAction == null) {
			exportTagAction = new ExportTagAction(messageSource, getActionFocusManager());
			exportTagAction.setFileChooser(getFileChooser());
			exportTagAction.setOptionPaneParent(getOptionPaneParent());
		}
		return exportTagAction;
	}

	public EditSignalParametersAction getEditSignalParametersAction() {
		if (editSignalMontageAction == null) {
			editSignalParametersAction = new EditSignalParametersAction(messageSource, getActionFocusManager());
			editSignalParametersAction.setSignalParametersDialog(getSignalParametersDialog());
		}
		return editSignalParametersAction;
	}

	public EditSignalMontageAction getEditSignalMontageAction() {
		if (editSignalMontageAction == null) {
			editSignalMontageAction = new EditSignalMontageAction(messageSource, getActionFocusManager());
			editSignalMontageAction.setSignalMontageDialog(getSignalMontageDialog());
		}
		return editSignalMontageAction;
	}

	public EditStoredMontagesAction getEditStoredMontagesAction() {
		if (editStoredMontagesAction == null) {
			editStoredMontagesAction = new EditStoredMontagesAction(messageSource);
			editStoredMontagesAction.setSignalMontageDialog(getSignalMontageDialog());
		}
		return editStoredMontagesAction;
	}

	public ApplyDefaultMontageAction getApplyDefaultMontageAction() {
		if (applyDefaultMontageAction == null) {
			applyDefaultMontageAction = new ApplyDefaultMontageAction(messageSource, getActionFocusManager());
		}
		return applyDefaultMontageAction;
	}

	public PreciseSelectionAction getPreciseSelectionAction() {
		if (preciseSelectionAction == null) {
			preciseSelectionAction = new PreciseSelectionAction(messageSource, getActionFocusManager());
			preciseSelectionAction.setSignalSelectionDialog(getSignalSelectionDialog());
		}
		return preciseSelectionAction;
	}

	public EditTagStylesAction getEditTagStylesAction() {
		if (editTagStylesAction == null) {
			editTagStylesAction = new EditTagStylesAction(messageSource, getActionFocusManager());
			editTagStylesAction.setTagStylePaletteDialog(getTagStylePaletteDialog());
		}
		return editTagStylesAction;
	}

	public EditTagDescriptionAction getEditTagDescriptionAction() {
		if (editTagDescriptionAction == null) {
			editTagDescriptionAction = new EditTagDescriptionAction(messageSource, getActionFocusManager());
			editTagDescriptionAction.setEditTagDescriptionDialog(getEditTagDescriptionDialog());
		}
		return editTagDescriptionAction;
	}

	public ExportSignalAction getExportSignalAction() {
		if (exportSignalAction == null) {
			exportSignalAction = new ExportSignalAction(messageSource, getActionFocusManager());
			exportSignalAction.setExportSignalDialog(getExportSignalDialog());
			exportSignalAction.setPleaseWaitDialog(getPleaseWaitDialog());
			exportSignalAction.setFileChooser(getFileChooser());
			exportSignalAction.setOptionPaneParent(getOptionPaneParent());
		}
		return exportSignalAction;
	}

	public ExportBookAction getExportBookAction() {
		if (exportBookAction == null) {
			exportBookAction = new ExportBookAction(messageSource, getActionFocusManager());
			exportBookAction.setPleaseWaitDialog(getPleaseWaitDialog());
			exportBookAction.setFileChooser(getFileChooser());
			exportBookAction.setOptionPaneParent(getOptionPaneParent());
		}
		return exportBookAction;
	}

	public AbortAllTasksAction getAbortAllTasksAction() {
		if (abortAllTasksAction == null) {
			abortAllTasksAction = new AbortAllTasksAction(messageSource);
			abortAllTasksAction.setTaskManager(getTaskManager());
			abortAllTasksAction.setOptionPaneParent(getOptionPaneParent());
		}
		return abortAllTasksAction;
	}

	public SuspendAllTasksAction getSuspendAllTasksAction() {
		if (suspendAllTasksAction == null) {
			suspendAllTasksAction = new SuspendAllTasksAction(messageSource);
			suspendAllTasksAction.setTaskManager(getTaskManager());
			suspendAllTasksAction.setOptionPaneParent(getOptionPaneParent());
		}
		return suspendAllTasksAction;
	}

	public ResumeAllTasksAction getResumeAllTasksAction() {
		if (resumeAllTasksAction == null) {
			resumeAllTasksAction = new ResumeAllTasksAction(messageSource);
			resumeAllTasksAction.setTaskManager(getTaskManager());
			resumeAllTasksAction.setOptionPaneParent(getOptionPaneParent());
		}
		return resumeAllTasksAction;
	}

	public RemoveAllTasksAction getRemoveAllTasksAction() {
		if (removeAllTasksAction == null) {
			removeAllTasksAction = new RemoveAllTasksAction(messageSource);
			removeAllTasksAction.setTaskManager(getTaskManager());
			removeAllTasksAction.setOptionPaneParent(getOptionPaneParent());
		}
		return removeAllTasksAction;
	}

	public RemoveAllFinishedTasksAction getRemoveAllFinishedTasksAction() {
		if (removeAllFinishedTasksAction == null) {
			removeAllFinishedTasksAction = new RemoveAllFinishedTasksAction(messageSource);
			removeAllFinishedTasksAction.setTaskManager(getTaskManager());
			removeAllFinishedTasksAction.setOptionPaneParent(getOptionPaneParent());
		}
		return removeAllFinishedTasksAction;
	}

	public RemoveAllAbortedTasksAction getRemoveAllAbortedTasksAction() {
		if (removeAllAbortedTasksAction == null) {
			removeAllAbortedTasksAction = new RemoveAllAbortedTasksAction(messageSource);
			removeAllAbortedTasksAction.setTaskManager(getTaskManager());
			removeAllAbortedTasksAction.setOptionPaneParent(getOptionPaneParent());
		}
		return removeAllAbortedTasksAction;
	}

	public RemoveAllFailedTasksAction getRemoveAllFailedTasksAction() {
		if (removeAllFailedTasksAction == null) {
			removeAllFailedTasksAction = new RemoveAllFailedTasksAction(messageSource);
			removeAllFailedTasksAction.setTaskManager(getTaskManager());
			removeAllFailedTasksAction.setOptionPaneParent(getOptionPaneParent());
		}
		return removeAllFailedTasksAction;
	}

	public ArrayList<AbstractSignalMLAction> getRunMethodActions() {
		if (runMethodActions == null) {
			Method[] methods = getMethodManager().getMethods();
			runMethodActions = new ArrayList<AbstractSignalMLAction>(methods.length);
			RunMethodAction runMethodAction;
			for (Method method : methods) {

				runMethodAction = new RunMethodAction(messageSource, method, getMethodManager());
				runMethodAction.setTaskManager(getTaskManager());
				runMethodActions.add(runMethodAction);

			}

			int unavailableMethodCount = methodManager.getUnavailableMethodCount();
			UnavailableMethodAction unavailableMethodAction;
			for (int i=0; i<unavailableMethodCount; i++) {

				unavailableMethodAction = new UnavailableMethodAction(messageSource, methodManager.getUnavailableMethodAt(i));
				unavailableMethodAction.setErrorsDialog(getErrorsDialog());
				runMethodActions.add(unavailableMethodAction);

			}

		}
		return runMethodActions;
	}

	public ArrayList<AbstractSignalMLAction> getIterateMethodActions() {
		if (iterateMethodActions == null) {
			Method[] methods = getMethodManager().getMethods();
			iterateMethodActions = new ArrayList<AbstractSignalMLAction>(methods.length);
			IterateMethodAction iterateMethodAction;
			for (Method method : methods) {

				if (method instanceof IterableMethod) {
					iterateMethodAction = new IterateMethodAction(messageSource, (IterableMethod) method, getMethodManager());
					iterateMethodAction.setTaskManager(getTaskManager());
					iterateMethodAction.setIterationSetupDialog(getIterationSetupDialog());
					iterateMethodActions.add(iterateMethodAction);
				}

			}
		}
		return iterateMethodActions;
	}

	public TableToTextExporter getTableToTextExporter() {
		if (tableToTextExporter == null) {
			tableToTextExporter = new TableToTextExporter();
		}
		return tableToTextExporter;
	}

	public MP5ApplicationExecutorConfigurer getMp5ExecutorConfigurer() {
		if (mp5ExecutorConfigurer == null) {
			mp5ExecutorConfigurer = new MP5ApplicationExecutorConfigurer();
			mp5ExecutorConfigurer.setRemoteExecutorDialog(getMp5RemoteExecutorDialog());
		}
		return mp5ExecutorConfigurer;
	}

	public JmxClient getJmxClient() {
		return jmxClient;
	}

	public void setJmxClient(JmxClient jmxClient) {
		this.jmxClient = jmxClient;
	}

	public JmxClient getTagClient() {
		return tagClient;
	}

	public void setTagClient(JmxClient tagClient) {
		this.tagClient = tagClient;
	}

	public DocumentView createDocumentViewPanel(Document document) throws SignalMLException {

		DocumentView documentView = null;

		if (document instanceof SignalDocument) {

			SignalView signalView;
			signalView = new SignalView((SignalDocument) document);

			signalView.setMessageSource(messageSource);
			signalView.setActionFocusManager(getActionFocusManager());
			signalView.setSlavePlotSettingsPopupDialog(getSlavePlotSettingsPopupDialog());
			signalView.setErrorsDialog(getErrorsDialog());
			signalView.setTagComparisonDialog(getTagComparisonDialog());
			signalView.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			signalView.setMontagePresetManager(getMontagePresetManager());
			signalView.setSignalMontageDialog(getSignalMontageDialog());
			signalView.setStartMonitorRecordingDialog(getStartMonitorRecordingDialog());
			signalView.setSignalParametersDialog(getSignalParametersDialog());
			signalView.setSignalSelectionDialog(getSignalSelectionDialog());
			signalView.setTagStylePaletteDialog(getTagStylePaletteDialog());
			signalView.setNewTagDialog(getNewTagDialog());
			signalView.setFileChooser(getFileChooser());
			signalView.setEditTagAnnotationDialog(getEditTagAnnotationDialog());
			signalView.setEditTagDescriptionDialog(getEditTagDescriptionDialog());
			signalView.setApplicationConfig(getApplicationConfig());
			signalView.initialize();

			documentView = signalView;

		} else if (document instanceof BookDocument) {

			BookView bookView;
			bookView = new BookView((BookDocument) document);

			bookView.setMessageSource(messageSource);
			bookView.setActionFocusManager(getActionFocusManager());
			bookView.setErrorsDialog(getErrorsDialog());
			bookView.setApplicationConfig(getApplicationConfig());
			bookView.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			bookView.setFileChooser(getFileChooser());
			bookView.setAtomTableDialog(getAtomTableDialog());
			bookView.setBookFilterDialog(getBookFilterDialog());
			bookView.setPleaseWaitDialog(getPleaseWaitDialog());

			bookView.initialize();

			documentView = bookView;

		} else {
			throw new ClassCastException("Unsuported document class [" + document.getClass().toString() + "]");
		}

		return documentView;
	}

	public void configureImportedElements() {

		ApplicationMethodManager localMethodManager = getMethodManager();
		localMethodManager.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
		localMethodManager.setFileChooser(getFileChooser());
		localMethodManager.setDialogParent(getDialogParent());
		localMethodManager.setTableToTextExporter(getTableToTextExporter());

		ApplicationTaskManager localTaskManager = getTaskManager();
		localTaskManager.setErrorsDialog(getErrorsDialog());
		localTaskManager.setPleaseWaitDialog(getPleaseWaitDialog());

		DynamicCompilationContext.getSharedInstance().getCompiler().setWarning(getDynamicCompilationWarningDialog());

	}

	public void configureAcceletators() {

		// configure accelerators
		getCloseWindowAction().setAccelerator("ctrl X");
		getHelpContentsAction().setAccelerator("F1");
		getViewModeAction().setAccelerator("F11");
		getOpenDocumentAction().setAccelerator("ctrl O");
		getCloseActiveDocumentAction().setAccelerator("ctrl F4");
		getOpenTagAction().setAccelerator("alt O");
		getCloseTagAction().setAccelerator("ctrl alt F4");
		getSaveAllDocumentsAction().setAccelerator("ctrl shift S");
		getSaveActiveDocumentAction().setAccelerator("ctrl S");
		getSaveAllDocumentsAction().setAccelerator("ctrl alt S");
		getSaveTagAction().setAccelerator("alt S");
		getSaveTagAsAction().setAccelerator("ctrl alt shift S");

	}

}

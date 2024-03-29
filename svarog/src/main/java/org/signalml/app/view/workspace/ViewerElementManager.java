/* ViewerElementManager.java created 2008-02-20
 *
 */

package org.signalml.app.view.workspace;

import com.alee.laf.tabbedpane.WebTabbedPane;
import com.thoughtworks.xstream.XStream;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.signalml.SignalMLOperationMode;
import org.signalml.app.action.HelpContentsAction;
import org.signalml.app.action.RunMethodAction;
import org.signalml.app.action.book.ExportBookAction;
import org.signalml.app.action.book.OpenBookDocumentAction;
import org.signalml.app.action.components.CloseWindowAction;
import org.signalml.app.action.document.CloseDocumentAction;
import org.signalml.app.action.document.OpenSignalWizardAction;
import org.signalml.app.action.document.monitor.CheckSignalAction;
import org.signalml.app.action.document.monitor.StartMonitorRecordingAction;
import org.signalml.app.action.document.monitor.StopMonitorRecordingAction;
import org.signalml.app.action.montage.ApplyDefaultMontageAction;
import org.signalml.app.action.montage.EditSignalMontageAction;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.action.signal.BasicToolbarSwitchAction;
import org.signalml.app.action.signal.ChangeToolAction;
import org.signalml.app.action.signal.EditPlotOptionsAction;
import org.signalml.app.action.signal.EditSignalFiltersAction;
import org.signalml.app.action.signal.EditSignalParametersAction;
import org.signalml.app.action.signal.ExportSignalAction;
import org.signalml.app.action.signal.PreciseSelectionAction;
import org.signalml.app.action.signal.RecordingToolbarSwitchAction;
import org.signalml.app.action.signal.ScaleToolbarSwitchAction;
import org.signalml.app.action.signal.SettingsToolbarSwitchAction;
import org.signalml.app.action.signal.SignalFilterSwitchAction;
import org.signalml.app.action.tag.ChooseActiveTagAction;
import org.signalml.app.action.tag.CloseTagAction;
import org.signalml.app.action.tag.CompareTagsAction;
import org.signalml.app.action.tag.EditTagDescriptionAction;
import org.signalml.app.action.tag.EditTagStylePresetsAction;
import org.signalml.app.action.tag.EditTagStylesAction;
import org.signalml.app.action.tag.ExportEEGLabTagAction;
import org.signalml.app.action.tag.NewTagAction;
import org.signalml.app.action.tag.OpenTagAction;
import org.signalml.app.action.tag.SaveTagAction;
import org.signalml.app.action.tag.SaveTagAsAction;
import org.signalml.app.action.tag.SynchronizeTagsWithTriggerAction;
import org.signalml.app.action.video.AddCameraAction;
import org.signalml.app.action.workspace.EditPreferencesAction;
import org.signalml.app.action.workspace.EditToolsAction;
import org.signalml.app.action.workspace.IterateMethodAction;
import org.signalml.app.action.workspace.ShowBottomPanelAction;
import org.signalml.app.action.workspace.ShowLeftPanelAction;
import org.signalml.app.action.workspace.ShowStatusBarAction;
import org.signalml.app.action.workspace.ViewModeAction;
import org.signalml.app.action.workspace.tasks.AbortAllTasksAction;
import org.signalml.app.action.workspace.tasks.RemoveAllAbortedTasksAction;
import org.signalml.app.action.workspace.tasks.RemoveAllFailedTasksAction;
import org.signalml.app.action.workspace.tasks.RemoveAllFinishedTasksAction;
import org.signalml.app.action.workspace.tasks.RemoveAllTasksAction;
import org.signalml.app.action.workspace.tasks.ResumeAllTasksAction;
import org.signalml.app.action.workspace.tasks.SuspendAllTasksAction;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.config.ManagerOfPresetManagers;
import org.signalml.app.document.BookDocument;
import org.signalml.app.document.DocumentDetector;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.DocumentManager;
import org.signalml.app.document.mrud.MRUDRegistry;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.iterate.IterationSetupDialog;
import org.signalml.app.method.mp5.MP5ExecutorManager;
import org.signalml.app.method.mp5.MP5LocalExecutorDialog;
import org.signalml.app.model.book.BookTreeModel;
import org.signalml.app.model.components.PropertySheetModel;
import org.signalml.app.model.components.TableToTextExporter;
import org.signalml.app.model.components.TaskTableModel;
import org.signalml.app.model.monitor.MonitorTreeModel;
import org.signalml.app.model.signal.SignalTreeModel;
import org.signalml.app.model.tag.TagTreeModel;
import org.signalml.app.model.workspace.WorkspaceTreeModel;
import org.signalml.app.task.ApplicationTaskManager;
import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._H;
import static org.signalml.app.util.i18n.SvarogI18n._R;
import org.signalml.app.video.VideoFrame;
import org.signalml.app.view.View;
import org.signalml.app.view.book.AtomTableDialog;
import org.signalml.app.view.book.BookView;
import org.signalml.app.view.book.filter.BookFilterDialog;
import org.signalml.app.view.common.components.LockableJSplitPane;
import org.signalml.app.view.common.dialogs.HelpDialog;
import org.signalml.app.view.common.dialogs.PleaseWaitDialog;
import org.signalml.app.view.document.monitor.StartMonitorRecordingDialog;
import org.signalml.app.view.document.monitor.signalchecking.CheckSignalDialog;
import org.signalml.app.view.document.opensignal.elements.SignalSourceTabbedPane;
import org.signalml.app.view.montage.SignalMontageDialog;
import org.signalml.app.view.montage.filters.EditFFTSampleFilterDialog;
import org.signalml.app.view.montage.filters.EditTimeDomainSampleFilterDialog;
import org.signalml.app.view.preferences.ApplicationPreferencesDialog;
import org.signalml.app.view.preferences.ApplicationToolsDialog;
import org.signalml.app.view.signal.MoveSignalSignalTool;
import org.signalml.app.view.signal.RulerSignalTool;
import org.signalml.app.view.signal.SelectChannelSignalTool;
import org.signalml.app.view.signal.SelectTagSignalTool;
import org.signalml.app.view.signal.SignalParametersDialog;
import org.signalml.app.view.signal.SignalSelectionDialog;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.signal.ZoomSignalTool;
import org.signalml.app.view.signal.export.ExportSignalDialog;
import org.signalml.app.view.signal.popup.ChannelOptionsPopupDialog;
import org.signalml.app.view.signal.popup.SlavePlotSettingsPopupDialog;
import org.signalml.app.view.signal.signalml.RegisterCodecDialog;
import org.signalml.app.view.tag.EditTagAnnotationDialog;
import org.signalml.app.view.tag.EditTagDescriptionDialog;
import org.signalml.app.view.tag.NewTagDialog;
import org.signalml.app.view.tag.TagStylePaletteDialog;
import org.signalml.app.view.tag.TagStylePresetDialog;
import org.signalml.app.view.tag.comparison.TagComparisonDialog;
import org.signalml.app.worker.monitor.ObciServerCapabilities;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.method.AbstractMethod;
import org.signalml.method.Method;
import org.signalml.method.bookaverage.BookAverageMethod;
import org.signalml.method.booktotag.BookToTagMethod;
import org.signalml.method.ep.EvokedPotentialMethod;
import org.signalml.method.iterator.IterableMethod;
import org.signalml.method.mp5.MP5Method;
import org.signalml.plugin.bookreporter.method.BookReporterMethod;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.signal.SignalTool;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.export.view.DocumentView;
import org.signalml.plugin.export.view.ViewerTreePane;
import org.signalml.plugin.fftsignaltool.SignalFFTTool;
import org.signalml.psychopy.action.StartMonitorRecordingPsychopyDialog;
import org.signalml.util.SvarogConstants;
import pl.edu.fuw.fid.signalanalysis.dtf.DtfMethodAction;

/**
 * ViewerElementManager
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerElementManager {

	private SignalMLOperationMode mode;

	public ViewerElementManager(SignalMLOperationMode mode) {
		this.mode = mode;
	}

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

	private ManagerOfPresetManagers managerOfPresetsManagers;

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
	/** A parent for new {@link java.awt.Dialog}s. */
	private Window dialogParent;
	private View view;

	private ViewerStatusBar statusBar;
	private JMenuBar menuBar;

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
	private PleaseWaitDialog pleaseWaitDialog;
	private ApplicationPreferencesDialog applicationPreferencesDialog;
	private ApplicationToolsDialog applicationToolsDialog;
	private RegisterCodecDialog registerCodecDialog;
	private SignalParametersDialog signalParametersDialog;
	private SignalMontageDialog signalMontageDialog;
	private SignalSelectionDialog signalSelectionDialog;
	private NewTagDialog newTagDialog;
	private EditTagAnnotationDialog editTagAnnotationDialog;
	private SlavePlotSettingsPopupDialog slavePlotSettingsPopupDialog;
	private ChannelOptionsPopupDialog channelOptionsPopupDialog;
	private TagStylePaletteDialog tagStylePaletteDialog;
	/**
	 * A dialog for editing tag style presets.
	 */
	private TagStylePresetDialog tagStylePresetDialog;
	private HelpDialog helpDialog;
	private TagComparisonDialog tagComparisonDialog;
	private EditTagDescriptionDialog editTagDescriptionDialog;
	private IterationSetupDialog iterationSetupDialog;
	private ExportSignalDialog exportSignalDialog;
	private EditFFTSampleFilterDialog editFFTSampleFilterDialog;

	/**
	 * A dialog allowing to edit the {@link TimeDomainSampleFilter} parameters.
	 */
	private EditTimeDomainSampleFilterDialog editTimeDomainSampleFilterDialog;

	/**
	 * A dialog shown when the user wants to start a recording of a monitor
	 * signal. Recording target files can be set using this dialog.
	 */
	private StartMonitorRecordingDialog startMonitorRecordingDialog;
	private StartMonitorRecordingPsychopyDialog startMonitorRecordingPsychopyDialog;

	private MP5LocalExecutorDialog mp5LocalExecutorDialog;
	private AtomTableDialog atomTableDialog;
	private BookFilterDialog bookFilterDialog;

	/* Actions */
	private CheckSignalAction checkSignalAction;
	private CloseWindowAction closeWindowAction;
	private EditPreferencesAction editPreferencesAction;
	private HelpContentsAction helpContentsAction;
	private ViewModeAction viewModeAction;
	private ShowStatusBarAction showStatusBarAction;
	private ShowLeftPanelAction showLeftPanelAction;
	private ShowBottomPanelAction showBottomPanelAction;
	private OpenBookDocumentAction openBookDocumentAction;
	private CloseDocumentAction closeActiveDocumentAction;
	private NewTagAction newTagAction;
	private OpenTagAction openTagAction;
	private CloseTagAction closeTagAction;
	private SaveTagAction saveTagAction;
	private SaveTagAsAction saveTagAsAction;
	private ExportEEGLabTagAction exportEEGLabTagAction;

	/**
	 * Represents an {@link Action action} responsible for showing a dialog
	 * allowing to select which tag document should be active (the one that
	 * is being edited).
	 */
	private ChooseActiveTagAction chooseActiveTagAction;

	/**
	 * Represents an {@link Action action} responsible for showing a dialog
	 * using which tag documents can be compared.
	 */
	private CompareTagsAction compareTagsAction;
	private SynchronizeTagsWithTriggerAction synchronizeTagsWithTriggerAction;

	private EditSignalParametersAction editSignalParametersAction;
	private EditSignalMontageAction editSignalMontageAction;
	private OpenSignalWizardAction openSignalWizardAction;
	private ApplyDefaultMontageAction applyDefaultMontageAction;
	private PreciseSelectionAction preciseSelectionAction;
	private EditTagStylesAction editTagStylesAction;
	/**
	 * An action performed when the user wants tag style presets to be edited or created.
	 */
	private EditTagStylePresetsAction editTagStylePresetsAction;
	private EditTagDescriptionAction editTagDescriptionAction;
	private ExportSignalAction exportSignalAction;
	private ExportBookAction exportBookAction;
	private AbortAllTasksAction abortAllTasksAction;
	private SuspendAllTasksAction suspendAllTasksAction;
	private ResumeAllTasksAction resumeAllTasksAction;
	private RemoveAllTasksAction removeAllTasksAction;
	private RemoveAllFinishedTasksAction removeAllFinishedTasksAction;
	private RemoveAllAbortedTasksAction removeAllAbortedTasksAction;
	private RemoveAllFailedTasksAction removeAllFailedTasksAction;

	/**
	 * Represents an {@link Action} invoked when the user wants to start
	 * a monitor signal recording.
	 */
	private StartMonitorRecordingAction startMonitorRecordingAction;

	/**
	 * Represents an {@link Action} invoked when the user wants to stop
	 * a monitor signal recording.
	 */
	private StopMonitorRecordingAction stopMonitorRecordingAction;

	private AddCameraAction addCameraAction;

	private ArrayList<AbstractSignalMLAction> runMethodActions;
	private ArrayList<AbstractSignalMLAction> iterateMethodActions;

	/* File chooser */
	private ViewerFileChooser fileChooser;

	/* MP5 executors */

	/* Menu */
	private JMenu fileMenu;
	private JMenu editMenu;
	private JMenu viewMenu;

	private JMenu psychopyMenu;
	/**
	 * A {@link JMenu} for operating on a monitor signal.
	 */
	private JMenu monitorMenu;

	/**
	 * A {@link JMenu} for operations concerning tags.
	 */
	private JMenu tagsMenu;

	private JMenu toolsMenu;

	private JMenu analysisMenu;

	private JMenu helpMenu;

	/* Other */
	private TableToTextExporter tableToTextExporter;

	public SignalMLOperationMode getMode() {
		return mode;
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

	public void setManagerOfPresetsManagers(ManagerOfPresetManagers managerOfPresetsManagers) {
		this.managerOfPresetsManagers = managerOfPresetsManagers;
	}

	public ManagerOfPresetManagers getManagerOfPresetsManagers() {
		return managerOfPresetsManagers;
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
			documentFlowIntegrator.setDocumentManager(getDocumentManager());
			documentFlowIntegrator.setMrudRegistry(getMrudRegistry());
			documentFlowIntegrator.setCodecManager(getCodecManager());
			documentFlowIntegrator.setActionFocusManager(getActionFocusManager());
			documentFlowIntegrator.setApplicationConfig(getApplicationConfig());
			documentFlowIntegrator.setMontagePresetManager(managerOfPresetsManagers.getMontagePresetManager());
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
			taskTableModel.setTaskManager(getTaskManager());
		}
		return taskTableModel;
	}

	public PropertySheetModel getPropertySheetModel() {
		if (propertySheetModel == null) {
			propertySheetModel = new PropertySheetModel();
		}
		return propertySheetModel;
	}

	public ViewerStatusBar getStatusBar() {
		if (statusBar == null) {
			statusBar = new ViewerStatusBar();
			statusBar.setMaximizeDocumentsAction(getViewModeAction());
			statusBar.setActionFocusManager(getActionFocusManager());
			statusBar.initialize();
			statusBar.setStatus(_R(("Svarog v.{0} ready"),
								   new Object[] {SvarogConstants.VERSION}));
			getActionFocusManager().addActionFocusListener(statusBar);
		}
		return statusBar;
	}

	public JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu(_("File"));
			fileMenu.setMnemonic(KeyEvent.VK_F);

			fileMenu.add(getOpenSignalWizardAction());
			fileMenu.add(getCloseActiveDocumentAction());

			fileMenu.addSeparator();

			fileMenu.add(getExportSignalAction());

			if (mode == SignalMLOperationMode.APPLICATION) {
				fileMenu.addSeparator();
				fileMenu.add(getCloseWindowAction());
			}
		}
		return fileMenu;
	}

	public JMenu getViewMenu() {
		if (viewMenu == null) {
			viewMenu = new JMenu(_("View"));
			viewMenu.setMnemonic(KeyEvent.VK_V);

			JMenu toolbarMenu = new JMenu(_("Toggle toolbars"));
			toolbarMenu.add(new JMenuItem(new BasicToolbarSwitchAction(actionFocusManager)));
			toolbarMenu.add(new JMenuItem(new ScaleToolbarSwitchAction(actionFocusManager)));
			toolbarMenu.add(new JMenuItem(new SettingsToolbarSwitchAction(actionFocusManager)));
			toolbarMenu.add(new JMenuItem(new RecordingToolbarSwitchAction(actionFocusManager)));
			viewMenu.add(toolbarMenu);

			viewMenu.add(new JCheckBoxMenuItem(getShowStatusBarAction()));
			if (mode == SignalMLOperationMode.APPLICATION) {
					viewMenu.add(new JCheckBoxMenuItem(getShowLeftPanelAction()));
					viewMenu.add(new JCheckBoxMenuItem(getShowBottomPanelAction()));
			}
			viewMenu.add(new JCheckBoxMenuItem(getViewModeAction()));
			viewMenu.addSeparator();

			viewMenu.add(new JMenuItem(new EditPlotOptionsAction(actionFocusManager, true)));

			viewMenu.add(new JMenuItem(getEditSignalMontageAction()));

			EditSignalFiltersAction editSignalFiltersAction = new EditSignalFiltersAction(actionFocusManager);
			editSignalFiltersAction.setSignalMontageDialog(getSignalMontageDialog());
			viewMenu.add(new JMenuItem(editSignalFiltersAction));

			SignalFilterSwitchAction signalFilterSwitchAction = new SignalFilterSwitchAction(actionFocusManager);
			viewMenu.add(new JMenuItem(signalFilterSwitchAction));
		}
		return viewMenu;
	}

	/**
	 * Returns the {@link JMenu} which holds the items responsible for
	 * operating on monitors.
	 * @return a {@link JMenu} for operating on monitor signals.
	 */
	public JMenu getMonitorMenu() {
		if (monitorMenu == null) {
			monitorMenu = new JMenu(_("Recording"));
			monitorMenu.setMnemonic(KeyEvent.VK_R);

			final OpenSignalWizardAction onlineAmplifiersAction =
					new OpenSignalWizardAction(this, SignalSourceTabbedPane.TAB_AMPLIFIERS);
			final OpenSignalWizardAction onlineExperimentsAction =
					new OpenSignalWizardAction(this, SignalSourceTabbedPane.TAB_EXPERIMENTS);

			monitorMenu.add(onlineAmplifiersAction);
			monitorMenu.add(onlineExperimentsAction);
			monitorMenu.addSeparator();
			monitorMenu.add(getAddCameraAction());
			monitorMenu.addSeparator();
			monitorMenu.add(getStartMonitorRecordingAction());
			monitorMenu.add(getStopMonitorRecordingAction());
			monitorMenu.addSeparator();
			monitorMenu.add(getCheckSignalAction());

			monitorMenu.addMenuListener(new MenuListener() {
				@Override
				public void menuSelected(MenuEvent me) {
					onlineExperimentsAction.setEnabled(
						ObciServerCapabilities.getSharedInstance().hasOnlineExperiments()
					);
					onlineAmplifiersAction.setEnabled(
						ObciServerCapabilities.getSharedInstance().hasOnlineAmplifiers()
					);
					getAddCameraAction().setEnabled(
						ObciServerCapabilities.getSharedInstance().hasCameraServer()
						&& VideoFrame.isVideoAvailable()
					);
				}
				@Override
				public void menuDeselected(MenuEvent me) {
					// nothing here
				}
				@Override
				public void menuCanceled(MenuEvent me) {
					// nothing here
				}
			});
		}
		return monitorMenu;
	}

	/**
	 * Returns the {@link JMenu} which holds the items responsible for
	 * operating on tags.
	 * @return a {@link JMenu} for items connected with operations on tags
	 */
	public JMenu getTagsMenu() {
		if (tagsMenu == null) {
			tagsMenu = new JMenu(_("Tags"));
			tagsMenu.setMnemonic(KeyEvent.VK_T);

			tagsMenu.add(getNewTagAction());
			tagsMenu.add(getOpenTagAction());
			tagsMenu.add(getSaveTagAction());
			tagsMenu.add(getSaveTagAsAction());
			tagsMenu.add(getExportEEGLabTagAction());
			tagsMenu.add(getCloseTagAction());
			tagsMenu.addSeparator();

			tagsMenu.add(getChooseActiveTagAction());
			tagsMenu.add(getEditTagDescriptionAction());
			tagsMenu.add(getEditTagStylesAction());
			tagsMenu.add(getEditTagStylePresetsAction());
			tagsMenu.addSeparator();

			tagsMenu.add(getCompareTagsAction());
			tagsMenu.add(getSynchronizeTagsWithTriggerAction());

		}
		return tagsMenu;
	}

	public JMenu getToolsMenu() {
		if (toolsMenu == null) {
			toolsMenu = new JMenu(_("Tools"));
			toolsMenu.add(createChangeToolAction(_("Select tags"), SelectTagSignalTool.class, "org/signalml/app/icon/arrow.png"));
			toolsMenu.add(createChangeToolAction(_("Move signal"), MoveSignalSignalTool.class, "org/signalml/app/icon/hand.png"));
			toolsMenu.add(createChangeToolAction(_("Magnify the signal"), ZoomSignalTool.class, "org/signalml/app/icon/zoom.png"));
			toolsMenu.add(createChangeToolAction(_("Measure the signal"), RulerSignalTool.class, "org/signalml/app/icon/ruler.png"));
			toolsMenu.add(createChangeToolAction(_("Signal FFT"), SignalFFTTool.class, "org/signalml/app/icon/fft.png"));
			toolsMenu.add(createChangeToolAction(_("Select signal fragments"), SelectChannelSignalTool.class, "org/signalml/app/icon/channelselection.png"));

			toolsMenu.addSeparator();
			toolsMenu.add(getEditPreferencesAction());
		}
		return toolsMenu;
	}

	private Action createChangeToolAction(String label, Class<? extends SignalTool> toolClass, String iconPath) {
		ChangeToolAction action = new ChangeToolAction(label, toolClass, actionFocusManager);
		if (iconPath != null) {
			action.setIconPath(iconPath);
		}
		return action;
	}

	public JMenu getAnalysisMenu() {
		if (analysisMenu == null) {
			analysisMenu = new JMenu(_("Analysis"));

			addRunMethodActionToMenu(analysisMenu, EvokedPotentialMethod.class);

			analysisMenu.add(new JMenu(_("Short-Time Fourier Transform")));

			analysisMenu.add(new JMenu(_("Wavelet Transform")));

			analysisMenu.add(new JMenu(_("Independent Component Analysis")));

			analysisMenu.add(new JMenuItem(DtfMethodAction.TITLE));

			JMenu mpMenu = new JMenu(_("Matching pursuit"));
			addRunMethodActionToMenu(mpMenu, MP5Method.class);
			mpMenu.add(getOpenBookDocumentAction());
			mpMenu.add(getExportBookAction());
			addRunMethodActionToMenu(mpMenu, BookAverageMethod.class);
			addRunMethodActionToMenu(mpMenu, BookReporterMethod.class);
			addRunMethodActionToMenu(mpMenu, BookToTagMethod.class);

			EditToolsAction configurationAction = new EditToolsAction(getApplicationToolsDialog());
			configurationAction.setConfig(getApplicationConfig());
			mpMenu.add(configurationAction);

			analysisMenu.add(mpMenu);

			if (mode == SignalMLOperationMode.APPLICATION) {

				JMenu iterationMenu = new JMenu(_("Iterate"));
				for (AbstractSignalMLAction action : getIterateMethodActions()) {
					iterationMenu.add(action);
				}
				iterationMenu.setVisible(!getIterateMethodActions().isEmpty());

				analysisMenu.add(iterationMenu);

			}
		}
		return analysisMenu;
	}

	public JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu(_("Help"));
			helpMenu.setMnemonic(KeyEvent.VK_H);
			helpMenu.add(getHelpContentsAction());
			helpMenu.add(createHelpContentsAction(_("Recording"), _H("recording.html")));
			helpMenu.add(createHelpContentsAction(_("Analysis"), _H("analysis.html")));
			helpMenu.add(createHelpContentsAction(_("Tools"), _H("tools.html")));
			helpMenu.add(createHelpContentsAction(_("Tags"), _H("tags.html")));
		}
		return helpMenu;
	}

	public JMenuBar getMenuBar() {
		if (menuBar == null) {
			menuBar = new JMenuBar();

			menuBar.add(getFileMenu());
			menuBar.add(getViewMenu());
			menuBar.add(getMonitorMenu());
			menuBar.add(getTagsMenu());
			menuBar.add(getToolsMenu());
			menuBar.add(getAnalysisMenu());
			menuBar.add(getHelpMenu());
		}
		return menuBar;
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
			treeTabbedPane = new ViewerTabbedPane(WebTabbedPane.TOP);
			treeTabbedPane.addTab(_("Tags"), (String) null, getTagTreePane(), _("Shows open tags"));
			treeTabbedPane.addTab(_("Signals"), (String) null, getSignalTreePane(), _("Shows open signals"));
			treeTabbedPane.addTab(_("Online"), (String) null, getMonitorTreePane(), _("Shows online signals"));
			treeTabbedPane.addTab(_("Books"), (String) null, getBookTreePane(), _("Shows open books"));
			treeTabbedPane.addTab(_("Workspace"), (String) null, getWorkspaceTreePane(), _("Shows workspace"));
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
			propertyTabbedPane.addTab(_("Console"), (String) null, getConsole(), _("Application messages console"));
			propertyTabbedPane.addTab(_("Tasks"), (String) null, getTaskTablePane(), _("Lists active tasks that run in the background or on the server"));
			propertyTabbedPane.addTab(_("Properties"), (String) null, getPropertySheetPane(), _("Shows properties of selected objects"));
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
			monitorTreePane = new ViewerTreePane(getMonitorTree());
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
			workspaceTree = new ViewerWorkspaceTree(getWorkspaceTreeModel());
			workspaceTree.setActionFocusManager(getActionFocusManager());
			workspaceTree.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			workspaceTree.addTreeSelectionListener(getPropertySheetModel());
		}
		return workspaceTree;
	}

	public ViewerSignalTree getSignalTree() {
		if (signalTree == null) {
			signalTree = new ViewerSignalTree(getSignalTreeModel());
			signalTree.setActionFocusManager(getActionFocusManager());
			signalTree.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			signalTree.addTreeSelectionListener(getPropertySheetModel());
		}
		return signalTree;
	}

	public ViewerMonitorTree getMonitorTree() {
		if (monitorTree == null) {
			monitorTree = new ViewerMonitorTree(getMonitorTreeModel());
			monitorTree.setActionFocusManager(getActionFocusManager());
			monitorTree.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			monitorTree.addTreeSelectionListener(getPropertySheetModel());
		}
		return monitorTree;
	}

	public ViewerBookTree getBookTree() {
		if (bookTree == null) {
			bookTree = new ViewerBookTree(getBookTreeModel());
			bookTree.setActionFocusManager(getActionFocusManager());
			bookTree.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			bookTree.addTreeSelectionListener(getPropertySheetModel());
		}
		return bookTree;
	}

	public ViewerTagTree getTagTree() {
		if (tagTree == null) {
			tagTree = new ViewerTagTree(getTagTreeModel());
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
			taskTable = new ViewerTaskTable(getTaskTableModel());
			taskTable.setActionFocusManager(getActionFocusManager());
			taskTable.setTaskManager(getTaskManager());
			taskTable.setMethodManager(getMethodManager());

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
			documentTabbedPane.setActionFocusManager(getActionFocusManager());
			documentTabbedPane.setView(getView());
			documentTabbedPane.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			documentTabbedPane.initialize();
		}
		return documentTabbedPane;
	}

	public PleaseWaitDialog getPleaseWaitDialog() {
		if (pleaseWaitDialog == null) {
			pleaseWaitDialog = new PleaseWaitDialog(getDialogParent());
			pleaseWaitDialog.initializeNow();
		}
		return pleaseWaitDialog;
	}

	public ApplicationPreferencesDialog getApplicationPreferencesDialog() {
		if (applicationPreferencesDialog == null) {
			applicationPreferencesDialog = new ApplicationPreferencesDialog(mode, getDialogParent(), true);
			applicationPreferencesDialog.setProfileDir(getProfileDir());
			applicationPreferencesDialog.setFileChooser(getFileChooser());
			if (mode == SignalMLOperationMode.APPLICATION) {
				applicationPreferencesDialog.setCodecManager(getCodecManager());
				applicationPreferencesDialog.setMp5ExecutorManager(getMp5ExecutorManager());
			}
		}
		return applicationPreferencesDialog;
	}

	public ApplicationToolsDialog getApplicationToolsDialog() {
		if (applicationToolsDialog == null) {
			applicationToolsDialog = new ApplicationToolsDialog(mode, getDialogParent(), true);
			applicationToolsDialog.setFileChooser(getFileChooser());
			applicationToolsDialog.setMp5ExecutorManager(getMp5ExecutorManager());
		}
		return applicationToolsDialog;
	}

	public CheckSignalDialog getCheckSignalDialog() {
		if (checkSignalDialog == null) {
			checkSignalDialog = new CheckSignalDialog(getDialogParent(), true);
		}
		return checkSignalDialog;
	}


	public RegisterCodecDialog getRegisterCodecDialog() {
		if (registerCodecDialog == null) {
			registerCodecDialog = new RegisterCodecDialog(getDialogParent(), true);
			registerCodecDialog.setCodecManager(getCodecManager());
			registerCodecDialog.setProfileDir(getProfileDir());
		}
		return registerCodecDialog;
	}

	public SignalParametersDialog getSignalParametersDialog() {
		if (signalParametersDialog == null) {
			signalParametersDialog = new SignalParametersDialog(getDialogParent(), true);
		}
		return signalParametersDialog;
	}

	public SignalMontageDialog getSignalMontageDialog() {
		if (signalMontageDialog == null) {
			signalMontageDialog = new SignalMontageDialog(this, getDialogParent(), true);
		}
		return signalMontageDialog;
	}

	public SignalSelectionDialog getSignalSelectionDialog() {
		if (signalSelectionDialog == null) {
			signalSelectionDialog = new SignalSelectionDialog(getDialogParent(), true);
		}
		return signalSelectionDialog;
	}

	public NewTagDialog getNewTagDialog() {
		if (newTagDialog == null) {
			newTagDialog = new NewTagDialog(getDialogParent(), true);
			newTagDialog.setApplicationConfig(getApplicationConfig());
		}
		return newTagDialog;
	}

	public EditTagAnnotationDialog getEditTagAnnotationDialog() {
		if (editTagAnnotationDialog == null) {
			editTagAnnotationDialog = new EditTagAnnotationDialog(getDialogParent(), true);
		}
		return editTagAnnotationDialog;
	}

	public SlavePlotSettingsPopupDialog getSlavePlotSettingsPopupDialog() {
		if (slavePlotSettingsPopupDialog == null) {
			slavePlotSettingsPopupDialog = new SlavePlotSettingsPopupDialog(getDialogParent(), true);
			// XXX this dialog reuses the main window's instance of the montage dialog
			// this seems to work and since the dialog is very big we try to keep it
			// like this if it works
			slavePlotSettingsPopupDialog.setSignalMontageDialog(getSignalMontageDialog());
		}
		return slavePlotSettingsPopupDialog;
	}

	public ChannelOptionsPopupDialog getChannelOptionsPopupDialog() {
		if (channelOptionsPopupDialog == null) {
			channelOptionsPopupDialog = new ChannelOptionsPopupDialog(getDialogParent(), true);
			// XXX this dialog reuses the main window's instance of the montage dialog
			// this seems to work and since the dialog is very big we try to keep it
			// like this if it works
			//channelOptionsPopupDialog.setSignalMontageDialog(getSignalMontageDialog());
		}
		return channelOptionsPopupDialog;
	}

	public TagStylePaletteDialog getTagStylePaletteDialog() {
		if (tagStylePaletteDialog == null) {
			tagStylePaletteDialog = new TagStylePaletteDialog(getDialogParent(), true);
			tagStylePaletteDialog.setFileChooser(getFileChooser());
		}
		return tagStylePaletteDialog;
	}

	/**
	 * Returns the dialog for creating/editing tag styles presets.
	 * @return
	 */
	public TagStylePresetDialog getTagStylePresetDialog() {
		if (tagStylePresetDialog == null) {
			tagStylePresetDialog = new TagStylePresetDialog(getDialogParent(), true);
			tagStylePresetDialog.setFileChooser(getFileChooser());
		}
		return tagStylePresetDialog;
	}

	public HelpDialog getHelpDialog() {
		if (helpDialog == null) {
			helpDialog = new HelpDialog(null, false);
		}
		return helpDialog;
	}

	public TagComparisonDialog getTagComparisonDialog() {
		if (tagComparisonDialog == null) {
			tagComparisonDialog = new TagComparisonDialog(getDialogParent(), true);
			tagComparisonDialog.setTableToTextExporter(getTableToTextExporter());
			tagComparisonDialog.setFileChooser(getFileChooser());
		}
		return tagComparisonDialog;
	}

	public EditTagDescriptionDialog getEditTagDescriptionDialog() {
		if (editTagDescriptionDialog == null) {
			editTagDescriptionDialog = new EditTagDescriptionDialog(getDialogParent(), true);
		}
		return editTagDescriptionDialog;
	}

	public IterationSetupDialog getIterationSetupDialog() {
		if (iterationSetupDialog == null) {
			iterationSetupDialog = new IterationSetupDialog(getDialogParent(), true);
			iterationSetupDialog.setMethodManager(getMethodManager());
		}
		return iterationSetupDialog;
	}

	public ExportSignalDialog getExportSignalDialog() {
		if (exportSignalDialog == null) {
			exportSignalDialog = new ExportSignalDialog(getDialogParent(), true);
		}
		return exportSignalDialog;
	}

	public EditFFTSampleFilterDialog getEditFFTSampleFilterDialog() {
		if (editFFTSampleFilterDialog == null) {
			editFFTSampleFilterDialog = new EditFFTSampleFilterDialog(getDialogParent(), true);
			editFFTSampleFilterDialog.setFileChooser(getFileChooser());
		}
		return editFFTSampleFilterDialog;
	}

	/**
	 * Returns a {@link EditTimeDomainSampleFilterDialog} used by this
	 * ViewerElementManager.
	 * @return the {@link EditTimeDomainSampleFilterDialog} used
	 */
	public EditTimeDomainSampleFilterDialog getEditTimeDomainSampleFilterDialog() {
		if (editTimeDomainSampleFilterDialog == null) {
			editTimeDomainSampleFilterDialog = new EditTimeDomainSampleFilterDialog(getDialogParent(), true);
			editTimeDomainSampleFilterDialog.setFileChooser(getFileChooser());
		}
		return editTimeDomainSampleFilterDialog;
	}


	/**
	 * Returns a {@link StartMonitorRecordingDialog} used by this
	 * ViewerElementManager.
	 * @return the {@link StartMonitorRecordingDialog} used
	 */
	public StartMonitorRecordingDialog getStartMonitorRecordingDialog() {
		if (startMonitorRecordingDialog == null) {
			startMonitorRecordingDialog = new StartMonitorRecordingDialog(getDialogParent(), true);
		}
		return startMonitorRecordingDialog;
	}
	
	public StartMonitorRecordingPsychopyDialog getStartMonitorRecordingPsychopyDialog() {
		if (startMonitorRecordingPsychopyDialog == null) {
			startMonitorRecordingPsychopyDialog = new StartMonitorRecordingPsychopyDialog(getDialogParent(), true);
		}
		return startMonitorRecordingPsychopyDialog;
	}

	public MP5LocalExecutorDialog getMp5LocalExecutorDialog() {
		if (mp5LocalExecutorDialog == null) {
			mp5LocalExecutorDialog = new MP5LocalExecutorDialog(getDialogParent(), true);
			mp5LocalExecutorDialog.setFileChooser(getFileChooser());
		}
		return mp5LocalExecutorDialog;
	}

	public AtomTableDialog getAtomTableDialog() {
		if (atomTableDialog == null) {
			atomTableDialog = new AtomTableDialog(getDialogParent(), true);
		}
		return atomTableDialog;
	}

	public BookFilterDialog getBookFilterDialog() {
		if (bookFilterDialog == null) {
			bookFilterDialog = new BookFilterDialog(managerOfPresetsManagers.getBookFilterPresetManager(), getDialogParent(), true);
			bookFilterDialog.setFileChooser(getFileChooser());
		}
		return bookFilterDialog;
	}

	public ViewerFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new ViewerFileChooser();
			fileChooser.setApplicationConfig(getApplicationConfig());
			fileChooser.initialize();
		}
		return fileChooser;
	}

	public CloseWindowAction getCloseWindowAction() {
		if (closeWindowAction == null) {
			closeWindowAction = new CloseWindowAction();
		}
		return closeWindowAction;
	}

	public EditPreferencesAction getEditPreferencesAction() {
		if (editPreferencesAction == null) {
			editPreferencesAction = new EditPreferencesAction();
			editPreferencesAction.setPreferencesDialog(getApplicationPreferencesDialog());
			editPreferencesAction.setConfig(getApplicationConfig());
		}
		return editPreferencesAction;
	}

	public HelpContentsAction getHelpContentsAction() {
		if (helpContentsAction == null) {
			helpContentsAction = createHelpContentsAction(_("About"), _H("contents.html"));
			helpContentsAction.setIconPath("org/signalml/app/icon/help.png");
		}
		return helpContentsAction;
	}

	protected HelpContentsAction createHelpContentsAction(String text, URL url) {
		HelpContentsAction action = new HelpContentsAction(text, url);
		action.setHelpDialog(getHelpDialog());
		return action;
	}

	public ViewModeAction getViewModeAction() {
		if (viewModeAction == null) {
			viewModeAction = new ViewModeAction();
		}
		return viewModeAction;
	}

	public ShowStatusBarAction getShowStatusBarAction() {
		if (showStatusBarAction == null) {
			showStatusBarAction = new ShowStatusBarAction();
		}
		return showStatusBarAction;
	}

	public ShowLeftPanelAction getShowLeftPanelAction() {
		if (showLeftPanelAction == null) {
			showLeftPanelAction = new ShowLeftPanelAction();
		}
		return showLeftPanelAction;
	}

	public ShowBottomPanelAction getShowBottomPanelAction() {
		if (showBottomPanelAction == null) {
			showBottomPanelAction = new ShowBottomPanelAction();
		}
		return showBottomPanelAction;
	}

	/**
	 * Returns the action performed when the user chooses an option to
	 * open a book.
	 * @return the action performed when the user wants a book to be opened
	 */
	public OpenBookDocumentAction getOpenBookDocumentAction() {
		if (openBookDocumentAction == null) {
			openBookDocumentAction = new OpenBookDocumentAction();
			openBookDocumentAction.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			openBookDocumentAction.setFileChooser(getFileChooser());
		}
		return openBookDocumentAction;
	}

	public CheckSignalAction getCheckSignalAction() {
		if (checkSignalAction == null) {
			checkSignalAction = new CheckSignalAction(getActionFocusManager());
			checkSignalAction.setCheckSignalDialog(getCheckSignalDialog());
		}
		return checkSignalAction;
	}

	/**
	 * Returns an {@link Action} responsible for starting a new monitor
	 * recording (it shows a dialog which allows to select recording target
	 * files and starts the recording).
	 * @return an {@link Action} responsible for starting a new monitor
	 * recording
	 */
	public StartMonitorRecordingAction getStartMonitorRecordingAction() {
		if (startMonitorRecordingAction == null) {
			startMonitorRecordingAction = new StartMonitorRecordingAction(getActionFocusManager());
			startMonitorRecordingAction.setStartMonitorRecordingDialog(getStartMonitorRecordingDialog());
		}
		return startMonitorRecordingAction;
	}

	/**
	 * Returns an {@link Action} responsible for stopping a monitor recording.
	 * @return an {@link Action} responsible for stopping an  monitor
	 * recording
	 */
	public StopMonitorRecordingAction getStopMonitorRecordingAction() {
		if (stopMonitorRecordingAction == null)
			stopMonitorRecordingAction = new StopMonitorRecordingAction(getActionFocusManager());
		return stopMonitorRecordingAction;
	}

	/**
	 * Returns an {@link Action} responsible for opening a new window
	 * with video preview.
	 * @return an {@link Action} responsible for opening video preview
	 */
	public AddCameraAction getAddCameraAction() {
		if (addCameraAction == null) {
			addCameraAction = new AddCameraAction(this);
		}
		return addCameraAction;
	}

	public CloseDocumentAction getCloseActiveDocumentAction() {
		if (closeActiveDocumentAction == null) {
			closeActiveDocumentAction = new CloseDocumentAction(getActionFocusManager());
			closeActiveDocumentAction.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
		}
		return closeActiveDocumentAction;
	}

	public NewTagAction getNewTagAction() {
		if (newTagAction == null) {
			newTagAction = new NewTagAction(getActionFocusManager());
			newTagAction.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			newTagAction.setNewTagDialog(getNewTagDialog());
		}
		return newTagAction;
	}

	public OpenTagAction getOpenTagAction() {
		if (openTagAction == null) {
			openTagAction = new OpenTagAction(getActionFocusManager());
			openTagAction.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			openTagAction.setFileChooser(getFileChooser());
			openTagAction.setOptionPaneParent(getOptionPaneParent());
		}
		return openTagAction;
	}

	public CloseTagAction getCloseTagAction() {
		if (closeTagAction == null) {
			closeTagAction = new CloseTagAction(getActionFocusManager());
			closeTagAction.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
		}
		return closeTagAction;
	}

	public SaveTagAction getSaveTagAction() {
		if (saveTagAction == null) {
			saveTagAction = new SaveTagAction(getActionFocusManager());
			saveTagAction.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
		}
		return saveTagAction;
	}

	public SaveTagAsAction getSaveTagAsAction() {
		if (saveTagAsAction == null) {
			saveTagAsAction = new SaveTagAsAction(getActionFocusManager());
			saveTagAsAction.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
		}
		return saveTagAsAction;
	}

	/**
	 * Returns an {@link Action} responsible for showing a dialog allowing
	 * to select which tag document should be active (the one that is being
	 * edited).
	 * @return an {@link Action} responsible for showing a dialog for
	 * selecting active tag document
	 */
	public ChooseActiveTagAction getChooseActiveTagAction() {
		if (chooseActiveTagAction == null) {
			chooseActiveTagAction = new ChooseActiveTagAction(getActionFocusManager());
		}
		return chooseActiveTagAction;
	}

	/**
	 * Returns an {@link Action} responsible for showing a dialog
	 * using which tag documents can be compared.
	 * @return an {@link Action} responsible for showing a dialog
	 * which can be used to compare tags
	 */
	public CompareTagsAction getCompareTagsAction() {
		if (compareTagsAction == null) {
			compareTagsAction = new CompareTagsAction(getActionFocusManager());
			compareTagsAction.setTagComparisonDialog(getTagComparisonDialog());
		}
		return compareTagsAction;
	}

	public SynchronizeTagsWithTriggerAction getSynchronizeTagsWithTriggerAction() {
		if (synchronizeTagsWithTriggerAction == null) {
			synchronizeTagsWithTriggerAction = new SynchronizeTagsWithTriggerAction(getActionFocusManager());
		}
		return synchronizeTagsWithTriggerAction;
	}

	public ExportEEGLabTagAction getExportEEGLabTagAction() {
		if (exportEEGLabTagAction == null) {
			exportEEGLabTagAction = new ExportEEGLabTagAction(getActionFocusManager());
			exportEEGLabTagAction.setFileChooser(getFileChooser());
			exportEEGLabTagAction.setOptionPaneParent(getOptionPaneParent());
		}
		return exportEEGLabTagAction;
	}

	public EditSignalParametersAction getEditSignalParametersAction() {
		if (editSignalParametersAction == null) {
			editSignalParametersAction = new EditSignalParametersAction(getActionFocusManager());
			editSignalParametersAction.setSignalParametersDialog(getSignalParametersDialog());
		}
		return editSignalParametersAction;
	}

	public EditSignalMontageAction getEditSignalMontageAction() {
		if (editSignalMontageAction == null) {
			editSignalMontageAction = new EditSignalMontageAction(getActionFocusManager());
			editSignalMontageAction.setSignalMontageDialog(getSignalMontageDialog());
		}
		return editSignalMontageAction;
	}

	public OpenSignalWizardAction getOpenSignalWizardAction() {
		if (openSignalWizardAction == null) {
			openSignalWizardAction = new OpenSignalWizardAction(this, null);
		}
		return openSignalWizardAction;
	}
	
	public ApplyDefaultMontageAction getApplyDefaultMontageAction() {
		if (applyDefaultMontageAction == null) {
			applyDefaultMontageAction = new ApplyDefaultMontageAction(getActionFocusManager());
		}
		return applyDefaultMontageAction;
	}

	public PreciseSelectionAction getPreciseSelectionAction() {
		if (preciseSelectionAction == null) {
			preciseSelectionAction = new PreciseSelectionAction(getActionFocusManager());
			preciseSelectionAction.setSignalSelectionDialog(getSignalSelectionDialog());
		}
		return preciseSelectionAction;
	}

	public EditTagStylesAction getEditTagStylesAction() {
		if (editTagStylesAction == null) {
			editTagStylesAction = new EditTagStylesAction(getActionFocusManager());
			editTagStylesAction.setTagStylePaletteDialog(getTagStylePaletteDialog());
		}
		return editTagStylesAction;
	}

	/**
	 * Returns the action called when tag styles presets are to be created/edited.
	 * @return
	 */
	public EditTagStylePresetsAction getEditTagStylePresetsAction() {
		if (editTagStylePresetsAction == null) {
			editTagStylePresetsAction = new EditTagStylePresetsAction(getActionFocusManager());
			editTagStylePresetsAction.setTagStylePaletteDialog(getTagStylePresetDialog());
		}
		return editTagStylePresetsAction;
	}

	public EditTagDescriptionAction getEditTagDescriptionAction() {
		if (editTagDescriptionAction == null) {
			editTagDescriptionAction = new EditTagDescriptionAction(getActionFocusManager());
			editTagDescriptionAction.setEditTagDescriptionDialog(getEditTagDescriptionDialog());
		}
		return editTagDescriptionAction;
	}

	public ExportSignalAction getExportSignalAction() {
		if (exportSignalAction == null) {
			exportSignalAction = new ExportSignalAction(getActionFocusManager());
			exportSignalAction.setExportSignalDialog(getExportSignalDialog());
			exportSignalAction.setPleaseWaitDialog(getPleaseWaitDialog());
			exportSignalAction.setFileChooser(getFileChooser());
			exportSignalAction.setOptionPaneParent(getOptionPaneParent());
		}
		return exportSignalAction;
	}

	public ExportBookAction getExportBookAction() {
		if (exportBookAction == null) {
			exportBookAction = new ExportBookAction(getActionFocusManager());
			exportBookAction.setPleaseWaitDialog(getPleaseWaitDialog());
			exportBookAction.setFileChooser(getFileChooser());
			exportBookAction.setOptionPaneParent(getOptionPaneParent());
		}
		return exportBookAction;
	}

	public AbortAllTasksAction getAbortAllTasksAction() {
		if (abortAllTasksAction == null) {
			abortAllTasksAction = new AbortAllTasksAction();
			abortAllTasksAction.setTaskManager(getTaskManager());
			abortAllTasksAction.setOptionPaneParent(getOptionPaneParent());
		}
		return abortAllTasksAction;
	}

	public SuspendAllTasksAction getSuspendAllTasksAction() {
		if (suspendAllTasksAction == null) {
			suspendAllTasksAction = new SuspendAllTasksAction();
			suspendAllTasksAction.setTaskManager(getTaskManager());
			suspendAllTasksAction.setOptionPaneParent(getOptionPaneParent());
		}
		return suspendAllTasksAction;
	}

	public ResumeAllTasksAction getResumeAllTasksAction() {
		if (resumeAllTasksAction == null) {
			resumeAllTasksAction = new ResumeAllTasksAction();
			resumeAllTasksAction.setTaskManager(getTaskManager());
			resumeAllTasksAction.setOptionPaneParent(getOptionPaneParent());
		}
		return resumeAllTasksAction;
	}

	public RemoveAllTasksAction getRemoveAllTasksAction() {
		if (removeAllTasksAction == null) {
			removeAllTasksAction = new RemoveAllTasksAction();
			removeAllTasksAction.setTaskManager(getTaskManager());
			removeAllTasksAction.setOptionPaneParent(getOptionPaneParent());
		}
		return removeAllTasksAction;
	}

	public RemoveAllFinishedTasksAction getRemoveAllFinishedTasksAction() {
		if (removeAllFinishedTasksAction == null) {
			removeAllFinishedTasksAction = new RemoveAllFinishedTasksAction();
			removeAllFinishedTasksAction.setTaskManager(getTaskManager());
			removeAllFinishedTasksAction.setOptionPaneParent(getOptionPaneParent());
		}
		return removeAllFinishedTasksAction;
	}

	public RemoveAllAbortedTasksAction getRemoveAllAbortedTasksAction() {
		if (removeAllAbortedTasksAction == null) {
			removeAllAbortedTasksAction = new RemoveAllAbortedTasksAction();
			removeAllAbortedTasksAction.setTaskManager(getTaskManager());
			removeAllAbortedTasksAction.setOptionPaneParent(getOptionPaneParent());
		}
		return removeAllAbortedTasksAction;
	}

	public RemoveAllFailedTasksAction getRemoveAllFailedTasksAction() {
		if (removeAllFailedTasksAction == null) {
			removeAllFailedTasksAction = new RemoveAllFailedTasksAction();
			removeAllFailedTasksAction.setTaskManager(getTaskManager());
			removeAllFailedTasksAction.setOptionPaneParent(getOptionPaneParent());
		}
		return removeAllFailedTasksAction;
	}

	public void addRunMethodActionToMenu(JMenu menu, Class<? extends AbstractMethod> methodClass) {
		for (Method method : getMethodManager().getMethods()) {
			if (methodClass.isInstance(method)) {
				RunMethodAction runMethodAction = new RunMethodAction(method, getMethodManager());
				runMethodAction.setTaskManager(getTaskManager());
				menu.add(runMethodAction);
			}
		}
	}

	public ArrayList<AbstractSignalMLAction> getIterateMethodActions() {
		if (iterateMethodActions == null) {
			Method[] methods = getMethodManager().getMethods();
			iterateMethodActions = new ArrayList<>(methods.length);
			IterateMethodAction iterateMethodAction;
			for (Method method : methods) {

				if (method instanceof IterableMethod) {
					iterateMethodAction = new IterateMethodAction((IterableMethod) method, getMethodManager());
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

	public DocumentView createDocumentViewPanel(Document document) throws SignalMLException {

		DocumentView documentView = null;

		if (document instanceof SignalDocument) {

			SignalView signalView;
			signalView = new SignalView((SignalDocument) document);
			signalView.setActionFocusManager(getActionFocusManager());
			signalView.setSlavePlotSettingsPopupDialog(getSlavePlotSettingsPopupDialog());
			signalView.setChannelOptionsPopupDialog(getChannelOptionsPopupDialog());
			signalView.setDocumentFlowIntegrator(getDocumentFlowIntegrator());
			signalView.setMontagePresetManager(managerOfPresetsManagers.getMontagePresetManager());
			signalView.setSignalMontageDialog(getSignalMontageDialog());
			signalView.setStartMonitorRecordingDialog(getStartMonitorRecordingDialog());
			signalView.setStartMonitorRecordingPsychopyDialog(getStartMonitorRecordingPsychopyDialog());
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
			bookView.setActionFocusManager(getActionFocusManager());
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
		localTaskManager.setPleaseWaitDialog(getPleaseWaitDialog());

	}

	public void configureAccelerators() {

		// configure accelerators
		getCloseWindowAction().setAccelerator("ctrl X");
		getHelpContentsAction().setAccelerator("F1");
		getViewModeAction().setAccelerator("F11");
		getOpenSignalWizardAction().setAccelerator("ctrl O");
		getCloseActiveDocumentAction().setAccelerator("ctrl F4");
		getOpenTagAction().setAccelerator("alt O");
		getCloseTagAction().setAccelerator("ctrl alt F4");
		getSaveTagAction().setAccelerator("alt S");
		getSaveTagAsAction().setAccelerator("ctrl alt shift S");
		getEditPreferencesAction().setAccelerator("ctrl P");
		getExportSignalAction().setAccelerator("ctrl E");
		getEditSignalMontageAction().setAccelerator("alt M");
		getNewTagAction().setAccelerator("alt N");

	}

}

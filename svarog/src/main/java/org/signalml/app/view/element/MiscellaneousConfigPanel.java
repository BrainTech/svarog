/* MiscellaneousConfigPanel.java created 2007-12-14
 *
 */
package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.SignalMLOperationMode;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.view.dialog.DynamicCompilationWarningDialog;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/**
 * Panel with various options of (tooltips, "view mode", warning dialogs):
 * Contains 3 sub-panels:
 * <ul>
 * <li>the general panel with some check-boxes telling:
 * <ul>
 * <li>{@link #getSaveConfigOnEveryChangeCheckBox() if} configuration files
 * should be saved after every change,</li>
 * <li>{@link #getRestoreWorkspaceCheckBox() if} the workspace should be
 * restored when the application starts,</li>
 * <li>{@link #getDontShowCompilationWarningCheckBox() if} if the {@link
 * DynamicCompilationWarningDialog} should be shown when a dynamic
 * compilation is needed,</li></ul></li>
 * <li>the panel which allows to select {@link #getToolTipInitialSpinner()
 * after} how many milliseconds the tool tips should appear and {@link
 * #getToolTipDismissSpinner() how long} they should be displayed,</li>
 * <li>the panel which allows to select the parameters of the "view mode":
 * <ul><li>{@link #getViewModeHidesMainToolBarCheckBox() if} the main tool
 * bar should be displayed,</li>
 * <li>{@link #getViewModeHidesLeftPanelCheckBox() if} the left panel
 * should be displayed,</li>
 * <li>{@link #getViewModeHidesBottomPanelCheckBox() if} the bottom panel
 * should be displayed,</li>
 * <li>{@link #getViewModeCompactsPageTagBarsCheckBox() if} the column
 * header (where the page tags are shown) should be compacted,</li>
 * <li>{@link #getViewModeSnapsToPageCheckBox() if} the signal plot should
 * be snapped to the beginning of the page.</li></ul></li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MiscellaneousConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link MessageSourceAccessor source} of messages (labels)
	 */
	private MessageSourceAccessor messageSource;

	/**
	 * the check-box if configuration files should be saved after every change
	 * (checked) or on exit (unchecked)
	 */
	private JCheckBox saveConfigOnEveryChangeCheckBox;
	/**
	 * the check-box if the workspace should be restored when the application
	 * starts
	 */
	private JCheckBox restoreWorkspaceCheckBox;
	
	/**
	 * the check-box if the {@link DynamicCompilationWarningDialog} should be
	 * shown when a dynamic compilation is needed (unchecked)
	 */
	private JCheckBox dontShowCompilationWarningCheckBox;

	/**
	 * the check-box if the main tool bar should (unchecked) be displayed in
	 * the "view mode" (F11) or not (checked)
	 */
	private JCheckBox viewModeHidesMainToolBarCheckBox;
	/**
	 * the check-box if the left panel should (unchecked) be displayed in
	 * the "view mode" (F11) or not (checked)
	 */
	private JCheckBox viewModeHidesLeftPanelCheckBox;
	/**
	 * the check-box if the bottom panel should (unchecked) be displayed in
	 * the "view mode" (F11) or not (checked)
	 */
	private JCheckBox viewModeHidesBottomPanelCheckBox;
	/**
	 * the check-box if the column header should (checked) be compacted in
	 * the "view mode" (F11) or not (unchecked)
	 */
	private JCheckBox viewModeCompactsPageTagBarsCheckBox;
	/**
	 * the check-box if the signal plot should (checked) be snapped to the
	 * beginning of the page in the "view mode" (F11)
	 */
	private JCheckBox viewModeSnapsToPageCheckBox;

	/**
	 * the spinner which allows to select after how many milliseconds the
	 * tooltip message should be shown
	 */
	private JSpinner toolTipInitialSpinner;
	/**
	 * the spinner which allows to select for how many milliseconds the
	 * tooltip message should be displayed
	 */
	private JSpinner toolTipDismissSpinner;

	/**
	 * the {@link SignalMLOperationMode mode} in which Svarog is running
	 */
	private SignalMLOperationMode mode;

	/**
	 * Constructor. Sets the {@link MessageSourceAccessor message source} and
	 * initializes this panel.
	 * @param messageSource the source of messages (labels)
	 * @param mode the {@link SignalMLOperationMode mode} in which Svarog
	 * is running (application or applet)
	 */
	public MiscellaneousConfigPanel(MessageSourceAccessor messageSource, SignalMLOperationMode mode) {
		super();
		this.messageSource = messageSource;
		this.mode = mode;
		initialize();
	}

	/**
	 * Initializes this panel with 3 sub-panels:
	 * <ul>
	 * <li>the general panel with some check-boxes telling:
	 * <ul>
	 * <li>{@link #getSaveConfigOnEveryChangeCheckBox() if} configuration files
	 * should be saved after every change,</li>
	 * <li>{@link #getRestoreWorkspaceCheckBox() if} the workspace should be
	 * restored when the application starts,</li>
	 * <li>{@link #getDontShowCompilationWarningCheckBox() if} if the {@link
	 * DynamicCompilationWarningDialog} should be shown when a dynamic
	 * compilation is needed,</li></ul></li>
	 * <li>the panel which allows to select {@link #getToolTipInitialSpinner()
	 * after} how many milliseconds the tool tips should appear and {@link
	 * #getToolTipDismissSpinner() how long} they should be displayed,</li>
	 * <li>the panel which allows to select the parameters of the "view mode":
	 * <ul><li>{@link #getViewModeHidesMainToolBarCheckBox() if} the main tool
	 * bar should be displayed,</li>
	 * <li>{@link #getViewModeHidesLeftPanelCheckBox() if} the left panel
	 * should be displayed,</li>
	 * <li>{@link #getViewModeHidesBottomPanelCheckBox() if} the bottom panel
	 * should be displayed,</li>
	 * <li>{@link #getViewModeCompactsPageTagBarsCheckBox() if} the column
	 * header (where the page tags are shown) should be compacted,</li>
	 * <li>{@link #getViewModeSnapsToPageCheckBox() if} the signal plot should
	 * be snapped to the beginning of the page.</li></ul></li>
	 * </ul>
	 */
	private void initialize() {

		setBorder(new EmptyBorder(3,3,3,3));
		setLayout(new BorderLayout());

		JPanel generalPanel = new JPanel();
		generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.Y_AXIS));
		generalPanel.setBorder(new CompoundBorder(
		                               new TitledBorder(messageSource.getMessage("preferences.miscellaneous.general")),
		                               new EmptyBorder(3,3,3,3)
		                       ));

		if (mode == SignalMLOperationMode.APPLICATION) {
			generalPanel.add(getSaveConfigOnEveryChangeCheckBox());
			generalPanel.add(getRestoreWorkspaceCheckBox());
		}
		if (mode == SignalMLOperationMode.APPLICATION) {
			generalPanel.add(getDontShowCompilationWarningCheckBox());
		}

		JPanel toolTipPanel = new JPanel();
		toolTipPanel.setLayout(new BoxLayout(toolTipPanel, BoxLayout.X_AXIS));
		toolTipPanel.setBorder(new CompoundBorder(
		                               new TitledBorder(messageSource.getMessage("preferences.miscellaneous.toolTip")),
		                               new EmptyBorder(3,3,3,3)
		                       ));

		toolTipPanel.add(new JLabel(messageSource.getMessage("preferences.miscellaneous.toolTipInitial")));
		toolTipPanel.add(Box.createHorizontalStrut(5));
		toolTipPanel.add(Box.createHorizontalGlue());
		toolTipPanel.add(getToolTipInitialSpinner());
		toolTipPanel.add(Box.createHorizontalStrut(5));
		toolTipPanel.add(Box.createHorizontalGlue());
		toolTipPanel.add(new JLabel(messageSource.getMessage("preferences.miscellaneous.toolTipDismiss")));
		toolTipPanel.add(Box.createHorizontalStrut(5));
		toolTipPanel.add(Box.createHorizontalGlue());
		toolTipPanel.add(getToolTipDismissSpinner());
		toolTipPanel.add(Box.createHorizontalStrut(5));
		toolTipPanel.add(Box.createHorizontalGlue());
		toolTipPanel.add(new JLabel(messageSource.getMessage("preferences.miscellaneous.toolTipMS")));

		JPanel viewModePanel = new JPanel();
		if (mode == SignalMLOperationMode.APPLICATION) {
			viewModePanel.setLayout(new GridLayout(3,2,3,3));
		} else {
			viewModePanel.setLayout(new GridLayout(2,2,3,3));
		}
		viewModePanel.setBorder(new CompoundBorder(
		                                new TitledBorder(messageSource.getMessage("preferences.miscellaneous.viewMode")),
		                                new EmptyBorder(3,3,3,3)
		                        ));

		viewModePanel.add(getViewModeHidesMainToolBarCheckBox());
		if (mode == SignalMLOperationMode.APPLICATION) {
			viewModePanel.add(getViewModeHidesLeftPanelCheckBox());
			viewModePanel.add(getViewModeHidesBottomPanelCheckBox());
		}
		viewModePanel.add(getViewModeCompactsPageTagBarsCheckBox());
		viewModePanel.add(getViewModeSnapsToPageCheckBox());

		add(generalPanel, BorderLayout.CENTER);

		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(toolTipPanel, BorderLayout.NORTH);
		southPanel.add(viewModePanel, BorderLayout.CENTER);

		add(southPanel, BorderLayout.SOUTH);

	}

	/**
	 * Returns the check-box if configuration files should be saved after every
	 * change (checked) or on exit (unchecked).
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box if configuration files should be saved after every
	 * change (checked) or on exit (unchecked)
	 */
	public JCheckBox getSaveConfigOnEveryChangeCheckBox() {
		if (saveConfigOnEveryChangeCheckBox == null) {
			saveConfigOnEveryChangeCheckBox = new JCheckBox(messageSource.getMessage("preferences.miscellaneous.saveConfigOnEveryChange"));
		}
		return saveConfigOnEveryChangeCheckBox;
	}

	/**
	 * Returns the check-box if the workspace should be restored when the
	 * application starts.
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box if the workspace should be restored when the
	 * application starts
	 */
	public JCheckBox getRestoreWorkspaceCheckBox() {
		if (restoreWorkspaceCheckBox == null) {
			restoreWorkspaceCheckBox = new JCheckBox(messageSource.getMessage("preferences.miscellaneous.restoreWorkspace"));
		}
		return restoreWorkspaceCheckBox;
	}

	/**
	 * Returns the check-box if the {@link DynamicCompilationWarningDialog}
	 * should be shown when a dynamic compilation is needed (unchecked).
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box if the {@code DynamicCompilationWarningDialog}
	 * should be shown when a dynamic compilation is needed (unchecked)
	 */
	public JCheckBox getDontShowCompilationWarningCheckBox() {
		if (dontShowCompilationWarningCheckBox == null) {
			dontShowCompilationWarningCheckBox = new JCheckBox(messageSource.getMessage("preferences.miscellaneous.dontShowCompilationWarning"));
		}
		return dontShowCompilationWarningCheckBox;
	}

	/**
	 * Returns the check-box if the main tool bar should (unchecked) be
	 * displayed in the "view mode" (F11) or not (checked).
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box if the main tool bar should (unchecked) be
	 * displayed in the "view mode" (F11) or not (checked)
	 */
	public JCheckBox getViewModeHidesMainToolBarCheckBox() {
		if (viewModeHidesMainToolBarCheckBox == null) {
			viewModeHidesMainToolBarCheckBox = new JCheckBox(messageSource.getMessage("preferences.miscellaneous.viewModeHidesMainToolBar"));
		}
		return viewModeHidesMainToolBarCheckBox;
	}

	/**
	 * Returns the check-box if the left panel should (unchecked) be displayed
	 * in the "view mode" (F11) or not (checked).
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box if the left panel should (unchecked) be displayed
	 * in the "view mode" (F11) or not (checked)
	 */
	public JCheckBox getViewModeHidesLeftPanelCheckBox() {
		if (viewModeHidesLeftPanelCheckBox == null) {
			viewModeHidesLeftPanelCheckBox = new JCheckBox(messageSource.getMessage("preferences.miscellaneous.viewModeHidesLeftPanel"));
		}
		return viewModeHidesLeftPanelCheckBox;
	}

	/**
	 * Returns the check-box if the bottom panel should (unchecked) be
	 * displayed in the "view mode" (F11) or not (checked).
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box if the bottom panel should (unchecked) be
	 * displayed in the "view mode" (F11) or not (checked)
	 */
	public JCheckBox getViewModeHidesBottomPanelCheckBox() {
		if (viewModeHidesBottomPanelCheckBox == null) {
			viewModeHidesBottomPanelCheckBox = new JCheckBox(messageSource.getMessage("preferences.miscellaneous.viewModeHidesBottomPanel"));
		}
		return viewModeHidesBottomPanelCheckBox;
	}

	/**
	 * Returns the check-box if the column header should (checked) be compacted
	 * in the "view mode" (F11) or not (unchecked).
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box if the column header should (checked) be compacted
	 * in the "view mode" (F11) or not (unchecked)
	 */
	public JCheckBox getViewModeCompactsPageTagBarsCheckBox() {
		if (viewModeCompactsPageTagBarsCheckBox == null) {
			viewModeCompactsPageTagBarsCheckBox = new JCheckBox(messageSource.getMessage("preferences.miscellaneous.viewModeCompactsPageTagBars"));
		}
		return viewModeCompactsPageTagBarsCheckBox;
	}

	/**
	 * Returns the check-box if the signal plot should (checked) be snapped to
	 * the beginning of the page in the "view mode" (F11).
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box if the signal plot should (checked) be snapped to
	 * the beginning of the page in the "view mode" (F11)
	 */
	public JCheckBox getViewModeSnapsToPageCheckBox() {
		if (viewModeSnapsToPageCheckBox == null) {
			viewModeSnapsToPageCheckBox = new JCheckBox(messageSource.getMessage("preferences.miscellaneous.viewModeSnapsToPage"));
		}
		return viewModeSnapsToPageCheckBox;
	}

	/**
	 * Returns the spinner which allows to select after how many milliseconds
	 * the tooltip message should be shown.
	 * If the spinner doesn't exist it is created.
	 * @return the spinner which allows to select after how many milliseconds
	 * the tooltip message should be shown
	 */
	public JSpinner getToolTipInitialSpinner() {
		if (toolTipInitialSpinner == null) {
			toolTipInitialSpinner = new JSpinner(new SpinnerNumberModel(100,100,100000,100));
			toolTipInitialSpinner.setPreferredSize(new Dimension(100,25));
		}
		return toolTipInitialSpinner;
	}

	/**
	 * Returns the spinner which allows to select for how many milliseconds the
	 * tooltip message should be displayed.
	 * If the spinner doesn't exist it is created.
	 * @return the spinner which allows to select for how many milliseconds the
	 * tooltip message should be displayed
	 */
	public JSpinner getToolTipDismissSpinner() {
		if (toolTipDismissSpinner == null) {
			toolTipDismissSpinner = new JSpinner(new SpinnerNumberModel(100,100,100000,100));
			toolTipDismissSpinner.setPreferredSize(new Dimension(100,25));
		}
		return toolTipDismissSpinner;
	}

	/**
	 * Fills all the fields of this panel (check-boxes and spinners) from the
	 * given {@link ApplicationConfiguration configuration} of Svarog
	 * @param applicationConfig the configuration of Svarog
	 */
	public void fillPanelFromModel(ApplicationConfiguration applicationConfig) {

		if (mode == SignalMLOperationMode.APPLICATION) {
			getSaveConfigOnEveryChangeCheckBox().setSelected(applicationConfig.isSaveConfigOnEveryChange());
			getRestoreWorkspaceCheckBox().setSelected(applicationConfig.isRestoreWorkspace());
		}
		if (mode == SignalMLOperationMode.APPLICATION) {
			getDontShowCompilationWarningCheckBox().setSelected(applicationConfig.isDontShowDynamicCompilationWarning());
		}

		getViewModeHidesMainToolBarCheckBox().setSelected(applicationConfig.isViewModeHidesBottomPanel());
		if (mode == SignalMLOperationMode.APPLICATION) {
			getViewModeHidesLeftPanelCheckBox().setSelected(applicationConfig.isViewModeHidesLeftPanel());
			getViewModeHidesBottomPanelCheckBox().setSelected(applicationConfig.isViewModeHidesBottomPanel());
		}
		getViewModeCompactsPageTagBarsCheckBox().setSelected(applicationConfig.isViewModeCompactsPageTagBars());
		getViewModeSnapsToPageCheckBox().setSelected(applicationConfig.isViewModeSnapsToPage());

		getToolTipInitialSpinner().setValue(applicationConfig.getToolTipInitialDelay());
		getToolTipDismissSpinner().setValue(applicationConfig.getToolTipDismissDelay());

	}

	/**
	 * Stores the user input from all check-boxes and spinners in this dialog
	 * in the {@link ApplicationConfiguration configuration} of Svarog.
	 * @param applicationConfig the configuration of Svarog
	 */
	public void fillModelFromPanel(ApplicationConfiguration applicationConfig) {

		if (mode == SignalMLOperationMode.APPLICATION) {
			applicationConfig.setSaveConfigOnEveryChange(getSaveConfigOnEveryChangeCheckBox().isSelected());
			applicationConfig.setRestoreWorkspace(getRestoreWorkspaceCheckBox().isSelected());
		}
		if (mode == SignalMLOperationMode.APPLICATION) {
			applicationConfig.setDontShowDynamicCompilationWarning(getDontShowCompilationWarningCheckBox().isSelected());
		}

		applicationConfig.setViewModeHidesMainToolBar(getViewModeHidesMainToolBarCheckBox().isSelected());
		if (mode == SignalMLOperationMode.APPLICATION) {
			applicationConfig.setViewModeHidesLeftPanel(getViewModeHidesLeftPanelCheckBox().isSelected());
			applicationConfig.setViewModeHidesBottomPanel(getViewModeHidesBottomPanelCheckBox().isSelected());
		}
		applicationConfig.setViewModeCompactsPageTagBars(getViewModeCompactsPageTagBarsCheckBox().isSelected());
		applicationConfig.setViewModeSnapsToPage(getViewModeSnapsToPageCheckBox().isSelected());

		applicationConfig.setToolTipInitialDelay((Integer) getToolTipInitialSpinner().getValue());
		applicationConfig.setToolTipDismissDelay((Integer) getToolTipDismissSpinner().getValue());

	}

	/**
	 * Validates this panel.
	 * This panel is always valid
	 * @param errors the object in which errors should be stored.
	 */
	public void validate(Errors errors) {
		// do nothing
	}

}

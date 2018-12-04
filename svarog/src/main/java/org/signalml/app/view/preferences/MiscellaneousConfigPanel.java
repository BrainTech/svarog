/* MiscellaneousConfigPanel.java created 2007-12-14
 *
 */
package org.signalml.app.view.preferences;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.SignalMLOperationMode;
import org.signalml.app.SvarogApplication;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.i18n.SvarogI18n;

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
	 * combo box for language selection
	 */
	private JComboBox languageComboBox;

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
	 * text field for sentry DSN (e.g https://sentry.io/...)
	 */
	private JTextField sentryDsnTextField;

	/**
	 * text field for sentry installation Site
	 */
	private JTextField sentrySiteTextField;

	/**
	 * the {@link SignalMLOperationMode mode} in which Svarog is running
	 */
	private SignalMLOperationMode mode;

	/**
	 * Constructor. Initializes the panel.
	 * @param mode the {@link SignalMLOperationMode mode} in which Svarog
	 * is running (application or applet)
	 */
	public MiscellaneousConfigPanel(SignalMLOperationMode mode) {
		super();
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
								   new TitledBorder(_("General")),
								   new EmptyBorder(3,3,3,3)
							   ));

		if (mode == SignalMLOperationMode.APPLICATION) {
			JPanel applicationPanel = new JPanel();
			applicationPanel.setLayout(new BoxLayout(applicationPanel, BoxLayout.X_AXIS));

			JPanel applicationPanelLeft = new JPanel();
			applicationPanelLeft.setLayout(new BoxLayout(applicationPanelLeft, BoxLayout.X_AXIS));
			applicationPanelLeft.add(new JLabel(_("Language")));
			applicationPanelLeft.add(Box.createHorizontalStrut(5));
			applicationPanelLeft.add(getLanguageComboBox());

			JPanel applicationPanelRight = new JPanel();
			applicationPanelRight.setLayout(new BoxLayout(applicationPanelRight, BoxLayout.Y_AXIS));
			applicationPanelRight.add(getSaveConfigOnEveryChangeCheckBox());
			applicationPanelRight.add(getRestoreWorkspaceCheckBox());

			applicationPanel.add(applicationPanelLeft);
			applicationPanel.add(Box.createHorizontalGlue());
			applicationPanel.add(applicationPanelRight);

			generalPanel.add(applicationPanel);
		}

		JPanel sentryPanel = new JPanel();
		sentryPanel.setBorder(new EmptyBorder(3,3,3,3));
		sentryPanel.setLayout(new BorderLayout(10, 0));
		JPanel sentryDsnPanel = new JPanel();
		sentryDsnPanel.setBorder(new EmptyBorder(3,3,3,3));
		sentryDsnPanel.setLayout(new BorderLayout(10, 0));
		JPanel sentrySitePanel = new JPanel();
		sentrySitePanel.setBorder(new EmptyBorder(3,3,3,3));
		sentrySitePanel.setLayout(new BorderLayout(10, 0));
		sentryDsnPanel.add(new JLabel(_("Sentry DSN")), BorderLayout.WEST);
		sentryDsnPanel.add(getSentryDsnTextField(), BorderLayout.CENTER);
		sentrySitePanel.add(new JLabel(_("Sentry Site")), BorderLayout.WEST);
		sentrySitePanel.add(getSentrySiteTextField(), BorderLayout.CENTER);
		sentryPanel.add(sentryDsnPanel, BorderLayout.NORTH);
		sentryPanel.add(sentrySitePanel, BorderLayout.CENTER);

		sentryPanel.add(new JLabel("("+_("Svarog needs to be restarted for changes to take effect")+")"), BorderLayout.SOUTH);
		generalPanel.add(sentryPanel);

		JPanel toolTipPanel = new JPanel();
		toolTipPanel.setLayout(new BoxLayout(toolTipPanel, BoxLayout.X_AXIS));
		toolTipPanel.setBorder(new CompoundBorder(
								   new TitledBorder(_("Tool tip settings")),
								   new EmptyBorder(3,3,3,3)
							   ));

		toolTipPanel.add(new JLabel(_("To show")));
		toolTipPanel.add(Box.createHorizontalStrut(5));
		toolTipPanel.add(Box.createHorizontalGlue());
		toolTipPanel.add(getToolTipInitialSpinner());
		toolTipPanel.add(Box.createHorizontalStrut(5));
		toolTipPanel.add(Box.createHorizontalGlue());
		toolTipPanel.add(new JLabel(_("To hide")));
		toolTipPanel.add(Box.createHorizontalStrut(5));
		toolTipPanel.add(Box.createHorizontalGlue());
		toolTipPanel.add(getToolTipDismissSpinner());
		toolTipPanel.add(Box.createHorizontalStrut(5));
		toolTipPanel.add(Box.createHorizontalGlue());
		toolTipPanel.add(new JLabel(_("[ms]")));

		JPanel viewModePanel = new JPanel();
		if (mode == SignalMLOperationMode.APPLICATION) {
			viewModePanel.setLayout(new GridLayout(3,2,3,3));
		} else {
			viewModePanel.setLayout(new GridLayout(2,2,3,3));
		}
		viewModePanel.setBorder(new CompoundBorder(
									new TitledBorder(_("View mode")),
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
	 * Return the combo-box for UI language selection.
	 * If the combo box doesn't exist, it is created.
	 *
	 * @return combo box instance
	 */
	public JComboBox getLanguageComboBox() {
		if (languageComboBox == null) {
			languageComboBox = new JComboBox(SvarogI18n.LANGUAGES);
		}
		return languageComboBox;
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
			saveConfigOnEveryChangeCheckBox = new JCheckBox(_("Save config files on every change (otherwise on exit)"));
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
			restoreWorkspaceCheckBox = new JCheckBox(_("Restore the workspace when the application loads"));
		}
		return restoreWorkspaceCheckBox;
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
			viewModeHidesMainToolBarCheckBox = new JCheckBox(_("Hide main toolbar"));
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
			viewModeHidesLeftPanelCheckBox = new JCheckBox(_("Hide left panel"));
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
			viewModeHidesBottomPanelCheckBox = new JCheckBox(_("Hide bottom panel"));
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
			viewModeCompactsPageTagBarsCheckBox = new JCheckBox(_("Compact page tags"));
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
			viewModeSnapsToPageCheckBox = new JCheckBox(_("Snap to page"));
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
	 * Return the text field which allows to enter Sentry DNS for logging
	 * crash reports in case OpenBCI server is not active.
	 * If the spinner doesn't exist it is created.
	 * @return created text field
	 */
	protected JTextField getSentryDsnTextField() {
		if (sentryDsnTextField == null)
			sentryDsnTextField = new JTextField();
		return sentryDsnTextField;
	}
	
	/**
	 * Return the text field which allows to enter Sentry instalation site
	 * for logging crash reports
	 * If the spinner doesn't exist it is created.
	 * @return created text field
	 */
	
	protected JTextField getSentrySiteTextField() {
		if (sentrySiteTextField == null)
			sentrySiteTextField = new JTextField();
		return sentrySiteTextField;
	}

	/**
	 * Fills all the fields of this panel (check-boxes and spinners) from the
	 * given {@link ApplicationConfiguration configuration} of Svarog
	 * @param applicationConfig the configuration of Svarog
	 */
	public void fillPanelFromModel(ApplicationConfiguration applicationConfig) {

		if (mode == SignalMLOperationMode.APPLICATION) {
			getLanguageComboBox().setSelectedItem(SvarogApplication.getGeneralConfiguration().getLocale());
			getSaveConfigOnEveryChangeCheckBox().setSelected(applicationConfig.isSaveConfigOnEveryChange());
			getRestoreWorkspaceCheckBox().setSelected(applicationConfig.isRestoreWorkspace());
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
		getSentryDsnTextField().setText(applicationConfig.getSentryDsn());
		getSentrySiteTextField().setText(applicationConfig.getSentrySite());


	}

	/**
	 * Stores the user input from all check-boxes and spinners in this dialog
	 * in the {@link ApplicationConfiguration configuration} of Svarog.
	 * @param applicationConfig the configuration of Svarog
	 */
	public void fillModelFromPanel(ApplicationConfiguration applicationConfig) {

		if (mode == SignalMLOperationMode.APPLICATION) {
			SvarogApplication.getGeneralConfiguration().setLocale((String) getLanguageComboBox().getSelectedItem());
			applicationConfig.setSaveConfigOnEveryChange(getSaveConfigOnEveryChangeCheckBox().isSelected());
			applicationConfig.setRestoreWorkspace(getRestoreWorkspaceCheckBox().isSelected());
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
		applicationConfig.setSentryDsn(getSentryDsnTextField().getText());
		applicationConfig.setSentrySite(getSentrySiteTextField().getText());


	}

	/**
	 * Validates this panel.
	 * This panel is always valid
	 * @param errors the object in which errors should be stored.
	 */
	public void validate(ValidationErrors errors) {
		// do nothing
	}

}

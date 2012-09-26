/* NewSleepComparisonDialog.java created 2008-02-27
 *
 */

package org.signalml.plugin.newstager.ui;

import static org.signalml.plugin.i18n.PluginI18n._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.model.components.PropertySheetModel;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.workspace.ViewerPropertySheet;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractPluginDialog;
import org.signalml.plugin.newstager.NewStagerPlugin;
import org.signalml.plugin.newstager.data.NewStagerSleepComparison;

/**
 * NewSleepComparisonDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class NewSleepComparisonDialog extends AbstractPluginDialog {

	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane;

	private NewStagerSleepComparisonTableModel sleepComparisonTableModel;
	private NewStagerSleepComparisonTable sleepComparisonTable;
	private JScrollPane sleepComparisonScrollPane;
	private JPanel sleepComparisonPanel;

	private NewStagerSleepComparisonStatisticTableModel sleepComparisonStatisticTableModel;
	private NewStagerSleepComparisonStatisticTable sleepComparisonStatisticTable;
	private JScrollPane sleepComparisonStatisticScrollPane;
	private JPanel sleepComparisonStatisticPanel;

	private PropertySheetModel propertySheetModel;
	private ViewerPropertySheet propertySheet;
	private JScrollPane propertyScrollPane;
	private JPanel propertySheetPanel;

	private JPanel statisticsPanel;

	public NewSleepComparisonDialog() {
		super();
	}

	public NewSleepComparisonDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle(_("Sleep staging comparison"));
		setIconImage(IconUtils.loadClassPathImage(NewStagerPlugin.iconPath));
		setResizable(false);
		super.initialize();
	}

	@Override
	public boolean isCancellable() {
		return false;
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout());
		interfacePanel.setBorder(new CompoundBorder(new TitledBorder(
									 _("Comparison data")), new EmptyBorder(3, 3, 3, 3)));

		interfacePanel.add(getTabbedPane(), BorderLayout.CENTER);

		return interfacePanel;

	}

	public NewStagerSleepComparisonTableModel getSleepComparisonTableModel() {
		if (sleepComparisonTableModel == null) {
			sleepComparisonTableModel = new NewStagerSleepComparisonTableModel();
		}
		return sleepComparisonTableModel;
	}

	public NewStagerSleepComparisonTable getSleepComparisonTable() {
		if (sleepComparisonTable == null) {
			sleepComparisonTable = new NewStagerSleepComparisonTable(
				getSleepComparisonTableModel());
		}
		return sleepComparisonTable;
	}

	public JScrollPane getSleepComparisonScrollPane() {
		if (sleepComparisonScrollPane == null) {
			sleepComparisonScrollPane = new JScrollPane(
				getSleepComparisonTable());
			sleepComparisonScrollPane.setPreferredSize(new Dimension(600, 550));
		}
		return sleepComparisonScrollPane;
	}

	public JPanel getSleepComparisonPanel() {
		if (sleepComparisonPanel == null) {
			sleepComparisonPanel = new JPanel(new BorderLayout());
			sleepComparisonPanel.setBorder(new CompoundBorder(new TitledBorder(
											   _("Stager (row) vs. expert (column) comparison")),
										   new EmptyBorder(3, 3, 3, 3)));
			sleepComparisonPanel.add(getSleepComparisonScrollPane(),
									 BorderLayout.CENTER);
		}
		return sleepComparisonPanel;
	}

	public NewStagerSleepComparisonStatisticTableModel getSleepComparisonStatisticTableModel() {
		if (sleepComparisonStatisticTableModel == null) {
			sleepComparisonStatisticTableModel = new NewStagerSleepComparisonStatisticTableModel();
		}
		return sleepComparisonStatisticTableModel;
	}

	public NewStagerSleepComparisonStatisticTable getSleepComparisonStatisticTable() {
		if (sleepComparisonStatisticTable == null) {
			sleepComparisonStatisticTable = new NewStagerSleepComparisonStatisticTable(
				getSleepComparisonStatisticTableModel());
		}
		return sleepComparisonStatisticTable;
	}

	public JScrollPane getSleepComparisonStatisticScrollPane() {
		if (sleepComparisonStatisticScrollPane == null) {
			sleepComparisonStatisticScrollPane = new JScrollPane(
				getSleepComparisonStatisticTable());
			sleepComparisonStatisticScrollPane.setPreferredSize(new Dimension(
						400, 300));
		}
		return sleepComparisonStatisticScrollPane;
	}

	public JPanel getSleepComparisonStatisticPanel() {
		if (sleepComparisonStatisticPanel == null) {
			sleepComparisonStatisticPanel = new JPanel(new BorderLayout());
			sleepComparisonStatisticPanel
			.setBorder(new CompoundBorder(
						   new TitledBorder(_("Stages")), new EmptyBorder(3,
								   3, 3, 3)));
			sleepComparisonStatisticPanel.add(
				getSleepComparisonStatisticScrollPane(),
				BorderLayout.CENTER);
		}
		return sleepComparisonStatisticPanel;
	}

	public PropertySheetModel getPropertySheetModel() {
		if (propertySheetModel == null) {
			propertySheetModel = new PropertySheetModel();
		}
		return propertySheetModel;
	}

	public ViewerPropertySheet getPropertySheet() {
		if (propertySheet == null) {
			propertySheet = new ViewerPropertySheet(getPropertySheetModel());
		}
		return propertySheet;
	}

	public JScrollPane getPropertyScrollPane() {
		if (propertyScrollPane == null) {
			propertyScrollPane = new JScrollPane(getPropertySheet());
			propertyScrollPane.setPreferredSize(new Dimension(400, 100));
		}
		return propertyScrollPane;
	}

	public JPanel getPropertySheetPanel() {
		if (propertySheetPanel == null) {
			propertySheetPanel = new JPanel(new BorderLayout());
			propertySheetPanel.setBorder(new CompoundBorder(new TitledBorder(
											 _("Other")), new EmptyBorder(3, 3, 3, 3)));
			propertySheetPanel
			.add(getPropertyScrollPane(), BorderLayout.CENTER);
		}
		return propertySheetPanel;
	}

	public JPanel getStatisticsPanel() {
		if (statisticsPanel == null) {
			statisticsPanel = new JPanel(new BorderLayout());
			statisticsPanel.add(getSleepComparisonStatisticPanel(),
								BorderLayout.CENTER);
			statisticsPanel.add(getPropertySheetPanel(), BorderLayout.SOUTH);
		}
		return statisticsPanel;
	}

	public JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane(JTabbedPane.TOP,
										 JTabbedPane.WRAP_TAB_LAYOUT);

			tabbedPane.addTab(_("Comparison"), getSleepComparisonPanel());
			tabbedPane.addTab(_("Statistics"), getStatisticsPanel());
		}
		return tabbedPane;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		NewStagerSleepComparison sleepComparison = (NewStagerSleepComparison) model;

		getSleepComparisonTableModel().setResult(sleepComparison);
		getSleepComparisonStatisticTableModel().setComparison(sleepComparison);

		getPropertySheetModel().setSubject(sleepComparison);

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		// nothing to do
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return NewStagerSleepComparison.class.isAssignableFrom(clazz);
	}

}

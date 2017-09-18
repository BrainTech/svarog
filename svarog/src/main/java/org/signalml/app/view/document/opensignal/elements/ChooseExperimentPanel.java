package org.signalml.app.view.document.opensignal.elements;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker.StateValue;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.ChooseExperimentTableModel;
import org.signalml.app.view.common.components.panels.AbstractPanel;
import org.signalml.app.worker.monitor.FindEEGExperimentsWorker;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

public class ChooseExperimentPanel extends AbstractPanel implements ListSelectionListener {

	public static String EXPERIMENT_SELECTED_PROPERTY = "experimentSelectedProperty";
	private static Logger logger = Logger.getLogger(ChooseExperimentPanel.class);
	
	protected ChooseExperimentTable chooseExperimentTable;
	private ChooseExperimentTableModel chooseExperimentTableModel;

	private JButton refreshButton;
	private JButton cancelButton;
	private JTextArea logTextArea;

	private FindEEGExperimentsWorker worker;
	private JProgressBar progressBar;

	public ChooseExperimentPanel() {
		createInterface();
	}

	//private methods are not 'virtual' in java
	protected ChooseExperimentTableModel getTableModel()
	{
		return new ChooseExperimentTableModel();
	}

	protected void setTitledBorder()
	{
		super.setTitledBorder(_("Choose experiment"));
	}
	
	protected void createInterface() {
		setTitledBorder();
		chooseExperimentTableModel = getTableModel();
		chooseExperimentTable = new ChooseExperimentTable(chooseExperimentTableModel);
		chooseExperimentTable.getSelectionModel().addListSelectionListener(this);
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(chooseExperimentTable);
		scrollPane.setPreferredSize(new Dimension(300, 200));
		add(scrollPane, BorderLayout.CENTER);

		add(createBottomPanel(), BorderLayout.SOUTH);
	}

	protected JPanel createBottomPanel() {
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(createLogPanel(), BorderLayout.CENTER);
		bottomPanel.add(createButtonsPanel(), BorderLayout.NORTH);

		return bottomPanel;
	}

	protected JPanel createLogPanel() {
		JPanel logPanel = new JPanel(new BorderLayout());
		logPanel.setBorder(new TitledBorder(_("Connection log")));
		logPanel.add(new JScrollPane(getLogTextField()), BorderLayout.CENTER);

		return logPanel;
	}

	protected JPanel createButtonsPanel() {
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBorder(new TitledBorder(""));
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

		buttonsPanel.add(getProgressBar());
		buttonsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonsPanel.add(getRefreshButton());
		buttonsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		buttonsPanel.add(getCancelButton());

		return buttonsPanel;
	}

	public JButton getRefreshButton() {
		if (refreshButton == null)
			refreshButton = new JButton(new RefreshButtonAction());
		return refreshButton;
	}

	protected JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton(new CancelButtonAction());
			cancelButton.setEnabled(false);
		}
		return cancelButton;
	}

	public JTextArea getLogTextField() {
		if (logTextArea == null) {
			logTextArea = new JTextArea(5, 10);
			logTextArea.setEditable(false);
		}
		return logTextArea;
	}

	public JProgressBar getProgressBar() {
		if(progressBar == null) {
			progressBar = new JProgressBar();
		}
		return progressBar;
	}

	protected void fireExperimentSelected(ExperimentDescriptor experiment) {
		this.firePropertyChange(EXPERIMENT_SELECTED_PROPERTY, null, experiment);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		fireExperimentSelected(getSelectedExperiment());
	}

	public ExperimentDescriptor getSelectedExperiment() {
		int selectedRow = chooseExperimentTable.getSelectedRow();
		ExperimentDescriptor selectedExperiment;
		if (selectedRow == -1)
			selectedExperiment = null;
		else
			selectedExperiment = chooseExperimentTableModel.getExperiments().get(selectedRow);
		return selectedExperiment;
	}

	public void clearSelection() {
		chooseExperimentTable.clearSelection();
	}

	/**
	 * Deletes all experiments from this panel.
	 */
	public void clearExperiments() {
		if (chooseExperimentTableModel != null)
			chooseExperimentTableModel.clearExperiments();
		getLogTextField().setText("");
	}
	
	public FindEEGExperimentsWorker getWorker(){
		return new FindEEGExperimentsWorker();
	}

	class RefreshButtonAction extends AbstractSignalMLAction implements PropertyChangeListener {
		private boolean executing = false;

		public RefreshButtonAction() {
			this.setText(_("Refresh"));
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			synchronized (this) {
				//only one action should be executed at once.
				chooseExperimentTableModel.setExperiments(null);
				if (executing)
					return;
				executing = true;
				setEnabled(false);
			}
			
			worker = getWorker();
			worker.addPropertyChangeListener(this);
			getProgressBar().setIndeterminate(true);
			getLogTextField().setText("");
			chooseExperimentTableModel.clearExperiments();
			getCancelButton().setEnabled(true);
			worker.execute();
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {

			if ((evt.getNewValue() instanceof StateValue)
					&& ((StateValue) evt.getNewValue()) == StateValue.DONE) {
				executing = false;
				setEnabled(true);
				getCancelButton().setEnabled(false);
				getProgressBar().setIndeterminate(false);
			}
			else if (evt.getPropertyName().equals(FindEEGExperimentsWorker.WORKER_LOG_APPENDED_PROPERTY)) {
				logger.debug("Appending" + evt.getNewValue());
				getLogTextField().append((String) evt.getNewValue());
			}
			else if (evt.getPropertyName().equals(FindEEGExperimentsWorker.NEW_EXPERIMENTS_RECEIVED)) {
				List<ExperimentDescriptor> newExperiments = (List<ExperimentDescriptor>) evt.getNewValue();
				chooseExperimentTableModel.addExperiments(newExperiments);
			}
		}
	}

	class CancelButtonAction extends AbstractSignalMLAction {
		private boolean executing = false;

		public CancelButtonAction() {
			this.setText(_("Cancel"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			worker.cancel(true);
		}
	}
}


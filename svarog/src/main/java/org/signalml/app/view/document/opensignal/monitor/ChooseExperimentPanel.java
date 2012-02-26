package org.signalml.app.view.document.opensignal.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker.StateValue;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.signalml.app.model.document.opensignal.ChooseExperimentTableModel;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.view.components.AbstractSignalMLPanel;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.app.worker.monitor.GetOpenBCIExperimentsWorker;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

public class ChooseExperimentPanel extends AbstractSignalMLPanel implements ListSelectionListener {

	public static String EXPERIMENT_SELECTED_PROPERTY = "experimentSelectedProperty";
	private static Logger logger = Logger.getLogger(ChooseExperimentPanel.class);
	
	private ChooseExperimentTable chooseExperimentTable;
	private ChooseExperimentTableModel chooseExperimentTableModel;
	private JButton refreshButton;

	public ChooseExperimentPanel() {
		initialize();
	}
	
	@Override
	protected void initialize() {
		setTitledBorder(_("Choose experiment"));
		
		chooseExperimentTableModel = new ChooseExperimentTableModel();
		chooseExperimentTable = new ChooseExperimentTable(chooseExperimentTableModel);
		chooseExperimentTable.getSelectionModel().addListSelectionListener(this);
		refreshButton = new JButton(new RefreshButtonAction());
		
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(chooseExperimentTable);
		scrollPane.setPreferredSize(new Dimension(300, 200));
		add(scrollPane, BorderLayout.CENTER);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(refreshButton);
		//buttonsPanel.add(startExperimentButton);
		
		add(buttonsPanel, BorderLayout.SOUTH);
	}
	
	protected void fireExperimentSelected(ExperimentDescriptor experiment) {
		this.firePropertyChange(EXPERIMENT_SELECTED_PROPERTY, null, experiment);
	}

	class RefreshButtonAction extends AbstractSignalMLAction implements PropertyChangeListener {
		private GetOpenBCIExperimentsWorker worker;
		private boolean executing = false;
		
		public RefreshButtonAction() {
			this.setText(_("Refresh"));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {

			synchronized(this) {
				//only one action should be executed at once.
				chooseExperimentTableModel.setExperiments(null);
				if(executing)
					return;
				executing = true;
				setEnabled(false);
			}

			System.out.println("Refreshing the list of experiments");

			worker = new GetOpenBCIExperimentsWorker(ChooseExperimentPanel.this);
			worker.addPropertyChangeListener(this);
			worker.execute();
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {

			if (((StateValue) evt.getNewValue()) == StateValue.DONE) {
				try {
					System.out.println("Refreshing experiments done");
					List<ExperimentDescriptor> experiments = worker.get();
					chooseExperimentTableModel.setExperiments(experiments);
				} catch (Exception e) {
					System.out.println("exception");
					e.printStackTrace();
					Dialogs.showExceptionDialog(ChooseExperimentPanel.this, e);
				} finally {
					executing = false;
					setEnabled(true);
				}
			}
		}

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
	
}


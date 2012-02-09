package org.signalml.app.view.document.opensignal.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker.StateValue;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.document.opensignal.ChooseExperimentTableModel;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.view.components.AbstractSignalMLPanel;
import org.signalml.app.worker.BusyDialogWorker;
import org.signalml.app.worker.monitor.GetOpenBCIExperimentsWorker;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

public class ChooseExperimentPanel extends AbstractSignalMLPanel implements ListSelectionListener {

	public static String EXPERIMENT_SELECTED_PROPERTY = "experimentSelectedProperty";
	
	private ApplicationConfiguration applicationConfiguration;
	private ChooseExperimentTable chooseExperimentTable;
	private ChooseExperimentTableModel chooseExperimentTableModel;
	private JButton refreshButton;
	private JButton startExperimentButton;

	public ChooseExperimentPanel(ApplicationConfiguration applicationConfiguration) {
		this.applicationConfiguration = applicationConfiguration;
		initialize();
	}
	
	@Override
	protected void initialize() {
		setTitledBorder(_("Choose experiment"));
		
		chooseExperimentTableModel = new ChooseExperimentTableModel();
		chooseExperimentTable = new ChooseExperimentTable(chooseExperimentTableModel);
		chooseExperimentTable.getSelectionModel().addListSelectionListener(this);
		refreshButton = new JButton(new RefreshButtonAction());
		startExperimentButton = new JButton(_("Start experiment"));
		
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
		private BusyDialogWorker busyDialogWorker;
		
		public RefreshButtonAction() {
			this.setText(_("Refresh"));
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {

			busyDialogWorker = new BusyDialogWorker(ChooseExperimentPanel.this);
			worker = new GetOpenBCIExperimentsWorker(applicationConfiguration);
			busyDialogWorker.execute();

			worker.addPropertyChangeListener(this);
			worker.execute();
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			try {
				if (((StateValue) evt.getNewValue()) == StateValue.DONE) {
					List<ExperimentDescriptor> experiments = worker.get();
					chooseExperimentTableModel.setExperiments(experiments);
					busyDialogWorker.cancel();
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
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
	
}


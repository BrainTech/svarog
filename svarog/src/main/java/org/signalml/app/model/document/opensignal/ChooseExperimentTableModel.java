package org.signalml.app.model.document.opensignal;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.signalml.app.view.document.opensignal.elements.AmplifierChannels;

public class ChooseExperimentTableModel extends AbstractTableModel {

	public static final int EXPERIMENT_NAME = 0;
	public static final int AMPLIFIER_NAME = 1;
	public static final int EXPERIMENT_STATUS = 2;
	
	private List<ExperimentDescriptor> experiments = new ArrayList<ExperimentDescriptor>();

	public ChooseExperimentTableModel() {
		
	}

	public List<ExperimentDescriptor> getExperiments() {
		return experiments;
	}

	public void setExperiments(List<ExperimentDescriptor> experiments) {
		this.experiments = experiments;
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return experiments.size();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		ExperimentDescriptor experiment = experiments.get(rowIndex);
		switch(columnIndex) {
			case EXPERIMENT_NAME: return experiment.getExperimentName();
			case EXPERIMENT_STATUS: return experiment.getExperimentStatus();
			case AMPLIFIER_NAME: return experiment.getAmplifier().getName();
			default: return null;
		}
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
			case EXPERIMENT_NAME: return _("Experiment");
			case AMPLIFIER_NAME: return _("Amplifier");
			case EXPERIMENT_STATUS: return _("Status");
			default: return "";
		}
	}

}

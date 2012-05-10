package org.signalml.app.model.document.opensignal.elements;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;

public class ChooseExperimentTableModel extends AbstractTableModel {

	public static Color NEW_EXPERIMENT_COLOR = new Color(255, 228, 196);;
	public static Color RUNNING_EXPERIMENT_COLOR = new Color(144, 238, 144);
	public static Color SELECTED_EXPERIMENT_COLOR = new Color(240, 120, 71);

	public static final int EXPERIMENT_NAME = 0;
	public static final int AMPLIFIER_NAME = 1;
	public static final int AMPLIFIER_TYPE = 2;
	public static final int EXPERIMENT_STATUS = 3;

	private List<ExperimentDescriptor> experiments = new ArrayList<ExperimentDescriptor>();

	public ChooseExperimentTableModel() {
	}

	public List<ExperimentDescriptor> getExperiments() {
		return experiments;
	}

	public Color getRowColor(int row, boolean isSelected) {
		if (isSelected)
			return SELECTED_EXPERIMENT_COLOR;

		if (experiments.get(row).getStatus() == ExperimentStatus.NEW)
			return NEW_EXPERIMENT_COLOR;
		else
			return RUNNING_EXPERIMENT_COLOR;
	}

	public void setExperiments(List<ExperimentDescriptor> experiments) {
		if (experiments == null)
			this.experiments.clear();
		else
			this.experiments = experiments;
		fireTableDataChanged();
	}

	public void clearExperiments() {
		this.experiments.clear();
		fireTableDataChanged();
	}

	public void addExperiments(List<ExperimentDescriptor> newExperiments) {
		this.experiments.addAll(newExperiments);

		int lastRow = this.experiments.size() - 1;
		int firstRow = lastRow - newExperiments.size() + 1;
		fireTableRowsInserted(firstRow, lastRow);
	}

	@Override
	public int getRowCount() {
		return experiments.size();
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		ExperimentDescriptor experiment = experiments.get(rowIndex);
		switch (columnIndex) {
		case EXPERIMENT_NAME:
			return experiment.getName();
		case EXPERIMENT_STATUS:
			return experiment.getStatus();
		case AMPLIFIER_NAME:
			return experiment.getAmplifier().getName();
		case AMPLIFIER_TYPE:
			return experiment.getAmplifier().getAmplifierType();
		default:
			return null;
		}
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case EXPERIMENT_NAME:
			return _("Experiment");
		case AMPLIFIER_NAME:
			return _("Amplifier");
		case EXPERIMENT_STATUS:
			return _("Status");
		case AMPLIFIER_TYPE:
			return _("Amplifier type");
		default:
			return "";
		}
	}

}

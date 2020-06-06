package org.signalml.app.model.document.opensignal.elements;

import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import static org.signalml.app.util.i18n.SvarogI18n._;

public class ChooseAmplifierTableModel extends ChooseExperimentTableModel{
	public static final int AMPLIFIER_NAME = 0;
	public static final int AMPLIFIER_TYPE = 1;
	
	@Override
	public int getColumnCount() {
		return 2;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		ExperimentDescriptor experiment = experiments.get(rowIndex);
		switch (columnIndex) {
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
		case AMPLIFIER_NAME:
			return _("Amplifier");
		case AMPLIFIER_TYPE:
			return _("Amplifier type");
		default:
			return "";
		}
	}
}

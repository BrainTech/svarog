package org.signalml.app.view.document.opensignal.elements;

import javax.swing.event.ListSelectionEvent;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.elements.ChooseAmplifierTableModel;
import org.signalml.app.model.document.opensignal.elements.ChooseExperimentTableModel;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.worker.monitor.FindAmplifiersWorker;
import org.signalml.app.worker.monitor.FindEEGExperimentsWorker;

/**
 *
 * @author Marian Dovgialo
 */
public class ChooseAmplifierPanel extends ChooseExperimentPanel{
	public static String AMPLIFIER_SELECTED_PROPERTY = "amplifierSelectedProperty";

	
	@Override
	public FindEEGExperimentsWorker getWorker(){
		return new FindAmplifiersWorker();
	}

	//private methods are not 'virtual' in java
	protected ChooseExperimentTableModel getTableModel()
	{
		return new ChooseAmplifierTableModel();
	}
	
	protected void setTitledBorder()
	{
		super.setTitledBorder(_("Choose amplifier"));
	}
	
	public ExperimentDescriptor getSelectedAmplifier() {
		return getSelectedExperiment();
	}
	
	protected void fireAmplifierSelected(ExperimentDescriptor experiment) {
		this.firePropertyChange(AMPLIFIER_SELECTED_PROPERTY, null, experiment);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		fireAmplifierSelected(getSelectedExperiment());
	}
}

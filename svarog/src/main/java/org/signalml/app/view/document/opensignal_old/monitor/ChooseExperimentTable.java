package org.signalml.app.view.document.opensignal_old.monitor;

import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.signalml.app.model.document.opensignal.ChooseExperimentTableModel;

public class ChooseExperimentTable extends JTable {

	public ChooseExperimentTable(ChooseExperimentTableModel model) {
		super(model);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
}

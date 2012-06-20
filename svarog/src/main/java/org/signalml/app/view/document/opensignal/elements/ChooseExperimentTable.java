package org.signalml.app.view.document.opensignal.elements;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.signalml.app.model.document.opensignal.elements.ChooseExperimentTableModel;

public class ChooseExperimentTable extends JTable {

	private TableCellRenderer tableCellRenderer = new ChooseExperimentsTableCellRenderer();;

	public ChooseExperimentTable(ChooseExperimentTableModel model) {
		super(model);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		return tableCellRenderer;
	}
}

class ChooseExperimentsTableCellRenderer extends DefaultTableCellRenderer {
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

		ChooseExperimentTableModel tableModel = (ChooseExperimentTableModel) table.getModel();

		label.setBackground(tableModel.getRowColor(row, isSelected));
		if (isSelected) {
			label.setForeground(Color.white);
		} else {
			label.setForeground(Color.black);

		}

		return label;
	}
}
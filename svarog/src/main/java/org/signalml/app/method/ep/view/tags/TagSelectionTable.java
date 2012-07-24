package org.signalml.app.method.ep.view.tags;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.signalml.app.model.components.table.AbstractSelectionTableModel;
import org.signalml.app.view.tag.TagIconProducer;
import org.signalml.plugin.export.signal.TagStyle;

public class TagSelectionTable extends JTable {

	private TagIconProducer tagIconProducer = new TagIconProducer();

	public TagSelectionTable(AbstractSelectionTableModel tableModel) {
		super(tableModel);

		setPreferredScrollableViewportSize(new Dimension(120, 200));
		setColumnsPreferredSizes();

		this.getTableHeader().setReorderingAllowed(false);
	}

	private void setColumnsPreferredSizes() {
		TableColumn column;
		for (int i = 0; i < getColumnCount(); i++) {
			column = getColumnModel().getColumn(i);
			if (i == TagSelectionTableModel.TAG_CHECKBOX_COLUMN_NUMBER) {
				column.setPreferredWidth(10);
			} else {
				column.setPreferredWidth(300);
			}
		}
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		if (column == TagSelectionTableModel.TAG_STYLE_NAME_COLUMN_NUMBER)
			return new TagRenderer(tagIconProducer);
		else
			return super.getCellRenderer(row, column);
	}

}

class TagRenderer extends DefaultTableCellRenderer {

	private TagIconProducer iconProducer;

	public TagRenderer(TagIconProducer tagIconProducer) {
		this.iconProducer = tagIconProducer;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		if (column == 0) {
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 1));

		if (isSelected) {
			panel.setBackground(table.getSelectionBackground());
		}

		if (value instanceof TagStyleGroup) {
			TagStyleGroup group = (TagStyleGroup) value;
			List<TagStyle> tagStyles = group.getTagStyles();

			for (int i = 0; i < group.getNumberOfTagStyles(); i++) {
				TagStyle tagStyle = tagStyles.get(i);
				JLabel label = new JLabel(tagStyle.getName());
				label.setIcon(iconProducer.getIcon(tagStyle));
				panel.add(label);

				if (i < group.getNumberOfTagStyles()-1) {
					panel.add(new JLabel("&"));
				}
			}
		}

		return panel;
	}

}
package org.signalml.app.view.tag.styles.attributes;

import javax.swing.table.AbstractTableModel;

import org.signalml.app.view.components.TagStylePropertiesPanel;
import org.signalml.plugin.export.signal.tagStyle.TagStyleAttributeDefinition;
import org.signalml.plugin.export.signal.tagStyle.TagStyleAttributes;

/**
 * The table model for tag attributes table.
 *
 * @author Piotr Szachewicz
 */
public class TagAttributesDefinitionsTableModel extends AbstractTableModel {

	/**
	 * The panel for editing tag style properties.
	 */
	private TagStylePropertiesPanel tagStylePropertiesPanel;
	/**
	 * The attributes definitions of the current tag style.
	 */
	private TagStyleAttributes tagStyleAttributes;

	/**
	 * Returns the tag style attributes definitions which are used by this
	 * model.
	 * @return tag style attributes definitions used by this model
	 */
	public TagStyleAttributes getTagStyleAttributes() {
		return tagStyleAttributes;
	}

	/**
	 * Sets the tag style properties panel to be used by this model. It is
	 * informed whenever the tag attributes definitions are changed.
	 * @param tagStylePropertiesPanel
	 */
	public void setTagStylePropertiesPanel(TagStylePropertiesPanel tagStylePropertiesPanel) {
		this.tagStylePropertiesPanel = tagStylePropertiesPanel;
	}

	/**
	 * Sets the data that will be used by this model.
	 * @param attributes attributes definitions that will be used by this model
	 */
	public void setData(TagStyleAttributes attributes) {
		this.tagStyleAttributes = attributes;
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		if (tagStyleAttributes == null) {
			return 0;
		}
		return tagStyleAttributes.getSize();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		TagStyleAttributeDefinition attributeDefinition = tagStyleAttributes.getAttributeDefinition(rowIndex);
		switch (columnIndex) {
		case 0:
			return attributeDefinition.getCode();
		case 1:
			return attributeDefinition.getDisplayName();
		case 2:
			return attributeDefinition.isVisible();
		}

		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == 2) {
			Boolean b = (Boolean) aValue;
			tagStyleAttributes.getAttributeDefinition(rowIndex).setVisible(b);
		}
		else if (columnIndex == 1) {
			String s = (String) aValue;
			tagStyleAttributes.getAttributeDefinition(rowIndex).setDisplayName(s);
		}
		if (tagStylePropertiesPanel != null)
			tagStylePropertiesPanel.setChanged(true);
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "code";
		case 1:
			return "display";
		case 2:
			return "visible";
		}
		return "";
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 2) {
			return Boolean.class;
		} else {
			return super.getColumnClass(columnIndex);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 2 || columnIndex == 1) {
			return true;
		} else {
			return false;
		}
	}

}

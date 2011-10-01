/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.signalml.app.view.tag.styles.attributes;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import org.signalml.app.view.element.TagStylePropertiesPanel;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.plugin.export.signal.tagStyle.TagStyleAttributeDefinition;
import org.signalml.plugin.export.signal.tagStyle.TagStyleAttributes;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class TagAttributesDefinitionsEditPanel extends JPanel {

	private TagStylePropertiesPanel tagStylePropertiesPanel;
	protected MessageSourceAccessor messageSource;
	private JTable table;
	private TagAttributesDefinitionsTableModel tableModel;

	public TagAttributesDefinitionsEditPanel(MessageSourceAccessor messageSource, TagStylePropertiesPanel tagStylePropertiesPanel) {
		this.messageSource = messageSource;
		this.tagStylePropertiesPanel = tagStylePropertiesPanel;
		initialize();
	}

	private void initialize() {
		this.setLayout(new BorderLayout(10, 10));
		this.setBorder(new TitledBorder("Tag attributes"));

		table = new JTable();
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);

		tableModel = new TagAttributesDefinitionsTableModel();

		this.add(scrollPane, BorderLayout.CENTER);
	}

	public void fillPanelFromModel(TagStyle style) {
		TagStyleAttributes attributesDefinitions;

		if (style == null) {
			attributesDefinitions = new TagStyleAttributes();
		} else {
			attributesDefinitions = style.getAttributesDefinitions().clone();
		}

		tableModel.setData(attributesDefinitions);
		tableModel.setTagStylePropertiesPanel(tagStylePropertiesPanel);
		table.setModel(tableModel);
	}

	public void fillModelFromPanel(TagStyle style) {
		TagStyleAttributes oldDefinitions = style.getAttributesDefinitions();
		TagStyleAttributes newDefinitions = tableModel.getTagStyleAttributes();

		int i = 0;
		for (; i < newDefinitions.getSize(); i++) {
			TagStyleAttributeDefinition newDefinition = newDefinitions.getAttributeDefinition(i);
			TagStyleAttributeDefinition oldDefinition = oldDefinitions.getAttributeDefinition(i);

			//we assume that nobody will change the order of elements
			if (!newDefinition.equals(oldDefinition)) {
				oldDefinition.copyFrom(newDefinition);
			}
		}

		for (; i < oldDefinitions.getSize(); i++) {
			style.getAttributesDefinitions().removeAttributeDefinition(i);
		}
	}
}

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
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.plugin.export.signal.tagStyle.TagStyleAttributes;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class TagAttributesDefinitionsEditPanel extends JPanel {

	protected MessageSourceAccessor messageSource;
	private JTable table;
	private TagAttributesDefinitionsTableModel tableModel;

	public TagAttributesDefinitionsEditPanel(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
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
			attributesDefinitions = style.getAttributesDefinitions();
		}

		tableModel.setData(attributesDefinitions);
		table.setModel(tableModel);
	}

	public void fillModelFromPanel(TagStyle style) {
		style.setAttributesDefinitions(tableModel.getTagStyleAttributes());
	}
}

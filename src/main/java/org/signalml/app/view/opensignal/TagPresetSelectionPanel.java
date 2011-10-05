/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.signalml.app.view.opensignal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.signalml.app.config.preset.StyledTagSetPresetManager;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.signalml.app.view.element.AbstractSignalMLPanel;
import org.signalml.domain.tag.StyledTagSet;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class TagPresetSelectionPanel extends AbstractSignalMLPanel {

	private final StyledTagSetPresetManager styledTagSetPresetManager;
	private JComboBox presetComboBox;

	public TagPresetSelectionPanel(MessageSourceAccessor messageSource, StyledTagSetPresetManager styledTagSetPresetManager) {
		super(messageSource);
		this.styledTagSetPresetManager = styledTagSetPresetManager;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout(10, 10));

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(messageSource.getMessage("opensignal.tagPresetSelectionPanelTitle")),
			new EmptyBorder(3, 3, 3, 3));
		setBorder(border);

		this.add(getPresetComboBox());

	}

	public void fillModelFromPanel(OpenMonitorDescriptor descriptor) {
		StyledTagSet selectedStylesPreset = (StyledTagSet) getPresetComboBox().getSelectedItem();
		descriptor.setTagStyles(selectedStylesPreset);
	}

	public JComboBox getPresetComboBox() {
		if (presetComboBox == null) {
			TagPresetComboBoxModel model = new TagPresetComboBoxModel(styledTagSetPresetManager);
			presetComboBox = new JComboBox(model);
		}
		return presetComboBox;
	}
}

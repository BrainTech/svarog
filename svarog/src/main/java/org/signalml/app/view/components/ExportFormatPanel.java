/* ExportSignalOptionsPanel.java created 2008-01-27
 *
 */
package org.signalml.app.view.components;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.domain.signal.ExportFormatType;
import org.signalml.app.view.components.ExportSignalOptionsPanel;
import org.signalml.app.view.components.ResolvableComboBox;

public class ExportFormatPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger
			.getLogger(ExportFormatPanel.class);

	private ResolvableComboBox formatComboBox;

	private JPanel optionsPanel;

	private ExportSignalOptionsPanel rawOptionsPanel;

	private ASCIIOptionsPanel asciiOptionsPanel;

	private EEGLabOptionsPanel eegLabOptionsPanel;

	/**
	 * Constructor. Initializes the panel.
	 */
	public ExportFormatPanel() {
		super();
		initialize();
	}

	private void initialize() {

		setBorder(new CompoundBorder(new TitledBorder(
				_("Export format options")), new EmptyBorder(3, 3, 3, 3)));

		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.weightx = 1;
		c.insets = new Insets(0, 2, 0, 2);

		JLabel formatLabel = new JLabel(_("Export format"));

		add(formatLabel, c);
		c.weightx = 1;
		c.gridx = 1;
		add(getFormatComboBox(), c);

		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 2;

		add(getOptionsPanel(), c);

		getFormatComboBox().setSelectedIndex(0);

	}

	public ExportSignalOptionsPanel getRawOptionsPanel() {
		if (rawOptionsPanel == null) {
			rawOptionsPanel = new ExportSignalOptionsPanel();
		}
		return rawOptionsPanel;
	}

	public ASCIIOptionsPanel getASCIIOptionsPanel() {
		if (asciiOptionsPanel == null) {
			asciiOptionsPanel = new ASCIIOptionsPanel(_("Separator: "));
		}
		return asciiOptionsPanel;
	}

	public EEGLabOptionsPanel getEEGLabOptionsPanel() {
		if (eegLabOptionsPanel == null) {
			eegLabOptionsPanel = new EEGLabOptionsPanel(_("Export tags"));
		}
		return eegLabOptionsPanel;
	}

	public JPanel getOptionsPanel() {
		if (optionsPanel == null) {
			optionsPanel = new JPanel(new CardLayout());
			optionsPanel.add(getRawOptionsPanel(),
					ExportFormatType.RAW.toString());
			optionsPanel.add(getASCIIOptionsPanel(),
					ExportFormatType.ASCII.toString());
			optionsPanel.add(getEEGLabOptionsPanel(),
					ExportFormatType.EEGLab.toString());
		}
		return optionsPanel;
	}

	public ResolvableComboBox getFormatComboBox() {
		if (formatComboBox == null) {
			formatComboBox = new ResolvableComboBox();
			formatComboBox.setModel(new DefaultComboBoxModel(ExportFormatType
					.values()));
			formatComboBox.setPreferredSize(new Dimension(80, 25));
			formatComboBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					CardLayout cl = (CardLayout) (getOptionsPanel().getLayout());
					cl.show(getOptionsPanel(),
							((ExportFormatType) e.getItem()).toString());
				}

			});

		}
		return formatComboBox;
	}

	/**
	 * Fills the fields of this panel from the given
	 * {@link SignalExportDescriptor export descriptor}.
	 * 
	 * @param descriptor
	 *            the export descriptor
	 */
	public void fillPanelFromModel(SignalExportDescriptor descriptor) {
		getFormatComboBox().setSelectedItem(descriptor.getFormatType());
		getASCIIOptionsPanel().fillPanelFromModel(descriptor);
		getRawOptionsPanel().fillPanelFromModel(descriptor);
	}

	/**
	 * Fills the given {@link SignalExportDescriptor export descriptor} with the
	 * user input from this panel.
	 * 
	 * @param descriptor
	 *            the descriptor to be filled
	 */
	public void fillModelFromPanel(SignalExportDescriptor descriptor) {
		ExportFormatType selectedFormatType = (ExportFormatType) getFormatComboBox()
				.getSelectedItem();
		descriptor.setFormatType(selectedFormatType);
		if (ExportFormatType.ASCII == selectedFormatType)
			getASCIIOptionsPanel().fillModelFromPanel(descriptor);
		else if (ExportFormatType.RAW == selectedFormatType)
			getRawOptionsPanel().fillModelFromPanel(descriptor);
		else
			getEEGLabOptionsPanel().fillModelFromPanel(descriptor);

	}

	public ExportFormatType getFormatType() {
		return (ExportFormatType) getFormatComboBox().getSelectedItem();
	}

	/**
	 * Validates this panel. Panel is always valid.
	 * 
	 * @param errors
	 *            the object in which errors should be stored
	 */
	public void validatePanel(ValidationErrors errors) {

		// nothing to do

	}

}

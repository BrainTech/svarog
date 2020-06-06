/* ExportSignalOptionsPanel.java created 2008-01-27
 *
 */
package org.signalml.app.view.signal.export;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import org.apache.log4j.Logger;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.signal.SignalExportDescriptor;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.components.ResolvableComboBox;
import org.signalml.app.view.common.components.panels.ComponentWithLabel;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;


/**
 * The panel that allows to select:
 * <ul>
 * <li>the {@link RawSignalSampleType type} of the exported sample
 * (short - 16 bit, integer - 32, float - 32, double - 64),</li>
 * <li>the {@link RawSignalByteOrder order} of bytes (little of big endian),</li>
 * <li>if the samples should be normalized,</li>
 * <li>if the information about the parameters of the stored signal should be
 * saved to an XML file.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RawExportOptionsPanel extends AbstractExportOptionsPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(RawExportOptionsPanel.class);

	/**
	 * the combo-box which allows to select the {@link RawSignalSampleType type}
	 * of the exported sample (short - 16 bit, integer - 32, float - 32,
	 * double - 64)
	 */
	private ResolvableComboBox sampleTypeComboBox;
	/**
	 * the combo-box which allows to select the {@link RawSignalByteOrder order}
	 * of bytes (little of big endian)
	 */
	private ResolvableComboBox byteOrderComboBox;
	/**
	 * the check-box which tells if the information about the parameters of
	 * the stored signal should be saved to an XML file
	 */
	private JCheckBox saveXMLCheckBox;

	/**
	 * the check-box which tells if the samples should be normalized
	 */
	private JCheckBox normalizeCheckBox;

	/**
	 * the last value of {@link #normalizeCheckBox}
	 */
	private boolean lastNormalize;

	/**
	 * Returns the combo-box which allows to select the
	 * {@link RawSignalSampleType type} of the exported sample
	 * (short - 16 bit, integer - 32, float - 32, double - 64).
	 * If the combo-box doesn't exist it is created and the listener is added
	 * to it. The listener:
	 * <ul>
	 * <li>if the type of sample is {@code INT} or {@code SHORT} enables the
	 * {@link #getNormalizeCheckBox() normalize check-box} and sets its value
	 * to the remembered value,</li>
	 * <li>otherwise remembers the value of the {@link #getNormalizeCheckBox()
	 * normalize check-box} and disables it.</li></ul>
	 * @return the combo-box which allows to select the type of the sample
	 */
	public ResolvableComboBox getSampleTypeComboBox() {
		if (sampleTypeComboBox == null) {
			sampleTypeComboBox = new ResolvableComboBox();
			sampleTypeComboBox.setModel(new DefaultComboBoxModel(RawSignalSampleType.values()));
			sampleTypeComboBox.setPreferredSize(new Dimension(80,25));

			sampleTypeComboBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {

						JCheckBox normalizeCB = getNormalizeCheckBox();
						RawSignalSampleType sampleType = (RawSignalSampleType) sampleTypeComboBox.getSelectedItem();

						if (sampleType == RawSignalSampleType.INT || sampleType == RawSignalSampleType.SHORT) {
							if (!normalizeCB.isEnabled()) {
								normalizeCB.setEnabled(true);
								normalizeCB.setSelected(lastNormalize);
							}
						} else {
							if (normalizeCB.isEnabled()) {
								lastNormalize = normalizeCB.isSelected();
								normalizeCB.setSelected(false);
								normalizeCB.setEnabled(false);
							}
						}

					}
				}

			});

		}
		return sampleTypeComboBox;
	}

	/**
	 * Returns the combo-box which allows to select the
	 * {@link RawSignalByteOrder order} of bytes (little of big endian).
	 * If the combo-box doesn't exist it is created.
	 * @return the combo-box which allows to select the order of bytes
	 */
	public ResolvableComboBox getByteOrderComboBox() {
		if (byteOrderComboBox == null) {
			byteOrderComboBox = new ResolvableComboBox();
			byteOrderComboBox.setModel(new DefaultComboBoxModel(RawSignalByteOrder.values()));
			byteOrderComboBox.setPreferredSize(new Dimension(80,25));
		}
		return byteOrderComboBox;
	}

	/**
	 * Returns the check-box which tells if the information about the
	 * parameters of the stored signal should be saved to an XML file.
	 * If the check-box doesn't exist it is created.
	 * @return the check-box which tells if the information about the
	 * parameters of the stored signal should be saved to an XML file
	 */
	public JCheckBox getSaveXMLCheckBox() {
		if (saveXMLCheckBox == null) {
			saveXMLCheckBox = new JCheckBox();
		}
		return saveXMLCheckBox;
	}

	/**
	 * Returns the check-box which tells if the samples should be normalized.
	 * If the check-box doesn't exist it is created.
	 * @return the check-box which tells if the samples should be normalized
	 */
	public JCheckBox getNormalizeCheckBox() {
		if (normalizeCheckBox == null) {
			normalizeCheckBox = new JCheckBox();
		}
		return normalizeCheckBox;
	}

	@Override
	public void fillPanelFromModel(SignalExportDescriptor descriptor) {

		getSampleTypeComboBox().setSelectedItem(descriptor.getSampleType());
		getByteOrderComboBox().setSelectedItem(descriptor.getByteOrder());

		getSaveXMLCheckBox().setSelected(descriptor.isSaveXML());
		getNormalizeCheckBox().setSelected(descriptor.isNormalize());

	}

	@Override
	public void fillModelFromPanel(SignalExportDescriptor descriptor) {

		descriptor.setSampleType((RawSignalSampleType) getSampleTypeComboBox().getSelectedItem());
		descriptor.setByteOrder((RawSignalByteOrder) getByteOrderComboBox().getSelectedItem());

		descriptor.setSaveXML(getSaveXMLCheckBox().isSelected());
		descriptor.setNormalize(getNormalizeCheckBox().isSelected());

	}


	/**
	 * Validates this panel. Panel is always valid.
	 * @param errors the object in which errors should be stored
	 */
	@Override
	public void validatePanel(ValidationErrors errors) {
		// nothing to do
	}

	@Override
	protected List<ComponentWithLabel> createComponents() {

		List<ComponentWithLabel> components = new ArrayList<ComponentWithLabel>();

		components.add(new ComponentWithLabel(new JLabel(_("Sample type")), getSampleTypeComboBox()));
		components.add(new ComponentWithLabel(new JLabel(_("Byte order")), getByteOrderComboBox()));
		components.add(new ComponentWithLabel(new JLabel(_("Save XML manifest")), getSaveXMLCheckBox()));
		components.add(new ComponentWithLabel(new JLabel(_("Normalize samples")), getNormalizeCheckBox()));

		return components;
	}

	@Override
	protected int getNumberOfColumns() {
		return 2;
	}

}

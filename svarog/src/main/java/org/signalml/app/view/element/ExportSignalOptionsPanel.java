/* ExportSignalOptionsPanel.java created 2008-01-27
 *
 */
package org.signalml.app.view.element;

import static org.signalml.app.SvarogApplication._;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.model.SignalExportDescriptor;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;

import org.springframework.validation.Errors;


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
public class ExportSignalOptionsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ExportSignalOptionsPanel.class);

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
	 * Constructor. Initializes the panel.
	 */
	public ExportSignalOptionsPanel() {
		super();
		initialize();
	}

	/**
	 * Adds the elements to this panel using the group layout.
	 * Elements are arranged in four columns and two rows:
	 * <ul>
	 * <li>first and third column contain labels for combo- or check-boxes,</li>
	 * <li>second and fourth column contain combo- or check-boxes,</li>
	 * <li>In first row there are two combo-boxes (and labels for them)
	 * (from left):
	 * <ul><li>the {@link #sampleTypeComboBox combo-box} which allows to
	 * select the {@link RawSignalSampleType type} of the exported sample
	 * (short - 16 bit, integer - 32, float - 32, double - 64),</li>
	 * <li>the {@link #byteOrderComboBox combo-box} which allows to select the
	 * {@link RawSignalByteOrder order} of bytes (little of big endian).</li>
	 * </ul><li>
	 * In second row there are two check-boxes (with labels for them)
	 * (from left):
	 * <ul><li>the {@link #saveXMLCheckBox check-box} which tells if the
	 * information about the parameters of the stored signal should be
	 * saved to an XML file,</li>
	 * <li>the {@link #normalizeCheckBox check-box} which tells if the
	 * samples should be normalized.</li></ul></ul>
	 */
	private void initialize() {

		setBorder(new CompoundBorder(
		                  new TitledBorder(_("Export options")),
		                  new EmptyBorder(3,3,3,3)
		          ));

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel sampleTypeLabel = new JLabel(_("Sample type"));
		JLabel byteOrderLabel = new JLabel(_("Byte order"));
		JLabel saveXMLLabel = new JLabel(_("Save XML manifest"));
		JLabel normalizeLabel = new JLabel(_("Normalize samples"));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
		        layout.createParallelGroup(Alignment.LEADING)
		        .addComponent(sampleTypeLabel)
		        .addComponent(normalizeLabel)
		);

		hGroup.addGroup(
		        layout.createParallelGroup(Alignment.TRAILING)
		        .addComponent(getSampleTypeComboBox())
		        .addComponent(getNormalizeCheckBox())
		);

		hGroup.addGroup(
		        layout.createParallelGroup(Alignment.LEADING)
		        .addComponent(byteOrderLabel)
		        .addComponent(saveXMLLabel)
		);

		hGroup.addGroup(
		        layout.createParallelGroup(Alignment.TRAILING)
		        .addComponent(getByteOrderComboBox())
		        .addComponent(getSaveXMLCheckBox())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(sampleTypeLabel)
				.addComponent(getSampleTypeComboBox())
				.addComponent(byteOrderLabel)
				.addComponent(getByteOrderComboBox())
			);
		
		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(normalizeLabel)
				.addComponent(getNormalizeCheckBox())
				.addComponent(saveXMLLabel)
				.addComponent(getSaveXMLCheckBox())
			);
		
		layout.setVerticalGroup(vGroup);		

	}

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

	/**
	 * Fills the fields of this panel from the given
	 * {@link SignalExportDescriptor export descriptor}.
	 * @param descriptor the export descriptor
	 */
	public void fillPanelFromModel(SignalExportDescriptor descriptor) {

		getSampleTypeComboBox().setSelectedItem(descriptor.getSampleType());
		getByteOrderComboBox().setSelectedItem(descriptor.getByteOrder());

		getSaveXMLCheckBox().setSelected(descriptor.isSaveXML());
		getNormalizeCheckBox().setSelected(descriptor.isNormalize());

	}

	/**
	 * Fills the given {@link SignalExportDescriptor export descriptor}
	 * with the user input from this panel.
	 * @param descriptor the descriptor to be filled
	 */
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
	public void validatePanel(Errors errors) {

		// nothing to do

	}

}

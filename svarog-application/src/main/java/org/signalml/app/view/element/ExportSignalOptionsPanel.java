/* ExportSignalOptionsPanel.java created 2008-01-27
 * 
 */
package org.signalml.app.view.element;

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
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** ExportSignalOptionsPanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ExportSignalOptionsPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ExportSignalOptionsPanel.class);
	
	private MessageSourceAccessor messageSource;
	
	private ResolvableComboBox sampleTypeComboBox;
	private ResolvableComboBox byteOrderComboBox;
	private JCheckBox saveXMLCheckBox;
	private JCheckBox normalizeCheckBox;
	
	private boolean lastNormalize;
		
	public ExportSignalOptionsPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {
		
		setBorder( new CompoundBorder( 
				new TitledBorder( messageSource.getMessage( "exportSignalDialog.options.title" ) ),
				new EmptyBorder( 3,3,3,3 )
		));
		
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel sampleTypeLabel = new JLabel(messageSource.getMessage("exportSignalDialog.options.sampleType"));
		JLabel byteOrderLabel = new JLabel(messageSource.getMessage("exportSignalDialog.options.byteOrder"));
		JLabel saveXMLLabel = new JLabel(messageSource.getMessage("exportSignalDialog.options.saveXML"));		
		JLabel normalizeLabel = new JLabel(messageSource.getMessage("exportSignalDialog.options.normalize"));
		
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
			
	public ResolvableComboBox getSampleTypeComboBox() {
		if( sampleTypeComboBox == null ) {
			sampleTypeComboBox = new ResolvableComboBox(messageSource);
			sampleTypeComboBox.setModel( new DefaultComboBoxModel( RawSignalSampleType.values() ) );
			sampleTypeComboBox.setPreferredSize( new Dimension(80,25) );
			
			sampleTypeComboBox.addItemListener( new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if( e.getStateChange() == ItemEvent.SELECTED ) {
						
						JCheckBox normalizeCB = getNormalizeCheckBox();
						RawSignalSampleType sampleType = (RawSignalSampleType) sampleTypeComboBox.getSelectedItem();
						
						if( sampleType == RawSignalSampleType.INT || sampleType == RawSignalSampleType.SHORT ) {
							if( !normalizeCB.isEnabled() ) {
								normalizeCB.setEnabled(true);
								normalizeCB.setSelected(lastNormalize);
							}
						} else {
							if( normalizeCB.isEnabled() ) {
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

	public ResolvableComboBox getByteOrderComboBox() {
		if( byteOrderComboBox == null ) {
			byteOrderComboBox = new ResolvableComboBox(messageSource);
			byteOrderComboBox.setModel( new DefaultComboBoxModel( RawSignalByteOrder.values() ) );
			byteOrderComboBox.setPreferredSize( new Dimension(80,25) );
		}
		return byteOrderComboBox;
	}

	public JCheckBox getSaveXMLCheckBox() {
		if( saveXMLCheckBox == null ) {
			saveXMLCheckBox = new JCheckBox();
		}
		return saveXMLCheckBox;
	}

	public JCheckBox getNormalizeCheckBox() {
		if( normalizeCheckBox == null ) {
			normalizeCheckBox = new JCheckBox();
		}
		return normalizeCheckBox;
	}
	
	public void fillPanelFromModel(SignalExportDescriptor descriptor) {
			
		getSampleTypeComboBox().setSelectedItem( descriptor.getSampleType() );
		getByteOrderComboBox().setSelectedItem( descriptor.getByteOrder() );
		
		getSaveXMLCheckBox().setSelected( descriptor.isSaveXML() );
		getNormalizeCheckBox().setSelected( descriptor.isNormalize() );
		
	}

	public void fillModelFromPanel(SignalExportDescriptor descriptor) {
		
		descriptor.setSampleType( (RawSignalSampleType) getSampleTypeComboBox().getSelectedItem() );
		descriptor.setByteOrder( (RawSignalByteOrder) getByteOrderComboBox().getSelectedItem() );
		
		descriptor.setSaveXML( getSaveXMLCheckBox().isSelected() );
		descriptor.setNormalize( getNormalizeCheckBox().isSelected() );
		
	}
	

	public void validatePanel(Errors errors) {
		
		// nothing to do
		
	}
	
}

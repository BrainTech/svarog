/* PagingParametersPanel.java created 2007-09-17
 * 
 */
package org.signalml.app.view.element;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.model.PagingParameterDescriptor;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** PagingParametersPanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class PagingParametersPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(PagingParametersPanel.class);
	
	private MessageSourceAccessor messageSource;
	
	private JTextField pageSizeField;
	private JTextField blocksPerPageField;
		
	/**
	 * This is the default constructor
	 */
	public PagingParametersPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
				
		CompoundBorder cb = new CompoundBorder(
			new TitledBorder(messageSource.getMessage("signalParameters.pagingSignalParameters")),
			new EmptyBorder(3,3,3,3)
		);
		
		setBorder(cb);
		
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel pageSizeLabel = new JLabel(messageSource.getMessage("pagingParameters.pageSize"));
		JLabel blocksPerPageLabel = new JLabel(messageSource.getMessage("pagingParameters.blocksPerPage"));
		
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		
		hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(pageSizeLabel)
				.addComponent(blocksPerPageLabel)
			);
		
		hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(getPageSizeField())
				.addComponent(getBlocksPerPageField())
			);
		
		layout.setHorizontalGroup(hGroup);
		
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		
		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
	            .addComponent(pageSizeLabel)
	            .addComponent(getPageSizeField())
			);
		
		vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
	            .addComponent(blocksPerPageLabel)
	            .addComponent(getBlocksPerPageField())
	    	);
		
		layout.setVerticalGroup(vGroup);		
		
	}

	public JTextField getPageSizeField() {
		if( pageSizeField == null ) {
			pageSizeField = new JTextField();
			pageSizeField.setPreferredSize(new Dimension(200,25));			
		}
		return pageSizeField;
	}

	public JTextField getBlocksPerPageField() {
		if( blocksPerPageField == null ) {
			blocksPerPageField = new JTextField();			
			blocksPerPageField.setPreferredSize(new Dimension(200,25));			
		}
		return blocksPerPageField;
	}
	
	public void fillPanelFromModel( PagingParameterDescriptor spd ) {
		
		Float pageSize = spd.getPageSize();
		if( pageSize != null ) {
			getPageSizeField().setText( pageSize.toString() );
		} else {
			getPageSizeField().setText( "" );
		}
		
		Integer blocksPerPage = spd.getBlocksPerPage();
		if( blocksPerPage != null ) {
			getBlocksPerPageField().setText( blocksPerPage.toString() );
		} else {
			getBlocksPerPageField().setText( "" );
		}
		
		if( spd.isPageSizeEditable() ) {
			getPageSizeField().setEditable(true);
			getPageSizeField().setToolTipText(null);
		} else {
			getPageSizeField().setEditable(false);
			getPageSizeField().setToolTipText(messageSource.getMessage("signalParameters.pagingNotEditable"));
		}
		
		if( spd.isBlocksPerPageEditable() ) {
			getBlocksPerPageField().setEditable(true);
			getBlocksPerPageField().setToolTipText(null);
		} else {
			getBlocksPerPageField().setEditable(false);
			getBlocksPerPageField().setToolTipText(messageSource.getMessage("signalParameters.pagingNotEditable"));
		}
		
	}
	
	public void fillModelFromPanel( PagingParameterDescriptor spd ) throws SignalMLException {
		try {
			if( spd.isPageSizeEditable() ) {
				spd.setPageSize( new Float(getPageSizeField().getText()) );
			}
			if( spd.isBlocksPerPageEditable() ) {
				spd.setBlocksPerPage( new Integer(getBlocksPerPageField().getText()) );
			}
		} catch( NumberFormatException ex ) {
			throw new SignalMLException(ex);
		}
	}
	
	public void fillPanelFromModel(RawSignalDescriptor descriptor) {

		getPageSizeField().setText( Float.toString( descriptor.getPageSize() ) );
		getBlocksPerPageField().setText( Integer.toString( descriptor.getBlocksPerPage() ) );
		
	}

	public void fillModelFromPanel(RawSignalDescriptor descriptor) {

		descriptor.setPageSize( Float.parseFloat( getPageSizeField().getText() ) );
		descriptor.setBlocksPerPage( Integer.parseInt( getBlocksPerPageField().getText() ) );
				
	}
	
	public void validatePanel( Errors errors ) {
		try {
			float pageSize = Float.parseFloat(getPageSizeField().getText());
			if( pageSize <= 0 ) {
				errors.rejectValue("pageSize", "error.pageSizeNegative");
			}
		} catch( NumberFormatException ex ) {
			errors.rejectValue("pageSize", "error.invalidNumber");
		}
		try {
			int blocksPerPage = Integer.parseInt(getBlocksPerPageField().getText());
			if( blocksPerPage <= 0 ) {
				errors.rejectValue("blocksPerPage", "error.blocksPerPageNegative");
			}
		} catch( NumberFormatException ex ) {
			errors.rejectValue("blocksPerPage", "error.invalidNumber");
		}
	}

			
}

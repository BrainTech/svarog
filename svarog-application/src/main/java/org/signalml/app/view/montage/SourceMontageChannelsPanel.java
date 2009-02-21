/* EditFixedMontagePanel.java created 2007-11-02
 * 
 */
package org.signalml.app.view.montage;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.montage.SourceMontageTableModel;
import org.signalml.domain.montage.SourceMontage;
import org.springframework.context.support.MessageSourceAccessor;

/** EditFixedMontagePanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SourceMontageChannelsPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	
	private SourceMontage montage;
	
	private SourceMontageTableModel sourceMontageTableModel;
	
	private SourceMontageTable sourceMontageTable;
	private JScrollPane sourceScrollPane;
			
	public SourceMontageChannelsPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {
		
		setLayout(new BorderLayout());
		CompoundBorder border = new CompoundBorder(
				new TitledBorder( messageSource.getMessage("sourceMontageTable.title") ),
				new EmptyBorder(3,3,3,3)
		);
		setBorder( border );
		
		add(getSourceScrollPane(), BorderLayout.CENTER);
				
	}	
			
	public SourceMontage getMontage() {
		return montage;
	}

	public void setMontage(SourceMontage montage) {
		if( this.montage != montage ) {
			this.montage = montage;
			getSourceMontageTableModel().setMontage(montage);
		}
	}

	public SourceMontageTableModel getSourceMontageTableModel() {
		if( sourceMontageTableModel == null ) {
			sourceMontageTableModel = new SourceMontageTableModel();
			sourceMontageTableModel.setMessageSource(messageSource);			
		}
		return sourceMontageTableModel;
	}

	public SourceMontageTable getSourceMontageTable() {
		if( sourceMontageTable == null ) {
			sourceMontageTable = new SourceMontageTable(getSourceMontageTableModel(), messageSource);
		}
		return sourceMontageTable;
	}

	public JScrollPane getSourceScrollPane() {
		if( sourceScrollPane == null ) {
			sourceScrollPane = new JScrollPane(getSourceMontageTable());
		}
		return sourceScrollPane;
	}
	
}

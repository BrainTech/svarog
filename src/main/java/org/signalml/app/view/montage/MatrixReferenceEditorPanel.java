/* EditMontageReferencePanel.java created 2007-10-24
 * 
 */
package org.signalml.app.view.montage;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.montage.ReferenceTableModel;
import org.signalml.domain.montage.Montage;
import org.springframework.context.support.MessageSourceAccessor;

/** EditMontageReferencePanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MatrixReferenceEditorPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	
	private Montage montage;
	
	private ReferenceTableModel referenceTableModel;
	private ReferenceTable referenceTable;
	private JScrollPane scrollPane;
			
	public MatrixReferenceEditorPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}
	
	private void initialize() {
		
		setLayout(new BorderLayout());
		
		CompoundBorder border = new CompoundBorder(
				new TitledBorder(  messageSource.getMessage("matrixReferenceEditor.editReference") ),
				new EmptyBorder(3,3,3,3)
		);		
		setBorder( border );
		
		add(getScrollPane(), BorderLayout.CENTER);
						
	}
			
	public Montage getMontage() {
		return montage;
	}

	public void setMontage(Montage montage) {
		if( this.montage != montage ) {
			this.montage = montage;
			getReferenceTableModel().setMontage(montage);
		}
	}

	public ReferenceTable getReferenceTable() {
		if( referenceTable == null ) {
			referenceTable = new ReferenceTable(getReferenceTableModel());
		}
		return referenceTable;
	}
		
	public ReferenceTableModel getReferenceTableModel() {
		if( referenceTableModel == null ) {
			referenceTableModel = new ReferenceTableModel();
		}
		return referenceTableModel;
	}

	public JScrollPane getScrollPane() {
		if( scrollPane == null ) {
			scrollPane = new JScrollPane(getReferenceTable());
		}
		return scrollPane;
	}
	
}

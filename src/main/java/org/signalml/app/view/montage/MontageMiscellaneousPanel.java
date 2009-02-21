/* EditMontageReferencePanel.java created 2007-10-24
 * 
 */
package org.signalml.app.view.montage;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import org.signalml.app.view.element.TextPanePanel;
import org.signalml.domain.montage.Montage;
import org.signalml.util.Util;
import org.springframework.context.support.MessageSourceAccessor;

/** EditMontageReferencePanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageMiscellaneousPanel extends JPanel implements PropertyChangeListener {
	
	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	
	private Montage montage;
	
	private TextPanePanel editDescriptionPanel;
			
	public MontageMiscellaneousPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}
	
	private void initialize() {
		
		setLayout(new GridLayout(3,1,3,3));
						
		add(getEditDescriptionPanel());
						
	}
		
	public Montage getMontage() {
		return montage;
	}

	public void setMontage(Montage montage) {
		if( this.montage != montage ) {
			if( this.montage != null ) {
				this.montage.removePropertyChangeListener(Montage.DESCRIPTION_PROPERTY, this);
			}
			this.montage = montage;
			if( montage != null ) {
				montage.addPropertyChangeListener(Montage.DESCRIPTION_PROPERTY, this);
				String text = montage.getDescription();
				editDescriptionPanel.getTextPane().setText( text != null ? text : "" );				
			}
		}
	}
	
	public TextPanePanel getEditDescriptionPanel() {
		if( editDescriptionPanel == null ) {
			editDescriptionPanel = new TextPanePanel( messageSource.getMessage("signalMontage.editDescription"));
			editDescriptionPanel.setPreferredSize(new Dimension(300,200));
			editDescriptionPanel.setMinimumSize(new Dimension(300,200));
			
			editDescriptionPanel.getTextPane().addFocusListener( new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					if( montage != null ) {
						String description = editDescriptionPanel.getTextPane().getText();
						if( description == null || description.isEmpty() || Util.validateString(description) ) {
							montage.setDescription( description );
						}						
					}
				}
			});
			
		}
		return editDescriptionPanel;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		if( Montage.DESCRIPTION_PROPERTY.equals(name) ) {
			String text = (String) evt.getNewValue();
			editDescriptionPanel.getTextPane().setText( text != null ? text : "" );
		}
	}	
	
}

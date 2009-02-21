/* MP5ExecutorListCellRenderer.java created 2008-02-08
 * 
 */

package org.signalml.app.method.mp5;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.signalml.method.mp5.MP5Executor;
import org.springframework.context.support.MessageSourceAccessor;

/** MP5ExecutorListCellRenderer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5ExecutorListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;
	
	private MP5Executor defaultExecutor;
	private Font normalFont;
	private Font boldFont;
	
	private MessageSourceAccessor messageSource;
	
	private String defaultString;
	
	public MP5ExecutorListCellRenderer( MessageSourceAccessor messageSource ) {
		super();
		this.messageSource = messageSource;
		normalFont = getFont().deriveFont(Font.PLAIN);
		boldFont = normalFont.deriveFont(Font.BOLD);
		defaultString = " " + messageSource.getMessage("mp5Method.config.defaultExecutor");
	}
	
	public MP5Executor getDefaultExecutor() {
		return defaultExecutor;
	}

	public void setDefaultExecutor(MP5Executor defaultExecutor) {
		this.defaultExecutor = defaultExecutor;
	}	

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		if( value instanceof MP5Executor ) {
			if( defaultExecutor != null && value == defaultExecutor ) {
				label.setText( messageSource.getMessage((MP5Executor) value) + defaultString );
				label.setFont(boldFont);
			} else {
				label.setText( messageSource.getMessage((MP5Executor) value) );
				label.setFont(normalFont);
			}
		}
		
		return label;
	}

}

/* ErrorListCellRenderer.java created 2007-09-24
 * 
 */

package org.signalml.app.view.element;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.signalml.app.util.IconUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;

/** ErrorListCellRenderer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ErrorListCellRenderer extends JPanel implements ListCellRenderer {

	// WARNING! this is broken, most likely hard and time consuming to fix
	
	private static final long serialVersionUID = 1L;
	
	protected static final Logger logger = Logger.getLogger(ErrorListCellRenderer.class);
	
	private JLabel label;
	private JTextArea textArea;
	
	private MessageSourceAccessor messageSource;
	
	public ErrorListCellRenderer(Dimension d) {
		super();
		setBorder(new EmptyBorder(3,3,3,3));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBackground(Color.WHITE);
		
		Icon icon = IconUtils.loadClassPathIcon("org/signalml/app/icon/error.png");			
		
		label = new JLabel();
		label.setIcon(icon);
		label.setText("");
		label.setVerticalAlignment(JLabel.CENTER);
		label.setAlignmentY(Component.CENTER_ALIGNMENT);
		label.setOpaque(false);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setAlignmentY(Component.CENTER_ALIGNMENT);
		textArea.setOpaque(false);
		textArea.setPreferredSize(new Dimension(400,30));
		
		add(label);
		add(Box.createHorizontalStrut(3));
		add(textArea);
						
	}

	@Override
	public boolean isOpaque() {
		return true;
	}
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		if( (index % 2) == 1 ) {
			setBackground(Color.YELLOW);
		} else {
			setBackground(Color.WHITE);
		}
		
		textArea.setText(messageSource.getMessage((MessageSourceResolvable) value));
		
		return this;
	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}
	
}

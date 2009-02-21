/* TagStyleListCellRenderer.java created 2008-01-14
 * 
 */

package org.signalml.app.view.tag;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.signalml.domain.tag.TagStyle;
import org.springframework.context.support.MessageSourceAccessor;

/** TagStyleListCellRenderer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStyleListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;
	private String noneString;

	public static final Color DISABLED_COLOR = new Color(220,220,220);
	
	private TagIconProducer tagIconProducer;
		
	public TagStyleListCellRenderer(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		
		noneString = messageSource.getMessage("tagStyle.none");		
	}
		
	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public TagIconProducer getTagIconProducer() {
		return tagIconProducer;
	}

	public void setTagIconProducer(TagIconProducer tagIconProducer) {
		this.tagIconProducer = tagIconProducer;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if( value instanceof TagStyle ) {
			TagStyle style = (TagStyle) value;
			
			if( style == null ) {
				label.setText(noneString);
				label.setIcon(null);
			} else {
				label.setText( style.getDescriptionOrName() );
				label.setIcon( tagIconProducer.getIcon(style) );
			}
		}
		
		return label;
	}
		
}

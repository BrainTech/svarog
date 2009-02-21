/* WignerMapPaletteComboBoxCellRenderer.java created 2008-03-06
 * 
 */

package org.signalml.app.view.book;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.springframework.context.support.MessageSourceAccessor;

/** WignerMapPaletteComboBoxCellRenderer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class WignerMapPaletteComboBoxCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	public WignerMapPaletteComboBoxCellRenderer(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		DefaultListCellRenderer renderer = (DefaultListCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if( value instanceof WignerMapPalette ) {
			WignerMapPalette palette = (WignerMapPalette) value;
			renderer.setText( messageSource.getMessage(palette) );
			renderer.setIcon( palette.getIcon() );
		}
		// else leave text put by superclass
		return renderer; 
	}
	
}

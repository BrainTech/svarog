/* WignerMapPaletteComboBoxCellRenderer.java created 2008-03-06
 *
 */

package org.signalml.app.view.book;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;


/** WignerMapPaletteComboBoxCellRenderer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class WignerMapPaletteComboBoxCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;
	public  WignerMapPaletteComboBoxCellRenderer() {
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		DefaultListCellRenderer renderer = (DefaultListCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof WignerMapPalette) {
			WignerMapPalette palette = (WignerMapPalette) value;
			renderer.setText(palette.i18n());
			renderer.setIcon(palette.getIcon());
		}
		// else leave text put by superclass
		return renderer;
	}

	/**
	 * Returns the {@link SvarogAccessI18nImpl} instance.
	 * @return the {@link SvarogAccessI18nImpl} singleton instance
	 */
	protected org.signalml.app.SvarogI18n getSvarogI18n() {
		return org.signalml.plugin.impl.SvarogAccessI18nImpl.getInstance();
	}
}

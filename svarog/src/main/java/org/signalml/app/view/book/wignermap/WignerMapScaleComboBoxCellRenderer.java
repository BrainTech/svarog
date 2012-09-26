/* WignerMapScaleComboBoxCellRenderer.java created 2008-03-06
 *
 */

package org.signalml.app.view.book.wignermap;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;

import org.signalml.app.util.IconUtils;
import org.signalml.domain.book.WignerMapScaleType;

/** WignerMapScaleComboBoxCellRenderer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class WignerMapScaleComboBoxCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;

	private Icon linearIcon;
	private Icon logIcon;
	private Icon sqrtIcon;

	public WignerMapScaleComboBoxCellRenderer() {
		linearIcon = IconUtils.loadClassPathIcon("org/signalml/app/icon/scalelinear.png");
		logIcon = IconUtils.loadClassPathIcon("org/signalml/app/icon/scalelog.png");
		sqrtIcon = IconUtils.loadClassPathIcon("org/signalml/app/icon/scalesqrt.png");
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		DefaultListCellRenderer renderer = (DefaultListCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof WignerMapScaleType) {
			WignerMapScaleType scale = (WignerMapScaleType) value;
			renderer.setText(scale.i18n());
			if (scale == WignerMapScaleType.NORMAL) {
				renderer.setIcon(linearIcon);
			}
			else if (scale == WignerMapScaleType.LOG) {
				renderer.setIcon(logIcon);
			}
			else if (scale == WignerMapScaleType.SQRT) {
				renderer.setIcon(sqrtIcon);
			} else {
				// else no icon
				renderer.setIcon(null);
			}
		}
		// else leave text put by superclass
		return renderer;
	}
}

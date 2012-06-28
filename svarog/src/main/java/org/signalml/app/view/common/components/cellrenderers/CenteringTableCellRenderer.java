/* CenteringCellRenderer.java created 2007-10-24
 *
 */

package org.signalml.app.view.common.components.cellrenderers;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Cell renderer for tables which centers the values in cells
 * (both horizontally and vertically).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class CenteringTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * Sets that the cells should be centered horizontally and vertically.
	 */
	public CenteringTableCellRenderer() {
		super();
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.CENTER);
	}

}

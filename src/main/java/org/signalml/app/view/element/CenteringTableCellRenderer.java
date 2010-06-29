/* CenteringCellRenderer.java created 2007-10-24
 *
 */

package org.signalml.app.view.element;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/** CenteringCellRenderer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class CenteringTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	public CenteringTableCellRenderer() {
		super();
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.CENTER);
	}

}

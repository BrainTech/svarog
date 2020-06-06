/* DateTableCellRenderer.java created 2007-10-19
 *
 */

package org.signalml.app.view.common.components.cellrenderers;

import java.util.Date;
import javax.swing.table.DefaultTableCellRenderer;
import org.signalml.util.FormatUtils;

/** DateTableCellRenderer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DateTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	protected void setValue(Object value) {
		setText(FormatUtils.formatTime((Date) value));
	}

}

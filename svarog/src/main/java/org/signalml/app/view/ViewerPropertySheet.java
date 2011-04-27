/* ViewerPropertySheet.java created 2007-09-11
 *
 */
package org.signalml.app.view;

import javax.swing.JTable;

import org.signalml.app.model.PropertySheetModel;

/** ViewerPropertySheet
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ViewerPropertySheet extends JTable {

	private static final long serialVersionUID = 1L;

	public ViewerPropertySheet(PropertySheetModel model) {
		super(model);
		setCellSelectionEnabled(true);
		getTableHeader().setReorderingAllowed(false);
	}

}

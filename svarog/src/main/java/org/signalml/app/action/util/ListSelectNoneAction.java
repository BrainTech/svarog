/* ListSelectNoneAction.java created 2008-03-05
 *
 */

package org.signalml.app.action.util;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JList;
import static org.signalml.app.util.i18n.SvarogI18n._;


/** ListSelectNoneAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ListSelectNoneAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private JList list;

	public ListSelectNoneAction(JList list) {
		super(_("Select none"));
		this.list = list;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		list.clearSelection();
	}

}

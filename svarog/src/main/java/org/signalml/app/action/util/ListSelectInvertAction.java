/* ListSelectInvertAction.java created 2008-03-05
 *
 */

package org.signalml.app.action.util;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JList;


/** ListSelectInvertAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ListSelectInvertAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private JList list;

	public ListSelectInvertAction(JList list) {
		super(_("Invert"));
		this.list = list;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int count = list.getModel().getSize();
		boolean[] selected = new boolean[count];
		int i;

		for (i=0; i<count; i++) {
			selected[i] = ! list.isSelectedIndex(i);
		}

		list.clearSelection();

		for (i=0; i<count; i++) {
			if (selected[i]) {
				list.addSelectionInterval(i, i);
			}
		}
	}

}

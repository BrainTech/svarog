/* ListSelectInvertAction.java created 2008-03-05
 *
 */

package org.signalml.app.action.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JList;

import org.springframework.context.support.MessageSourceAccessor;

/** ListSelectInvertAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ListSelectInvertAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private JList list;

	public ListSelectInvertAction(MessageSourceAccessor messageSource, JList list) {
		super(messageSource.getMessage("action.util.selectInvert"));
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
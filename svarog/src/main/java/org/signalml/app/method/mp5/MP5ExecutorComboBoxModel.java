/* MP5ExecutorComboBoxModel.java created 2008-02-08
 *
 */

package org.signalml.app.method.mp5;

import javax.swing.ComboBoxModel;
import org.signalml.method.mp5.MP5Executor;

/** MP5ExecutorComboBoxModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5ExecutorComboBoxModel extends MP5ExecutorListModel implements ComboBoxModel {

	private static final long serialVersionUID = 1L;

	private MP5Executor selected;

	public MP5ExecutorComboBoxModel(MP5ExecutorManager manager) {
		super(manager);
	}

	@Override
	public Object getSelectedItem() {
		return selected;
	}

	@Override
	public void setSelectedItem(Object item) {
		selected = (MP5Executor) item;
	}

}

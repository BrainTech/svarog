/* MP5ExecutorListModel.java created 2008-02-08
 * 
 */

package org.signalml.app.method.mp5;

import javax.swing.AbstractListModel;

/** MP5ExecutorListModel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5ExecutorListModel extends AbstractListModel implements MP5ExecutorManagerListener {

	private static final long serialVersionUID = 1L;

	protected MP5ExecutorManager manager;
	
	public MP5ExecutorListModel(MP5ExecutorManager manager) {
		this.manager = manager;
		manager.addMP5ExecutorManagerListener(this);
	}

	@Override
	public Object getElementAt(int index) {
		return manager.getExecutorAt(index);
	}

	@Override
	public int getSize() {
		return manager.getExecutorCount();
	}

	@Override
	public void defaultExecutorChanged(MP5ExecutorManagerEvent ev) {
		// ignored
	}

	@Override
	public void executorAdded(MP5ExecutorManagerEvent ev) {
		int index = ev.getIndex();
		fireIntervalAdded(this, index, index);
	}

	@Override
	public void executorChanged(MP5ExecutorManagerEvent ev) {
		int index = ev.getIndex();
		fireContentsChanged(this, index, index);
	}

	@Override
	public void executorRemoved(MP5ExecutorManagerEvent ev) {
		int index = ev.getIndex();
		fireIntervalRemoved(this, index, index);
	}
	
}

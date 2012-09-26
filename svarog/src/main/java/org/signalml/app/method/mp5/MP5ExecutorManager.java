/* MP5ExecutorManager.java created 2008-02-08
 *
 */

package org.signalml.app.method.mp5;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.event.EventListenerList;

import org.signalml.app.config.AbstractXMLConfiguration;
import org.signalml.app.util.XMLUtils;
import org.signalml.exception.SanityCheckException;
import org.signalml.method.mp5.MP5Executor;
import org.signalml.method.mp5.MP5ExecutorLocator;
import org.signalml.method.mp5.MP5LocalProcessExecutor;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/** MP5ExecutorManager
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("mp5executors")
public class MP5ExecutorManager extends AbstractXMLConfiguration implements MP5ExecutorLocator {

	private ArrayList<MP5Executor> executors;
	private MP5Executor defaultExecutor;

	private transient HashMap<String, MP5Executor> executorMap = null;
	private transient EventListenerList listenerList = new EventListenerList();

	public MP5ExecutorManager() {
		executors = new ArrayList<MP5Executor>();
		defaultExecutor = null;
	}

	@Override
	public XStream getStreamer() {
		if (streamer == null) {
			streamer = createExecutorStreamer();
		}
		return streamer;
	}

	private XStream createExecutorStreamer() {
		XStream streamer = XMLUtils.getDefaultStreamer();
		Annotations.configureAliases(
			streamer,
			MP5ExecutorManager.class,
			MP5LocalProcessExecutor.class
			//MP5RemoteExecutor.class,
			//MP5RemotePasswordExecutor.class
		);
		streamer.setMode(XStream.ID_REFERENCES);

		return streamer;
	}

	private HashMap<String, MP5Executor> getExecutorMap() {
		if (executorMap == null) {
			executorMap = new HashMap<String, MP5Executor>();
			for (MP5Executor e : executors) {
				executorMap.put(e.getUID(), e);
			}
		}
		return executorMap;
	}

	public int getExecutorCount() {
		return executors.size();
	}

	public MP5Executor getExecutorAt(int index) {
		return executors.get(index);
	}

	@Override
	public MP5Executor findExecutor(String uid) {
		return getExecutorMap().get(uid);
	}

	public boolean addExecutor(MP5Executor executor) {
		String uid = executor.getUID();
		if (getExecutorMap().get(uid) != null) {
			throw new SanityCheckException("Executor already added");
		}
		boolean added = executors.add(executor);
		if (added) {
			int index = executors.indexOf(executor);
			getExecutorMap().put(uid, executor);
			fireExecutorAdded(index);
			if (defaultExecutor == null) {
				setDefaultExecutor(executor);
			}
		}
		return added;
	}

	public void setExecutorAt(int index, MP5Executor executor) {
		MP5Executor existingExecutor = executors.get(index);
		if (executor != existingExecutor) {
			String uid = executor.getUID();
			if (getExecutorMap().get(uid) != null) {
				throw new SanityCheckException("Executor already added");
			}
			getExecutorMap().remove(existingExecutor.getUID());
			executors.set(index, executor);
			getExecutorMap().put(uid, executor);
		}
		fireExecutorChanged(index);
		if (executor != existingExecutor) {
			if (existingExecutor == defaultExecutor) {
				setDefaultExecutor(executor);
			}
		}
	}

	public MP5Executor removeExecutor(int index) {
		MP5Executor removed = executors.remove(index);
		getExecutorMap().remove(removed.getUID());
		fireExecutorRemoved(index);
		if (removed == defaultExecutor) {
			if (executors.size() > 0) {
				setDefaultExecutor(executors.get(0));
			} else {
				setDefaultExecutor(null);
			}
		}
		return removed;
	}

	public boolean removeExecutor(MP5Executor executor) {
		int index = executors.indexOf(executor);
		boolean removed = executors.remove(executor);
		if (removed) {
			getExecutorMap().remove(executor.getUID());
			fireExecutorRemoved(index);
			if (executor == defaultExecutor) {
				if (executors.size() > 0) {
					setDefaultExecutor(executors.get(0));
				} else {
					setDefaultExecutor(null);
				}
			}
		}
		return removed;
	}


	public MP5Executor getDefaultExecutor() {
		return defaultExecutor;
	}

	public void setDefaultExecutor(MP5Executor defaultExecutor) {
		if (this.defaultExecutor != defaultExecutor) {
			if (defaultExecutor != null && !executors.contains(defaultExecutor)) {
				throw new IllegalArgumentException("Executor not in list");
			}
			this.defaultExecutor = defaultExecutor;
			fireDefaultExecutorChanged();
		}
	}

	@Override
	public String getStandardFilename() {
		return "mp5-executor.xml";
	}

	protected void fireExecutorAdded(int index) {
		Object[] listeners = listenerList.getListenerList();
		MP5ExecutorManagerEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==MP5ExecutorManagerListener.class) {
				if (e == null) {
					e = new MP5ExecutorManagerEvent(this,index);
				}
				((MP5ExecutorManagerListener)listeners[i+1]).executorAdded(e);
			}
		}
	}

	protected void fireExecutorChanged(int index) {
		Object[] listeners = listenerList.getListenerList();
		MP5ExecutorManagerEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==MP5ExecutorManagerListener.class) {
				if (e == null) {
					e = new MP5ExecutorManagerEvent(this, index);
				}
				((MP5ExecutorManagerListener)listeners[i+1]).executorChanged(e);
			}
		}
	}

	protected void fireExecutorRemoved(int index) {
		Object[] listeners = listenerList.getListenerList();
		MP5ExecutorManagerEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==MP5ExecutorManagerListener.class) {
				if (e == null) {
					e = new MP5ExecutorManagerEvent(this,index);
				}
				((MP5ExecutorManagerListener)listeners[i+1]).executorRemoved(e);
			}
		}
	}

	protected void fireDefaultExecutorChanged() {
		Object[] listeners = listenerList.getListenerList();
		MP5ExecutorManagerEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==MP5ExecutorManagerListener.class) {
				if (e == null) {
					e = new MP5ExecutorManagerEvent(this);
				}
				((MP5ExecutorManagerListener)listeners[i+1]).defaultExecutorChanged(e);
			}
		}
	}

	public void addMP5ExecutorManagerListener(MP5ExecutorManagerListener listener) {
		listenerList.add(MP5ExecutorManagerListener.class, listener);
	}

	public void removeMP5ExecutorManagerListener(MP5ExecutorManagerListener listener) {
		listenerList.remove(MP5ExecutorManagerListener.class, listener);
	}

}

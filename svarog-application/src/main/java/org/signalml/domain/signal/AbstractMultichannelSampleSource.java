/* AbstractMultichannelSampleSource.java created 2008-01-27
 * 
 */

package org.signalml.domain.signal;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/** AbstractMultichannelSampleSource
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractMultichannelSampleSource implements MultichannelSampleSource {

	protected PropertyChangeSupport pcSupport;

	public AbstractMultichannelSampleSource() {
		pcSupport = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcSupport.removePropertyChangeListener(listener);
	}
		
}

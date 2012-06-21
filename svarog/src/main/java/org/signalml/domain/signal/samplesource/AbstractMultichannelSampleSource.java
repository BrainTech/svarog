/* AbstractMultichannelSampleSource.java created 2008-01-27
 *
 */

package org.signalml.domain.signal.samplesource;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This is an abstract class representing the multichannel source of signal
 * samples with the support of {@link PropertyChangeListener listeners}.
 *
 * @see MultichannelSampleSource
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractMultichannelSampleSource implements MultichannelSampleSource {

	/**
	 * A {@link PropertyChangeSupport support} for changes associated with
	 * this montage.
	 */
	protected PropertyChangeSupport pcSupport;

	/**
	 * Default constructor. Creates an empty source with an empty
	 * {@link PropertyChangeSupport change support}.
	 */
	public AbstractMultichannelSampleSource() {
		pcSupport = new PropertyChangeSupport(this);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcSupport.removePropertyChangeListener(listener);
	}

}

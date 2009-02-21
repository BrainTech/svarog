/* BookProcessor.java created 2008-02-28
 * 
 */

package org.signalml.domain.book;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Enumeration;

import javax.swing.event.EventListenerList;

/** BookProcessor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookProcessor implements StandardBook, EventProducerBook, PropertyChangeListener, BookListener {

	protected StandardBook source;
		
	protected PropertyChangeSupport pcSupport;
	protected EventListenerList listenerList;
	
	public BookProcessor(StandardBook source) {
		pcSupport = new PropertyChangeSupport(this);
		listenerList = new EventListenerList();
		this.source = source;
		if( source instanceof EventProducerBook ) {
			EventProducerBook eventSource = (EventProducerBook) source;
			eventSource.addPropertyChangeListener(this);
			eventSource.addBookListener(this);
		}
	}

	@Override
	public void close() {
		if( source instanceof EventProducerBook ) {
			EventProducerBook eventSource = (EventProducerBook) source;
			eventSource.removePropertyChangeListener(this);
			eventSource.removeBookListener(this);
		}
	}

	@Override
	public String getBookComment() {
		return source.getBookComment();
	}

	@Override
	public float getCalibration() {
		return source.getCalibration();
	}

	@Override
	public int getChannelCount() {
		return source.getChannelCount();
	}

	@Override
	public String getChannelLabel(int channelIndex) {
		return source.getChannelLabel(channelIndex);
	}

	@Override
	public String getDate() {
		return source.getDate();
	}

	@Override
	public int getDictionarySize() {
		return source.getDictionarySize();
	}

	@Override
	public char getDictionaryType() {
		return source.getDictionaryType();
	}

	@Override
	public float getEnergyPercent() {
		return source.getEnergyPercent();
	}

	@Override
	public int getMaxIterationCount() {
		return source.getMaxIterationCount();
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		return source.getProperty(name);
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		return source.getPropertyNames();
	}

	@Override
	public float getSamplingFrequency() {
		return source.getSamplingFrequency();
	}

	@Override
	public StandardBookSegment[] getSegmentAt(int segmentIndex) {
		return source.getSegmentAt(segmentIndex);
	}

	@Override
	public StandardBookSegment getSegmentAt(int segmentIndex, int channelIndex) {
		return source.getSegmentAt(segmentIndex, channelIndex);
	}

	@Override
	public int getSegmentCount() {
		return source.getSegmentCount();
	}

	@Override
	public int getSignalChannelCount() {
		return source.getChannelCount();
	}

	@Override
	public String getTextInfo() {
		return source.getTextInfo();
	}

	@Override
	public String getVersion() {
		return source.getVersion();
	}

	@Override
	public String getWebSiteInfo() {
		return source.getWebSiteInfo();
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcSupport.addPropertyChangeListener(propertyName, listener);
	}
	
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcSupport.removePropertyChangeListener(propertyName, listener);
	}

	@Override
	public void addBookListener(BookListener listener) {
		listenerList.add(BookListener.class, listener);
	}

	@Override
	public void removeBookListener(BookListener listener) {
		listenerList.remove(BookListener.class, listener);
	}

	protected void onAnyBookEvent( BookEvent ev ) {
		// do nothing
	}
	
	@Override
	public void atomAdded(BookEvent ev) {
		onAnyBookEvent(ev);
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==BookListener.class) {
	             ((BookListener)listeners[i+1]).atomAdded(ev);
	         }
		}
	}

	@Override
	public void atomChanged(BookEvent ev) {
		onAnyBookEvent(ev);
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==BookListener.class) {
	             ((BookListener)listeners[i+1]).atomChanged(ev);
	         }
		}		
	}

	@Override
	public void atomRemoved(BookEvent ev) {
		onAnyBookEvent(ev);
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==BookListener.class) {
	             ((BookListener)listeners[i+1]).atomRemoved(ev);
	         }
		}
	}

	@Override
	public void bookStructureChanged(BookEvent ev) {
		onAnyBookEvent(ev);
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==BookListener.class) {
	             ((BookListener)listeners[i+1]).bookStructureChanged(ev);
	         }
		}
	}

	@Override
	public void segmentAdded(BookEvent ev) {
		onAnyBookEvent(ev);
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==BookListener.class) {
	             ((BookListener)listeners[i+1]).segmentAdded(ev);
	         }
		}		
	}

	@Override
	public void segmentAtomsChanged(BookEvent ev) {
		onAnyBookEvent(ev);
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==BookListener.class) {
	             ((BookListener)listeners[i+1]).segmentAtomsChanged(ev);
	         }
		}		
	}

	@Override
	public void segmentChanged(BookEvent ev) {
		onAnyBookEvent(ev);
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==BookListener.class) {
	             ((BookListener)listeners[i+1]).segmentChanged(ev);
	         }
		}		
	}

	@Override
	public void segmentRemoved(BookEvent ev) {
		onAnyBookEvent(ev);
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==BookListener.class) {
	             ((BookListener)listeners[i+1]).segmentRemoved(ev);
	         }
		}		
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		pcSupport.firePropertyChange(evt);
	}
	
}

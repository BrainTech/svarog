/* AbstractMutableBook.java created 2008-02-23
 * 
 */

package org.signalml.domain.book;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.event.EventListenerList;

/** AbstractMutableBook
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractMutableBook implements MutableBook, EventProducerBook {

	private PropertyChangeSupport pcSupport;
	private EventListenerList listenerList;

	public AbstractMutableBook() {
		pcSupport = new PropertyChangeSupport(this);
		listenerList = new EventListenerList();
	}
	
	public void addAtom( StandardBookAtom atom, int channelIndex, int segmentIndex ) {
		MutableBookSegment segment = (MutableBookSegment) getSegmentAt(segmentIndex, channelIndex);
		int index = segment.addAtom(atom);
		fireAtomAdded(channelIndex, segmentIndex, index);
	}
	
	public void setAtomAt( int channelIndex, int segmentIndex, int atomIndex, StandardBookAtom atom ) {
		MutableBookSegment segment = (MutableBookSegment) getSegmentAt(segmentIndex, channelIndex);
		segment.setAtomAt(atomIndex, atom);
		fireAtomChanged(channelIndex, segmentIndex, atomIndex);
	}
	
	public void removeAtomAt( int channelIndex, int segmentIndex, int atomIndex ) {
		MutableBookSegment segment = (MutableBookSegment) getSegmentAt(segmentIndex, channelIndex);
		segment.removeAtomAt(atomIndex);
		fireAtomRemoved(channelIndex, segmentIndex, atomIndex);
	}
	
	public void publishSegmentAtomsChanged( int channelIndex, int segmentIndex ) {
		fireSegmentAtomsChanged(channelIndex, segmentIndex);
	}
	
	public void publishSegmentDataChanged( int segmentIndex ) {
		fireSegmentChanged(segmentIndex);
	}
	
	public void publishBookStructureChanged() {
		fireBookStructureChanged();
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

	protected void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue) {
		pcSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
	}

	protected void fireIndexedPropertyChange(String propertyName, int index, int oldValue, int newValue) {
		pcSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
	}

	protected void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
		pcSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
	}

	protected void firePropertyChange(PropertyChangeEvent evt) {
		pcSupport.firePropertyChange(evt);
	}

	protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
		pcSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
		pcSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		pcSupport.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	protected void fireBookStructureChanged() {
		Object[] listeners = listenerList.getListenerList();
		BookEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==BookListener.class) {
	        	 if( e == null ) { 
	        		 e = new BookEvent(this);
	        	 }
	             ((BookListener)listeners[i+1]).bookStructureChanged(e);
	         }
	     }
	}
	
	protected void fireSegmentAdded(int segmentIndex) {
		Object[] listeners = listenerList.getListenerList();
		BookEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==BookListener.class) {
	        	 if( e == null ) { 
	        		 e = new BookEvent(this, segmentIndex);
	        	 }
	             ((BookListener)listeners[i+1]).segmentAdded(e);
	         }
	     }
	}

	protected void fireSegmentChanged(int segmentIndex) {
		Object[] listeners = listenerList.getListenerList();
		BookEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==BookListener.class) {
	        	 if( e == null ) { 
	        		 e = new BookEvent(this, segmentIndex);
	        	 }
	             ((BookListener)listeners[i+1]).segmentChanged(e);
	         }
	     }
	}

	protected void fireSegmentRemoved(int segmentIndex) {
		Object[] listeners = listenerList.getListenerList();
		BookEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==BookListener.class) {
	        	 if( e == null ) { 
	        		 e = new BookEvent(this, segmentIndex);
	        	 }
	             ((BookListener)listeners[i+1]).segmentRemoved(e);
	         }
	     }
	}

	protected void fireSegmentAtomsChanged(int channelIndex, int segmentIndex) {
		Object[] listeners = listenerList.getListenerList();
		BookEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==BookListener.class) {
	        	 if( e == null ) { 
	        		 e = new BookEvent(this, channelIndex, segmentIndex);
	        	 }
	             ((BookListener)listeners[i+1]).segmentAtomsChanged(e);
	         }
	     }
	}

	protected void fireAtomAdded(int channelIndex, int segmentIndex, int atomIndex) {
		Object[] listeners = listenerList.getListenerList();
		BookEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==BookListener.class) {
	        	 if( e == null ) { 
	        		 e = new BookEvent(this, channelIndex, segmentIndex, atomIndex);
	        	 }
	             ((BookListener)listeners[i+1]).atomAdded(e);
	         }
	     }
	}

	protected void fireAtomChanged(int channelIndex, int segmentIndex, int atomIndex) {
		Object[] listeners = listenerList.getListenerList();
		BookEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==BookListener.class) {
	        	 if( e == null ) { 
	        		 e = new BookEvent(this, channelIndex, segmentIndex, atomIndex);
	        	 }
	             ((BookListener)listeners[i+1]).atomChanged(e);
	         }
	     }
	}
	
	protected void fireAtomRemoved(int channelIndex, int segmentIndex, int atomIndex) {
		Object[] listeners = listenerList.getListenerList();
		BookEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==BookListener.class) {
	        	 if( e == null ) { 
	        		 e = new BookEvent(this, channelIndex, segmentIndex, atomIndex);
	        	 }
	             ((BookListener)listeners[i+1]).atomRemoved(e);
	         }
	     }
	}
	
}

/* ActionFocusSupport.java created 2007-11-17
 * 
 */

package org.signalml.app.action.selector;

import javax.swing.event.EventListenerList;

/** ActionFocusSupport
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ActionFocusSupport {

	private Object source;
		
	private EventListenerList listenerList = new EventListenerList();
	
	public ActionFocusSupport(Object source) {
		if( source == null ) {
			throw new NullPointerException("No source");
		}
		this.source = source;
	}
	
	public void addActionFocusListener(ActionFocusListener listener) {
		listenerList.add(ActionFocusListener.class, listener);
	}

	public void removeActionFocusListener(ActionFocusListener listener) {
		listenerList.remove(ActionFocusListener.class, listener);
	}
	
	public void fireActionFocusChanged() {
		Object[] listeners = listenerList.getListenerList();
		ActionFocusEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==ActionFocusListener.class) {
				 if( e == null ) { 
					 e = new ActionFocusEvent(source);
				 }
				 ((ActionFocusListener)listeners[i+1]).actionFocusChanged(e);
			 }
		 }
	}	
		
}

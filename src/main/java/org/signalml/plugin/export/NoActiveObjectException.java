/**
 * 
 */
package org.signalml.plugin.export;

import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.SvarogAccessGUI;

/**
 * Thrown if a function should return an active object, but there
 * is none, for example:
 * <ul>
 * <li>if there is no active {@link ExportedTag tag} for function
 * {@link SvarogAccessSignal#getActiveTag() getActiveTag()},</li>
 * <li>if there is no active {@link Document} for function
 * {@link SvarogAccessSignal#getActiveDocument()},</li>
 * <li>if there is no active tab for function
 * {@link SvarogAccessGUI#getSelectedMainTab()}.</li>
 * </ul>
 * 
 * @author Marcin Szumski
 */
public class NoActiveObjectException extends SignalMLException{

	private static final long serialVersionUID = 1L;

	/**
	 * Empty constructor.
	 * @see Exception#Exception()
	 */
	public NoActiveObjectException(){
		
	}
	
	/**
	 * Constructor.
     * @see Exception#Exception(String)
     */
	public NoActiveObjectException(String message){
		super(message);
	}
	
	/**
	 * Constructor.
	 * @see Exception#Exception(Throwable)
	 */
	public NoActiveObjectException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor.
	 * @see Exception#Exception(String, Throwable)
	 */
	public NoActiveObjectException(String message, Throwable cause) {
		super(message, cause);
	}
	
	
}

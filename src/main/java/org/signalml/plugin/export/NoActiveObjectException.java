/**
 * 
 */
package org.signalml.plugin.export;

/**
 * Thrown if function should return an active object, but there
 * is none.
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

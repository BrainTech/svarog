/* SignalMLSecurityManager.java created 2008-02-29
 * 
 */

package org.signalml.applet;

import org.apache.log4j.Logger;
import org.codehaus.janino.JavaSourceClassLoader;

/** SignalMLSecurityManager
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalMLSecurityManager extends SecurityManager {

	protected static final Logger logger = Logger.getLogger(SignalMLSecurityManager.class);
	
	public SignalMLSecurityManager() {
		super();
	}
	
	@Override
	public void checkRead(String file) {
		
		try {
			super.checkRead(file);
		} catch( SecurityException ex ) {

			// in case access is denied test if this is a dynamic class
			Class<?>[] classContext = getClassContext();
			for( Class<?> c : classContext ) {
				ClassLoader classLoader = c.getClassLoader();
				if( classLoader instanceof JavaSourceClassLoader ) {
					// dynamic code, ignore the exception, allow access
					logger.info( "Allowing dynamic code to read file [" + file + "]" );
					return;
				}
			}

			// XXX: not dynamic, reassert exception
			throw ex;
			
		}
		
	}
	
}

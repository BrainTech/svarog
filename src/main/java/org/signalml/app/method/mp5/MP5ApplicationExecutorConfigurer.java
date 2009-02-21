/* MP5ApplicationExecutorConfigurer.java created 2008-02-18
 * 
 */

package org.signalml.app.method.mp5;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.signalml.method.ComputationException;
import org.signalml.method.mp5.MP5Executor;
import org.signalml.method.mp5.MP5ExecutorConfigurer;
import org.signalml.method.mp5.MP5RemotePasswordExecutor;

/** MP5ApplicationExecutorConfigurer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5ApplicationExecutorConfigurer implements MP5ExecutorConfigurer {

	protected static final Logger logger = Logger.getLogger(MP5ApplicationExecutorConfigurer.class);
	
	private MP5RemoteExecutorDialog remoteExecutorDialog;
	
	public MP5RemoteExecutorDialog getRemoteExecutorDialog() {
		return remoteExecutorDialog;
	}

	public void setRemoteExecutorDialog(MP5RemoteExecutorDialog remoteExecutorDialog) {
		this.remoteExecutorDialog = remoteExecutorDialog;
	}

	@Override
	public void configure(MP5Executor executor) throws ComputationException {

		if( executor instanceof MP5RemotePasswordExecutor ) {
			
			final MP5RemotePasswordExecutor remoteExecutor = (MP5RemotePasswordExecutor) executor;
			
			String password = remoteExecutor.getPassword();
			if( password == null || password.isEmpty() ) {
				
				try {
					SwingUtilities.invokeAndWait( new Runnable() {

						@Override
						public void run() {

							boolean oldPasswordOnly = false;
							try {
								
								oldPasswordOnly = remoteExecutorDialog.isPasswordOnly();
								remoteExecutorDialog.setPasswordOnly(true);
								
								boolean ok = remoteExecutorDialog.showDialog(remoteExecutor, true);
								if( !ok ) {
									remoteExecutor.setPassword(null);
								}
								
							} finally {
								remoteExecutorDialog.setPasswordOnly(oldPasswordOnly);
							}
							
						}
						
					});
				} catch (InterruptedException ex) {
					// ignore
				} catch (InvocationTargetException ex) {
					Throwable cause = ex.getCause();
					logger.error( "Failed to configure executor", cause );
					throw new ComputationException( cause );
				}
				
				password = remoteExecutor.getPassword();				
				if( password == null || password.isEmpty() ) {
					throw new ComputationException("error.mp5.noPassword");
				}
				
			}
			
		}
		
	}

}

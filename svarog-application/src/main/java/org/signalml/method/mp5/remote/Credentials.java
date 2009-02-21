/* Credentials.java created 2008-02-17
 * 
 */

package org.signalml.method.mp5.remote;

/** Credentials
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class Credentials {

	private PasswordCredentials passwordCredentials;
	
	private SharedSecretCredentials sharedSecretCredentials;

	public PasswordCredentials getPasswordCredentials() {
		return passwordCredentials;
	}

	public void setPasswordCredentials(PasswordCredentials passwordCredentials) {
		this.passwordCredentials = passwordCredentials;
	}

	public SharedSecretCredentials getSharedSecretCredentials() {
		return sharedSecretCredentials;
	}

	public void setSharedSecretCredentials(SharedSecretCredentials sharedSecretCredentials) {
		this.sharedSecretCredentials = sharedSecretCredentials;
	}
	
}

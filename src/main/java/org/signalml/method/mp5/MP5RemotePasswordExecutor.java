/* MP5RemotePasswordExecutor.java created 2008-02-21
 *
 */

package org.signalml.method.mp5;

import org.signalml.method.mp5.remote.Credentials;
import org.signalml.method.mp5.remote.PasswordCredentials;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** MP5RemotePasswordExecutor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("mp5remoteexecutor")
public class MP5RemotePasswordExecutor extends MP5RemoteExecutor {

	private transient String password;
	private boolean savePassword;

	private String savedPassword;

	public String getPassword() {
		if (password == null && savedPassword != null) {
			password = savedPassword;
		}
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
		if (savePassword) {
			this.savedPassword = password;
		}
	}

	public boolean isSavePassword() {
		return savePassword;
	}

	public void setSavePassword(boolean savePassword) {
		if (this.savePassword != savePassword) {
			this.savePassword = savePassword;
			if (savePassword) {
				this.savedPassword = this.password;
			} else {
				this.savedPassword = null;
			}
		}
	}

	@Override
	public Credentials createCredentials() {

		Credentials credentials = new Credentials();
		PasswordCredentials passwordCredentials = new PasswordCredentials();

		passwordCredentials.setUserName(userName);
		passwordCredentials.setPassword(password);

		credentials.setPasswordCredentials(passwordCredentials);

		return credentials;

	}

}

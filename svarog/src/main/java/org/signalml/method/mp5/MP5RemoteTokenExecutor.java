/* MP5RemoteTokenExecutor.java created 2008-02-21
 *
 */

package org.signalml.method.mp5;

import java.util.Date;

import org.signalml.method.mp5.remote.Credentials;
import org.signalml.method.mp5.remote.SharedSecretCredentials;

/** MP5RemoteTokenExecutor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5RemoteTokenExecutor extends MP5RemoteExecutor {

	private Date loginTime;
	private String token;

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public Credentials createCredentials() {

		Credentials credentials = new Credentials();

		SharedSecretCredentials sharedSecretCredentials = new SharedSecretCredentials();
		sharedSecretCredentials.setUserName(userName);
		sharedSecretCredentials.setLoginTime(loginTime);
		sharedSecretCredentials.setToken(token);

		credentials.setSharedSecretCredentials(sharedSecretCredentials);

		return credentials;

	}

}

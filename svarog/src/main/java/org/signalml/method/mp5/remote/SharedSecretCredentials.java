/* SharedSecretCredentials.java created 2008-02-17
 *
 */

package org.signalml.method.mp5.remote;

import java.util.Date;

/** SharedSecretCredentials
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SharedSecretCredentials {

	private String userName;
	private Date loginTime;
	private String token;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

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

}

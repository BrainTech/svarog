/* TestConnectionRequest.java created 2008-02-17
 *
 */

package org.signalml.method.mp5.remote;

/** TestConnectionRequest
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TestConnectionRequest {

	private Credentials credentials = new Credentials();

	private String helloString;

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public String getHelloString() {
		return helloString;
	}

	public void setHelloString(String helloString) {
		this.helloString = helloString;
	}

}

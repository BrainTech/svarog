/* DecompositionRequest.java created 2008-02-18
 *
 */

package org.signalml.method.mp5.remote;

import java.util.UUID;
import java.util.zip.DataFormatException;

import org.signalml.util.Util;

/** DecompositionRequest
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DecompositionRequest {

	private Credentials credentials = new Credentials();

	private String uid;

	private String signal;

	private String config;

	public DecompositionRequest() {
		uid = UUID.randomUUID().toString();
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getSignal() {
		return signal;
	}

	public void setSignal(String signal) {
		this.signal = signal;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public void setBinarySignal(byte[] signal) {

		setSignal(Util.compressAndBase64Encode(signal));

	}

	public byte[] getBinarySignal() throws DataFormatException {

		return Util.base64DecodeAndDecompress(getSignal());

	}

}

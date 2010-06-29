/* DecompositionResponse.java created 2008-02-18
 *
 */

package org.signalml.method.mp5.remote;

import java.util.zip.DataFormatException;

import org.signalml.util.Util;

/** DecompositionResponse
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DecompositionResponse {

	private String book;

	public String getBook() {
		return book;
	}

	public void setBook(String book) {
		this.book = book;
	}

	public void setBinaryBook(byte[] book) {

		setBook(Util.compressAndBase64Encode(book));

	}

	public byte[] getBinaryBook() throws DataFormatException {

		return Util.base64DecodeAndDecompress(getBook());

	}

}

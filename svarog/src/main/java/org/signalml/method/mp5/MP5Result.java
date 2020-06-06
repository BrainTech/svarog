/* MP5Result.java created 2007-10-03
 *
 */

package org.signalml.method.mp5;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.Serializable;

/** MP5Result
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("mp5result")
public class MP5Result implements Serializable {

	private static final long serialVersionUID = 1L;

	private String bookFilePath;

	public String getBookFilePath() {
		return bookFilePath;
	}

	public void setBookFilePath(String bookFilePath) {
		this.bookFilePath = bookFilePath;
	}

}

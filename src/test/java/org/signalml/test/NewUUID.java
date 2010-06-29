/* NewUUID.java created 2007-09-12
 *
 */
package org.signalml.test;

import java.util.UUID;

/** NewUUID
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class NewUUID {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UUID uuid = UUID.randomUUID();
		System.out.println(uuid.toString());
	}

}

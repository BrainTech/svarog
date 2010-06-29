/* Test.java created 2007-10-23
 *
 */

package org.signalml.test;

import java.util.regex.Pattern;

import org.signalml.domain.montage.eeg.EegChannel;

/** Test
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		EegChannel[] all = EegChannel.values();

		for (EegChannel c : all) {
			Pattern matchingPattern = c.getMatchingPattern();
			if (matchingPattern != null) {
				System.out.println(c.name() + "\t" + matchingPattern.pattern());
			}
		}

	}

}

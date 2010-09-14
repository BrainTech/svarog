/* TagStyleNameComparator.java created 2007-10-01
 *
 */

package org.signalml.domain.tag;

import java.util.Comparator;

/**
 * This class represents the comparator of (two) {@link TagStyle tag styles}.
 * Comparison is done by comparing the names of styles.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStyleNameComparator implements Comparator<TagStyle> {

        /**
         * Compares two {@link TagStyle tag styles} by comparing their
         * names (as strings).
         * @param ts1 the first tag style to be compared
         * @param ts2 the first tag style to be compared
         * @return the value <code>0</code> if the names are equal,
         * a value less than <code>0</code> if the name of the first style
         * is lexicographically less than the name of the second,
         * a value greater than <code>0</code> if the name of the first style
         * is lexicographically greater than the name of the second.
         */
	@Override
	public int compare(TagStyle ts1, TagStyle ts2) {
		return ts1.getName().compareTo(ts2.getName());
	}

}

/* TagStyleNameComparator.java created 2007-10-01
 *
 */

package org.signalml.domain.tag;

import java.util.Comparator;

/** TagStyleNameComparator
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStyleNameComparator implements Comparator<TagStyle> {

	@Override
	public int compare(TagStyle ts1, TagStyle ts2) {
		return ts1.getName().compareTo(ts2.getName());
	}

}

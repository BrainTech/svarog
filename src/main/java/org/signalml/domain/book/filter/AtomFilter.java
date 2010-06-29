/* AtomFilter.java created 2008-02-25
 *
 */
package org.signalml.domain.book.filter;

import org.signalml.domain.book.StandardBookAtom;
import org.signalml.domain.book.StandardBookSegment;

/** AtomFilter
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface AtomFilter {

	boolean matches(StandardBookSegment segment, StandardBookAtom atom);

}
import org.signalml.domain.book.StandardBookAtom;
import org.signalml.domain.book.StandardBookSegment;
import org.signalml.domain.book.filter.AtomFilter;

/** MyCustomFilter
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MyCustomFilter implements AtomFilter {

	public boolean matches(StandardBookSegment segment, StandardBookAtom atom) {
		return ( segment.indexOfAtom(atom) > 5 );
	}

}

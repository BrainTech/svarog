/* AtomFilterChain.java created 2008-02-25
 *
 */

package org.signalml.domain.book.filter;

import java.util.ArrayList;
import java.util.Iterator;

import org.signalml.app.config.preset.Preset;
import org.signalml.domain.book.StandardBookAtom;
import org.signalml.domain.book.StandardBookSegment;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** AtomFilterChain
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("chain")
public class AtomFilterChain implements Preset {

	private static final long serialVersionUID = 1L;

	private String name;

	private ArrayList<AbstractAtomFilter> chain;

	private boolean filteringEnabled;
	private boolean alternative;

	public AtomFilterChain() {
		chain = new ArrayList<AbstractAtomFilter>();
		filteringEnabled = true;
		alternative = false;
	}

	public AtomFilterChain(AtomFilterChain template) {
		chain = new ArrayList<AbstractAtomFilter>();
		Iterator<AbstractAtomFilter> it = template.chain.iterator();
		while (it.hasNext()) {
			chain.add(it.next().duplicate());
		}
		filteringEnabled = template.filteringEnabled;
		alternative = template.alternative;
		name = template.name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public AtomFilterChain duplicate() {
		return new AtomFilterChain(this);
	}

	public boolean isFilteringEnabled() {
		return filteringEnabled;
	}

	public void setFilteringEnabled(boolean filteringEnabled) {
		this.filteringEnabled = filteringEnabled;
	}

	public boolean isAlternative() {
		return alternative;
	}

	public void setAlternative(boolean alternative) {
		this.alternative = alternative;
	}

	public boolean isFiltered() {
		if (!filteringEnabled) {
			return false;
		}
		if (chain.isEmpty()) {
			return false;
		}
		for (AbstractAtomFilter filter : chain) {
			if (filter.isEnabled()) {
				return true;
			}
		}
		return false;
	}

	public int getFilterCount() {
		return chain.size();
	}

	public AbstractAtomFilter getFilterAt(int index) {
		return chain.get(index);
	}

	public int addFilter(AbstractAtomFilter filter) {
		chain.add(filter);
		return chain.indexOf(filter);
	}

	public AbstractAtomFilter removeFilterAt(int index) {
		return chain.remove(index);
	}

	public boolean matches(StandardBookSegment segment, StandardBookAtom atom) {

		boolean anyTried = false;

		if (filteringEnabled) {

			Iterator<AbstractAtomFilter> it = chain.iterator();
			AbstractAtomFilter filter;
			boolean passes;
			while (it.hasNext()) {
				filter = it.next();
				if (filter.isEnabled()) {
					anyTried = true;
					passes = (filter.isBlocking() ^ filter.matches(segment, atom));
					if (alternative) {
						if (passes) {
							return true;
						}
					} else {
						if (!passes) {
							return false;
						}
					}
				}
			}

		}

		if (alternative && anyTried) {
			// at least one filter has been tried, and neither passed
			return false;
		}

		// if there were no filters tried or all passed in AND mode then pass

		return true;

	}

	public boolean isEmpty() {
		return chain.isEmpty();
	}

	@Override
	public String toString() {
		return name;
	}

}

/* SingleNameSpaceContext.java created 2008-01-29
 *
 */

package org.signalml.app.util;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/** SingleNameSpaceContext
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SingleNameSpaceContext implements NamespaceContext {

	private String prefix;
	private String uri;

	private ArrayList<String> prefixes;

	public SingleNameSpaceContext(String prefix, String uri) {
		this.prefix = prefix;
		this.uri = uri;

		prefixes = new ArrayList<>();
		prefixes.add(prefix);

	}

	@Override
	public String getNamespaceURI(String prefix) {
		if (this.prefix.equals(prefix)) {
			return uri;
		}
		return XMLConstants.DEFAULT_NS_PREFIX;
	}

	@Override
	public String getPrefix(String namespaceURI) {
		if (uri.equals(namespaceURI)) {
			return prefix;
		}
		return null;
	}

	@Override
	public Iterator<String> getPrefixes(String namespaceURI) {
		return prefixes.iterator();
	}

}

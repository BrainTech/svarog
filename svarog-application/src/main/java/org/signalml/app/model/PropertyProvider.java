/* PropertyProvider.java created 2007-10-05
 * 
 */

package org.signalml.app.model;

import java.beans.IntrospectionException;
import java.util.List;

/** PropertyProvider
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface PropertyProvider {

	List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException;
	
}

/* WriterExportable.java created 2007-12-07
 *
 */

package org.signalml.app.model;

import java.io.IOException;
import java.io.Writer;

/** WriterExportable
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface WriterExportableTable {

	void export(Writer writer, String columnSeparator, String rowSeparator, Object userObject) throws IOException;

}

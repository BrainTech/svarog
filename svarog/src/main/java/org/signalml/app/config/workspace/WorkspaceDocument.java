/* WorkspaceDocument.java created 2007-12-15
 *
 */

package org.signalml.app.config.workspace;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.app.document.mrud.MRUDEntry;

/** WorkspaceDocument
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("workspacedocument")
public class WorkspaceDocument {

	protected MRUDEntry mrudEntry;

	protected WorkspaceDocument() {
	}

	public WorkspaceDocument(MRUDEntry mrudEntry) {
		this.mrudEntry = mrudEntry;
	}

	public MRUDEntry getMrudEntry() {
		return mrudEntry;
	}

}

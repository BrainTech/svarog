/* WorkspaceTag.java created 2007-12-15
 *
 */

package org.signalml.app.config.workspace;

import org.signalml.app.document.MRUDEntry;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.TagDocument;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** WorkspaceTag
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("workspacetag")
public class WorkspaceTag extends WorkspaceDocument {

	protected WorkspaceTag() {
		super();
	}

	public WorkspaceTag(TagDocument tagDocument) {
		mrudEntry = new MRUDEntry(ManagedDocumentType.TAG, tagDocument.getClass(), tagDocument.getBackingFile().getAbsolutePath());
		// nothing special to do
	}

	public void configureTag(TagDocument tagDocument) {
		// nothing special to do
	}

}

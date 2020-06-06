/* ChannelPropertyEditor.java created 2007-11-04
 *
 */

package org.signalml.app.model.components;

import java.beans.PropertyEditorSupport;
import javax.swing.tree.TreePath;
import org.signalml.app.document.signal.SignalDocument;

/** ChannelPropertyEditor
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelPropertyEditor extends PropertyEditorSupport implements TreePathAwarePropertyEditor {

	private SignalDocument signalDocument;

	@Override
	public String getAsText() {
		Integer channel = (Integer) getValue();
		String label;
		if (signalDocument == null) {
			label = channel.toString();
		} else {
			label = signalDocument.getMontage().getSourceChannelLabelAt(channel);
		}
		return label;
	}

	@Override
	public void setTreePath(TreePath treePath) {
		if (treePath != null) {
			int cnt = treePath.getPathCount();
			for (int i=cnt-1; i>=0; i--) {
				Object comp = treePath.getPathComponent(i);
				if (comp instanceof SignalDocument) {
					signalDocument = (SignalDocument) comp;
					return;
				}
			}
		}
		signalDocument = null;
	}

}

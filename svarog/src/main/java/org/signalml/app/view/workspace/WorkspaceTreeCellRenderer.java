/**
 *
 */
package org.signalml.app.view.workspace;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.mrud.MRUDEntry;
import org.signalml.plugin.export.signal.Document;

/** WorkspaceTreeCellRenderer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class WorkspaceTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		if (value instanceof Document) {
			ManagedDocumentType type = ManagedDocumentType.getForClass(((Document) value).getClass());
			Icon icon = null;
			if (type != null) {
				icon = type.getIcon();
			}
			if (icon != null) {
				label.setIcon(icon);
			}
		}
		else if (value instanceof MRUDEntry) {
			ManagedDocumentType type = ((MRUDEntry) value).getDocumentType();
			Icon icon = null;
			if (type != null) {
				icon = type.getIcon();
			}
			if (icon != null) {
				label.setIcon(icon);
			}
		}

		return label;

	}

}

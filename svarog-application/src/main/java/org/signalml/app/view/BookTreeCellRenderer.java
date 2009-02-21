package org.signalml.app.view;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.signalml.app.document.Document;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.model.BookAtomTreeNode;
import org.signalml.app.model.BookChannelTreeNode;
import org.signalml.app.model.BookSegmentTreeNode;
import org.signalml.app.util.IconUtils;

/** BookTreeCellRenderer
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		if( value instanceof BookAtomTreeNode ) {
			label.setIcon( IconUtils.getBookAtomIcon() );
		}
		else if( value instanceof BookSegmentTreeNode ) {
			label.setIcon( IconUtils.getBookSegmentIcon() );
		}
		else if( value instanceof BookChannelTreeNode ) {
			label.setIcon( IconUtils.getBookChannelIcon() );
		}
		else if( value instanceof Document ) {
			ManagedDocumentType type = ManagedDocumentType.getForClass(((Document) value).getClass());
			Icon icon = null;
			if( type != null ) {
				icon = type.getIcon();
			}
			if( icon != null ) {
				label.setIcon(icon);
			}
		}
		
		return label;
	}
	
}

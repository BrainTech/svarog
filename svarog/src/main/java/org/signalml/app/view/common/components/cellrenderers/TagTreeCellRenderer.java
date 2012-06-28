/**
 *
 */
package org.signalml.app.view.common.components.cellrenderers;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.model.tag.TagStylesTreeNode;
import org.signalml.app.model.tag.TagTypeTreeNode;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.tag.TagIconProducer;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;

/** WorkspaceTreeCellRenderer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	private TagIconProducer iconProducer;

	public TagTreeCellRenderer(TagIconProducer iconProducer) {
		super();
		this.iconProducer = iconProducer;
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		if (value instanceof Tag) {
			label.setIcon(iconProducer.getIcon(((Tag) value).getStyle()));
		}
		else if (value instanceof TagStyle) {
			label.setIcon(iconProducer.getIcon((TagStyle) value));
		}
		else if (value instanceof TagTypeTreeNode) {
			label.setIcon(IconUtils.getTagIcon(((TagTypeTreeNode) value).getType()));
		}
		else if (value instanceof TagStylesTreeNode) {
			label.setIcon(IconUtils.getPaletteIcon());
		}
		else if (value instanceof Document) {
			ManagedDocumentType type = ManagedDocumentType.getForClass(((Document) value).getClass());
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

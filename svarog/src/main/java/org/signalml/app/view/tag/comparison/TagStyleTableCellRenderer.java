/* TagStyleTableCellRenderer.java created 2007-12-04
 *
 */

package org.signalml.app.view.tag.comparison;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.tag.TagIconProducer;
import org.signalml.plugin.export.signal.TagStyle;

/** TagStyleTableCellRenderer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStyleTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;
	private String noneString;

	public static final Color DISABLED_COLOR = new Color(220,220,220);

	private TagIconProducer tagIconProducer;

	public TagStyleTableCellRenderer() {
		super();

		setHorizontalAlignment(CENTER);
		setVerticalAlignment(CENTER);
		setHorizontalTextPosition(CENTER);
		setVerticalTextPosition(BOTTOM);

		noneString = _("(none)");

	}



	public TagIconProducer getTagIconProducer() {
		return tagIconProducer;
	}

	public void setTagIconProducer(TagIconProducer tagIconProducer) {
		this.tagIconProducer = tagIconProducer;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		TagStyle style = (TagStyle) value;

		if (!isSelected) {
			label.setBackground(DISABLED_COLOR);
		}

		if (style == null) {
			label.setText(noneString);
			label.setIcon(null);
		} else {
			label.setText(style.getDescriptionOrName());
			label.setIcon(tagIconProducer.getIcon(style));
		}

		return label;
	}

}

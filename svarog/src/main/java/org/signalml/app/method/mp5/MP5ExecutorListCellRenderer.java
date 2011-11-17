/* MP5ExecutorListCellRenderer.java created 2008-02-08
 *
 */

package org.signalml.app.method.mp5;

import static org.signalml.app.SvarogI18n._;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.signalml.method.mp5.MP5Executor;

/** MP5ExecutorListCellRenderer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5ExecutorListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;

	private MP5Executor defaultExecutor;
	private Font normalFont;
	private Font boldFont;
	private String defaultString;

	public MP5ExecutorListCellRenderer() {
		super();
		normalFont = getFont().deriveFont(Font.PLAIN);
		boldFont = normalFont.deriveFont(Font.BOLD);
		defaultString = " " + _("(default)");
	}

	public MP5Executor getDefaultExecutor() {
		return defaultExecutor;
	}

	public void setDefaultExecutor(MP5Executor defaultExecutor) {
		this.defaultExecutor = defaultExecutor;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (value instanceof MP5Executor) {
			if (defaultExecutor != null && value == defaultExecutor) {
				label.setText(getSvarogI18n().getMessage((MP5Executor) value) + defaultString);
				label.setFont(boldFont);
			} else {
				label.setText(getSvarogI18n().getMessage((MP5Executor) value));
				label.setFont(normalFont);
			}
		}

		return label;
	}

	/**
	 * Returns the {@link SvarogAccessI18nImpl} instance.
	 * @return the {@link SvarogAccessI18nImpl} singleton instance
	 */
	protected org.signalml.app.SvarogI18n getSvarogI18n() {
		return org.signalml.app.SvarogI18n.getInstance();
	}
}

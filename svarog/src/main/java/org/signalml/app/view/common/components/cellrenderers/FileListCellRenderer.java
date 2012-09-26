/* FileListCellRenderer.java created 2008-03-05
 *
 */

package org.signalml.app.view.common.components.cellrenderers;

import java.awt.Component;
import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * The cell renderer for the list of files.
 * Works as parent class, but if the {@code value} is of type {@code File}
 * replaces the text with the absolute path of the file.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class FileListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 1L;

	/**
	 * Returns the {@link DefaultListCellRenderer#getListCellRendererComponent(
	 * JList, Object, int, boolean, boolean) component} obtained from parent,
	 * but if the {@code value} is of type {@code File} replaces the text with
	 * the absolute path of the file.
	 */
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

		DefaultListCellRenderer renderer = (DefaultListCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (value instanceof File) {
			renderer.setText(((File) value).getAbsolutePath());
		}

		return renderer;

	}

}

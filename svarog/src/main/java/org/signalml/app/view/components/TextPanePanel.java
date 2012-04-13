/* TextPanePanel.java created 2007-10-24
 *
 */

package org.signalml.app.view.components;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;

/**
 * Panel with the {@link #getTextPane() text pane} within a ScrollPane and the
 * TitledBorder.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TextPanePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the real text pane which is encapsulated in this panel
	 */
	private JTextPane textPane;
	/**
	 * the scroll pane within which the {@link #textPane} is located
	 */
	private JScrollPane scrollPane;

	/**
	 * Constructor.
	 * Creates this panel with BorderLayout and given title on the border.
	 * This panel contains a {@link #getTextPane() text pane} within a
	 * ScrollPane.
	 * @param title the title of this panel
	 */
	public TextPanePanel(String title) {
		super(new BorderLayout());

		if (title != null && !title.isEmpty()) {
			setBorder(new TitledBorder(title));
		}

		textPane = new JTextPane();

		scrollPane = new JScrollPane(textPane);

		add(scrollPane);

	}

	/**
	 * Returns the text pane located in this panel.
	 * @return the text pane located in this panel
	 */
	public JTextPane getTextPane() {
		return textPane;
	}

}

/* TitledComponentPanel.java created 2007-09-18
 *
 */

package org.signalml.app.view.components;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Panel which contains the component and its title above it.
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TitledComponentPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * Creates this panel with {@code BoxLayout} and two elements (from top to
	 * bottom):
	 * <ul>
	 * <li>the given title,</li>
	 * <li>the given component.</li></ul>
	 * @param title the title to be set
	 * @param component the component
	 */
	public TitledComponentPanel(String title, JComponent component) {
		super();
		this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		this.add(new JLabel(title));
		this.add(Box.createHorizontalStrut(5));
		this.add(Box.createHorizontalGlue());
		this.add(component);
	}

}

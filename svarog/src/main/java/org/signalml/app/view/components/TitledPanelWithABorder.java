/* TitledPanelWithABorder.java created 2011-03-06
 *
 */

package org.signalml.app.view.components;

import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Piotr Szachewicz
 */
public class TitledPanelWithABorder extends JPanel {

	public TitledPanelWithABorder() {
	}
	
	public TitledPanelWithABorder(String title) {
		this.setTitledBorder(title);
	}

	public void setTitledBorder(String title) {
		CompoundBorder border = new CompoundBorder(
			new TitledBorder(title),
			new EmptyBorder(3, 3, 3, 3));

		this.setBorder(border);
	}

}

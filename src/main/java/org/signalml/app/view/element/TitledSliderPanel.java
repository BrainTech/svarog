/* TitledSliderPanel.java created 2007-09-26
 *
 */

package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;

/** TitledSliderPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TitledSliderPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JSlider slider;
	private JLabel label;

	public TitledSliderPanel(String title, JSlider slider) {
		super();
		this.slider = slider;
		setLayout(new BorderLayout());
		label = new JLabel(title);
		label.setBorder(new EmptyBorder(0,8,0,0));
		label.setFont(new Font("Dialog", Font.PLAIN, 10));

		label.setOpaque(false);
		slider.setOpaque(false);

		add(label, BorderLayout.NORTH);
		add(slider, BorderLayout.CENTER);

	}

	public JSlider getSlider() {
		return slider;
	}

	@Override
	public boolean isOpaque() {
		return false;
	}

	@Override
	protected void paintComponent(Graphics g) {
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension d = slider.getPreferredSize();
		Dimension d2 = label.getPreferredSize();
		Insets i = getInsets();
		Dimension pref = new Dimension(d.width, d.height);
		if (pref.width < d2.width) {
			pref.width = d2.width;
		}
		pref.width += (i.left + i.right);
		pref.height += (i.top + i.bottom + d2.height);
		return pref;
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

}

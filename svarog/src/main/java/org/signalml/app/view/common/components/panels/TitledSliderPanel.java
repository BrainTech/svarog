/* TitledSliderPanel.java created 2007-09-26
 *
 */

package org.signalml.app.view.common.components.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;

/**
 * Panel with the slider and the title above it.
 * Contains two elements:
 * <ul>
 * <li>the label with the title; the label has the bottom border,</li>
 * <li>the slider.</li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TitledSliderPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the slider
	 */
	private JSlider slider;
	/**
	 * the label with the title; the label has the bottom border
	 */
	private JLabel label;

	/**
	 * Constructor.
	 * Creates this panel with {@link BorderLayout} and two elements (from top
	 * to bottom):
	 * <ul>
	 * <li>the label with the title; the label has the bottom border,</li>
	 * <li>the slider.</li></ul>
	 * @param title the title of this panel
	 * @param slider the slider to be used
	 */
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

	/**
	 * Returns the slider for this panel.
	 * @return the slider
	 */
	public JSlider getSlider() {
		return slider;
	}

	/**
	 * Returns that the slider is not opaque.
	 * @see JComponent#isOpaque()
	 */
	@Override
	public boolean isOpaque() {
		return false;
	}

	@Override
	protected void paintComponent(Graphics g) {
	}

	/**
	 * Returns the preferred size of this panel.
	 * <ul>
	 * <li>The preferred width is the width of the wider element (slider and
	 * label)+ the width of the border.</li>
	 * <li>The preferred height is the sum of heights of the slider, the label
	 * and the border.</li></ul>
	 */
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

	/**
	 * The minimum size of this panel is equal to the {@link
	 * #getPreferredSize() preferred size}.
	 */
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	/**
	 * The maximum size of this panel is equal to the {@link
	 * #getPreferredSize() preferred size}.
	 */
	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

}

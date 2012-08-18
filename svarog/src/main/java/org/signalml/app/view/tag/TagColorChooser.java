package org.signalml.app.view.tag;

import java.awt.Component;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.colorchooser.AbstractColorChooserPanel;

/**
 * A color chooser for selecting tag style color.
 *
 * @author Piotr Szachewicz
 */
public class TagColorChooser extends JColorChooser {

	public TagColorChooser() {
		super();

		setPreviewPanel(new JPanel());

		AbstractColorChooserPanel chooserPanel = getChooserPanels()[1];
		removeSliderFromComponent(chooserPanel);

		setChooserPanels(new AbstractColorChooserPanel[]
				{chooserPanel}
		);
	}

	/**
	 * Since JDK 7, sliders were introduced to control the HSB/RGB/..
	 * values of the color. We do not want them because they are too
	 * big.
	 * @param component the component from which the sliders should
	 * be removed.
	 */
	protected void removeSliderFromComponent(JComponent component) {
		for (Component c: component.getComponents()) {
			if (c instanceof JSlider)
				component.remove(c);
			else if (c instanceof JComponent)
				removeSliderFromComponent((JComponent) c);
		}
	}

}

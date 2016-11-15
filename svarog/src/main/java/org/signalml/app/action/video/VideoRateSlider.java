package org.signalml.app.action.video;

import java.awt.Dimension;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JSlider;

/**
 * Horizontal slider for selecting video playback rate as percentage (100 = normal rate).
 *
 * @author piotr.rozanski@braintech.pl
 */
public final class VideoRateSlider extends JSlider {

	private static final int MIN_PERCENTAGE = 50;
	private static final int MAX_PERCENTAGE = 200;
	private static final int INITAL_PERCENTAGE = 100;

	private static final int PREFERRED_WIDTH = 180;
	private static final int PREFERRED_HEIGHT = 30;
	private static final int MINOR_TICK_SPACING = 10;
	private static final int MAJOR_TICK_SPACING = 50;

	public VideoRateSlider() {
		super(JSlider.HORIZONTAL, MIN_PERCENTAGE, MAX_PERCENTAGE, INITAL_PERCENTAGE);
		Dimension size = new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);

		setFocusable(false);
		setMinorTickSpacing(MINOR_TICK_SPACING);
		setMajorTickSpacing(MAJOR_TICK_SPACING);
		setOpaque(false);
		setPaintLabels(true);
		setSnapToTicks(true);

		// unfortunately, Swing uses obsolete Dictionary interface
		Hashtable labels = new Hashtable();
		for (int i=MIN_PERCENTAGE; i<=MAX_PERCENTAGE; i+=MAJOR_TICK_SPACING) {
			labels.put(i, new JLabel(i+"%"));
		}
		setLabelTable(labels);
	}

}

package org.signalml.app.video.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JComponent;
import org.signalml.app.util.IconUtils;

/**
 * Simple separator of a fixed preferred size, displaying a given image.
 *
 * @author piotr.rozanski@braintech.pl
 */
public final class ImageSeparator extends JComponent {

	/**
	 * image to be displayed
	 */
	private final Image image;

	/**
	 * Create a new separator with a given image.
	 *
	 * @param imageResourcePath  resource path to the image
	 */
	public ImageSeparator(String imageResourcePath) {
		image = IconUtils.loadClassPathImage(imageResourcePath);
		Dimension size = new Dimension(image.getWidth(null), image.getHeight(null));
		setMinimumSize(size);
		setPreferredSize(size);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int x = (getWidth() - image.getWidth(null)) / 2;
		int y = (getHeight() - image.getHeight(null)) / 2;
		g.drawImage(image, x, y, null);
	}

}

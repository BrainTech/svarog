package org.signalml.app.video.components.internal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;

/**
 * RenderCallbackAdapter implementation used internally by SvarogRenderCallback.
 * Each instance of this class allocates buffer of a given dimension, which
 * cannot be changed after object is instantiated.
 * Whenever onDisplay event is triggered, two things happen: <ul>
 * <li>media frame is rendered into the internal buffer (image),</li>
 * <li>given surface (Component) instance is repaint()-ed.</li></ul>
 *
 * @author piotr.rozanski@braintech.pl
 */
public class RenderCallbackAdapterToImage extends RenderCallbackAdapter {

	private final Component surface;
	private final Dimension dimension;

	public final BufferedImage image;

	public RenderCallbackAdapterToImage(Component surface, Dimension dimension) {
		super(new int[dimension.width * dimension.height]);
		this.surface = surface;
		this.dimension = new Dimension(dimension);
		this.image = GraphicsEnvironment
			.getLocalGraphicsEnvironment()
			.getDefaultScreenDevice()
			.getDefaultConfiguration()
			.createCompatibleImage(dimension.width, dimension.height);
	}

	/**
	 * Check whether dimension of the internal buffer is the same as given.
	 *
	 * @param dimension  dimension to check
	 * @return true if dimensions are equal, false otherwise
	 */
	public boolean matchesDimension(Dimension dimension) {
		return this.dimension.equals(dimension);
	}

	@Override
	protected void onDisplay(DirectMediaPlayer dmp, int[] rgbBuffer) {
		image.setRGB(0, 0, dimension.width, dimension.height, rgbBuffer, 0, dimension.width);
		surface.repaint();
	}
}

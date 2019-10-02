package org.signalml.app.video.components.internal;

import com.sun.jna.Memory;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.concurrent.Semaphore;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;

/**
 * RenderCallback implementation for SvarogMediaComponent.
 * Internally uses RenderCallbackAdapterToImage instances, re-created whenever
 * dimension of the displayed buffer change.
 * In order for the rendering to work, three conditions must be fulfilled:<ul>
 * <li>DirectMediaPlayerForURL instance must return this SvarogRenderCallback
 * instance in onGetRenderCallback,</li>
 * <li>setAllowedMediaPlayer must be called with DirectMediaPlayer as parameter,</li>
 * <li>render method must be called in surface's paintComponent method.</li></ul>
 *
 * @author piotr.rozanski@braintech.pl
 */
public class SvarogRenderCallback implements RenderCallback {

	private final Component surface;
	private volatile RenderCallbackAdapterToImage adapter;
	private volatile DirectMediaPlayer dmp;

	private final Semaphore semaphore;

	public SvarogRenderCallback(Component surface) {
		this.surface = surface;
		this.semaphore = new Semaphore(1);
	}

    /**
     * Render media frame into the internal adapter's BufferedImage.
     *
     * @param dmp media player to which the event relates
     * @param memorys video data for one frame
     * @param bf information about the format of the buffer used
     */
	@Override
	public void display(DirectMediaPlayer dmp, Memory[] memorys, BufferFormat bf) {
		if (dmp == this.dmp) {
			// check if previous rendering process is not in progress
			if (semaphore.tryAcquire()) try {
				Dimension bufferDimension = new Dimension(bf.getWidth(), bf.getHeight());
				if (adapter == null || !adapter.matchesDimension(bufferDimension)) {
					adapter = new RenderCallbackAdapterToImage(surface, bufferDimension);
				}
				adapter.display(dmp, memorys, bf);
			} finally {
				semaphore.release();
			}
		}
	}

	/**
	 * Assign given DirectMediaPlayer instance as currently allowed
	 * to use this render callback.
	 *
	 * @param dmp can be null, in which case none will be allowed
	 */
	public void setAllowedMediaPlayer(DirectMediaPlayer dmp) {
		this.dmp = dmp;
	}

	/**
	 * Render internal adapter's BufferedImage contents into a given component.
	 *
	 * @param g2d  component's graphic surface
	 * @param width  width of component in pixels
	 * @param height  height of component in pixels
	 */
	public void render(Graphics2D g2d, int width, int height) {
		// check if previous rendering process is not in progress
		if (adapter != null && semaphore.tryAcquire()) try {
			int targetWidth = Math.min(width, adapter.image.getWidth() * height / adapter.image.getHeight());
			int targetHeight = Math.min(height, adapter.image.getHeight() * width / adapter.image.getWidth());
			g2d.drawImage(adapter.image, (width-targetWidth)/2, (height-targetHeight)/2, targetWidth, targetHeight, null);
		} finally {
			semaphore.release();
		}
	}

}

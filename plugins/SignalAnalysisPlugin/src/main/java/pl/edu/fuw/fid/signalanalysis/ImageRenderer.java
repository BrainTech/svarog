package pl.edu.fuw.fid.signalanalysis;

import java.awt.image.BufferedImage;

/**
 * @author ptr@mimuw.edu.pl
 */
public interface ImageRenderer {

	/**
	 * Render image consisting of computation result.
	 * This method will be run in background thread, so it can have a while.
	 * Implementation should periodically check if status.isCancelled()
	 * and abort (return null) if so.
	 *
	 * @param width   requested width of image in pixels
	 * @param height  requested height of image in pixels
	 * @param xMin    value for leftmost x dimension
	 * @param xMax    value for rightmost x dimension (just outside the image, for px = width)
	 * @param yMin    value for leftmost y dimension (just outside the image, for py = height)
	 * @param yMax    value for rightmost y dimension
	 * @param status  status to be checked periodically
	 * @return  computation result as image
	 * @throws Exception if computational error occurs
	 */
	public BufferedImage renderImage(int width, int height, double xMin, double xMax, double yMin, double yMax, ImageRendererStatus status) throws Exception;

}

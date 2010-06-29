/* TagComposite.java created 2007-10-14
 *
 */

package org.signalml.app.view.tag;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/** TagComposite
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagComposite implements Composite {

	// XXX this works and gives a very nice effect with superimposing tags, but is way too slow to use ATM :( - not used

	private int[] srcSamples = null;
	private int[] dstSamples = null;

	private int srcCapacity;
	private int dstCapacity;

	@Override
	public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
		return new TagCompositeContext();
	}

	private class TagCompositeContext implements CompositeContext {

		@Override
		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {

			int minX = Math.max(src.getMinX(), dstIn.getMinX());
			int minY = Math.max(src.getMinY(), dstIn.getMinY());
			int maxX = Math.min(src.getMinX()+src.getWidth(), dstIn.getMinX()+dstIn.getWidth());
			int maxY = Math.min(src.getMinY()+src.getHeight(), dstIn.getMinY()+dstIn.getHeight());
			int width = maxX-minX;
			int height = maxY-minY;

			int srcBands = src.getNumBands();
			int dstBands = dstIn.getNumBands();

			int reqSrcCapacity = srcBands*width*height;
			int reqDstCapacity = dstBands*width*height;

			if (srcSamples == null || srcCapacity < reqSrcCapacity) {
				srcSamples = new int[reqSrcCapacity];
				srcCapacity = reqSrcCapacity;
			}

			if (dstSamples == null || dstCapacity < reqDstCapacity) {
				dstSamples = new int[reqDstCapacity];
				dstCapacity = reqDstCapacity;
			}

			src.getPixels(minX,minY,width,height,srcSamples);
			dstIn.getPixels(minX,minY,width,height,dstSamples);

			int x, y;
			int srcOffset, dstOffset;

			for (y=0; y<height; y++) {
				for (x=0; x<width; x++) {
					srcOffset = srcBands*(y*width+x);
					dstOffset = dstBands*(y*width+x);

					if (dstSamples[dstOffset] == 255 && dstSamples[dstOffset+1] == 255 && dstSamples[dstOffset+2] == 255) {
						dstSamples[dstOffset] = srcSamples[srcOffset];
						dstSamples[dstOffset+1] = srcSamples[srcOffset+1];
						dstSamples[dstOffset+2] = srcSamples[srcOffset+2];
					} else {
						dstSamples[dstOffset+0] = (dstSamples[dstOffset+0] + srcSamples[srcOffset+0])/2;
						dstSamples[dstOffset+1] = (dstSamples[dstOffset+1] + srcSamples[srcOffset+1])/2;
						dstSamples[dstOffset+2] = (dstSamples[dstOffset+2] + srcSamples[srcOffset+2])/2;
					}
				}
			}

			dstOut.setPixels(minX,minY,width,height,dstSamples);

		}

		@Override
		public void dispose() {
		}

	}

}

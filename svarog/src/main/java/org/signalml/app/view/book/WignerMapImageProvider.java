/* WignerMapImageProvider.java created 2008-03-03
 *
 */

package org.signalml.app.view.book;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import org.apache.log4j.Logger;

/** WignerMapImageProvider
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class WignerMapImageProvider {

	private static final int PALETTE_SIZE = 256;

	protected static final Logger logger = Logger.getLogger(WignerMapImageProvider.class);

	public BufferedImage getImage(double[][] map, int width, int height, WignerMapPalette palette) {

		int[] paletteBuffer = palette.getPalette();

		byte[] byteBuffer = new byte[width*height];
		int i = 0;

		for (int y=0; y<height; y++) {
			for (int x=0; x<width; x++) {
				byteBuffer[i] = (byte) StrictMath.floor(0.5+(255*map[x][height-1-y]));
				i++;
			}
		}

		DataBufferByte buffer = new DataBufferByte(byteBuffer, i);

		IndexColorModel colorModel = new IndexColorModel(8,PALETTE_SIZE,paletteBuffer,0,false,-1,DataBuffer.TYPE_BYTE);
		WritableRaster raster = Raster.createWritableRaster(colorModel.createCompatibleSampleModel(width, height), buffer, null);

		BufferedImage image = new BufferedImage(colorModel, raster, true, null);

		return image;

	}

}

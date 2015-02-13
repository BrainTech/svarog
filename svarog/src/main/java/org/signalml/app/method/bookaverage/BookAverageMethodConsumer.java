/* BookAverageMethodConsumer.java created 2007-10-23
 *
 */

package org.signalml.app.method.bookaverage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.signalml.app.method.MethodResultConsumer;
import org.signalml.app.view.book.wignermap.WignerMapImageProvider;
import org.signalml.app.view.book.wignermap.WignerMapPalette;
import org.signalml.method.Method;
import org.signalml.method.bookaverage.BookAverageData;
import org.signalml.method.bookaverage.BookAverageResult;
import org.signalml.plugin.export.SignalMLException;

/**
 * BookAverageMethodConsumer
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * (+ fixed by) piotr@develancer.pl
 */
public class BookAverageMethodConsumer implements MethodResultConsumer {
	@Override
	public boolean consumeResult(Method method, Object methodData, Object methodResult) throws SignalMLException {
		BookAverageData data = (BookAverageData) methodData;
		BookAverageResult result = (BookAverageResult) methodResult;

		double[][] map = result.getMap();
		int width = map.length;
		int height = map[0].length;
		WignerMapImageProvider mip = new WignerMapImageProvider();
		BufferedImage image = mip.getImage(map, width, height, WignerMapPalette.RAINBOW);

		File outputfile = new File(data.getOutputFilePath());
		try {
			ImageIO.write(image, "png", outputfile);
			java.awt.Desktop.getDesktop().open(outputfile);
		} catch (IOException ex) {
			Logger.getLogger(BookAverageMethodConsumer.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}
}

/* EegSignalTypeConfigurer.java created 2007-11-22
 *
 */

package org.signalml.domain.montage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import org.signalml.app.document.SignalDocument;
import org.signalml.domain.montage.system.IChannelFunction;
import org.signalml.domain.montage.generators.RawMontageGenerator;
import org.signalml.domain.montage.system.ChannelFunction;

/**
 * This class represents the {@link SignalTypeConfigurer configurer} for
 * an EEG signal.
 * It configures (or creates) a {@link Montage montage} to be an EEG montage.
 * Contains generators for different EEG montages
 * (left and right ear, linked ears, longitudinal, transverse).
 * Returns the backdrop for the signal of that type.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalConfigurer {

	/**
	 * The 'length' of an ear or nose (the triangle) on the backdrop image.
	 * (How much an ear 'protrudes').
	 */
	public static final int EAR_OR_NOSE_LENGTH = 30;
	/**
	 * The 'width' of an ear or nose (marked as a triangle) on the backdrop
	 * image.
	 */
	public static final int EAR_OR_NOSE_WIDTH = 34;

        /**
         * the predefined raw MontageGenerator
         */
	private static final RawMontageGenerator rawMontageGenerator = new RawMontageGenerator();

        /**
         * the cached backdrop
         */
	private Image cachedBackdrop = null;
        /**
         * the width (pixels) of the cached backdrop
         */
	private int cachedBackdropWidth;
        /**
         * the height (pixels) of the cached backdrop
         */
	private int cachedBackdropHeight;

         /**
         * Creates the {@link Montage montage} with channelCount channels for
          * a EEG signal
         * @param channelCount the desired number of channels for the montage
         * @return the created montage
         */
	public static Montage createMontage(int channelCount) {

		Montage montage = new Montage(new SourceMontage(channelCount));
		rawMontageGenerator.createMontage(montage);
		return montage;

	}

        /**
         * Creates the {@link Montage montage} based on the
         * {@link SignalDocument document} with a signal
         * @return the created montage
         */
	public static Montage createMontage(SignalDocument signalDocument) {

		Montage montage = new Montage(new SourceMontage(signalDocument));
		rawMontageGenerator.createMontage(montage);
		return montage;

	}

        /**
         * Returns an {@link EegChannel eegChannel} of type 'UNKNOWN'
         * @return EegChannel.UNKNOWN
         */
	public static IChannelFunction genericChannel() {
		return ChannelFunction.UNKNOWN;
	}

        /**
         * Creates the backdrop for electrodes positions matrix.
         * If backdrop parameters haven't changed and cachedBackdrop exists,
         * it is used.
         * @param width the width of the backdrop
         * @param height the height of the backdrop
         * @return an Image with backdrop for electrodes positions matrix
         */
	public Image getMatrixBackdrop(int width, int height) {

		if (width < 60 || height < 60) {
			return null;
		}

		if (cachedBackdrop == null || cachedBackdropWidth != width || cachedBackdropHeight != height) {

			cachedBackdrop = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) cachedBackdrop.getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);
			g.setColor(Color.BLACK);
			g.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.CAP_ROUND));
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Area head = new Area(new Ellipse2D.Float(20, 20, width-41, height-41));
			int noseX = ((width-40)/2) + 20;
			int earY = (height-20)/2 + 20;

			Area nose = new Area(new Polygon(new int[] { noseX, noseX-EAR_OR_NOSE_WIDTH/2, noseX+EAR_OR_NOSE_WIDTH/2}, new int[] { 0, EAR_OR_NOSE_LENGTH, EAR_OR_NOSE_LENGTH }, 3));
			Area lEar = new Area(new Polygon(new int[] { 0, EAR_OR_NOSE_LENGTH, EAR_OR_NOSE_LENGTH}, new int [] { earY, earY-EAR_OR_NOSE_WIDTH/2, earY+EAR_OR_NOSE_WIDTH/2 }, 3));
			Area rEar = new Area(new Polygon(new int[] { width-1, width-31, width-31 }, new int [] { earY, earY-EAR_OR_NOSE_WIDTH/2, earY+EAR_OR_NOSE_WIDTH/2 }, 3));

			head.add(nose);
			head.add(lEar);
			head.add(rEar);

			g.setColor(Color.DARK_GRAY);
			g.draw(head);

			cachedBackdropWidth = width;
			cachedBackdropHeight = height;

		}

		return cachedBackdrop;

	}

}

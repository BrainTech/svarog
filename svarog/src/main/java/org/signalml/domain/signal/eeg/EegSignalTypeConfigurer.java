/* EegSignalTypeConfigurer.java created 2007-11-22
 *
 */

package org.signalml.domain.signal.eeg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.signalml.app.document.SignalDocument;
import org.signalml.domain.montage.IChannelFunction;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageGenerator;
import org.signalml.domain.montage.RawMontageGenerator;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.montage.eeg.ChannelFunction;
import org.signalml.domain.montage.eeg.LeftEarMontageGenerator;
import org.signalml.domain.montage.eeg.LinkedEarsMontageGenerator;
import org.signalml.domain.montage.eeg.CAMontageGenerator;
import org.signalml.domain.montage.eeg.LongitudinalIIMontageGenerator;
import org.signalml.domain.montage.eeg.LongitudinalIMontageGenerator;
import org.signalml.domain.montage.eeg.RightEarMontageGenerator;
import org.signalml.domain.montage.eeg.TransverseIMontageGenerator;
import org.signalml.domain.signal.SignalType;
import org.signalml.domain.signal.SignalTypeConfigurer;

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
public class EegSignalTypeConfigurer implements SignalTypeConfigurer {

        /**
         * the predefined raw MontageGenerator
         */
	private static final RawMontageGenerator rawMontageGenerator = new RawMontageGenerator();

        /**
         * the constant list of predefined montageGenerators
         */
	private static final List<MontageGenerator> montageGenerators = getAllMontageGenerators();

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
         * Creates the constant list of predefined
         * {@link MontageGenerator montageGenerators} for an EEG signal
         * @return the constant list of predefined montageGenerators for
         * an EEG signal
         */
	private static List<MontageGenerator> getAllMontageGenerators() {
		ArrayList<MontageGenerator> generators = new ArrayList<MontageGenerator>();
		generators.add(rawMontageGenerator);
		generators.add(new LeftEarMontageGenerator());
		generators.add(new RightEarMontageGenerator());
		generators.add(new LinkedEarsMontageGenerator());
		generators.add(new CAMontageGenerator());		
		generators.add(new LongitudinalIMontageGenerator());
		generators.add(new LongitudinalIIMontageGenerator());
		generators.add(new TransverseIMontageGenerator());

		return Collections.unmodifiableList(generators);
	}

         /**
         * Creates the {@link Montage montage} with channelCount channels for
          * a EEG signal
         * @param channelCount the desired number of channels for the montage
         * @return the created montage
         */
	@Override
	public Montage createMontage(int channelCount) {

		Montage montage = new Montage(new SourceMontage(SignalType.EEG_10_20, channelCount));
		rawMontageGenerator.createMontage(montage);
		return montage;

	}

        /**
         * Creates the {@link Montage montage} based on the
         * {@link SignalDocument document} with a signal
         * @param signalDocument the document with a signal
         * @return the created montage
         */
	@Override
	public Montage createMontage(SignalDocument signalDocument) {

		Montage montage = new Montage(new SourceMontage(signalDocument));
		rawMontageGenerator.createMontage(montage);
		return montage;

	}

	@Override
	public IChannelFunction[] allChannels() {
		return ChannelFunction.values();
	}

        /**
         * Returns an {@link EegChannel eegChannel} of type 'UNKNOWN'
         * @return EegChannel.UNKNOWN
         */
	@Override
	public IChannelFunction genericChannel() {
		return ChannelFunction.UNKNOWN;
	}

        /**
         * Finds an {@link EegChannel EEG channel} of a given name.
         * @param name the name of the type of the channel
         * @return the channel of a given name
         */
	@Override
	public IChannelFunction channelForName(String name) {
		if (name == null || name.isEmpty()) {
			return ChannelFunction.UNKNOWN;
		}
		IChannelFunction channel = ChannelFunction.forName(name);
		if (channel == null) {
			return ChannelFunction.UNKNOWN;
		}
		return channel;
	}

        /**
         * Creates the backdrop for electrodes positions matrix.
         * If backdrop parameters haven't changed and cachedBackdrop exists,
         * it is used.
         * @param width the width of the backdrop
         * @param height the height of the backdrop
         * @return an Image with backdrop for electrodes positions matrix
         */
	@Override
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

			Area head = new Area(new Ellipse2D.Float(20, 20, width-41, height-21));
			int noseX = ((width-40)/2) + 20;
			int earY = (height-20)/2 + 20;

			Area nose = new Area(new Polygon(new int[] { noseX, noseX-17, noseX+17 }, new int[] { 0, 30, 30 }, 3));
			Area lEar = new Area(new Polygon(new int[] { 0, 30, 30 }, new int [] { earY, earY-17, earY+17 }, 3));
			Area rEar = new Area(new Polygon(new int[] { width-1, width-31, width-31 }, new int [] { earY, earY-17, earY+17 }, 3));

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

        /**
         * Returns the height of the matrix of {@link EegChannel EEG channels}.
         * @return the height of the matrix of EEG channels
         */
	@Override
	public int getMatrixHeight() {
		return ChannelFunction.MATRIX_HEIGHT;
	}

        /**
         * Returns the width of the matrix of {@link EegChannel EEG channels}.
         * @return the width of the matrix of EEG channels
         */
	@Override
	public int getMatrixWidth() {
		return ChannelFunction.MATRIX_WIDTH;
	}

        /**
         * Returns the number of predefined
         * {@link MontageGenerator montage generators}.
         * @return the number of predefined montage generators
         */
	@Override
	public int getMontageGeneratorCount() {
		return montageGenerators.size();
	}

        /**
         * Returns the collection of predefined
         * {@link MontageGenerator montage generators}.
         * @return the collection of predefined montage generators
         */
	@Override
	public Collection<MontageGenerator> getMontageGenerators() {
		return montageGenerators;
	}

        /**
         * Finds the {@link MontageGenerator montage generator} of
         * a certain index
         * @param index index of MontageGenerator to be found
         * @return the found MontageGenerator
         */
	@Override
	public MontageGenerator getMontageGeneratorAt(int index) {
		return montageGenerators.get(index);
	}

}

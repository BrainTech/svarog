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
import org.signalml.domain.montage.Channel;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageGenerator;
import org.signalml.domain.montage.RawMontageGenerator;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.montage.eeg.EegChannel;
import org.signalml.domain.montage.eeg.LeftEarMontageGenerator;
import org.signalml.domain.montage.eeg.LinkedEarsMontageGenerator;
import org.signalml.domain.montage.eeg.LongitudinalIIMontageGenerator;
import org.signalml.domain.montage.eeg.LongitudinalIMontageGenerator;
import org.signalml.domain.montage.eeg.RightEarMontageGenerator;
import org.signalml.domain.montage.eeg.TransverseIMontageGenerator;
import org.signalml.domain.signal.AbstractSignalTypeConfigurer;
import org.signalml.domain.signal.SignalType;
import org.signalml.domain.signal.SignalTypeConfigurer;

/** EegSignalTypeConfigurer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EegSignalTypeConfigurer extends AbstractSignalTypeConfigurer implements SignalTypeConfigurer {

	private static final RawMontageGenerator rawMontageGenerator = new RawMontageGenerator();
	private static final List<MontageGenerator> montageGenerators = getAllMontageGenerators();

	private Image cachedBackdrop = null;
	private int cachedBackdropWidth;
	private int cachedBackdropHeight;

	private static List<MontageGenerator> getAllMontageGenerators() {
		ArrayList<MontageGenerator> generators = new ArrayList<MontageGenerator>();
		generators.add(rawMontageGenerator);
		generators.add(new LeftEarMontageGenerator());
		generators.add(new RightEarMontageGenerator());
		generators.add(new LinkedEarsMontageGenerator());
		generators.add(new LongitudinalIMontageGenerator());
		generators.add(new LongitudinalIIMontageGenerator());
		generators.add(new TransverseIMontageGenerator());

		return Collections.unmodifiableList(generators);
	}

	@Override
	public Montage createMontage(int channelCount) {

		Montage montage = new Montage(new SourceMontage(SignalType.EEG_10_20, channelCount));
		rawMontageGenerator.createMontage(montage);
		return montage;

	}

	@Override
	public Montage createMontage(SignalDocument signalDocument) {

		Montage montage = new Montage(new SourceMontage(signalDocument));
		rawMontageGenerator.createMontage(montage);
		return montage;

	}

	@Override
	public Channel[] allChannels() {
		return EegChannel.values();
	}

	@Override
	public Channel genericChannel() {
		return EegChannel.UNKNOWN;
	}

	@Override
	public Channel channelForName(String name) {
		if (name == null || name.isEmpty()) {
			return EegChannel.UNKNOWN;
		}
		Channel channel = EegChannel.forName(name);
		if (channel == null) {
			return EegChannel.UNKNOWN;
		}
		return channel;
	}

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

	@Override
	public int getMatrixHeight() {
		return EegChannel.MATRIX_HEIGHT;
	}

	@Override
	public int getMatrixWidth() {
		return EegChannel.MATRIX_WIDTH;
	}

	@Override
	public int getMontageGeneratorCount() {
		return montageGenerators.size();
	}

	@Override
	public Collection<MontageGenerator> getMontageGenerators() {
		return montageGenerators;
	}

	@Override
	public MontageGenerator getMontageGeneratorAt(int index) {
		return montageGenerators.get(index);
	}

}

/* GenericSignalTypeConfigurer.java created 2007-11-29
 *
 */

package org.signalml.domain.signal;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.signalml.app.document.SignalDocument;
import org.signalml.domain.montage.Channel;
import org.signalml.domain.montage.GenericChannel;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageGenerator;
import org.signalml.domain.montage.RawMontageGenerator;
import org.signalml.domain.montage.SourceMontage;

/** GenericSignalTypeConfigurer
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class GenericSignalTypeConfigurer extends AbstractSignalTypeConfigurer implements SignalTypeConfigurer {

	private static final RawMontageGenerator rawMontageGenerator = new RawMontageGenerator();
	private static final List<MontageGenerator> montageGenerators = getAllMontageGenerators();

	private static List<MontageGenerator> getAllMontageGenerators() {
		ArrayList<MontageGenerator> generators = new ArrayList<MontageGenerator>();
		generators.add(rawMontageGenerator);

		return Collections.unmodifiableList(generators);
	}

	@Override
	public Montage createMontage(int channelCount) {

		Montage montage = new Montage(new SourceMontage(SignalType.OTHER, channelCount));
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
		return GenericChannel.values();
	}

	@Override
	public Channel genericChannel() {
		return GenericChannel.UNKNOWN;
	}

	@Override
	public Channel channelForName(String name) {
		if (name == null || name.isEmpty()) {
			return GenericChannel.UNKNOWN;
		}
		Channel channel = GenericChannel.forName(name);
		if (channel == null) {
			return GenericChannel.UNKNOWN;
		}
		return channel;
	}

	@Override
	public Image getMatrixBackdrop(int width, int height) {
		return null;
	}

	@Override
	public int getMatrixHeight() {
		return 0;
	}

	@Override
	public int getMatrixWidth() {
		return 0;
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

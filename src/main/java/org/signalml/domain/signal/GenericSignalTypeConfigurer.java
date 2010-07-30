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

/**
 * This class represents a configurer for a raw {@link Montage montage}.
 * Uses predefined {@link RawMontageGenerator raw montage generator} and
 * has no backdrop.
 *
 * @see SignalTypeConfigurer
 * @see RawMontageGenerator
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class GenericSignalTypeConfigurer extends AbstractSignalTypeConfigurer implements SignalTypeConfigurer {


        /**
         * the predefined {@link RawMontageGenerator raw montage generator}
         */
	private static final RawMontageGenerator rawMontageGenerator = new RawMontageGenerator();

        /**
         * the constant list of predefined montageGenerators
         */
	private static final List<MontageGenerator> montageGenerators = getAllMontageGenerators();

        /**
         * Returns the constant list of predefined
         * {@link MontageGenerator montage generators}.
         * It consists of one element - <code>rawMontageGenerator</code>.
         * @return constant list of predefined montageGenerators
         */
	private static List<MontageGenerator> getAllMontageGenerators() {
		ArrayList<MontageGenerator> generators = new ArrayList<MontageGenerator>();
		generators.add(rawMontageGenerator);

		return Collections.unmodifiableList(generators);
	}

        /**
         * Creates a {@link Montage montage} with <code>channelCount</code>
         * channels for a raw signal
         * @param channelCount desired number of channels in the montage
         * @return the created montage
         */
	@Override
	public Montage createMontage(int channelCount) {

		Montage montage = new Montage(new SourceMontage(SignalType.OTHER, channelCount));
		rawMontageGenerator.createMontage(montage);
		return montage;

	}

        /**
         * Creates {@link Montage montage} based on the
         * {@link SignalDocument document} with the signal.
         * @param signalDocument the document with the signal
         * @return the created montage
         */
	@Override
	public Montage createMontage(SignalDocument signalDocument) {

		Montage montage = new Montage(new SourceMontage(signalDocument));
		rawMontageGenerator.createMontage(montage);
		return montage;

	}

        /**
         * Returns an array of {@link GenericChannel generic channels} of
         * possible types.
         * @return an array of generic channels of possible types
         */
	@Override
	public Channel[] allChannels() {
		return GenericChannel.values();
	}


        /**
         * Returns {@link GenericChannel generic channel} of type 'UNKNOWN'.
         * @return GenericChannel.UNKNOWN
         */
	@Override
	public Channel genericChannel() {
		return GenericChannel.UNKNOWN;
	}

        /**
         * Finds a {@link GenericChannel generic channel} of a given name.
         * @param name type of a channel name
         * @return the {@link Channel channel} of a given name
         */
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

        /**
         * Creates backdrop for signal positions matrix.
         * @param width width of a backdrop
         * @param height height of a backdrop
         * @return <code>null</code> because matrix doesn't exist
         */
	@Override
	public Image getMatrixBackdrop(int width, int height) {
		return null;
	}

        /**
         * Returns the height of a {@link Channel channels} matrix.
         * @return 0 because matrix doesn't exist
         */
	@Override
	public int getMatrixHeight() {
		return 0;
	}

        /**
         * Returns the width of a {@link Channel channels} matrix.
         * @return 0 because matrix doesn't exist
         */
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

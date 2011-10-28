/* SignalTypeConfigurer.java created 2007-11-22
 *
 */

package org.signalml.domain.signal;

import java.awt.Image;
import java.util.Collection;

import org.signalml.app.document.SignalDocument;
import org.signalml.domain.montage.Channel;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageGenerator;
import org.signalml.domain.montage.filter.SampleFilterDefinition;

/**
 * This is an interface for a configurer of {@link Montage montages}.
 * It configures (or creates) a montage to be of a specified
 * (by implementation) type.
 * Contains also the backdrop for a signal, {@link MontageGenerator generators}
 * and possible types of signal {@link Channel channels}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SignalTypeConfigurer {

        /**
         * Creates montage with channelCount channels.
         * @param channelCount desired number of channels for montage
         * @return created montage
         */
	Montage createMontage(int channelCount);

        /**
         * Creates montage based on document with a signal.
         * @param signalDocument the document with the signal
         * @return created montage
         */
	Montage createMontage(SignalDocument signalDocument);

        /**
         * Returns an array with {@link Channel channels} of possible types.
         * @return an array with Channels of possible types
         */
	Channel[] allChannels();

        /**
         * Returns the {@link Channel channel} that is generic for a montage
         * of this type.
         * @return the channel that is generic for a montage of this type
         */
	Channel genericChannel();

        /**
         * Finds a {@link Channel channel} of a given name from list of possible
         * types.
         * @param name type of a channel name
         * @return the channel of a given name
         */
	Channel channelForName(String name);

        /**
         * Returns the width of a {@link Channel channels} matrix.
         * @return the width of a channels matrix, 0 if matrix doesn't exist
         */
	int getMatrixWidth();

        /**
         * Returns the height of a {@link Channel channels} matrix.
         * @return the height of a channels matrix, 0 if matrix doesn't exist
         */
	int getMatrixHeight();

        /**
         * Creates backdrop for signal positions matrix.
         * @param width width of a backdrop
         * @param height height of a backdrop
         * @return an image with backdrop for signal positions matrix,
         * <code>null</code> if matrix doesn't exist
         */
	Image getMatrixBackdrop(int width, int height);

        /**
         * Returns the number of predefined
         * {@link MontageGenerator montage generators}.
         * @return the number of predefined montage generators
         */
	int getMontageGeneratorCount();

        /**
         * Returns the collection of predefined
         * {@link MontageGenerator montage generators}.
         * @return the collection of predefined montage generators
         */
	Collection<MontageGenerator> getMontageGenerators();

        /**
         * Finds the {@link MontageGenerator montage generator} of a given index
         * @param index the index of MontageGenerator to be found
         * @return the found montage generator
         */
	MontageGenerator getMontageGeneratorAt(int index);

}

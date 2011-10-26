/* SignalProcessingChain.java created 2008-01-27
 *
 */

package org.signalml.domain.signal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.nio.channels.Pipe.SourceChannel;
import java.util.Date;

import org.apache.log4j.Logger;
import org.signalml.app.SvarogApplication;
import org.signalml.app.document.MRUDEntry;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.RawSignalDocument;
import org.signalml.app.document.RawSignalMRUDEntry;
import org.signalml.app.document.SignalMLDocument;
import org.signalml.app.document.SignalMLMRUDEntry;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecReader;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageChannel;
import org.signalml.domain.montage.MontageMismatchException;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalSampleSource;
import org.signalml.domain.signal.space.SignalSourceLevel;
import org.signalml.exception.MissingCodecException;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.SignalMLException;

/**
 * This class represents the chain (series) of
 * {@link MultichannelSampleSource sample sources} from which the first
 * is the {@link OriginalMultichannelSampleSource original source of samples}
 * and the rest in the series is using previous as the source.
 * The types of sample sources in the series depend on the type of the chain.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalProcessingChain extends AbstractMultichannelSampleSource implements MultichannelSampleSource, PropertyChangeListener {

	protected static final Logger logger = Logger.getLogger(SignalProcessingChain.class);

	private boolean createdSource;

        /**
         * the {@link OriginalMultichannelSampleSource original source}
         * of samples
         */
	private OriginalMultichannelSampleSource source;
        /**
         * the {@link MultichannelSampleBuffer buffer} for the
         * {@link OriginalMultichannelSampleSource original source} of samples
         */
	private MultichannelSampleBuffer sourceBuffer;
        /**
         * the {@link MultichannelSampleMontage source} of samples for
         * the montage
         */
	private MultichannelSampleMontage montage;
        /**
         * the {@link MultichannelSampleBuffer buffer} for the
         * {@link MultichannelSampleMontage source} of samples for
         * the montage
         */
	private MultichannelSampleBuffer montageBuffer;

        /**
         * the {@link MultichannelSampleFilter source} of samples
         * for the filter
         */
	private MultichannelSampleFilter filter;

        /**
         * the last {@link MultichannelSampleSource sample source} in the
         * chain, used to return samples
         */
	private MultichannelSampleSource output;

        /**
         * Constructor. Creates an empty processing chain based on a
         * given {@link OriginalMultichannelSampleSource original source}
         * and the {@link SignalType type} of the signal.
         * It doesn't provide the ability to return samples.
         * @param source the original source of the signal
         * @param signalType the type of the signal
         */
	protected SignalProcessingChain(OriginalMultichannelSampleSource source) {
		super();

		this.source = source;

	}

        /**
         * Constructor. Creates the processing chain using the given
         * {@link SignalProcessingChainDescriptor descriptor}.
         * @param descriptor the descriptor of the chain
         * @throws IOException if there is an error while reading samples from
         * file
         * @throws SignalMLException if codec doesn't exist or some other error
         * with the codec
         */
	public SignalProcessingChain(SignalProcessingChainDescriptor descriptor) throws IOException, SignalMLException {
		super();

		MRUDEntry mrud = descriptor.getDocument();

		OriginalMultichannelSampleSource source = null;

		if (mrud instanceof SignalMLMRUDEntry) {

			SignalMLMRUDEntry smlEntry = (SignalMLMRUDEntry) mrud;
			SignalMLCodec codec = SvarogApplication.getSharedInstance().getSignalMLCodecManager().getCodecByUID(smlEntry.getCodecUID());
			if (codec == null) {
				logger.warn("Mrud codec not found for uid [" + smlEntry.getCodecUID() + "]");
				throw new MissingCodecException("error.mrudMissingCodecException");
			}

			SignalMLCodecReader reader = codec.createReader();
			reader.open(smlEntry.getPath());

			source = new SignalMLCodecSampleSource(reader);

			if (source.isCalibrationCapable()) {
				source.setCalibrationGain(smlEntry.getCalibrationGain());
			}
			if (!source.isSamplingFrequencyCapable()) {
				source.setSamplingFrequency(smlEntry.getSamplingFrequency());
			}
			if (!source.isChannelCountCapable()) {
				source.setChannelCount(smlEntry.getChannelCount());
			}

		}
		else if (mrud instanceof RawSignalMRUDEntry) {

			RawSignalMRUDEntry rawEntry = (RawSignalMRUDEntry) mrud;
			RawSignalDescriptor rawDescriptor = rawEntry.getDescriptor();

			source = new RawSignalSampleSource(rawEntry.getFile(), rawDescriptor.getChannelCount(), rawDescriptor.getSamplingFrequency(), rawDescriptor.getSampleType(), rawDescriptor.getByteOrder());
			source.setCalibrationGain(rawDescriptor.getCalibrationGain());
			source.setCalibrationOffset(rawDescriptor.getCalibrationOffset());

		} else {
			throw new SanityCheckException("Don't know how to open this kind of mrud [" + mrud.getClass().getName() + "]");
		}

		this.source = source;

		output = source;
		if (descriptor.isSourceBuffered()) {
			sourceBuffer = new MultichannelSampleBuffer(output, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
			output = sourceBuffer;
		}
		if (descriptor.isAssembled()) {
			montage = new MultichannelSampleMontage(output);
			montage.setCurrentMontage(descriptor.getMontage());
			output = montage;
			if (descriptor.isMontageBuffered()) {
				montageBuffer = new MultichannelSampleBuffer(output, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
				output = montageBuffer;
			}
		}
		if (descriptor.isFiltered()) {
			filter = new MultichannelSampleFilter(output, source);
			filter.setCurrentMontage(descriptor.getMontage());
			output = filter;
		}

		configureOutput();

	}

        /**
         * Adds this chain as a listener for the output
         * {@link MultichannelSampleSource source}.
         */
	protected void configureOutput() {
		output.addPropertyChangeListener(this);
	}

        @Override
	public void destroy() {

		output.removePropertyChangeListener(this);

		if (createdSource) {
			source.destroy();
		}

		if (filter != null) {
			filter.destroy();
		}

		if (montageBuffer != null) {
			montageBuffer.destroy();
		}

		if (montage != null) {
			montage.destroy();
		}

		if (sourceBuffer != null) {
			sourceBuffer.destroy();
		}

	}

        /**
         * Creates the simplest possible chain where the
         * {@link OriginalMultichannelSampleSource original source} of samples
         * is also the output.
         * @param source the original source of samples
         * @param signalType the {@link SignalType type} of the signal
         * @return the created chain
         */
	public static SignalProcessingChain createRawChain(OriginalMultichannelSampleSource source) {

		SignalProcessingChain chain = new SignalProcessingChain(source);
		chain.output = chain.source;

		chain.configureOutput();

		return chain;

	}

        /**
         * Creates the chain of {@link MultichannelSampleSource sources} that
         * is buffered but doesn't contain any montage and is not filtered.
         * <code>original source = source -> buffer = output</code>
         * @param source the 
         * {@link OriginalMultichannelSampleSource original source} of samples
         * @param signalType the {@link SignalType type} of the signal
         * @return the created chain
         */
	public static SignalProcessingChain createBufferedRawChain(OriginalMultichannelSampleSource source) {

		SignalProcessingChain chain = new SignalProcessingChain(source);
		chain.sourceBuffer = new MultichannelSampleBuffer(chain.source, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
		chain.output = chain.sourceBuffer;

		chain.configureOutput();

		return chain;

	}

        /**
         * Creates the chain:
         * <code>original source = source -> buffer -> montage = output</code>
         * @param source the
         * {@link OriginalMultichannelSampleSource original source} of samples
         * @param signalType the type of the signal
         * @return the created chain
         */
	public static SignalProcessingChain createAssembledChain(OriginalMultichannelSampleSource source) {

		SignalProcessingChain chain = new SignalProcessingChain(source);
		chain.sourceBuffer = new MultichannelSampleBuffer(chain.source, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
		chain.montage = new MultichannelSampleMontage(chain.sourceBuffer);
		chain.output = chain.montage;

		chain.configureOutput();

		return chain;

	}

        /**
         * Creates the chain:
         * <code>original source = source -> buffer -> montage
         * -> montage buffer = output</code>
         * @param source the
         * {@link OriginalMultichannelSampleSource original source} of samples
         * @param signalType the type of the signal
         * @return the created chain
         */
	public static SignalProcessingChain createBufferedAssembledChain(OriginalMultichannelSampleSource source) {

		SignalProcessingChain chain = new SignalProcessingChain(source);
		chain.sourceBuffer = new MultichannelSampleBuffer(chain.source, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
		chain.montage = new MultichannelSampleMontage(chain.sourceBuffer);
		chain.montageBuffer = new MultichannelSampleBuffer(chain.montage, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
		chain.output = chain.montageBuffer;

		chain.configureOutput();

		return chain;

	}

        /**
         * Creates the chain:
         * <code>original source = source -> montage
         * -> filter = output</code>
         * @param source the
         * {@link OriginalMultichannelSampleSource original source} of samples
         * @param signalType the type of the signal
         * @return the created chain
         */
        public static SignalProcessingChain createNotBufferedFilteredChain(OriginalMultichannelSampleSource source) {

		SignalProcessingChain chain = new SignalProcessingChain(source);
		chain.montage = new MultichannelSampleMontage(chain.source);
		chain.filter = new MultichannelSampleFilter(chain.montage, source);
		chain.output = chain.filter;

		chain.configureOutput();
	
		return chain;

	}
	public static SignalProcessingChain createFilteredChain(OriginalMultichannelSampleSource source) {

		SignalProcessingChain chain = new SignalProcessingChain(source);
		chain.sourceBuffer = new MultichannelSampleBuffer(chain.source, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
		chain.montage = new MultichannelSampleMontage(chain.sourceBuffer);
		chain.montageBuffer = new MultichannelSampleBuffer(chain.montage, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
		chain.filter = new MultichannelSampleFilter(chain.montageBuffer, source);
		chain.output = chain.filter;

		chain.configureOutput();

		return chain;

	}

	// NOTE! buffering filtered chain is risky, so no "createBufferedFilterChain" - be sure to know what you're doing if you decide to implement one
	// (filtered fragments may not meet correctly at the edges?)

        /**
         * Creates the simplest possible chain where the
         * {@link OriginalMultichannelSampleSource original source} of samples
         * is also the output.
         * Uses the same the {@link SignalType type} of the signal as it is used
         * in this chain.
         * @param sampleSource the original source of samples
         * @return the created chain
         * @throws SignalMLException never
         */
	protected SignalProcessingChain createRawLevelSharedChain(OriginalMultichannelSampleSource sampleSource) throws SignalMLException {

		SignalProcessingChain chain = new SignalProcessingChain(sampleSource);
		chain.output = chain.source;

		chain.configureOutput();

		return chain;

	}

        /**
         * Creates the simplest possible chain where the
         * {@link OriginalMultichannelSampleSource original source} of samples
         * is also the output.
         * Uses the same the {@link SignalType type} of the signal as it is used
         * in this chain.
         * Uses the same (shares) the original sample source with this chain.
         * @return the created chain
         * @throws SignalMLException never
         */
	public SignalProcessingChain createRawLevelSharedChain() throws SignalMLException {

		return createRawLevelSharedChain(this.getSource());

	}

        /**
         * Creates the simplest possible chain where the
         * {@link OriginalMultichannelSampleSource original source} of samples
         * is also the output.
         * Uses the same the {@link SignalType type} of the signal as it is used
         * in this chain.
         * Uses the copy of the original sample source from this chain.
         * @return the created chain
         * @throws SignalMLException if there is an error while duplicating
         * the original source
         */
	public SignalProcessingChain createRawLevelCopyChain() throws SignalMLException {

		OriginalMultichannelSampleSource sampleSource = this.getSource().duplicate();
		SignalProcessingChain chain = createRawLevelSharedChain(sampleSource);
		chain.createdSource = true;
		return chain;

	}

        /**
         * Creates the chain:
         * <code>original source = source -> buffer -> montage = output</code>.
         * Uses the same the {@link SignalType type} of the signal as it is used
         * in this chain.
         * Uses the same {@link Montage montage} as it is used in this chain.
         * @param sampleSource the
         * {@link OriginalMultichannelSampleSource original source} of samples
         * @return the created chain
         * @throws SignalMLException if the number of channels in
         * the original source is different then the number of source
         * channels in the montage.
         */
	protected SignalProcessingChain createAssembledLevelChain(OriginalMultichannelSampleSource sampleSource) throws SignalMLException {

		SignalProcessingChain chain = new SignalProcessingChain(sampleSource);
		chain.sourceBuffer = new MultichannelSampleBuffer(chain.source, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
		chain.montage = new MultichannelSampleMontage(chain.sourceBuffer);
		MultichannelSampleMontage baseMontage = this.getMontage();
		if (baseMontage != null) {
			chain.montage.setCurrentMontage(baseMontage.getCurrentMontage());
		}
		chain.output = chain.montage;

		chain.configureOutput();

		return chain;

	}

        /**
         * Creates the chain:
         * <code>original source = source -> buffer -> montage = output</code>.
         * Uses the same the {@link SignalType type} of the signal as it is used
         * in this chain.
         * Uses the same {@link Montage montage} as it is used in this chain.
         * Shares the {@link OriginalMultichannelSampleSource original source}
         * with this chain.
         * @return the created chain
         * @throws SignalMLException if the number of channels in
         * the original source is different then the number of source
         * channels in the montage.
         */
	public SignalProcessingChain createAssembledLevelSharedChain() throws SignalMLException {

		return createAssembledLevelChain(this.getSource());

	}

        /**
         * Creates the chain:
         * <code>original source = source -> buffer -> montage = output</code>.
         * Uses the same the {@link SignalType type} of the signal as it is used
         * in this chain.
         * Uses the same {@link Montage montage} as it is used in this chain.
         * Uses the copy of the
         * {@link OriginalMultichannelSampleSource original source} from this
         * chain.
         * @return the created chain
         * @throws SignalMLException if the number of channels in
         * the original source is different then the number of source
         * channels in the montage.
         */
	public SignalProcessingChain createAssembledLevelCopyChain() throws SignalMLException {

		OriginalMultichannelSampleSource sampleSource = this.getSource().duplicate();
		SignalProcessingChain chain = createAssembledLevelChain(sampleSource);
		chain.createdSource = true;
		return chain;

	}

        /**
         * Creates the chain:
         * <code>original source = source -> buffer -> montage
         * -> montage buffer -> filter = output</code>
         * Uses the same the {@link SignalType type} of the signal as it is used
         * in this chain.
         * Uses the same {@link Montage montage} as it is used in this chain.
         * @param sampleSource the
         * {@link OriginalMultichannelSampleSource original source} of samples
         * @return the created chain
         * @throws SignalMLException if the number of channels in
         * the original source is different then the number of source
         * channels in the montage.
         */
	protected SignalProcessingChain createFilteredLevelChain(OriginalMultichannelSampleSource sampleSource) throws SignalMLException {

		SignalProcessingChain chain = new SignalProcessingChain(sampleSource);
		chain.sourceBuffer = new MultichannelSampleBuffer(chain.source, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
		chain.montage = new MultichannelSampleMontage(chain.sourceBuffer);
		// no montage buffer is used
		chain.filter = new MultichannelSampleFilter(chain.montage, sampleSource);
		MultichannelSampleMontage baseMontage = this.getMontage();
		if (baseMontage != null) {
			Montage currentBaseMontage = baseMontage.getCurrentMontage();
			chain.montage.setCurrentMontage(currentBaseMontage);
			chain.filter.setCurrentMontage(currentBaseMontage);
		}
		chain.output = chain.filter;

		chain.configureOutput();

		return chain;

	}

        /**
         * Creates the chain:
         * <code>original source = source -> buffer -> montage
         * -> montage buffer -> filter = output</code>
         * Uses the same the {@link SignalType type} of the signal as it is used
         * in this chain.
         * Uses the same {@link Montage montage} as it is used in this chain.
         * Shares the {@link OriginalMultichannelSampleSource original source}
         * with this chain.
         * @return the created chain
         * @throws SignalMLException if the number of channels in
         * the original source is different then the number of source
         * channels in the montage.
         */
	public SignalProcessingChain createFilteredLevelSharedChain() throws SignalMLException {

		return createFilteredLevelChain(this.getSource());

	}

        /**
         * Creates the chain:
         * <code>original source = source -> buffer -> montage
         * -> montage buffer -> filter = output</code>
         *  Uses the same the {@link SignalType type} of the signal as it is used
         * in this chain.
         * Uses the same {@link Montage montage} as it is used in this chain.
         * Uses the copy of the
         * {@link OriginalMultichannelSampleSource original source} from this
         * chain.
         * @return the created chain
         * @throws SignalMLException if the number of channels in
         * the original source is different then the number of source
         * channels in the montage.
         */
	public SignalProcessingChain createFilteredLevelCopyChain() throws SignalMLException {

		OriginalMultichannelSampleSource sampleSource = this.getSource().duplicate();
		SignalProcessingChain chain = createFilteredLevelChain(sampleSource);
		chain.createdSource = true;
		return chain;

	}

        /**
         * Creates the chain of the given {@link SignalSourceLevel level}.
         * FILTERED:
         * <code>original source = source -> buffer -> montage
         * -> montage buffer -> filter = output</code>
         * ASSEMBLED:
         * <code>original source = source -> buffer -> montage
         * -> montage buffer = output</code>
         * RAW:
         * <code>original source = source = output</code>
         * Uses the same the {@link SignalType type} of the signal as it is used
         * in this chain.
         * Uses the same (shares)
         * {@link OriginalMultichannelSampleSource original source} as in this
         * chain.
         * If necessary uses the same {@link Montage montage} as it is used
         * in this chain.
         * @param level the desired level of the chain
         * @return the created chain
         * @throws SignalMLException if the number of channels in
         * the original source is different then the number of source
         * channels in the montage.
         */
	public SignalProcessingChain createLevelSharedChain(SignalSourceLevel level) throws SignalMLException {

		switch (level) {

		case FILTERED :
			return createFilteredLevelSharedChain();

		case ASSEMBLED :
			return createAssembledLevelSharedChain();

		case RAW :
		default :
			return createRawLevelSharedChain();

		}

	}

        /**
         * Creates the chain of the given {@link SignalSourceLevel level}.
         * FILTERED:
         * <code>original source = source -> buffer -> montage
         * -> montage buffer -> filter = output</code>
         * ASSEMBLED:
         * <code>original source = source -> buffer -> montage
         * -> montage buffer = output</code>
         * RAW:
         * <code>original source = source = output</code>
         * Uses the same the {@link SignalType type} of the signal as it is used
         * in this chain.
         * Uses the copy of the
         * {@link OriginalMultichannelSampleSource original source} from this
         * chain.
         * If necessary uses the same {@link Montage montage} as it is used
         * in this chain.
         * @param level the desired level of the chain
         * @return the created chain
         * @throws SignalMLException if the number of channels in
         * the original source is different then the number of source
         * channels in the montage.
         */
	public SignalProcessingChain createLevelCopyChain(SignalSourceLevel level) throws SignalMLException {

		switch (level) {

		case FILTERED :
			return createFilteredLevelCopyChain();

		case ASSEMBLED :
			return createAssembledLevelCopyChain();

		case RAW :
		default :
			return createRawLevelCopyChain();

		}

	}

        /**
         * Creates the {@link SignalProcessingChainDescriptor descriptor}
         * describing this chain and stores in it all possible information.
         * @return the created descriptor
         */
	public SignalProcessingChainDescriptor createDescriptor() {

		SignalProcessingChainDescriptor descriptor = new SignalProcessingChainDescriptor();

		descriptor.setFiltered(filter != null);
		descriptor.setMontageBuffered(montageBuffer != null);
		if (montage != null) {
			descriptor.setAssembled(true);
			descriptor.setMontage(new Montage(montage.getCurrentMontage()));
		} else {
			descriptor.setAssembled(false);
			descriptor.setMontage(null);
		}
		descriptor.setSourceBuffered(sourceBuffer != null);

		if (source instanceof SignalMLCodecSampleSource) {

			SignalMLCodecSampleSource codecSource = (SignalMLCodecSampleSource) source;
			SignalMLCodecReader reader = codecSource.getReader();
			SignalMLCodec codec = reader.getCodec();

			SignalMLMRUDEntry mrud = new SignalMLMRUDEntry(ManagedDocumentType.SIGNAL, SignalMLDocument.class, reader.getCurrentFilename(), codec.getSourceUID(), codec.getFormatName());
			mrud.setLastTimeOpened(new Date());
			mrud.setPageSize(-1F);
			mrud.setBlocksPerPage(-1);
			mrud.setSamplingFrequency(codecSource.getSamplingFrequency());
			mrud.setChannelCount(codecSource.getChannelCount());
			mrud.setCalibrationGain(codecSource.getSingleCalibrationGain());

			descriptor.setDocument(mrud);

		}
		else if (source instanceof RawSignalSampleSource) {

			RawSignalSampleSource rawSource = (RawSignalSampleSource) source;
			RawSignalDescriptor rawDescriptor = new RawSignalDescriptor();

			rawDescriptor.setSamplingFrequency(rawSource.getSamplingFrequency());
			rawDescriptor.setSampleCount(rawSource.getSampleCount());
			rawDescriptor.setChannelCount(rawSource.getChannelCount());
			rawDescriptor.setCalibrationGain(rawSource.getCalibrationGain());
			rawDescriptor.setSampleType(rawSource.getSampleType());
			rawDescriptor.setByteOrder(rawSource.getByteOrder());

			RawSignalMRUDEntry mrud = new RawSignalMRUDEntry(ManagedDocumentType.SIGNAL, RawSignalDocument.class, rawSource.getFile().getAbsolutePath(), rawDescriptor);
			mrud.setLastTimeOpened(new Date());

			descriptor.setDocument(mrud);

		} else {

			throw new SanityCheckException("Unsupported sample source type: " + source.getClass().getName());

		}

		return descriptor;

	}

	@Override
	public int getChannelCount() {
		return output.getChannelCount();
	}

	@Override
	public int getDocumentChannelIndex(int channel) {
		return output.getDocumentChannelIndex(channel);
	}

	@Override
	public String getLabel(int channel) {
		return output.getLabel(channel);
	}

	@Override
	public int getSampleCount(int channel) {
		return output.getSampleCount(channel);
	}

	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {
		output.getSamples(channel, target, signalOffset, count, arrayOffset);
	}

	@Override
	public float getSamplingFrequency() {
		return output.getSamplingFrequency();
	}

        /**
         * Returns if the last source in the chain (<code>output</code>
         * is capable of returning a channel count
         * @return true if the last source in the chain (<code>output</code>
         * is capable of returning a channel count, false otherwise
         */
	@Override
	public boolean isChannelCountCapable() {
		return output.isChannelCountCapable();
	}

        /**
         * Returns if the last source in the chain (<code>output</code>
         * is capable of returning a sampling frequency
         * @return true if the last source in the chain (<code>output</code>
         * is capable of returning a sampling frequency, false otherwise
         */
	@Override
	public boolean isSamplingFrequencyCapable() {
		return output.isSamplingFrequencyCapable();
	}

        /**
         * Fires all listeners that this chain has changed
         * @param evt an event describing the change
         */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		pcSupport.firePropertyChange(evt);
	}

        /**
         * Returns the {@link OriginalMultichannelSampleSource original source}
         * of samples.
         * @return the original source of samples
         */
	public OriginalMultichannelSampleSource getSource() {
		return source;
	}

        /**
         * Returns the {@link MultichannelSampleBuffer buffer} for the
         * {@link OriginalMultichannelSampleSource original source} of samples.
         * @return the buffer for the original source of samples.
         */
	public MultichannelSampleBuffer getSourceBuffer() {
		return sourceBuffer;
	}

        /**
         * Returns the {@link MultichannelSampleMontage source} of samples for
         * the montage.
         * @return the source of samples for the montage
         */
	public MultichannelSampleMontage getMontage() {
		return montage;
	}

        /**
         * Returns the {@link MultichannelSampleBuffer buffer} for the
         * {@link MultichannelSampleMontage source} of samples for
         * the montage.
         * @return the buffer for the source of samples for the montage
         */
	public MultichannelSampleBuffer getMontageBuffer() {
		return montageBuffer;
	}

        /**
         * Returns the {@link MultichannelSampleFilter source} of samples
         * for the filter.
         * @return the source of samples for the filter.
         */
	public MultichannelSampleFilter getFilter() {
		return filter;
	}

        /**
         * Returns the last {@link MultichannelSampleSource source}
         * in the chain.
         * @return the last source in the chain
         */
	public MultichannelSampleSource getOutput() {
		return output;
	}

        /**
         * Changes the actual {@link Montage montage} in the
         * {@link MultichannelSampleMontage source} of samples for the montage
         * and in the {@link MultichannelSampleFilter source} of samples for the
         * filter
         * @param montageDef the new montage to be set
         * @throws MontageMismatchException if the number of source channels in
         * the montage is different then the number of channels in the source
         */
	public void applyMontageDefinition(Montage montageDef) throws MontageMismatchException {

		if (montage != null) {
			montage.setCurrentMontage(montageDef);
		}
		if (filter != null) {
			filter.setCurrentMontage(montageDef);
		}

	}

	/**
         * Changes the actual {@link Montage montage} in the
         * {@link MultichannelSampleMontage source} of samples for the montage
         * but doesn't change it in the {@link MultichannelSampleFilter source}
	 * of samples for the filter.
	 *
	 * Therefore, it should only be used when filters did not change.
	 *
         * @param montageDef the new montage to be set
         * @throws MontageMismatchException if the number of source channels in
         * the montage is different then the number of channels in the source
         */
	public void applyMontageDefinitionWithoutfilters(Montage montageDef) throws MontageMismatchException {
		if (montage != null) {
			montage.setCurrentMontage(montageDef);
		}
	}

        /**
         * Returns indexes of {@link MontageChannel montage channels} in the
         * {@link Montage montage} in which the given
         * {@link SourceChannel source channel} is the primary channel.
         * If there is no montage only index of provided channel is returned.
         * @param channel the index of the source channel
         * @return an array of indexes of montage channels in which
         * the given source channel is the primary channel.
         */
	public int[] getDependantChannelIndices(int channel) {

		if (montage == null) {
			return new int[] { channel };
		} else {
			return montage.getMontageChannelIndices(channel);
		}

	}

        /**
         * Returns an array of labels of channels in the <code>output</code>
         * {@link MultichannelSampleSource source} of samples.
         * @return an array of labels of channels in the <code>output</code>
         * source of samples
         */
	public String[] getLabels() {

		if (montage == null) {
			int channelCount = getChannelCount();
			String[] labels = new String[channelCount];
			for (int i=0; i<channelCount; i++) {
				labels[i] = getLabel(i);
			}
			return labels;
		} else {
			return montage.getLabels();
		}

	}

        /**
         * Returns the label of the {@link SourceChannel source channel} of a
         * given index
         * @param channel the index of a channel
         * @return string with a label of the channel
         */
	public String getPrimaryLabel(int channel) {

		if (montage == null) {
			return output.getLabel(channel);
		} else {
			return montage.getPrimaryLabel(channel);
		}

	}

}

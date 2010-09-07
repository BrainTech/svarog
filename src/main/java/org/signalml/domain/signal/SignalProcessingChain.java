/* SignalProcessingChain.java created 2008-01-27
 * 
 */

package org.signalml.domain.signal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
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
import org.signalml.domain.montage.MontageMismatchException;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalSampleSource;
import org.signalml.domain.signal.space.SignalSourceLevel;
import org.signalml.exception.MissingCodecException;
import org.signalml.exception.SanityCheckException;
import org.signalml.exception.SignalMLException;

/** SignalProcessingChain
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalProcessingChain extends AbstractMultichannelSampleSource implements MultichannelSampleSource, PropertyChangeListener {

	protected static final Logger logger = Logger.getLogger(SignalProcessingChain.class);
	
	private SignalType signalType;
	
	private boolean createdSource;
	
	private OriginalMultichannelSampleSource source;
	private MultichannelSampleBuffer sourceBuffer;
	private MultichannelSampleMontage montage;
	private MultichannelSampleBuffer montageBuffer;	
	
	private MultichannelSampleFilter filter;
	
	private MultichannelSampleSource output;
	
	protected SignalProcessingChain(OriginalMultichannelSampleSource source, SignalType signalType) {
		super();
		
		this.source = source;
		this.signalType = signalType;

	}
	
	public SignalProcessingChain(SignalProcessingChainDescriptor descriptor) throws IOException, SignalMLException {
		super();
		
		MRUDEntry mrud = descriptor.getDocument();
		
		OriginalMultichannelSampleSource source = null;
		
		if(mrud instanceof SignalMLMRUDEntry) {
			
			SignalMLMRUDEntry smlEntry = (SignalMLMRUDEntry) mrud;
			SignalMLCodec codec = SvarogApplication.getSignalMLCodecManager().getCodecByUID(smlEntry.getCodecUID());
			if (codec == null) {
				logger.warn("Mrud codec not found for uid [" + smlEntry.getCodecUID() + "]");
				throw new MissingCodecException("error.mrudMissingCodecException");
			}
			
			SignalMLCodecReader reader = codec.createReader();
			reader.open( smlEntry.getPath() );
			
			source = new SignalMLCodecSampleSource(reader);
			
			if( source.isCalibrationCapable() ) {
				source.setCalibration( smlEntry.getCalibration() );
			}
			if( !source.isSamplingFrequencyCapable() ) {
				source.setSamplingFrequency( smlEntry.getSamplingFrequency() );				
			}
			if( !source.isChannelCountCapable() ) {
				source.setChannelCount( smlEntry.getChannelCount() );
			}
			
		}
		else if( mrud instanceof RawSignalMRUDEntry ) {
			
			RawSignalMRUDEntry rawEntry = (RawSignalMRUDEntry) mrud;
			RawSignalDescriptor rawDescriptor = rawEntry.getDescriptor();
			
			source = new RawSignalSampleSource(rawEntry.getFile(), rawDescriptor.getChannelCount(), rawDescriptor.getSamplingFrequency(), rawDescriptor.getSampleType(), rawDescriptor.getByteOrder() );
			source.setCalibration( rawDescriptor.getCalibration() );
			
		}
		else {
			throw new SanityCheckException("Don't know how to open this kind of mrud [" + mrud.getClass().getName() + "]");
		}
		
		this.source = source;
		this.signalType = descriptor.getType();

		output = source;
		if( descriptor.isSourceBuffered() ) {
			sourceBuffer = new MultichannelSampleBuffer(output, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
			output = sourceBuffer;
		}
		if( descriptor.isAssembled() ) {
			montage = new MultichannelSampleMontage(signalType, output);
			montage.setCurrentMontage( descriptor.getMontage() );
			output = montage;
			if( descriptor.isMontageBuffered() ) {			
				montageBuffer = new MultichannelSampleBuffer(output, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
				output = montageBuffer;
			}
		}
		if( descriptor.isFiltered() ) {
			filter = new MultichannelSampleFilter(output,source);
			filter.setCurrentMontage( descriptor.getMontage() );
			output = filter;
		}
		
		configureOutput();
				
	}
	
	protected void configureOutput() {
		output.addPropertyChangeListener(this);
	}
	
	public void destroy() {

		output.removePropertyChangeListener(this);
		
		if( createdSource ) {
			source.destroy();
		}
		
		if( filter != null ) {
			filter.destroy();
		}
		
		if( montageBuffer != null ) {
			montageBuffer.destroy();
		}
		
		if( montage != null ) {
			montage.destroy();
		}
				
		if( sourceBuffer != null ) {
			sourceBuffer.destroy();
		}
		
	}
	
	public static SignalProcessingChain createRawChain(OriginalMultichannelSampleSource source, SignalType signalType) {
		
		SignalProcessingChain chain = new SignalProcessingChain(source,signalType);
		chain.output = chain.source;
		
		chain.configureOutput();
	
		return chain;
		
	}
	
	public static SignalProcessingChain createBufferedRawChain(OriginalMultichannelSampleSource source, SignalType signalType) {

		SignalProcessingChain chain = new SignalProcessingChain(source,signalType);
		chain.sourceBuffer = new MultichannelSampleBuffer(chain.source, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
		chain.output = chain.sourceBuffer;
		
		chain.configureOutput();
	
		return chain;
		
	}
	
	public static SignalProcessingChain createAssembledChain(OriginalMultichannelSampleSource source, SignalType signalType) {

		SignalProcessingChain chain = new SignalProcessingChain(source,signalType);
		chain.sourceBuffer = new MultichannelSampleBuffer(chain.source, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
		chain.montage = new MultichannelSampleMontage(signalType, chain.sourceBuffer);
		chain.output = chain.montage;
		
		chain.configureOutput();
	
		return chain;
		
	}
	
	public static SignalProcessingChain createBufferedAssembledChain(OriginalMultichannelSampleSource source, SignalType signalType) {

		SignalProcessingChain chain = new SignalProcessingChain(source,signalType);
		chain.sourceBuffer = new MultichannelSampleBuffer(chain.source, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
		chain.montage = new MultichannelSampleMontage(signalType, chain.sourceBuffer);
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
        public static SignalProcessingChain createNotBufferedFilteredChain(OriginalMultichannelSampleSource source, SignalType signalType) {

		SignalProcessingChain chain = new SignalProcessingChain(source,signalType);
		chain.montage = new MultichannelSampleMontage(signalType, chain.source);
		chain.filter = new MultichannelSampleFilter(chain.montage,source);
		chain.output = chain.filter;

		chain.configureOutput();
	
		return chain;

	}

	public static SignalProcessingChain createFilteredChain(OriginalMultichannelSampleSource source, SignalType signalType) {

		SignalProcessingChain chain = new SignalProcessingChain(source,signalType);
		chain.sourceBuffer = new MultichannelSampleBuffer(chain.source, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
		chain.montage = new MultichannelSampleMontage(signalType, chain.sourceBuffer);
		chain.montageBuffer = new MultichannelSampleBuffer(chain.montage, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
		chain.filter = new MultichannelSampleFilter(chain.montageBuffer);
		chain.output = chain.filter;
		
		chain.configureOutput();
	
		return chain;
		
	}

	// NOTE! buffering filtered chain is risky, so no "createBufferedFilterChain" - be sure to know what you're doing if you decide to implement one
	// (filtered fragments may not meet correctly at the edges?)

	protected SignalProcessingChain createRawLevelSharedChain( OriginalMultichannelSampleSource sampleSource ) throws SignalMLException {
		
		SignalProcessingChain chain = new SignalProcessingChain( sampleSource, this.getSignalType() );
		chain.output = chain.source;
		
		chain.configureOutput();
	
		return chain;		
		
	}
	
	public SignalProcessingChain createRawLevelSharedChain() throws SignalMLException {
				
		return createRawLevelSharedChain( this.getSource() );		
		
	}
	
	public SignalProcessingChain createRawLevelCopyChain() throws SignalMLException {
		
		OriginalMultichannelSampleSource sampleSource = this.getSource().duplicate();			
		SignalProcessingChain chain = createRawLevelSharedChain( sampleSource );
		chain.createdSource = true;
		return chain;
		
	}

	protected SignalProcessingChain createAssembledLevelChain( OriginalMultichannelSampleSource sampleSource ) throws SignalMLException {
		
		SignalType signalType = this.getSignalType();
		SignalProcessingChain chain = new SignalProcessingChain( sampleSource, signalType );
		chain.sourceBuffer = new MultichannelSampleBuffer(chain.source, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
		chain.montage = new MultichannelSampleMontage(signalType, chain.sourceBuffer);
		MultichannelSampleMontage baseMontage = this.getMontage();
		if( baseMontage != null ) {
			chain.montage.setCurrentMontage( baseMontage.getCurrentMontage() );
		}
		chain.output = chain.montage;
		
		chain.configureOutput();
	
		return chain;
		
	}
	
	public SignalProcessingChain createAssembledLevelSharedChain() throws SignalMLException {
				
		return createAssembledLevelChain(this.getSource());
						
	}
		
	public SignalProcessingChain createAssembledLevelCopyChain() throws SignalMLException {
		
		OriginalMultichannelSampleSource sampleSource = this.getSource().duplicate();
		SignalProcessingChain chain = createAssembledLevelChain(sampleSource);
		chain.createdSource = true;
		return chain;
				
	}

	protected SignalProcessingChain createFilteredLevelChain( OriginalMultichannelSampleSource sampleSource ) throws SignalMLException {
		
		SignalType signalType = this.getSignalType();
		SignalProcessingChain chain = new SignalProcessingChain( sampleSource, signalType );
		chain.sourceBuffer = new MultichannelSampleBuffer(chain.source, MultichannelSampleBuffer.INITIAL_BUFFER_SIZE);
		chain.montage = new MultichannelSampleMontage(signalType, chain.sourceBuffer);
		// no montage buffer is used
		chain.filter = new MultichannelSampleFilter(chain.montage);
		MultichannelSampleMontage baseMontage = this.getMontage();
		if( baseMontage != null ) {
			Montage currentBaseMontage = baseMontage.getCurrentMontage();
			chain.montage.setCurrentMontage( currentBaseMontage );
			chain.filter.setCurrentMontage( currentBaseMontage );
		}
		chain.output = chain.filter;
		
		chain.configureOutput();
	
		return chain;
				
	}
	
	public SignalProcessingChain createFilteredLevelSharedChain() throws SignalMLException {
				
		return createFilteredLevelChain(this.getSource());
				
	}
	
	public SignalProcessingChain createFilteredLevelCopyChain() throws SignalMLException {
		
		OriginalMultichannelSampleSource sampleSource = this.getSource().duplicate();		
		SignalProcessingChain chain = createFilteredLevelChain(sampleSource);
		chain.createdSource = true;
		return chain;
				
	}

	public SignalProcessingChain createLevelSharedChain( SignalSourceLevel level ) throws SignalMLException {
		
		switch( level ) {
		
		case FILTERED :
			return createFilteredLevelSharedChain();
		
		case ASSEMBLED :
			return createAssembledLevelSharedChain();
			
		case RAW :
		default :
			return createRawLevelSharedChain();
			
		}
		
	}
	
	public SignalProcessingChain createLevelCopyChain( SignalSourceLevel level ) throws SignalMLException {
	
		switch( level ) {
		
		case FILTERED :
			return createFilteredLevelCopyChain();
		
		case ASSEMBLED :
			return createAssembledLevelCopyChain();
			
		case RAW :
		default :
			return createRawLevelCopyChain();
			
		}
		
	}
	
	public SignalProcessingChainDescriptor createDescriptor() {
		
		SignalProcessingChainDescriptor descriptor = new SignalProcessingChainDescriptor();
		descriptor.setType(signalType);
		
		descriptor.setFiltered( filter != null );
		descriptor.setMontageBuffered( montageBuffer != null );
		if( montage != null ) {
			descriptor.setAssembled(true);
			descriptor.setMontage( new Montage( montage.getCurrentMontage() ) );
		} else {
			descriptor.setAssembled(false);
			descriptor.setMontage(null);
		}
		descriptor.setSourceBuffered( sourceBuffer != null );
		
		if( source instanceof SignalMLCodecSampleSource ) {
			
			SignalMLCodecSampleSource codecSource = (SignalMLCodecSampleSource) source;
			SignalMLCodecReader reader = codecSource.getReader();
			SignalMLCodec codec = reader.getCodec();
								
			SignalMLMRUDEntry mrud = new SignalMLMRUDEntry(ManagedDocumentType.SIGNAL, SignalMLDocument.class, reader.getCurrentFilename(), codec.getSourceUID(), codec.getFormatName());
			mrud.setLastTimeOpened(new Date());
			mrud.setPageSize(-1F);
			mrud.setBlocksPerPage(-1);
			mrud.setSamplingFrequency(codecSource.getSamplingFrequency());
			mrud.setChannelCount(codecSource.getChannelCount());
			mrud.setCalibration(codecSource.getCalibration());
			
			descriptor.setDocument(mrud);
			
		}
		else if( source instanceof RawSignalSampleSource ) {
			
			RawSignalSampleSource rawSource = (RawSignalSampleSource) source;
			RawSignalDescriptor rawDescriptor = new RawSignalDescriptor();

			rawDescriptor.setSamplingFrequency( rawSource.getSamplingFrequency() );
			rawDescriptor.setSampleCount( rawSource.getSampleCount() );
			rawDescriptor.setChannelCount( rawSource.getChannelCount() );
			rawDescriptor.setCalibration( rawSource.getCalibration() );
			rawDescriptor.setSampleType( rawSource.getSampleType() );
			rawDescriptor.setByteOrder( rawSource.getByteOrder() );
			
			RawSignalMRUDEntry mrud = new RawSignalMRUDEntry(ManagedDocumentType.SIGNAL, RawSignalDocument.class, rawSource.getFile().getAbsolutePath(), rawDescriptor );
			mrud.setLastTimeOpened(new Date());

			descriptor.setDocument(mrud);
			
		}
		else {
			
			throw new SanityCheckException( "Unsupported sample source type: " + source.getClass().getName() );
			
		}
		
		return descriptor;
		
	}
		
	@Override
	public float getCalibration() {
		return output.getCalibration();
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

	@Override
	public boolean isCalibrationCapable() {
		return output.isCalibrationCapable();
	}

	@Override
	public boolean isChannelCountCapable() {
		return output.isChannelCountCapable();
	}

	@Override
	public boolean isSamplingFrequencyCapable() {
		return output.isSamplingFrequencyCapable();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		pcSupport.firePropertyChange(evt);
	}

	public SignalType getSignalType() {
		return signalType;
	}

	public OriginalMultichannelSampleSource getSource() {
		return source;
	}

	public MultichannelSampleBuffer getSourceBuffer() {
		return sourceBuffer;
	}

	public MultichannelSampleMontage getMontage() {
		return montage;
	}

	public MultichannelSampleBuffer getMontageBuffer() {
		return montageBuffer;
	}

	public MultichannelSampleFilter getFilter() {
		return filter;
	}

	public MultichannelSampleSource getOutput() {
		return output;
	}
	
	public void applyMontageDefinition( Montage montageDef ) throws MontageMismatchException {
		
		if( montage != null ) {
			montage.setCurrentMontage(montageDef);
		}
		if( filter != null ) {
			filter.setCurrentMontage(montageDef);
		}
		
	}
	
	public int[] getDependantChannelIndices(int channel) {
		
		if( montage == null ) {
			return new int[] { channel };
		} else {
			return montage.getMontageChannelIndices(channel);
		}

	}
	
	public String[] getLabels() {
		
		if( montage == null ) {
			int channelCount = getChannelCount();
			String[] labels = new String[channelCount];
			for( int i=0; i<channelCount; i++ ) {
				labels[i] = getLabel(i);
			}
			return labels;
		} else {
			return montage.getLabels();
		}
		
	}
	
	public String getPrimaryLabel( int channel ) {
		
		if( montage == null ) {
			return output.getLabel(channel);
		} else {
			return montage.getPrimaryLabel(channel);
		}
		
	}
	
}

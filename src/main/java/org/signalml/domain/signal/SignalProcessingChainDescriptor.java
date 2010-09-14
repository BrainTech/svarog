/* SignalProcessingChainDescriptor.java created 2008-01-27
 *
 */

package org.signalml.domain.signal;

import org.signalml.app.document.MRUDEntry;
import org.signalml.domain.montage.Montage;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * This class represents the description of the
 * {@link SignalProcessingChain signal processing chain}.
 * Description contains the {@link SignalType type} of the signal,
 * document with the signal, the {@link Montage} used and the information
 * if successive types of {@link MultichannelSampleSource sources} are used 
 * in the chain.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("sigprocchain")
public class SignalProcessingChainDescriptor {

        /**
         * the {@link SignalType type} of the signal
         */
	private SignalType type;

        /**
         * the {@link MRUDEntry entry} with the file with the signal
         */
	private MRUDEntry document;
        /**
         * the {@link Montage montage} used in the described
         * {@link SignalProcessingChain chain}
         */
	private Montage montage;

        /**
         * if the {@link OriginalMultichannelSampleSource original source}
         * of samples is {@link MultichannelSampleBuffer buffered}
         */
	private boolean sourceBuffered;
        /**
         * if the chain contains the
         * {@link MultichannelSampleMontage source of montage samples}
         */
	private boolean assembled;
        /**
         * if the {@link MultichannelSampleMontage source of montage samples}
         * is {@link MultichannelSampleBuffer buffered}
         */
	private boolean montageBuffered;
        /**
         * if the {@link SignalProcessingChain chain} contains a
         * {@link MultichannelSampleFilter filter}
         */
	private boolean filtered;

        /**
         * Constructor. Creates an empty descriptor.
         */
	public SignalProcessingChainDescriptor() {
	}

        /**
         * Returns the {@link SignalType type} of the signal.
         * @return the type of the signal
         */
	public SignalType getType() {
		return type;
	}

        /**
         * Sets the {@link SignalType type} of the signal.
         * @param type the type to be set
         */
	public void setType(SignalType type) {
		this.type = type;
	}

        /**
         * Returns the {@link MRUDEntry entry} with the file with the signal/
         * @return the entry with the file with the signal
         */
	public MRUDEntry getDocument() {
		return document;
	}

        /**
         * Sets the {@link MRUDEntry entry} with the file with the signal.
         * @param document the entry to be set
         */
	public void setDocument(MRUDEntry document) {
		this.document = document;
	}

        /**
         * Returns the {@link Montage montage} used in the described
         * {@link SignalProcessingChain chain}.
         * @return the montage used in the described chain
         */
	public Montage getMontage() {
		return montage;
	}

        /**
         * Sets the {@link Montage montage} used in the described
         * {@link SignalProcessingChain chain}.
         * @param montage the montage to be set
         */
	public void setMontage(Montage montage) {
		this.montage = montage;
	}

        /**
         * Returns if the {@link OriginalMultichannelSampleSource original source}
         * of samples is {@link MultichannelSampleBuffer buffered}.
         * @return true if the original source of samples is buffered,
         * false otherwise
         */
	public boolean isSourceBuffered() {
		return sourceBuffered;
	}

        /**
         * Sets if the {@link OriginalMultichannelSampleSource original source}
         * of samples is {@link MultichannelSampleBuffer buffered}.
         * @param sourceBuffered true if the original source of samples is
         * buffered, false otherwise
         */
	public void setSourceBuffered(boolean sourceBuffered) {
		this.sourceBuffered = sourceBuffered;
	}

        /**
         * Returns if the chain contains the
         * {@link MultichannelSampleMontage source of montage samples}.
         * @return true if the chain contains the source of montage samples,
         * false otherwise
         */
	public boolean isAssembled() {
		return assembled;
	}

        /**
         * Sets if the chain contains the
         * {@link MultichannelSampleMontage source of montage samples}.
         * @param assembled true if the chain contains the source of montage samples,
         * false otherwise
         */
	public void setAssembled(boolean assembled) {
		this.assembled = assembled;
	}

        /**
         * Returns if the 
         * {@link MultichannelSampleMontage source of montage samples}
         * is {@link MultichannelSampleBuffer buffered}.
         * @return true if the source of montage samples is buffered, false
         * otherwise
         */
	public boolean isMontageBuffered() {
		return montageBuffered;
	}

        /**
         * Sets if the {@link MultichannelSampleMontage source of montage samples}
         * is {@link MultichannelSampleBuffer buffered}.
         * @param montageBuffered true if the source of montage samples is
         * buffered, false otherwise
         */
	public void setMontageBuffered(boolean montageBuffered) {
		this.montageBuffered = montageBuffered;
	}

        /**
         * Returns if the {@link SignalProcessingChain chain} contains a
         * {@link MultichannelSampleFilter filter}.
         * @return true if the chain contains a filter, false otherwise
         */
	public boolean isFiltered() {
		return filtered;
	}

        /**
         * Sets if the {@link SignalProcessingChain chain} contains a
         * {@link MultichannelSampleFilter filter}.
         * @param filtered true if the chain contains a filter, false otherwise
         */
	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}

}

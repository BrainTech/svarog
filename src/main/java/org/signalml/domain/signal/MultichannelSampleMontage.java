/* MultichannelSampleMontage.java created 2007-09-24
 *
 */

package org.signalml.domain.signal;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageChannel;
import org.signalml.domain.montage.MontageMismatchException;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.exception.SanityCheckException;

/**
 * This class represents a source of samples for a {@link Montage montage}.
 * Using the given montage and source of samples combines (adds with coefficients) different
 * {@link SourceChannel source channels} to provide desired output.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MultichannelSampleMontage extends MultichannelSampleProcessor {

	protected static final Logger logger = Logger.getLogger(MultichannelSampleMontage.class);

	private static final String MATRIX_PROPERTY = "matrix";

        /**
         * an array of entries holding information about
         * {@link MontageChannel montage channels}. Indexes in the array are the
         * same as indexes of montage channels in the {@link Montage montage}.
         */
	private Entry[] montage;

        /**
         * a 2D array associating {@link SourceChannel source channels}
         * (their indexes) with {@link MontageChannel montage channels} for
         * which they are primary channels.
         * <code>reverseMontage[i]</code> - an array of indexes of montage
         * channels which have source channel of index <code>i</code> as primary
         * channel.
         */
	private int[][] reverseMontage;

        /**
         * an array of coefficients (references) between
         * {@link MontageChannel montage channels} and
         * {@link SourceChannel source channels}.
         * <code>matrixData[i][j]</code> the coefficient (reference) between
         * montage channel of index <code>i</code> and the source channel
         * of index <code>j</code>.
         */
	private float[][] matrixData;

        /**
         * an auxiliary array to hold samples of source channels
         */
	private double[] auxSamples;

        /**
         * the type of a signal in the montage
         */
	private SignalType signalType;

        /**
         * the montage used to set parameters of this source
         */
	private Montage currentMontage = null;

        /**
         * an array associating montage channels with the number of samples
         * in them
         */
	private int[] sampleCounts;

        /**
         * Constructor. Creates a source of montage samples for the signal of
         * given {@link SignalType type} based on a given {@link Montage montage}
         * (to set all attributes) and a given source of samples.
         * @param signalType the type of a signal
         * @param source the source of (source) samples
         * @param montage the montage used to set all attributes of this
         * sample source
         * @throws MontageMismatchException thrown when the number of channels
         * in <code>source</code> is different then the number of source
         * channels in the montage.
         */
	public MultichannelSampleMontage(SignalType signalType, MultichannelSampleSource source, Montage montage) throws MontageMismatchException {
		super(source);
		this.signalType = signalType;
		setCurrentMontage(montage);
	}

        /**
         * Constructor. Creates a source of montage samples for the signal of
         * given {@link SignalType type} based on a given source of samples.
         * As a montage empty montage for a given type of signal is used.
         * @param signalType the type of a signal
         * @param source the source of (source) samples
         * @throws SanityCheckException must not happen, means error in code.
         */
	public MultichannelSampleMontage(SignalType signalType, MultichannelSampleSource source) {
		super(source);
		this.signalType = signalType;
		this.currentMontage = getFailsafeMontage();
		try {
			applyMontage(this.currentMontage);
		} catch (MontageMismatchException ex) {
			throw new SanityCheckException("Applying fail safe montage must not fail");
		}
	}

        /**
         * Creates an empty montage for a given type of signal (attribute
         * <code>signalType</code>
         * @return an empty montage for a given type of signal.
         */
	private Montage getFailsafeMontage() {
		return signalType.getConfigurer().createMontage(source.getChannelCount());
	}

        /**
         * Returns the number of channels in this source.
         * @return the number of channels in this source.
         */
	@Override
	public int getChannelCount() {
		return montage.length;
	}

        /**
         * Fires this source that the montage has changed. Updates attributes
         * from the (changed) montage.
         * @param evt an event object describing the change
         */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		try {
			setCurrentMontage(currentMontage);
		} catch (MontageMismatchException ex) {
			logger.debug("Exception after property change", ex);
		}
		super.propertyChange(evt);
	}

	@Override
	public int getDocumentChannelIndex(int channel) {
		return source.getDocumentChannelIndex(montage[channel].primaryChannel);
	}

        /**
         * Returns indexes of {@link MontageChannel montage channels} in which
         * the given {@link SourceChannel source channel} is the primary channel.
         * @param channel the index of the source channel
         * @return an array of indexes of montage channels in which
         * the given source channel is the primary channel.
         */
	public int[] getMontageChannelIndices(int channel) {
		return reverseMontage[channel];
	}

        /**
         * Returns the label of a {@link MontageChannel montage channel} of
         * a given index.
         * If there is no label of a montage channel, label of primary
         * channel is used.
         * @param channel the index of the montage channel
         * @return the label of a montage channel of a given index.
         */
	@Override
	public String getLabel(int channel) {
		Entry me = montage[channel];
		if (me.label != null) {
			return me.label;
		}
		return source.getLabel(me.primaryChannel);
	}

        /**
         * For the given {@link MontageChannel montage channel} returns
         * the label of its primary channel.
         * @param channel the index of the montage channel
         * @return the label of the primary channel
         */
	public String getPrimaryLabel(int channel) {
		Entry me = montage[channel];
		if (me.primaryLabel != null) {
			return me.primaryLabel;
		}
		return source.getLabel(me.primaryChannel);
	}

        /**
         * Returns an array of labels of {@link MontageChannel montage channels}.
         * If there is no label of any montage channel, label of primary
         * channel is used.
         * @return an array of labels of montage channels
         */
	public String[] getLabels() {
		int cnt = getChannelCount();
		String[] labels = new String[cnt];
		for (int i=0; i<cnt; i++) {
			labels[i] = getLabel(i);
		}
		return labels;
	}

        /**
         * Returns an array of labels of primary channels (ordered by indexes
         * of {@link MontageChannel montage channels}).
         * @return an array of labels of primary channels
         */
	public String[] getPrimaryLabels() {
		int cnt = getChannelCount();
		String[] labels = new String[cnt];
		for (int i=0; i<cnt; i++) {
			labels[i] = getPrimaryLabel(i);
		}
		return labels;
	}

	@Override
	public int getSampleCount(int channel) {
		return sampleCounts[channel];
	}

        /**
         * Returns the given number of samples for a given
         * {@link MontageChannel montage channel} starting from a given position
         * in time.
         * @param channel the number of the montage channel
         * @param target the array to which results will be written starting
         * from position <code>arrayOffset</code>
         * @param signalOffset the position (in time) in the signal starting
         * from which samples will be returned
         * @param count the number of samples to be returned
         * @param arrayOffset the offset in <code>target</code> array starting
         * from which samples will be written
         */
	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {

		int channelCnt = source.getChannelCount();
		int primaryChannel = montage[channel].primaryChannel;
		int i, e;

		//In case we have 'fake' primaryChannel (eg. in montage there is a channel added by 'add empty channel'
		//we need to immitate source samples as 0
		if (source.getChannelCount() > primaryChannel)
			source.getSamples(primaryChannel, target, signalOffset, count, arrayOffset);
		else
			for (i=arrayOffset;i<arrayOffset+count;i++)
				target[i] = (double) 0.0;

		float coeff = matrixData[channel][primaryChannel];
		int idx;
		if (coeff != 1) {
			idx = arrayOffset;
			for (e=0; e<count; e++) {
				target[idx] *= coeff;
				idx++;
			}
		}

		for (i=0; i<channelCnt; i++) {
			if (i == primaryChannel) {
				continue;
			}
			coeff = matrixData[channel][i];
			if (coeff != 0) {
				if (auxSamples == null || auxSamples.length < count) {
					auxSamples = new double[count];
				}
				source.getSamples(i, auxSamples, signalOffset, count, 0);
				idx = arrayOffset;
				if (coeff != 1) {
					for (e=0; e<count; e++) {
						target[idx] += auxSamples[e] * coeff;
						idx++;
					}
				} else {
					for (e=0; e<count; e++) {
						target[idx] += auxSamples[e];
						idx++;
					}
				}
			}
		}

	}

        /**
         * Returns an array of entries holding information about
         * {@link MontageChannel montage channels}. Indexes in the array are the
         * same as indexes of montage channels in the {@link Montage montage}.
         * @return an array of entries with information about montage channels
         */
	public Entry[] getMontage() {
		return montage;
	}

        /**
         * Returns the montage associated with this sample source
         * @return the montage associated with this sample source
         */
	public Montage getCurrentMontage() {
		return currentMontage;
	}

        /**
         * Sets the {@link Montage montage} to be associated with this sample
         * source and uses it to set attributes of this sample source.
         * @param currentMontage the montage to be used
         * @throws MontageMismatchException thrown if the number of channels in
         * the <code>source</code> is different then the number of source
         * channels in the given montage.
         */
	public void setCurrentMontage(Montage currentMontage) throws MontageMismatchException {
		try {
			applyMontage(currentMontage);
		} catch (MontageMismatchException ex) {
			logger.error("Failed to apply montage", ex);
			this.currentMontage = getFailsafeMontage();
			applyMontage(this.currentMontage);
			throw ex;
		}
		this.currentMontage = currentMontage;
	}

        /**
         * Returns a 2D array associating {@link SourceChannel source channels}
         * (their indexes) with {@link MontageChannel montage channels} for
         * which they are primary channels
         * @return a 2D array associating source channels with montage channels
         * for which they are primary channels
         */
	public int[][] getReverseMontage() {
		return reverseMontage;
	}

        /**
         * Returns an array of coefficients (references) between
         * {@link MontageChannel montage channels} and
         * {@link SourceChannel source channels}.
         * <code>matrixData[i][j]</code> the coefficient (reference) between
         * montage channel of index <code>i</code> and the source channel
         * of index <code>j</code>.
         * @return an array of coefficients between montage channels and
         * source channels
         */
	public float[][] getMatrixData() {
		return matrixData;
	}

        /**
         * Sets the attributes of this source to the values taken from a given
         * montage
         * @param montage the montage to be used
         * @throws MontageMismatchException thrown if the number of channels in
         * the <code>source</code> is different then the number of source
         * channels in the given montage
         */
	private void applyMontage(Montage montage) throws MontageMismatchException {

		int srcCnt = montage.getSourceChannelCount();

		/*if (srcCnt != source.getChannelCount()) {
			throw new MontageMismatchException("error.badChannelCount");
			}*/

		int cnt = montage.getMontageChannelCount();

		int i;
		int e;
		int sampleCount;

		Entry[] entries = new Entry[cnt];
		float[][] matrix = new float[cnt][];
		int[] sampleCounts = new int[cnt];
		ArrayList<LinkedList<Integer>> backRefs = new ArrayList<LinkedList<Integer>>(srcCnt);
		for (i=0; i<srcCnt; i++) {
			backRefs.add(new LinkedList<Integer>());
		}
		LinkedList<Integer> channelBackRefs;
		int primaryChannel;

		for (i=0; i<cnt; i++) {
			primaryChannel = montage.getMontagePrimaryChannelAt(i);
			channelBackRefs = backRefs.get(primaryChannel);
			channelBackRefs.add(i);
			entries[i] = new Entry(primaryChannel, montage.getSourceChannelLabelAt(primaryChannel), montage.getMontageChannelLabelAt(i));
			matrix[i] = montage.getReferenceAsFloat(i);

			sampleCounts[i] =  source.getSampleCount(primaryChannel);

			// compensate for possible shorter reference
			for (e=0; e<matrix[i].length; e++) {
				if (e != primaryChannel && matrix[i][e] != 0) {
					sampleCount = source.getSampleCount(e);
					if (sampleCount < sampleCounts[i]) {
						sampleCounts[i] = sampleCount;
					}
				}
			}

		}

		int[][] backRefArr = new int[srcCnt][];
		int[] channelBackRefArr;
		Iterator<Integer> it;

		for (i=0; i<srcCnt; i++) {
			channelBackRefs = backRefs.get(i);
			channelBackRefArr = new int[channelBackRefs.size()];
			e = 0;
			it = channelBackRefs.iterator();
			while (it.hasNext()) {
				channelBackRefArr[e] = it.next();
				e++;
			}
			backRefArr[i] = channelBackRefArr;
		}

		int oldCount = (this.montage != null ? this.montage.length : 0);
		this.montage = entries;
		this.reverseMontage = backRefArr;

		pcSupport.firePropertyChange(CHANNEL_COUNT_PROPERTY, oldCount, entries.length);

		float[][] oldMatrixData = this.matrixData;
		this.matrixData = matrix;
		this.sampleCounts = sampleCounts;

		pcSupport.firePropertyChange(MATRIX_PROPERTY, oldMatrixData, matrixData);

	}

        /**
         * For a given {@link MontageChannel montage channel} holds:
         * 1. the number of {@link SourceChannel primary channel} associated with
         * this montage channel
         * 2. the label of a source channel
         * 3. the label of this montage channel
         */
	private class Entry {

                /**
                 * the number of a {@link SourceChannel primary channel}
                 * associated with this montage channel
                 */
		private int primaryChannel;

                /**
                 * the label of a {@link SourceChannel primary channel}
                 */
		private String primaryLabel;

                /**
                 * the label of this montage channel
                 */
		private String label;

                /**
                 * Constructor. Creates an entry with given informations.
                 * @param sourceChannel the number of a
                 * {@link SourceChannel primary channel} associated with this
                 * montage channel
                 * @param primaryLabel the label of a
                 * {@link SourceChannel primary channel}
                 * @param label the label of this montage channel
                 */
		public Entry(int sourceChannel, String primaryLabel, String label) {
			this.primaryChannel = sourceChannel;
			this.primaryLabel = primaryLabel;
			this.label = label;
		}

	}

}

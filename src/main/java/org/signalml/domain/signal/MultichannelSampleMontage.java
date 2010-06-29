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
import org.signalml.domain.montage.MontageMismatchException;
import org.signalml.exception.SanityCheckException;

/** MultichannelSampleMontage
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MultichannelSampleMontage extends MultichannelSampleProcessor {

	protected static final Logger logger = Logger.getLogger(MultichannelSampleMontage.class);

	private static final String MATRIX_PROPERTY = "matrix";

	private Entry[] montage;
	private int[][] reverseMontage;

	private float[][] matrixData;

	private double[] auxSamples;

	private SignalType signalType;
	private Montage currentMontage = null;

	private int[] sampleCounts;

	public MultichannelSampleMontage(SignalType signalType, MultichannelSampleSource source, Montage montage) throws MontageMismatchException {
		super(source);
		this.signalType = signalType;
		setCurrentMontage(montage);
	}

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

	private Montage getFailsafeMontage() {
		return signalType.getConfigurer().createMontage(source.getChannelCount());
	}

	@Override
	public int getChannelCount() {
		return montage.length;
	}

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

	public int[] getMontageChannelIndices(int channel) {
		return reverseMontage[channel];
	}

	@Override
	public String getLabel(int channel) {
		Entry me = montage[channel];
		if (me.label != null) {
			return me.label;
		}
		return source.getLabel(me.primaryChannel);
	}

	public String getPrimaryLabel(int channel) {
		Entry me = montage[channel];
		if (me.primaryLabel != null) {
			return me.primaryLabel;
		}
		return source.getLabel(me.primaryChannel);
	}

	public String[] getLabels() {
		int cnt = getChannelCount();
		String[] labels = new String[cnt];
		for (int i=0; i<cnt; i++) {
			labels[i] = getLabel(i);
		}
		return labels;
	}

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

	@Override
	public void getSamples(int channel, double[] target, int signalOffset, int count, int arrayOffset) {

		int channelCnt = source.getChannelCount();
		int primaryChannel = montage[channel].primaryChannel;
		int i, e;

		source.getSamples(primaryChannel, target, signalOffset, count, arrayOffset);
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

	public Entry[] getMontage() {
		return montage;
	}

	public Montage getCurrentMontage() {
		return currentMontage;
	}

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

	public int[][] getReverseMontage() {
		return reverseMontage;
	}

	public float[][] getMatrixData() {
		return matrixData;
	}

	private void applyMontage(Montage montage) throws MontageMismatchException {

		int srcCnt = montage.getSourceChannelCount();

		if (srcCnt != source.getChannelCount()) {
			throw new MontageMismatchException("error.badChannelCount");
		}

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

	private class Entry {

		private int primaryChannel;
		private String primaryLabel;
		private String label;

		public Entry(int sourceChannel, String primaryLabel, String label) {
			this.primaryChannel = sourceChannel;
			this.primaryLabel = primaryLabel;
			this.label = label;
		}

	}

}

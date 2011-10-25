/* Montage.java created 2007-10-23
 *
 */

package org.signalml.domain.montage;

import org.signalml.domain.montage.generators.IMontageGenerator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.signalml.app.config.preset.Preset;
import org.signalml.app.document.SignalDocument;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.domain.montage.filter.FFTSampleFilter;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;
import org.signalml.util.Util;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.domain.montage.system.EegSystem;

/**
 * This class represents the signal montage.
 * Montage is the representation of the EEG channels.
 * Every montage channel is a difference between the voltage of the electrode
 * and voltage of some reference (may be another electrode or average of electrodes).
 * 
 * This class contains a list of {@link MontageChannel montage channels} and
 * a list of {@link MontageSampleFilter filters}.
 * Filters can be excluded either for selected channels or for all of them.
 * This class has also assigned listeners informing about changes in a montage.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("montage")
public class Montage extends SourceMontage implements Preset {

	private static final long serialVersionUID = 1L;

	public static final String MONTAGE_GENERATOR_PROPERTY = "montageGenerator";
	public static final String NAME_PROPERTY = "montageGenerator";
	public static final String DESCRIPTION_PROPERTY = "description";
	public static final String MAJOR_CHANGE_PROPERTY = "majorChange";
	public static final String FILTERING_ENABLED_PROPERTY = "filteringEnabled";
	public static final String FILTFILT_ENABLED_PROPERTY = "filtfiltEnabled";

        /**
         * string representing the name of the montage
         */
	private String name;

        /**
         * string containing the description of the montage
         */
	private String description;

        /**
         * {@link MontageGenerator generator} for the current object
         */
	private IMontageGenerator montageGenerator;

        /**
         * {@link MontageChannel montaged channels} of a signal in the montage
         */
	private ArrayList<MontageChannel> montageChannels;

        /**
         * HashMap associating {@link SourceChannel source channels} with
         * the list of MontageChannels for which these channels are
         * primaryChannels
         */
	private transient HashMap<SourceChannel,LinkedList<MontageChannel>> montageChannelsByPrimary;

        /**
         * HashMap associating {@link MontageChannel montage channels}
         * with their labels
         */
	private transient HashMap<String,MontageChannel> montageChannelsByLabel;

        /**
         * the list of {@link MontageSampleFilter filters}
         */
	private ArrayList<MontageSampleFilter> filters = new ArrayList<MontageSampleFilter>();

        /**
         * tells whether the signal is being filtered
         */
	private boolean filteringEnabled = true;

	/**
	 * tells whether the signal should be filtered using filtfilt
	 */
	private boolean filtfiltEnabled = true;

        /**
         * tells whether montage is undergoing a major change
         */
	private transient boolean majorChange = false;

        /**
         * Constructor. Creates an empty Montage.
         */
	protected Montage() {
		super();
	}

        /**
         * Copy constructor.
         * @param montage the montage to be copied
         */
	public Montage(Montage montage) {
		this();
		copyFrom(montage);
	}

        /**
         * Copy constructor. Creates a Montage from the given
         * {@link SourceMontage SourceMontage (superclass)}
         * @param sourceMontage SourceMontage object to be copied as Montage
         */
	public Montage(SourceMontage sourceMontage) {
		this();
		super.copyFrom(sourceMontage);

		montageChannels = new ArrayList<MontageChannel>();
	}

        /**
         * Constructor. Creates Montage from the document with a signal.
         * @param document the document with a signal
         */
	public Montage(SignalDocument document) {
		super(document);

		montageChannels = new ArrayList<MontageChannel>();
	}

        /**
         * Creates a copy of this montage.
         * @return the created copy
         */
	@Override
	public Montage clone() {
		Montage montage = new Montage();
		montage.copyFrom(this);
		return montage;
	}

        /**
         * Copies parameters of the given Montage to this montage.
         * MontageChannels and filters are also copied.
         * Listeners are not copied.
         * @param montage the Montage which parameters are to be copied to
         * this montage
         */
	protected void copyFrom(Montage montage) {
		super.copyFrom(montage);

		montageChannels = new ArrayList<MontageChannel>(montage.montageChannels.size());
		HashMap<String, MontageChannel> map = getMontageChannelsByLabel();
		map.clear();
		getMontageChannelsByPrimary().clear();
		MontageChannel newChannel;
		LinkedList<MontageChannel>  list;
		for (MontageChannel channel : montage.montageChannels) {
			newChannel = new MontageChannel(channel, sourceChannels);
			list = getMontageChannelsByPrimaryList(newChannel.getPrimaryChannel());
			montageChannels.add(newChannel);
			map.put(newChannel.getLabel(), newChannel);
			list.add(newChannel);
		}

		if (montage.filters != null) {
			filters = new ArrayList<MontageSampleFilter>(montage.filters.size());
			MontageSampleFilter newFilter;
			for (MontageSampleFilter filter : montage.filters) {
				newFilter = new MontageSampleFilter(filter, montageChannels, montage.montageChannels);
				filters.add(newFilter);
			}
		} else {
			filters = new ArrayList<MontageSampleFilter>();
		}
		filteringEnabled = montage.filteringEnabled;
                filtfiltEnabled = montage.filtfiltEnabled;

		setName(montage.name);
		setDescription(montage.description);
		setMontageGenerator(montage.montageGenerator);

		fireMontageStructureChanged(this);

	}

        /**
         * Checks if this montage is compatible with the object given as parameter.
         * Montages are compatible if:
         * 1. they are compatible as {@link SourceMontage source montages}
         * 2. have the same number of montage channels
         * 3. for each source channel montage channels have the same references
         * @param montage the montage to be compared with a current object
         * @return true if montages are compatible, false otherwise
         */
	public boolean isCompatible(Montage montage) {

		boolean sourceCompatible = super.isCompatible(montage);
		if (!sourceCompatible) {
			return false;
		}

		int cnt = getMontageChannelCount();
		int mCnt = montage.getMontageChannelCount();

		if (cnt != mCnt) {
			// different montage channel count
			return false;
		}

		// now for each source channel check that the montage channels
		// in each montage have same references. Order, labels don't matter
		int srcCnt = getSourceChannelCount();
		int i;
		LinkedList<MontageChannel> ourChannels;
		LinkedList<MontageChannel> theirChannels;
		int ourSize;
		int theirSize;
		boolean found;
		MontageChannel our;
		Iterator<MontageChannel> theirIt;
		Iterator<MontageChannel> ourIt;

		for (i=0; i<srcCnt; i++) {

			ourChannels = getMontageChannelsByPrimaryList(sourceChannels.get(i));
			theirChannels = montage.getMontageChannelsByPrimaryList(montage.sourceChannels.get(i));

			ourSize = ourChannels.size();
			theirSize = theirChannels.size();

			if (ourSize != theirSize) {
				// different montage channel count
				return false;
			}
			if (ourSize == 0) {
				continue;
			}

			ourIt = ourChannels.iterator();
			while (ourIt.hasNext()) {
				our = ourIt.next();
				theirIt = theirChannels.iterator();
				found = false;
				while (theirIt.hasNext()) {
					if (our.isEqualReference(theirIt.next(), montage.sourceChannels)) {
						found = true;
						break;
					}
				}
				if (!found) {
					return false;
				}
			}

		}

		return true;

	}

        /**
         * Resets all parameters of the current object.
         */
	public void reset() {

		getMontageChannelsByLabel().clear();
		getMontageChannelsByPrimary().clear();
		montageChannels.clear();

		for (MontageSampleFilter filter : filters) {
			filter.clearExclusion();
		}

		if (!majorChange) {
			fireMontageStructureChanged(this);
			fireMontageSampleFiltersChanged(this);
		}

		setMontageGenerator(null);
		setChanged(true);

	}

        /**
         * Returns the name of this montage.
         * @return the name of this montage
         */
        @Override
	public String getName() {
		return name;
	}

        /**
         * Sets the name of this montage.
         * @param name String with the name to be set
         */
        @Override
	public void setName(String name) {
		if (!Util.equalsWithNulls(this.name, name)) {
			String oldName = this.name;
			this.name = name;
			pcSupport.firePropertyChange(NAME_PROPERTY, oldName, name);
			setChanged(true);
		}
	}

        /**
         * Returns the description of this montage.
         * @return the description of this montage
         */
	public String getDescription() {
		return description;
	}

        /**
         * Sets the description of this montage.
         * @param description String with the description to be set
         */
	public void setDescription(String description) {
		if (!Util.equalsWithNulls(this.description, description)) {
			String oldDescription = this.description;
			this.description = description;
			pcSupport.firePropertyChange(DESCRIPTION_PROPERTY, oldDescription, description);
			setChanged(true);
		}
	}

        /**
         * Returns the generator for the this montage.
         * @return the generator for the this montage
         */
	public IMontageGenerator getMontageGenerator() {
		return montageGenerator;
	}

        /**
         * Sets the new {@link MontageGenerator generator} for this montage.
         * @param montageGenerator generator to be set
         */
	public void setMontageGenerator(IMontageGenerator montageGenerator) {
		if (!Util.equalsWithNulls(this.montageGenerator, montageGenerator)) {
			IMontageGenerator oldGenerator = this.montageGenerator;
			this.montageGenerator = montageGenerator;
			pcSupport.firePropertyChange(MONTAGE_GENERATOR_PROPERTY, oldGenerator, montageGenerator);
			setChanged(true);
		}
	}

        /**
         * Returns whether this montage is undergoing a major change.
         * @return true this montage is undergoing a major change,
         * false otherwise
         */
	public boolean isMajorChange() {
		return majorChange;
	}

        /**
         * Sets the majorChange property to a given value
         * @param majorChange the value to be set
         */
	public void setMajorChange(boolean majorChange) {
		if (this.majorChange != majorChange) {
			this.majorChange = majorChange;
			pcSupport.firePropertyChange(MAJOR_CHANGE_PROPERTY, !majorChange, majorChange);
			if (!majorChange) {
				// turned off
				fireMontageStructureChanged(this);
				fireMontageSampleFiltersChanged(this);
				setChanged(true);
			}
		}
	}


        /**
         * Tells whether the signal is being filtered.
         * @return true if signal is being filtered, false otherwise
         */
	public boolean isFilteringEnabled() {
		return filteringEnabled;
	}

        /**
         * Sets the filteringEnabled property to a given value.
         * @param filteringEnabled the value to be set
         */
	public void setFilteringEnabled(boolean filteringEnabled) {
		if (this.filteringEnabled != filteringEnabled) {
			this.filteringEnabled = filteringEnabled;
			pcSupport.firePropertyChange(FILTERING_ENABLED_PROPERTY, !filteringEnabled, filteringEnabled);
		}
	}

	/**
	 * Tells whether the signal should be filtered using filtfilt.
	 * @return true if the signal should be filtered using filtfilt, false
	 * otherwise
	 */
	public boolean isFiltfiltEnabled() {
		return filtfiltEnabled;
	}

	/**
	 * Sets the filtfiltEnabled property to a given value.
	 * @param filtfiltEnabled the value to be set
	 */
	public void setFiltfiltEnabled(boolean filtfiltEnabled) {
		if (this.filtfiltEnabled != filtfiltEnabled) {
			this.filtfiltEnabled = filtfiltEnabled;
			pcSupport.firePropertyChange(FILTFILT_ENABLED_PROPERTY, !filtfiltEnabled, filtfiltEnabled);
		}
	}

        /**
         * Returns the name of this montage.
         * @return String with  the name of this montage.
         */
	@Override
	public String toString() {
		return name;
	}


        /**
         * Returns HashMap associating {@link SourceChannel source channels} with
         * the list of {@link MontageChannel montage channels} for which these
         * channels are primaryChannels.
         * @return HashMap associating source channels with
         * the list of  montage channels for which these channels are
         * primaryChannels
         */
	protected HashMap<SourceChannel, LinkedList<MontageChannel>> getMontageChannelsByPrimary() {
		if (montageChannelsByPrimary == null) {
			montageChannelsByPrimary = new HashMap<SourceChannel, LinkedList<MontageChannel>>();
		}
		return montageChannelsByPrimary;
	}

        /**
         * Returns the list of {@link MontageChannel montage channels} for which
         * the given {@link SourceChannel source channel} is a primary channel.
         * @param channel the source channel object for which list is returned
         * @return the list of montage channels for which given source channel
         * is a primary channel
         */
        protected LinkedList<MontageChannel> getMontageChannelsByPrimaryList(SourceChannel channel) {
		HashMap<SourceChannel, LinkedList<MontageChannel>> map = getMontageChannelsByPrimary();
		LinkedList<MontageChannel> list = map.get(channel);
		if (list == null) {
			list = new LinkedList<MontageChannel>();
			map.put(channel, list);
			for (MontageChannel montageChannel : montageChannels) {
				if (montageChannel.getPrimaryChannel() == channel) {
					list.add(montageChannel);
				}
			}
		}
		return list;
	}

        /**
         * Returns HashMap associating {@link MontageChannel montage channels}
         * with their labels.
         * @return HashMap associating montage channels with their labels
         */
	protected HashMap<String,MontageChannel> getMontageChannelsByLabel() {
		if (montageChannelsByLabel == null) {
			montageChannelsByLabel = new HashMap<String, MontageChannel>();
			for (MontageChannel channel : montageChannels) {
				montageChannelsByLabel.put(channel.getLabel(), channel);
			}
		}
		return montageChannelsByLabel;
	}

        /**
         * Finds a {@link MontageChannel montage channel} with a given label.
         * @param label String with a label to be found
         * @return the found montage channel
         */
	protected MontageChannel getMontageChannelByLabel(String label) {
		return getMontageChannelsByLabel().get(label);
	}

        /**
         * Returns the number of {@link MontageChannel montage channels}
         * in the montage.
         * @return the number of montage channels in the montage
         */
	public int getMontageChannelCount() {
		return montageChannels.size();
	}

        /**
         * Returns the index of primary channel for selected
         * {@link MontageChannel montage channel}.
         * @param index the index of montage channel
         * @return found index of primary channel
         */
	public int getMontagePrimaryChannelAt(int index) {
		return montageChannels.get(index).getPrimaryChannel().getChannel();
	}

        /**
         * Returns the label of a {@link MontageChannel montage channel}
         * at a given index.
         * @param index the index of a montage channel
         * @return the label of a montage channel
         */
	public String getMontageChannelLabelAt(int index) {
		return montageChannels.get(index).getLabel();
	}

        /**
         * Sets a new label for a {@link MontageChannel montage channel}
         * at a given index.
         * @param index the index of a montage channel
         * @param label a new label to be set
         * @return the old label
         * @throws MontageException if the label empty,
         * with illegal characters or not unique
         */
	public String setMontageChannelLabelAt(int index, String label) throws MontageException {

		if (label == null || label.isEmpty()) {
			throw new MontageException("error.montageChannelLabelEmpty");
		}
		if (Util.hasSpecialChars(label)) {
			throw new MontageException("error.montageChannelLabelBadChars");
		}

		MontageChannel channel = montageChannels.get(index);
		String oldLabel = channel.getLabel();
		HashMap<String, MontageChannel> map = getMontageChannelsByLabel();
		if (!oldLabel.equals(label)) {
			MontageChannel namedChannel = map.get(label);
			if (namedChannel != null && namedChannel != channel) {
				throw new MontageException("error.montageChannelLabelDuplicate");
			}
			map.remove(oldLabel);
			channel.setLabel(label);
			map.put(label, channel);
			if (!majorChange) {
				fireMontageChannelsChanged(this, new int[] { index }, new int[] { channel.getPrimaryChannel().getChannel() });
				setChanged(true);
			}
			setMontageGenerator(null);
		}

		return oldLabel;

	}

        /**
         * Checks whether the {@link SourceChannel source channel} of a given
         * index is in use, i.e. either is a primaryChannel for some
         * {@link MontageChannel montage channel}, or there is a reference
         * to it in a montage channel.
         * @param index the index of source channel
         * @return true if the source channel is in use, false otherwise
         */
	public boolean isSourceChannelInUse(int index) {

		SourceChannel channel = sourceChannels.get(index);

		if (!getMontageChannelsByPrimaryList(channel).isEmpty()) {
			return true;
		}

		for (MontageChannel montageChannel : montageChannels) {
			if (montageChannel.hasReference(channel)) {
				return true;
			}
		}

		return false;

	}

        /**
         * Checks if a given {@link MontageChannel montage channel} has
         * a reference to a given {@link SourceChannel source channel}.
         * @param montageIndex an index of a montage channel
         * @param sourceIndex an index of a source channel
         * @return true if there is a reference, false otherwise
         */
	public boolean hasReference(int montageIndex, int sourceIndex) {
		return montageChannels.get(montageIndex).hasReference(sourceChannels.get(sourceIndex));
	}

        /**
         * Checks if a given {@link MontageChannel montage channel} has
         * a reference to any {@link SourceChannel source channel}.
         * @param montageIndex an index of montage channel
         * @return true if there is a reference, false otherwise
         */
	public boolean hasReference(int montageIndex) {
		return montageChannels.get(montageIndex).hasReference();
	}

        /**
         * Returns a list of {@link MontageChannel montage channels} for which
         * a {@link SourceChannel source channel} of a given index
         * is a primary channel.
         * @param index an index of a source channel
         * @return a list of montage channels for which source channel of
         * a given index is a primary channel
         */
	public int[] getMontageChannelsForSourceChannel(int index) {

		SourceChannel channel = sourceChannels.get(index);

		LinkedList<MontageChannel> list = getMontageChannelsByPrimaryList(channel);
		int[] result = new int[list.size()];
		int cnt = 0;

		for (MontageChannel montageChannel : list) {
			result[cnt] = montageChannels.indexOf(montageChannel);
			cnt++;
		}

		return result;

	}

        /**
         * Creates a unique label for a {@link MontageChannel montage channel}
         * based on a given String
         * @param stem String object on which a new label will be based
         * @return created unique label
         */
	public String getNewMontageChannelLabel(String stem) {

		int cnt = 2;

		String candidate = stem;
		HashMap<String, MontageChannel> map = getMontageChannelsByLabel();
		while (map.containsKey(candidate)) {
			candidate = stem + " (" + cnt + ")";
			cnt++;
		}

		return candidate;

	}

        /**
         * For a {@link MontageChannel montage channel} of a given index,
         * returns an array of references in the form of Strings
         * @param index an index of the montage channel
         * @return an array of references for a montage channel of a given index
         */
	public String[] getReference(int index) {
		String[] references = new String[sourceChannels.size()];
		montageChannels.get(index).getReferences(references);
		return references;
	}

        /**
         * For a {@link MontageChannel montage channel} of a given index,
         * returns a string representing its references
         * @param index an index of the montage channel
         * @return a string representing channel`s references
         */

	public String getReferenceReadable(int index) {
		String[] references = new String[sourceChannels.size()];
		montageChannels.get(index).getReferences(references);
		String result = ""; // start with the first element
		String ONE = "1", MINUS = "-";
		for (int i=0; i<references.length; i++) {
			if ((references[i] == null) || (this.getSourceChannelFunctionAt(i).getType() == ChannelType.ZERO))
				// null means that no reference for given sourceChannel is present
				// empty is 0 - also ignore
				continue;
			else {
				// combine current reference with other - insert '*' chars etc
				String pre = "";
				if ((references[i].startsWith(MINUS)) || (result.length() == 0))
					pre = "";
				else
					pre = "+";
				if (!references[i].equals(ONE))
					pre = pre + references[i] + "*";
				
				if (this.getSourceChannelFunctionAt(i).getType() == ChannelType.ONE)
					result = result + pre + "1";
				else
					result = result + pre + sourceChannels.get(i).getLabel();
			}
		}
		return result;
	}


        /**
         * For a {@link MontageChannel montage channel} of a given index,
         * returns an array of references in the form of floats
         * (converted from Strings).
         * @param index an index of MontageChannel object
         * @return an array of references for a MontageChannel of a given index
         */
	public float[] getReferenceAsFloat(int index) {
		float[] references = new float[sourceChannels.size()];
		montageChannels.get(index).getReferencesAsFloat(references);
		return references;
	}

        /**
         * Sets a new array of references for a
         * {@link MontageChannel montage channel} of a given index.
         * @param index an index of a montage channel
         * @param references a list of references in the form of Strings
         * @throws NumberFormatException if the references array is to long
         * (larger then the number of source channels)
         */
	public void setReference(int index, String[] references) throws NumberFormatException {
		if (references.length > sourceChannels.size()) {
			throw new IndexOutOfBoundsException("References too long [" + references.length + "]");
		}
		MontageChannel channel = montageChannels.get(index);
		channel.setReferences(references, sourceChannels);
		if (!majorChange) {
			fireMontageReferenceChanged(this, new int[] { index }, new int[] { channel.getPrimaryChannel().getChannel() });
			setChanged(true);
		}
		setMontageGenerator(null);
	}

        /**
         * Returns the reference between a given
         * {@link MontageChannel montage channel} and a given
         * {@link SourceChannel source channel}.
         * @param montageIndex an index of montage channel
         * @param sourceIndex an index of source channel
         * @return reference between a given montage channel and
         * a given source channel
         */
	public String getReference(int montageIndex, int sourceIndex) {
		return montageChannels.get(montageIndex).getReference(sourceChannels.get(sourceIndex));
	}

        /**
         * Checks if the reference between a given
         * {@link MontageChannel montage channel} and
         * a given {@link SourceChannel source channel} is symmetric.
         * @param montageIndex an index of montage channel
         * @param sourceIndex an index of source channel
         * @return true if the reference is symmetric, false otherwise
         */
	public boolean isReferenceSymmetric(int montageIndex, int sourceIndex) {
		return montageChannels.get(montageIndex).isSymmetricWeight(sourceChannels.get(sourceIndex));
	}

        /**
         * Sets the reference between a given
         * {@link MontageChannel montage channel} and
         * a given {@link SourceChannel source channel} to a given value.
         * @param montageIndex an index of a montage channel
         * @param sourceIndex an index of a source channel
         * @param value the value of reference to be set
         * @throws NumberFormatException thrown when the references array is
         * to long (larger then the number of source channels)
         */
	public void setReference(int montageIndex, int sourceIndex, String value) throws NumberFormatException {
		MontageChannel channel = montageChannels.get(montageIndex);
		channel.setReference(sourceChannels.get(sourceIndex), value);
		if (!majorChange) {
			fireMontageReferenceChanged(this, new int[] { montageIndex }, new int[] { channel.getPrimaryChannel().getChannel() });
			setChanged(true);
		}
		setMontageGenerator(null);
	}

        /**
         * Removes the reference between a given
         * {@link MontageChannel montage channel} and
         * a given {@link SourceChannel source channel}.
         * @param montageIndex an index of a montage channel
         * @param sourceIndex an index of a source channel
         */
	public void removeReference(int montageIndex, int sourceIndex) {
		MontageChannel channel = montageChannels.get(montageIndex);
		channel.removeReference(sourceChannels.get(sourceIndex));
		if (!majorChange) {
			fireMontageReferenceChanged(this, new int[] { montageIndex }, new int[] { channel.getPrimaryChannel().getChannel() });
			setChanged(true);
		}
		setMontageGenerator(null);
	}

        /**
         * Adds a new {@link SourceChannel source channel} with a given label
         * and function to the list of source channels.
         * @param label a unique label for new source channel
         * @param function a unique function for new source channel
         * @throws MontageException if a label or function not unique
         */
	@Override
	public void addSourceChannel(String label, IChannelFunction function) throws MontageException {
		super.addSourceChannel(label, function);
		setMontageGenerator(null);
	}

        /**
         * Removes the last {@link SourceChannel source channel} on the the list
         * of source channels, montage channels with it as a primary and
         * all references to it.
         * @return removed source channel
         */
	@Override
	public SourceChannel removeSourceChannel() {

		if (sourceChannels.isEmpty()) {
			return null;
		}
		SourceChannel channel = sourceChannels.get(sourceChannels.size() - 1) ;

		LinkedList<MontageChannel> list = getMontageChannelsByPrimaryList(channel);
		MontageChannel montageChannel;

		if (!list.isEmpty()) {
			int[] indices = new int[list.size()];
			int[] primaryIndices = new int[indices.length];
			int cnt = 0;
			Iterator<MontageChannel> it = list.iterator();
			while (it.hasNext()) {
				montageChannel = it.next();
				indices[cnt] = montageChannels.indexOf(montageChannel);
				primaryIndices[cnt] = montageChannel.getPrimaryChannel().getChannel();
				cnt++;
			}
			it = list.iterator();
			HashMap<String, MontageChannel> map = getMontageChannelsByLabel();

			LinkedList<Integer> filterIndexList = new LinkedList<Integer>();
			int filterCnt = filters.size();
			int i;

			while (it.hasNext()) {
				montageChannel = it.next();
				map.remove(montageChannel.getLabel());
				montageChannels.remove(montageChannel);

				for (i=0; i<filterCnt; i++) {
					if (filters.get(i).removeExcludedChannel(montageChannel)) {
						if (!filterIndexList.contains(i)) {
							filterIndexList.add(i);
						}
					}
				}
			}

			list.clear();

			if (!majorChange) {
				Collections.sort(filterIndexList);

				fireMontageChannelsRemoved(this, indices, primaryIndices);
				fireMontageSampleFilterExclusionChanged(this, filterIndexList);
			}
		}

		int size = montageChannels.size();
		for (int i=0; i<size; i++) {
			montageChannel = montageChannels.get(i);
			LinkedList<Integer> indexList = new LinkedList<Integer>();
			LinkedList<Integer> primaryIndexList = new LinkedList<Integer>();
			if (montageChannel.hasReference(channel)) {
				montageChannel.removeReference(channel);
				indexList.add(i);
				primaryIndexList.add(montageChannel.getPrimaryChannel().getChannel());
			}
			if (!indexList.isEmpty()) {
				if (!majorChange) {
					fireMontageReferenceChanged(this, indexList, primaryIndexList);
				}
			}
		}

		super.removeSourceChannel();
		setMontageGenerator(null);

		return channel;

	}

        /**
         * Adds a given {@link MontageChannel montage channel} to necessary
         * collections (montageChannels, montageChannelsByLabel,
         * montageChannelsByPrimary).
         * To montageChannels it is added at a given index.
         * @param channel a montage channel to be added
         * @param atIndex an index at which channel will be added to
         * montageChannels list. If atIndex<0 then will be added
         * at the end of the list
         * @return an index at which channel was added to a montageChannels list
         */
	protected int addMontageChannelInternal(MontageChannel channel, int atIndex) {
		getMontageChannelsByLabel().put(channel.getLabel(), channel);
		getMontageChannelsByPrimaryList(channel.getPrimaryChannel()).add(channel);
		if (atIndex < 0) {
			montageChannels.add(channel);
			return montageChannels.size() - 1;
		} else {
			montageChannels.add(atIndex, channel);
			return atIndex;
		}
	}

        /**
         * Creates a montage channel using a {@link SourceChannel source channel}
         * of a given index as primaryChannel and puts it at selected index on
         * a montageChannels list.
         * @param sourceIndex index of a SourceChannel
         * @param atIndex index at which channel will be added to the
         * montageChannels list. If atIndex<0 then will be added at the end of
         * the list.
         * @return index at which channel was added to montageChannels list.
         */
	public int addMontageChannel(int sourceIndex, int atIndex) {
		int[] sourceIndices = new int[] { sourceIndex };
		int[] indices = addMontageChannels(sourceIndices, atIndex);
		return indices[0];
	}

        /**
         * Creates a montage channel using a {@link SourceChannel source channel}
         * of a given index as primaryChannel and puts it at the end
         * montageChannels list.
         * @param sourceIndex index of a SourceChannel
         * @return index at which channel was added to montageChannels list.
         */
	public int addMontageChannel(int sourceIndex) {
		return addMontageChannel(sourceIndex, -1);
	}

        /**
         * For each index on a sourceIndices list creates a
         * {@link MontageChannel montage channel} with a
         * {@link SourceChannel source channel} of this index as primaryChannel.
         * Puts created montage channel at the end of montageChannels list.
         * @param sourceIndices a list of indexes of SourceChannels
         * @return a list of indexes of created montage channels on
         * a montageChannels list
         */
	public int[] addMontageChannels(int[] sourceIndices) {
		return addMontageChannels(sourceIndices, -1);
	}

        /**
         * Creates {@link MontageChannel montage channels} from
         * {@link SourceChannel source channels} of <i>count</i> consecutive
         * indexes starting from <i>fromSourceIndex</i>
         * and puts them on a <i>montageChannels</i> list starting from
         * <i>atIndex</i>.
         * @param fromSourceIndex an index of a source channel from which adding
         * new montage channels will be started
         * @param count a number of montage channels to be added
         * @param atIndex an index starting from which created montage channels
         * are to be put. If atIndex<0 then they will be added at the end of
         * the list
         * @return a list of indexes of created montage channels on a montageChannels list
         */
	public int[] addMontageChannels(int fromSourceIndex, int count, int atIndex) {
		int[] sourceIndices = new int[count];
		for (int i=0; i<count; i++) {
			sourceIndices[i] = fromSourceIndex + i;
		}
		return addMontageChannels(sourceIndices, atIndex);
	}

        /**
         * Creates {@link MontageChannel montage channels} from
         * {@link SourceChannel source channel} of <i>count</i> consecutive
         * indexes starting from <i>fromSourceIndex</i>
         * and puts them at the end of <i>montageChannels</i> list.
         * @param fromSourceIndex an index of a source channel from which adding
         * new montage channels will be started
         * @param count a number of montage channel to be added
         * @return a list of indexes of created montage channels on a
         * montageChannels list
         */
	public int[] addMontageChannels(int fromSourceIndex, int count) {
		return addMontageChannels(fromSourceIndex, count, -1);
	}


        /**
         * For each index on a sourceIndices list creates
         * {@link MontageChannel montage channels} with a
         * {@link SourceChannel source channel} of this index as primaryChannel.
         * Puts created montage channels on a montageChannels list starting from
         * a given index.
         * @param sourceIndices a list of indexes of source channels
         * @param atIndex an index starting from which created montage channels
         * are to be put. If atIndex<0 then will be added at the end of a list.
         * @return a list of indexes of created montage channels on
         * a montageChannels list
         */
        public int[] addMontageChannels(int[] sourceIndices, int atIndex) {
		int[] indices = new int[sourceIndices.length];
		if (sourceIndices.length == 0) {
			return indices;
		}
		SourceChannel channel;
		MontageChannel montageChannel;
		for (int i=0; i<sourceIndices.length; i++) {
			channel = sourceChannels.get(sourceIndices[i]);
			montageChannel = new MontageChannel(channel);
			montageChannel.setLabel(getNewMontageChannelLabel(channel.getLabel()));
			indices[i] = addMontageChannelInternal(montageChannel, (atIndex < 0 ? atIndex : atIndex+i));
		}
		if (!majorChange) {
			fireMontageChannelsAdded(this, indices, sourceIndices);
			setChanged(true);
		}
		setMontageGenerator(null);
		return indices;
	}

        /**
         * Adds a bipolar {@link MontageChannel montage channel} to a montage
         * with a selected reference channel
         * @param sourceIndex an index of a {@link SourceChannel source channel}
         * @param atIndex an index at which channel will be added to a
         * montageChannels list. If atIndex<0 then will be added at the
         * end of a list.
         * @param label a label of a new montage channel
         * @param referenceChannel an index of a source channel to which
         * montage channel should have a reference
         * @return an index at which the channel was added to
         * a montageChannels list.
         */
	public int addBipolarMontageChannel(int sourceIndex, int atIndex, String label, int referenceChannel) {

		SourceChannel channel = sourceChannels.get(sourceIndex);
		MontageChannel montageChannel = new MontageChannel(channel);
		montageChannel.setLabel(getNewMontageChannelLabel(label));
		montageChannel.setReference(sourceChannels.get(referenceChannel), "-1");
		int index = addMontageChannelInternal(montageChannel, atIndex);

		if (!majorChange) {
			int[] indices = new int[] { index };
			int[] sourceIndices = new int[] { sourceIndex };
			fireMontageChannelsAdded(this, indices, sourceIndices);
			setChanged(true);
		}
		setMontageGenerator(null);
		return index;

	}

        /**
         * Adds a bipolar {@link MontageChannel montage channel} to a montage
         * with a selected reference channel. Puts it at the end of
         * the montageChannels list.
         * @param sourceIndex an index of a {@link SourceChannel source channel}
         * @param label a label of a new montage channel
         * @param referenceChannel an index of a source channel to which
         * montage channel should have a reference
         * @return an index at which the channel was added to
         * the montageChannels list.
         */
	public int addBipolarMontageChannel(int sourceIndex, String label, int referenceChannel) {
		return addBipolarMontageChannel(sourceIndex, -1, label, referenceChannel);
	}

        /**
         * Removes a {@link MontageChannel montage channel} of specified index.
         * @param index an index of a channel to be removed
         * @return the removed montage channel
         */
	public MontageChannel removeMontageChannel(int index) {
		int[] indices = new int[] { index };
		MontageChannel[] channels = removeMontageChannels(indices);
		return channels[0];
	}

        /**
         * Removes consecutive {@link MontageChannel montage channels} starting
         * from a given index.
         * @param fromIndex an index to start from
         * @param count a number of channels to be removed
         * @return an array of removed montage channels
         */
	public MontageChannel[] removeMontageChannels(int fromIndex, int count) {
		int[] indices = new int[count];
		for (int i=0; i<count; i++) {
			indices[i] = fromIndex + i;
		}
		return removeMontageChannels(indices);
	}

	/**
         * Removes {@link MontageChannel montage channels} of specified indexes.
         * @param indices a list of indexes of channels to be removed
         * @return an array of removed montage channels
         */
        public MontageChannel[] removeMontageChannels(int[] indices) {

		MontageChannel[] channels = new MontageChannel[indices.length];
		if (indices.length == 0) {
			return channels;
		}

		int[] sourceIndices = new int[indices.length];

		for (int i=0; i<indices.length; i++) {
			channels[i] = montageChannels.get(indices[i]);
			sourceIndices[i] = channels[i].getPrimaryChannel().getChannel();
			getMontageChannelsByLabel().remove(channels[i].getLabel());
			getMontageChannelsByPrimaryList(channels[i].getPrimaryChannel()).remove(channels[i]);
		}

		LinkedList<Integer> filterIndexList = new LinkedList<Integer>();

		for (int i=0; i<indices.length; i++) {
			montageChannels.remove(channels[i]);
		}

		Iterator<MontageSampleFilter> it = filters.iterator();
		MontageSampleFilter filter;
		while (it.hasNext()) {
			filter = it.next();
			for (int i=0; i<indices.length; i++) {
				if (filter.removeExcludedChannel(channels[i])) {
					if (!filterIndexList.contains(i)) {
						filterIndexList.add(i);
					}
				}
			}
		}

		if (!majorChange) {

			Collections.sort(filterIndexList);

			fireMontageChannelsRemoved(this, indices, sourceIndices);
			fireMontageSampleFilterExclusionChanged(this, filterIndexList);
			setChanged(true);
		}

		setMontageGenerator(null);

		return channels;

	}

        /**
         * Moves count consecutive {@link MontageChannel montage channels}
         * on a montageChannels list starting from given index.
         * @param fromIndex an index from which selecting montage channels
         * to be moved starts
         * @param count a number of consecutive MontageChannels to be moved
         * @param delta a number of positions MontageChannels are to be moved.
         * If &gt 0 channels are moved forward, if &lt 0 are moved backward.
         * @return a number of positions MontageChannels were moved
         */
	public int moveMontageChannelRange(int fromIndex, int count, int delta) {

		if (delta == 0 || count == 0) {
			return 0;
		}

		int possibleDelta;
		int size = montageChannels.size();

		if (delta > 0) {
			possibleDelta = Math.min(delta, size - (fromIndex+count));
		} else {
			possibleDelta = Math.max(delta, -fromIndex);
		}

		if (possibleDelta == 0) {
			return 0;
		}

		LinkedList<Integer> indexList = new LinkedList<Integer>();
		LinkedList<Integer> primaryIndexList = new LinkedList<Integer>();

		MontageChannel channel;

		int i;
		MontageChannel[] cache = new MontageChannel[count];

		for (i=0; i<count; i++) {
			cache[i] = montageChannels.get(fromIndex + i);
		}

		if (possibleDelta > 0) {
			// rows moved down

			for (i=0; i<possibleDelta; i++) {
				channel = montageChannels.get(fromIndex + count + i);
				montageChannels.set(fromIndex + i, channel);
				indexList.add(fromIndex + i);
				primaryIndexList.add(channel.getPrimaryChannel().getChannel());
			}

		} else if (possibleDelta < 0) {
			// rows moved up

			for (i=-1; i>=possibleDelta; i--) {
				channel = montageChannels.get(fromIndex + i);
				montageChannels.set(fromIndex + count + i, channel);
				indexList.add(fromIndex + count + i);
				primaryIndexList.add(channel.getPrimaryChannel().getChannel());
			}

		}

		for (i=0; i<count; i++) {
			montageChannels.set(fromIndex + possibleDelta + i, cache[i]);
			indexList.add(fromIndex + possibleDelta + i);
			primaryIndexList.add(cache[i].getPrimaryChannel().getChannel());

		}

		if (!majorChange) {
			fireMontageStructureChanged(this);
			setChanged(true);
		}
		setMontageGenerator(null);

		return possibleDelta;

	}

        /**
         * Returns if the montage is bipolar (has only bipolar references).
         * @return true if the montage is bipolar, false otherwise
         */
	public boolean isBipolar() {
		for (MontageChannel channel : montageChannels) {
			if (!channel.isBipolarReference()) {
				return false;
			}
		}
		return true;
	}

        /**
         * Returns whether the montage is filtered.
         * @return true if the montage is filtered, false otherwise
         */
	public boolean isFiltered() {
		if (!filteringEnabled) {
			return false;
		}
		if (filters.isEmpty()) {
			return false;
		}
		return true;
	}

        /**
         * Checks whether a given montage channel is excluded from all filters.
         * @param channel an index of montage channel to be checked
         * @return true if a montage channel is excluded from all filters, false otherwise
         */
	public boolean isExcludeAllFilters(int channel) {
		return montageChannels.get(channel).isExcludeAllFilters();
	}

        /**
         * Sets if a given {@link MontageChannel montage channel} should
         * exclude all filters.
         * @param channel an index of a montage channel for which
         * a new value is to be set
         * @param exclude true if all channels should be excluded,
         * false otherwise
         */
	public void setExcludeAllFilters(int channel, boolean exclude) {
		MontageChannel montageChannel = montageChannels.get(channel);
		boolean oldValue = montageChannel.isExcludeAllFilters();
		if (oldValue != exclude) {
			montageChannel.setExcludeAllFilters(exclude);
			if (!majorChange) {
				fireMontageSampleFiltersChanged(this);
				setChanged(true);
			}
		}
	}

        /**
         * Returns the number of filters for a montage.
         * @return the number of filters for a montage
         */
	public int getSampleFilterCount() {
		return filters.size();
	}

        /**
         * Returns the definition of a filter of a given index.
         * @param index an index of a filter
         * @return the definition of a filter
         */
	public SampleFilterDefinition getSampleFilterAt(int index) {
		return filters.get(index).getDefinition();
	}

	/**
	 * Adds a new filter to a montage.
	 * (Note: {@link TimeDomainSampleFilter TimeDomainSampleFilters} are
	 * added before {@link FFTSampleFilter FFTSampleFilters}).
	 * @param definition a definition of a filter to be added
	 * @return an index of added filter
	 */
	public int addSampleFilter(SampleFilterDefinition definition) {

		MontageSampleFilter filter = new MontageSampleFilter(definition);

		//time domain filters are added and thus processed before FFTSampleFilters
		if (definition instanceof TimeDomainSampleFilter) {
			int i = 0;
			for (i = 0; i < filters.size(); i++) {
				if (filters.get(i).getDefinition() instanceof FFTSampleFilter)
					break;
			}
			filters.add(i, filter);
		}
		else
			filters.add(filter);

		int index = filters.indexOf(filter);

		if (!majorChange) {
			fireMontageSampleFilterAdded(this, new int[] {index});
			setChanged(true);
		}

		return index;

	}

        /**
         * Changes the definition of a given filter.
         * @param index an index of a filter
         * @param definition new definition to be set
         */
	public void updateSampleFilter(int index, SampleFilterDefinition definition) {

		MontageSampleFilter montageSampleFilter = filters.get(index);
		montageSampleFilter.setDefinition(definition);

		if (!majorChange) {
			fireMontageSampleFilterChanged(this, new int[] { index });
			setChanged(true);
		}

	}

        /**
         * Removes a filter from a montage.
         * @param index an index of filter to be removed
         * @return the definition of a removed filter
         */
	public SampleFilterDefinition removeSampleFilter(int index) {

		MontageSampleFilter removed = filters.remove(index);
		if (removed == null) {
			return null;
		}

		if (!majorChange) {
			fireMontageSampleFilterRemoved(this, new int[] { index });
			setChanged(true);
		}

		return removed.getDefinition();

	}

        /**
         * Removes all filters from the montage.
         */
	public void clearFilters() {

		filters.clear();

		if (!majorChange) {
			fireMontageSampleFiltersChanged(this);
			setChanged(true);
		}

	}

        /**
         * Checks if a given channel is excluded from a given filter.
         * If a filter is not enabled it is excluded for all channels.
         * @param filterIndex an index of a filter
         * @param channelIndex an index of a channel
         * @return true if a channel is excluded from a filter, false otherwise
         */
	public boolean isFilteringExcluded(int filterIndex, int channelIndex) {
		if (isExcludeAllFilters(channelIndex)) {
			return true;
		} else {
			MontageSampleFilter filter = filters.get(filterIndex);
			if (!filter.isEnabled() || filter.isChannelExcluded(montageChannels.get(channelIndex))) {
				return true;
			}
		}
		return false;
	}

        /**
         * Creates an array of exclusions for a given filter.
         * On position <i>i<\i> is an information if channel <i>i<\i>
         * is excluded from a given filter.
         * If filter is not enabled it is excluded for all channels.
         * @param filterIndex an index of a filter
         * @return created array
         */
	public boolean[] getFilteringExclusionArray(int filterIndex) {
		MontageSampleFilter filter = filters.get(filterIndex);
		if (!filter.isEnabled()) {
			boolean[] trueArray = new boolean[montageChannels.size()];
			Arrays.fill(trueArray, true);
			return trueArray;
		}
		boolean[] exclusionArray = filter.getExclusionArray(montageChannels);
		for (int i=0; i<exclusionArray.length; i++) {
			exclusionArray[i] |= isExcludeAllFilters(i);
		}
		return exclusionArray;
	}

        /**
         * Checks if a filter is enabled (option to exclude all
         * channels checked).
         * @param filterIndex an index of a filter
         * @return true if filter is enabled, false otherwise
         */
	public boolean isFilterEnabled(int filterIndex) {
		return filters.get(filterIndex).isEnabled();
	}

        /**
         * Sets if a filter should be enabled (option to exclude all
         * channels checked).
         * @param filterIndex an index of a filter
         * @param enabled the value to be set
         */
	public void setFilterEnabled(int filterIndex, boolean enabled) {
		MontageSampleFilter filter = filters.get(filterIndex);
		boolean oldEnabled = filter.isEnabled();
		if (oldEnabled != enabled) {
			filter.setEnabled(enabled);

			if (!majorChange) {
				fireMontageSampleFilterExclusionChanged(this, new int[] { filterIndex });
				setChanged(true);
			}

		}
	}

        /**
         * Checks if a channel is excluded from a filter.
         * DOESN'T include a situation when a filter is not enabled or
         * a channel is excluded from all filters.
         * @param filterIndex an index of a filter
         * @param channelIndex an index of a channel
         * @return true if a filter is excluded, false otherwise
         */
	public boolean isFilterChannelExcluded(int filterIndex, int channelIndex) {
		return filters.get(filterIndex).isChannelExcluded(montageChannels.get(channelIndex));
	}

        /**
         * Creates an array of exclusions for a given filter.
         * On position <i>i<\i> is an information if channel <i>i<\i>
         * is excluded from a given filter.
         * DOESN'T include a situation when a filter is not enabled or a channel
         * is excluded from all filters.
         * @param filterIndex an index of a filter
         * @return created array
         */
	public boolean[] getFilterExclusionArray(int filterIndex) {
		return filters.get(filterIndex).getExclusionArray(montageChannels);
	}

        /**
         * Sets if a given channel should be excluded from a given filter.
         * @param filterIndex an index of a filter
         * @param channelIndex an index of a channel
         * @param excluded the value to be set (true if should be excluded,
         * false otherwise)
         */
	public void setFilterChannelExcluded(int filterIndex, int channelIndex, boolean excluded) {

		boolean done;
		if (excluded) {
			done = filters.get(filterIndex).addExcludedChannel(montageChannels.get(channelIndex));
		} else {
			done = filters.get(filterIndex).removeExcludedChannel(montageChannels.get(channelIndex));
		}

		if (done) {
			if (!majorChange) {
				fireMontageSampleFilterExclusionChanged(this, new int[] { filterIndex });
				setChanged(true);
			}
		}

	}

        /**
         * Clears exclusions for a given filter.
         * DOESN'T include a situation when a filter is not enabled or
         * a channel is excluded from all filters.
         * @param filterIndex an index of a filter
         */
	public void clearFilterExclusion(int filterIndex) {
		boolean done = filters.get(filterIndex).clearExclusion();
		if (done) {
			if (!majorChange) {
				fireMontageSampleFilterExclusionChanged(this, new int[] { filterIndex });
				setChanged(true);
			}
		}
	}

        /**
         * Adds a MontageListener to a montage.
         * @param l a MontageListener to be added
         */
	public void addMontageListener(MontageListener l) {
		listenerList.add(MontageListener.class, l);
	}

        /**
         * Removes a MontageListener from a montage.
         * @param l a MontageListener to be removed
         */
	public void removeMontageListener(MontageListener l) {
		listenerList.remove(MontageListener.class, l);
	}

        /**
         * Adds a MontageSampleFilterListener to a montage.
         * @param l a MontageSampleFilterListener to be added
         */
	public void addMontageSampleFilterListener(MontageSampleFilterListener l) {
		listenerList.add(MontageSampleFilterListener.class, l);
	}

        /**
         * Removes a MontageSampleFilterListener from a montage.
         * @param l a MontageSampleFilterListener to be removed
         */
	public void removeMontageSampleFilterListener(MontageSampleFilterListener l) {
		listenerList.remove(MontageSampleFilterListener.class, l);
	}

        /**
         * Fires all MontageListeners that a montage structure has changed.
         * @param source a montage that has changed
         */
	protected void fireMontageStructureChanged(Object source) {
		Object[] listeners = listenerList.getListenerList();
		MontageEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==MontageListener.class) {
				if (e == null) {
					e = new MontageEvent(source, null, null);
				}
				((MontageListener)listeners[i+1]).montageStructureChanged(e);
			}
		}
	}

        /**
         * Fires all MontageListeners that channels has been added.
         * @param source a montage that has changed
         * @param channels an array with indexes of added montage channels
         * @param primaryChannels an array with indexes of SourceChannels
         * that were added
         */
	protected void fireMontageChannelsAdded(Object source, int[] channels, int[] primaryChannels) {
		Object[] listeners = listenerList.getListenerList();
		MontageEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==MontageListener.class) {
				if (e == null) {
					e = new MontageEvent(source, channels, primaryChannels);
				}
				((MontageListener)listeners[i+1]).montageChannelsAdded(e);
			}
		}
	}

         /**
         * Fires all MontageListeners that channels has been removed.
         * @param source a montage that has changed
         * @param channels an array with indexes of removed channels
         * @param primaryChannels an array with indexes of SourceChannels
          * that were removed
         */
	protected void fireMontageChannelsRemoved(Object source, int[] channels, int[] primaryChannels) {
		Object[] listeners = listenerList.getListenerList();
		MontageEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==MontageListener.class) {
				if (e == null) {
					e = new MontageEvent(source, channels, primaryChannels);
				}
				((MontageListener)listeners[i+1]).montageChannelsRemoved(e);
			}
		}
	}

        /**
         * Creates an array of int from LinkedList of Integers.
         * @param list a LinkedList of Integers
         * @return created array
         */
	private int[] toArray(LinkedList<Integer> list) {
		int i = 0;
		int[] indices = new int[list.size()];
		Iterator<Integer> it = list.iterator();
		while (it.hasNext()) {
			indices[i] = it.next();
			i++;
		}
		return indices;
	}

        /**
         * Fires all MontageListeners that channels has been changed.
         * @param source a montage that has changed
         * @param indexList a list with indexes of changed montage channels
         * @param primaryIndexList a list with indexes of SourceChannels that
         * were changed
         */
	protected void fireMontageChannelsChanged(Object source, LinkedList<Integer> indexList, LinkedList<Integer> primaryIndexList) {
		fireMontageChannelsChanged(source, toArray(indexList), toArray(primaryIndexList));
	}

        /**
         * Fires all MontageListeners that channels has been changed.
         * @param source a montage that has changed
         * @param channels an array with indexes of changed montage channels
         * @param primaryChannels an array with indexes of SourceChannels that
         * were changed
         */
	protected void fireMontageChannelsChanged(Object source, int[] channels, int[] primaryChannels) {
		Object[] listeners = listenerList.getListenerList();
		MontageEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==MontageListener.class) {
				if (e == null) {
					e = new MontageEvent(source, channels, primaryChannels);
				}
				((MontageListener)listeners[i+1]).montageChannelsChanged(e);
			}
		}
	}

        /**
         * Fires all MontageListeners that reference between pairs montage
         * channel - source channel has been changed.
         * @param source a montage that has changed
         * @param indexList a list with indexes of montage channels with
         * reference changed
         * @param primaryIndexList a list with indexes of SourceChannels to
         * which references changed
         */
	protected void fireMontageReferenceChanged(Object source, LinkedList<Integer> indexList, LinkedList<Integer> primaryIndexList) {
		fireMontageReferenceChanged(source, toArray(indexList), toArray(primaryIndexList));
	}

        /**
         * Fires all MontageListeners that reference between pairs montage
         * channel - source channel has been changed.
         * @param source a montage that has changed
         * @param channels an array with indexes of montage channels with
         * reference changed
         * @param primaryChannels array with indexes of SourceChannels to which
         * references changed
         */
	protected void fireMontageReferenceChanged(Object source, int[] channels, int[] primaryChannels) {
		Object[] listeners = listenerList.getListenerList();
		MontageEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==MontageListener.class) {
				if (e == null) {
					e = new MontageEvent(source, channels, primaryChannels);
				}
				((MontageListener)listeners[i+1]).montageReferenceChanged(e);
			}
		}
	}

        /**
         * Fires all MontageListeners that SampleFilters were added.
         * @param source a montage that has changed
         * @param indices an array of indexes of added filters
         */
	protected void fireMontageSampleFilterAdded(Object source, int[] indices) {
		Object[] listeners = listenerList.getListenerList();
		MontageSampleFilterEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==MontageSampleFilterListener.class) {
				if (e == null) {
					e = new MontageSampleFilterEvent(source, indices);
				}
				((MontageSampleFilterListener)listeners[i+1]).filterAdded(e);
			}
		}
	}

        /**
         * Fires all MontageListeners that SampleFilters were changed.
         * @param source a montage that has changed
         * @param indices an array of indexes of changed filters
         */
	protected void fireMontageSampleFilterChanged(Object source, int[] indices) {
		Object[] listeners = listenerList.getListenerList();
		MontageSampleFilterEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==MontageSampleFilterListener.class) {
				if (e == null) {
					e = new MontageSampleFilterEvent(source, indices);
				}
				((MontageSampleFilterListener)listeners[i+1]).filterChanged(e);
			}
		}
	}

        /**
         * Fires all MontageListeners that SampleFilters were removed.
         * @param source montage that has changed
         * @param indices array of indexes of removed filters
         */
	protected void fireMontageSampleFilterRemoved(Object source, int[] indices) {
		Object[] listeners = listenerList.getListenerList();
		MontageSampleFilterEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==MontageSampleFilterListener.class) {
				if (e == null) {
					e = new MontageSampleFilterEvent(source, indices);
				}
				((MontageSampleFilterListener)listeners[i+1]).filterRemoved(e);
			}
		}
	}

        /**
         * Fires all MontageListeners that exclusions for SampleFilters were
         * changed.
         * @param source a montage that has changed
         * @param indexList a list of indexes of filters for which exclusions
         * has changed
         */
	protected void fireMontageSampleFilterExclusionChanged(Object source, LinkedList<Integer> indexList) {
		fireMontageSampleFilterExclusionChanged(source, toArray(indexList));
	}

        /**
         * Fires all MontageListeners that exclusions for SampleFilters were
         * changed.
         * @param source a montage that has changed
         * @param indices an array of indexes of filters for which exclusions
         * has changed
         */
	protected void fireMontageSampleFilterExclusionChanged(Object source, int[] indices) {
		Object[] listeners = listenerList.getListenerList();
		MontageSampleFilterEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==MontageSampleFilterListener.class) {
				if (e == null) {
					e = new MontageSampleFilterEvent(source, indices);
				}
				((MontageSampleFilterListener)listeners[i+1]).filterExclusionChanged(e);
			}
		}
	}

        /**
         * Fires all MontageListeners that all SampleFilters were changed.
         * @param source a montage that has changed
         */
	protected void fireMontageSampleFiltersChanged(Object source) {
		Object[] listeners = listenerList.getListenerList();
		MontageSampleFilterEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==MontageSampleFilterListener.class) {
				if (e == null) {
					e = new MontageSampleFilterEvent(source);
				}
				((MontageSampleFilterListener)listeners[i+1]).filtersChanged(e);
			}
		}
	}

}

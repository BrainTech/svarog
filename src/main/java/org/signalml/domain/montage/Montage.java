/* Montage.java created 2007-10-23
 *
 */

package org.signalml.domain.montage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.signalml.app.config.preset.Preset;
import org.signalml.app.document.SignalDocument;
import org.signalml.domain.montage.filter.SampleFilterDefinition;
import org.signalml.util.Util;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** Montage
 * Class representing a montage
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

        /**
         * String representing name of a montage
         */
	private String name;

        /**
         * String containing description of a montage
         */
	private String description;

        /**
         * MontageGenerator for a current object
         */
	private MontageGenerator montageGenerator;

        /**
         * channels of a signal in a montage
         */
	private ArrayList<MontageChannel> montageChannels;

        /**
         * HashMap associating SourceChannel object with list of MontageChannels for which this object is a primaryChannel
         */
	private transient HashMap<SourceChannel,LinkedList<MontageChannel>> montageChannelsByPrimary;

        /**
         * HashMap associating MontageChannels with their labels
         */
	private transient HashMap<String,MontageChannel> montageChannelsByLabel;

        /**
         * List of filters for montage
         */
	private ArrayList<MontageSampleFilter> filters = new ArrayList<MontageSampleFilter>();

        /**
         * Tells whether signal is being filtered
         */
	private boolean filteringEnabled = true;

        /**
         * Tells whether montage is undergoing a major change
         */
	private transient boolean majorChange = false;

        /**
         * Constructor. Creates empty Montage
         */
	protected Montage() {
		super();
	}

        /**
         * Copy constructor
         * @param montage
         */
	public Montage(Montage montage) {
		this();
		copyFrom(montage);
	}

        /**
         * Copy constructor from given SourceMontage (superclass)
         * @param sourceMontage SourceMontage object to be copied as Montage
         */
	public Montage(SourceMontage sourceMontage) {
		this();
		super.copyFrom(sourceMontage);

		montageChannels = new ArrayList<MontageChannel>();
	}

        /**
         * Constructor. Creates Montage from document with a signal
         * @param document document with a signal
         */
	public Montage(SignalDocument document) {
		super(document);

		montageChannels = new ArrayList<MontageChannel>();
	}

        /**
         * Creates a copy a current Montage object
         * @return created object
         */
	@Override
	public Montage clone() {
		Montage montage = new Montage();
		montage.copyFrom(this);
		return montage;
	}

        /**
         * copies given Montage parameters to current object. MontageChannels and filters are also copied.
         * Listeners are not copied
         * @param montage Montage which parameters are to be copied to current object
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

		setName(montage.name);
		setDescription(montage.description);
		setMontageGenerator(montage.montageGenerator);

		fireMontageStructureChanged(this);

	}

        /**
         * Checks if current object is compatible with object given as parameter.
         * Object are compatible if:
         * 1. they are compatible as SourceMontages
         * 2. have the same number of montage channels
         * 3. for each source channel montage channels have the same references
         * @param montage
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
         * Resets parameter of current object
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
         *
         * @return name of a montage
         */
	public String getName() {
		return name;
	}

        /**
         * Sets a name of a montage
         * @param name String with a name to be set
         */
	public void setName(String name) {
		if (!Util.equalsWithNulls(this.name, name)) {
			String oldName = this.name;
			this.name = name;
			pcSupport.firePropertyChange(NAME_PROPERTY, oldName, name);
			setChanged(true);
		}
	}

        /**
         *
         * @return description of a montage
         */
	public String getDescription() {
		return description;
	}

        /**
         * Sets a description of a montage
         * @param description String with a description to be set
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
         * 
         * @return MontageGenerator for a current object
         */
	public MontageGenerator getMontageGenerator() {
		return montageGenerator;
	}

        /**
         * Sets a new montageGenerator for a current object
         * @param montageGenerator MontageGenerator to be set
         */
	public void setMontageGenerator(MontageGenerator montageGenerator) {
		if (!Util.equalsWithNulls(this.montageGenerator, montageGenerator)) {
			MontageGenerator oldGenerator = this.montageGenerator;
			this.montageGenerator = montageGenerator;
			pcSupport.firePropertyChange(MONTAGE_GENERATOR_PROPERTY, oldGenerator, montageGenerator);
			setChanged(true);
		}
	}

        /**
         * Returns whether there was a major change of current object
         * @return true if there was, false otherwise
         */
	public boolean isMajorChange() {
		return majorChange;
	}

        /**
         * Sets majorChange property to a given value
         * @param majorChange value to be set
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
         * Tells whether signal is being filtered
         * @return true if signal is being filtered, false otherwise
         */
	public boolean isFilteringEnabled() {
		return filteringEnabled;
	}

        /**
         * Sets filteringEnabled property to a given value
         * @param filteringEnabled value to be set
         */
	public void setFilteringEnabled(boolean filteringEnabled) {
		if (this.filteringEnabled != filteringEnabled) {
			this.filteringEnabled = filteringEnabled;
			pcSupport.firePropertyChange(FILTERING_ENABLED_PROPERTY, !filteringEnabled, filteringEnabled);
		}
	}

        /**
         *
         * @return String with a name of a current object
         */
	@Override
	public String toString() {
		return name;
	}


        /**
         *
         * @return HashMap associating SourceChannel object with list of MontageChannels for which this object is a primaryChannel
         */
	protected HashMap<SourceChannel, LinkedList<MontageChannel>> getMontageChannelsByPrimary() {
		if (montageChannelsByPrimary == null) {
			montageChannelsByPrimary = new HashMap<SourceChannel, LinkedList<MontageChannel>>();
		}
		return montageChannelsByPrimary;
	}

        /**
         * Returns the list of MontageChannels for which given SourceChannel is a primary channel
         * @param channel SourceChannel object for which list is returned
         * @return list of MontageChannels for which given SourceChannel is a primary channel
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
         *
         * @return HashMap associating MontageChannels with their labels
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
         * Finds a MontageChannel with a given label
         * @param label String with label to be found
         * @return found MontageChannel object
         */
	protected MontageChannel getMontageChannelByLabel(String label) {
		return getMontageChannelsByLabel().get(label);
	}

        /**
         *
         * @return number of MontageChannels in Montage
         */
	public int getMontageChannelCount() {
		return montageChannels.size();
	}

        /**
         * returns number of primary channel for selected MontageChannel
         * @param index index of MontageChannel
         * @return found number of primary channel
         */
	public int getMontagePrimaryChannelAt(int index) {
		return montageChannels.get(index).getPrimaryChannel().getChannel();
	}

        /**
         * Returns a label of MontageChannel at a given index
         * @param index index of MontageChannel
         * @return label of MontageChannel
         */
	public String getMontageChannelLabelAt(int index) {
		return montageChannels.get(index).getLabel();
	}

        /**
         * Sets a new label for a MontageChannel at a given index
         * @param index index of MontageChannel
         * @param label new label to be set
         * @return old label
         * @throws MontageException thrown if label empty, with illegal characters or not unique
         */
	public String setMontageChannelLabelAt(int index, String label) throws MontageException {

		if (label == null || label.isEmpty()) {
			throw new MontageException("error.montageChannelLabelEmpty");
		}
		if (!Util.validateString(label)) {
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
         * Checks whether SourceChannel of given index is in use, i.e.
         * either is a primaryChannel for some MontageChannel, or there is a reference to it in MontageChannel
         * @param index index of SourceChannel
         * @return true if SourceChannel is in use, false otherwise
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
         * Checks if given MontageChannel has a reference to a given SourceChannel
         * @param montageIndex index of MontageChannel
         * @param sourceIndex index of SourceChannel
         * @return true if there is a reference, false otherwise
         */
	public boolean hasReference(int montageIndex, int sourceIndex) {
		return montageChannels.get(montageIndex).hasReference(sourceChannels.get(sourceIndex));
	}

        /**
         * Checks if given MontageChannel has a reference to any SourceChannel
         * @param montageIndex index of MontageChannel
         * @return true if there is a reference, false otherwise
         */
	public boolean hasReference(int montageIndex) {
		return montageChannels.get(montageIndex).hasReference();
	}

        /**
         * Returns list of MontageChannels for which SourceChannel of a given index is a primary channel
         * @param index index of SourceChannel object
         * @return list of MontageChannels for which SourceChannel of a given index is a primary channel
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
         * Creates unique label for a MontageChannel based on a given Sting
         * @param stem String object on which new label will be based
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
         * For a MontageChannel of a given index, returns array of references in form of Strings
         * @param index index of MontageChannel object
         * @return array of references for a MontageChannel of a given index
         */
	public String[] getReference(int index) {
		String[] references = new String[sourceChannels.size()];
		montageChannels.get(index).getReferences(references);
		return references;
	}

        /**
         * For a MontageChannel of a given index, returns array of references in form of floats (converted from Strings)
         * @param index of MontageChannel object
         * @return array of references for a MontageChannel of a given index
         */
	public float[] getReferenceAsFloat(int index) {
		float[] references = new float[sourceChannels.size()];
		montageChannels.get(index).getReferencesAsFloat(references);
		return references;
	}

        /**
         * Sets a new array of references for a MontageChannel of a given index
         * @param index index of MontageChannel object
         * @param references list of references in form of String
         * @throws NumberFormatException thrown when references array is to long (larger then number of sourceChannels)
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
         * Returns the reference between a given MontageChannel and a given SourceChannel
         * @param montageIndex index of MontageChannel
         * @param sourceIndex index of SourceChannel
         * @return reference between a given MontageChannel and a given SourceChannel
         */
	public String getReference(int montageIndex, int sourceIndex) {
		return montageChannels.get(montageIndex).getReference(sourceChannels.get(sourceIndex));
	}

        /**
         * Checks if the reference between a given MontageChannel and a given SourceChannel is symmetric
         * @param montageIndex index of MontageChannel
         * @param sourceIndex index of SourceChannel
         * @return true if reference is symmetric, false otherwise
         */
	public boolean isReferenceSymmetric(int montageIndex, int sourceIndex) {
		return montageChannels.get(montageIndex).isSymmetricWeight(sourceChannels.get(sourceIndex));
	}

        /**
         * Sets the reference between a given MontageChannel and a given SourceChannel to a given value
         * @param montageIndex index of MontageChannel
         * @param sourceIndex index of SourceChannel
         * @param value value of reference to be set
         * @throws NumberFormatException thrown when references array is to long (larger then number of sourceChannels)
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
         * Removes the reference between a given MontageChannel and a given SourceChannel
         * @param montageIndex index of MontageChannel
         * @param sourceIndex index of SourceChannel
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
         * Adds new SourceChannel with a given label and function to sourceChannels list
         * @param label unique label for new SourceChannel
         * @param function unique function for new SourceChannel
         * @throws MontageException thrown when label or function not unique
         */
	@Override
	public void addSourceChannel(String label, Channel function) throws MontageException {
		super.addSourceChannel(label, function);
		setMontageGenerator(null);
	}

        /**
         * Removes last SourceChannel on the sourceChannels list, montage channels with it as a primary and all references to it
         * @return removed SourceChannel
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
         * Adds given MontageChannel to necessary collections (montageChannels, montageChannelsByLabel, montageChannelsByPrimary).
         * To montageChannels is added at given index.
         * @param channel MontageChannel to be added
         * @param atIndex index at which channel will be added to montageChannels list. If atIndex<0 then will be added at the end of a list
         * @return index at which channel was added to montageChannels list.
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
         * Creates montage channel using SourceChannel of given index as primaryChannel and puts it at selected index on montageChannels list
         * @param sourceIndex index of a SourceChannel
         * @param atIndex index at which channel will be added to montageChannels list. If atIndex<0 then will be added at the end of a list
         * @return index at which channel was added to montageChannels list.
         */
	public int addMontageChannel(int sourceIndex, int atIndex) {
		int[] sourceIndices = new int[] { sourceIndex };
		int[] indices = addMontageChannels(sourceIndices, atIndex);
		return indices[0];
	}

        /**
         * Creates montage channel using SourceChannel of given index as primaryChannel and puts it at the end montageChannels list
         * @param sourceIndex index of a SourceChannel
         * @return index at which channel was added to montageChannels list.
         */
	public int addMontageChannel(int sourceIndex) {
		return addMontageChannel(sourceIndex, -1);
	}

        /**
         * For each index on a sourceIndices list creates a MontageChannel with SourceChannel of this index as primaryChannel.
         * Puts created MontageChannel at the end montageChannels list
         * @param sourceIndices list of indexes of SourceChannels
         * @return list of indexes of created MontageChannels on montageChannels list
         */
	public int[] addMontageChannels(int[] sourceIndices) {
		return addMontageChannels(sourceIndices, -1);
	}

        /**
         * Creates a MontageChannel from SourceChannels of <i>count</i> consecutive indexes starting from <i>fromSourceIndex</i>
         * and puts them on <i>montageChannels</i> list starting from <i>atIndex</i>
         * @param fromSourceIndex index of a SourceChannel from which adding new MontageChannels will be started
         * @param count number of MontageChannels to be added
         * @param atIndex atIndex index starting from which created MontageChannels are to be put. If atIndex<0 then will be added at the end of a list
         * @return list of indexes of created MontageChannels on montageChannels list
         */
	public int[] addMontageChannels(int fromSourceIndex, int count, int atIndex) {
		int[] sourceIndices = new int[count];
		for (int i=0; i<count; i++) {
			sourceIndices[i] = fromSourceIndex + i;
		}
		return addMontageChannels(sourceIndices, atIndex);
	}

        /**
         * Creates a MontageChannel from SourceChannels of <i>count</i> consecutive indexes starting from <i>fromSourceIndex</i>
         * and puts them at the end of <i>montageChannels</i> list
         * @param fromSourceIndex index of a SourceChannel from which adding new MontageChannels will be started
         * @param count number of MontageChannels to be added
         * @return list of indexes of created MontageChannels on montageChannels list
         */
	public int[] addMontageChannels(int fromSourceIndex, int count) {
		return addMontageChannels(fromSourceIndex, count, -1);
	}


        /**
         * For each index on a sourceIndices list creates a MontageChannel with SourceChannel of this index as primaryChannel.
         * Puts created MontageChannel on a montageChannels list starting from given index
         * @param sourceIndices list of indexes of SourceChannels
         * @param atIndex index starting from which created MontageChannels are to be put. If atIndex<0 then will be added at the end of a list
         * @return list of indexes of created MontageChannels on montageChannels list
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
         * Adds MontageChanel to a bipolar montage with a reference
         * @param sourceIndex index of a SourceChannel
         * @param atIndex atIndex index at which channel will be added to montageChannels list. If atIndex<0 then will be added at the end of a list
         * @param label label of a new MontageChanel
         * @param referenceChannel index of SourceChannel to which montageChannel should have a reference
         * @return index at which channel was added to montageChannels list.
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
         * Adds MontageChanel to a bipolar montage with a reference. Puts it at the end of montageChannels list
         * @param sourceIndex index of a SourceChannel
         * @param label label of a new MontageChanel
         * @param referenceChannel index of SourceChannel to which montageChannel should have a reference
         * @return index at which channel was added to montageChannels list.
         */
	public int addBipolarMontageChannel(int sourceIndex, String label, int referenceChannel) {
		return addBipolarMontageChannel(sourceIndex, -1, label, referenceChannel);
	}

        /**
         * Removes montage channel of specified index
         * @param index index of channel to be removed
         * @return removed MontageChannel object
         */
	public MontageChannel removeMontageChannel(int index) {
		int[] indices = new int[] { index };
		MontageChannel[] channels = removeMontageChannels(indices);
		return channels[0];
	}

        /**
         * Removes consecutive montage channels starting from given index
         * @param fromIndex index to start from
         * @param count number of channels to be removed
         * @return array of removed MontageChannle objects
         */
	public MontageChannel[] removeMontageChannels(int fromIndex, int count) {
		int[] indices = new int[count];
		for (int i=0; i<count; i++) {
			indices[i] = fromIndex + i;
		}
		return removeMontageChannels(indices);
	}

	/**
         * Removes montage channels of specified indexes
         * @param indices list of indexes of channels to be removed
         * @return array of removed MontageChannle objects
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
         * Moves count consecutive montage channels on montageChannels list starting from given index
         * @param fromIndex index from which selecting montage channels to be moved starts
         * @param count number of consecutive MontageChannels to be moved
         * @param delta number of positions MontageChannels are to be moved. If &gt 0 channels are moved forward, if &lt 0 are moved backward
         * @return number of positions MontageChannels were moved
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
         * Returns if montage is bipolar (has only bipolar references)
         * @return true if montage is bipolar, false otherwise
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
         * Returns whether montage is filtered
         * @return true if montage is filtered, false otherwise
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
         * Checks whether given montage channel is excluded from all filters
         * @param channel number of montage channel to be checked
         * @return true if montage channel is excluded from all filters, false otherwise
         */
	public boolean isExcludeAllFilters(int channel) {
		return montageChannels.get(channel).isExcludeAllFilters();
	}

        /**
         * Sets if given montage channel should exclude all filters
         * @param channel number of montage channel for which new value is to be set
         * @param exclude true if all channels should be excluded, false otherwise
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
         *
         * @return number of filters for a montage
         */
	public int getSampleFilterCount() {
		return filters.size();
	}

        /**
         * Returns definition of a filter of a given index
         * @param index index of a filter
         * @return definition of a filter
         */
	public SampleFilterDefinition getSampleFilterAt(int index) {
		return filters.get(index).getDefinition();
	}

        /**
         * Adds a new filter to a montage
         * @param definition definition of a filter to be added
         * @return index of added filter
         */
	public int addSampleFilter(SampleFilterDefinition definition) {

		MontageSampleFilter filter = new MontageSampleFilter(definition);
		filters.add(filter);
		int index = filters.indexOf(filter);

		if (!majorChange) {
			fireMontageSampleFilterAdded(this, new int[] { index });
			setChanged(true);
		}

		return index;

	}

        /**
         * Changes the definition of a given filter
         * @param index index of a filter
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
         * Removes filter from montage
         * @param index index of filter to be removed
         * @return definition of removed filter
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
         * Removes all filters from a montage
         */
	public void clearFilters() {

		filters.clear();

		if (!majorChange) {
			fireMontageSampleFiltersChanged(this);
			setChanged(true);
		}

	}

        /**
         * Checks if given channel is excluded from a given filter.
         * If filter is not enabled it is excluded for all channels.
         * @param filterIndex index of a filter
         * @param channelIndex index of a channel
         * @return true if channel is excluded from a filter, false otherwise
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
         * On position <i>i<\i> is an information if channel <i>i<\i> is excluded from given filter.
         * If filter is not enabled it is excluded for all channels.
         * @param filterIndex index of a filter
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
         * Checks if filter is enabled (option to exclude all channels checked)
         * @param filterIndex index of a filter
         * @return true if filter is enabled, false otherwise
         */
	public boolean isFilterEnabled(int filterIndex) {
		return filters.get(filterIndex).isEnabled();
	}

        /**
         * Sets if filter should be enabled (option to exclude all channels checked)
         * @param filterIndex index of a filter
         * @param enabled value to be set
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
         * Checks if channel is excluded from a filter.
         * DOESN'T include a situation when filter is not enabled or channel is excluded from all filters.
         * @param filterIndex index of a filter
         * @param channelIndex index of a channel
         * @return true if filter is excluded, false otherwise
         */
	public boolean isFilterChannelExcluded(int filterIndex, int channelIndex) {
		return filters.get(filterIndex).isChannelExcluded(montageChannels.get(channelIndex));
	}

        /**
         * Creates an array of exclusions for a given filter.
         * On position <i>i<\i> is an information if channel <i>i<\i> is excluded from given filter.
         * DOESN'T include a situation when filter is not enabled or channel is excluded from all filters.
         * @param filterIndex index of a filter
         * @return created array
         */
	public boolean[] getFilterExclusionArray(int filterIndex) {
		return filters.get(filterIndex).getExclusionArray(montageChannels);
	}

        /**
         * Sets if given channel should be excluded from a given filter.
         * @param filterIndex index of a filter
         * @param channelIndex index of a channel
         * @param excluded value to be set (true if should be excluded, false otherwise)
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
         * DOESN'T include a situation when filter is not enabled or channel is excluded from all filters.
         * @param filterIndex
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
         * Adds a MontageListener to a montage
         * @param l MontageListener to be added
         */
	public void addMontageListener(MontageListener l) {
		listenerList.add(MontageListener.class, l);
	}

        /**
         * Removes a MontageListener from a montage
         * @param l MontageListener to be removed
         */
	public void removeMontageListener(MontageListener l) {
		listenerList.remove(MontageListener.class, l);
	}

        /**
         * Adds a MontageSampleFilterListener to a montage
         * @param l MontageSampleFilterListener to be added
         */
	public void addMontageSampleFilterListener(MontageSampleFilterListener l) {
		listenerList.add(MontageSampleFilterListener.class, l);
	}

        /**
         * Removes a MontageSampleFilterListener from a montage
         * @param l MontageSampleFilterListener to be removed
         */
	public void removeMontageSampleFilterListener(MontageSampleFilterListener l) {
		listenerList.remove(MontageSampleFilterListener.class, l);
	}

        /**
         * Fires all MontageListeners that montage structure has changed
         * @param source montage that has changed
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
         * Fires all MontageListeners that channels has been added
         * @param source montage that has changed
         * @param channels array with indexes of added channels
         * @param primaryChannels array with indexes of SourceChannels that were added
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
         * Fires all MontageListeners that channels has been removed
         * @param source montage that has changed
         * @param channels array with indexes of removed channels
         * @param primaryChannels array with indexes of SourceChannels that were removed
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
         * Creates an array of int from LinkedList of Integers
         * @param list LinkedList of Integers
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
         * Fires all MontageListeners that channels has been changed
         * @param source montage that has changed
         * @param indexList list with indexes of changed channels
         * @param primaryIndexList list with indexes of SourceChannels that were changed
         */
	protected void fireMontageChannelsChanged(Object source, LinkedList<Integer> indexList, LinkedList<Integer> primaryIndexList) {
		fireMontageChannelsChanged(source, toArray(indexList), toArray(primaryIndexList));
	}

        /**
         * Fires all MontageListeners that channels has been changed
         * @param source montage that has changed
         * @param channels array with indexes of changed channels
         * @param primaryChannels array with indexes of SourceChannels that were changed
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
         * Fires all MontageListeners that reference between pairs montage channel - source channel has been changed
         * @param source montage that has changed
         * @param indexList list with indexes of channels with reference changed
         * @param primaryIndexList list with indexes of SourceChannels to which references changed
         */
	protected void fireMontageReferenceChanged(Object source, LinkedList<Integer> indexList, LinkedList<Integer> primaryIndexList) {
		fireMontageReferenceChanged(source, toArray(indexList), toArray(primaryIndexList));
	}

        /**
         * Fires all MontageListeners that reference between pairs montage channel - source channel has been changed
         * @param source montage that has changed
         * @param channels array with indexes of channels with reference changed
         * @param primaryChannels array with indexes of SourceChannels to which references changed
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
         * Fires all MontageListeners that SampleFilters were added
         * @param source montage that has changed
         * @param indices array of indexes of added filters
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
         * Fires all MontageListeners that SampleFilters were changed
         * @param source montage that has changed
         * @param indices array of indexes of changed filters
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
         * Fires all MontageListeners that SampleFilters were removed
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
         * Fires all MontageListeners that exclusions for SampleFilters were changed
         * @param source montage that has changed
         * @param indexList list of indexes of filters for which exclusions has changed
         */
	protected void fireMontageSampleFilterExclusionChanged(Object source, LinkedList<Integer> indexList) {
		fireMontageSampleFilterExclusionChanged(source, toArray(indexList));
	}

        /**
         * Fires all MontageListeners that exclusions for SampleFilters were changed
         * @param source montage that has changed
         * @param indices array of indexes of filters for which exclusions has changed
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
         * Fires all MontageListeners that all SampleFilters were changed
         * @param source montage that has changed
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

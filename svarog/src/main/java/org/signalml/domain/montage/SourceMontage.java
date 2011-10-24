/* SourceMontage.java created 2007-11-22
 *
 */

package org.signalml.domain.montage;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.signalml.app.document.SignalDocument;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.SignalType;
import org.signalml.domain.montage.ChannelType;
import org.signalml.domain.signal.SignalTypeConfigurer;
import org.signalml.exception.SanityCheckException;
import org.signalml.util.Util;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.signalml.domain.montage.eeg.ChannelFunction;
import org.signalml.domain.montage.system.EegElectrode;
import org.signalml.domain.montage.system.EegSystem;

/**
 * This class represents a source montage.
 * Source montage consists of a list of {@link SourceChannel source channels},
 * out of which every one has an assigned {@link Channel function (location)}.
 * This class has also assigned listeners informing about changes in it.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("sourcemontage")
public class SourceMontage {

	public static final String CHANGED_PROPERTY = "changed";

	protected static final Logger logger = Logger.getLogger(SourceMontage.class);

        /**
         * a {@link SignalType type} of a signal for this SourceMontage
         */
	protected SignalType signalType;

	private String eegSystemName;
	private transient EegSystem eegSystem;

        /**
         * a list of SourceChannels in this SourceMontage
         */
	protected ArrayList<SourceChannel> sourceChannels;

        /**
         * {@link SignalTypeConfigurer configurer} for a signal type
         */
	private transient SignalTypeConfigurer signalTypeConfigurer;

        /**
         * HashMap associating {@link SourceChannel source channels}
         * with their function
         */
	private transient HashMap<IChannelFunction,LinkedList<SourceChannel>> sourceChannelsByFunction;

        /**
         * HashMap associating {@link SourceChannel source channels}
         * with their labels
         */
	private transient HashMap<String,SourceChannel> sourceChannelsByLabel;

        /**
         * list of EventListeners associated with this source montage
         */
	protected transient EventListenerList listenerList = new EventListenerList();

        /**
         * PropertyChangeSupport associated with this source montage
         */
	protected transient PropertyChangeSupport pcSupport = new PropertyChangeSupport(this);

        /**
         * informs whether this source montage has been changed
         */
	private transient boolean changed = false;

        /**
         * Constructor. Creates an empty SourceMontage.
         */
	protected SourceMontage() {
	}

        /**
         * Constructor. Creates a SourceMontage for a given
         * {@link SignalType type} of a signal, but without channels.
         * @param signalType a type of a signal for which a SourceMontage
         * is to be created
         */
	public SourceMontage(SignalType signalType) {
		if (signalType == null) {
			throw new NullPointerException("Signal type null");
		}
		this.signalType = signalType;
		sourceChannels = new ArrayList<SourceChannel>();
	}

        /**
         * Constructor. Creates a SourceMontage for a given
         * {@link SignalType type} of a signal with channelCount empty channels.
         * @param signalType a type of a signal
         * @param channelCount a number of channels to be created
         * @throws SanityCheckException thrown when addSourceChannel fails
         * because of duplicate labels/functions.
         * Means there is an error in code
         */
        //TODO moim zdaniem ten wyjątek nie ma prawa być wyrzucony
	public SourceMontage(SignalType signalType, int channelCount) {

		this(signalType);

		SignalTypeConfigurer configurer = getSignalTypeConfigurer();
		try {
			for (int i=0; i<channelCount; i++) {
				addSourceChannel("L" + (i+1), configurer.genericChannel());
			}
		} catch (MontageException ex) {
			throw new SanityCheckException("Failed to build default source montage", ex);
		}

	}

        /**
         * Constructor. Creates a SourceMontage from a given
         * {@link SignalDocument document} with a signal.
         * @param document a document with a signal
         * @throws SanityCheckException if {@link #addSourceChannel} fails
         * because of duplicate labels/functions.
         * Means there is error in code.
         */
	public SourceMontage(SignalDocument document) {

		this(document.getType());

		MultichannelSampleSource mss = document.getSampleSource();
		int channelCount = mss.getChannelCount();

		String label;
		SignalTypeConfigurer configurer = getSignalTypeConfigurer();
		HashMap<String, SourceChannel> map = getSourceChannelsByLabel();
		IChannelFunction function;
		for (int i=0; i<channelCount; i++) {
			label = mss.getLabel(i);
			function = configurer.channelForName(label);
			if (map.containsKey(label)) {
				logger.warn("WARNING! Duplicate label [" + label + "]");
				label = getNewSourceChannelLabel(label);
				logger.debug("Changed to [" + label + "]");
			}
			if (function.isUnique() && !getSourceChannelsByFunctionList(function).isEmpty()) {
				logger.warn("WARNING! Duplicate function [" + function + "] changing to generic");
				function = configurer.genericChannel();
			}
			try {
				addSourceChannel(label, configurer.channelForName(label));
			} catch (MontageException ex) {
				throw new SanityCheckException("addSourceChannel still failed");
			}
		}

	}

        /**
         * Copy constructor.
         * @param montage a SourceMontage to be copied
         */
	public SourceMontage(SourceMontage montage) {
		super();
		copyFrom(montage);
	}

        /**
         * Copies the given SourceMontage parameters to this source montage.
         * {@link #sourceChannels Source channels} are also copied.
         * {@link #listenerList Listeners} are not copied.
         * @param montage a SourceMontage which parameters are to be copied
         */
	protected void copyFrom(SourceMontage montage) {
		setChanged(montage.changed);
		signalType = montage.signalType;

		sourceChannels = new ArrayList<SourceChannel>(montage.sourceChannels.size());
		HashMap<String, SourceChannel> map = getSourceChannelsByLabel();
		map.clear();
		getSourceChannelsByFunction().clear();
		SourceChannel newChannel;
		LinkedList<SourceChannel> list;
		for (SourceChannel channel : montage.sourceChannels) {
			newChannel = new SourceChannel(channel);
			list = getSourceChannelsByFunctionList(newChannel.getFunction());
			sourceChannels.add(newChannel);
			map.put(newChannel.getLabel(), newChannel);
			list.add(newChannel);
		}

		setEegSystem(montage.eegSystem);
	}

        /**
         * Checks if this montage has the same number of source channels
         * as signal in a given {@link SignalDocument document}.
         * @param document a document with signal to be compared with
         * this source montage
         * @return true if a number of {@link SourceChannel source channels}
         * is the same, false otherwise
         */
	public boolean isCompatible(SignalDocument document) {
		return(document.getChannelCount() == getSourceChannelCount());
	}

        /**
         * Checks if this source montage has the same number of
         * {@link SourceChannel source channels} as given SourceMontage object.
         * @param montage a SourceMontage to be compared with this source
         * montage
         * @return true if number of source channels is the same, false otherwise
         */
	public boolean isCompatible(SourceMontage montage) {
		return(montage.getSourceChannelCount() == getSourceChannelCount());
	}

        /**
         * Adapts this source montage to a given {@link SignalDocument document}
         * with a signal.
         * Changes the number of {@link SourceChannel source channels}
         * (adds from file or removes excessive) to make it equal with the
         * number of channels in a document.
         * @param document a document with a signal to which this source montage
         * is to be adapted
         */
	public void adapt(SignalDocument document) {

		int dCnt = document.getChannelCount();
		int mCnt = getSourceChannelCount();

		if (dCnt > mCnt) {
			MultichannelSampleSource mss = document.getSampleSource();
			for (int i=mCnt; i<dCnt; i++) {
				try {
					addSourceChannel(getNewSourceChannelLabel(mss.getLabel(i)), null);
				} catch (MontageException ex) {
					throw new SanityCheckException(ex);
				}
			}
		} else if (dCnt < mCnt) {
			for (int i=mCnt-1; i>=dCnt; i--) {
				if ((this.getSourceChannelFunctionAt(i).getType() == ChannelType.ZERO)
					|| (this.getSourceChannelFunctionAt(i).getType() == ChannelType.ONE))
					continue;
				removeSourceChannel();
			}
		}
	}

        /**
         * Returns the {@link SignalType type} of a signal for this source
         * montage.
         * @return the type of a signal for current object
         */
	public SignalType getSignalType() {
		return signalType;
	}

	public EegSystem getEegSystem() {
		return eegSystem;
	}

	public void setEegSystem(EegSystem eegSystem) {
		this.eegSystem = eegSystem;

		if (eegSystem == null) {
			eegSystemName = null;
			return;
		}
		else {
			eegSystemName = eegSystem.getName();
		}

		for (SourceChannel sourceChannel: sourceChannels) {
			refreshElectrodeAndFunctionForSourceChannel(sourceChannel);
		}
		fireSourceMontageEegSystemChanged(this);
	}

	protected void refreshElectrodeAndFunctionForSourceChannel(SourceChannel sourceChannel) {
		if (sourceChannel == null)
			return;
		if (eegSystem == null)
			return;
		EegElectrode electrodeForChannel = eegSystem.getElectrode(sourceChannel.getLabel());
		if (electrodeForChannel != null) {
			sourceChannel.setEegElectrode(electrodeForChannel);
			sourceChannel.setFunction(ChannelFunction.EEG);
		}
		else {
			sourceChannel.setEegElectrode(null);
			sourceChannel.setFunction(ChannelFunction.UNKNOWN);
		}
		fireSourceMontageChannelChanged(this, sourceChannel.getChannel());
	}

        /**
         * Informs whether this SourceMontage has been changed.
         * @return true if this SourceMontage has been changed, false otherwise
         */
	public boolean isChanged() {
		return changed;
	}


        /**
         * Sets {@link #changed <code>changed</code>} parameter.
         * @param changed a changed parameter to be set
         */
	public void setChanged(boolean changed) {
		if (this.changed != changed) {
			this.changed = changed;
			pcSupport.firePropertyChange(CHANGED_PROPERTY, !changed, changed);
		}
	}

        /**
         * Returns the {@link SignalTypeConfigurer configurer} for
         * this source montage.
         * @return the configurer for this source montage
         */
	public SignalTypeConfigurer getSignalTypeConfigurer() {
		if (signalTypeConfigurer == null) {
			signalTypeConfigurer = signalType.getConfigurer();
			if (signalTypeConfigurer == null) {
				throw new NullPointerException("Configurer null");
			}
		}
		return signalTypeConfigurer;
	}

        /**
         * Returns a HashMap associating {@link SourceChannel source channels}
         * with their function.
         * If doesn't exists it is created as empty and returned.
         * @return a HashMap associating source channels with their function.
         */
	protected HashMap<IChannelFunction, LinkedList<SourceChannel>> getSourceChannelsByFunction() {
		if (sourceChannelsByFunction == null) {
			sourceChannelsByFunction = new HashMap<IChannelFunction, LinkedList<SourceChannel>>();
		}
		return sourceChannelsByFunction;
	}

        /**
         * Returns list of {@link SourceChannel source channels} with a
         * given {@link Channel function}.
         * @param function a function that source channels should fulfil
         * @return list of source channels with a given function
         */
	protected LinkedList<SourceChannel> getSourceChannelsByFunctionList(IChannelFunction function) {
		HashMap<IChannelFunction, LinkedList<SourceChannel>> map = getSourceChannelsByFunction();
		LinkedList<SourceChannel> list = map.get(function);
		if (list == null) {
			list = new LinkedList<SourceChannel>();
			map.put(function, list);
			for (SourceChannel channel : sourceChannels) {
				if (channel.getFunction() == function) {
					list.add(channel);
				}
			}
		}
		return list;
	}

        /**
         * Returns a HashMap associating {@link SourceChannel source channels}
         * with their labels.
         * If doesn't exist it is created.
         * @return a HashMap associating source channels with their labels.
         */
	protected HashMap<String,SourceChannel> getSourceChannelsByLabel() {
		if (sourceChannelsByLabel == null) {
			sourceChannelsByLabel = new HashMap<String, SourceChannel>();
			for (SourceChannel channel : sourceChannels) {
				sourceChannelsByLabel.put(channel.getLabel(), channel);
			}
		}
		return sourceChannelsByLabel;
	}

        /**
         * Returns a {@link SourceChannel source channel} of a given label.
         * @param label a label of source channel to be found
         * @return the found source channel
         */
	protected SourceChannel getSourceChannelByLabel(String label) {
		return getSourceChannelsByLabel().get(label);
	}

        /**
         * Returns the number of {@link SourceChannel source channels}
         * @return the number of source channels
         */
	public int getSourceChannelCount() {
		return sourceChannels.size();
	}

        /**
         * Finds the label of a {@link SourceChannel source channel} of a
         * given index.
         * @param index an index of source channel to be found
         * @return the label of a source channel of a given index
         */
	public String getSourceChannelLabelAt(int index) {
		return sourceChannels.get(index).getLabel();
	}

	public SourceChannel getSourceChannelAt(int index) {
		return sourceChannels.get(index);
	}

        /**
         * Returns the function of a {@link SourceChannel source channel} of
         * a given index.
         * @param index an index of a source channel to be found
         * @return the function of a source channel of a given index
         */
	public IChannelFunction getSourceChannelFunctionAt(int index) {
		return sourceChannels.get(index).getFunction();
	}

        /**
         * Finds a {@link SourceChannel source channel} of a given index and
         * changes its label to a given value
         * @param index an index of source channel
         * @param label a String with a unique label to be set
         * @return old a label of found source channel
         * @throws MontageException thrown when the label empty, not unique
         * or containing invalid characters
         */
	public String setSourceChannelLabelAt(int index, String label) throws MontageException {

		if (label == null || label.isEmpty()) {
			throw new MontageException("error.sourceChannelLabelEmpty");
		}
		if (Util.hasSpecialChars(label)) {
			throw new MontageException("error.sourceChannelLabelBadChars");
		}

		SourceChannel channel = sourceChannels.get(index);
		String oldLabel = channel.getLabel();
		HashMap<String, SourceChannel> map = getSourceChannelsByLabel();

		if (!oldLabel.equals(label)) {

			SourceChannel namedChannel = map.get(label);
			if (namedChannel != null && namedChannel != channel) {
				throw new MontageException("error.sourceChannelLabelDuplicate");
			}
			channel.setLabel(label);
			map.remove(oldLabel);
			map.put(label, channel);

			// see about function update
			refreshElectrodeAndFunctionForSourceChannel(channel);
			/*IChannelFunction function = channel.getFunction();
			if (function.getType() == ChannelType.UNKNOWN) {
				// function is unknown, we can update, let's see what we get
				IChannelFunction newFunctionCandidate = getSignalTypeConfigurer().channelForName(label);
				if (newFunctionCandidate.getType() != ChannelType.UNKNOWN) {
					// the function is known, check if we could use it
					if (!newFunctionCandidate.isUnique() || getSourceChannelsByFunctionList(newFunctionCandidate).isEmpty()) {
						// we could, do it
						getSourceChannelsByFunctionList(function).remove(channel);
						channel.setFunction(newFunctionCandidate);
						getSourceChannelsByFunctionList(newFunctionCandidate).add(channel);
					}

				}
			}*/

			fireSourceMontageChannelChanged(this, channel.getChannel());
			setChanged(true);

		}

		return oldLabel;

	}

        /**
         * Finds a {@link SourceChannel source channel} of a given index and
         * changes its {@link Channel function} to a given value.
         * @param index an index of source channel
         * @param function a Channel object with a new function for a SourceChannel
         * @return old a function of found source channel
         * @throws MontageException if the function is not unique
         */
	public IChannelFunction setSourceChannelFunctionAt(int index, IChannelFunction function) throws MontageException {

		SourceChannel channel = sourceChannels.get(index);
		IChannelFunction oldFunction = channel.getFunction();

		if (oldFunction != function) {

			LinkedList<SourceChannel> list = getSourceChannelsByFunctionList(function);
			if (function.isUnique() && !list.isEmpty()) {
				throw new MontageException("error.sourceChannelFunctionDuplicate");
			}
			if (!function.isMutable()) {
				throw new MontageException("error.sourceChannelFunctionImmutable");
			}
			LinkedList<SourceChannel> oldList = getSourceChannelsByFunctionList(oldFunction);
			oldList.remove(channel);
			channel.setFunction(function);
			list.add(channel);
			fireSourceMontageChannelChanged(this, channel.getChannel());
			setChanged(true);

		}

		return oldFunction;

	}

        /**
         * Returns an array of indexes of {@link SourceChannel source channels}
         * with a given {@link Channel function}.
         * @param function a function that source channels should fulfil
         * @return an array of indexes of source channels with a given function
         */
	public int[] getSourceChannelsByFunction(IChannelFunction function) {
		LinkedList<SourceChannel> list = getSourceChannelsByFunctionList(function);
		int[] indices = new int[list.size()];
		int i = 0;
		for (SourceChannel channel : list) {
			indices[i] = channel.getChannel();
			i++;
		}
		return indices;
	}

	public int[] getSourceChannelsByTypes(ChannelType[] types) {
		LinkedList<SourceChannel> list = getSourceChannelsByFunctionList(null);
		int indSize = 0;
		for (int i = 0; i < this.getSourceChannelCount(); i++)
			for (int j = 0; j < types.length; j++)
				if (this.getSourceChannelFunctionAt(i).getType() == types[j])
					indSize ++;

		int[] indices = new int[indSize];
		int ind = 0;
		for (int i = 0; i < this.getSourceChannelCount(); i++)
			for (int j = 0; j < types.length; j++)
				if (this.getSourceChannelFunctionAt(i).getType() == types[j]) {
					indices[ind] = i;
					ind ++;
				}
		return indices;
	}

        /**
         * Returns an index of a first {@link SourceChannel source channel}
         * with a given {@link Channel function}.
         * @param function a function that source channel should fulfil
         * @return an index of a first source channel with a given function,
         * -1 if doesn't exist
         */
	public int getFirstSourceChannelWithFunction(IChannelFunction function) {
		LinkedList<SourceChannel> list = getSourceChannelsByFunctionList(function);
		if (list.isEmpty()) {
			return -1;
		}
		return list.getFirst().getChannel();
	}

        /**
         * Returns an index of a first {@link SourceChannel source channel} with
         * a given label.
         * @param label a String with a label to be found
         * @return an index of a first source channel with a given label,
         * -1 if doesn't exist
         */
	public int getSourceChannelForLabel(String label) {
		SourceChannel channel = getSourceChannelsByLabel().get(label);
		if (channel == null) {
			return -1;
		}
		return channel.getChannel();
	}

	/**
         * Adds a new {@link SourceChannel source channel} with a given label
         * and {@link Channel function}.
         * @param label a unique label for new source channel
         * @param function a unique function for new source channel
         * @throws MontageException thrown when label or function not unique
         */
        public void addSourceChannel(String label, IChannelFunction function) throws MontageException {

		IChannelFunction nonNullChannel = (function != null ? function : getSignalTypeConfigurer().genericChannel());

		HashMap<String, SourceChannel> map = getSourceChannelsByLabel();
		if (map.containsKey(label)) {
			throw new MontageException("error.sourceChannelLabelDuplicate");
		}

		LinkedList<SourceChannel> list = getSourceChannelsByFunctionList(nonNullChannel);
		if (nonNullChannel.isUnique() && !list.isEmpty()) {
			throw new MontageException("error.sourceChannelFunctionDuplicate");
		}

		SourceChannel channel = new SourceChannel(sourceChannels.size(), label, nonNullChannel);
		sourceChannels.add(channel);
		map.put(label, channel);
		list.add(channel);

		fireSourceMontageChannelAdded(this, channel.getChannel());
		setChanged(true);

	}

        /**
         * Removes the last {@link SourceChannel source channel} on the
         * {@link #sourceChannels sourceChannels} list from this SourceMontage
         * @return the removed source channel
         */
	public SourceChannel removeSourceChannel() {
		if (sourceChannels.isEmpty()) {
			return null;
		}
		int index = sourceChannels.size() - 1;
		SourceChannel channel = sourceChannels.get(index);
		getSourceChannelsByLabel().remove(channel.getLabel());
		getSourceChannelsByFunctionList(channel.getFunction()).remove(channel);
		sourceChannels.remove(index);
		fireSourceMontageChannelRemoved(this, index);
		setChanged(true);

		return channel;
	}

        /**
         * Finds a unique label for a {@link SourceChannel source channel}.
         * @param stem a String on which new label is to be based
         * @return a unique label for a source channel
         */
	public String getNewSourceChannelLabel(String stem) {

		int cnt = 2;

		String candidate = stem;
		HashMap<String, SourceChannel> map = getSourceChannelsByLabel();
		while (map.containsKey(candidate)) {
			candidate = stem + " (" + cnt + ")";
			cnt++;
		}

		return candidate;

	}

        /**
         * Add a given {@link PropertyChangeListener listener} to the
         * list of listeners. The listener is registered for all properties.
         * @param listener The PropertyChangeListener to be added
         */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcSupport.addPropertyChangeListener(listener);
	}

        /**
         * Add a {@link PropertyChangeListener listener} for a specific property.
         * The listener will be invoked only when a call on firePropertyChange
         * names that specific property.
         * @param propertyName The name of the property to listen on.
         * @param listener the PropertyChangeListener to be added
         */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcSupport.addPropertyChangeListener(propertyName, listener);
	}

        /**
         * Remove a {@link PropertyChangeListener listener} from the listener
         * list. This removes the lister that was registered for all properties.
         * @param listener the PropertyChangeListener to be removed
         */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcSupport.removePropertyChangeListener(listener);
	}

        /**
         * Remove a {@link PropertyChangeListener listener} for a specific
         * property.
         * @param propertyName the name of the property that was listened on.
         * @param listener the PropertyChangeListener to be removed
         */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcSupport.removePropertyChangeListener(propertyName, listener);
	}

        /**
         * Adds the listener as a listener of a type
         * {@link SourceMontageListener}.
         * @param l the listener to be added
         */
	public void addSourceMontageListener(SourceMontageListener l) {
		listenerList.add(SourceMontageListener.class, l);
	}

        /**
         * Removes the listener as a listener of a type
         * {@link SourceMontageListener}.
         * @param l the listener to be removed
         */
	public void removeSourceMontageListener(SourceMontageListener l) {
		listenerList.remove(SourceMontageListener.class, l);
	}

        /**
         * Fires an event of adding a {@link SourceChannel channel}.
         * @param source the object on which the Event initially occurred.
         * @param channel an index of an added channel
         */
	protected void fireSourceMontageChannelAdded(Object source, int channel) {
		Object[] listeners = listenerList.getListenerList();
		SourceMontageEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==SourceMontageListener.class) {
				if (e == null) {
					e = new SourceMontageEvent(source, channel);
				}
				((SourceMontageListener)listeners[i+1]).sourceMontageChannelAdded(e);
			}
		}
	}

        /**
         * Fires an event of removing a {@link SourceChannel channel}.
         * @param source the object on which the Event initially occurred.
         * @param channel an index of removed channel
         */
	protected void fireSourceMontageChannelRemoved(Object source, int channel) {
		Object[] listeners = listenerList.getListenerList();
		SourceMontageEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==SourceMontageListener.class) {
				if (e == null) {
					e = new SourceMontageEvent(source, channel);
				}
				((SourceMontageListener)listeners[i+1]).sourceMontageChannelRemoved(e);
			}
		}
	}

        /**
         * Fires an event of changing a {@link SourceChannel channel}.
         * @param source the object on which the Event initially occurred.
         * @param channel an index of a changed channel
         */
	protected void fireSourceMontageChannelChanged(Object source, int channel) {
		Object[] listeners = listenerList.getListenerList();
		SourceMontageEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==SourceMontageListener.class) {
				if (e == null) {
					e = new SourceMontageEvent(source, channel);
				}
				((SourceMontageListener)listeners[i+1]).sourceMontageChannelChanged(e);
			}
		}
	}

	protected void fireSourceMontageEegSystemChanged(Object source) {
		Object[] listeners = listenerList.getListenerList();
		SourceMontageEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==SourceMontageListener.class) {
				if (e == null) {
					e = new SourceMontageEvent(source);
				}
				((SourceMontageListener)listeners[i+1]).sourceMontageEegSystemChanged(e);
			}
		}
	}

	public String getEegSystemName() {
		return eegSystemName;
	}

}

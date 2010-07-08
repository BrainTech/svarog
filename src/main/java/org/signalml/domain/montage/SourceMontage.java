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
import org.signalml.domain.signal.SignalTypeConfigurer;
import org.signalml.exception.SanityCheckException;
import org.signalml.util.Util;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** SourceMontage
 * Class representing a source montage
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("sourcemontage")
public class SourceMontage {

	public static final String CHANGED_PROPERTY = "changed";

	protected static final Logger logger = Logger.getLogger(SourceMontage.class);

        /**
         * SignalType for a SourceMontage
         */
	protected SignalType signalType;


        /**
         * List of SourceChannels in a SourceMontage
         */
	protected ArrayList<SourceChannel> sourceChannels;

        /**
         * SignalTypeConfigurer for a signalType
         */
	private transient SignalTypeConfigurer signalTypeConfigurer;

        /**
         * HashMap associating SourceChannels with their function
         */
	private transient HashMap<Channel,LinkedList<SourceChannel>> sourceChannelsByFunction;

        /**
         * HashMap associating SourceChannels with their labels
         */
	private transient HashMap<String,SourceChannel> sourceChannelsByLabel;

        /**
         * list of EventListeners associated with current object
         */
	protected transient EventListenerList listenerList = new EventListenerList();

        /**
         * PropertyChangeSupport associated with current object
         */
	protected transient PropertyChangeSupport pcSupport = new PropertyChangeSupport(this);

        /**
         * Informs whether SourceMontage has been changed
         */
	private transient boolean changed = false;

        /**
         * Constructor. Creates empty SourceMontage
         */
	protected SourceMontage() {
	}

        /**
         * Constructor. Creates SourceMontage for a given signalType without channels
         * @param signalType type of a signal for which SourceMontage is to be created
         */
	public SourceMontage(SignalType signalType) {
		if (signalType == null) {
			throw new NullPointerException("Signal type null");
		}
		this.signalType = signalType;
		sourceChannels = new ArrayList<SourceChannel>();
	}

        /**
         * Constructor. Creates SourceMontage for a given signalType with channelCount empty channels
         * @param signalType type of a signal
         * @param channelCount number of channels to be created
         * @throws SanityCheckException when addSourceChannel fails because of duplicate labels/functions. Means there is error in code
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
         * Constructor. Creates SourceMontage from a given document with a signal
         * @param document document with a signal
         * @throws SanityCheckException when addSourceChannel fails because of duplicate labels/functions. Means there is error in code
         */
	public SourceMontage(SignalDocument document) {

		this(document.getType());

		MultichannelSampleSource mss = document.getSampleSource();
		int channelCount = mss.getChannelCount();

		String label;
		SignalTypeConfigurer configurer = getSignalTypeConfigurer();
		HashMap<String, SourceChannel> map = getSourceChannelsByLabel();
		Channel function;
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
         * Copy constructor
         * @param montage SourceMontage to be copied
         */
	public SourceMontage(SourceMontage montage) {
		super();
		copyFrom(montage);
	}

        /**
         * copies given SourceMontage parameters to current object. sourceChannels are also copied.
         * Listeners are not copied
         * @param montage SourceMontage which parameters are to be copied
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
	}

        /**
         * Checks if current object has the same number of source channels as signal in given document
         * @param document document with signal to be compared with current object
         * @return true if number of source channels the same, false otherwise
         */
	public boolean isCompatible(SignalDocument document) {
		return(document.getChannelCount() == getSourceChannelCount());
	}

        /**
         * Checks if current object has the same number of source channels as given SourceMontage object
         * @param montage SourceMontage to be compared with current object
         * @return true if number of source channels the same, false otherwise
         */
	public boolean isCompatible(SourceMontage montage) {
		return(montage.getSourceChannelCount() == getSourceChannelCount());
	}

        /**
         * Adapts current object to a given document with a signal. Changes the number of source channels (adding from file or removing excessive) to make it equal with number of channels in document
         * @param document document with a signal to which current object is to be adapted
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
			for (int i=mCnt; i>dCnt; i--) {
				removeSourceChannel();
			}
		}

	}

        /**
         *
         * @return type of a signal for current object
         */
	public SignalType getSignalType() {
		return signalType;
	}

        /**
         * Informs whether SourceMontage has been changed
         * @return true if SourceMontage has been changed, false otherwise
         */
	public boolean isChanged() {
		return changed;
	}


        /**
         * Sets changed parameter
         * @param changed changed parameter to be set
         */
	public void setChanged(boolean changed) {
		if (this.changed != changed) {
			this.changed = changed;
			pcSupport.firePropertyChange(CHANGED_PROPERTY, !changed, changed);
		}
	}

        /**
         *
         * @return SignalTypeConfigurer for a current object
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
         * Returns HashMap associating SourceChannels with their function. If doesn't exists it is created as empty and returned
         * @return HashMap associating SourceChannels with their function.
         */
	protected HashMap<Channel, LinkedList<SourceChannel>> getSourceChannelsByFunction() {
		if (sourceChannelsByFunction == null) {
			sourceChannelsByFunction = new HashMap<Channel, LinkedList<SourceChannel>>();
		}
		return sourceChannelsByFunction;
	}

        /**
         * Returns list of SourceChannels with a given function
         * @param function function thatSourceChannels should fulfil
         * @return list of SourceChannels with a given function
         */
	protected LinkedList<SourceChannel> getSourceChannelsByFunctionList(Channel function) {
		HashMap<Channel, LinkedList<SourceChannel>> map = getSourceChannelsByFunction();
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
         * Returns HashMap associating SourceChannels with their labels. If doesn't exists it is created
         * @return HashMap associating SourceChannels with their labels.
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
         * Returns SourceChannel of a given label
         * @param label label of SourceChannel to be found
         * @return found SourceChannel
         */
	protected SourceChannel getSourceChannelByLabel(String label) {
		return getSourceChannelsByLabel().get(label);
	}

        /**
         *
         * @return number of SourceChannels
         */
	public int getSourceChannelCount() {
		return sourceChannels.size();
	}

        /**
         * Finds label of SourceChannel of a given index
         * @param index index of SourceChannel to be found
         * @return label of SourceChannel of a given index
         */
	public String getSourceChannelLabelAt(int index) {
		return sourceChannels.get(index).getLabel();
	}

        /**
         * Returns function of a SourceChannel of a given index
         * @param index index of SourceChannel to be found
         * @return function of a SourceChannel of a given index
         */
	public Channel getSourceChannelFunctionAt(int index) {
		return sourceChannels.get(index).getFunction();
	}

        /**
         * Finds SourceChannel of a given index and changes its label to a given value
         * @param index index of SourceChannel
         * @param label String with a unique label to be set
         * @return old label of found SourceChannel
         * @throws MontageException thrown when label empty, not unique or containing invalid characters
         */
	public String setSourceChannelLabelAt(int index, String label) throws MontageException {

		if (label == null || label.isEmpty()) {
			throw new MontageException("error.sourceChannelLabelEmpty");
		}
		if (!Util.validateString(label)) {
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
			Channel function = channel.getFunction();
			if (function.getType() == ChannelType.UNKNOWN) {
				// function is unknown, we can update, let's see what we get
				Channel newFunctionCandidate = getSignalTypeConfigurer().channelForName(label);
				if (newFunctionCandidate.getType() != ChannelType.UNKNOWN) {
					// the function is known, check if we could use it
					if (!newFunctionCandidate.isUnique() || getSourceChannelsByFunctionList(newFunctionCandidate).isEmpty()) {
						// we could, do it
						getSourceChannelsByFunctionList(function).remove(channel);
						channel.setFunction(newFunctionCandidate);
						getSourceChannelsByFunctionList(newFunctionCandidate).add(channel);
					}

				}
			}

			fireSourceMontageChannelChanged(this, channel.getChannel());
			setChanged(true);

		}

		return oldLabel;

	}

        /**
         * Finds SourceChannel of a given index and changes its function to a given value
         * @param index index of SourceChannel
         * @param function Channel object with a new function for a SourceChannel
         * @return old function of found SourceChannel
         * @throws MontageException thrown when function is not unique
         */
	public Channel setSourceChannelFunctionAt(int index, Channel function) throws MontageException {

		SourceChannel channel = sourceChannels.get(index);
		Channel oldFunction = channel.getFunction();

		if (oldFunction != function) {

			LinkedList<SourceChannel> list = getSourceChannelsByFunctionList(function);
			if (function.isUnique() && !list.isEmpty()) {
				throw new MontageException("error.sourceChannelFunctionDuplicate");
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
         * Returns array of indexes of SourceChannels with a given function
         * @param function function thatSourceChannels should fulfil
         * @return array of indexes of SourceChannels with a given function
         */
	public int[] getSourceChannelsByFunction(Channel function) {
		LinkedList<SourceChannel> list = getSourceChannelsByFunctionList(function);
		int[] indices = new int[list.size()];
		int i = 0;
		for (SourceChannel channel : list) {
			indices[i] = channel.getChannel();
			i++;
		}
		return indices;
	}

        /**
         * Returns index of first SourceChannels with a given function
         * @param function function thatSourceChannels should fulfil
         * @return index of first SourceChannels with a given function, -1 if doesn't exist
         */
	public int getFirstSourceChannelWithFunction(Channel function) {
		LinkedList<SourceChannel> list = getSourceChannelsByFunctionList(function);
		if (list.isEmpty()) {
			return -1;
		}
		return list.getFirst().getChannel();
	}

        /**
         * Returns index of first SourceChannels with a given label
         * @param label String with a label to be found
         * @return index of first SourceChannels with a given label, -1 if doesn't exist
         */
	public int getSourceChannelForLabel(String label) {
		SourceChannel channel = getSourceChannelsByLabel().get(label);
		if (channel == null) {
			return -1;
		}
		return channel.getChannel();
	}

	/**
         * Adds new SourceChannel with a given label and function
         * @param label unique label for new SourceChannel
         * @param function unique function for new SourceChannel
         * @throws MontageException thrown when label or function not unique
         */
        public void addSourceChannel(String label, Channel function) throws MontageException {

		Channel nonNullChannel = (function != null ? function : getSignalTypeConfigurer().genericChannel());

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
         * Removes last SourceChannel on the sourceChannels list from SourceMontage
         * @return removed SourceChannel
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
		fireSourceMontageChannelAdded(this, index);
		setChanged(true);

		return channel;
	}

        /**
         * Finds unique label for a SourceChannel.
         * @param stem String on which new label is to be based
         * @return unique label for a SourceChannel
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
         * Add a PropertyChangeListener to the listener list. The listener is registered for all properties.
         * @param listener The PropertyChangeListener to be added
         */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcSupport.addPropertyChangeListener(listener);
	}

        /**
         * Add a PropertyChangeListener for a specific property. The listener will be invoked only when a call on firePropertyChange names that specific property
         * @param propertyName The name of the property to listen on.
         * @param listener The PropertyChangeListener to be added
         */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcSupport.addPropertyChangeListener(propertyName, listener);
	}

        /**
         * Remove a PropertyChangeListener from the listener list. This removes a PropertyChangeListener that was registered for all properties.
         * @param listener The PropertyChangeListener to be removed
         */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcSupport.removePropertyChangeListener(listener);
	}

        /**
         * Remove a PropertyChangeListener for a specific property.
         * @param propertyName The name of the property that was listened on.
         * @param listener The PropertyChangeListener to be removed
         */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcSupport.removePropertyChangeListener(propertyName, listener);
	}

        /**
         * Adds the listener as a listener of a type SourceMontageListener
         * @param l the listener to be added
         */
	public void addSourceMontageListener(SourceMontageListener l) {
		listenerList.add(SourceMontageListener.class, l);
	}

        /**
         * Removes the listener as a listener of a type SourceMontageListener
         * @param l the listener to be removed
         */
	public void removeSourceMontageListener(SourceMontageListener l) {
		listenerList.remove(SourceMontageListener.class, l);
	}

        /**
         * Fires event of adding channel
         * @param source The object on which the Event initially occurred.
         * @param channel index of channel added
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
         * Fires event of removing channel
         * @param source The object on which the Event initially occurred.
         * @param channel index of channel removed
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
         * Fires event of changing channel
         * @param source The object on which the Event initially occurred.
         * @param channel index of channel changed
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

}

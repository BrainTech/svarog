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
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("sourcemontage")
public class SourceMontage {

	public static final String CHANGED_PROPERTY = "changed";

	protected static final Logger logger = Logger.getLogger(SourceMontage.class);

	protected SignalType signalType;
	protected ArrayList<SourceChannel> sourceChannels;

	private transient SignalTypeConfigurer signalTypeConfigurer;

	private transient HashMap<Channel,LinkedList<SourceChannel>> sourceChannelsByFunction;
	private transient HashMap<String,SourceChannel> sourceChannelsByLabel;

	protected transient EventListenerList listenerList = new EventListenerList();
	protected transient PropertyChangeSupport pcSupport = new PropertyChangeSupport(this);

	private transient boolean changed = false;

	protected SourceMontage() {
	}

	public SourceMontage(SignalType signalType) {
		if (signalType == null) {
			throw new NullPointerException("Signal type null");
		}
		this.signalType = signalType;
		sourceChannels = new ArrayList<SourceChannel>();
	}

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

	public SourceMontage(SourceMontage montage) {
		super();
		copyFrom(montage);
	}

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
		// listeners are not copied
	}

	public boolean isCompatible(SignalDocument document) {
		return(document.getChannelCount() == getSourceChannelCount());
	}

	public boolean isCompatible(SourceMontage montage) {
		return(montage.getSourceChannelCount() == getSourceChannelCount());
	}

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

	public SignalType getSignalType() {
		return signalType;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		if (this.changed != changed) {
			this.changed = changed;
			pcSupport.firePropertyChange(CHANGED_PROPERTY, !changed, changed);
		}
	}

	public SignalTypeConfigurer getSignalTypeConfigurer() {
		if (signalTypeConfigurer == null) {
			signalTypeConfigurer = signalType.getConfigurer();
			if (signalTypeConfigurer == null) {
				throw new NullPointerException("Configurer null");
			}
		}
		return signalTypeConfigurer;
	}

	protected HashMap<Channel, LinkedList<SourceChannel>> getSourceChannelsByFunction() {
		if (sourceChannelsByFunction == null) {
			sourceChannelsByFunction = new HashMap<Channel, LinkedList<SourceChannel>>();
		}
		return sourceChannelsByFunction;
	}

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

	protected HashMap<String,SourceChannel> getSourceChannelsByLabel() {
		if (sourceChannelsByLabel == null) {
			sourceChannelsByLabel = new HashMap<String, SourceChannel>();
			for (SourceChannel channel : sourceChannels) {
				sourceChannelsByLabel.put(channel.getLabel(), channel);
			}
		}
		return sourceChannelsByLabel;
	}

	protected SourceChannel getSourceChannelByLabel(String label) {
		return getSourceChannelsByLabel().get(label);
	}

	public int getSourceChannelCount() {
		return sourceChannels.size();
	}

	public String getSourceChannelLabelAt(int index) {
		return sourceChannels.get(index).getLabel();
	}

	public Channel getSourceChannelFunctionAt(int index) {
		return sourceChannels.get(index).getFunction();
	}

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

	public int getFirstSourceChannelWithFunction(Channel function) {
		LinkedList<SourceChannel> list = getSourceChannelsByFunctionList(function);
		if (list.isEmpty()) {
			return -1;
		}
		return list.getFirst().getChannel();
	}

	public int getSourceChannelForLabel(String label) {
		SourceChannel channel = getSourceChannelsByLabel().get(label);
		if (channel == null) {
			return -1;
		}
		return channel.getChannel();
	}

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

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcSupport.removePropertyChangeListener(propertyName, listener);
	}

	public void addSourceMontageListener(SourceMontageListener l) {
		listenerList.add(SourceMontageListener.class, l);
	}

	public void removeSourceMontageListener(SourceMontageListener l) {
		listenerList.remove(SourceMontageListener.class, l);
	}

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

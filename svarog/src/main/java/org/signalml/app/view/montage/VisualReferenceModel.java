/* VisualReferenceModel.java created 2007-11-30
 *
 */

package org.signalml.app.view.montage;

import static org.signalml.app.SvarogI18n._;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.event.EventListenerList;

import org.signalml.domain.montage.Channel;
import org.signalml.domain.montage.ChannelType;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageEvent;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.MontageListener;
import org.signalml.domain.montage.SourceMontageEvent;
import org.signalml.domain.montage.SourceMontageListener;
import org.signalml.domain.signal.SignalTypeConfigurer;
import org.signalml.exception.SanityCheckException;
import org.signalml.util.Util;

/** VisualReferenceModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class VisualReferenceModel implements SourceMontageListener, MontageListener {

	public static final String BIPOLAR_MODE_PROPERTY = "bipolarMode";
	public static final String BIPOLAR_COMPATIBLE_PROPERTY = "bipolarCompatible";
	public static final String ACTIVE_CHANNEL_PROPERTY = "activeChannel";
	public static final String ACTIVE_ARROW_PROPERTY = "activeArrow";

	private Montage montage;

	private transient PropertyChangeSupport pcSupport;
	private transient EventListenerList listenerList = new EventListenerList();

	private boolean bipolarMode;
	private boolean bipolarCompatible;
	private VisualReferenceArrow activeArrow;
	private VisualReferenceChannel activeChannel;

	private VisualReferenceChessboardBin othersBin;
	private VisualReferenceChessboardBin primariesBin;
	private VisualReferenceChessboardBin referencesBin;
	private VisualReferencePositionedBin positionedBin;

	private ArrayList<VisualReferenceSourceChannel> sourceChannels;
	private ArrayList<VisualReferenceChannel> channels;
	private ArrayList<LinkedList<VisualReferenceChannel>> channelLists;
	private ArrayList<VisualReferenceArrow> arrows;

	public VisualReferenceModel() {
		pcSupport = new PropertyChangeSupport(this);
		readAllFromMontage();
	}

	public Montage getMontage() {
		return montage;
	}

	public void setMontage(Montage montage) {
		if (this.montage != montage) {
			if (this.montage != null) {
				this.montage.removeSourceMontageListener(this);
				this.montage.removeMontageListener(this);
			}
			this.montage = montage;
			if (montage != null) {
				montage.addSourceMontageListener(this);
				montage.addMontageListener(this);
				setBipolarCompatible(montage.isBipolar());
			} else {
				setBipolarCompatible(false);
			}
			readAllFromMontage();
		}
	}

	private void readAllFromMontage() {

		// TODO consider making this lazy

		sourceChannels = new ArrayList<VisualReferenceSourceChannel>();
		channels = new ArrayList<VisualReferenceChannel>();
		channelLists = new ArrayList<LinkedList<VisualReferenceChannel>>();
		arrows = new ArrayList<VisualReferenceArrow>();
		othersBin = new VisualReferenceChessboardBin();
		primariesBin = new VisualReferenceChessboardBin();
		referencesBin = new VisualReferenceChessboardBin();

		if (montage != null) {

			SignalTypeConfigurer configurer = montage.getSignalTypeConfigurer();
			positionedBin = new VisualReferencePositionedBin(configurer.getMatrixWidth(), configurer.getMatrixHeight());

			int cnt = montage.getSourceChannelCount();
			int i;
			int primary;
			VisualReferenceSourceChannel sourceChannel;
			VisualReferenceChannel channel;
			sourceChannels.ensureCapacity(cnt);
			channelLists.ensureCapacity(cnt);
			Channel function;
			ChannelType functionType;

			for (i=0; i<cnt; i++) {
				sourceChannel = new VisualReferenceSourceChannel(i);
				sourceChannel.setLabel(montage.getSourceChannelLabelAt(i));
				sourceChannel.setFunction(montage.getSourceChannelFunctionAt(i));

				sourceChannels.add(sourceChannel);
				channelLists.add(new LinkedList<VisualReferenceChannel>());
				function = montage.getSourceChannelFunctionAt(i);
				if (function.getMatrixCol() >= 0 && function.getMatrixRow() >= 0) {
					// the channel is positioned
					positionedBin.add(sourceChannel);
				} else {
					functionType = function.getType();
					switch (functionType) {

					case PRIMARY :
					case UNKNOWN : // unknowns are treated as assorted primaries
						primariesBin.add(sourceChannel);
						break;

					case REFERENCE :
						referencesBin.add(sourceChannel);
						break;

					default : // the rest
						othersBin.add(sourceChannel);
						break;

					}
				}
			}

			cnt = montage.getMontageChannelCount();
			channels.ensureCapacity(cnt);
			for (i=0; i<cnt; i++) {

				primary = montage.getMontagePrimaryChannelAt(i);
				channel = new VisualReferenceChannel(primary);
				channel.setLabel(montage.getMontageChannelLabelAt(i));
				channels.add(channel);
				channelLists.get(primary).add(channel);

			}

			readArrowsFromMontage();

		} else {
			positionedBin = new VisualReferencePositionedBin(0,0);
		}

		othersBin.setName(_("Others"));
		referencesBin.setName(_("Refs"));
		primariesBin.setName(_("Prims"));
		positionedBin.setName(_("Positioned"));

		setActiveChannel(null);
		fireMontageStructureChanged(this);

	}

	private void readArrowsFromMontage() {

		arrows.clear();

		int cnt = montage.getMontageChannelCount();
		String[] refs;
		int i, e;
		int primary;
		VisualReferenceArrow arrow;
		for (i=0; i<cnt; i++) {
			primary = montage.getMontagePrimaryChannelAt(i);

			refs = montage.getReference(i);
			for (e=0; e<refs.length; e++) {
				if (e != primary && refs[e] != null) {
					arrow = new VisualReferenceArrow(e, i);
					arrows.add(arrow);
				}
			}


		}

	}

	// montageIndex is a source index in this mode
	private void addBipolarModeReference(int montageIndex, int sourceIndex) {

		int[] current = montage.getMontageChannelsForSourceChannel(montageIndex);
		int i;
		int refCandidate = -1;
		for (i=0; i<current.length; i++) {
			if (montage.hasReference(current[i], sourceIndex)) {
				// this bipolar channel already exist
				return;
			}
			if (refCandidate < 0 && !montage.hasReference(current[i])) {
				refCandidate = current[i];
			}

		}

		String label = montage.getSourceChannelLabelAt(montageIndex) + "-" + montage.getSourceChannelLabelAt(sourceIndex);
		if (refCandidate >= 0) {
			montage.setReference(refCandidate, sourceIndex, "-1");
			try {
				montage.setMontageChannelLabelAt(refCandidate, montage.getNewMontageChannelLabel(label));
			} catch (MontageException ex) {
				// this should not happen, label was generated
				throw new SanityCheckException(ex);
			}
		} else {
			montage.addBipolarMontageChannel(montageIndex, label, sourceIndex);
		}

	}

	private void addRegularModeReference(int montageIndex, int sourceIndex) {

		int primary = montage.getMontagePrimaryChannelAt(montageIndex);
		if (primary == sourceIndex) {
			return;
		}

		String[] refs = montage.getReference(montageIndex);
		int i;
		int cnt = 0;
		for (i=0; i<refs.length; i++) {
			if (i == primary || i == sourceIndex) {  // neither primary nor current are counted
				continue;
			}
			if (refs[i] != null) {
				cnt++;
			}
		}
		cnt++; // count in currently added

		String token;
		if (cnt == 1) {
			token = "-1";
		} else {
			token = "-1/" + cnt;
		}

		boolean changed = false;

		for (i=0; i<refs.length; i++) {
			if (i == primary) {
				continue;
			}
			if (i == sourceIndex || refs[i] != null) {
				if (!changed && (refs[i] == null || !refs[i].equals(token))) {
					changed = true;
				}
				refs[i] = token;
			}
		}

		if (changed) {
			montage.setReference(montageIndex, refs);
		}

	}

	public void addReference(int montageIndex, int sourceIndex) {

		if (bipolarMode) {

			// bipolar mode reaction
			addBipolarModeReference(montageIndex, sourceIndex);

		} else {

			// normal mode reaction
			addRegularModeReference(montageIndex, sourceIndex);

		}

	}

	// montageIndex is a montage index in this mode
	private void removeBipolarModeReference(int montageIndex, int sourceIndex) {

		/*
		int[] current = montage.getMontageChannelsForSourceChannel( montageIndex );
		int i;
		for( i=0; i<current.length; i++ ) {
			if( montage.hasReference( current[i], sourceIndex ) ) {
				montage.removeMontageChannel( current[i] );
				return;
			}
		}
		*/

		montage.removeMontageChannel(montageIndex);

	}

	private void removeRegularModeReference(int montageIndex, int sourceIndex) {

		int primary = montage.getMontagePrimaryChannelAt(montageIndex);
		if (primary == sourceIndex) {
			return;
		}

		String[] refs = montage.getReference(montageIndex);
		int i;
		int cnt = 0;
		for (i=0; i<refs.length; i++) {
			if (i == primary || i == sourceIndex) {  // neither primary nor current are counted
				continue;
			}
			if (refs[i] != null) {
				cnt++;
			}
		}

		String token;
		if (cnt == 0) {
			token = null;
		}
		else if (cnt == 1) {
			token = "-1";
		} else {
			token = "-1/" + cnt;
		}

		boolean changed = false;

		for (i=0; i<refs.length; i++) {
			if (i == primary) {
				continue;
			}
			if (i == sourceIndex) {
				if (!changed && (refs[i] != null)) {
					changed = true;
				}
				refs[i] = null;
			}
			if (refs[i] != null) {
				if (!changed && !Util.equalsWithNulls(refs[i], token)) {
					changed = true;
				}
				refs[i] = token;
			}
		}

		if (changed) {
			montage.setReference(montageIndex, refs);
		}

	}

	public void removeReference(int montageIndex, int sourceIndex) {

		if (bipolarMode) {

			// bipolar mode reaction
			removeBipolarModeReference(montageIndex, sourceIndex);

		} else {

			// normal mode reaction
			removeRegularModeReference(montageIndex, sourceIndex);

		}

	}

	public VisualReferenceBin getOthersBin() {
		return othersBin;
	}

	public VisualReferenceBin getPrimariesBin() {
		return primariesBin;
	}

	public VisualReferenceBin getReferencesBin() {
		return referencesBin;
	}

	public VisualReferencePositionedBin getPositionedBin() {
		return positionedBin;
	}

	public VisualReferenceSourceChannel getSourceChannel(int index) {
		return sourceChannels.get(index);
	}

	public int indexOfSourceChannel(VisualReferenceSourceChannel o) {
		return sourceChannels.indexOf(o);
	}

	public boolean isSourceChannelsEmpty() {
		return sourceChannels.isEmpty();
	}

	public Iterator<VisualReferenceSourceChannel> sourceChannelIterator() {
		return sourceChannels.iterator();
	}

	public int sourceChannelsSize() {
		return sourceChannels.size();
	}

	public VisualReferenceChannel getChannel(int index) {
		return channels.get(index);
	}

	public int indexOfChannel(VisualReferenceChannel channel) {
		return channels.indexOf(channel);
	}

	public boolean isChannelsEmpty() {
		return channels.isEmpty();
	}

	public Iterator<VisualReferenceChannel> channelsIterator() {
		return channels.iterator();
	}

	public int channelsSize() {
		return channels.size();
	}

	public VisualReferenceChannel getChannelPerPrimary(int sourceIndex, int index) {
		return channelLists.get(sourceIndex).get(index);
	}

	public int indexOfChannelPerPrimary(int sourceIndex, VisualReferenceChannel channel) {
		return channelLists.get(sourceIndex).indexOf(channel);
	}

	public boolean isChannelsPerPrimaryEmpty(int sourceIndex) {
		return channelLists.get(sourceIndex).isEmpty();
	}

	public Iterator<VisualReferenceChannel> channelsPerPrimaryIterator(int sourceIndex) {
		return channelLists.get(sourceIndex).iterator();
	}

	public int channelsPerPrimarySize(int sourceIndex) {
		return channelLists.get(sourceIndex).size();
	}

	public VisualReferenceArrow getArrow(int index) {
		return arrows.get(index);
	}

	public int indexOfArrow(VisualReferenceArrow o) {
		return arrows.indexOf(o);
	}

	public boolean isArrowsEmpty() {
		return arrows.isEmpty();
	}

	public Iterator<VisualReferenceArrow> arrowsIterator() {
		return arrows.iterator();
	}

	public int arrowsSize() {
		return arrows.size();
	}

	public boolean isBipolarMode() {
		return bipolarMode;
	}

	public void setBipolarMode(boolean bipolarMode) {
		boolean newBipolarMode = bipolarMode && isBipolarCompatible();
		if (this.bipolarMode != newBipolarMode) {
			this.bipolarMode = newBipolarMode;
			pcSupport.firePropertyChange(BIPOLAR_MODE_PROPERTY, !newBipolarMode, newBipolarMode);
		}
	}

	public boolean isBipolarCompatible() {
		return bipolarCompatible;
	}

	public void setBipolarCompatible(boolean bipolarCompatible) {
		if (this.bipolarCompatible != bipolarCompatible) {
			this.bipolarCompatible = bipolarCompatible;
			if (!bipolarCompatible) {
				setBipolarMode(false);
			}
			pcSupport.firePropertyChange(BIPOLAR_COMPATIBLE_PROPERTY, !bipolarCompatible, bipolarCompatible);
		}
	}

	public int getArrowOrder(int target, int source) {

		int primary = montage.getMontagePrimaryChannelAt(target);
		int[] channels = montage.getMontageChannelsForSourceChannel(primary);
		int order = 0;

		for (int channel : channels) {
			if (channel == target) {
				break;
			}
			if (montage.hasReference(channel, source)) {
				order++;
			}
		}

		return order;

	}

	public VisualReferenceArrow getActiveArrow() {
		return activeArrow;
	}

	public void setActiveArrow(VisualReferenceArrow activeArrow) {
		if (this.activeArrow != activeArrow) {
			VisualReferenceArrow oldArrow = this.activeArrow;
			if (activeArrow != null) {
				selectChannelAt(activeArrow.getTargetChannel());
			}
			this.activeArrow = activeArrow;
			pcSupport.firePropertyChange(ACTIVE_ARROW_PROPERTY, oldArrow, activeArrow);
		}
	}

	public VisualReferenceChannel getActiveChannel() {
		return activeChannel;
	}

	public void setActiveChannel(VisualReferenceChannel activeChannel) {
		if (this.activeChannel != activeChannel) {
			VisualReferenceChannel oldChannel = this.activeChannel;
			setActiveArrow(null);
			this.activeChannel = activeChannel;
			pcSupport.firePropertyChange(ACTIVE_CHANNEL_PROPERTY, oldChannel, activeChannel);
		}
	}

	public void selectNextChannel() {
		if (channels.isEmpty()) {
			return;
		}
		if (activeChannel == null) {
			setActiveChannel(channels.get(0));
		} else {
			int index = channels.indexOf(activeChannel);
			index = (index + 1) % channels.size();
			setActiveChannel(channels.get(index));
		}
	}

	public void selectPreviousChannel() {
		if (channels.isEmpty()) {
			return;
		}
		if (activeChannel == null) {
			setActiveChannel(channels.get(channels.size()-1));
		} else {
			int index = channels.indexOf(activeChannel);
			index--;
			if (index < 0) {
				index = channels.size() - 1;
			}
			setActiveChannel(channels.get(index));
		}
	}

	public void selectChannelAt(int index) {
		setActiveChannel(channels.get(index));
	}

	@Override
	public void montageChannelsAdded(MontageEvent ev) {
		readAllFromMontage();
		setBipolarCompatible(montage.isBipolar());
		fireMontageChannelsChanged(this, ev);
	}

	@Override
	public void montageChannelsChanged(MontageEvent ev) {
		int index = ev.getChannel();
		channels.get(index).setLabel(montage.getMontageChannelLabelAt(index));
		fireMontageChannelsChanged(this, ev);
	}

	@Override
	public void montageChannelsRemoved(MontageEvent ev) {
		readAllFromMontage();
		setBipolarCompatible(montage.isBipolar());
		fireMontageChannelsChanged(this, ev);
	}

	@Override
	public void montageReferenceChanged(MontageEvent ev) {
		readArrowsFromMontage();
		setBipolarCompatible(montage.isBipolar());
		fireReferenceChanged(this, ev);
	}

	@Override
	public void montageStructureChanged(MontageEvent ev) {
		readAllFromMontage();
		setBipolarCompatible(montage.isBipolar());
		fireMontageStructureChanged(this);
	}

	@Override
	public void sourceMontageChannelAdded(SourceMontageEvent ev) {
		readAllFromMontage();
		setBipolarCompatible(montage.isBipolar());
		fireSourceChannelsChanged(this, ev);
	}

	@Override
	public void sourceMontageChannelChanged(SourceMontageEvent ev) {
		readAllFromMontage();
		setBipolarCompatible(montage.isBipolar());
		fireSourceChannelsChanged(this, ev);
	}

	@Override
	public void sourceMontageChannelRemoved(SourceMontageEvent ev) {
		readAllFromMontage();
		setBipolarCompatible(montage.isBipolar());
		fireSourceChannelsChanged(this, ev);
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

	public void addVisualReferenceListener(VisualReferenceListener l) {
		listenerList.add(VisualReferenceListener.class, l);
	}

	public void removeVisualReferenceListener(VisualReferenceListener l) {
		listenerList.remove(VisualReferenceListener.class, l);
	}

	protected void fireSourceChannelsChanged(Object source, SourceMontageEvent sourceMontageEvent) {
		Object[] listeners = listenerList.getListenerList();
		VisualReferenceEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==VisualReferenceListener.class) {
				if (e == null) {
					e = new VisualReferenceEvent(source, sourceMontageEvent);
				}
				((VisualReferenceListener)listeners[i+1]).sourceChannelsChanged(e);
			}
		}
	}

	protected void fireMontageChannelsChanged(Object source, MontageEvent montageEvent) {
		Object[] listeners = listenerList.getListenerList();
		VisualReferenceEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==VisualReferenceListener.class) {
				if (e == null) {
					e = new VisualReferenceEvent(source, montageEvent);
				}
				((VisualReferenceListener)listeners[i+1]).montageChannelsChanged(e);
			}
		}
	}

	protected void fireReferenceChanged(Object source, MontageEvent montageEvent) {
		Object[] listeners = listenerList.getListenerList();
		VisualReferenceEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==VisualReferenceListener.class) {
				if (e == null) {
					e = new VisualReferenceEvent(source, montageEvent);
				}
				((VisualReferenceListener)listeners[i+1]).referenceChanged(e);
			}
		}
	}

	protected void fireMontageStructureChanged(Object source) {
		Object[] listeners = listenerList.getListenerList();
		VisualReferenceEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==VisualReferenceListener.class) {
				if (e == null) {
					e = new VisualReferenceEvent(source);
				}
				((VisualReferenceListener)listeners[i+1]).montageStructureChanged(e);
			}
		}
	}

}

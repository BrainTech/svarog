/* AbstractSignalSourcePanel.java created 2011-03-06
 *
 */
package org.signalml.app.view.opensignal;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.ComboBoxModel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.signalml.app.config.preset.EegSystemsPresetManager;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.montage.EegSystemSelectionPanel;
import org.signalml.domain.montage.system.EegSystem;

/**
 * A panel used to set parameters for an abstract signal source.
 *
 * @author Piotr Szachewicz
 */
abstract public class AbstractSignalSourcePanel extends JPanel implements PropertyChangeListener {

	/**
	 * PropertyChangeSupport to fire property changes when needed.
	 */
	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * ViewerElementManager used by this panel.
	 */
        protected ViewerElementManager viewerElementManager;

	/**
	 * A panel for selecting which type of signal source is chosen (file/openBCI/
	 * amplifier).
	 */
        private SignalSourceSelectionPanel signalSourceSelectionPanel;
	/**
	 * The panel for selecting the currently used EEG system.
	 */
	protected EegSystemSelectionPanel eegSystemSelectionPanel;
	/**
	 * A preset manager which manages the available {@link EegSystem EEG systems}.
	 */
	private EegSystemsPresetManager eegSystemsPresetManager;

	/**
	 * Constructor.
	 * @param viewerElementManager ViewerElementManager used by this panel
	 */
        public AbstractSignalSourcePanel(ViewerElementManager viewerElementManager) {
                this.viewerElementManager = viewerElementManager;
		this.eegSystemsPresetManager = viewerElementManager.getEegSystemsPresetManager();
                createInterface();
        }

	/**
	 * Returns the ViewerElementManager used by this panel.
	 * @return the ViewerElementManager used by this panel
	 */
        public ViewerElementManager getViewerElementManager() {
                return viewerElementManager;
        }

	/**
	 * Creates the GUI components for this panel.
	 */
        protected final void createInterface() {
                this.setLayout(new GridLayout(1, 2, 5, 0));
                this.setBorder(new EmptyBorder(5, 5, 5, 5));

                JPanel absoluteLeftColumnPanel = new JPanel(new BorderLayout());
                absoluteLeftColumnPanel.add(getSignalSourceSelectionPanel(), BorderLayout.NORTH);
                absoluteLeftColumnPanel.add(createLeftColumnPanel(), BorderLayout.CENTER);

                this.add(absoluteLeftColumnPanel);
                this.add(createRightColumnPanel());
        }

	/**
	 * This panel is divided into two columns. This method should return
	 * the panel containing the components which should be shown in the
	 * left column.
	 * @return a panel containing the left column
	 */
        abstract protected JPanel createLeftColumnPanel();

	/**
	 * This panel is divided into two columns. This method should return
	 * the panel containing the components which should be shown in the
	 * right column.
	 * @return the panel containing the right column
	 */
        abstract protected JPanel createRightColumnPanel();

	/**
	 * Returns the panel for selecting the signal source.
	 * @return the panel for selecting the signal source
	 */
	public SignalSourceSelectionPanel getSignalSourceSelectionPanel() {
		if (signalSourceSelectionPanel == null) {
			signalSourceSelectionPanel = new SignalSourceSelectionPanel();
			signalSourceSelectionPanel.addPropertyChangeListener(this);
		}
		return signalSourceSelectionPanel;
	}

	/**
	 * Sets the model to be used for the signal source selection combo box.
	 * @param model the model to be used for the signal source selection
	 * combo box
	 */
	public void setSignalSourceSelectionComboBoxModel(ComboBoxModel model) {
		getSignalSourceSelectionPanel().setSelectionComboBoxModel(model);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		forwardPropertyChange(evt);
	}

	/**
	 * Sends the received {@link PropertyChangeEvent} changing only
	 * the {@link PropertyChangeEvent#source} of the property to this
	 * panel.
	 * @param evt the event to be forwarded
	 */
	protected void forwardPropertyChange(PropertyChangeEvent evt) {
		PropertyChangeEvent newEvent = new PropertyChangeEvent(this, evt.getPropertyName(), null, evt.getNewValue());
		propertyChangeSupport.firePropertyChange(newEvent);
	}

	/**
	 * Fires the property telling that the number of channels have changed.
	 * @param newNumberOfChannels the new number of channels.
	 */
	protected void fireNumberOfChannelsChangedProperty(int newNumberOfChannels) {
		propertyChangeSupport.firePropertyChange(AbstractSignalParametersPanel.NUMBER_OF_CHANNELS_PROPERTY, null, newNumberOfChannels);
	}

	/**
	 * Returns whether signal source metadata is filled (sampling frequency, etc.).
	 * @return true if metadata is filled, false otherwise
	 */
	abstract public boolean isMetadataFilled();

	/**
	 * Returns the channel count of the signal source configured using this
	 * panel.
	 * @return the channel count of the signal source configured using this
	 * panel
	 */
	abstract public int getChannelCount();

	/**
	 * Returns the sampling frequency of the signal source configured using this
	 * panel.
	 * @return the sampling frequency of the signal source configured using this
	 * panel
	 */
	abstract public float getSamplingFrequency();

	/**
	 * Sets the sampling frequency for this signal source.
	 * @param samplingFrequency the sampling frequency to be set
	 */
	abstract public void setSamplingFrequency(float samplingFrequency);

	/**
	 * Returns the panel for selecting the currently used {@link EegSystem
	 * EEG system}.
	 * @return the EEG system selection panel
	 */
	protected EegSystemSelectionPanel getEegSystemSelectionPanel() {
		if (eegSystemSelectionPanel == null) {
			eegSystemSelectionPanel = new EegSystemSelectionPanel(eegSystemsPresetManager, this);
		}
		return eegSystemSelectionPanel;
	}

}

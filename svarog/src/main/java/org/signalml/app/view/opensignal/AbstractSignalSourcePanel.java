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
import org.signalml.app.view.ViewerElementManager;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * A panel used to set parameters for an abstract signal source.
 *
 * @author Piotr Szachewicz
 */
abstract public class AbstractSignalSourcePanel extends JPanel implements PropertyChangeListener {

	/**
	 * Message source capable of resolving localized messages.
	 */
        protected MessageSourceAccessor messageSource;

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
	 * Constructor.
	 * @param messageSource message source capable of resolving localized messages
	 * @param viewerElementManager ViewerElementManager used by this panel
	 */
        public AbstractSignalSourcePanel(MessageSourceAccessor messageSource, ViewerElementManager viewerElementManager) {
                this.messageSource = messageSource;
                this.viewerElementManager = viewerElementManager;
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
			signalSourceSelectionPanel = new SignalSourceSelectionPanel(messageSource);
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

}
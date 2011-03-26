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
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
abstract public class AbstractSignalSourcePanel extends JPanel implements PropertyChangeListener {

        protected MessageSourceAccessor messageSource;
        protected ViewerElementManager viewerElementManager;
        protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
        private SignalSourceSelectionPanel signalSourceSelectionPanel;

        public AbstractSignalSourcePanel(MessageSourceAccessor messageSource, ViewerElementManager viewerElementManager) {
                this.messageSource = messageSource;
                this.viewerElementManager = viewerElementManager;
                createInterface();
        }

        public ViewerElementManager getViewerElementManager() {
                return viewerElementManager;
        }

        protected final void createInterface() {
                this.setLayout(new GridLayout(1, 2, 5, 0));
                this.setBorder(new EmptyBorder(5, 5, 5, 5));

                JPanel absoluteLeftColumnPanel = new JPanel(new BorderLayout());
                absoluteLeftColumnPanel.add(getSignalSourceSelectionPanel(), BorderLayout.NORTH);
                absoluteLeftColumnPanel.add(createLeftColumnPanel(), BorderLayout.CENTER);

                this.add(absoluteLeftColumnPanel);
                this.add(createRightColumnPanel());
        }

        abstract protected JPanel createLeftColumnPanel();

        abstract protected JPanel createRightColumnPanel();       

	public SignalSourceSelectionPanel getSignalSourceSelectionPanel() {
		if (signalSourceSelectionPanel == null) {
			signalSourceSelectionPanel = new SignalSourceSelectionPanel(messageSource);
			signalSourceSelectionPanel.addPropertyChangeListener(this);
		}
		return signalSourceSelectionPanel;
	}

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

	protected void fireNumberOfChannelsChangedProperty(int newNumberOfChannels) {
		propertyChangeSupport.firePropertyChange(AbstractSignalParametersPanel.NUMBER_OF_CHANNELS_PROPERTY, null, newNumberOfChannels);
	}

	abstract public boolean isMetadataFilled();
	abstract public int getChannelCount();
	abstract public float getSamplingFrequency();

}
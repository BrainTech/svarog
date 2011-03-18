/* AbstractMonitorSourcePaenl.java created 2011-03-18
 *
 */

package org.signalml.app.view.opensignal;

import org.signalml.app.view.ViewerElementManager;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
abstract public class AbstractMonitorSourcePanel extends AbstractSignalSourcePanel {

	public static String OPENBCI_CONNECTED_PROPERTY = "openBCIConnectedProperty";

	protected boolean isConnected = false;

	public AbstractMonitorSourcePanel(MessageSourceAccessor messageSource, ViewerElementManager viewerElementManager) {
		super(messageSource, viewerElementManager);
	}

	public void setConnected(boolean filled) {
		isConnected = filled;
		if (isConnected)
			fireOpenBCIConnected();
		else
			fireOpenBCIDisconnected();
		getSignalSourceSelectionPanel().setEnabled(!filled);
	}

	@Override
	public boolean isMetadataFilled() {
		return isConnected;
	}

	protected void fireOpenBCIConnected() {
		propertyChangeSupport.firePropertyChange(OPENBCI_CONNECTED_PROPERTY, false, true);
	}

	protected void fireOpenBCIDisconnected() {
		propertyChangeSupport.firePropertyChange(OPENBCI_CONNECTED_PROPERTY, true, false);
	}

}

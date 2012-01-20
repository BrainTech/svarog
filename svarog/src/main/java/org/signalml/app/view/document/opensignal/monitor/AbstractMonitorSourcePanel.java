/* AbstractMonitorSourcePaenl.java created 2011-03-18
 *
 */

package org.signalml.app.view.document.opensignal.monitor;

import org.signalml.app.config.preset.StyledTagSetPresetManager;
import org.signalml.app.view.document.opensignal.AbstractSignalSourcePanel;
import org.signalml.app.view.workspace.ViewerElementManager;

/**
 * Represents a panel for setting parameters of a monitor signal source.
 *
 * @author Piotr Szachewicz
 */
abstract public class AbstractMonitorSourcePanel extends AbstractSignalSourcePanel {

	/**
	 * Property telling whether Svarog is connected to openBCI.
	 */
	public static String OPENBCI_CONNECTED_PROPERTY = "openBCIConnectedProperty";

	/**
	 * True if Svarog is connected to openBCI, false otherwise.
	 */
	protected boolean isConnected = false;

	/**
	 * A panel for selecting a tag style preset to be used by the monitor to
	 * be opened.
	 */
	protected TagPresetSelectionPanel tagPresetSelectionPanel;

	/**
	 * Constructor.
	 * @param viewerElementManager ViewerElementManager used by this panel
	 */
	public AbstractMonitorSourcePanel(ViewerElementManager viewerElementManager) {
		super(viewerElementManager);
	}

	/**
	 * Changes this panel openBCI connection state and fires appropriate
	 * properties changes.
	 * @param connected the connection state to be set
	 */
	public void setConnected(boolean connected) {
		isConnected = connected;
		if (isConnected)
			fireOpenBCIConnected();
		else
			fireOpenBCIDisconnected();
		getSignalSourceSelectionPanel().setEnabled(!connected);
	}

	@Override
	public boolean isMetadataFilled() {
		return isConnected;
	}

	/**
	 * Fires the OPENBCI_CONNECTED_PROPERTY change (to connected state).
	 */
	protected void fireOpenBCIConnected() {
		propertyChangeSupport.firePropertyChange(OPENBCI_CONNECTED_PROPERTY, false, true);
	}

	/**
	 * Fires the OPENBCI_CONNECTED_PROPERTY change (to disconnected state).
	 */
	protected void fireOpenBCIDisconnected() {
		propertyChangeSupport.firePropertyChange(OPENBCI_CONNECTED_PROPERTY, true, false);
	}

	/**
	 * Returns the panel for selecting tag style preset to be used by the
	 * monitor to be opened.
	 * @return panel for selecting tag style preset
	 */
	public TagPresetSelectionPanel getTagPresetSelectionPanel() {
		if (tagPresetSelectionPanel == null) {
			tagPresetSelectionPanel = new TagPresetSelectionPanel( viewerElementManager.getStyledTagSetPresetManager());
		}
		return tagPresetSelectionPanel;
	}

}

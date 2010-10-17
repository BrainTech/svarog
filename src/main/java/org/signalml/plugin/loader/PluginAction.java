/**
 * 
 */
package org.signalml.plugin.loader;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;

/**
 * A button used to create a
 * {@link PluginDialog plug-in options dialog}.
 * @author Marcin Szumski
 */
public class PluginAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	/**
	 * the dialog that will be created after clicking this
	 * button
	 */
	private PluginDialog pluginDialog;
	/**
	 * an array of plug-in {@link PluginState states}
	 */
	private ArrayList<PluginState> descriptions;
	
	/**
	 * Constructor. 
	 * @param descriptions an array of plug-in {@link PluginState states}
	 */
	public PluginAction(ArrayList<PluginState> descriptions){
		super("Plugins options");
		this.descriptions = descriptions;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		pluginDialog.showDialog(descriptions, true);
	}
	/**
	 * @param pluginDialog the pluginDialog to set
	 */
	public void setPluginDialog(PluginDialog pluginDialog) {
		this.pluginDialog = pluginDialog;
	}
}

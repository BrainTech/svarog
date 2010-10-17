/**
 * 
 */
package org.signalml.plugin.loader;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * This panel allows to manage plug-in options.
 * Contains two sub-panels:
 * <ul>
 * <li>first to select folders in which plug-ins are stored</li>
 * <li>second to select which plug-ins should be active</li>
 * </ul>
 * @author Marcin Szumski
 */
public class PluginPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * the panel containing the table with information
	 * which plug-ins are active
	 * @see PluginListPanel 
	 */
	private PluginListPanel pluginListPanel;
	/**
	 * the panel containing the list of directories in
	 * which plug-ins are stored
	 * @see PluginDirsPanel
	 */
	private PluginDirsPanel pluginDirsPanel;
	
	
	/**
	 * an array of plug-in {@link PluginState states}
	 */
	private ArrayList<PluginState> descriptions;
	/**
	 * an array of directories in which plug-ins are
	 * stored
	 */
	private ArrayList<File> pluginDirs;
	
	/**
	 * Constructor. Creates and adds two sub-panels.
	 * @param pluginDescriptions an array of plug-in {@link PluginState states}
	 * @param pluginDirectories an array of directories in which plug-ins are
	 * stored
	 */
	public PluginPanel(ArrayList<PluginState> pluginDescriptions, ArrayList<File> pluginDirectories){
		setLayout(new BorderLayout());
		this.pluginDirs = pluginDirectories;
		descriptions = pluginDescriptions;
		
		pluginListPanel = new PluginListPanel(descriptions);
		pluginDirsPanel = new PluginDirsPanel(pluginDirs);
		add(pluginDirsPanel, BorderLayout.CENTER);
		add(pluginListPanel, BorderLayout.SOUTH);
		
		
		
	}
	
	
	/**
	 * Fills the sub-panels with the current data.
	 * @param pluginDescriptions an array of plug-in {@link PluginState states}
	 */
	public void fillPanelFromModel(ArrayList<PluginState> pluginDescriptions){
		pluginListPanel.fillPanelFromModel(pluginDescriptions);
		pluginDirsPanel.fillPanelFromModel();
		//add(new JCheckBox(descriptions.get(0).toString()));
		/*JCheckBox checkBoxes[] = new JCheckBox[descriptions.size()];
		for (int i = 0; i < checkBoxes.length; ++i){
			checkBoxes[i] = new JCheckBox(descriptions.get(i).toString());
		}
		list = new JList(checkBoxes);*/
	}

	/**
	 * Updates the data with information from sub-panels.
	 * @param descriptions an array of plug-in {@link PluginState states}
	 */
	public void fillModelFromPanel(ArrayList<PluginState> descriptions){
		pluginListPanel.fillModelFromPanel(descriptions);
		pluginDirsPanel.fillModelFromPanel();
	}
}

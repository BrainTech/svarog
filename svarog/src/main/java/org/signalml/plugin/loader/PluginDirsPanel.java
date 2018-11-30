package org.signalml.plugin.loader;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * This panel contains only a text field with the string containing
 * paths to directories in which plug-ins are stored.
 * Paths to these directories are separated by semicolons.
 * @author Marcin Szumski
 */
public class PluginDirsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the text field in with paths to directories
	 * in which plug-ins are stored
	 */
	private JTextField pluginDirsField = new JTextField();
	/**
	 * an array of directories in which plug-ins are stored
	 */
	private ArrayList<File> pluginDirs;

	/**
	 * Constructor.
	 * Creates and adds the field to this panel
	 * @param pluginDirs an array of directories in which
	 * plug-ins are stored
	 */
	public PluginDirsPanel(ArrayList<File> pluginDirs) {
		this.pluginDirs = pluginDirs;
		setLayout(new BorderLayout());
		CompoundBorder cb = new CompoundBorder(
			new TitledBorder(_("Plugin directories")),
			null
		);

		setBorder(cb);
		add(pluginDirsField);

	}

	/**
	 * Converts an array of directories in which plug-ins are stored
	 * to the string containing paths to those directories.
	 * @param pluginDirs an array of directories in which
	 * plug-ins are stored
	 * @return the string containing paths to directories in which
	 * plug-ins are stored
	 */
	private String getPluginDirsString(ArrayList<File> pluginDirs) {
		String result = new String();
		int i = 0;
		for (File pluginDir :  pluginDirs) {
			String path = pluginDir.getPath();
			result += path;
			if (++i < pluginDirs.size()) result += ";";
		}
		return result;
	}

	/**
	 * Converts the string containing paths to directories
	 * in which plug-ins are stored to an array of those directories.
	 * @param pluginDirsString the string containing paths to directories
	 * in which plug-ins are stored
	 * @return an array of directories in which plug-ins are stored
	 */
	private ArrayList<File> getPluginDirs(String pluginDirsString) {
		String[] paths;
		paths = pluginDirsString.split(";");
		ArrayList<File> pluginDirs = new ArrayList<File>();
		for (String path : paths) {
			File dir;
			if (path!=null) {
				dir = new File(path.trim());
				pluginDirs.add(dir);
			}


		}
		return pluginDirs;
	}

	/**
	 * Fills the text field with the string containing paths to directories
	 * in which plug-ins are stored
	 */
	public void fillPanelFromModel() {
		pluginDirsField.setText(getPluginDirsString(pluginDirs));
	}

	/**
	 * Updates an array of directories in which plug-ins are stored with
	 * the user input.
	 */
	public void fillModelFromPanel() {
		ArrayList<File> dirs = getPluginDirs(pluginDirsField.getText());
		pluginDirs.clear();
		pluginDirs.addAll(dirs);
	}

}

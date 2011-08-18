package org.signalml.plugin.newartifact;

import org.signalml.plugin.data.PluginConfigForMethod;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.method.PluginMethodManager;
import org.signalml.plugin.tool.AbstractPluginTool;

/**
 * @author kdr
 */

public class NewArtifactPlugin extends AbstractPluginTool {

	private PluginMethodManager manager;

	@Override
	public void register(SvarogAccess access) throws SignalMLException {
	    access.getConfigAccess().setupConfig(this, "classpath:/org/signalml/plugin/newartifact/resource/config.xml");
		this.manager = new PluginMethodManager(access,
						       (PluginConfigForMethod) access.getConfigAccess()
						       .getResource("config"));

		this.setupGUI(access.getGUIAccess());
	}

	private void setupGUI(SvarogAccessGUI guiAccess)
	throws UnsupportedOperationException, SignalMLException {
		guiAccess
		.addButtonToToolsMenu(new NewArtifactPluginAction(this.manager));
	}
}

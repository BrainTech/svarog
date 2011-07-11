package org.signalml.plugin.newartifact;

import org.signalml.plugin.data.PluginConfigForMethod;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.method.PluginMethodManager;
import org.signalml.plugin.tool.AbstractPluginTool;
import org.signalml.plugin.tool.PluginAccessHelper;
import org.signalml.plugin.tool.PluginResourceRepository;

/**
 * @author kdr
 */

public class NewArtifactPlugin extends AbstractPluginTool {

	private PluginMethodManager manager;

	@Override
	public void register(SvarogAccess access) throws SignalMLException {
		PluginAccessHelper
		.SetupConfig(this,
			     "classpath:resources/org/signalml/plugin/newartifact/resource/config.xml");

		this.manager = new PluginMethodManager(access,
						       (PluginConfigForMethod) PluginResourceRepository
						       .GetResource("config"));

		this.setupGUI(access.getGUIAccess());
	}

	private void setupGUI(SvarogAccessGUI guiAccess)
	throws UnsupportedOperationException, PluginException {
		guiAccess
		.addButtonToToolsMenu(new NewArtifactPluginAction(this.manager));
	}
}

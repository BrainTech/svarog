package org.signalml.plugin.newstager;

import org.signalml.plugin.data.PluginConfigForMethod;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.method.PluginMethodManager;
import org.signalml.plugin.newstager.ui.NewStagerPluginAction;
import org.signalml.plugin.tool.PluginAccessHelper;
import org.signalml.plugin.tool.PluginResourceRepository;

/**
 * @author kdr
 */

public class NewStagerPlugin implements Plugin {

	public static final String iconPath = "org/signalml/app/icon/runmethod.png"; //TODO move this elsewhere

	private PluginMethodManager manager;

	@Override
	public void register(SvarogAccess access) throws SignalMLException {
		PluginAccessHelper
		.SetupConfig(this, access,
					 "classpath:org/signalml/plugin/newstager/resource/config.xml");

		this.manager = new PluginMethodManager(access,
											   (PluginConfigForMethod) PluginResourceRepository.GetResource(
													   "config", NewStagerPlugin.class));

		this.setupGUI(access.getGUIAccess());
		// PluginAccessHelper.SetupGUI(access.getGUIAccess(), this.createGUI());
	}

	private void setupGUI(SvarogAccessGUI guiAccess)
	throws UnsupportedOperationException, PluginException {
		guiAccess
		.addButtonToToolsMenu(new NewStagerPluginAction(this.manager));
	}
}

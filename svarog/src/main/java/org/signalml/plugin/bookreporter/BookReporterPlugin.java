package org.signalml.plugin.bookreporter;

import org.signalml.plugin.bookreporter.ui.BookReporterPluginAction;
import org.signalml.plugin.data.PluginConfigForMethod;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.method.PluginMethodManager;
import org.signalml.plugin.tool.PluginAccessHelper;
import org.signalml.plugin.tool.PluginResourceRepository;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * @author piotr@develancer.pl
 * (based on kdr's NewStagerPlugin)
 */
public class BookReporterPlugin implements Plugin {

	public static final String iconPath = "org/signalml/app/icon/runmethod.png"; //TODO move this elsewhere

	private PluginMethodManager manager;

	@Override
	public void register(SvarogAccess access) throws SignalMLException {
		PluginAccessHelper
			.SetupConfig(this, access,
				"classpath:org/signalml/plugin/bookreporter/resource/config.xml");

		this.manager = new PluginMethodManager(access,
			(PluginConfigForMethod) PluginResourceRepository.GetResource(
				"config", BookReporterPlugin.class));

		this.setupGUI(access.getGUIAccess());
		// PluginAccessHelper.SetupGUI(access.getGUIAccess(), this.createGUI());
	}

	private void setupGUI(SvarogAccessGUI guiAccess)
		throws UnsupportedOperationException, PluginException {
		guiAccess
			.addSubmenuToAnalysisMenu(_("Matching pursuit"))
			.add(new BookReporterPluginAction(this.manager));
	}
}

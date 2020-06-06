package org.signalml.plugin.tool;

import java.util.HashMap;
import java.util.Map;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SvarogAccess;

public class PluginAccessHelper {

	private static Map<Class<? extends Plugin>, SvarogAccess> AccessMap = new HashMap<Class<? extends Plugin>, SvarogAccess>();

	public static void SetupConfig(Plugin plugin, SvarogAccess svarogAccess)
	throws PluginException {
		PluginContextHelper.AddPluginContext(plugin);
		AccessMap.put(plugin.getClass(), svarogAccess);
	}

	public static void SetupConfig(Plugin plugin, SvarogAccess svarogAccess,
								   String configResourceName) throws PluginException {
		PluginResourceRepository.RegisterPlugin(plugin.getClass(),
												configResourceName);
		PluginAccessHelper.SetupConfig(plugin, svarogAccess);
	}

	public static SvarogAccess GetSvarogAccess() {
		Class<? extends Plugin> klass = PluginContextHelper
										.FindContextPluginClass();
		return AccessMap.get(klass);
	}

}

package org.signalml.plugin.tool;

import java.util.HashMap;
import java.util.Map;

import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.Plugin;
import static org.signalml.plugin.i18n.PluginI18n._;

public class PluginContextHelper {

	private static Map<String, Class<? extends Plugin>> PluginPackages = new HashMap<String, Class<? extends Plugin>>();

	protected static Class<? extends Plugin> FindContextPluginClass() {
		try {
			StackTraceElement stackTrace[] = Thread.currentThread()
											 .getStackTrace();

			for (int i = 1; i < stackTrace.length; ++i) {
				StackTraceElement e = stackTrace[i];
				String klassName = e.getClassName();

				if (klassName != null) {
					for (int pos = klassName.length(); (pos = klassName
														.lastIndexOf(".", pos - 1)) != -1;) {
						String packageName = klassName.substring(0, pos);
						Class<? extends Plugin> klass = PluginPackages
														.get(packageName);
						if (klass != null) {
							return klass;
						}
					}
				}
			}

		} catch (SecurityException e) {
			// do nothing; can't actually happen
		}

		return null;
	}

	protected static void AddPluginContext(Plugin plugin)
	throws PluginException {
		Class<? extends Plugin> klass = plugin.getClass();
		if (klass.isAnonymousClass()) {
			throw new PluginException(_("Anonymous plugin classes not supported"));
		}

		Package _package = klass.getPackage();
		if (_package == null) {
			throw new PluginException(_("Cannot get plugin package"));
		}

		String packageName = _package.getName();
		if (packageName == null) {
			throw new PluginException(_("Cannot get plugin package name"));
		}

		if (PluginPackages.containsKey(packageName)) {
			throw new PluginException(String.format(_("Plugin class %s already registered"), packageName));
		}

		PluginPackages.put(packageName, klass);
	}

}

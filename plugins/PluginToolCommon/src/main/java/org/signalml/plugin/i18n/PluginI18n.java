package org.signalml.plugin.i18n;

public class PluginI18n {

	private static IPluginI18nAccess Access = new PluginGenericI18nAccess();

	public static String _(String msgKey) {
		return getSharedInstance()._(msgKey);
	}

	public static String _R(String msgKey, Object... arguments) {
		return getSharedInstance()._R(msgKey, arguments);
	}

	public static IPluginI18nAccess getSharedInstance() {
		return PluginI18n.Access;
	}

	public static void setSharedInstance(IPluginI18nAccess access) {
		PluginI18n.Access = access;
	}
}

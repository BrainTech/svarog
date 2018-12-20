package org.signalml.plugin.i18n;

import java.net.URL;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.tool.PluginAccessHelper;

public class PluginGenericI18nAccess implements IPluginI18nAccess {
	
	@Override
	public String _(String msgKey) {
		SvarogAccess access = PluginAccessHelper.GetSvarogAccess();
		if (access == null) {
			return msgKey;
		}
		return access.getI18nAccess().translate(msgKey);
	}

	@Override
	public String _R(String msgKey, Object... arguments) {
		SvarogAccess access = PluginAccessHelper.GetSvarogAccess();
		if (access == null) {
			return msgKey;
		}
		return access.getI18nAccess().translateR(msgKey, arguments);
	}

	@Override
	public URL _H(String htmlName) {
		SvarogAccess access = PluginAccessHelper.GetSvarogAccess();
		if (access == null) {
			return null;
		}
		return access.getI18nAccess().getHelpURL(htmlName);
	}
}

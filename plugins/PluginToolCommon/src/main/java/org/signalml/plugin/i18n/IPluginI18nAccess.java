package org.signalml.plugin.i18n;

public interface IPluginI18nAccess {

	String _(String msgKey);

	String _R(String msgKey, Object... arguments);

}

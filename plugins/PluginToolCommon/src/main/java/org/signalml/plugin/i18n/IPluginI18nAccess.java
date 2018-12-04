package org.signalml.plugin.i18n;

import java.net.URL;

public interface IPluginI18nAccess {

	String _(String msgKey);

	String _R(String msgKey, Object... arguments);

	URL _H(String htmlName);
}

package org.signalml.plugin.i18n;

import java.util.List;

import org.signalml.plugin.data.PluginConfig;
import org.signalml.plugin.data.PluginConfigWithMessageSourceReference;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.tool.PluginResourceRepository;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.DefaultResourceLoader;

public class PluginMessageSourceManager {

	public MessageSourceAccessor createInstance(PluginConfig config,
			List<String> baseNames) {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setCacheSeconds(-1);

		ClassLoader classLoader = config.getPluginClass().getClassLoader();
		messageSource.setResourceLoader(new DefaultResourceLoader(classLoader));

		messageSource.setBasenames(baseNames.toArray(new String[0]));

		return new MessageSourceAccessor(messageSource,
						 LocaleContextHolder.getLocale());
	}

	public static MessageSourceAccessor GetMessageSource() throws PluginException {
		Object resource = PluginResourceRepository.GetResource("config");

		PluginConfigWithMessageSourceReference config;
		try {
			config = (PluginConfigWithMessageSourceReference) resource;
		} catch (ClassCastException e) {
			throw new PluginException(e);
		}

		Object result = PluginResourceRepository.GetResource(
					config.getMessageSourceName(), config.getPluginClass());
		try {
			return (MessageSourceAccessor) result;
		} catch (ClassCastException e) {
			throw new PluginException(e);
		}
	}
}

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
			// TODO: handle exception
		}
	}
	/*
	 * private static Map<Class<? extends Plugin>, MessageSourceAccessor> map =
	 * new HashMap<Class<? extends Plugin>, MessageSourceAccessor>();
	 *
	 * public static void RegisterPlugin(Class<? extends Plugin> plugin) {
	 * MessageSourceAccessor source = PluginMessageSourceManager
	 * .CreateMessageSource(plugin); // TODO
	 * PluginMessageSourceManager.map.put(plugin, source); }
	 *
	 * public static MessageSourceAccessor GetMessageSourceForPlugin( Class<?
	 * extends Plugin> plugin) { return
	 * PluginMessageSourceManager.map.get(plugin); }
	 *
	 * private static MessageSourceAccessor CreateMessageSource(Class<? extends
	 * Plugin> plugin) { ReloadableResourceBundleMessageSource messageSource =
	 * new ReloadableResourceBundleMessageSource();
	 * messageSource.setCacheSeconds(-1);
	 *
	 * ClassLoader classLoader =
	 * plugin.getClassLoader();//URLClassLoader.newInstance(urls,
	 * Thread.currentThread().getContextClassLoader()); ResourceLoader loader =
	 * new DefaultResourceLoader(classLoader);
	 * messageSource.setResourceLoader(loader);
	 *
	 * messageSource.setBasenames(new String[] {
	 * "classpath:org/signalml/app/resource/message",
	 * "classpath:org/signalml/plugin/newartifact/resource/message", //TODO //
	 * "classpath:org/signalml/resource/wsmessage" });
	 *
	 * return new MessageSourceAccessor(messageSource,
	 * LocaleContextHolder.getLocale()); }
	 */
}

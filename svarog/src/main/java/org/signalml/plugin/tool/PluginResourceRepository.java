package org.signalml.plugin.tool;

import java.util.HashMap;
import java.util.Map;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.Plugin;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PluginResourceRepository {

	private static Map<Class<? extends Plugin>, BeanFactory> beanFactoryMap = new HashMap<Class<? extends Plugin>, BeanFactory>();

	public static void RegisterPlugin(Class<? extends Plugin> pluginClass,
									  String configResourceName) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
			new String[] { configResourceName }, false, null);

		ctx.setClassLoader(pluginClass.getClassLoader());
		ctx.refresh();

		beanFactoryMap.put(pluginClass, ctx);
	}

	public static Object GetResource(String resourceName,
									 Class<? extends Plugin> pluginClass) throws PluginException {

		BeanFactory factory = beanFactoryMap.get(pluginClass);
		if (factory == null) {
			throw new PluginException(_("No enclosing bean factory found"));
		}

		try {
			return factory.getBean(resourceName);
		} catch (BeansException e) {
			throw new PluginException(e);
		}
	}

	public static Object GetResource(String resourceName)
	throws PluginException {
		Class<? extends Plugin> pluginClass = PluginContextHelper.FindContextPluginClass();
		if (pluginClass == null)
			throw new PluginException(_("No enclosing plugin class found"));

		return GetResource(resourceName, pluginClass);
	}

}

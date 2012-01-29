package org.signalml.plugin.tool;

import java.io.IOException;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			throw new PluginException("No enclosing bean factory found");
		}

		try {
			return factory.getBean(resourceName);
		} catch (BeansException e) {
			throw new PluginException(e);
		}
	}

	public static Object GetResource(String resourceName)
			throws PluginException {
		Class<? extends Plugin> pluginClass = FindContextPluginClass();
		if (pluginClass == null)
			throw new PluginException("No enclosing plugin class found");

		return GetResource(resourceName, pluginClass);
	}

	private static Class<? extends Plugin> FindContextPluginClass() {
		try {
			CodeSource thisSource = PluginResourceRepository.class
					.getProtectionDomain().getCodeSource();
			if (thisSource == null)
				return null;

			StackTraceElement stackTrace[] = Thread.currentThread()
					.getStackTrace();
			//ClassLoader loader = PluginResourceRepository.class
			//		.getClassLoader();

			for (int i = 1; i < stackTrace.length; ++i) {
				//StackTraceElement e = stackTrace[i];
				Class<?> klass = sun.reflect.Reflection.getCallerClass(i);

				CodeSource source = klass.getProtectionDomain().getCodeSource();
				if (source != null) {
					if (!source.getLocation()
							.sameFile(thisSource.getLocation())) {
						Class<? extends Plugin> pluginClass = TryFindAssociatedPluginClass(klass);
						if (pluginClass != null)
							return pluginClass;
					}
				}
			}

		} catch (SecurityException e) {
			// do nothing
		}

		return null;
	}

	private static Class<? extends Plugin> TryFindAssociatedPluginClass(
			Class<?> klass) {
		CodeSource source = klass.getProtectionDomain().getCodeSource();

		if (source == null)
			return null;

		ClassLoader loader = klass.getClassLoader();
		Pattern classPattern = Pattern.compile("(.*)\\.class$",
				Pattern.CASE_INSENSITIVE);

		try {
			JarInputStream jarStream = new JarInputStream(source.getLocation()
					.openStream());

			while (true) {
				JarEntry entry = jarStream.getNextJarEntry();
				if (entry == null)
					break;

				String entryName = entry.getName();
				if (!entry.isDirectory()) {
					Matcher m = classPattern.matcher(entryName);
					if (m.matches()) {
						try {
							Class<?> jarClass = Class.forName(m.group(1)
									.replace('/', '.'), false, loader);
							if (Plugin.class.isAssignableFrom(jarClass))
								try {
									return jarClass.asSubclass(Plugin.class);
								} catch (ClassCastException e) {
									continue;
								}

						} catch (ClassNotFoundException e) {
							continue;
						}
					}
				}
			}

		} catch (IOException e) {
			return null;
		}

		return null;
	}
}

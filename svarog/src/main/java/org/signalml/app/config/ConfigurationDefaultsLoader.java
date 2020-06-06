package org.signalml.app.config;

import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ConfigurationDefaultsLoader {

	protected static final Logger logger = Logger.getLogger(ConfigurationDefaultsLoader.class);

	public static Properties Load(Class<?> klass, String classPath) {
		Properties properties = new Properties();
		try {
			Resource r = new ClassPathResource(classPath, klass);
			properties.load(r.getInputStream());
		} catch (IOException ex) {
			logger.error("Failed to load default properties - i/o exception", ex);
			throw new RuntimeException(ex);
		}

		return properties;
	}

}

package org.signalml.plugin.impl;

import java.io.IOException;
import javax.swing.ImageIcon;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import org.signalml.plugin.export.PluginAuth;
import org.signalml.plugin.export.resources.SvarogAccessResources;
import org.apache.log4j.Logger;

/**
 * {@link SvarogAccessResources} implementation using org.springframework.context.
 *
 */
public class SvarogAccessResourcesImpl implements SvarogAccessResources {
	protected static final Logger log = Logger.getLogger(SvarogAccessResources.class);

	/**
	 * Returns the singleton instance.
	 * @return
	 */
	public static SvarogAccessResourcesImpl getInstance() {
		return Instance;
	}

	private static final SvarogAccessResourcesImpl Instance = new SvarogAccessResourcesImpl();

	private PluginAccessClass pluginAccessClass;

	private SvarogAccessResourcesImpl() {
	}

	private Class<?> getClass(PluginAuth auth) {
		return (pluginAccessClass.getPluginHead(auth).getPluginObj().getClass());
	}

	protected void setPluginAccessClass(PluginAccessClass pac) {
		this.pluginAccessClass = pac;
	}

	public ImageIcon loadClassPathIcon(PluginAuth auth, String classpath) throws IOException {
		Resource icon = new ClassPathResource(classpath, getClass(auth));
		log.debug("trying to load " + icon.getURL());
		try {
			return new ImageIcon(icon.getURL());
		} catch (IOException ex) {
			log.error("WARNING: failed to open icon recource [" + icon.toString() + "]", ex);
			throw ex;
		}
	}
}

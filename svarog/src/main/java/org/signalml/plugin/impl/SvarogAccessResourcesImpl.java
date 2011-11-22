package org.signalml.plugin.impl;

import java.io.IOException;
import javax.swing.ImageIcon;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import org.signalml.plugin.export.resources.SvarogAccessResources;
import org.apache.log4j.Logger;

/**
 * {@link SvarogAccessResources} implementation using org.springframework.context.
 *
 */
public class SvarogAccessResourcesImpl implements SvarogAccessResources {
	protected static final Logger log = Logger.getLogger(SvarogAccessResources.class);

	private final Class klass;

	public SvarogAccessResourcesImpl(Class klass) {
		this.klass = klass;
	}

	public ImageIcon loadClassPathIcon(String classpath) throws IOException {
		Resource icon = new ClassPathResource(classpath, this.klass);
		log.debug("trying to load " + icon.getURL());
		try {
			return new ImageIcon(icon.getURL());
		} catch (IOException ex) {
			log.error("WARNING: failed to open icon recource [" + icon + "]", ex);
			throw ex;
		}
	}
}

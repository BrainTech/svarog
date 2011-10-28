package org.signalml.app.config.preset;

import java.io.File;

import com.thoughtworks.xstream.XStream;
import java.io.BufferedInputStream;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * A {@link PresetManager} that can be used for read-only presets stored
 * in the class path resource (that is - the /src/main/resources/org/signalml
 * directory).
 *
 * @author Piotr Szachewicz
 */
public abstract class AbstractResourceXMLConfiguration extends AbstractPresetManager {

	@Override
	public void readFromXML(File f, XStream streamer) throws IOException {
		Resource resource = new ClassPathResource(getStandardFilename());
		InputStream inputStream = new BufferedInputStream(resource.getInputStream());
		readFromInputStream(inputStream);
	}

	@Override
	public void writeToXML(File f, XStream streamer) throws IOException {
		throw new UnsupportedOperationException("Resource XML configuration cannot be saved to a file - it is read only");
	}

}

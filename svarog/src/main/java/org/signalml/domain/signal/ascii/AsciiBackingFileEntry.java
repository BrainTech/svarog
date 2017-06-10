package org.signalml.domain.signal.ascii;

import java.io.File;

/**
 * Pair of temporary files generated from ASCII data.
 * Instances of this class are managed by {@link AsciiBackingFileRepository}.
 *
 * @author piotr.rozanski@braintech.pl
 */
public class AsciiBackingFileEntry {

	public final File raw;
	public final File xml;

	public AsciiBackingFileEntry(File raw, File xml) {
		this.raw = raw;
		this.xml = xml;
	}

}

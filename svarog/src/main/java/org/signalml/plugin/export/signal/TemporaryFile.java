package org.signalml.plugin.export.signal;

import java.io.File;
import org.apache.log4j.Logger;

/**
 * The file that is removed from the disk when this instance is destroyed
 * by Garbage Collector.
 *
 * @author Marcin Szumski
 */
public class TemporaryFile extends File {

	protected static final Logger logger = Logger.getLogger(TemporaryFile.class);

	/**
	 * See {@link File#File(File, String)}
	 * @param parent the parent abstract pathname
	 * @param child the child pathname string
	 */
	public TemporaryFile(File parent, String child) {
		super(parent, child);
	}


	/**
	 * See {@link File#File(String)}
	 * @param pathname the pathname string
	 */
	public TemporaryFile(String pathname) {
		super(pathname);
	}


	private static final long serialVersionUID = 1L;




	@Override
	protected void finalize() {
		try {
			delete();
			super.finalize();
		} catch (Throwable e) {
			logger.error("", e);
		}
	}
}

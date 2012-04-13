package org.signalml.plugin.fft;

import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.SvarogAccess;

/**
 * This class contains only the empty registration function, which is called
 * when this plug-in is loaded.
 *
 * @author Marcin Szumski
 */
public class FFT implements Plugin {

	/**
	 * Does nothing as this plug-in is only a library for other plug-ins.
	 */
	@Override
	public void register(SvarogAccess arg0) throws SignalMLException { }
}

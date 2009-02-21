/* FFTWindowTypeSettings.java created 2008-02-04
 * 
 */
package org.signalml.app.config;

import org.signalml.fft.WindowType;

/** FFTWindowTypeSettings
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface FFTWindowTypeSettings {

	WindowType getWindowType();

	void setWindowType(WindowType windowType);

	double getWindowParameter();

	void setWindowParameter(double windowParameter);

}
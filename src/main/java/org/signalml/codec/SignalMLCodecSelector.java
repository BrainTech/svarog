/* SignalMLCodecSelector.java created 2007-09-19
 *
 */

package org.signalml.codec;

/** SignalMLCodecSelector
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SignalMLCodecSelector {

	SignalMLCodec getSelectedCodec();

	void setSelectedCodec(SignalMLCodec codec);

}

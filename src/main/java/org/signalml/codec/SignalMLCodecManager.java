/* SignalMLCodecManager.java created 2007-09-17
 *
 */

package org.signalml.codec;

import java.io.File;
import java.io.IOException;


/** SignalMLCodecManager
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SignalMLCodecManager {

	int getCodecCount();
	SignalMLCodec getCodecAt(int index);
	SignalMLCodec getCodecForFormat(String formatName);
	SignalMLCodec getCodecByUID(String uid);
	int getIndexOfCodec(SignalMLCodec codec);

	void registerSignalMLCodec(SignalMLCodec codec);

	void removeSignalMLCodec(SignalMLCodec codec);
	void removeSignalMLCodecAt(int index);

	void writeToPersistence(File file) throws IOException;
	void readFromPersistence(File file) throws IOException, CodecException;

	void addSignalMLCodecManagerListener(SignalMLCodecManagerListener listener);
	void removeSignalMLCodecManagerListener(SignalMLCodecManagerListener listener);

}

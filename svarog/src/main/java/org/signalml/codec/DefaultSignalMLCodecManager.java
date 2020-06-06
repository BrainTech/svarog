/* DefaultSignalMLCodecManager.java created 2007-09-17
 *
 */

package org.signalml.codec;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.swing.event.EventListenerList;
import org.apache.log4j.Logger;
import org.signalml.app.config.SignalMLCodecConfiguration;
import org.signalml.app.config.SignalMLCodecDescriptor;
import org.signalml.util.Util;


/** DefaultSignalMLCodecManager
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DefaultSignalMLCodecManager implements SignalMLCodecManager {

	protected static final Logger logger = Logger.getLogger(DefaultSignalMLCodecManager.class);

	private Vector<SignalMLCodec> codecs = new Vector<SignalMLCodec>();
	private Map<String,SignalMLCodec> codecsByFormatName = new HashMap<String,SignalMLCodec>();
	private Map<String,SignalMLCodec> codecsByUID = new HashMap<String,SignalMLCodec>();

	private File profileDir;
	private XStream streamer;

	private EventListenerList listenerList = new EventListenerList();

	@Override
	public int getCodecCount() {
		synchronized (this) {
			return codecs.size();
		}
	}

	@Override
	public SignalMLCodec getCodecAt(int index) {
		synchronized (this) {
			return codecs.elementAt(index);
		}
	}

	@Override
	public SignalMLCodec getCodecForFormat(String formatName) {
		synchronized (this) {
			return codecsByFormatName.get(formatName);
		}
	}

	@Override
	public SignalMLCodec getCodecByUID(String uid) {
		SignalMLCodec ret = codecsByUID.get(uid);
		if (ret == null && uid.startsWith("org.signalml.codec.precompiled.")) {
			ret = StaticCodec.forSourceName(uid);
		}
		return ret;
	}

	@Override
	public void registerSignalMLCodec(SignalMLCodec codec) {
		synchronized (this) {

			if (codecs.contains(codec)) {
				logger.warn("ignoring duplicate codec: " + codec);
				return;
			}

			int index = registerSignalMLCodecInternal(codec);
			fireCodecAdded(codec, index);

		}
	}

	private int registerSignalMLCodecInternal(SignalMLCodec codec) {

		String registerAsFormatName = codec.getFormatName();
		logger.info("registering codec " + registerAsFormatName + ": " + codec);
		assert registerAsFormatName != null;

		SignalMLCodec uidCodec = codecsByUID.get(codec.getSourceUID());
		if (uidCodec != null && uidCodec != codec) {
			codecsByFormatName.remove(uidCodec.getFormatName());
			codecsByUID.remove(codec.getSourceUID());
			codecs.remove(uidCodec);
		}

		SignalMLCodec oldCodec = codecsByFormatName.get(registerAsFormatName);
		if (oldCodec != null) {
			codecsByFormatName.remove(registerAsFormatName);
			codecsByUID.remove(oldCodec.getSourceUID());
			codecs.remove(oldCodec);
		}
		codecs.add(codec);
		codecsByFormatName.put(registerAsFormatName,codec);
		codecsByUID.put(codec.getSourceUID(),codec);

		return codecs.indexOf(codec);

	}

	@Override
	public int getIndexOfCodec(SignalMLCodec codec) {
		synchronized (this) {
			return codecs.indexOf(codec);
		}
	}

	@Override
	public void removeSignalMLCodec(SignalMLCodec codec) {
		logger.info("unregistering codec " + codec.getFormatName());
		synchronized (this) {
			if (!codecs.contains(codec)) {
				return;
			}
			codecsByFormatName.remove(codec.getFormatName());
			codecsByUID.remove(codec.getSourceUID());

			int index = codecs.indexOf(codec);
			codecs.remove(codec);

			fireCodecRemoved(codec, index);

		}
	}

	@Override
	public void removeSignalMLCodecAt(int index) {
		synchronized (this) {
			removeSignalMLCodec(codecs.get(index));
		}
	}

	private void clear() {
		synchronized (this) {
			codecs.clear();
			codecsByFormatName.clear();
			fireCodecsChanged();
		}
	}

	public void writeToPersistence(File file) throws IOException {

		SignalMLCodecDescriptor[] descr;
		synchronized (this) {
			descr = new SignalMLCodecDescriptor[codecs.size()];
			for (int i=0; i<descr.length; i++) {
				descr[i] = new SignalMLCodecDescriptor(codecs.get(i));
			}
		}

		SignalMLCodecConfiguration config = new SignalMLCodecConfiguration(descr);
		config.writeToXML((file == null) ? config.getStandardFile(profileDir) : file, streamer);

	}

	public void readFromPersistence(File file) throws IOException, CodecException {

		SignalMLCodecConfiguration config = new SignalMLCodecConfiguration();;
		if (file == null) {
			file = config.getStandardFile(profileDir);
		}
		config.readFromXML(file,streamer);

		synchronized (this) {
			clear();
			SignalMLCodecDescriptor[] descr = config.getCodecs();
			SignalMLCodec codec;
			for (SignalMLCodecDescriptor descriptor : descr) {
				codec = descriptor.getCodec();
				codec.setFormatName(descriptor.getFormatName());
				registerSignalMLCodecInternal(codec);
			}

			fireCodecsChanged();
		}

	}

	public File getProfileDir() {
		synchronized (this) {
			return profileDir;
		}
	}

	public void setProfileDir(File profileDir) {
		synchronized (this) {
			this.profileDir = profileDir;
		}
	}

	public XStream getStreamer() {
		synchronized (this) {
			return streamer;
		}
	}

	public void setStreamer(XStream streamer) {
		synchronized (this) {
			this.streamer = streamer;
		}
	}

	public void verify() {
		synchronized (this) {
			logger.debug("Verifying codec manager");
			Iterator<SignalMLCodec> it = codecs.iterator();
			while (it.hasNext()) {
				SignalMLCodec codec = it.next();
				if (codec instanceof XMLSignalMLCodec) {
					XMLSignalMLCodec xmlCodec = (XMLSignalMLCodec) codec;
					File expectedPath = new File(xmlCodec.getRepositoryDir(), "smlcache-" + System.getProperty("user.name") + Util.FILE_SEP + xmlCodec.getCacheDirName());
					logger.debug("Codec [" + codec.getFormatName() + "] expected at [" + expectedPath.getAbsolutePath() + "]");
					if (!expectedPath.exists()) {
						logger.warn("Codec directory lost, removing codec");
						codecsByFormatName.remove(codec.getFormatName());
						it.remove();
						continue;
					}
				}
				logger.debug("Codec ok");
			}
			logger.debug("Verification complete");
		}
	}

	public void cleanUp() {
		synchronized (this) {
			logger.debug("Cleaning up codec repository - not implemented");

			// TODO cleanup stale dirs from profile cache

			logger.debug("Cleaning up complete");
		}
	}

	protected void fireCodecAdded(SignalMLCodec codec, int index) {
		Object[] listeners = listenerList.getListenerList();
		SignalMLCodecManagerEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==SignalMLCodecManagerListener.class) {
				if (e == null) {
					e = new SignalMLCodecManagerEvent(this,codec,index);
				}
				((SignalMLCodecManagerListener)listeners[i+1]).codecAdded(e);
			}
		}
	}

	protected void fireCodecRemoved(SignalMLCodec codec, int index) {
		Object[] listeners = listenerList.getListenerList();
		SignalMLCodecManagerEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==SignalMLCodecManagerListener.class) {
				if (e == null) {
					e = new SignalMLCodecManagerEvent(this,codec,index);
				}
				((SignalMLCodecManagerListener)listeners[i+1]).codecRemoved(e);
			}
		}
	}

	protected void fireCodecsChanged() {
		Object[] listeners = listenerList.getListenerList();
		SignalMLCodecManagerEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==SignalMLCodecManagerListener.class) {
				if (e == null) {
					e = new SignalMLCodecManagerEvent(this);
				}
				((SignalMLCodecManagerListener)listeners[i+1]).codecsChanged(e);
			}
		}
	}

	public void addSignalMLCodecManagerListener(SignalMLCodecManagerListener listener) {
		synchronized (this) {
			listenerList.add(SignalMLCodecManagerListener.class, listener);
		}
	}

	public void removeSignalMLCodecManagerListener(SignalMLCodecManagerListener listener) {
		synchronized (this) {
			listenerList.remove(SignalMLCodecManagerListener.class, listener);
		}
	}

}

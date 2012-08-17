/* DefaultMRUDRegistry.java created 2007-09-12
 *
 */
package org.signalml.app.document.mrud;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.EventListenerList;

import org.signalml.app.config.MRUDConfiguration;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.plugin.export.signal.Document;

import com.thoughtworks.xstream.XStream;

/**
 * Implementation of {@link MRUDRegistry}.
 * The initial capacity of it is 50 (this cache can remember 50
 * {@link MRUDEntry entries}), but can be changed through
 * {@link #setRegistrySize(int)}.
 * Each entry is stored in three collections:
 * <ul>
 * <li>the vector with all entries,</li>
 * <li>the vector containing entries for a specified {@link ManagedDocumentType
 * type} of a document,</li>
 * <li>the map associating files with the {@link MRUDEntry entries} in which
 * they are described.</li>
 * </ul>
 * This class contains also {@link MRUDRegistryListener listeners} listening
 * for addition and removal of entries.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DefaultMRUDRegistry implements MRUDRegistry {

	/**
	 * the vector with all {@link MRUDEntry entries} in this registry
	 */
	private Vector<MRUDEntry> entries = new Vector<MRUDEntry>(100);

	/**
	 * the map associating {@link ManagedDocumentType types} of {@link Document
	 * documents} with vectors of {@link MRUDEntry entries} for these types
	 */
	private Map<ManagedDocumentType,Vector<MRUDEntry>> entryVectorsByType = new HashMap<ManagedDocumentType,Vector<MRUDEntry>>(10);

	/**
	 * the map associating files with the {@link MRUDEntry entries} in which
	 * they are described
	 */
	private Map<File,MRUDEntry> entriesByFile = new HashMap<File,MRUDEntry>(100);

	/**
	 * the number of entries that can be stored in this registry
	 */
	private int registrySize = 50;

	/**
	 * {@link MRUDComparator comparator} of {@link MRUDEntry entries}
	 */
	private MRUDComparator comparator;

	/**
	 * true if {@link #entries} is sorted, false otherwise
	 */
	private boolean isMainSorted = true;

	/**
	 * the profile directory
	 */
	private File profileDir;

	/**
	 * the stream used to read stored {@link MRUDEntry entires} from file
	 */
	private XStream streamer;

	/**
	 * the listeners listening on changes (registering an removing
	 * {@link MRUDEntry entries}) in this registry
	 */
	private EventListenerList listenerList = new EventListenerList();

	/**
	 * Constructor. Sets the default comparator.
	 */
	public DefaultMRUDRegistry() {
		comparator = new MRUDComparator();
	}

	/**
	 * Returns the number of {@link MRUDEntry entries} that can be stored
	 * in this registry.
	 * @return the number of entries that can be stored in this registry
	 */
	public int getRegistrySize() {
		synchronized (this) {
			return registrySize;
		}
	}

	/**
	 * Sets the number of {@link MRUDEntry entries} that can be stored in this
	 * registry.
	 * @param registrySize the number of entries that can be stored in this
	 * registry
	 */
	public void setRegistrySize(int registrySize) {
		synchronized (this) {
			this.registrySize = registrySize;
		}
	}

	@Override
	public int getMRUDEntryCount() {
		synchronized (this) {
			return entries.size();
		}
	}

	@Override
	public MRUDEntry getMRUDEntryAt(int index) {
		synchronized (this) {
			return entries.elementAt(index);
		}
	}

	@Override
	public int getIndexOfMRUDEntry(MRUDEntry mrud) {
		synchronized (this) {
			return entries.indexOf(mrud);
		}
	}

	@Override
	public int getMRUDEntryCount(ManagedDocumentType type) {
		synchronized (this) {
			Vector<MRUDEntry> vector = entryVectorsByType.get(type);
			if (vector != null) {
				return vector.size();
			}
		}
		return 0;
	}

	@Override
	public MRUDEntry getMRUDEntryAt(ManagedDocumentType type, int index) {
		synchronized (this) {
			Vector<MRUDEntry> vector = entryVectorsByType.get(type);
			if (vector != null) {
				return vector.elementAt(index);
			}
		}
		return null;
	}

	@Override
	public int getIndexOfMRUDEntry(ManagedDocumentType type, MRUDEntry mrud) {
		synchronized (this) {
			Vector<MRUDEntry> vector = entryVectorsByType.get(type);
			if (vector != null) {
				return vector.indexOf(mrud);
			}
		}
		return -1;
	}

	/**
	 * Removes all {@link MRUDEntry entries} from this registry.
	 */
	private void clear() {
		entries.clear();
		entriesByFile.clear();
		entryVectorsByType.clear();
	}

	/**
	 * Adds a new {@link MRUDEntry entry} to this registry.
	 * In order to do that:
	 * <ul>
	 * <li>if there is an entry describing the same file removes it,</li>
	 * <li>if this registry is full (number of entries equals
	 * {@link #registrySize}) the entry with the oldest
	 * {@link MRUDEntry#getLastTimeOpened() last open time} is removed</li>
	 * <li>the entry is added to the {@link #entries vector} with all entries,
	 * </li>
	 * <li>the entry is added to the {@link #entriesByFile map} associating
	 * files with the entries in which they are described,</li>
	 * <li>the entry is added to the vector with entries for a specified
	 * {@link ManagedDocumentType type} of a {@link Document document}.
	 * If such vector doesn't exist it is created and added to
	 * {@link #entryVectorsByType},</li>
	 * </ul>
	 * @param mrud the entry to be added
	 * @return the vector with entries for a specified type of a document
	 */
	private Vector<MRUDEntry> registerMRUDEntryInternal(MRUDEntry mrud) {

		String path = mrud.getPath();
		File file = (new File(path)).getAbsoluteFile();
		MRUDEntry existingMrud = entriesByFile.get(file);
		if (existingMrud != null) {
			int exIndex = entries.indexOf(existingMrud);
			entries.remove(exIndex);
			Vector<MRUDEntry> exVector = entryVectorsByType.get(existingMrud.getDocumentType());
			int exInTypeIndex = -1;
			if (exVector != null) {
				exInTypeIndex = exVector.indexOf(existingMrud);
				exVector.remove(exInTypeIndex);
			}
			fireMrudEntryRemoved(existingMrud, exIndex, exInTypeIndex);
		}

		if (entries.size() == registrySize) {
			// something needs to be removed
			if (!isMainSorted) {
				Collections.sort(entries,comparator);
				isMainSorted = true;
			}
			MRUDEntry dumpedMrud = entries.lastElement();
			int duIndex = entries.indexOf(dumpedMrud);
			Vector<MRUDEntry> duVector = entryVectorsByType.get(dumpedMrud.getDocumentType());
			int duInTypeIndex = -1;
			if (duVector != null) {
				duInTypeIndex = duVector.indexOf(dumpedMrud);
				duVector.remove(duInTypeIndex);
			}
			entries.remove(duIndex);
			fireMrudEntryRemoved(dumpedMrud, duIndex, duInTypeIndex);
		}

		entriesByFile.put(file,mrud);
		entries.add(mrud);
		isMainSorted = false;

		Vector<MRUDEntry> vector = entryVectorsByType.get(mrud.getDocumentType());
		if (vector == null) {
			vector = new Vector<MRUDEntry>(100);
			entryVectorsByType.put(mrud.getDocumentType(), vector);
		}
		vector.add(mrud);

		return vector;

	}

	@Override
	public void registerMRUDEntry(MRUDEntry mrud) {

		synchronized (this) {

			if (entries.contains(mrud)) {
				return;
			}

			Vector<MRUDEntry> vector = registerMRUDEntryInternal(mrud);

			Collections.sort(entries, comparator);
			isMainSorted = true;

			Collections.sort(vector,comparator);

			fireMrudEntryRegistered(mrud, entries.indexOf(mrud), vector.indexOf(mrud));

		}

	}

	/**
	 * Writes all {@link MRUDEntry entries} to the specified file.
	 * @param file the file
	 * @throws IOException if there is an I/O error while writing
	 * entries to the file
	 */
	public void writeToPersistence(File file) throws IOException {

		MRUDEntry[] mruds;
		synchronized (this) {
			mruds = new MRUDEntry[entries.size()];
			entries.toArray(mruds);
		}

		MRUDConfiguration config = new MRUDConfiguration(mruds);
		config.writeToXML((file == null) ? config.getStandardFile(profileDir) : file, streamer);

	}

	/**
	 * Reads {@link MRUDEntry entries} from the specified file.
	 * If the number of read entries is greater then the
	 * {@link #getRegistrySize() size} of this registry, only first {@code size}
	 * entries is used.
	 * @param file the file
	 * @throws IOException if an I/O error occurs while reading entries from
	 * file
	 */
	public void readFromPersistence(File file) throws IOException {

		MRUDConfiguration config = new MRUDConfiguration();;
		config.readFromXML((file == null) ? config.getStandardFile(profileDir) : file, streamer);

		synchronized (this) {
			clear();
			MRUDEntry[] mruds = config.getMruds();
			for (int i=0; i<Math.min(mruds.length, registrySize); i++) {
				registerMRUDEntryInternal(mruds[i]);
			}

			Collections.sort(entries, comparator);
			isMainSorted = true;

			for (Vector<MRUDEntry> vector : entryVectorsByType.values()) {
				Collections.sort(vector,comparator);
			}
		}

	}

	/**
	 * Returns the profile directory.
	 * @return the profile directory
	 */
	public File getProfileDir() {
		return profileDir;
	}

	/**
	 * Sets the profile directory.
	 * @param profileDir the profile directory
	 */
	public void setProfileDir(File profileDir) {
		this.profileDir = profileDir;
	}

	/**
	 * Returns the stream used to read stored {@link MRUDEntry entires} from
	 * file.
	 * @return the stream used to read stored {@link MRUDEntry entires} from
	 * file
	 */
	public XStream getStreamer() {
		return streamer;
	}

	/**
	 * Sets the stream used to read stored {@link MRUDEntry entires} from file.
	 * @param streamer the stream used to read stored {@link MRUDEntry entires}
	 * from file
	 */
	public void setStreamer(XStream streamer) {
		this.streamer = streamer;
	}

	/**
	 * Informs all {@link MRUDRegistryListener listeners} that the
	 * {@link MRUDEntry entry} was added to this registry.
	 * @param entry the added entry
	 * @param index the index of this entry in the vector of all entries
	 * @param inTypeIndex the index of this entry in the vector of entries
	 * for a specified {@link ManagedDocumentType type} of a {@link Document
	 * document}
	 */
	protected void fireMrudEntryRegistered(MRUDEntry entry, int index, int inTypeIndex) {
		Object[] listeners = listenerList.getListenerList();
		MRUDRegistryEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==MRUDRegistryListener.class) {
				if (e == null) {
					e = new MRUDRegistryEvent(this,entry,index,inTypeIndex);
				}
				((MRUDRegistryListener)listeners[i+1]).mrudEntryRegistered(e);
			}
		}
	}

	/**
	 * Informs all {@link MRUDRegistryListener listeners} that the
	 * {@link MRUDEntry entry} was removed from this registry.
	 * @param entry the removed entry
	 * @param index the index of this entry in the vector of all entries
	 * @param inTypeIndex the index of this entry in the vector of entries
	 * for a specified {@link ManagedDocumentType type} of a {@link Document
	 * document}
	 */
	protected void fireMrudEntryRemoved(MRUDEntry entry, int index, int inTypeIndex) {
		Object[] listeners = listenerList.getListenerList();
		MRUDRegistryEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==MRUDRegistryListener.class) {
				if (e == null) {
					e = new MRUDRegistryEvent(this,entry,index,inTypeIndex);
				}
				((MRUDRegistryListener)listeners[i+1]).mrudEntryRemoved(e);
			}
		}
	}

	@Override
	public void addMRUDRegistryListener(MRUDRegistryListener listener) {
		listenerList.add(MRUDRegistryListener.class, listener);
	}

	@Override
	public void removeMRUDRegistryListener(MRUDRegistryListener listener) {
		listenerList.remove(MRUDRegistryListener.class, listener);
	}

	/**
	 * Compares {@link MRUDEntry entries} due to their last open time.
	 * The entry is smaller then another entry if it was last open after that
	 * entry.
	 */
	class MRUDComparator implements Comparator<MRUDEntry> {

		/**
		 * Compares two {@link MRUDEntry entries}.
		 * @param o1 first entry
		 * @param o2 second entry
		 * @return {@code 0} if the entries have the same last open time,
		 * value grater then {@code 0} if the second entry was last time opened
		 * before first, value smaller then {@code 0} otherwise
		 */
		@Override
		public int compare(MRUDEntry o1, MRUDEntry o2) {
			// note the unary minus!
			return -o1.getLastTimeOpened().compareTo(o2.getLastTimeOpened());
		}

	}

}

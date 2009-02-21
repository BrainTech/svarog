/* DefaultMRUDRegistry.java created 2007-09-12
 * 
 */
package org.signalml.app.document;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.EventListenerList;

import org.signalml.app.config.MRUDConfiguration;

import com.thoughtworks.xstream.XStream;

/** DefaultMRUDRegistry
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DefaultMRUDRegistry implements MRUDRegistry {

	private Vector<MRUDEntry> entries = new Vector<MRUDEntry>(100);
	private Map<ManagedDocumentType,Vector<MRUDEntry>> entryVectorsByType = new HashMap<ManagedDocumentType,Vector<MRUDEntry>>(10);
	private Map<File,MRUDEntry> entriesByFile = new HashMap<File,MRUDEntry>(100);
	
	private int registrySize = 50;
	private MRUDComparator comparator;
	
	private boolean isMainSorted = true;
	
	private File profileDir;
	private XStream streamer;
	
	private EventListenerList listenerList = new EventListenerList();
	
	public DefaultMRUDRegistry() {
		comparator = new MRUDComparator();
	}

	public int getRegistrySize() {
		synchronized( this ) {
			return registrySize;
		}
	}

	public void setRegistrySize(int registrySize) {
		synchronized( this ) {
			this.registrySize = registrySize;
		}
	}	
	
	@Override
	public int getMRUDEntryCount() {
		synchronized( this ) {
			return entries.size();
		}
	}
	
	@Override
	public MRUDEntry getMRUDEntryAt(int index) {		
		synchronized( this ) {
			return entries.elementAt(index);
		}
	}
	
	@Override
	public int getIndexOfMRUDEntry(MRUDEntry mrud) {
		synchronized( this ) {
			return entries.indexOf(mrud);
		}
	}
	
	@Override
	public int getMRUDEntryCount(ManagedDocumentType type) {
		synchronized( this ) {
			Vector<MRUDEntry> vector = entryVectorsByType.get(type);
			if( vector != null ) {
				return vector.size();
			}
		}
		return 0;
	}

	@Override
	public MRUDEntry getMRUDEntryAt(ManagedDocumentType type, int index) {
		synchronized( this ) {
			Vector<MRUDEntry> vector = entryVectorsByType.get(type);
			if( vector != null ) {
				return vector.elementAt(index);
			}
		}
		return null;
	}

	@Override
	public int getIndexOfMRUDEntry(ManagedDocumentType type, MRUDEntry mrud) {
		synchronized( this ) {
			Vector<MRUDEntry> vector = entryVectorsByType.get(type);
			if( vector != null ) {
				return vector.indexOf(mrud);
			}
		}
		return -1;
	}
	
	private void clear() {
		entries.clear();
		entriesByFile.clear();
		entryVectorsByType.clear();
	}
	
	private Vector<MRUDEntry> registerMRUDEntryInternal(MRUDEntry mrud) {

		String path = mrud.getPath();
		File file = (new File(path)).getAbsoluteFile();
		MRUDEntry existingMrud = entriesByFile.get(file);
		if( existingMrud != null ) {			
			int exIndex = entries.indexOf(existingMrud);
			entries.remove(exIndex);
			Vector<MRUDEntry> exVector = entryVectorsByType.get(existingMrud.getDocumentType());
			int exInTypeIndex = -1;
			if( exVector != null ) {
				exInTypeIndex = exVector.indexOf(existingMrud);
				exVector.remove(exInTypeIndex);
			}
			fireMrudEntryRemoved(existingMrud, exIndex, exInTypeIndex);
		}
		
		if( entries.size() == registrySize ) {
			// something needs to be removed
			if( !isMainSorted ) {
				Collections.sort(entries,comparator);
				isMainSorted = true;
			}
			MRUDEntry dumpedMrud = entries.lastElement();
			int duIndex = entries.indexOf(dumpedMrud);
			Vector<MRUDEntry> duVector = entryVectorsByType.get(dumpedMrud.getDocumentType());
			int duInTypeIndex = -1;
			if( duVector != null ) {
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
		if( vector == null ) {
			vector = new Vector<MRUDEntry>(100);
			entryVectorsByType.put(mrud.getDocumentType(), vector);
		}
		vector.add(mrud);
				
		return vector;
		
	}
	
	@Override
	public void registerMRUDEntry(MRUDEntry mrud) {

		synchronized( this ) {

			if( entries.contains(mrud) ) {
				return;
			}
			
			Vector<MRUDEntry> vector = registerMRUDEntryInternal(mrud);
			
			Collections.sort(entries, comparator);
			isMainSorted = true;

			Collections.sort(vector,comparator);
			
			fireMrudEntryRegistered(mrud, entries.indexOf(mrud), vector.indexOf(mrud));
			
		}
		
	}
	
	public void writeToPersistence(File file) throws IOException {
		
		MRUDEntry[] mruds;
		synchronized( this ) {
			mruds = new MRUDEntry[entries.size()];
			entries.toArray(mruds);
		}

		MRUDConfiguration config = new MRUDConfiguration(mruds);
		config.writeToXML( (file == null) ? config.getStandardFile( profileDir ) : file, streamer );
		
	}
	
	public void readFromPersistence(File file) throws IOException {

		MRUDConfiguration config = new MRUDConfiguration();;
		config.readFromXML( (file == null) ? config.getStandardFile( profileDir ) : file, streamer);
			
		synchronized( this ) {
			clear();
			MRUDEntry[] mruds = config.getMruds();
			for( int i=0; i<Math.min( mruds.length, registrySize ); i++ ) {
				registerMRUDEntryInternal(mruds[i]);
			}

			Collections.sort(entries, comparator);
			isMainSorted = true;
			
			for( Vector<MRUDEntry> vector : entryVectorsByType.values() ) {
				Collections.sort(vector,comparator);
			}
		}
		
	}

	public File getProfileDir() {
		return profileDir;
	}

	public void setProfileDir(File profileDir) {
		this.profileDir = profileDir;
	}

	public XStream getStreamer() {
		return streamer;
	}

	public void setStreamer(XStream streamer) {
		this.streamer = streamer;
	}
	
	protected void fireMrudEntryRegistered(MRUDEntry entry, int index, int inTypeIndex) {
		Object[] listeners = listenerList.getListenerList();
		MRUDRegistryEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==MRUDRegistryListener.class) {
	        	 if( e == null ) {
	        		 e = new MRUDRegistryEvent(this,entry,index,inTypeIndex);
	        	 }
	             ((MRUDRegistryListener)listeners[i+1]).mrudEntryRegistered(e);
	         }
	     }
	}

	protected void fireMrudEntryRemoved(MRUDEntry entry, int index, int inTypeIndex) {
		Object[] listeners = listenerList.getListenerList();
		MRUDRegistryEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
	         if (listeners[i]==MRUDRegistryListener.class) {
	        	 if( e == null ) {
	        		 e = new MRUDRegistryEvent(this,entry,index,inTypeIndex);
	        	 }
	             ((MRUDRegistryListener)listeners[i+1]).mrudEntryRemoved(e);
	         }
	     }
	}
	
	public void addMRUDRegistryListener(MRUDRegistryListener listener) {
		listenerList.add(MRUDRegistryListener.class, listener);
	}

	public void removeMRUDRegistryListener(MRUDRegistryListener listener) {
		listenerList.remove(MRUDRegistryListener.class, listener);
	}
	
	class MRUDComparator implements Comparator<MRUDEntry> {

		@Override
		public int compare(MRUDEntry o1, MRUDEntry o2) {
			// note the unary minus!
			return -o1.getLastTimeOpened().compareTo(o2.getLastTimeOpened());
		}
		
	}
	
}

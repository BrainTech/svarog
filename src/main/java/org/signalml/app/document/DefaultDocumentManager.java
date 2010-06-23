/* DefaultDocumentManager.java created 2007-09-10
 * 
 */
package org.signalml.app.document;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.signalml.exception.SanityCheckException;

/** DefaultDocumentManager
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DefaultDocumentManager implements DocumentManager {

	protected static final Logger logger = Logger.getLogger(DefaultDocumentManager.class);
	
	private Vector<Document> documents = new Vector<Document>(100,100);
	
	private Map<File,Document> documentsByFile = new HashMap<File,Document>(100);
	
	private Map<ManagedDocumentType,Vector<Document>> documentVectorsByType = new HashMap<ManagedDocumentType,Vector<Document>>(10);
			
	private EventListenerList listenerList = new EventListenerList();
	
	@Override
	public boolean isAllSaved() {
		synchronized( this ) {
			for( Document document : documents ) {
				if( document instanceof MutableDocument ) {
					MutableDocument md = (MutableDocument) document;
					if( !md.isSaved() ) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	@Override
	public int getDocumentCount() {
		synchronized( this ) {
			return documents.size();
		}
	}
	
	@Override
	public Document getDocumentAt(int index) {
		synchronized( this ) {
			return documents.elementAt(index);
		}
	}
	
	@Override
	public int getIndexOfDocument(Document document) {
		synchronized( this ) {
			return documents.indexOf(document);
		}
	}
	
	@Override
	public Iterator<Document> iterator() {
		synchronized( this ) {
			return documents.iterator();
		}
	}
	
	@Override
	public Document getDocumentByFile(File file) {
		synchronized( this ) {
			return documentsByFile.get(file);
		}
	}
			
	@Override
	public void addDocument( Document document ) {
		
		synchronized( this ) {
			if( documents.contains(document) ) {
				logger.info("Document already in the manager" );
				return;
			}
			
			if( document instanceof FileBackedDocument ) {				
				FileBackedDocument fbd = (FileBackedDocument) document;
				File file = fbd.getBackingFile();
				if( file != null ) {
					File absFile = file.getAbsoluteFile();
					if( documentsByFile.containsKey(absFile) ) {
						throw new SanityCheckException("Sanity check failed, the same path already open");
					}
					
					documentsByFile.put(absFile, document);			
				}
			}
			
			documents.add(document);
					
			ManagedDocumentType type = ManagedDocumentType.getForClass(document.getClass());
			int inTypeIndex = -1;
			if( type != null ) {
				Vector<Document> vector = documentVectorsByType.get(type);
				if( vector == null ) {
					vector = new Vector<Document>(100);
					documentVectorsByType.put(type, vector);
				}
				vector.add(document);
				inTypeIndex = vector.indexOf(document);
			}
			
			fireDocumentAdded(document, documents.indexOf(document), inTypeIndex);
			
		}
		
	}

	@Override
	public void removeDocumentAt( int index ) {
		
		synchronized( this ) {
			removeDocumentInternal(documents.elementAt(index));
		}
		
	}
	
	@Override
	public void removeDocument( Document document ) {
		
		synchronized( this ) {
			if( !documents.contains(document) ) {
				return;
			}
					
			removeDocumentInternal(document);
		}
		
	}
	
	private void removeDocumentInternal(Document document) {
		
		if( document instanceof FileBackedDocument ) {				
			FileBackedDocument fbd = (FileBackedDocument) document;
			File file = fbd.getBackingFile();
			if( file != null ) {
				documentsByFile.remove(file.getAbsoluteFile());
			}
		}
		
		int index = documents.indexOf(document);

		ManagedDocumentType type = ManagedDocumentType.getForClass(document.getClass());
		int inTypeIndex = -1;
		if( type != null ) {
			Vector<Document> vector = documentVectorsByType.get(type);			
			if( vector != null ) {
				inTypeIndex = vector.indexOf(document);
				vector.remove(document);
			}
		}

		documents.remove(document);
				
		
		fireDocumentRemoved(document, index, inTypeIndex);
		
	}
		
	
	@Override
	public void onDocumentPathChange( Document document, File oldFile, File newFile ) {
		
		synchronized( this ) {

			if( !documents.contains(document) ) {
				return;
			}
			
			if( oldFile != null ) {
				documentsByFile.remove(oldFile.getAbsoluteFile());
			}
			
			if( newFile != null ) {
				documentsByFile.put(newFile.getAbsoluteFile(), document);
			}
			
			ManagedDocumentType type = ManagedDocumentType.getForClass(document.getClass());
			int inTypeIndex = -1;
			if( type != null ) {
				Vector<Document> vector = documentVectorsByType.get(type);			
				if( vector != null ) {
					inTypeIndex = vector.indexOf(document);
				}
			}
			
			fireDocumentPathChanged(document, documents.indexOf(document), inTypeIndex);
			
		}
				
	}
	
	@Override
	public int getDocumentCount(ManagedDocumentType type) {

		synchronized( this ) {
			Vector<Document> vector = documentVectorsByType.get(type);
			if( vector != null ) {
				return vector.size();
			}
		}
		
		return 0;
		
	}
	
	@Override
	public Document getDocumentAt(ManagedDocumentType type, int index) {

		synchronized( this ) {
			Vector<Document> vector = documentVectorsByType.get(type);
			if( vector != null ) {
				return vector.elementAt(index);
			}
		}
		
		return null;
		
	}

	@Override
	public int getIndexOfDocument(ManagedDocumentType type, Document document) {
		
		synchronized( this ) {
			Vector<Document> vector = documentVectorsByType.get(type);
			if( vector != null ) {
				return vector.indexOf(document);
			}
		}

		return -1;
		
	}
	
	protected void fireDocumentAdded(Document document, int index, int inTypeIndex) {
		Object[] listeners = listenerList.getListenerList();
		DocumentManagerEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==DocumentManagerListener.class) {
				 if( e == null ) { 
					 e = new DocumentManagerEvent(this,document,index,inTypeIndex);
				 }
				 ((DocumentManagerListener)listeners[i+1]).documentAdded(e);
			 }
		 }
	}
	
	protected void fireDocumentRemoved(Document document, int index, int inTypeIndex) {
		Object[] listeners = listenerList.getListenerList();
		DocumentManagerEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==DocumentManagerListener.class) {
				 if( e == null ) { 
					 e = new DocumentManagerEvent(this,document,index,inTypeIndex);
				 }
				 ((DocumentManagerListener)listeners[i+1]).documentRemoved(e);
			 }
		 }
	}
	
	protected void fireDocumentPathChanged(Document document, int index, int inTypeIndex) {
		Object[] listeners = listenerList.getListenerList();
		DocumentManagerEvent e = null;
		for (int i = listeners.length-2; i>=0; i-=2) {
			 if (listeners[i]==DocumentManagerListener.class) {
				 if( e == null ) { 
					 e = new DocumentManagerEvent(this,document,index,inTypeIndex);
				 }
				 ((DocumentManagerListener)listeners[i+1]).documentPathChanged(e);
			 }
		 }
	}
	
	public void addDocumentManagerListener(DocumentManagerListener listener) {
		synchronized( this ) {
			listenerList.add(DocumentManagerListener.class, listener);
		}
	}

	public void removeDocumentManagerListener(DocumentManagerListener listener) {
		synchronized( this ) {
			listenerList.remove(DocumentManagerListener.class, listener);
		}
	}

}

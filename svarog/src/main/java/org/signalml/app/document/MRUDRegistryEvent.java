/* MRUDRegistryEvent.java created 2007-09-21
 *
 */

package org.signalml.app.document;

import java.util.EventObject;

import org.signalml.plugin.export.signal.Document;

/**
 * The event associated with a change in a {@link MRUDRegistry}
 * (addition or removal of an {@link MRUDEntry entry}).
 * Contains 3 fields:
 * <ul>
 * <li>the entry connected with the change,</li>
 * <li>the index of the entry in the collection of all entries in the
 * registry,</li>
 * <li>the index of the entry in the collection of entries for a specified
 * {@link ManagedDocumentType type} of a {@link Document}.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MRUDRegistryEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link MRUDEntry entry} connected with the change
	 */
	private MRUDEntry entry;

	/**
	 * the index of the {@link MRUDEntry entry} in the collection of all
	 * entries in the registry
	 */
	private int index;
	/**
	 * the index of the {@link MRUDEntry entry} in the collection of entries
	 * for a specified {@link ManagedDocumentType type} of a {@link Document}
	 */
	private int inTypeIndex;

	/**
	 * Constructor. Sets all parameters of this event.
	 * @param source the {@link MRUDRegistry registry} in which the change
	 * occurred
	 * @param entry the {@link MRUDEntry entry} connected with the change
	 * @param index the index of the entry in the collection of all
	 * entries in the registry
	 * @param inTypeIndex  the index of the entry in the collection of entries
	 * for a specified {@link ManagedDocumentType type} of a {@link Document}
	 */
	public MRUDRegistryEvent(MRUDRegistry source, MRUDEntry entry, int index, int inTypeIndex) {
		super(source);
		this.entry = entry;
		this.index = index;
		this.inTypeIndex = inTypeIndex;
	}

	//FIXME there is no manager in this event - probably instead of this function there should be a function {@code getMRUDRegistry()}
	public DocumentManager getDocumentManager() {
		return (DocumentManager) source;
	}

	/**
	 * Returns the {@link MRUDEntry entry} connected with the change.
	 * @return the entry connected with the change
	 */
	public MRUDEntry getEntry() {
		return entry;
	}

	/**
	 * Returns the index of the {@link MRUDEntry entry} in the collection of
	 * all entries in the {@link MRUDRegistry registry}.
	 * @return the index of the entry in the collection of all
	 * entries in the registry
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns the index of the {@link MRUDEntry entry} in the collection of
	 * entries for a specified {@link ManagedDocumentType type} of a
	 * {@link Document}.
	 * @return the index of the entry in the collection of entries
	 * for a specified type} of a document
	 */
	public int getInTypeIndex() {
		return inTypeIndex;
	}

}

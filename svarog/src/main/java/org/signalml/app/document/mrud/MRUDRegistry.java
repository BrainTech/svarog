/* MRUDRegistry.java created 2007-09-12
 *
 */
package org.signalml.app.document.mrud;

import org.signalml.app.document.ManagedDocumentType;

/**
 * Interface for a cache of {@link MRUDEntry file descriptions}.
 * Organizes the descriptions by the {@link ManagedDocumentType types} of
 * documents that can be created from described files.
 * Allows to:
 * <ul>
 * <li>get the number of all entries in this registry and the number
 * entries for a given type of a document,</li>
 * <li>get the entry of a given "global" index in this registry and of
 * a given in-type index,</li>
 * <li>get the in-type or "global" index of a given entry,</li>
 * <li>add an entry,</li>
 * <li>and and remove {@link MRUDRegistryListener listeners}.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MRUDRegistry {

	/**
	 * Returns the number of all {@link MRUDEntry entries} in this registry.
	 * @return the number of all entries in this registry
	 */
	int getMRUDEntryCount();

	/**
	 * Returns the {@link MRUDEntry entry} of a given "global" index (the index in the
	 * collection of all entries) in this registry.
	 * @param index the index of an entry
	 * @return the entry of a given index
	 */
	MRUDEntry getMRUDEntryAt(int index);

	/**
	 * Returns the "global" index (the index in the collection of all
	 * {@link MRUDEntry entries}) of a given entry.
	 * @param mrud the entry
	 * @return the index of the entry
	 */
	int getIndexOfMRUDEntry(MRUDEntry mrud);

	/**
	 * Returns the number of entries for a given {@link ManagedDocumentType
	 * type} of a document.
	 * @param type the type of a document
	 * @return the number of entries of that type in this registry
	 */
	int getMRUDEntryCount(ManagedDocumentType type);

	/**
	 * Returns the {@link MRUDEntry entry} of a given in-type index (the index
	 * in the collection of entries for a given {@link ManagedDocumentType
	 * type} of a document) in this registry.
	 * @param type the type of a document described by entry
	 * @param index the in-type index of an entry
	 * @return the entry of a given index
	 */
	MRUDEntry getMRUDEntryAt(ManagedDocumentType type, int index);

	/**
	 * Returns the in-type index (the index in the collection of entries for
	 * a given {@link ManagedDocumentType type} of a document) of a given entry.
	 * @param type the type of a document described by entry
	 * @param mrud the entry
	 * @return the index in-type of the entry
	 */
	int getIndexOfMRUDEntry(ManagedDocumentType type, MRUDEntry mrud);

	/**
	 * Adds an {@link MRUDEntry entry} to this registry.
	 * If the size of the registry is exceeded, the oldest entry (access times)
	 * is removed.
	 * @param mrud the entry to be added
	 */
	void registerMRUDEntry(MRUDEntry mrud);

	/**
	 * Adds a {@link MRUDRegistryListener listener} to this registry.
	 * @param listener the listener to be added
	 */
	void addMRUDRegistryListener(MRUDRegistryListener listener);

	/**
	 * Removes a {@link MRUDRegistryListener listener} from this registry.
	 * @param listener the listener to be removed
	 */
	void removeMRUDRegistryListener(MRUDRegistryListener listener);

}

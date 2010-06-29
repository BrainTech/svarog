/* MRUDRegistry.java created 2007-09-12
 *
 */
package org.signalml.app.document;

/** MRUDRegistry
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MRUDRegistry {

	int getMRUDEntryCount();
	MRUDEntry getMRUDEntryAt(int index);
	int getIndexOfMRUDEntry(MRUDEntry mrud);

	int getMRUDEntryCount(ManagedDocumentType type);
	MRUDEntry getMRUDEntryAt(ManagedDocumentType type, int index);
	int getIndexOfMRUDEntry(ManagedDocumentType type, MRUDEntry mrud);

	void registerMRUDEntry(MRUDEntry mrud);

	void addMRUDRegistryListener(MRUDRegistryListener listener);
	void removeMRUDRegistryListener(MRUDRegistryListener listener);

}

/* MRUDRegistryListener.java created 2007-09-21
 *
 */

package org.signalml.app.document;

import java.util.EventListener;


/**
 * Interface for a listener on changes that occur in a {@link MRUDRegistry}.
 * These changes include:
 * <ul>
 * <li>addition of an {@link MRUDEntry entry},</li>
 * <li>removal of an entry.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface MRUDRegistryListener extends EventListener {

	/**
	 * Invoked when an {@link MRUDEntry entry} is added to the registry.
	 * @param e the {@link MRUDRegistryEvent event} with the parameters of
	 * the change.
	 */
	void mrudEntryRegistered(MRUDRegistryEvent e);

	/**
	 * Invoked when an {@link MRUDEntry entry} is removed from the registry.
	 * @param e the {@link MRUDRegistryEvent event} with the parameters of
	 * the change.
	 */
	void mrudEntryRemoved(MRUDRegistryEvent e);

}

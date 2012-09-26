/**
 *
 */
package org.signalml.plugin.export;

import org.signalml.plugin.export.change.SvarogAccessChangeSupport;
import org.signalml.plugin.export.config.SvarogAccessConfig;
import org.signalml.plugin.export.i18n.SvarogAccessI18n;
import org.signalml.plugin.export.method.SvarogAccessMethod;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.export.resources.SvarogAccessResources;


/**
 * This interface is used by plug-ins to communicate with Svarog.
 * Every plug-in during the {@link Plugin#register(SvarogAccess) registration}
 * gets the instance of this interface and from it can obtain the sub-interfaces:
 * <ul>
 * <li>{@link SvarogAccessChangeSupport}</li>
 * <li>{@link SvarogAccessGUI}</li>
 * <li>{@link SvarogAccessSignal}</li>
 * </ul>
 * @author Marcin Szumski
 * @author Stanislaw Findeisen (Eisenbits)
 */
public interface SvarogAccess {

	/**
	 * Returns the interface to access Svarog i18n features.
	 * @return the interface to access Svarog i18n features
	 */
	SvarogAccessI18n getI18nAccess();

	/**
	 * Returns the interface to access GUI features.
	 * @return the interface to access GUI features
	 */
	SvarogAccessGUI getGUIAccess();

	/**
	 * Returns Svarog configuration facade.
	 * @return Svarog configuration facade
	 */
	SvarogAccessConfig getConfigAccess();

	/**
	 * Returns the methods and tasks in Svarog core facade.
	 * @return the methods and tasks in Svarog core facade
	 */
	SvarogAccessMethod getMethodAccess();

	/**
	 * Returns the interface to access logic features.
	 * @return the interface to access logic features
	 */
	SvarogAccessSignal getSignalAccess();

	/**
	 * Returns the interface to listen of changes in the program.
	 * @return the interface to listen of changes
	 */
	SvarogAccessChangeSupport getChangeSupport();

	/**
	 * Returns the interface to access classpath resources.
	 * @return the interface to access classpath resources
	 */
	SvarogAccessResources getResourcesAccess();
}

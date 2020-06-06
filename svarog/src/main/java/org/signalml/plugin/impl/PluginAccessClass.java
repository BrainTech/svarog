package org.signalml.plugin.impl;

import org.apache.log4j.Logger;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.change.SvarogAccessChangeSupport;
import org.signalml.plugin.export.config.SvarogAccessConfig;
import org.signalml.plugin.export.method.SvarogAccessMethod;
import org.signalml.plugin.export.resources.SvarogAccessResources;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.impl.change.SvarogAccessChangeSupportImpl;

/**
 * Implementation of {@link SvarogAccess} interface.
 * Allows to return only one, {@link #getSharedInstance() shared instance}
 * of this class.
 * Passes the information that the {@link #setInitializationPhaseEnd
 * initialization phase} has finished to the {@link GUIAccessImpl GUI access}
 * and the information that the application is {@link #onClose() closing}
 * to {@link SvarogAccessChangeSupportImpl change support}.
 *
 * @author Marcin Szumski
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class PluginAccessClass implements SvarogAccess {

	private static final Logger logger = Logger.getLogger(PluginAccessClass.class);

	/**
	 * the manager of Svarog elements
	 */
	private static ViewerElementManager manager = null;

	private final Class _klass;

	private final SvarogAccessResourcesImpl resourcesAccessImpl;

	/**
	 * access to GUI features of Svarog
	 */
	protected static final GUIAccessImpl guiAccess = GUIAccessImpl.getInstance();


	/** Svarog methods and tasks facade. */
	private static final MethodAccessImpl methodAccessImpl = MethodAccessImpl.getInstance();

	/** Svarog configuration facade. */
	private static final ConfigAccessImpl configAccessImpl = ConfigAccessImpl.getInstance();

	/**
	 * access to ordinary features of Svarog
	 */
	private static final SignalsAccessImpl signalsAccess = SignalsAccessImpl.getInstance();
	/**
	 * access to listen on changes in Svarog
	 */
	private static final SvarogAccessChangeSupportImpl changeSupport = SvarogAccessChangeSupportImpl.getInstance();

	/**
	 * Constructor. Creates child accesses.
	 */
	public PluginAccessClass(Class<? extends Plugin> klass) {
		this._klass = klass;
		this.resourcesAccessImpl = new SvarogAccessResourcesImpl(this._klass);
	}

	/**
	 * Returns the implementation of {@link SvarogAccessGUI}.
	 * @return the implementation of GUI access
	 */
	public static GUIAccessImpl getGUIImpl() {
		return guiAccess;
	}

	/**
	 * @param manager the element manager to set
	 */
	public static void setManager(ViewerElementManager manager) {
		try {
			assert(PluginAccessClass.manager == null);
			PluginAccessClass.manager = manager;
			signalsAccess.setViewerElementManager(manager);
			guiAccess.setViewerElementManager(manager);
			changeSupport.setViewerElementManager(manager);
			configAccessImpl.setViewerElementManager(manager);
			methodAccessImpl.setViewerElementManager(manager);
		} catch (Exception e) {
			logger.error("error in plug-in interface while setting element manager");
			logger.error("", e);
		}
	}


	/**
	 * @return the element manager
	 */
	public static ViewerElementManager getManager() {
		return PluginAccessClass.manager;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccess#getGUIAccess()
	 */
	@Override
	public SvarogAccessGUI getGUIAccess() {
		return guiAccess;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccess#getSignalAccess()
	 */
	@Override
	public SvarogAccessSignal getSignalAccess() {
		return signalsAccess;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccess#getChangeSupport()
	 */
	@Override
	public SvarogAccessChangeSupport getChangeSupport() {
		return changeSupport;
	}

	/**
	 * @param initializationPhase true if it is an initialization phase,
	 * false otherwise
	 */
	public static void setInitializationPhaseEnd() {
		guiAccess.setInitializationPhaseEnd();
	}

	/**
	 * Calls {@link SvarogAccessChangeSupportImpl#onClose()}.
	 */
	public static void onClose() {
		changeSupport.onClose();
	}

	@Override
	public SvarogAccessMethod getMethodAccess() {
		return methodAccessImpl;
	}

	@Override
	public SvarogAccessConfig getConfigAccess() {
		return configAccessImpl;
	}

	@Override
	public SvarogAccessResources getResourcesAccess() {
		return this.resourcesAccessImpl;
	}
}

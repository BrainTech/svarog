package org.signalml.plugin.impl;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.SvarogI18n;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.change.SvarogAccessChangeSupport;
import org.signalml.plugin.export.config.SvarogAccessConfig;
import org.signalml.plugin.export.i18n.SvarogAccessI18n;
import org.signalml.plugin.export.method.SvarogAccessMethod;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.impl.change.ChangeSupportImpl;
import org.signalml.plugin.loader.PluginHead;

/**
 * Implementation of {@link SvarogAccess} interface.
 * Allows to return only one, {@link #getSharedInstance() shared instance}
 * of this class.
 * Passes the information that the {@link #setInitializationPhaseEnd
 * initialization phase} has finished to the {@link GUIAccessImpl GUI access}
 * and the information that the application is {@link #onClose() closing}
 * to {@link ChangeSupportImpl change support}. 
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
	
	private SvarogI18n i18nAccessImpl;
	
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
	private static final ChangeSupportImpl changeSupport = ChangeSupportImpl.getInstance();

	/**
	 * The plugin map.
	 */
	private static HashMap<Object, PluginHead> pluginMap
		= new HashMap<Object, PluginHead>();
	
	/**
	 * Constructor. Creates child accesses.
	 */
	public PluginAccessClass(PluginHead head){
		this.i18nAccessImpl = new SvarogI18n(head.getPluginObj().getClass(), "xxx");
	}
	
	/**
	 * Adds given plugin to {@link #pluginMap}.
	 * @param head plugin to add
	 */
	public synchronized static void addPlugin(PluginHead head) {
	    if (pluginMap.containsKey(head)) {
		    throw new IllegalArgumentException("Duplicate plugin auth! (" + head + ")");
	    }
	    pluginMap.put(head, head);
	    logger.debug("addPlugin: " + head + " --> " + head);
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
			e.printStackTrace();
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
	 * Calls {@link ChangeSupportImpl#onClose()}.
	 */
	public static void onClose(){
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
    public SvarogAccessI18n getI18nAccess() {
        return i18nAccessImpl;
    }
}

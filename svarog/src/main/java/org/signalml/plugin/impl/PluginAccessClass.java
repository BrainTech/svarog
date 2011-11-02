package org.signalml.plugin.impl;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.plugin.export.PluginAuth;
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
	 * the unique shared instance of this class
	 */
	private static PluginAccessClass sharedInstance = null;

	/**
	 * the manager of Svarog elements
	 */
	private ViewerElementManager manager;
	
	private SvarogAccessI18nImpl i18nAccessImpl;
	
	/**
	 * access to GUI features of Svarog
	 */
	protected GUIAccessImpl guiAccess;
	
	/** Svarog methods and tasks facade. */
	private MethodAccessImpl methodAccessImpl;
	
	/** Svarog configuration facade. */
	private ConfigAccessImpl configAccessImpl;
	
	/**
	 * access to ordinary features of Svarog
	 */
	private SignalsAccessImpl signalsAccess;
	/**
	 * access to listen on changes in Svarog
	 */
	private ChangeSupportImpl changeSupport;

	/**
	 * The plugin map.
	 */
	private HashMap<PluginAuth,PluginHead> pluginMap = new HashMap<PluginAuth,PluginHead>();
	
	/**
	 * Constructor. Creates child accesses.
	 */
	private PluginAccessClass(){
		guiAccess = new GUIAccessImpl(this);
		methodAccessImpl = new MethodAccessImpl(this);
		configAccessImpl = new ConfigAccessImpl(this);
		signalsAccess = new SignalsAccessImpl(this);
		changeSupport = new ChangeSupportImpl(this);
		i18nAccessImpl = SvarogAccessI18nImpl.getInstance();
		i18nAccessImpl.setPluginAccessClass(this);
	}
	
	/**
	 * Returns the shared instance of this class.
	 * @return the shared instance of this class
	 */
	public static PluginAccessClass getSharedInstance()
	{
		if (sharedInstance == null) {
		    synchronized (PluginAccessClass.class) {
		        if (sharedInstance == null)
		            sharedInstance = new PluginAccessClass();
		    }
		}

		return sharedInstance;
	}
	
	/**
	 * Adds given plugin to {@link #pluginMap}.
	 * @param head plugin to add
	 */
	public synchronized void addPlugin(PluginHead head) {
	    PluginAuth auth = head.getPluginAuth();
	    if (pluginMap.containsKey(auth)) {
	        throw new IllegalArgumentException("Duplicate plugin auth! (" + (auth.getID()) + ")");
	    }
	    pluginMap.put(auth, head);
	    logger.debug("addPlugin: " + auth + " --> " + head);
	}
	
    /**
     * Looks up {@link #pluginMap} for the given plugin based on its key.
     * @param auth plugin key
     * @return the plugin head found
     * @throws IllegalArgumentException if there is no mapping for this key
     */
	public synchronized PluginHead getPluginHead(PluginAuth auth) {
	    PluginHead head = pluginMap.get(auth);
	    if (null != head)
	        return head;
	    throw new IllegalArgumentException("No such plugin! (" + auth + ")");
	}

	/**
	 * Returns the implementation of {@link SvarogAccessGUI}.
	 * @return the implementation of GUI access
	 */
	public static GUIAccessImpl getGUIImpl() {
		return getSharedInstance().guiAccess;
	}

	/**
	 * @param manager the element manager to set
	 */
	public void setManager(ViewerElementManager manager) {
		try {
			this.manager = manager;
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
	public ViewerElementManager getManager() {
		return manager;
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
		if (changeSupport == null)
			changeSupport = new ChangeSupportImpl(this);
		return changeSupport;
	}

	/**
	 * @param initializationPhase true if it is an initialization phase,
	 * false otherwise
	 */
	public void setInitializationPhaseEnd() {
		guiAccess.setInitializationPhaseEnd();
	}
	
	/**
	 * Calls {@link ChangeSupportImpl#onClose()}.
	 */
	public void onClose(){
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

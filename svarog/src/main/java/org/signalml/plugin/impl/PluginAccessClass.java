package org.signalml.plugin.impl;

import org.apache.log4j.Logger;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.change.SvarogAccessChangeSupport;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import org.signalml.plugin.impl.change.ChangeSupportImpl;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Implementation of {@link SvarogAccess} interface.
 * Allows to return only one, {@link #getSharedInstance() shared instance}
 * of this class.
 * Passes the information that the {@link #setInitializationPhase(boolean)
 * initialization phase} has finished to the {@link GUIAccessImpl GUI access}
 * and the information that the application is {@link #onClose() closing}
 * to {@link ChangeSupportImpl change support}. 
 * 
 * @author Marcin Szumski
 */
public class PluginAccessClass implements SvarogAccess{
	
	private static final Logger logger = Logger.getLogger(PluginAccessClass.class);
	
	/**
	 * the unique shared instance of this class
	 */
	private static PluginAccessClass sharedInstance = null;

	/**
	 * the manager of Svarog elements
	 */
	private ViewerElementManager manager;
	
	/**
	 * access to GUI features of Svarog
	 */
	protected GUIAccessImpl guiAccess;
	/**
	 * access to ordinary features of Svarog
	 */
	private SignalsAccessImpl signalsAccess;
	/**
	 * access to listen on changes in Svarog
	 */
	private ChangeSupportImpl changeSupport;

	
	/**
	 * Constructor. Creates child accesses.
	 */
	private PluginAccessClass(){
		guiAccess = new GUIAccessImpl();
		signalsAccess = new SignalsAccessImpl();
		changeSupport = new ChangeSupportImpl();
	}
	
	/**
	 * Returns the shared instance of this class.
	 * @return the shared instance of this class
	 */
	public static PluginAccessClass getSharedInstance()
	{
		if (null == sharedInstance)
			sharedInstance = new PluginAccessClass();
		return sharedInstance;
	}

	/**
	 * Returns the implementation of {@link SvarogAccessGUI}.
	 * @return the implementation of GUI access
	 */
	public static GUIAccessImpl getGUIImpl(){
		return sharedInstance.guiAccess;
	}

	/**
	 * @param manager the element manager to set
	 */
	public void setManager(ViewerElementManager manager) {
		try {
			this.manager = manager;
			signalsAccess.setManager(manager);
			guiAccess.setManager(manager);
			changeSupport.setManager(manager);
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
			changeSupport = new ChangeSupportImpl();
		return changeSupport;
	}

	/**
	 * @param initializationPhase true if it is an initialization phase,
	 * false otherwise
	 */
	public void setInitializationPhase(boolean initializationPhase) {
		guiAccess.setInitializationPhase(initializationPhase);
	}
	
	/**
	 * Calls {@link ChangeSupportImpl#onClose()}.
	 */
	public void onClose(){
		changeSupport.onClose();
	}
	
	/**
	 * Returns the source of messages.
	 * @return the source of messages
	 */
	public MessageSourceAccessor getMessageSource(){
		return manager.getMessageSource();
	}
	
	

}

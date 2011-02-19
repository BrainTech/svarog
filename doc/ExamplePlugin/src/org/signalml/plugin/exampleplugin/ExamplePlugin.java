package org.signalml.plugin.exampleplugin;

import javax.swing.JMenu;

import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.change.SvarogAccessChangeSupport;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagStyle;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.SvarogAccessGUI;

/**
 * The main class of this plug-in.
 * The instance of this class is created just after this plug-in is loaded.
 * There is only one required function - {@link #register(SvarogAccess)}.
 * @author Marcin Szumski
 */
public class ExamplePlugin implements Plugin {

	/**
	 * the access to {@link SvarogAccessSignal signal functions}
	 */
	SvarogAccessSignal signalAccess;
	/**
	 * the access to {@link SvarogAccessGUI GUI functions}
	 */
	SvarogAccessGUI guiAccess;
	/**
	 * the access to {@link SvarogAccessChangeSupport listening} on changes
	 */
	SvarogAccessChangeSupport changeSupport;
	
	/**
	 * This function is called just after this plug-in is loaded.
	 * Here should:
	 * <ul>
	 * <li>be added GUI elements (as adding them later won't be
	 * possible),</li>
	 * <li>be registered listening on changes,</li>
	 * <li>be performed all operations that initialize this plug-in</li>
	 * </ul>
	 */
	@Override
	public void register(SvarogAccess access) throws SignalMLException {
		signalAccess = access.getSignalAccess();
		guiAccess = access.getGUIAccess();
		changeSupport = access.getChangeSupport();
		initialize();
	}
	
	/**
	 * Creates and adds the tag sub-menu.
	 * This menu contains 3 buttons:
	 * <ul>
	 * <li>{@link ShowActiveTagAction showing} the currently active tag,</li>
	 * <li>{@link ShowTagsFromActiveSignalAction showing} all tags for the
	 * active document,</li>
	 * <li>{@link PreciseTagAction showing} the {@link PreciseTagDialog dialog}
	 * that allows to create a custom (precise) tag.</li>
	 * </ul>
	 * Created sub-menu is added to column header pop-up menu. 
	 */
	private void createTagSubmenu(){
		JMenu menu = new JMenu("Tag actions");
		menu.add(new ShowActiveTagAction(signalAccess));
		menu.add(new ShowTagsFromActiveSignalAction(signalAccess));
		menu.add(new PreciseTagAction(signalAccess));
		guiAccess.addSubMenuToColumnHeaderPopupMenu(menu);
	}
	
	/**
	 * Adds elements to the GUI:
	 * <ul>
	 * <li>{@link #createTagSubmenu() tag sub-menu},</li>
	 * <li>{@link OpenBookAction button} that shows the {@link OpenBookDialog
	 * dialog} that allows to open book file,</li>
	 * <li>{@link SamplesPanelAction button} that adds the {@link SamplesPanel
	 * property tab} in which first 100 samples of all channels are displayed</li>
	 * and registers listeners.
	 */
	private void initialize(){
		createTagSubmenu();
		guiAccess.addButtonToSignalPlotPopupMenu(new SamplesPanelAction(signalAccess, guiAccess));
		guiAccess.addButtonToEditMenu(new OpenBookAction(signalAccess));
		registerListening();
	}
	
	/**
	 * Creates a {@link ExamplePluginListener listener} that listens on changes
	 * associated with {@link ExportedTag tags} and {@link ExportedTagStyle
	 * tag styles} (addition, removal, change) and registers this listener.
	 */
	private void registerListening(){
		ExamplePluginListener listener = new ExamplePluginListener();
		changeSupport.addTagListener(listener);
		changeSupport.addTagStyleListener(listener);
	}

}

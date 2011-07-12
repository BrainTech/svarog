/**
 * 
 */
package org.signalml.plugin.export.view;


import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.signalml.app.view.signal.SelectTagSignalTool;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.signal.SignalTool;
import org.signalml.plugin.export.signal.SignalToolButtonListener;

/**
 * This is an interface that allows to:
 * <ul>
 * <li>add buttons and sub-menus,</li>
 * <li>add and remove tabs,</li>
 * <li>add {@link SignalTool signal tools},</li>
 * <li>draw components on the {@link ExportedSignalPlot signal plot}.</li>
 * </ul>
 * <p>
 * Operations of addition of tabs, sub-menus and signal tools can be performed
 * only in the initialization phase (in the method
 * {@link Plugin#register(org.signalml.plugin.export.SvarogAccess) register}
 * of the starting class}).
 * <p>
 * The menus that are added to pop-up menus are copied, so that they can
 * be added for example to different signal plots.
 * But, as in this operation only actions are copied, you have to be careful
 * to use only buttons in these menus.
 *
 * @author Marcin Szumski
 * @author Stanislaw Findeisen
 */
public interface SvarogAccessGUI {

	/**
	 * Adds a button at the end of tools menu.
	 * Can be accessed only during initialization phase.
	 * @param action the {@link Action} used to create button
	 * @return created menu item
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public JMenuItem addButtonToToolsMenu(Action action) throws UnsupportedOperationException;
	
	/**
	 * Adds a submenu at the end of tools menu.
	 * Can be accessed only during initialization phase.
	 * @param menu the menu that will be added as submenu
	 * @return created menu item
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public JMenuItem addSubmenuToToolsMenu(JMenu menu) throws UnsupportedOperationException;
	
	/**
	 * Adds a button at the end of edit menu.
	 * Can be accessed only during initialization phase.
	 * @param action the {@link Action} used to create button
	 * @return created menu item
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public JMenuItem addButtonToEditMenu(Action action) throws UnsupportedOperationException;
	
	/**
	 * Adds a submenu at the end of tools menu.
	 * Can be accessed only during initialization phase.
	 * @param menu the menu that will be added as submenu
	 * @return created menu item
	 * @throws UnsupportedOperationException if the function called
	 * not during in the initialization phase.
	 */
	public JMenuItem addSubmenuToEditMenu(JMenu menu) throws UnsupportedOperationException;
	
	/**
	 * Adds a button at the end of popup menu in the signal plot.
	 * Can be accessed only during initialization phase.
	 * @param action the {@link Action} used to create button
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addButtonToSignalPlotPopupMenu(Action action) throws UnsupportedOperationException;//area where the signal is displayed
	
	/**
	 * Adds a submenu at the end of popup menu in the signal plot.
	 * Can be accessed only during initialization phase.
	 * @param menu the menu that will be added as submenu
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addSubMenuToSignalPlotPopupMenu(JMenu menu) throws UnsupportedOperationException;

	//tabs on the left
	/**
	 * Adds a button at the end of popup menu in the workspace tree tab
	 * which appears while clicking on a MRUD entry node.
	 * Can be accessed only during initialization phase.
	 * @param action the {@link Action} used to create button
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addButtonToWorkspaceTreeMRUDPopupMenu(Action action) throws UnsupportedOperationException;//workspace tab
	
	/**
	 * Adds a button at the end of popup menu in the workspace tree tab
	 * which appears while clicking on a document node.
	 * Can be accessed only during initialization phase.
	 * @param action the {@link Action} used to create button
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addButtonToWorkspaceTreeDocumentPopupMenu(Action action) throws UnsupportedOperationException;
	
	/**
	 * Adds a button at the end of popup menu in the workspace tree tab
	 * which appears while clicking on in other areas then a MRUD entry or
	 * a document node.
	 * Can be accessed only during initialization phase.
	 * @param action the {@link Action} used to create button
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addButtonToWorkspaceTreeOtherPopupMenu(Action action) throws UnsupportedOperationException;
	
	/**
	 * Adds a submenu at the end of popup menu in the workspace tree tab
	 * which appears while clicking on a MRUD entry node.
	 * Can be accessed only during initialization phase.
	 * @param menu the menu that will be added as submenu
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addSubMenuToWorkspaceTreeMRUDPopupMenu(JMenu menu) throws UnsupportedOperationException;
	
	/**
	 * Adds a button at the end of popup menu in the workspace tree tab
	 * which appears while clicking on a document node.
	 * Can be accessed only during initialization phase.
	 * @param menu the menu that will be added as submenu
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addSubMenuToWorkspaceTreeDocumentPopupMenu(JMenu menu) throws UnsupportedOperationException;
	
	/**
	 * Adds a submenu at the end of popup menu in the workspace tree tab
	 * which appears while clicking on in other areas then a MRUD entry or
	 * a document node.
	 * Can be accessed only during initialization phase.
	 * @param menu the menu that will be added as submenu
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addSubMenuToWorkspaceTreeOtherPopupMenu(JMenu menu) throws UnsupportedOperationException;
	
	/**
	 * Adds a button at the end of popup menu in the tag tree tab
	 * which appears while clicking on a signal document node.
	 * Can be accessed only during initialization phase.
	 * @param action the {@link Action} used to create button
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addButtonToTagTreeSignalDocumentPopupMenu(Action action) throws UnsupportedOperationException;//tag tab
	
	/**
	 * Adds a button at the end of popup menu in the tag tree tab
	 * which appears while clicking on a tag document node.
	 * Can be accessed only during initialization phase.
	 * @param action the {@link Action} used to create button
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addButtonToTagTreeTagDocumentPopupMenu(Action action) throws UnsupportedOperationException;
	
	/**
	 * Adds a button at the end of popup menu in the tag tree tab
	 * which appears while clicking on a tag style node.
	 * Can be accessed only during initialization phase.
	 * @param action the {@link Action} used to create button
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addButtonToTagTreeTagStylePopupMenu(Action action) throws UnsupportedOperationException;
	
	/**
	 * Adds a button at the end of popup menu in the tag tree tab
	 * which appears while clicking on a tag node.
	 * Can be accessed only during initialization phase.
	 * @param action the {@link Action} used to create button
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addButtonToTagTreeTagPopupMenu(Action action) throws UnsupportedOperationException;
	
	/**
	 * Adds a submenu at the end of popup menu in the tag tree tab
	 * which appears while clicking on a signal document node.
	 * Can be accessed only during initialization phase.
	 * @param menu the menu that will be added as submenu
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addSubMenuToTagTreeSignalDocumentPopupMenu(JMenu menu) throws UnsupportedOperationException;
	
	/**
	 * Adds a submenu at the end of popup menu in the tag tree tab
	 * which appears while clicking on a tag document node.
	 * Can be accessed only during initialization phase.
	 * @param menu the menu that will be added as submenu
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addSubMenuToTagTreeTagDocumentPopupMenu(JMenu menu) throws UnsupportedOperationException;
	
	/**
	 * Adds a submenu at the end of popup menu in the tag tree tab
	 * which appears while clicking on a tag style node.
	 * Can be accessed only during initialization phase.
	 * @param menu the menu that will be added as submenu
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addSubMenuToTagTreeTagStylePopupMenu(JMenu menu) throws UnsupportedOperationException;
	
	/**
	 * Adds a submenu at the end of popup menu in the tag tree tab
	 * which appears while clicking on a tag node.
	 * Can be accessed only during initialization phase.
	 * @param menu the menu that will be added as submenu
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addSubMenuToTagTreeTagPopupMenu(JMenu menu) throws UnsupportedOperationException;
	
	/**
	 * Adds a button at the end of popup menu in the signal tree tab
	 * which appears while clicking on a document node.
	 * Can be accessed only during initialization phase.
	 * @param action the {@link Action} used to create button
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addButtonToSignalTreeDocumentPopupMenu(Action action) throws UnsupportedOperationException;//signal tab
	
	/**
	 * Adds a button at the end of popup menu in the signal tree tab
	 * which appears while clicking on a signal page node.
	 * Can be accessed only during initialization phase.
	 * @param action the {@link Action} used to create button
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addButtonToSignalTreeSignalPagePopupMenu(Action action) throws UnsupportedOperationException;
	
	/**
	 * Adds a submenu at the end of popup menu in the signal tree tab
	 * which appears while clicking on a document node.
	 * Can be accessed only during initialization phase.
	 * @param menu the menu that will be added as submenu
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addSubMenuToSignalTreeDocumentPopupMenu(JMenu menu) throws UnsupportedOperationException;
	
	/**
	 * Adds a submenu at the end of popup menu in the signal tree tab
	 * which appears while clicking on a signal page node.
	 * Can be accessed only during initialization phase.
	 * @param menu the menu that will be added as submenu
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addSubMenuToSignalTreeSignalPagePopupMenu(JMenu menu) throws UnsupportedOperationException;
	
	
	/**
	 * Adds a button at the end of popup menu in the book tree tab
	 * which appears while clicking on a book document node.
	 * Can be accessed only during initialization phase.
	 * @param action the {@link Action} used to create button
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addButtonToBookTreeBookDocumentPopupMenu(Action action) throws UnsupportedOperationException;//book tab
	
	/**
	 * Adds a submenu at the end of popup menu in the book tree tab
	 * which appears while clicking on a book document node.
	 * Can be accessed only during initialization phase.
	 * @param menu the menu that will be added as submenu
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addSubMenuToBookTreeBookDocumentPopupMenu(JMenu menu) throws UnsupportedOperationException;
	
	/**
	 * Adds a button at the end of popup menu in the hypnogram plot
	 * (the area at the top of signal view tab).
	 * Can be accessed only during initialization phase.
	 * @param action the {@link Action} used to create button
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addButtonToHypnogramPlotPopupMenu(Action action) throws UnsupportedOperationException;
	
	/**
	 * Adds a submenu at the end of popup menu in the hypnogram plot
	 * (the area at the top of signal view tab).
	 * Can be accessed only during initialization phase.
	 * @param menu the menu that will be added as submenu
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addSubMenuToHypnogramPlotPopupMenu(JMenu menu) throws UnsupportedOperationException;
	
	/**
	 * Adds a button at the end of popup menu in the column header area
	 * (the area below hypnogram plot, above signal plot).
	 * Can be accessed only during initialization phase.
	 * @param action the {@link Action} used to create button
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addButtonToColumnHeaderPopupMenu(Action action) throws UnsupportedOperationException;
	
	/**
	 * Adds a submenu at the end of popup menu in the column header area
	 * (the area below hypnogram plot, above signal plot).
	 * Can be accessed only during initialization phase.
	 * @param menu the menu that will be added as submenu
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addSubMenuToColumnHeaderPopupMenu(JMenu menu) throws UnsupportedOperationException;
	
	
	
	/**
	 * Adds a button to the tools button group and associates the tool
	 * (for example FFT window) with it.
	 * For every view there is created a copy of both signal tool and button.
	 * Can be accessed only during initialization phase.
	 * @param tool the tool to be associated with this button
	 * @param icon the icon that is used to create a button
	 * @param toolTipText the tool tip text for a button or null if no
	 * tool tip text should be used
	 * @param buttonListener the action listener for a button
	 * or null if no mouse listener for this button should be used
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 * @see SignalTool
	 * @see SelectTagSignalTool
	 */
	public void addSignalTool(SignalTool tool, Icon icon, String toolTipText, SignalToolButtonListener buttonListener) throws UnsupportedOperationException;
	
	/**
	 * Adds the button to the main toolbar (the top one).
	 * Can be accessed only during initialization phase.
	 * In other situations will do nothing.
	 * @param action the action used to create a button.
	 * @throws UnsupportedOperationException if the function called
	 * not during the initialization phase.
	 */
	public void addButtonToMainToolbar(Action action) throws UnsupportedOperationException;
	

	
	/**
	 * Adds a tab to main panel. Tab should be associated with a document.
	 * DocumentView should be able to return a document.
	 * @param tab the panel (view) to be added as tab
	 * @param title the title of the tab, if no title provided
	 * {@code tab.getName()} is used
	 * @param icon the icon of the tab, null if no icon should be used
	 * @param tip the tool tip for the tab
	 */
	void addMainTab(DocumentView tab, String title, Icon icon, String tip);
	
	/**
	 * Removes a tab created from a given view from main panel.
	 * @param tab the view that was used to create the tab
	 * @throws IllegalArgumentException if there is no such tab
	 */
	void removeMainTab(DocumentView tab) throws IllegalArgumentException;
	
	/**
	 * Returns the {@link DocumentView document view} associated with an active tab. 
	 * @return the document view associated with an active tab
	 * @throws NoActiveObjectException if there is no active main tab
	 */
	DocumentView getSelectedMainTab() throws NoActiveObjectException;
	
	/**
	 * Adds the new tab to the tree panel (the panel on the left).
	 * @param treePane the tree panel to be added
	 * @param title the title of the tab, if no title provided
	 * {@code tab.getName()} is used
	 * @param icon the icon of the tab, null if no icon should be used
	 * @param tip the tool tip for the tab
	 */
	void addTreeTab(ViewerTreePane treePane, String title, Icon icon, String tip);
	
	/**
	 * Removes a tab created from a given tree panel from the tree panel.
	 * @param tab the tree panel that was used to create the tab
	 * @throws IllegalArgumentException if there is no such tab
	 */
	void removeTreeTab(ViewerTreePane tab) throws IllegalArgumentException;
	
	/**
	 * Returns a panel associated with an active tab in the tree tabbed panel.
	 * @return a panel associated with an active tab in the tree tabbed panel
	 * @throws NoActiveObjectException if there is no active tree tab
	 */
	ViewerTreePane getSelectedTreeTab() throws NoActiveObjectException;
	
	/**
	 * Adds a new property tab (on the bottom).
	 * @param panel the panel to be added as tab.
	 */
	void addPropertyTab(JPanel panel);
	
	/**
	 * Returns the active {@link ExportedSignalPlot signal plot}.
	 * @return the active signal plot
	 * @throws NoActiveObjectException if there is no active plot
	 */
	ExportedSignalPlot getActiveSignalPlot() throws NoActiveObjectException;
	
	/**
	 * Removes a tab created from a given panel from the property tabbed panel.
	 * @param panel the panel that ws used to create a tab
	 * @throws IllegalArgumentException if there is no such tab
	 */
	void removePropertyTab(JPanel panel) throws IllegalArgumentException;
	
	/**
	 * Returns a panel associated with an active tab in the property tabbed
	 * panel.
	 * @return a panel associated with an active tab in the property tabbed
	 * panel.
	 * @throws NoActiveObjectException if there is no active property tab
	 */
	JPanel getSelectedPropertyTab() throws NoActiveObjectException;

    /**
     * Returns a reference to the parent for all new {@link java.awt.Dialog}s.
     */
    java.awt.Window getDialogParent();

    /**
     * A specialized subclass of {@link javax.swing.JFileChooser} to be used by plugins.
     */
    FileChooser getFileChooser();
}

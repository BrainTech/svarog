/**
 * 
 */
package org.signalml.plugin.exampleplugin;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import org.signalml.plugin.export.signal.SvarogAccessSignal;

/**
 * When the action is performed there is created a {@link OpenBookDialog dialog}
 * which allows the user to input the path to the book file and tries to
 * open that file.
 *  
 * @author Marcin Szumski
 */
public class OpenBookAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	/**
	 * the {@link SvarogAccessSignal access} to signal options 
	 */
	private SvarogAccessSignal signalAccess;
	
	/**
	 * Constructor. Sets {@link SvarogAccessSignal signal access}.
	 * @param signalAccess access to set
	 */
	public OpenBookAction(SvarogAccessSignal signalAccess) {
		super("Open book");
		this.signalAccess = signalAccess;
	}
	
	/**
	 * Creates a {@link OpenBookDialog dialog} to which the user inputs the
	 * path to the book file and which tries to open this file.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		OpenBookDialog dialog = new OpenBookDialog(signalAccess);
		File profileDir = signalAccess.getProfileDirectory();
		String path = profileDir.getAbsolutePath();
		dialog.showDialog(path);
	}

}

/**
 * 
 */
package org.signalml.plugin.exampleplugin;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.ExportedTagDocument;
import org.signalml.plugin.export.signal.SvarogAccessSignal;

/**
 * When the action is performed there is created a {@link PreciseTagDialog
 * dialog} that allows to create a custom (precise) tag.
 * @author Marcin Szumski
 */
public class PreciseTagAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	/**
	 * the {@link SvarogAccessSignal access} to signal options 
	 */
	private SvarogAccessSignal signalAccess;
	
	/**
	 * Constructor. Sets {@link SvarogAccessSignal signal access}.
	 * @param signalAccess access to set
	 */
	public PreciseTagAction(SvarogAccessSignal signalAccess) {
		super("Add precise tag");
		this.signalAccess = signalAccess;
	}
	
	/**
	 * If there is an active {@link ExportedTagDocument tag document} creates
	 * and show a {@link PreciseTagDialog dialog} that allows to create a
	 * custom (precise) tag.
	 * If there is no active tag document shows pop-up with the information.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		try {
			signalAccess.getActiveTagDocument();
			PreciseTagDialog dialog = new PreciseTagDialog(signalAccess);
			dialog.showDialog(null,true);
		} catch (NoActiveObjectException e1) {
			JOptionPane.showMessageDialog(null, "no active tag or signal document");
		}
	}

}

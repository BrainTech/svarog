/**
 * 
 */
package org.signalml.plugin.exampleplugin;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JOptionPane;

import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagDocument;
import org.signalml.plugin.export.signal.SvarogAccessSignal;

/**
 * When this action is performed a popup with the list of all {@link ExportedTag tags}
 * for the active signal (from all {@link ExportedTagDocument tag documents}
 * dependent from the active signal) is shown.
 * @author Marcin Szumski
 */
public class ShowTagsFromActiveSignalAction extends ShowTagAction {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor. Sets {@link SvarogAccessSignal signal access}.
	 * @param signalAccess access to set
	 */
	public ShowTagsFromActiveSignalAction(SvarogAccessSignal signalAccess) {
		super(signalAccess, "Show all tags");
	}

	/**
	 * Shows a popup with the list of all {@link ExportedTag tags}
	 * for the active signal (from all {@link ExportedTagDocument tag documents}
	 * dependent from the active signal).
	 * If there is no active signal appropriate communicate is shown.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			List<ExportedTag> tags = signalAccess.getTagsFromAllDocumentsAssociatedWithAcitiveSignal();
			String text = new String();
			for (ExportedTag tag : tags){
				text += tagToString(tag);
			}
			JOptionPane.showMessageDialog(null, text);
		} catch (NoActiveObjectException e1) {
			JOptionPane.showMessageDialog(null, "there is no active signal");
		}

	}

}

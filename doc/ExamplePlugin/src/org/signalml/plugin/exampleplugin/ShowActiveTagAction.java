/**
 *
 */
package org.signalml.plugin.exampleplugin;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.SvarogAccessSignal;

/**
 * When this action is performed the pop-up with the description of
 * an active tag is shown.
 * @author Marcin Szumski
 */
public class ShowActiveTagAction extends ShowTagAction {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor. Sets {@link SvarogAccessSignal signal access}.
	 * @param signalAccess access to set
	 */
	public ShowActiveTagAction(SvarogAccessSignal signalAccess) {
		super(signalAccess, "Show active tag");
	}

	/**
	 * Shows the pop-up with the description of an active tag.
	 * If there is no active tag appropriate communicate is shown.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			ExportedTag tag = signalAccess.getActiveTag();
			String text = tagToString(tag);
			JOptionPane.showMessageDialog(null, text);
		} catch (NoActiveObjectException e1) {
			JOptionPane.showMessageDialog(null, "there is no active tag");
		}

	}

}

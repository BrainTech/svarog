/* ExportSamplesToClipboardAction.java created 2008-01-15
 *
 */

package org.signalml.app.action;

import static org.signalml.app.SvarogApplication._;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;

/** ExportSamplesToClipboardAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class ExportSamplesToClipboardAction extends ExportSamplesAction implements ClipboardOwner {

	protected static final Logger logger = Logger.getLogger(ExportSamplesToClipboardAction.class);

	private static final long serialVersionUID = 1L;

	public ExportSamplesToClipboardAction() {
		super();
		setText(_("Copy samples to clipboard"));
		setIconPath("org/signalml/app/icon/clipboard.png");
		setToolTip(_("Copy samples to clipboard"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		String samplesAsString = getSamplesAsString();

		if (samplesAsString != null) {

			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(new StringSelection(samplesAsString), this);

		}

	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(true);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// don't care
	}

}

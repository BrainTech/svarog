package org.signalml.app.method.ep.action;

import org.apache.log4j.Logger;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.method.ep.EvokedPotentialResult;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/**
 * An abstract action for saving the EP averaging results to a file.
 *
 * @author Piotr Szachewicz
 */
public abstract class AbstractSaveAction extends AbstractSignalMLAction {

	protected static final Logger logger = Logger.getLogger(AbstractSaveAction.class);

	protected ViewerFileChooser fileChooser;
	protected EvokedPotentialResult result;

	public AbstractSaveAction(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public void setResult(EvokedPotentialResult result) {
		this.result = result;
	}

	@Override
	public void setEnabledAsNeeded() {
		setEnabled(result != null);
	}

}

package org.signalml.plugin.newartifact;

import java.awt.event.ActionEvent;

import org.signalml.plugin.data.PluginConfigMethodData;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.method.PluginMethodManager;

public class NewArtifactPluginAction extends AbstractSignalMLAction {

	/**
	 *
	 */
	private static final long serialVersionUID = -6604346438530363530L;

	private PluginMethodManager mgr;

	public NewArtifactPluginAction(PluginMethodManager mgr) throws PluginException {
		super();
		this.mgr = mgr;
		PluginConfigMethodData config = mgr.getMethodConfig();
		this.setText(config.getRunMethodString());
		this.setIconPath(config.getIconPath());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mgr.runMethod();
	}

}

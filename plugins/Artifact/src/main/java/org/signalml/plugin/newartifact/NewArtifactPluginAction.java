package org.signalml.plugin.newartifact;

import java.awt.event.ActionEvent;

import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.method.PluginMethodManager;
import org.signalml.plugin.newartifact.NewArtifactPlugin;
import static org.signalml.plugin.newartifact.NewArtifactPlugin._;

public class NewArtifactPluginAction extends AbstractSignalMLAction {

	/**
	 *
	 */
	private static final long serialVersionUID = -6604346438530363530L;

	private PluginMethodManager mgr;

	public NewArtifactPluginAction(PluginMethodManager mgr) {
		super();
		this.mgr = mgr;
		this.setText(_("Artifact detection (new!)"));
		this.setIconPath(NewArtifactPlugin.iconPath);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mgr.runMethod();
	}

}

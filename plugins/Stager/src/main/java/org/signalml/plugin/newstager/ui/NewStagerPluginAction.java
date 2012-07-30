package org.signalml.plugin.newstager.ui;


import static org.signalml.plugin.i18n.PluginI18n._;

import java.awt.event.ActionEvent;

import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.method.PluginMethodManager;
import org.signalml.plugin.newstager.NewStagerPlugin;

public class NewStagerPluginAction extends AbstractSignalMLAction {

	/**
	 *
	 */
	private static final long serialVersionUID = -6604346438530363530L;

	private PluginMethodManager mgr;

	public NewStagerPluginAction(PluginMethodManager mgr) {
		super();
		this.mgr = mgr;
		this.setText(_("Automatic sleep staging (new!)"));
		this.setIconPath(NewStagerPlugin.iconPath);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mgr.runMethod();
	}

}

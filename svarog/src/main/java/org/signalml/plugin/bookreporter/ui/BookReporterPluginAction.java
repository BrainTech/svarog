package org.signalml.plugin.bookreporter.ui;

import java.awt.event.ActionEvent;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.method.PluginMethodManager;

/**
 * @author piotr@develancer.pl
 * (based on NewStagerPluginAction)
 */
public class BookReporterPluginAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	private PluginMethodManager mgr;

	public BookReporterPluginAction(PluginMethodManager mgr) {
		super();
		this.mgr = mgr;
		this.setText(_("EEG profiles (MP)"));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mgr.runMethod();
	}

}

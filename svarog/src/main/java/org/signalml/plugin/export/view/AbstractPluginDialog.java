package org.signalml.plugin.export.view;

import java.awt.Window;

import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.app.SvarogI18n;
import org.signalml.plugin.export.i18n.SvarogAccessI18n;

public abstract class AbstractPluginDialog extends AbstractDialog {
	private static final long serialVersionUID = 1L;

	protected AbstractPluginDialog() {
		super();
	}
	
	protected AbstractPluginDialog(Window w, boolean isModal) {
		super(w, isModal);
	}
}

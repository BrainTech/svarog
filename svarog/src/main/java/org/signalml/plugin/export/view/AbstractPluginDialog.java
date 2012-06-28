package org.signalml.plugin.export.view;

import java.awt.Window;

import org.signalml.app.util.i18n.SvarogI18n;
import org.signalml.app.view.common.dialogs.AbstractDialog;
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

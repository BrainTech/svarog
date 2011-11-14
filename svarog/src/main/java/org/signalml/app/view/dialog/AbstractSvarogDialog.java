package org.signalml.app.view.dialog;

import java.awt.Window;

import org.signalml.app.SvarogI18n;
import org.signalml.plugin.impl.SvarogAccessI18nImpl;

public abstract class AbstractSvarogDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;
	protected SvarogI18n messageSource;
	
	protected AbstractSvarogDialog() {
		super();
		this.messageSource = getSvarogI18n();
	}
	
	protected AbstractSvarogDialog(Window w, boolean isModal) {
		super(w, isModal);
		this.messageSource = getSvarogI18n();
	}

	/**
	 * Svarog i18n API accessor (convenience helper).
	 * @return
	 */
	protected SvarogI18n getSvarogI18n() {
		return SvarogAccessI18nImpl.getInstance();
	}
}

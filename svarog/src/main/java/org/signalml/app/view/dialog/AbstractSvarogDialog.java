package org.signalml.app.view.dialog;

import java.awt.Window;

public abstract class AbstractSvarogDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;
	
	protected AbstractSvarogDialog() { }
	
	protected AbstractSvarogDialog(Window w, boolean isModal) {
		super(w, isModal);
	}
}

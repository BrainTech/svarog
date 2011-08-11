package org.signalml.plugin.sf;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * GrenadeBomb action (immediate).
 *
 * @author Zbigniew Jędrzejewski-Szmek
 */
public class GrenadeAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public GrenadeAction() {
		super("sf.grenade");
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		throw new OutOfMemoryError();
	}
}

package org.signalml.plugin.sf;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * ClockBomb action (timeout 0).
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class BombAction extends AbstractAction {
    
    private static final long serialVersionUID = 1L;

    public BombAction() {
        super("sf.bomba");
    }

    @Override
    public void actionPerformed(ActionEvent a) {
        new ClockBomb().run();
    }
    
}

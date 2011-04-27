/* SignalPlotScrollPane.java created 2007-10-17
 *
 */

package org.signalml.app.view.signal;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JScrollPane;

/** SignalPlotScrollPane
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalPlotScrollPane extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private static final Dimension MINIMUM_SIZE = new Dimension(0,0);

	public SignalPlotScrollPane() {
		super();
	}

	public SignalPlotScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
		super(view, vsbPolicy, hsbPolicy);
	}

	public SignalPlotScrollPane(Component view) {
		super(view);
	}

	public SignalPlotScrollPane(int vsbPolicy, int hsbPolicy) {
		super(vsbPolicy, hsbPolicy);
	}

	@Override
	public Dimension getMinimumSize() {
		return MINIMUM_SIZE;
	}

	/*
	@Override
	public String getToolTipText() {
		return "???";
	}
	*/

}

/* SignalPlotCorner.java created 2007-11-08
 *
 */

package org.signalml.app.view.signal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;


/** SignalPlotCorner
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class SignalPlotCorner extends JPanel {

	private static final long serialVersionUID = 1L;

	protected SignalPlot plot;

	public SignalPlotCorner(SignalPlot plot) {
		super();
		setBorder(new EmptyBorder(3,3,3,3));
		this.plot = plot;
	}

	public SignalPlot getPlot() {
		return plot;
	}

	public int getPreferredWidth() {
		return super.getPreferredSize().width;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(plot.getView().getSynchronizedRowHeaderWidth(), super.getPreferredSize().height);
	}

}

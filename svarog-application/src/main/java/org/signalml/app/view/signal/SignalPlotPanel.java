/* SignalPlotPanel.java created 2007-11-08
 * 
 */

package org.signalml.app.view.signal;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/** SignalPlotPanel
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalPlotPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JPanel labelPanel;
	
	public SignalPlotPanel(SignalPlot plot, SignalPlotScrollPane scrollPane) {
		
		super(new BorderLayout());
		
		if( !plot.isMaster() ) {
			labelPanel = new JPanel( new BorderLayout() );
			labelPanel.setBorder( new EmptyBorder(0,2,2,2) );
			labelPanel.add( plot.getSignalPlotTitleLabel(), BorderLayout.CENTER );
			labelPanel.add( plot.getSignalPlotSynchronizationLabel(), BorderLayout.EAST );
			
			add( labelPanel, BorderLayout.SOUTH );
		}
		
		add( scrollPane, BorderLayout.CENTER );
		
	}
		
}

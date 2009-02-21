/* SlaveSignalPlotCorner.java created 2007-11-08
 * 
 */

package org.signalml.app.view.signal;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.element.CompactButton;
import org.signalml.app.view.signal.popup.SlavePlotSettingsPopupDialog;

/** SlaveSignalPlotCorner
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SlaveSignalPlotCorner extends SignalPlotCorner {

	private static final long serialVersionUID = 1L;
	
	private SlavePlotSettingsPopupDialog slavePlotSettingsPopupDialog;

	private CompactButton configureSlavePlotButton;
	
	public SlaveSignalPlotCorner(SignalPlot plot) {
		super(plot);
		setLayout( new BoxLayout(this, BoxLayout.Y_AXIS) );
		
		configureSlavePlotButton = new CompactButton( new ConfigureSlavePlotAction() );
		configureSlavePlotButton.setAlignmentY(Component.TOP_ALIGNMENT);
		
		JButton removeSlavePlotButton = new CompactButton( new RemoveSlavePlotAction() );
		removeSlavePlotButton.setAlignmentY(Component.TOP_ALIGNMENT);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.X_AXIS) );
		
		buttonPanel.add( removeSlavePlotButton );
		buttonPanel.add( Box.createHorizontalStrut(3) );
		buttonPanel.add( configureSlavePlotButton );
		buttonPanel.add( Box.createHorizontalStrut(3) );
		buttonPanel.add( Box.createHorizontalGlue() );
		
		add( buttonPanel );
		add( Box.createVerticalStrut(3) );
		add( Box.createVerticalGlue() );
		
	}

	public SlavePlotSettingsPopupDialog getSlavePlotSettingsPopupDialog() {
		return slavePlotSettingsPopupDialog;
	}

	public void setSlavePlotSettingsPopupDialog(SlavePlotSettingsPopupDialog slavePlotSettingsPopupDialog) {
		this.slavePlotSettingsPopupDialog = slavePlotSettingsPopupDialog;
	}
	
	protected class RemoveSlavePlotAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public RemoveSlavePlotAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/removeslaveplot.png") );
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("signalView.removeSlavePlotToolTip"));
		}
		
		public void actionPerformed(ActionEvent ev) {			
			
			plot.getView().removeSlavePlot(plot);
			
		}
		
	}

	protected class ConfigureSlavePlotAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ConfigureSlavePlotAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/configureslaveplot.png") );
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("signalView.configureSlavePlotToolTip"));
		}
		
		public void actionPerformed(ActionEvent ev) {			
			
			Container ancestor = getTopLevelAncestor();
			Point containerLocation = ancestor.getLocation();
			Point location = SwingUtilities.convertPoint(configureSlavePlotButton, new Point(0,0), ancestor);
			slavePlotSettingsPopupDialog.initializeNow();
			if( location.y < ancestor.getHeight()/2 ) {
				location.translate(containerLocation.x, containerLocation.y);
			} else {
				location.translate(containerLocation.x, containerLocation.y + configureSlavePlotButton.getHeight() - slavePlotSettingsPopupDialog.getHeight() );
			}
			slavePlotSettingsPopupDialog.setLocation(location);			
			slavePlotSettingsPopupDialog.showDialog(plot);
			
		}
		
	}
	
}

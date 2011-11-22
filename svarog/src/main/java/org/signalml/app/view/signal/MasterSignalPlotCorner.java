/* MasterSignalPlotCorner.java created 2007-11-08
 *
 */

package org.signalml.app.view.signal;

import static org.signalml.app.SvarogI18n._;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.element.CompactButton;

/** MasterSignalPlotCorner
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MasterSignalPlotCorner extends SignalPlotCorner {

	private static final long serialVersionUID = 1L;

	private AddSlavePlotAction addSlavePlotAction;

	public MasterSignalPlotCorner(SignalPlot plot) {
		super(plot);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		addSlavePlotAction = new AddSlavePlotAction();
		add(new CompactButton(addSlavePlotAction));
		add(Box.createVerticalGlue());
	}

	public void setAddSlavePlotEnabled(boolean enabled) {
		addSlavePlotAction.setEnabled(enabled);
	}

	protected class AddSlavePlotAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AddSlavePlotAction() {
			super();
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/addslaveplot.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Create new signal plot"));
		}

		public void actionPerformed(ActionEvent ev) {

			plot.getView().addSlavePlot(plot);

		}

	}

}

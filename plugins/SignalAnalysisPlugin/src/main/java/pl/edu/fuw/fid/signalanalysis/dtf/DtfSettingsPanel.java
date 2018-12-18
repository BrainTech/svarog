package pl.edu.fuw.fid.signalanalysis.dtf;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.signalml.app.view.signal.signalselection.ChannelSpacePanel;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import static org.signalml.plugin.i18n.PluginI18n._;

/**
 * Panel for DTF method settings: selected channels
 * and maximum order for the AR model.
 *
 * @author ptr@mimuw.edu.pl
 */
public final class DtfSettingsPanel extends JPanel {

	private final ChannelSpacePanel channelPanel;
	private final JSpinner orderSpinner;

	public DtfSettingsPanel(SignalSpaceConstraints signalSpaceConstraints) {
		super(new BorderLayout());
		this.channelPanel = new ChannelSpacePanel();
		this.channelPanel.setConstraints(signalSpaceConstraints);
		this.orderSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));

		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(new JLabel(_("Max order of AR model:")), BorderLayout.WEST);
		bottomPanel.add(orderSpinner, BorderLayout.CENTER);

		add(channelPanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	public int[] getSelectedChannels() {
		return channelPanel.getChannelList().getSelectedIndices();
	}

	public int getMaxModelOrder() {
		return (Integer) orderSpinner.getValue();
	}

	public int showAsConfirmDialog(Component parentComponent) {
		return JOptionPane.showConfirmDialog(parentComponent, this, _("Directed Transfer Function"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
	}

}

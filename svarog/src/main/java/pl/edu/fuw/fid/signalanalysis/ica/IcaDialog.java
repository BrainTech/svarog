package pl.edu.fuw.fid.signalanalysis.ica;

import java.awt.Window;
import java.net.URL;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.signalml.app.model.components.validation.ValidationErrors;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.app.view.signal.signalselection.ChannelSpacePanel;
import org.signalml.app.view.signal.signalselection.TimeSpacePanel;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.ExportedSignalSelection;
import org.signalml.plugin.export.signal.SignalSelection;

/**
 * Setup dialog for ICA method. Allows to select time interval of the signal
 * (selecting the whole signal is possible as well) and a subset of channels.
 *
 * @author ptr@mimuw.edu.pl
 */
public class IcaDialog extends AbstractDialog {

	private final SignalSpaceConstraints signalSpaceConstraints;
	private final ExportedSignalSelection selection;
	private URL contextHelpURL;

	private ChannelSpacePanel channelPanel;
	private TimeSpacePanel timePanel;

	public IcaDialog(Window parent, SignalSpaceConstraints signalSpaceConstraints, ExportedSignalSelection selection) {
		super(parent, true);
		setTitle(_("Compute ICA"));
		this.signalSpaceConstraints = signalSpaceConstraints;
		this.selection = selection;
	}

	@Override
	protected JComponent createInterface() {
		channelPanel = new ChannelSpacePanel();
		channelPanel.setConstraints(signalSpaceConstraints);

		timePanel = new TimeSpacePanel();
		timePanel.setConstraints(signalSpaceConstraints);

		SignalSpace signalSpace = new SignalSpace();
		if (selection != null) {
			signalSpace.configureFromSelections(new SignalSelection(selection), null);
		}
		timePanel.fillPanelFromModel(signalSpace);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(channelPanel);
		panel.add(timePanel);
		return panel;
	}

	@Override
	protected URL getContextHelpURL() {
		if (contextHelpURL == null) {
			contextHelpURL = getClass().getResource("help.html");
		}
		return contextHelpURL;
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return clazz == SignalSpace.class;
	}

	@Override
	protected void fillDialogFromModel(Object model) throws SignalMLException {
		// nothing here
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		SignalSpace signalSpace = (SignalSpace) model;
		channelPanel.fillModelFromPanel(signalSpace);
		timePanel.fillModelFromPanel(signalSpace);
	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		channelPanel.validatePanel(errors);
		timePanel.validatePanel(errors);
	}

}

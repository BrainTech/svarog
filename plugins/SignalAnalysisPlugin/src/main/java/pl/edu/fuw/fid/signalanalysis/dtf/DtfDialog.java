package pl.edu.fuw.fid.signalanalysis.dtf;

import java.awt.Window;
import java.net.URL;
import javax.swing.JComponent;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.domain.montage.Montage;
import org.signalml.plugin.export.SignalMLException;
import static org.signalml.plugin.i18n.PluginI18n._;

/**
 * Dialog for displaying results of DTF method, consisting of a single
 * DTFTabbedPane panel and help button.
 *
 * @author ptr@mimuw.edu.pl
 */
public class DtfDialog extends AbstractDialog {

	private final DtfTabbedPane pane;
	private URL contextHelpURL;

	public DtfDialog(Window parent, XYSeriesWithLegend[] criteria, String[] channels, ArModel[] models, int spectrumSize, Montage sources) {
		super(parent, false);
		setTitle(_("DTF results"));
		pane = new DtfTabbedPane(criteria, channels, models, spectrumSize, sources);
	}

	@Override
	protected JComponent createInterface() {
		return pane;
	}

	@Override
	protected URL getContextHelpURL() {
		if (contextHelpURL == null) {
			contextHelpURL = getClass().getResource("help.html");
		}
		return contextHelpURL;
	}

	@Override
	public boolean isCancellable() {
		return false;
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return true;
	}

	@Override
	protected void fillDialogFromModel(Object model) throws SignalMLException {
		// nothing here
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		// nothing here
	}

}

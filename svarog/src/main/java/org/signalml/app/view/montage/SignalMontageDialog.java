/* SignalMontageDialog.java created 2007-09-11
 *
 */
package org.signalml.app.view.montage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.montage.MontageDescriptor;
import org.signalml.app.util.i18n.SvarogI18n;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.AbstractDialog;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.SvarogConstants;

/** SignalMontageDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalMontageDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private ViewerElementManager viewerElementManager;
	private SignalMontagePanel signalMontagePanel;

	private URL contextHelpURL = null;

	public SignalMontageDialog(ViewerElementManager viewerElementManager,
							   Window f, boolean isModal) {

		super(f, isModal);
		this.viewerElementManager = viewerElementManager;
	}

	@Override
	protected void initialize() {
		setTitle(_("Signal montage and filters"));
		setPreferredSize(SvarogConstants.MIN_ASSUMED_DESKTOP_SIZE);
		super.initialize();
		setMinimumSize(new Dimension(800, 600));
	}

	@Override
	public JComponent createInterface() {
		JPanel interfacePanel = new JPanel(new BorderLayout());

		interfacePanel.add(getSignalMontagePanel(), BorderLayout.CENTER);

		return interfacePanel;
	}

	public SignalMontagePanel getSignalMontagePanel() {
		if (signalMontagePanel == null)
			signalMontagePanel = new SignalMontagePanel(viewerElementManager.getFileChooser());
		return signalMontagePanel;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		getSignalMontagePanel().fillPanelFromModel(model);
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		getSignalMontagePanel().fillModelFromPanel(model);
	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		getSignalMontagePanel().validate(model, errors);
	}

	@Override
	protected void onDialogClose() {
		super.onDialogClose();
		getSignalMontagePanel().setMontageToPanels(null);
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return MontageDescriptor.class.isAssignableFrom(clazz);
	}

	@Override
	protected URL getContextHelpURL() {
		if (contextHelpURL == null) {
			contextHelpURL = SvarogI18n._H("contents.html", "montage");
		}
		return contextHelpURL;
	}

}

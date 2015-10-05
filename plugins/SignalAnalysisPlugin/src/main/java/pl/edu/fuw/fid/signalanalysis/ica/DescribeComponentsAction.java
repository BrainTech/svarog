package pl.edu.fuw.fid.signalanalysis.ica;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.domain.montage.Montage;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.export.view.SvarogAccessGUI;

/**
 * @author ptr@mimuw.edu.pl
 */
public class DescribeComponentsAction extends AbstractSignalMLAction {

	private static final String TITLE = "Describe components";

	private final SvarogAccessGUI guiAccess;
	private final SvarogAccessSignal signalAccess;

	public DescribeComponentsAction(SvarogAccessGUI guiAccess, SvarogAccessSignal signalAccess) {
		super();
		this.guiAccess = guiAccess;
		this.signalAccess = signalAccess;
		this.setText(TITLE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Document document = signalAccess.getActiveDocument();
			if (!(document instanceof SignalDocument)) {
				throw new NoActiveObjectException();
			}
			Montage icaMontage = ((SignalDocument) document).getMontage();
			if (icaMontage == null) {
				throw new NoActiveObjectException();
			}

			DescribeComponentsPanel panel = new DescribeComponentsPanel(icaMontage);
			JDialog dialog = new JDialog(guiAccess.getDialogParent(), "Components' topography");
			dialog.setContentPane(panel);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setMinimumSize(new Dimension(400, 300));
			dialog.pack();
			dialog.setVisible(true);

		} catch (NoActiveObjectException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "Choose a signal with an existing ICA analysis.", "Error", JOptionPane.WARNING_MESSAGE);
		}
	}
}

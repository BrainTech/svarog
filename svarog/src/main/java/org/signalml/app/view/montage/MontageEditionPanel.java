package org.signalml.app.view.montage;

import java.awt.BorderLayout;
import javax.swing.JTabbedPane;

import org.signalml.app.view.common.components.panels.AbstractPanel;
import org.signalml.app.view.common.dialogs.errors.ExceptionDialog;
import org.signalml.app.view.common.dialogs.errors.ValidationErrorsDialog;
import org.signalml.app.view.montage.visualreference.VisualReferenceEditorPanel;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.generators.IMontageGenerator;

import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * This class represents a panel for editing the signal's montage.
 * Contains a dropdown list for selecting a montage generator
 * and the tabs for selecting which channels should be included in the
 * montage and what are the references between the channels.
 *
 * @author Piotr Szachewicz
 */
public class MontageEditionPanel extends AbstractPanel {

	/**
	 * A panel for selecting a {@link IMontageGenerator} to be applied.
	 */
	protected MontageGeneratorPanel generatorPanel;
	/**
	 * A panel for selecting which channels should be included in the target
	 * montage.
	 */
	protected MontageChannelsPanel channelsPanel;
	/**
	 * A panel for editing the references between the channels using
	 * a matrix (table).
	 */
	protected MatrixReferenceEditorPanel matrixReferenceEditorPanel;
	/**
	 * A panel for editing the references between the channels using
	 * a GUI.
	 */
	protected VisualReferenceEditorPanel visualReferenceEditorPanel;
	/**
	 * A {@link JTabbedPane} which contains the channelsPane, the
	 * matrixReferenceEditorPanel and the visualReferenceEditorPanel.
	 */
	private JTabbedPane tabbedPane;

	/**
	 * Constructor.
	 */
	public MontageEditionPanel() {
		createInterface();
	}

	protected void createInterface() {

		generatorPanel = new MontageGeneratorPanel();

		channelsPanel = new MontageChannelsPanel();
		matrixReferenceEditorPanel = new MatrixReferenceEditorPanel();
		visualReferenceEditorPanel = new VisualReferenceEditorPanel();

		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(_("Channels"), channelsPanel);
		tabbedPane.addTab(_("Reference GUI"), visualReferenceEditorPanel);
		tabbedPane.addTab(_("Reference matrix"), matrixReferenceEditorPanel);

		this.setLayout(new BorderLayout());
		this.add(generatorPanel, BorderLayout.NORTH);
		this.add(tabbedPane, BorderLayout.CENTER);
	}

	/**
	 * Sets the signal bound.
	 *
	 * @param signalBound
	 *            the new signal bound
	 */
	public void setSignalBound(boolean signalBound) {
		channelsPanel.setSignalBound(signalBound);
	}

	/**
	 * Sets the correct values from montage to be visible and editable
	 * in all panels contanined in this panel.
	 *
	 * @param montage the montage to be applied
	 */
	public void setMontageToPanels(Montage montage) {
		generatorPanel.setMontage(montage);
		channelsPanel.setMontage(montage);
		visualReferenceEditorPanel.setMontage(montage);
		matrixReferenceEditorPanel.setMontage(montage);
	}

	public void setErrorsDialog(ValidationErrorsDialog errorsDialog) {
		generatorPanel.setErrorsDialog(errorsDialog);
	}

}

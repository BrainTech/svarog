package org.signalml.app.view.montage;

import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.element.AbstractSignalMLPanel;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.generators.IMontageGenerator;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * This class represents a panel for editing the signal's montage.
 * Contains a dropdown list for selecting a montage generator
 * and the tabs for selecting which channels should be included in the
 * montage and what are the references between the channels.
 *
 * @author Piotr Szachewicz
 */
public class MontageEditionPanel extends AbstractSignalMLPanel {

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
	 * @param messageSourceAccessor
	 */
	public MontageEditionPanel(MessageSourceAccessor messageSourceAccessor) {
		super(messageSourceAccessor);
		initialize();
	}

	@Override
	protected void initialize() {

		generatorPanel = new MontageGeneratorPanel(messageSource);

		channelsPanel = new MontageChannelsPanel(messageSource);
		matrixReferenceEditorPanel = new MatrixReferenceEditorPanel(messageSource);
		visualReferenceEditorPanel = new VisualReferenceEditorPanel(messageSource);

		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(messageSource.getMessage("signalMontage.channelsTabTitle"), channelsPanel);
		tabbedPane.addTab(messageSource.getMessage("signalMontage.visualTabTitle"), visualReferenceEditorPanel);
		tabbedPane.addTab(messageSource.getMessage("signalMontage.matrixTabTitle"), matrixReferenceEditorPanel);

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

	/**
	 * Sets the {@link ErrorsDialog} to be used by this panel to inform
	 * the user about errors.
	 * @param errorsDialog the {@link ErrorsDialog} to be used
	 */
	public void setErrorsDialog(ErrorsDialog errorsDialog) {
		generatorPanel.setErrorsDialog(errorsDialog);
	}

}

/* EditMontageReferencePanel.java created 2007-10-24
 *
 */
package org.signalml.app.view.montage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.apache.log4j.Logger;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.montage.MontageGeneratorListModel;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.components.ResolvableComboBox;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.common.dialogs.errors.ValidationErrorsDialog;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageException;
import org.signalml.domain.montage.SourceChannel;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.montage.SourceMontageEvent;
import org.signalml.domain.montage.SourceMontageListener;
import org.signalml.domain.montage.generators.IMontageGenerator;
import org.springframework.validation.Errors;

/**
 * The panel which allows to select the {@link MontageGenerator montage
 * generator} and generate a {@link Montage montage} using it. The new montage
 * is generated on the basis of the {@link #getMontage()
 * current montage}.
 *
 * This panel contains the {@link #getGeneratorComboBox() generator combo-box}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 * Sp. z o.o.
 */
public class MontageGeneratorPanel extends JPanel {

	/**
	 * the default serialization constant.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the logger.
	 */
	protected static final Logger logger = Logger.getLogger(MontageGeneratorPanel.class);

	/**
	 * the {@link Montage montage} that is edited.
	 */
	private Montage montage;

	/**
	 * the combo-box which allows to select the {@link MontageGenerator montage
	 * genarator}. When the generator is selected from the list it is used to {@link
	 * #tryGenerate(MontageGenerator) generate} a new {@link Montage} on the
	 * basis of the {@link #montage old one}.
	 */
	private JComboBox generatorComboBox;

	/**
	 * the {@link MontageGeneratorListModel model} for the
	 * {@link #generatorComboBox}.
	 */
	private MontageGeneratorListModel montageGeneratorListModel;

	/**
	 * the dialog with errors which is shown when:
	 * <ul>
	 * <li>the {@link #getMontage() montage} can not be used to generate the.
	 * {@link Montage montage} using a {@link #getGeneratorComboBox() selected}
	 * {@link MontageGenerator generator},</li>
	 * <li>when {@link #getShowErrorsAction() ShowErrorsAction} is
	 * performed,</li>
	 * </ul>
	 */
	private ValidationErrorsDialog errorsDialog;

	/**
	 * the listener associated with the addition/removal/change of a.
	 * {@link SourceChannel source channel} in a {@link #getMontage() montage}
	 */
	private SourceMontageChangeListener sourceMontageChangeListener;

	/**
	 * Constructor. Sets the source of messages and {@link #initialize()
	 * initializes} this panel
	 */
	public MontageGeneratorPanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with 3 elements:
	 * <ul>
	 * <li>the {@link #getGeneratorComboBox() generator combo-box},</li>
	 * <li>the {@link #getReloadButton() reload button},</li>
	 * <li>the {@link #getShowErrorsButton() show errors button},</li></ul>
	 */
	private void initialize() {

		sourceMontageChangeListener = new SourceMontageChangeListener();

		setLayout(new BorderLayout());

		JPanel choicePanel = new JPanel();
		choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.X_AXIS));

		CompoundBorder border = new CompoundBorder(
				new TitledBorder(_("Choose generator")),
				new EmptyBorder(3, 3, 3, 3)
		);
		choicePanel.setBorder(border);

		choicePanel.add(new JLabel(_("Generator")));
		choicePanel.add(Box.createHorizontalStrut(5));
		choicePanel.add(Box.createHorizontalGlue());
		choicePanel.add(getGeneratorComboBox());

		add(choicePanel, BorderLayout.CENTER);

	}

	/**
	 * Gets the {@link Montage montage} that is edited.
	 *
	 * @return the {@link Montage montage} that is edited
	 */
	public Montage getMontage() {
		return montage;
	}

	/**
	 * Sets the {@link Montage montage} that is edited.
	 *
	 * @param montage the new {@link Montage montage} that is edited
	 */
	public void setMontage(Montage montage) {
		if (this.montage != montage) {
			if (this.montage != null) {
				this.montage.removeSourceMontageListener(sourceMontageChangeListener);
			}
			this.montage = montage;
			if (montage != null) {
				montage.addSourceMontageListener(sourceMontageChangeListener);
				getMontageGeneratorListModel().setEegSystem(montage.getEegSystem());
			} else {
				getMontageGeneratorListModel().setEegSystem(null);
			}
		}
	}

	/**
	 * Gets the validation dialog with errors which is shown when:
	 * <ul>
	 * <li>the {@link #getMontage() montage} can not be used to generate the.
	 *
	 * @return the {@link ValidationErrorsDialog} with errors which is shown
	 * when:
	 * <ul>
	 * <li>the {@link #getMontage() montage} can not be used to generate the
	 */
	public ValidationErrorsDialog getErrorsDialog() {
		return errorsDialog;
	}

	/**
	 * Sets the dialog with errors which is shown when:
	 * <ul>
	 * <li>the {@link #getMontage() montage} can not be used to generate the.
	 *
	 * @param errorsDialog the new validation errors dialog with errors which is
	 * shown when:
	 * <ul>
	 * <li>the {@link #getMontage() montage} can not be used to generate the
	 */
	public void setErrorsDialog(ValidationErrorsDialog errorsDialog) {
		this.errorsDialog = errorsDialog;
	}

	/**
	 * Gets the {@link MontageGeneratorListModel model} for the
	 * {@link #generatorComboBox}.
	 *
	 * @return the {@link MontageGeneratorListModel model} for the
	 * {@link #generatorComboBox}
	 */
	protected MontageGeneratorListModel getMontageGeneratorListModel() {
		if (montageGeneratorListModel == null) {
			montageGeneratorListModel = new MontageGeneratorListModel(
				_("(choose a pre-defined preset to reset montage)")
			);
		}
		return montageGeneratorListModel;
	}

	/**
	 * Gets the combo-box which allows to select the {@link MontageGenerator
	 * montage genarator}.
	 *
	 * @return the combo-box which allows to select the {@link MontageGenerator
	 *         montage genarator}
	 */
	public JComboBox getGeneratorComboBox() {
		if (generatorComboBox == null) {
			generatorComboBox = new ResolvableComboBox();
			generatorComboBox.setModel(getMontageGeneratorListModel());
			generatorComboBox.setPreferredSize(new Dimension(300, 25));

			generatorComboBox.addActionListener((ActionEvent e) -> {
				if (montage == null || generatorComboBox.getSelectedIndex() == 0) {
					return;
				}

				IMontageGenerator generator = (IMontageGenerator) generatorComboBox.getSelectedItem();
				generatorComboBox.setSelectedIndex(0);
				tryGenerate(generator);
			});

		}
		return generatorComboBox;
	}

	/**
	 * Tries to generate a new {@link Montage montage} on the basis of the
	 * current {@link #getMontage() montage} using the provided {@link
	 * MontageGenerator montage generator}:
	 * <ol>
	 * <li>{@link MontageGenerator#validateSourceMontage(SourceMontage, Errors)
	 * Checks} if the current montage can be used with the provided generator.
	 * If not shows the errors.</li>
	 * <li>{@link MontageGenerator#createMontage(Montage) creates} the montage
	 * on the basis of the current montage,</li>
	 * </ol>
	 *
	 * @param generator the {@link MontageGenerator montage generator} to be
	 * used
	 */
	public void tryGenerate(IMontageGenerator generator) {

		ValidationErrors errors = new ValidationErrors();
		generator.validateSourceMontage(montage, errors);

		if (errors.hasErrors()) {
			errorsDialog.showDialog(errors);
			return;
		}

		try {
			generator.createMontage(montage);
		} catch (MontageException ex) {
			logger.error("Montage generation failed", ex);
			Dialogs.showExceptionDialog(this, ex);
		}

	}

	/**
	 * The listener associated with the addition/removal/change of a.
	 *
	 * {@link SourceChannel source channel} in a montage.
	 * <p>
	 * When any of these events occurs:
	 * <ul>
	 * <li>updates the state of the
	 * {@link MontageGeneratorPanel#getReloadAction() reload} and
	 * {@link MontageGeneratorPanel#getShowErrorsAction() show errors}
	 * actions,</li>
	 * <li>{@link MontageGenerator#validateSourceMontage(SourceMontage, Errors)
	 * validates} the {@link Montage montage}.</li>
	 * </ul>
	 */
	protected class SourceMontageChangeListener implements SourceMontageListener {


		/**
		 * @see #onChange()
		 */
		@Override
		public void sourceMontageChannelAdded(SourceMontageEvent ev) {
			// nothing here
		}

		/**
		 * @see #onChange()
		 */
		@Override
		public void sourceMontageChannelChanged(SourceMontageEvent ev) {
			// nothing here
		}

		/**
		 * @see #onChange()
		 */
		@Override
		public void sourceMontageChannelRemoved(SourceMontageEvent ev) {
			// nothing here
		}

		@Override
		public void sourceMontageEegSystemChanged(SourceMontageEvent ev) {
			getMontageGeneratorListModel().setEegSystem(montage.getEegSystem());
		}

	}

}

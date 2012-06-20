/* AbstractWizardDialog.java created 2007-09-17
 *
 */

package org.signalml.app.view.components.dialogs;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.components.dialogs.errors.Dialogs;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.validation.Errors;


/**
 * Abstract dialog which can have more then one step.
 * Contains:
 * <ul>
 * <li>the panel with CardLayout in which the interface of steps are displayed,
 * </li><li>the extended control panel with buttons to go to the {@link
 * NextAction next} and {@link PreviousAction previous} step,</li>
 * <li>functions that should be implemented by sub-classes:<ul><li>
 * creation of the interface for the step,</li><li>
 * {@link #validateDialogStep(int, Object, Errors) validation} of the step,</li>
 * <li>the operations that should be performed when the step changes.</li></ul>
 * <li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractWizardDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	/**
	 * the interface for this dialog with the CardLayout and the number
	 * of cards equal to the {@link #getStepCount() number} of steps
	 */
	private JPanel interfacePanel;
	/**
	 * the interfaces for steps; the number of elements in this array equals
	 * the number of steps
	 */
	private JComponent[] interfaceStepComponents;

	/**
	 * the layout for {@link #interfacePanel}
	 */
	private CardLayout cardLayout;

	/**
	 * {@link NextAction action} to go to the next step
	 */
	private NextAction nextAction;
	/**
	 * {@link PreviousAction action} to go to the previous step
	 */
	private PreviousAction previousAction;

	/**
	 * the button to {@link #nextAction go} to the next step
	 */
	private JButton nextButton;

	/**
	 * the button to {@link #previousAction go} to the previous step
	 */
	private JButton previousButton;

	private int currentStep = 0;

	/**
	 * Constructor. Sets message source, parent window and if this dialog
	 * blocks top-level windows.
	 * @param f the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public AbstractWizardDialog(Window f, boolean isModal) {
		super(f, isModal);
	}

	/**
	 * Returns the number of steps this dialog contains.
	 * @return the number of steps
	 */
	public abstract int getStepCount();

	/**
	 * Returns the interface for the given step.
	 * If such interface doesn't exist it is {@link #createInterface()
	 * created}.
	 * @param step the number of the step
	 * @return the interface for the step
	 */
	public JComponent getInterfaceForStep(int step) {
		if (interfaceStepComponents == null) {
			interfaceStepComponents = new JComponent[getStepCount()];
		}
		if (interfaceStepComponents[step] == null) {
			interfaceStepComponents[step] = createInterfaceForStep(step);
		}
		return interfaceStepComponents[step];
	}

	/**
	 * Creates the interface for the given step.
	 * Components of this interface depend on the implementation.
	 * @param step the number of the step
	 * @return the interface for the step
	 */
	protected abstract JComponent createInterfaceForStep(int step);

	/**
	 * Validates the given step.
	 * @param step the number of the step to be validated
	 * @param model the model for this dialog (not used in current
	 * implementations)
	 * @param errors the object in which the errors will be stored
	 * @throws SignalMLException depends on the implementation
	 */
	public void validateDialogStep(int step, Object model, ValidationErrors errors) throws SignalMLException {
		/* do nothing */
	}

	/**
	 * {@link #validateDialogStep(int, Object, Errors) Validates} all steps of
	 * this dialog.
	 */
	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {
		// default implementation revalidates all steps
		for (int i=0; i<getStepCount(); i++) {
			validateDialogStep(i, model, errors);
		}
	}

	/**
	 * Creates the interface for this dialog with the CardLayout and the number
	 * of cards equal to the {@link #getStepCount() number} of steps.
	 * Each card contains the {@link #getInterfaceForStep(int) interface} for
	 * for the consecutive step.
	 */
	@Override
	public JComponent createInterface() {

		interfacePanel = new JPanel();
		cardLayout = new CardLayout();
		interfacePanel.setLayout(cardLayout);

		int stepCount = Math.max(0, getStepCount());

		for (int i=0; i<stepCount; i++) {
			interfacePanel.add(getInterfaceForStep(i), "step"+i);
		}

		return interfacePanel;

	}

	/**
	 * Creates the control panel that allows to (buttons from left to right):
	 * <ul>
	 * <li>go to the {@link PreviousAction previous} and {@link NextAction
	 * next} step,</li>
	 * <li>finish (if all steps are completed) or cancel this dialog.</li>
	 * </ul>
	 */
	@Override
	protected JPanel createControlPane() {

		JPanel controlPane = super.createControlPane();
		controlPane.setBorder(new EmptyBorder(8, 4, 2, 4));

		getOkAction().putValue(AbstractAction.NAME, _("Finish"));
		getOkButton().setPreferredSize(new Dimension(120, 25));

		controlPane.add(Box.createHorizontalStrut(5), 1);

		nextAction = new NextAction();
		nextButton = new JButton(nextAction);
		nextButton.setHorizontalTextPosition(JButton.LEADING);
		nextButton.setPreferredSize(new Dimension(130, 25));
		getRootPane().setDefaultButton(nextButton);
		controlPane.add(nextButton, 1);

		controlPane.add(Box.createHorizontalStrut(3), 1);

		previousAction = new PreviousAction();
		previousButton = new JButton(previousAction);
		previousButton.setPreferredSize(new Dimension(130, 25));
		controlPane.add(previousButton, 1);
		previousAction.setEnabled(false);

		return controlPane;

	}

	/**
	 * Goes back to step 0.
	 */
	@Override
	protected void resetDialog() {
		super.resetDialog();
		currentStep = 0;
		getOkAction().setEnabled(isFinishAllowedOnStep(0));
		previousAction.setEnabled(false);
		nextAction.setEnabled(true);
		getRootPane().setDefaultButton(nextButton);
		cardLayout.show(interfacePanel,"step0");
	}

	/**
	 * Performs operations necessary when a step is changed.
	 * Depends on an implementation.
	 * @param toStep the number of the step before the change
	 * @param fromStep the number of the step after the change
	 * @param model the model for this dialog
	 * @return {@code true} if operation is successful, {@code false} otherwise
	 * @throws SignalMLException never thrown in implementations
	 */
	protected boolean onStepChange(int toStep, int fromStep, Object model) throws SignalMLException {
		return true;
	}

	/**
	 * Returns if this dialog can be finished on a given step.
	 * @param step the number of the step
	 * @return {@code true} if this dialog can be finished (and OK button
	 * should be active), {@code false} otherwise
	 */
	public boolean isFinishAllowedOnStep(int step) {
		return true;
	}

	/**
	 * Returns if validation should be performed when the step is changed
	 * back.
	 * By default false.
	 * @return {@code false}
	 */
	public boolean isBackTransitionValidated() {
		return false;
	}

	/**
	 * Abstract action performed when a user changes the step (presses
	 * {@link PreviousAction previous} or {@link NextAction next} button.
	 *
	 */
	protected class NavigationAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		/**
		 * the number of steps that should be navigated back (negative value)
		 * or forward (positive value)
		 */
		protected int step;

		/**
		 * Constructor.
		 * Sets the step change and the text of the button for this action.
		 * @param step the number of steps that should be navigated back (negative value)
		 * or forward (positive value)
		 * @param text the text of the button for this action
		 */
		public NavigationAction(int step, String text) {
			super(text);
			this.step = step;
		}

		/**
		 * Performed when a user changes the step (presses
		 * {@link PreviousAction previous} or {@link NextAction next} button:
		 * <ul>
		 * <li>{@link AbstractWizardDialog#validateDialogStep(int, Object,
		 * Errors) validates} the current step - if the step is not valid
		 * {@link AbstractWizardDialog#showValidationErrors(Errors) shows}
		 * errors and returns,</li>
		 * <li>{@link AbstractWizardDialog#onStepChange(int, int, Object)
		 * performs} operations necessary when the step is changed,</li>
		 * <li>enables or disables buttons:
		 * <ul><li> {@link PreviousAction previous} and {@link NextAction
		 * next} button depending on the number of the step (if it is first,
		 * last or none of those),</li>
		 * <li> {@link AbstractWizardDialog#getOkAction() OK} button depending
		 * on whether dialog
		 * {@link AbstractWizardDialog#isFinishAllowedOnStep(int) can be
		 * finished} or not.</li></ul>
		 * </ul>
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			if (isBackTransitionValidated() || step > 0) {
				ValidationErrors errors = new ValidationErrors();
				try {
					validateDialogStep(currentStep,getCurrentModel(),errors);
				} catch (SignalMLException ex) {
					logger.error("Dialog validation threw an exception", ex);
					Dialogs.showExceptionDialog(AbstractWizardDialog.this, ex);
					setCurrentModel(null);
					setClosedWithOk(false);
					setVisible(false);
					return;
				}

				if (errors.hasErrors()) {
					showValidationErrors(errors);
					return;
				}
			}

			try {
				boolean allow = onStepChange(currentStep+step, currentStep, getCurrentModel());
				if (!allow) {
					return;
				}
			} catch (SignalMLException ex) {
				logger.error("Dialog transition threw an exception", ex);
				Dialogs.showExceptionDialog(AbstractWizardDialog.this, ex);
				setCurrentModel(null);
				setClosedWithOk(false);
				setVisible(false);
				return;
			}

			currentStep += step;

			cardLayout.show(interfacePanel, "step"+currentStep);

			if (currentStep == 0) {
				previousAction.setEnabled(false);
			} else {
				previousAction.setEnabled(true);
			}

			getOkAction().setEnabled(isFinishAllowedOnStep(currentStep));

			if (currentStep == (getStepCount()-1)) {
				nextAction.setEnabled(false);
				getRootPane().setDefaultButton(getOkButton());
			} else {
				nextAction.setEnabled(true);
				getRootPane().setDefaultButton(nextButton);
			}

			getOkButton().repaint();

		}

	}

	/**
	 * Action performed when a user presses {@code next} button.
	 * It is a {@link NavigationAction} with a step {@code 1}.
	 */
	protected class NextAction extends NavigationAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the step, the message and the icon.
		 */
		public NextAction() {
			super(1,_("Next"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/next.png"));
		}

		/**
		 * Performed when a user presses {@code next} button.
		 * If the number of the next step exceeds the number of steps
		 * warning is displayed and no action is taken.
		 * Otherwise parent {@link NavigationAction#actionPerformed(ActionEvent)
		 * function} is called.
		 */
		public void actionPerformed(ActionEvent ev) {

			if ((currentStep + 1) >= getStepCount()) {
				logger.warn("WARNING: attempt to navigate past step count");
				return;
			}

			super.actionPerformed(ev);

		}

	}

	/**
	 * Action performed when a user presses {@code previous} button.
	 * It is a {@link NavigationAction} with a step {@code -1}.
	 */
	protected class PreviousAction extends NavigationAction {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor. Sets the step, the message and the icon.
		 */
		public PreviousAction() {
			super(-1,_("Back"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/previous.png"));
		}

		/**
		 * Performed when a user presses {@code previous} button.
		 * If the number of the previous step exceeds the number of steps
		 * warning is displayed and no action is taken.
		 * Otherwise parent {@link NavigationAction#actionPerformed(ActionEvent)
		 * function} is called.
		 */
		public void actionPerformed(ActionEvent ev) {

			if ((currentStep + step) < 0) {
				logger.warn("WARNING: attempt to navigate back past the first element");
				return;
			}

			super.actionPerformed(ev);

		}

	}
}

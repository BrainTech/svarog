/* AbstractWizardDialog.java created 2007-09-17
 *
 */

package org.signalml.app.view.dialog;

import java.awt.CardLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.signalml.app.util.IconUtils;
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/** AbstractWizardDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractWizardDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private JPanel interfacePanel;
	private JComponent[] interfaceStepComponents;
	private CardLayout cardLayout;

	private NextAction nextAction;
	private PreviousAction previousAction;

	private JButton nextButton;
	private JButton previousButton;

	private int currentStep = 0;

	public AbstractWizardDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public AbstractWizardDialog(MessageSourceAccessor messageSource,Window f, boolean isModal) {
		super(messageSource, f, isModal);
	}

	public abstract int getStepCount();

	public JComponent getInterfaceForStep(int step) {
		if (interfaceStepComponents == null) {
			interfaceStepComponents = new JComponent[getStepCount()];
		}
		if (interfaceStepComponents[step] == null) {
			interfaceStepComponents[step] = createInterfaceForStep(step);
		}
		return interfaceStepComponents[step];
	}

	protected abstract JComponent createInterfaceForStep(int step);

	public void validateDialogStep(int step, Object model, Errors errors) throws SignalMLException {
		/* do nothing */
	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		// default implementation revalidates all steps
		for (int i=0; i<getStepCount(); i++) {
			validateDialogStep(i, model, errors);
		}
	}

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

	@Override
	protected JPanel createControlPane() {

		JPanel controlPane = super.createControlPane();

		getOkAction().putValue(AbstractAction.NAME, messageSource.getMessage("finish"));

		controlPane.add(Box.createHorizontalStrut(5), 1);

		nextAction = new NextAction();
		nextButton = new JButton(nextAction);
		nextButton.setHorizontalTextPosition(JButton.LEADING);
		getRootPane().setDefaultButton(nextButton);
		controlPane.add(nextButton, 1);

		controlPane.add(Box.createHorizontalStrut(3), 1);

		previousAction = new PreviousAction();
		previousButton = new JButton(previousAction);
		controlPane.add(previousButton, 1);
		previousAction.setEnabled(false);

		return controlPane;

	}

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

	protected boolean onStepChange(int toStep, int fromStep, Object model) throws SignalMLException {
		return true;
	}

	public boolean isFinishAllowedOnStep(int step) {
		return true;
	}

	public boolean isBackTransitionValidated() {
		return false;
	}

	protected class NavigationAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		protected int step;

		public NavigationAction(int step, String text) {
			super(text);
			this.step = step;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			if (isBackTransitionValidated() || step > 0) {
				Errors errors = new BindException(getCurrentModel(), "data");
				try {
					validateDialogStep(currentStep,getCurrentModel(),errors);
				} catch (SignalMLException ex) {
					logger.error("Dialog validation threw an exception", ex);
					ErrorsDialog.showImmediateExceptionDialog(AbstractWizardDialog.this, ex);
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
				ErrorsDialog.showImmediateExceptionDialog(AbstractWizardDialog.this, ex);
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

	protected class NextAction extends NavigationAction {

		private static final long serialVersionUID = 1L;

		public NextAction() {
			super(1,messageSource.getMessage("next"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/next.png"));
		}

		public void actionPerformed(ActionEvent ev) {

			if ((currentStep + 1) >= getStepCount()) {
				logger.warn("WARNING: attempt to navigate past step count");
				return;
			}

			super.actionPerformed(ev);

		}

	}

	protected class PreviousAction extends NavigationAction {

		private static final long serialVersionUID = 1L;

		public PreviousAction() {
			super(-1,messageSource.getMessage("back"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/previous.png"));
		}

		public void actionPerformed(ActionEvent ev) {

			if ((currentStep + step) < 0) {
				logger.warn("WARNING: attempt to navigate back past the first element");
				return;
			}

			super.actionPerformed(ev);

		}

	}


}

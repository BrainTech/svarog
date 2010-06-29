/* StagerExpertTagPanel.java created 2008-02-21
 *
 */
package org.signalml.app.method.stager;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** StagerExpertTagPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class StagerExpertTagPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MessageSourceAccessor messageSource;

	private JTextField tagTextField;
	private JButton chooseTagButton;

	private ViewerFileChooser fileChooser;

	private File tagFile;

	private String labelCode;
	private String chooseButtonToolTipCode;

	public StagerExpertTagPanel(MessageSourceAccessor messageSource, ViewerFileChooser fileChooser) {
		super();
		this.messageSource = messageSource;
		this.fileChooser = fileChooser;
	}

	public void initialize() {

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel tagFileLabel = new JLabel(messageSource.getMessage(labelCode));

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		Component glue = Box.createHorizontalGlue();

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(tagFileLabel)
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(glue)
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(getTagTextField())
		);

		hGroup.addGroup(
		        layout.createParallelGroup()
		        .addComponent(getChooseTagButton())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
		        layout.createParallelGroup(Alignment.BASELINE)
		        .addComponent(tagFileLabel)
		        .addComponent(glue)
		        .addComponent(getTagTextField())
		        .addComponent(getChooseTagButton())
		);

		layout.setVerticalGroup(vGroup);

	}

	public JTextField getTagTextField() {
		if (tagTextField == null) {
			tagTextField = new JTextField();
			Dimension dimension = new Dimension(300,25);
			tagTextField.setPreferredSize(dimension);
			tagTextField.setMinimumSize(dimension);
			tagTextField.setMaximumSize(dimension);
			tagTextField.setEditable(false);
		}
		return tagTextField;
	}

	public JButton getChooseTagButton() {
		if (chooseTagButton == null) {
			chooseTagButton = new JButton(new ChooseTagFileAction());
		}
		return chooseTagButton;
	}

	public void validatePanel(Errors errors) {

		// nothing to do

	}

	public File getTagFile() {
		return tagFile;
	}

	public void setTagFile(File tagFile) {
		this.tagFile = tagFile;
		if (tagFile == null) {
			getTagTextField().setText("");
		} else {
			getTagTextField().setText(tagFile.getAbsolutePath());
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		getTagTextField().setEnabled(enabled);
		getChooseTagButton().setEnabled(enabled);
		super.setEnabled(enabled);
	}

	public String getLabelCode() {
		return labelCode;
	}

	public void setLabelCode(String labelCode) {
		this.labelCode = labelCode;
	}

	public String getChooseButtonToolTipCode() {
		return chooseButtonToolTipCode;
	}

	public void setChooseButtonToolTipCode(String chooseButtonToolTipCode) {
		this.chooseButtonToolTipCode = chooseButtonToolTipCode;
	}

	protected class ChooseTagFileAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ChooseTagFileAction() {
			super(messageSource.getMessage("stagerMethod.dialog.resultReview.chooseExpertTagFile"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/find.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage(chooseButtonToolTipCode));
		}

		public void actionPerformed(ActionEvent ev) {

			File file = fileChooser.chooseExpertTag(StagerExpertTagPanel.this.getTopLevelAncestor());
			if (file == null) {
				return;
			}

			tagFile = file;

			getTagTextField().setText(tagFile.getAbsolutePath());

		}

	}

}

/* ArtifactExpertTagPanel.java created 2008-02-21
 *
 */
package org.signalml.app.method.artifact;

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
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.ViewerFileChooser;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** ArtifactExpertTagPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ArtifactExpertTagPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected MessageSourceAccessor messageSource;

	private JTextField tagTextField;
	private JButton chooseTagButton;

	protected ViewerFileChooser fileChooser;

	protected File tagFile;

	private String labelCode;
	private String chooseButtonToolTipCode;

	public ArtifactExpertTagPanel(MessageSourceAccessor messageSource, ViewerFileChooser fileChooser) {
		super();
		this.messageSource = messageSource;
		this.fileChooser = fileChooser;
		initialize();
	}

	public void initialize() {

		setBorder(new CompoundBorder(
		                  new TitledBorder(messageSource.getMessage("artifactMethod.dialog.iteration.expertTagTitle")),
		                  new EmptyBorder(3,3,3,3)
		          ));

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel tagFileLabel = new JLabel(messageSource.getMessage("artifactMethod.dialog.iteration.expertTag"));

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

		if (tagFile == null || !tagFile.exists() || !tagFile.canRead()) {
			errors.rejectValue("expertTagFile", "error.artifact.badTagFilePath");
		}

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
			super(messageSource.getMessage("artifactMethod.dialog.iteration.choose"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/find.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,messageSource.getMessage("artifactMethod.dialog.iteration.expertTagToolTip"));
		}

		public void actionPerformed(ActionEvent ev) {

			File file = fileChooser.chooseExpertTag(ArtifactExpertTagPanel.this.getTopLevelAncestor());
			if (file == null) {
				return;
			}

			tagFile = file;

			getTagTextField().setText(tagFile.getAbsolutePath());

		}

	}

}

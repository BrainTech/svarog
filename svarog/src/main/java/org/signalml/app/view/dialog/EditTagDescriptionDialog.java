/* EditTagDescriptionDialog.java created 2007-11-22
 *
 */

package org.signalml.app.view.dialog;

import static org.signalml.app.SvarogApplication._;
import java.awt.Dimension;
import java.awt.Window;
import java.lang.String;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.signalml.app.document.TagDocument;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.element.TextPanePanel;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.SignalMLException;

/**
 * Dialog which allows to specify the description of a {@link TagDocument}.
 * Contains two panels:
 * <ul>
 * <li>the {@link TextPanePanel text panel} for the
 * {@link StyledTagSet#setInfo(String) description} of the document,</li>
 * <li>the text panel for the {@link StyledTagSet#setMontageInfo(String)
 * description} of the {@link Montage montage}.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EditTagDescriptionDialog extends org.signalml.app.view.dialog.AbstractSvarogDialog  {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link TextPanePanel text panel} for the {@link StyledTagSet#setInfo(String)
	 * description} of the {@link StyledTagSet tag set} for
	 * a specified {@link TagDocument}
	 */
	private TextPanePanel textInfoPanel;
	/**
	 * the {@link TextPanePanel text panel} for the {@link StyledTagSet#setMontageInfo(String)
	 * description} of the {@link Montage montage} for
	 * a specified {@link TagDocument}
	 */
	private TextPanePanel montageInfoPanel;

	/**
	 * Constructor. Sets parent window and if this dialog
	 * blocks top-level windows.
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public EditTagDescriptionDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	/**
	 * The model for this dialog has to be of type {@link TagDocument}.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return TagDocument.class.isAssignableFrom(clazz);
	}

	/**
	 * Using the {@link StyledTagSet tag set} for the provided {@link
	 * TagDocument} sets the {@link StyledTagSet#getInfo() description}
	 * of the tag set and the {@link StyledTagSet#getMontageInfo() information}
	 * about the {@link Montage} in the text fields.
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		TagDocument tagDocument = (TagDocument) model;
		StyledTagSet tagSet = tagDocument.getTagSet();
		String description = tagSet.getInfo();
		textInfoPanel.getTextPane().setText(description != null ? description : "");
		description = tagSet.getMontageInfo();
		montageInfoPanel.getTextPane().setText(description != null ? description : "");
	}

	/**
	 * Using the user input sets the {@link StyledTagSet#getInfo() description}
	 * of the {@link StyledTagSet tag set} and the
	 * {@link StyledTagSet#getMontageInfo() information} about the {@link Montage} in the
	 * tag set for the {@link TagDocument} provided as a model.
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		TagDocument tagDocument = (TagDocument) model;
		String description = textInfoPanel.getTextPane().getText();
		if (description.isEmpty()) {
			description = null;
		}
		tagDocument.getTagSet().setInfo(description);
	}

	/**
	 * Sets the title and the icon of this dialog and calls the
	 * {@link AbstractDialog#initialize() initialization} in the parent.
	 */
	@Override
	protected void initialize() {
		setTitle(_("Set tag document description"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/edittagdescription.png"));
		super.initialize();
	}

	/**
	 * Creates the interface for this dialog, which consists of two panels:
	 * <ul>
	 * <li>the {@link TextPanePanel text panel} for the {@link StyledTagSet#setInfo(String)
	 * description} of the {@link StyledTagSet tag set},</li>
	 * <li>the text panel for the {@link StyledTagSet#setMontageInfo(String)
	 * description} of the {@link Montage montage}.</li>
	 * </ul>
	 */
	@Override
	public JComponent createInterface() {

		textInfoPanel = new TextPanePanel(_("Tag document description"));
		textInfoPanel.setPreferredSize(new Dimension(400,200));

		montageInfoPanel = new TextPanePanel(_("Montage description"));
		montageInfoPanel.setPreferredSize(new Dimension(400,200));
		montageInfoPanel.getTextPane().setEditable(false);

		JPanel interfacePanel = new JPanel();
		interfacePanel.setLayout(new BoxLayout(interfacePanel, BoxLayout.Y_AXIS));
		interfacePanel.add(textInfoPanel);
		interfacePanel.add(montageInfoPanel);

		return interfacePanel;

	}

}

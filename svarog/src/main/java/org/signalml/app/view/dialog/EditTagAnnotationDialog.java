/* EditTagAnnotationDialog.java created 2007-10-23
 *
 */

package org.signalml.app.view.dialog;

import static org.signalml.app.SvarogI18n._;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JComponent;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.element.TextPanePanel;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Tag;

/**
 * Dialog which allows to change the {@link Tag#getAnnotation() annotation} for
 * the {@link Tag}.
 * Contains a single {@link TextPanePanel} in which the annotation can be
 * entered.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EditTagAnnotationDialog extends AbstractDialog  {

	private static final long serialVersionUID = 1L;

	private TextPanePanel textPanePanel;

	/**
	 * Constructor. Sets parent window and if this dialog
	 * blocks top-level windows.
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public EditTagAnnotationDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	/**
	 * The model for this dialog has to have type {@link Tag}.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return Tag.class.isAssignableFrom(clazz);
	}

	/**
	 * Sets the current {@link Tag#getAnnotation() annotation} of the
	 * {@link Tag} provided as a model in the text pane.
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		Tag tag = (Tag) model;
		String annotation = tag.getAnnotation();
		textPanePanel.getTextPane().setText(annotation != null ? annotation : "");
	}

	/**
	 * Sets the user input as the {@link Tag#setAnnotation(String) annotation} of the
	 * {@link Tag} provided as a model.
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		Tag tag = (Tag) model;
		String annotation = textPanePanel.getTextPane().getText();
		if (annotation.isEmpty()) {
			annotation = null;
		}
		tag.setAnnotation(annotation);
	}

	/**
	 * Sets the title and the icon of this dialog and calls the
	 * {@link AbstractDialog#initialize() initialization} in the parent.
	 */
	@Override
	protected void initialize() {
		setTitle(_("Set tag annotation"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/editannotation.png"));
		super.initialize();
	}

	/**
	 * Returns the {@link TextPanePanel} as the interface for this dialog.
	 */
	@Override
	public JComponent createInterface() {

		textPanePanel = new TextPanePanel(_("Set tag annotation"));
		textPanePanel.setPreferredSize(new Dimension(300,200));

		return textPanePanel;
	}

}

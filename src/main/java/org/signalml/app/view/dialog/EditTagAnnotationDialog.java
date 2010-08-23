/* EditTagAnnotationDialog.java created 2007-10-23
 *
 */

package org.signalml.app.view.dialog;

import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JComponent;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.element.TextPanePanel;
import org.signalml.domain.tag.Tag;
import org.signalml.exception.SignalMLException;
import org.signalml.util.Util;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** EditTagAnnotationDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EditTagAnnotationDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private TextPanePanel textPanePanel;

	public EditTagAnnotationDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public EditTagAnnotationDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return Tag.class.isAssignableFrom(clazz);
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		Tag tag = (Tag) model;
		String annotation = tag.getAnnotation();
		textPanePanel.getTextPane().setText(annotation != null ? annotation : "");
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		Tag tag = (Tag) model;
		String annotation = textPanePanel.getTextPane().getText();
		if (annotation.isEmpty()) {
			annotation = null;
		}
		tag.setAnnotation(annotation);
	}

	@Override
	protected void initialize() {
		setTitle(messageSource.getMessage("tagAnnotation.title"));
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/editannotation.png"));
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		textPanePanel = new TextPanePanel(messageSource.getMessage("tagAnnotation.title"));
		textPanePanel.setPreferredSize(new Dimension(300,200));

		return textPanePanel;
	}

}

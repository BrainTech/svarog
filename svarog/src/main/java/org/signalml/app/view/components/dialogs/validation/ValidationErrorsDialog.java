package org.signalml.app.view.components.dialogs.validation;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.util.IconUtils;
import org.signalml.app.view.components.dialogs.AbstractDialog;
import org.signalml.plugin.export.SignalMLException;

/**
 * Dialog for showing {@link ValidationErrors}.
 * 
 * @author Piotr Szachewicz
 */
public class ValidationErrorsDialog extends AbstractDialog {
	/**
	 * An enum containing possible user responses in a dialog.
	 */
	public static enum DIALOG_OPTIONS { YES, NO };
	
	/**
	 * The preferred size of the scroll pane which contains the errors jlist.
	 */
	private static Dimension scrollPanePreferredDimensions = new Dimension(420, 250);

	/**
	 * the list on which errors are displayed
	 */
	private JList errorList = null;

	/**
	 * the code to obtain the title for this dialog from the source of messages;
	 * if the code is {@code null} the default title is used
	 */
	private String titleCode = null;

	/**
	 * Constructor. Sets parent window and if this dialog
	 * blocks top-level windows.
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public ValidationErrorsDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	/**
	 * Constructor. Sets parent window and if this dialog
	 * blocks top-level windows.
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 * @param titleCode  the code to obtain the title for this dialog from the
	 * source of messages; if the code is {@code null} the default title is used
	 */
	public ValidationErrorsDialog(Window w, boolean isModal, String titleCode) {
		super(w, isModal);
		this.titleCode = titleCode;
	}

	/**
	 * Sets the title and the icon and calls the {@link AbstractDialog#initialize()
	 * initialization} in parent.
	 * The title is obtained using the {@code titleCode} or if it is {@code
	 * null} the default one is used.
	 */
	@Override
	protected void initialize() {

		if (titleCode == null) {
			setTitle(_("Errors in data"));
		} else {
			setTitle(titleCode);
		}
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/error.png"));

		super.initialize();

	}

	/**
	 * Creates the interface for this dialog.
	 * Contains only the list of errors/exceptions which is located within
	 * a scroll pane.
	 */
	@Override
	public JComponent createInterface() {

		this.setResizable(false);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(3,3,3,3));

		errorList = new JList(new Object[0]);
		errorList.setBorder(new LineBorder(Color.LIGHT_GRAY));
		errorList.setFont(new Font("Dialog", Font.PLAIN, 12));

		JScrollPane scrollPane = new JScrollPane(errorList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(scrollPanePreferredDimensions);

		panel.add(scrollPane, BorderLayout.CENTER);

		errorList.setCellRenderer(new DefaultListCellRenderer() {

			private static final long serialVersionUID = 1L;
			Icon icon = IconUtils.loadClassPathIcon("org/signalml/app/icon/error.png");

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				label.setIcon(icon);
				String text = (String) value;

				int maximumTextWidth = getMaximumTextWidth();
				text = wrapTextForLabelIfNecessary(text, maximumTextWidth);
				label.setText(text);

				return label;
			}

			/**
			 * Returns the maximum width a label text can have.
			 * @return the maximum width a label text can have
			 */
			private int getMaximumTextWidth() {
				return (int) scrollPanePreferredDimensions.getWidth() - icon.getIconWidth() - errorList.getInsets().left - errorList.getInsets().right;
			}

		});

		return panel;

	}

	/**
	 * Wraps the given text so that it its width is less than maximum width.
	 * @param text the text to be shown
	 * @param maximumWidth the maximum width that the text can have
	 * @return the wrapped text
	 */
	private String wrapTextForLabelIfNecessary(String text, int maximumWidth) {
		return "<html><table><tr><td width=" + maximumWidth + ">" + text + "</td></tr></table></html>";
	}

	/**
	 * This dialog can not be canceled.
	 */
	@Override
	public boolean isCancellable() {
		return false;
	}

	/**
	 * Depending on the type of the {@code model}:
	 * <ul>
	 * <li>for {@link Errors}:<ul>
	 * <li>obtains the list of errors and converts it to an array,</li>
	 * <li>using this array creates the {@link ErrorListModel model} for the
	 * list of errors and sets it,</li></ul></li>
	 * <li>for {@link MessageSourceResolvable}:
	 * <ul>
	 * <li>creates the array containing one element - this model,</li>
	 * <li>using this array creates the {@link ErrorListModel model} for the
	 * list of errors and sets it.</li></ul></li>
	 * </ul>
	 */
	@Override
	protected void fillDialogFromModel(Object model) throws SignalMLException {
		
		if (model instanceof ValidationErrors) {
			ValidationErrors errors = (ValidationErrors) model;
			errorList.setModel(errors);
		}
	}

	/**
	 * Does nothing as it is a read only dialog.
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		// read only dialog
	}

	/**
	 * The model for this dialog must be either of type {@link Errors} or
	 * {@link MessageSourceResolvable}.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return ValidationErrors.class.isAssignableFrom(clazz);
	}

}

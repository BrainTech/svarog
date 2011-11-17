/* ErrorsDialog.java created 2007-09-19
 *
 */

package org.signalml.app.view.dialog;

import static org.signalml.app.SvarogI18n._;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.apache.log4j.Logger;
import org.signalml.app.util.IconUtils;
import org.signalml.exception.ResolvableException;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.MessageSourceResolvable;

import org.springframework.validation.Errors;

/**
 * Dialog with the list of errors.
 * Contains only the list of errors/exceptions which is located within
 * a scroll pane.
 * <p>
 * Contains a {@link #showImmediateExceptionDialog(Window, Throwable) static
 * function} which displays the dialog with the provided exception.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ErrorsDialog extends org.signalml.app.view.dialog.AbstractSvarogDialog  {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ErrorsDialog.class);

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
	public ErrorsDialog(Window w, boolean isModal) {
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
	public ErrorsDialog(Window w, boolean isModal, String titleCode) {
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
			setTitle(getSvarogI18n().getMessage(titleCode));
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
				String text = getSvarogI18n().getMessage((MessageSourceResolvable) value);

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
		if (model instanceof Errors) {
			Errors errors = (Errors) model;
			Object[] errArr = errors.getAllErrors().toArray();
			errorList.setModel(new ErrorListModel(errArr));
		} else if (model instanceof MessageSourceResolvable) {
			errorList.setModel(new ErrorListModel(new Object[] { model }));
		} else {
			throw new ClassCastException();
		}
	}

	/**
	 * Shows this dialog with the list of errors from the provided object.
	 * @param errors the Errors object from which errors are to be shown
	 * @return if this dialog was closed with OK
	 */
	public boolean showErrors(Errors errors) {
		setTitle(_("Errors in data"));
		return showDialog(errors,true);
	}

	/**
	 * Shows this dialog with just one element - the provided throwable.
	 * If the throwable is of type {@link MessageSourceResolvable} it is simply
	 * used, otherwise the {@link ResolvableException} is created from it.
	 * @param t the throwable to be displayed
	 * @return if this dialog was closed with OK
	 */
	public boolean showException(Throwable t) {
		setTitle(_("Exception occured"));
		MessageSourceResolvable resolvable;
		if (t instanceof MessageSourceResolvable) {
			resolvable = (MessageSourceResolvable) t;
		} else {
			resolvable = new ResolvableException(t);
		}
		return showDialog(resolvable,true);
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
		return Errors.class.isAssignableFrom(clazz) || MessageSourceResolvable.class.isAssignableFrom(clazz);
	}

	/**
	 * The model for the list of errors.
	 */
	private class ErrorListModel extends AbstractListModel {

		private static final long serialVersionUID = 1L;

		/**
		 * the array containing errors.
		 */
		private Object[] errors;

		/**
		 * Creates this model and sets the data for it (errors).
		 * @param errors the array containing errors/exceptions
		 */
		public ErrorListModel(Object[] errors) {
			super();
			this.errors = errors;
		}

		@Override
		public Object getElementAt(int index) {
			return errors[index];
		}

		@Override
		public int getSize() {
			return errors.length;
		}

	}

	/**
	 * Shows the {@link ErrorsDialog} with the provided exception.
	 * The dialog is shown in the Event Dispatching Thread.
	 * @param c the component from which the parent window for this dialog
	 * will be retrieved
	 * @param t the exception to be displayed
	 */
	public static void showImmediateExceptionDialog(final JComponent c, final Throwable t) {

		Window w = null;
		if (c != null) {
			Container cont = c.getTopLevelAncestor();
			if (cont instanceof Window) {
				w = (Window) cont;
			}
		}

		showImmediateExceptionDialog(w, t);


	}

	/**
	 * Shows the {@link ErrorsDialog} with the provided exception.
	 * The dialog is shown in the Event Dispatching Thread.
	 * @param w the parent window or null if there is no parent
	 * @param t the exception to be displayed
	 */
	public static void showImmediateExceptionDialog(final Window w, final Throwable t) {

		Runnable job = new Runnable() {

			@Override
			public void run() {

				ErrorsDialog errorsDialog = new ErrorsDialog(w,true,"error.exception");

				MessageSourceResolvable resolvable;
				if (t instanceof MessageSourceResolvable) {
					resolvable = (MessageSourceResolvable) t;
				} else {
					resolvable = new ResolvableException(t);
				}

				errorsDialog.showDialog(resolvable, true);

				logger.debug("Exception dialog shown", t);

			}

		};

		SwingUtilities.invokeLater(job);

	}

}

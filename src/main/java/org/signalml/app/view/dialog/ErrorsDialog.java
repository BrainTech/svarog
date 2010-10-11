/* ErrorsDialog.java created 2007-09-19
 *
 */

package org.signalml.app.view.dialog;

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
import org.signalml.plugin.export.view.AbstractDialog;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** ErrorsDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ErrorsDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(ErrorsDialog.class);

	private static MessageSourceAccessor staticMessageSource = null;

	private JList errorList = null;
	private String titleCode = null;

	public static MessageSourceAccessor getStaticMessageSource() {
		return staticMessageSource;
	}

	public static void setStaticMessageSource(MessageSourceAccessor staticMessageSource) {
		ErrorsDialog.staticMessageSource = staticMessageSource;
	}

	public ErrorsDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public ErrorsDialog(MessageSourceAccessor messageSource,Window w, boolean isModal) {
		super(messageSource,w, isModal);
	}

	public ErrorsDialog(MessageSourceAccessor messageSource,Window w, boolean isModal, String titleCode) {
		super(messageSource,w, isModal);
		this.titleCode = titleCode;
	}

	@Override
	protected void initialize() {

		if (titleCode == null) {
			setTitle(messageSource.getMessage("errors.title"));
		} else {
			setTitle(messageSource.getMessage(titleCode));
		}
		setIconImage(IconUtils.loadClassPathImage("org/signalml/app/icon/error.png"));

		super.initialize();

	}

	@Override
	public JComponent createInterface() {

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new EmptyBorder(3,3,3,3));

		errorList = new JList(new Object[0]);
		errorList.setBorder(new LineBorder(Color.LIGHT_GRAY));
		errorList.setFont(new Font("Dialog", Font.PLAIN, 12));

		JScrollPane scrollPane = new JScrollPane(errorList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension(420,250));

		panel.add(scrollPane, BorderLayout.CENTER);

		errorList.setCellRenderer(new DefaultListCellRenderer() {

			private static final long serialVersionUID = 1L;
			Icon icon = IconUtils.loadClassPathIcon("org/signalml/app/icon/error.png");

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				label.setIcon(icon);
				label.setText(messageSource.getMessage((MessageSourceResolvable) value));

				return label;
			}

		});

		/*
		ErrorListCellRenderer renderer = new ErrorListCellRenderer(scrollPane.getPreferredSize());
		renderer.setMessageSource(messageSource);
		errorList.setCellRenderer(renderer);
		*/

		return panel;

	}

	@Override
	public boolean isCancellable() {
		return false;
	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
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

	public boolean showErrors(Errors errors) {
		setTitle(messageSource.getMessage("errors.title"));
		return showDialog(errors,true);
	}

	public boolean showException(Throwable t) {
		setTitle(messageSource.getMessage("error.exception"));
		MessageSourceResolvable resolvable;
		if (t instanceof MessageSourceResolvable) {
			resolvable = (MessageSourceResolvable) t;
		} else {
			resolvable = new ResolvableException(t);
		}
		return showDialog(resolvable,true);
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		// read only dialog
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return Errors.class.isAssignableFrom(clazz) || MessageSourceResolvable.class.isAssignableFrom(clazz);
	}

	private class ErrorListModel extends AbstractListModel {

		private static final long serialVersionUID = 1L;

		private Object[] errors;

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

	public static void showImmediateExceptionDialog(final Window w, final Throwable t) {

		Runnable job = new Runnable() {

			@Override
			public void run() {

				ErrorsDialog errorsDialog = new ErrorsDialog(staticMessageSource,w,true,"error.exception");

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

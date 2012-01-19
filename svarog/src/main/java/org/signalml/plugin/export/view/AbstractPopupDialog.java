/* AbstractPopupDialog.java created 2007-10-13
 *
 */

package org.signalml.plugin.export.view;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.border.LineBorder;

import org.signalml.app.view.components.dialogs.AbstractDialog;

/**
 * The abstract popup dialog, from which every popup dialog in Svarog should
 * inherit.
 * Contains the control pane (with OK and CANCEL button).
 * It differs from {@link AbstractDialog} only with different initialization,
 * which makes this dialog look a bit different.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractPopupDialog extends AbstractDialog {

	static final long serialVersionUID = 1L;

	/**
	 * Constructor. Sets message source.
	 */
	public AbstractPopupDialog() {
		super();
	}

	/**
	 * Constructor. Sets message source, parent window and if this dialog
	 * blocks top-level windows.
	 * @param w the parent window or null if there is no parent
	 * @param isModal true, dialog blocks top-level windows, false otherwise
	 */
	public AbstractPopupDialog(Window w, boolean isModal) {
		super(w, isModal);
	}

	@Override
	public boolean isControlPanelEquipped() {
		return true;
	}

	@Override
	public boolean isCancellable() {
		return true;
	}

	/**
	 * Returns true if clicking on the form should be approving action
	 * ({@code OkAction}), false if it should be a canceling action
	 * ({@code CancelAction}).
	 * @return true if clicking on the form should be approving action,
	 * false if it should be a canceling action
	 */
	public boolean isFormClickApproving() {
		return false;
	}

	@Override
	protected void initialize() {

		setUndecorated(true);
		getRootPane().setBorder(new LineBorder(Color.LIGHT_GRAY));
		super.initialize();

		MouseAdapter ma = new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if (isFormClickApproving()) {
					getOkAction().actionPerformed(new ActionEvent(this, 0, "ok"));
				} else {
					getCancelAction().actionPerformed(new ActionEvent(this, 0, "cancel"));
				}
				e.consume();
			}

		};

		addMouseListener(ma);

	}

}

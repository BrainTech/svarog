/* AbstractPopupDialog.java created 2007-10-13
 *
 */

package org.signalml.plugin.export.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.border.LineBorder;

import org.signalml.app.view.common.dialogs.AbstractDialog;

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

	static final int CROSS_SIZE = 20;
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
	
	@Override
	protected void initialize() {

		setUndecorated(true);
		getRootPane().setBorder(new LineBorder(Color.LIGHT_GRAY));
		super.initialize();
			int winWidth = this.getWidth();
		        MouseAdapter ma = new MouseAdapter() {

                       @Override
                       public void mousePressed(MouseEvent e) {
			       // cant find what produces cross
			       // so doing thi crazy hack
				Point point = e.getPoint();
				if (winWidth - point.x < CROSS_SIZE & point.y < CROSS_SIZE)
				{
					getOkAction().actionPerformed(new ActionEvent(this, 0, "ok"));
				}
                              
                               e.consume();
                       }

               };

               addMouseListener(ma);


	}

}

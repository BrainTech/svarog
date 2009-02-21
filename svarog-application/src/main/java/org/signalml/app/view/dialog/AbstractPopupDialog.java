/* AbstractPopupDialog.java created 2007-10-13
 * 
 */

package org.signalml.app.view.dialog;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.border.LineBorder;

import org.springframework.context.support.MessageSourceAccessor;

/** AbstractPopupDialog
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractPopupDialog extends AbstractDialog {
	
	static final long serialVersionUID = 1L;

	public AbstractPopupDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public AbstractPopupDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	public boolean isControlPanelEquipped() {
		return true;
	}
	
	@Override
	public boolean isCancellable() {
		return true;
	}
	
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
				if( isFormClickApproving() ) {
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

/* AbstractSignalMLAction.java created 2007-09-10
 * 
 */
package org.signalml.app.action;

import java.awt.Component;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.signalml.app.util.IconUtils;
import org.springframework.context.support.MessageSourceAccessor;

/** AbstractSignalMLAction
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractSignalMLAction extends AbstractAction {
			
	static final long serialVersionUID = 1L;

	protected MessageSourceAccessor messageSource;
	
	protected AbstractSignalMLAction() {
		super();
	}
	
	public AbstractSignalMLAction(MessageSourceAccessor messageSource) {
		super();
		if( messageSource == null ) {
			throw new NullPointerException("No message source");
		}
		this.messageSource = messageSource;
		setEnabledAsNeeded();		
	}
			
	public void setAccelerator(String accelerator) {
		if( accelerator != null ) {
			putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator));
		} else {
			putValue(AbstractAction.ACCELERATOR_KEY, null);
		}
	}

	public void setIconPath(String iconPath) {
		if( iconPath != null ) {
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon(iconPath) );
		} else {
			putValue(AbstractAction.SMALL_ICON, null );
		}
	}

	public void setText(String text) {
		if( text != null ) {
			putValue(AbstractAction.NAME, messageSource.getMessage(text));
		} else {
			putValue(AbstractAction.NAME, null);
		}
	}

	public void setText(String text, Object[] arguments) {
		if( text != null ) {
			putValue(AbstractAction.NAME, messageSource.getMessage(text, arguments));
		} else {
			putValue(AbstractAction.NAME, null);
		}
	}
	
	public void setToolTip(String toolTip) {
		if( toolTip != null ) {
			putValue(AbstractAction.SHORT_DESCRIPTION, messageSource.getMessage(toolTip));
		} else {
			putValue(AbstractAction.SHORT_DESCRIPTION, null);
		}
	}
	
	public void setEnabledAsNeeded() {
		setEnabled(true);
	}
	
	public Object findFocusSelector( Object source, Class<?> clazz ) {

		if( source == null || clazz == null ) {
			return null;
		}
		
		if( clazz.isAssignableFrom(source.getClass()) ) {
			
			return source;
			
		} else if( source instanceof Component ) {
			
			
			Component current = (Component) source;
			do {
								
				if( current instanceof JPopupMenu ) {
					current = ((JPopupMenu) current).getInvoker();
					continue;
				}
				
				current = current.getParent();
				
				if( current != null ) {
					
					if( clazz.isAssignableFrom( current.getClass() ) ) {
						return current;
					}
					
				}
								
			} while( current != null );
			
		}
		
		
		return null;
	}
				
}

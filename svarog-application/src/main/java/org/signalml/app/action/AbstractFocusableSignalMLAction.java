/* AbstractFocusableSignalMLAction.java created 2007-10-15
 * 
 */

package org.signalml.app.action;

import org.signalml.app.action.selector.ActionFocusEvent;
import org.signalml.app.action.selector.ActionFocusListener;
import org.signalml.app.action.selector.ActionFocusSelector;
import org.springframework.context.support.MessageSourceAccessor;

/** AbstractFocusableSignalMLAction
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractFocusableSignalMLAction<T extends ActionFocusSelector> extends AbstractSignalMLAction implements ActionFocusListener {

	static final long serialVersionUID = 1L;
	
	private T actionFocusSelector;
		
	public AbstractFocusableSignalMLAction(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public AbstractFocusableSignalMLAction(MessageSourceAccessor messageSource, T actionFocusSelector) {
		super();
		if( messageSource == null ) {
			throw new NullPointerException("No message source");
		}
		this.messageSource = messageSource;
		if( actionFocusSelector == null ) {
			throw new NullPointerException( "No action focus selector" );
		}
		this.actionFocusSelector = actionFocusSelector;
		actionFocusSelector.addActionFocusListener(this);
		setEnabledAsNeeded();
	}
	
	public T getActionFocusSelector() {
		return actionFocusSelector;
	}
	
	protected void setActionFocusSelector(T actionFocusSelector) {
		if( actionFocusSelector == null ) {
			throw new NullPointerException( "No action focus selector" );			
		}
		if( this.actionFocusSelector != actionFocusSelector ) {
			this.actionFocusSelector = actionFocusSelector;
			setEnabledAsNeeded();
		}
	}

	@Override
	public void actionFocusChanged(ActionFocusEvent e) {
		setEnabledAsNeeded();
	}
			
}

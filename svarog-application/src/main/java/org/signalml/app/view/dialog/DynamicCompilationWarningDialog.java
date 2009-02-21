/* DynamicCompilationWarningDialog.java created 2008-03-03
 * 
 */

package org.signalml.app.view.dialog;

import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.util.IconUtils;
import org.signalml.compilation.DynamicCompilationWarning;
import org.springframework.context.support.MessageSourceAccessor;

/** DynamicCompilationWarningDialog
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class DynamicCompilationWarningDialog extends AbstractMessageDialog implements DynamicCompilationWarning {
	
	private static final long serialVersionUID = 1L;

	private Runnable showRunnable; 
	
	public DynamicCompilationWarningDialog(MessageSourceAccessor messageSource) {
		super(messageSource);
	}

	public DynamicCompilationWarningDialog(MessageSourceAccessor messageSource,Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	@Override
	protected void initialize() {
		setTitle( messageSource.getMessage("dynamicCompilationWarning.title") );
		setIconImage( IconUtils.getWarningIcon().getImage() );
		
		getMessageLabel().setText(
				"<html><body><div style=\"width: 300px; text-align: justify;\">"
				+ messageSource.getMessage( "dynamicCompilationWarning.text" )
				+ "</div></body></html>"				
		);
		
		getMessagePanel().setBorder( new CompoundBorder(
				new TitledBorder( messageSource.getMessage("dynamicCompilationWarning.frameTitle") ),
				new EmptyBorder(3,3,3,3)				
		));
		
		super.initialize();
	}
		
	@Override
	public boolean getDontShowAgain() {

		ApplicationConfiguration config = getApplicationConfig();
		
		if( config != null ) {
			return config.isDontShowDynamicCompilationWarning();
		} else {
			Preferences prefs = getPreferences();
			if( prefs != null ) {
				return prefs.getBoolean( "dontShowDynamicCompilationWarning", false );
			}
		}
		
		return false;
	}

	@Override
	public void setDontShowAgain(boolean dontShow) {

		ApplicationConfiguration config = getApplicationConfig();
		Preferences prefs = getPreferences();
		
		if( config != null ) {
			config.setDontShowDynamicCompilationWarning(dontShow);
		}
		if( prefs != null ) {
			prefs.putBoolean( "dontShowDynamicCompilationWarning", dontShow );
		}
		
	}

	@Override
	public boolean warn() {

		if( getDontShowAgain() ) {
			return true;
		}
		
		if( SwingUtilities.isEventDispatchThread() ) {

			return showDialog(null, true);
		
		} else {
					
			if( showRunnable == null ) {
				showRunnable = new Runnable() {
	
					@Override
					public void run() {
						showDialog(null, true);
					}
					
				};
			}
		
			try {
				SwingUtilities.invokeAndWait(showRunnable);
			} catch (InterruptedException ex) {
				logger.error( "Exception on dialog", ex );
			} catch (InvocationTargetException ex) {
				logger.error( "Exception on dialog", ex );
			}
		
			return isClosedWithOk();
			
		}
		
	}

}

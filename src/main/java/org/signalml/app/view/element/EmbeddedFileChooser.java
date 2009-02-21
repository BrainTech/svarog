/* EmbeddedFileChooser.java created 2008-01-17
 * 
 */

package org.signalml.app.view.element;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;

import org.springframework.validation.Errors;

/** EmbeddedFileChooser
 * 
 *  This class provides ugly hack fixes for what appears to be bugs or bad design
 *  in JFileChooser.
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EmbeddedFileChooser extends JFileChooser {

	private static final long serialVersionUID = 1L;

	private boolean suppressActionEvent = false;
	private boolean invokeDefaultButtonOnApprove = false;
	
	private ActionListener defaultInvoker = null;
	
	public EmbeddedFileChooser() {
		super();
	}

	public EmbeddedFileChooser(File currentDirectory, FileSystemView fsv) {
		super(currentDirectory, fsv);
	}

	public EmbeddedFileChooser(File currentDirectory) {
		super(currentDirectory);
	}

	public EmbeddedFileChooser(FileSystemView fsv) {
		super(fsv);
	}

	public EmbeddedFileChooser(String currentDirectoryPath, FileSystemView fsv) {
		super(currentDirectoryPath, fsv);
	}

	public EmbeddedFileChooser(String currentDirectoryPath) {
		super(currentDirectoryPath);
	}

	@Override
	protected void setup(FileSystemView view) {
		super.setup(view);
		
		super.setControlButtonsAreShown(false);		
	}
	
	@Override
	public void setControlButtonsAreShown(boolean b) {
		// XXX ugly hack
		// this is ignored on purpose
	}

	@Override
	protected void fireActionPerformed(String command) {
		// hacked to allow for supression
		if( !suppressActionEvent ) {
			super.fireActionPerformed(command);
		}
	}
	
	public void forceApproveSelection() {
		
		// XXX ugly hack
		
		// if the control buttons are hidden, then there is no way to type in file name
		// rather than selecting a file from the list
		// forms using EmbeddedFileChooser must call this before trying to obtain the selected file
		// because otherwise the typed filename is ignored
		
		// this is done by invoking the action because the processing in this
		// action is very complicated and copying all that code here would
		// not be practical. However the resulting action event needs to be
		// supressed to allow some use cases
		
		// action name is appended with "-auto" to allow any action listeners to differentiate
		// the two cases
		FileChooserUI ui = getUI();
		if( ui instanceof BasicFileChooserUI ) {
			Action approveSelectionAction = ((BasicFileChooserUI) ui).getApproveSelectionAction();
			try {
				suppressActionEvent = true;
				approveSelectionAction.actionPerformed(new ActionEvent(this,0,JFileChooser.APPROVE_SELECTION + "-auto"));
			} finally {
				suppressActionEvent = false;
			}
		}
				
	}
	
	public void validateFile( Errors errors, String property, boolean acceptNone, boolean acceptMissing, boolean acceptDirectory, boolean acceptUnreadable, boolean acceptReadOnly ) {
				
		File file = getSelectedFile();
		if( file == null || file.getPath().length() == 0 ) {
			if( !acceptNone ) {
				errors.rejectValue(property, "error.fileMustBeChosen");
			}
		} else {
			if( !file.exists() ) {
				if( !acceptMissing ) {
					errors.rejectValue(property, "error.fileNotFound");
				}
			} else {
				if( !acceptDirectory && file.isDirectory() ) {
					errors.rejectValue(property, "error.fileNotFile");
				}
				if( !acceptUnreadable && !file.canRead() ) {
					errors.rejectValue(property, "error.fileNotReadable");
				}
				if( !acceptReadOnly && !file.canWrite() ) {
					errors.rejectValue(property, "error.fileNotWritable");
				}
			}
		}
				
	}

	public boolean isInvokeDefaultButtonOnApprove() {
		return invokeDefaultButtonOnApprove;
	}

	public void setInvokeDefaultButtonOnApprove(boolean invokeDefaultButtonOnApprove) {
		if( this.invokeDefaultButtonOnApprove != invokeDefaultButtonOnApprove ) {
			if( this.invokeDefaultButtonOnApprove ) {
				this.removeActionListener(defaultInvoker);
			}
			this.invokeDefaultButtonOnApprove = invokeDefaultButtonOnApprove;
			if( invokeDefaultButtonOnApprove ) {
				if( defaultInvoker == null ) {
					defaultInvoker = new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {					
							if( e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION )) {
								getRootPane().getDefaultButton().getAction().actionPerformed(e);
							}
						}
						
					};
					addActionListener(defaultInvoker);
				}
			}
		}
	}
	
}

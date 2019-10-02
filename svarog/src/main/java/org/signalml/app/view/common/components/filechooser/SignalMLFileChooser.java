package org.signalml.app.view.common.components.filechooser;

import com.alee.extended.filechooser.WebFileTable;
import static org.signalml.app.util.i18n.SvarogI18n._;

import java.io.File;
import java.lang.reflect.*;
import com.alee.extended.list.FileListViewType;
import com.alee.extended.list.WebFileList;
import com.alee.laf.filechooser.FileChooserViewType;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.filechooser.WebFileChooserPanel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;

import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.view.common.dialogs.errors.Dialogs;

class SingleClickWebFileChooserPanel extends WebFileChooserPanel
{
}

/**
 * A file chooser which should be used by all Svarog components. Adds a
 * favourites panel to the file chooser.
 *
 * @author Piotr Szachewicz
 */
public class SignalMLFileChooser extends WebFileChooser {

	/**
	 * Constructs a <code>EmbeddedFileChooser</code> pointing to the user's
	 * default directory. This default depends on the operating system. It is
	 * typically the "My Documents" folder on Windows, and the user's home
	 * directory on Unix.
	 */
	public SignalMLFileChooser() {
		getFileChooserPanel().setViewType(FileChooserViewType.table);
                EmbeddedFileChooserFavorites f = new EmbeddedFileChooserFavorites(this);
		this.setAccessory(f);
                
                
                // will work only with weblaf 1.2.8 - dirty hacks to patch its behaviour to allow signal file info to be updated after single click it instead of double
                validateWeblafWersion();
                hackSetupSingleClickFileBrowsingTable();
                hackSetupSingleClickFileBrowsingList();
                        
	}
        
        void validateWeblafWersion()
        {
            try {
                Field field = WebFileChooserPanel.class.getDeclaredField("quotedFileNameProvider");
            } catch (NoSuchFieldException ex) {
                Dialogs.showError("Open Signal menu weblaf single click hack incompatability! Using Weblaf version different to 1.2.8? Consider removing this hack!");
                Logger.getLogger(SignalMLFileChooser.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(SignalMLFileChooser.class.getName()).log(Level.SEVERE, null, ex);
            }


        }
        
        void hackSetupSingleClickFileBrowsingList()
        {               
            try
                {
                    Field field = WebFileChooserPanel.class.getDeclaredField("fileList");
                    field.setAccessible(true);//Very important, this allows the setting to work.
                    WebFileList fileList = (WebFileList) field.get(getFileChooserPanel());
                    fileList.addMouseListener(
                                            new MouseAdapter ()
                           {
                               @Override
                               public void mouseClicked ( final MouseEvent e )
                               {
                                   
                                   
                                   Method fireApproveAction;
                                   try {
                                       fireApproveAction = WebFileChooserPanel.class.getDeclaredMethod("fireApproveAction", ActionEvent.class);
                                            if ( SwingUtilities.isLeftMouseButton ( e ) && fileList.getSelectedIndex () != -1 )
                                        {
                                            final File file = fileList.getSelectedFile ();
                                            if ( !file.isDirectory () )
                                            {
                                                try {
                                                    fireApproveAction.setAccessible(true);
                                                    fireApproveAction.invoke(getFileChooserPanel(), new ActionEvent ( fileList, e.getID (), "Files selected", e.getWhen (), e.getModifiers () ));
                                                } catch (IllegalAccessException ex) {
                                                    Logger.getLogger(SignalMLFileChooser.class.getName()).log(Level.SEVERE, null, ex);
                                                } catch (IllegalArgumentException ex) {
                                                    Logger.getLogger(SignalMLFileChooser.class.getName()).log(Level.SEVERE, null, ex);
                                                } catch (InvocationTargetException ex) {
                                                    Logger.getLogger(SignalMLFileChooser.class.getName()).log(Level.SEVERE, null, ex);
                                                }
                                            }
                                   }
                                       
                                   } catch (NoSuchMethodException ex) {
                                       Logger.getLogger(SignalMLFileChooser.class.getName()).log(Level.SEVERE, null, ex);
                                   } catch (SecurityException ex) {
                                       Logger.getLogger(SignalMLFileChooser.class.getName()).log(Level.SEVERE, null, ex);
                                   }
                                   

                               }
                           }
                    );
                
                
                }
                catch (NoSuchFieldException e)
                        {}
                catch (IllegalAccessException e)
                {}
                catch ( SecurityException e)
                {}
                
            
        }
        
        void hackSetupSingleClickFileBrowsingTable()
        {               
            try
                {
                    Field field = WebFileChooserPanel.class.getDeclaredField("fileTable");
                    field.setAccessible(true);//Very important, this allows the setting to work.
                    WebFileTable fileTable = (WebFileTable) field.get(getFileChooserPanel());
                    fileTable.addMouseListener(
                                            new MouseAdapter ()
                           {
                               @Override
                               public void mouseClicked ( final MouseEvent e )
                               {
                                   
                                   Method fireApproveAction;
                                   try {
                                       fireApproveAction = WebFileChooserPanel.class.getDeclaredMethod("fireApproveAction", ActionEvent.class);
                                       if ( SwingUtilities.isLeftMouseButton ( e ) && fileTable.getSelectedRow () != -1 )
                                        {
                                            final File file = fileTable.getSelectedFile();
                                            if ( !file.isDirectory () )
                                            {
                                                try {
                                                    fireApproveAction.setAccessible(true);
                                                    fireApproveAction.invoke(getFileChooserPanel(), new ActionEvent ( fileTable, e.getID (), "Files selected", e.getWhen (), e.getModifiers () ) );
                                                } catch (IllegalAccessException ex) {
                                                    Logger.getLogger(SignalMLFileChooser.class.getName()).log(Level.SEVERE, null, ex);
                                                } catch (IllegalArgumentException ex) {
                                                    Logger.getLogger(SignalMLFileChooser.class.getName()).log(Level.SEVERE, null, ex);
                                                } catch (InvocationTargetException ex) {
                                                    Logger.getLogger(SignalMLFileChooser.class.getName()).log(Level.SEVERE, null, ex);
                                                }
                                               
                                            }
                                   }
                                       
                                   } catch (NoSuchMethodException ex) {
                                       Logger.getLogger(SignalMLFileChooser.class.getName()).log(Level.SEVERE, null, ex);
                                   } catch (SecurityException ex) {
                                       Logger.getLogger(SignalMLFileChooser.class.getName()).log(Level.SEVERE, null, ex);
                                   }
                                   

                               }
                           }
                    );
                
                
                }
                catch (NoSuchFieldException e)
                        {}
                catch (IllegalAccessException e)
                {}
                catch ( SecurityException e)
                {}
                
            
        }

	public void lastDirectoryChanged() {
		if (getAccessory() != null) {
			String dir = getSelectedFile().getParent();
			getAccessory().lastDirectoryChanged(dir);
		}
	}

	/**
	 * Validates the chosen file. The file is valid if following occurs
	 * (conjunction):
	 * <ul>
	 * <li>the file is selected or {@code acceptNone} is {@code true},</li>
	 * <li>the file exists or {@code acceptMissing} is {@code true},</li>
	 * <li>the file is not a directory or {@code acceptDirectory} is
	 * {@code true},</li>
	 * <li>the file can be read or {@code acceptUnreadable} is {@code true},</li>
	 * <li>the file can be written or {@code acceptReadOnly} is {@code true},</li>
	 * </ul>
	 *
	 * @param errors
	 *            the variable in which errors are stored
	 * @param property
	 *            the name of the property
	 * @param acceptNone
	 *            if no file selected should be accepted
	 * @param acceptMissing
	 *            if not existing files should be accepted
	 * @param acceptDirectory
	 *            if directories should be accepted
	 * @param acceptUnreadable
	 *            if unreadable files should be accepted
	 * @param acceptReadOnly
	 *            if read only files should be accepted
	 */
	public void validateFile(ValidationErrors errors, String property, boolean acceptNone, boolean acceptMissing, boolean acceptDirectory, boolean acceptUnreadable, boolean acceptReadOnly) {

		File file = getSelectedFile();
		if (file == null || file.getPath().length() == 0) {
			if (!acceptNone) {
				errors.addError(_("A file must be chosen"));
			}
		} else {
			if (!file.exists()) {
				if (!acceptMissing) {
					errors.addError(_("File not found"));
				}
			} else {
				if (!acceptDirectory && file.isDirectory()) {
					errors.addError(_("File is not a regular file"));
				}
				if (!acceptUnreadable && !file.canRead()) {
					errors.addError(_("File is not readable"));
				}
				if (!acceptReadOnly && !file.canWrite()) {
					errors.addError(_("File is not writable"));
				}
			}
		}

	}

	@Override
	public EmbeddedFileChooserFavorites getAccessory() {
		return (EmbeddedFileChooserFavorites) super.getAccessory();
	}

}

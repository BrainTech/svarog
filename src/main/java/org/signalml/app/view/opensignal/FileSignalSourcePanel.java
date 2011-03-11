/* FileSignalSourcePanel.java created 2011-03-06
 *
 */

package org.signalml.app.view.opensignal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.dialog.OpenDocumentDialog;
import org.signalml.app.view.element.EmbeddedFileChooser;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;
import org.springframework.context.support.MessageSourceAccessor;
import org.signalml.app.view.element.FileChooserPanel;
import org.signalml.app.view.element.SignalMLOptionsPanel;

/**
 *
 * @author Piotr Szachewicz
 */
public class FileSignalSourcePanel extends AbstractSignalSourcePanel {

	private FileChooserPanel fileChooserPanel;
	private FileOpenMethodPanel fileOpenMethodPanel;
	private SignalParametersPanelForRawSignalFile signalParametersPanel;
	private SignalMLOptionsPanel signalMLOptionsPanel;

	public FileSignalSourcePanel(MessageSourceAccessor messageSource, ViewerElementManager viewerElementManager) {
		super(messageSource, viewerElementManager);
	}

	@Override
	protected JPanel createLeftColumnPanel() {
		JPanel leftColumnPanel = new JPanel();
		leftColumnPanel.setLayout(new BorderLayout());
		leftColumnPanel.add(getFileChooserPanel());

		return leftColumnPanel;
	}

	@Override
	protected JPanel createRightColumnPanel() {
		JPanel rightColumnPanel = new JPanel(new BorderLayout());
		rightColumnPanel.add(getFileOpenMethodPanel(), BorderLayout.NORTH);
		rightColumnPanel.add(getSignalParametersPanel(), BorderLayout.CENTER);
		return rightColumnPanel;
	}

        @Override
        public void fillPanelFromModel(Object model) throws SignalMLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void fillModelFromPanel(Object model) throws SignalMLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

	public FileChooserPanel getFileChooserPanel() {
		if (fileChooserPanel == null) {
			fileChooserPanel = new FileChooserPanel(messageSource, ManagedDocumentType.SIGNAL);
		}
		return fileChooserPanel;
	}

	protected FileOpenMethodPanel getFileOpenMethodPanel() {
		if (fileOpenMethodPanel == null)
			fileOpenMethodPanel = new FileOpenMethodPanel(messageSource);
		return fileOpenMethodPanel;
	}

	public SignalParametersPanelForRawSignalFile getSignalParametersPanel() {
		if (signalParametersPanel == null) {
			signalParametersPanel = new SignalParametersPanelForRawSignalFile(messageSource, viewerElementManager.getApplicationConfig());
		}
		return signalParametersPanel;
	}

	public SignalMLOptionsPanel getSignalMLOptionsPanel() {
		if (signalMLOptionsPanel == null) {
			signalMLOptionsPanel = new SignalMLOptionsPanel(messageSource);
		}
		return signalMLOptionsPanel;
	}

}

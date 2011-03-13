/* FileSignalSourcePanel.java created 2011-03-06
 *
 */

package org.signalml.app.view.opensignal;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JPanel;

import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.model.OpenFileSignalDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.signalml.app.view.element.FileChooserPanel;
import org.signalml.app.view.element.SignalMLOptionsPanel;
import org.signalml.domain.signal.raw.RawSignalDescriptor;

/**
 *
 * @author Piotr Szachewicz
 */
public class FileSignalSourcePanel extends AbstractSignalSourcePanel {

	private FileChooserPanel fileChooserPanel;
	private FileOpenMethodPanel fileOpenMethodPanel;

	private JPanel cardPanelForSignalParameters;
	private SignalParametersPanelForRawSignalFile rawSignalParametersPanel;
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
		//rightColumnPanel.add(getRawSignalParametersPanel(), BorderLayout.CENTER);
		rightColumnPanel.add(getCardPanelForSignalParameters(), BorderLayout.CENTER);
		return rightColumnPanel;
	}

        public void fillPanelFromModel(OpenFileSignalDescriptor openFileSignalDescriptor) {
		FileOpenSignalMethod method = openFileSignalDescriptor.getMethod();

		fileOpenMethodPanel.setSelectedOpenSignalMethod(method);
		if (method.isRaw()) {
			rawSignalParametersPanel.fillPanelFromModel(openFileSignalDescriptor.getRawSignalDescriptor());
		}
        }

	public void fillModelFromPanel(OpenFileSignalDescriptor descriptor) {
		FileOpenSignalMethod method = fileOpenMethodPanel.getSelectedOpenSignalMethod();
		if (method.isRaw()) {
			RawSignalDescriptor rawSignalDescriptor = descriptor.getRawSignalDescriptor();
			rawSignalParametersPanel.fillModelFromPanel(rawSignalDescriptor);
			
			File selectedFile = fileChooserPanel.getSelectedFile();
			
			descriptor.setFile(selectedFile);
		}
	}

	public FileChooserPanel getFileChooserPanel() {
		if (fileChooserPanel == null) {
			fileChooserPanel = new FileChooserPanel(messageSource, ManagedDocumentType.SIGNAL);
		}
		return fileChooserPanel;
	}

	protected FileOpenMethodPanel getFileOpenMethodPanel() {
		if (fileOpenMethodPanel == null) {
			fileOpenMethodPanel = new FileOpenMethodPanel(messageSource);
			fileOpenMethodPanel.addPropertyChangeListener(this);
		}
		return fileOpenMethodPanel;
	}

	public JPanel getCardPanelForSignalParameters() {
		if (cardPanelForSignalParameters == null) {
			cardPanelForSignalParameters = new JPanel(new CardLayout());
			cardPanelForSignalParameters.add(getRawSignalParametersPanel(), FileOpenSignalMethod.RAW.toString());
			cardPanelForSignalParameters.add(getSignalMLOptionsPanel(), FileOpenSignalMethod.SIGNALML.toString());
		}
		return cardPanelForSignalParameters;
	}

	public SignalParametersPanelForRawSignalFile getRawSignalParametersPanel() {
		if (rawSignalParametersPanel == null) {
			rawSignalParametersPanel = new SignalParametersPanelForRawSignalFile(messageSource, viewerElementManager.getApplicationConfig());
			rawSignalParametersPanel.addPropertyChangeListener(this);
		}
		return rawSignalParametersPanel;
	}

	public SignalMLOptionsPanel getSignalMLOptionsPanel() {
		if (signalMLOptionsPanel == null) {
			signalMLOptionsPanel = new SignalMLOptionsPanel(messageSource);
		}
		return signalMLOptionsPanel;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();

		if (propertyName.equals(FileOpenMethodPanel.FILE_OPEN_METHOD_PROPERTY_CHANGED)) {
			FileOpenSignalMethod method = (FileOpenSignalMethod) evt.getNewValue();
			System.out.println("file open method changed to " + method);

			CardLayout cl = (CardLayout)(cardPanelForSignalParameters.getLayout());
			cl.show(cardPanelForSignalParameters, method.toString());
		}
		else
			super.propertyChange(evt);

	}

}

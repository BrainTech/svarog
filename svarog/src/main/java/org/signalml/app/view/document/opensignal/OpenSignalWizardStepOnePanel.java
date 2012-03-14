package org.signalml.app.view.document.opensignal;

import static org.signalml.app.util.i18n.SvarogI18n._;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.io.File;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.signalml.app.SvarogApplication;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.view.components.FileChooserPanel;
import org.signalml.app.view.document.opensignal.elements.SignalParametersPanel;
import org.signalml.app.view.document.opensignal_old.monitor.ChannelSelectPanel;
import org.signalml.app.view.document.opensignal_old.monitor.ChooseExperimentPanel;
import org.signalml.app.view.document.opensignal_old.monitor.TagPresetSelectionPanel;
import org.signalml.app.view.montage.EegSystemSelectionPanel;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.domain.montage.system.EegSystem;

public class OpenSignalWizardStepOnePanel extends JPanel {
	
	private ViewerElementManager viewerElementManager;
	
	/**
	 * The panel for choosing a file to be opened.
	 */
	private FileChooserPanel fileChooserPanel;
	private ChooseExperimentPanel chooseExperimentPanel;

	private SignalParametersPanel signalParametersPanel;
	private ChannelSelectPanel channelSelectPanel;
	
	private EegSystemSelectionPanel eegSystemSelectionPanel;
	private TagPresetSelectionPanel tagPresetSelectionPanel;

	public OpenSignalWizardStepOnePanel(ViewerElementManager viewerElementManager) {
		this.viewerElementManager = viewerElementManager;
		
		this.setLayout(new GridLayout(1, 2));
		this.add(createLeftPanel());
		this.add(createRightPanel());
	}
	
	protected JPanel createLeftPanel() {
		JPanel leftPanel = new JPanel(new BorderLayout());
		
		JTabbedPane signalSourceTabbedPane = new JTabbedPane();
		signalSourceTabbedPane.addTab(_("FILE"), getFileChooserPanel());
		chooseExperimentPanel = new ChooseExperimentPanel();
		signalSourceTabbedPane.addTab(_("MONITOR"), chooseExperimentPanel);
		leftPanel.add(signalSourceTabbedPane, BorderLayout.NORTH);
		
		signalParametersPanel = new SignalParametersPanel();
		leftPanel.add(signalParametersPanel, BorderLayout.CENTER);
		
		return leftPanel;
	}
	
	protected JPanel createRightPanel() {
		JPanel rightPanel = new JPanel(new BorderLayout());
		
		channelSelectPanel = new ChannelSelectPanel();
		rightPanel.add(channelSelectPanel, BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(getTagPresetSelectionPanel(), BorderLayout.NORTH);
		southPanel.add(getEegSystemSelectionPanel(), BorderLayout.SOUTH);
		
		rightPanel.add(southPanel, BorderLayout.SOUTH);
		
		return rightPanel;
	}
	
	/**
	 * Returns the panel for choosing which signal file should be opened.
	 * @return the panel for choosing which signal file should be opened
	 */
	public FileChooserPanel getFileChooserPanel() {
		if (fileChooserPanel == null) {
			fileChooserPanel = new FileChooserPanel( ManagedDocumentType.SIGNAL);
			
			String lastFileChooserPath = SvarogApplication.getApplicationConfiguration().getLastFileChooserPath();
			getFileChooserPanel().getFileChooser().setCurrentDirectory(new File(lastFileChooserPath));
		}
		return fileChooserPanel;
	}
	
	/**
	 * Returns the panel for selecting tag style preset to be used by the
	 * monitor to be opened.
	 * @return panel for selecting tag style preset
	 */
	public TagPresetSelectionPanel getTagPresetSelectionPanel() {
		if (tagPresetSelectionPanel == null) {
			tagPresetSelectionPanel = new TagPresetSelectionPanel( viewerElementManager.getStyledTagSetPresetManager());
		}
		return tagPresetSelectionPanel;
	}
	
	/**
	 * Returns the panel for selecting the currently used {@link EegSystem
	 * EEG system}.
	 * @return the EEG system selection panel
	 */
	protected EegSystemSelectionPanel getEegSystemSelectionPanel() {
		if (eegSystemSelectionPanel == null) {
			eegSystemSelectionPanel = new EegSystemSelectionPanel(viewerElementManager.getEegSystemsPresetManager());
		}
		return eegSystemSelectionPanel;
	}
}

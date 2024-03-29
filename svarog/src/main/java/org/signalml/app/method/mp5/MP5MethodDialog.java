/* MP5MethodDialog.java created 2007-10-28
 *
 */

package org.signalml.app.method.mp5;

import com.alee.laf.tabbedpane.WebTabbedPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Formatter;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.signalml.app.config.preset.Preset;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.document.FileBackedDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.InitializingMethodConfigurer;
import org.signalml.app.method.MethodPresetManager;
import org.signalml.app.method.PresetEquippedMethodConfigurer;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.model.signal.SignalExportDescriptor;
import org.signalml.app.util.IconUtils;
import org.signalml.app.util.i18n.SvarogI18n;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.AbstractSignalSpaceAwarePresetDialog;
import org.signalml.app.view.common.dialogs.OptionPane;
import org.signalml.app.view.common.dialogs.PleaseWaitDialog;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.signal.PositionedTag;
import org.signalml.app.view.signal.SampleSourceUtils;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.signal.signalselection.SignalSpacePanel;
import org.signalml.app.view.workspace.ViewerFileChooser;
import org.signalml.app.worker.document.ExportSignalWorker;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.domain.signal.raw.RawSignalWriter;
import org.signalml.domain.signal.samplesource.MultichannelSegmentedSampleSource;
import org.signalml.domain.signal.space.SegmentedSampleSourceFactory;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.exception.SanityCheckException;
import org.signalml.method.Method;
import org.signalml.method.mp5.MP5Algorithm;
import org.signalml.method.mp5.MP5ConfigCreator;
import org.signalml.method.mp5.MP5Data;
import org.signalml.method.mp5.MP5Parameters;
import org.signalml.method.mp5.MP5RuntimeParameters;
import org.signalml.method.mp5.MP5WritingModeType;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.util.Util;

/** MP5MethodDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5MethodDialog extends AbstractSignalSpaceAwarePresetDialog implements InitializingMethodConfigurer, PresetEquippedMethodConfigurer {

	private static final long serialVersionUID = 1L;

	public static final Dimension FIELD_SIZE = new Dimension(400,25);

	private static final long MEMORY_MB = 1024L*1024L;
	private static final long MEMORY_GB = 1024L*1024L*1024L;
	private static final long MEMORY_TB = 1024L*1024L*1024L*1024L;

	private PleaseWaitDialog pleaseWaitDialog;
	private MP5ExecutorManager executorManager;

	private MP5LocalExecutorDialog localExecutorDialog;
	private MP5ToolConfigDialog configDialog;

	private SignalSpacePanel signalSpacePanel;
	private MP5DictionaryConfigPanel dictionaryConfigPanel;
	private MP5DecompositionConfigPanel decompositionConfigPanel;
	private MP5ExpertConfigPanel expertConfigPanel;

	private MP5RawConfigPanel rawConfigPanel;

	private JTabbedPane tabbedPane;

	private boolean rawMode = false;
	private SwitchEditModeAction switchEditModeAction;

	private MP5ConfigCreator mp5ConfigCreator = null;
	private RawSignalWriter rawSignalWriter = null;

	private MP5Data currentData;
	private MP5Parameters currentParameters;

	private MP5SaveForLaterUseDialog mP5SaveForLaterUseDialog;

	private URL contextHelpURL = null;

	private int currentPageLength;
	private int currentSelectedChannelCount;

	private NumberFormat memoryFormat = new DecimalFormat("0.00");
	private NumberFormat atomCountFormat = DecimalFormat.getIntegerInstance();

	private Window dialogParent;

	public MP5MethodDialog(MethodPresetManager methodPresetManager, Window w) {
		super(methodPresetManager, w, true);
	}

	@Override
	protected JPanel createButtonPane() {
		JPanel buttonPane = super.createButtonPane();
		buttonPane.add(Box.createHorizontalStrut(10), 1);
		buttonPane.add(new JButton(new SaveConfigAction()), 1);
		buttonPane.add(Box.createHorizontalStrut(3), 1);
		buttonPane.add(new JButton(getSwitchEditModeAction()), 1);
		return buttonPane;
	}

	@Override
	public void initialize(ApplicationMethodManager manager) {
		setFileChooser(manager.getFileChooser());
		executorManager = manager.getMp5ExecutorManager();
		dialogParent = manager.getDialogParent();
	}

	@Override
	protected void initialize() {
		setTitle(_("MP Decomposition configuration"));
		setIconImage(IconUtils.loadClassPathImage(MP5MethodDescriptor.ICON_PATH));
		setResizable(false);
		super.initialize();
	}

	public MP5ToolConfigDialog getConfigDialog() {
		if (configDialog == null) {
			configDialog = new MP5ToolConfigDialog(dialogParent,true);
			configDialog.setExecutorManager(executorManager);
			configDialog.setLocalExecutorDialog(getLocalExecutorDialog());
		}
		return configDialog;
	}

	@Override
	protected URL getContextHelpURL() {
		if (contextHelpURL == null) {
			contextHelpURL = SvarogI18n._H("decompMP5.html");
		}
		return contextHelpURL;
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel();
		interfacePanel.setLayout(new BorderLayout());
		interfacePanel.setBorder(new EmptyBorder(3,3,3,3));

		interfacePanel.add(getTabbedPane());

		getSignalSpacePanel().getChannelSpacePanel().getChannelList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				JList list = getSignalSpacePanel().getChannelSpacePanel().getChannelList();
				int size = list.getModel().getSize();
				ListSelectionModel selecton = list.getSelectionModel();

				int selCount = 0;
				for (int i=0; i<size; i++) {
					if (selecton.isSelectedIndex(i)) {
						selCount++;
					}
				}

				boolean mmpEnabled = (selCount > 1);

				getDecompositionConfigPanel().getAlgorithmConfigPanel().setMMPEnabled(mmpEnabled);

				currentSelectedChannelCount = selCount;
				updateInfoFields();

			}

		});

		ChangeListener changeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateInfoFields();
			}
		};
		ItemListener itemListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					updateInfoFields();
				}
			}
		};

		getDictionaryConfigPanel().getDictionaryDensityConfigPanel().getEnergyErrorSpinner().addChangeListener(changeListener);
		getDecompositionConfigPanel().getAlgorithmConfigPanel().getAlgorithmComboBox().addItemListener(itemListener);

		return interfacePanel;

	}

	public SignalSpacePanel getSignalSpacePanel() {
		if (signalSpacePanel == null) {
			signalSpacePanel = new SignalSpacePanel();
			signalSpacePanel.getChannelSpacePanel().getChannelList().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		}
		return signalSpacePanel;
	}

	public MP5DictionaryConfigPanel getDictionaryConfigPanel() {
		if (dictionaryConfigPanel == null) {
			dictionaryConfigPanel = new MP5DictionaryConfigPanel(this);
		}
		return dictionaryConfigPanel;
	}

	public MP5DecompositionConfigPanel getDecompositionConfigPanel() {
		if (decompositionConfigPanel == null) {
			decompositionConfigPanel = new MP5DecompositionConfigPanel(this);
		}
		return decompositionConfigPanel;
	}

	public MP5ExpertConfigPanel getExpertConfigPanel() {
		if (expertConfigPanel == null) {
			expertConfigPanel = new MP5ExpertConfigPanel(executorManager,this);
		}
		return expertConfigPanel;
	}

	public MP5RawConfigPanel getRawConfigPanel() {
		if (rawConfigPanel == null) {
			rawConfigPanel = new MP5RawConfigPanel(executorManager,this);
		}
		return rawConfigPanel;
	}

	public JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {

			tabbedPane = new WebTabbedPane(WebTabbedPane.TOP);
			configureTabbedPaneForRawMode(rawMode);

		}
		return tabbedPane;
	}

	private void configureTabbedPaneForRawMode(boolean rawMode) {

		JTabbedPane pane = getTabbedPane();
		pane.removeAll();
		pane.addTab(_("Signal selection"), getSignalSpacePanel());

		if (rawMode) {
			pane.addTab(_("Edit raw config"), getRawConfigPanel());
		} else {
			pane.addTab(_("Dictionary"), getDictionaryConfigPanel());
			pane.addTab(_("Decomposition"), getDecompositionConfigPanel());
			pane.addTab(_("Advanced/setup"), getExpertConfigPanel());
		}

	}

	public SwitchEditModeAction getSwitchEditModeAction() {
		if (switchEditModeAction == null) {
			switchEditModeAction = new SwitchEditModeAction();
		}
		return switchEditModeAction;
	}

	public boolean setRawMode(boolean rawMode) {
		if (this.rawMode != rawMode) {

			SwitchEditModeAction modeAction = getSwitchEditModeAction();
			if (rawMode) {
				// enter raw mode

				modeAction.putValue(AbstractAction.NAME, _("To friendly mode"));
				modeAction.putValue(AbstractAction.SHORT_DESCRIPTION,_("Edit MP configuration with a user friendly editor"));

			} else {
				// exit raw mode

				if (getRawConfigPanel().isConfigChanged()) {
					int ans = OptionPane.showRawConfigWillBeLost(this);
					if (ans != OptionPane.OK_OPTION) {
						return false;
					}
				}

				modeAction.putValue(AbstractAction.NAME, _("To raw mode"));
				modeAction.putValue(AbstractAction.SHORT_DESCRIPTION,_("Edit MP configuration as text"));

			}

			this.rawMode = rawMode;

			configureTabbedPaneForRawMode(rawMode);
			getTabbedPane().setSelectedIndex(1);

			return true;

		} else {
			return true;
		}

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		MP5ApplicationData data = (MP5ApplicationData) model;
		currentData = data;

		SignalDocument signalDocument = data.getSignalDocument();
		SignalView signalView = (SignalView) signalDocument.getDocumentView();
		SignalPlot masterPlot = signalView.getMasterPlot();

		SignalSpaceConstraints signalSpaceConstraints = signalView.createSignalSpaceConstraints();

		TagDocument tagDocument = signalDocument.getActiveTag();
		if (tagDocument != null) {
			tagDocument.updateSignalSpaceConstraints(signalSpaceConstraints);
		}

		getSignalSpacePanel().setConstraints(signalSpaceConstraints);
		currentPageLength = (int)(signalSpaceConstraints.getPageSize() * signalSpaceConstraints.getSamplingFrequency());

		MP5Parameters parameters = (MP5Parameters) getPresetManager().getDefaultPreset();
		if (parameters == null) {
			parameters = data.getParameters();
		}

		SignalSelection signalSelection = signalView.getSignalSelection(masterPlot);
		Tag tag = null;
		if (tagDocument != null) {
			PositionedTag tagSelection = signalView.getTagSelection(masterPlot);
			if (tagSelection != null) {
				if (tagSelection.getTagPositionIndex() == signalDocument.getTagDocuments().indexOf(tagDocument)) {
					tag = tagSelection.getTag();
				}
			}
		}

		parameters.getSignalSpace().configureFromSelections(signalSelection, tag);

		fillDialogFromParameters(parameters, true);

		if (parameters.getRawConfigText() == null) {
			getExpertConfigPanel().getExecutorPanel().fillPanelFromModel(data);
		} else {
			getRawConfigPanel().getExecutorPanel().fillPanelFromModel(data);
		}

		getRawConfigPanel().setConfigChanged(true);

	}

	private void fillDialogFromParameters(MP5Parameters parameters, boolean includeSpace) {

		currentParameters = parameters;

		if (includeSpace) {
			getSignalSpacePanel().fillPanelFromModel(parameters.getSignalSpace());
		}

		MP5RawConfigPanel rawConfig = getRawConfigPanel();
		if (parameters.getRawConfigText() == null) {

			rawConfig.setConfigChanged(false);

			setRawMode(false);

			getDictionaryConfigPanel().fillPanelFromParameters(parameters);
			getDecompositionConfigPanel().fillPanelFromParameters(parameters);
			getExpertConfigPanel().fillPanelFromParameters(parameters);

		} else {

			rawConfig.setConfigChanged(false);

			setRawMode(true);

			rawConfig.fillPanelFromParameters(parameters);

		}

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		MP5ApplicationData data = (MP5ApplicationData) model;

		fillParametersFromDialog(data.getParameters());

		if (data.getParameters().getRawConfigText() == null) {
			getExpertConfigPanel().getExecutorPanel().fillModelFromPanel(data);
		} else {
			getRawConfigPanel().getExecutorPanel().fillModelFromPanel(data);
		}

		data.calculate();

	}

	private void fillParametersFromDialog(MP5Parameters parameters) {

		getSignalSpacePanel().fillModelFromPanel(parameters.getSignalSpace());

		if (rawMode) {
			getRawConfigPanel().fillParametersFromPanel(parameters);
		} else {
			getDictionaryConfigPanel().fillParametersFromPanel(parameters);
			getDecompositionConfigPanel().fillParametersFromPanel(parameters);
			getExpertConfigPanel().fillParametersFromPanel(parameters);

			parameters.setRawConfigText(null);
		}

	}

	@Override
	public Preset getPreset() throws SignalMLException {

		MP5Parameters parameters = new MP5Parameters();

		fillParametersFromDialog(parameters);

		return parameters;

	}

	@Override
	public void setPreset(Preset preset, boolean includeSpace) throws SignalMLException {

		MP5Parameters parameters = (MP5Parameters) preset;

		fillDialogFromParameters(parameters, includeSpace);

		getRawConfigPanel().setConfigChanged(true);

	}

	@Override
	public void validateDialog(Object model, ValidationErrors errors) throws SignalMLException {


		getSignalSpacePanel().validatePanel(errors);

		if (rawMode) {
			getRawConfigPanel().validatePanel(errors);
		} else {
			getDictionaryConfigPanel().validatePanel(errors);
			getDecompositionConfigPanel().validatePanel(errors);
			getExpertConfigPanel().validatePanel(errors);
		}

		if (rawMode) {
			getRawConfigPanel().getExecutorPanel().validatePanel(errors);
		} else {
			getExpertConfigPanel().getExecutorPanel().validatePanel(errors);
		}

	}

	public void updateInfoFields() {

		MP5DictionaryConfigPanel panel = getDictionaryConfigPanel();
		MP5DictionaryDensityConfigPanel densityConfigPanel = panel.getDictionaryDensityConfigPanel();
		double eps2 = ((Number) densityConfigPanel.getEnergyErrorSpinner().getValue()).doubleValue();
		double eps = Math.sqrt(eps2);
		double a = (1 + eps*Math.sqrt((2.0-eps2)*(eps2*eps2-2.0*eps2+2.0))) / ((1.0-eps2)*(1.0-eps2));

		// R = 0.5pi * N * ln(N)/ln(a) / ln(1-eps2)
		double approxAtomCount = Math.ceil(0.5 * Math.PI * currentPageLength
			* Math.log(currentPageLength)/Math.log(a) / Math.abs(Math.log(1.0-eps2)) );

		MP5Algorithm algorithm = (MP5Algorithm) getDecompositionConfigPanel().getAlgorithmConfigPanel().getAlgorithmComboBox().getSelectedItem();
		int k;

		switch (algorithm) {

		case SMP :
		case MMP2 :
			k = 1;
			break;

		case MMP1 :
		case MMP3 :
			k = currentSelectedChannelCount;
			break;

		default :
			throw new SanityCheckException("Unsupported algorithm [" + algorithm + "]");

		}

		// 2*sizeof(unsigned short int) + sizeof(unsigned int) + 3*sizeof(float) + 3*K*sizeof(float) + szieof(unsigned char)
		int atomSize = 2*2 + 4 + 3*4 + 3*k*4 + 1;

		double dictionarySize = approxAtomCount * atomSize;

		double unitedSize;
		String units;

		if (dictionarySize < MEMORY_GB) {
			unitedSize = ((double) dictionarySize) / MEMORY_MB;
			units = "MB";
		}
		else if (dictionarySize < MEMORY_TB) {
			unitedSize = ((double) dictionarySize) / MEMORY_GB;
			units = "GB";
		} else {
			unitedSize = ((double) dictionarySize) / MEMORY_TB;
			units = "TB";
		}

		String memoryUsageString = memoryFormat.format(unitedSize) + " " + units;
		String approxAtomCountString = atomCountFormat.format(approxAtomCount);

		densityConfigPanel.getAtomCountTextField().setText(approxAtomCountString);
		densityConfigPanel.getRamUsageTextField().setText(memoryUsageString);

	}

	@Override
	protected void onDialogClose() {
		try {
			Preset preset = getPreset();
			getPresetManager().setDefaultPreset(preset);
		} catch (Exception ex) {
			logger.debug("Failed to get preset", ex);
		}
		currentParameters = null;
	}

	@Override
	public boolean configure(Method method, Object methodDataObj) throws SignalMLException {

		if (executorManager.getExecutorCount() == 0) {

			boolean configureOk = getConfigDialog().showDialog(null, true);
			if (!configureOk) {
				return false;
			}

		}

		return showDialog(methodDataObj, true);
	}

	@Override
	public void setPresetManager(PresetManager presetManager) {
	}

	public MP5ConfigCreator getMp5ConfigCreator() {
		if (mp5ConfigCreator == null) {
			mp5ConfigCreator = new MP5ConfigCreator();
		}
		return mp5ConfigCreator;
	}

	protected MP5LocalExecutorDialog getLocalExecutorDialog() {
		if (localExecutorDialog == null) {
			localExecutorDialog = new MP5LocalExecutorDialog(this,true);
			localExecutorDialog.setFileChooser(getFileChooser());
		}
		return localExecutorDialog;
	}

	protected PleaseWaitDialog getPleaseWaitDialog() {
		if (pleaseWaitDialog == null) {
			pleaseWaitDialog = new PleaseWaitDialog(this);
			pleaseWaitDialog.initializeNow();
		}
		return pleaseWaitDialog;
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return MP5ApplicationData.class.isAssignableFrom(clazz);
	}

	protected class SwitchEditModeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SwitchEditModeAction() {
			super(_("To raw mode"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/switcheditmode.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Edit MP configuration as text"));
		}

		public void actionPerformed(ActionEvent ev) {

			if (currentParameters == null) {
				return;
			}

			if (rawMode) {

				// switch from raw mode

				fillParametersFromDialog(currentParameters);
				getRawConfigPanel().getExecutorPanel().fillModelFromPanel(currentData);

				boolean ok = setRawMode(false);
				if (!ok) {
					return;
				}

				currentParameters.setRawConfigText(null);

				fillDialogFromParameters(currentParameters, false);
				getExpertConfigPanel().getExecutorPanel().fillPanelFromModel(currentData);

			} else {

				// switch to raw mode

				fillParametersFromDialog(currentParameters);
				getExpertConfigPanel().getExecutorPanel().fillModelFromPanel(currentData);

				boolean ok = setRawMode(true);
				if (!ok) {
					return;
				}

				MP5ConfigCreator mp5ConfigCreator = getMp5ConfigCreator();

				Formatter configFormatter = mp5ConfigCreator.createConfigFormatter(false);

				mp5ConfigCreator.writeRuntimeInvariantConfig(currentParameters, configFormatter);

				currentParameters.setRawConfigText(configFormatter.toString());

				fillDialogFromParameters(currentParameters, false);
				getRawConfigPanel().getExecutorPanel().fillPanelFromModel(currentData);

				getRawConfigPanel().setConfigChanged(false);

			}

		}

	}


	protected class SaveConfigAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SaveConfigAction() {
			super(_("Save for later use"));
			putValue(AbstractAction.SMALL_ICON, IconUtils.loadClassPathIcon("org/signalml/app/icon/saveconfig.png"));
			putValue(AbstractAction.SHORT_DESCRIPTION,_("Save for later use"));
		}

		public void actionPerformed(ActionEvent ev) {

			Object currentModel = getCurrentModel();
			if (currentModel != null) {
				ValidationErrors errors = new ValidationErrors();
				try {
					validateDialog(currentModel,errors);
				} catch (SignalMLException ex) {
					logger.error("Dialog validation threw an exception", ex);
					Dialogs.showExceptionDialog(MP5MethodDialog.this, ex);
					return;
				}

				if (errors.hasErrors()) {
					showValidationErrors(errors);
					return;
				}
			}

			MP5Parameters parameters = new MP5Parameters();
			fillParametersFromDialog(parameters);

			MP5SaveForLaterUseDescriptor mP5SaveForLaterUseDescriptor = new MP5SaveForLaterUseDescriptor();
			mP5SaveForLaterUseDescriptor.setSaveSignal(true);
			mP5SaveForLaterUseDescriptor.setSaveConfig(true);

			if (mP5SaveForLaterUseDialog == null) {
				mP5SaveForLaterUseDialog = new MP5SaveForLaterUseDialog(MP5MethodDialog.this, true);
			}

			boolean ok = mP5SaveForLaterUseDialog.showDialog(mP5SaveForLaterUseDescriptor, true);
			if (!ok) {
				return;
			}
			if (!mP5SaveForLaterUseDescriptor.isSaveConfig() && !mP5SaveForLaterUseDescriptor.isSaveSignal()) {
				return;
			}

			MP5ApplicationData currentData = (MP5ApplicationData) currentModel;;
			SignalDocument signalDocument = currentData.getSignalDocument();

			boolean hasFile = false;

			File configFile = null;
			File signalFile = null;

			if (mP5SaveForLaterUseDescriptor.isSaveConfig()) {

				File file = null;
				do {

					file = ((ViewerFileChooser) getFileChooser()).chooseSaveMP5ConfigFile(MP5MethodDialog.this); //TODO remove cast
					if (file == null) {
						return;
					}

					hasFile = true;

					if (file.exists()) {
						int res = OptionPane.showFileAlreadyExists(MP5MethodDialog.this);
						if (res != OptionPane.OK_OPTION) {
							hasFile = false;
						}
					}

				} while (!hasFile);

				configFile = file;
				signalFile = Util.changeOrAddFileExtension(file, "bin");

				if (configFile.equals(signalFile)) {
					signalFile = new File(file.getParent(), "_" + file.getName());
				}

			} else {
				if (signalDocument instanceof FileBackedDocument) {
					File backingFile = ((FileBackedDocument) signalDocument).getBackingFile();
					if (backingFile != null) {
						signalFile = new File(backingFile.getName());
						String extension = Util.getFileExtension(signalFile, false);
						if (extension == null || ! "bin".equals(extension)) {
							signalFile = Util.changeOrAddFileExtension(signalFile, "bin");
						}
					}
				}
			}

			if (mP5SaveForLaterUseDescriptor.isSaveSignal()) {

				File file = null;
				do {

					file = getFileChooser().chooseExportSignalFile(MP5MethodDialog.this, signalFile);
					if (file == null) {
						return;
					}

					hasFile = true;

					if (file.exists() || (configFile != null && file.equals(configFile))) {
						int res = OptionPane.showFileAlreadyExists(MP5MethodDialog.this);
						if (res != OptionPane.OK_OPTION) {
							hasFile = false;
						}
					}

				} while (!hasFile);

				signalFile = file;

			}

			SignalView signalView = (SignalView) signalDocument.getDocumentView();
			SignalPlot plot = signalView.getMasterPlot();

			SignalProcessingChain signalChain = plot.getSignalChain();
			SignalSpace signalSpace = parameters.getSignalSpace();

			SignalProcessingChain copyChain;
			try {
				copyChain = signalChain.createLevelCopyChain(signalSpace.getSignalSourceLevel());
			} catch (SignalMLException ex) {
				logger.error("Failed to create signal chain", ex);
				Dialogs.showExceptionDialog((Window) null, ex);
				return;
			}

			TagDocument tagDocument = signalDocument.getActiveTag();

			SegmentedSampleSourceFactory factory = SegmentedSampleSourceFactory.getSharedInstance();
			MultichannelSegmentedSampleSource sampleSource = factory.getSegmentedSampleSource(copyChain, signalSpace, tagDocument != null ? tagDocument.getTagSet() : null, plot.getPageSize(), plot.getBlockSize());

			int minSampleCount = SampleSourceUtils.getMinSampleCount(sampleSource);

			if (mP5SaveForLaterUseDescriptor.isSaveSignal()) {

				if (rawSignalWriter == null) {
					rawSignalWriter = new RawSignalWriter();
				}

				SignalExportDescriptor signalExportDescriptor = new SignalExportDescriptor();
				signalExportDescriptor.setSampleType(RawSignalSampleType.FLOAT);
				signalExportDescriptor.setByteOrder(RawSignalByteOrder.LITTLE_ENDIAN);
				signalExportDescriptor.setNormalize(false);

				ExportSignalWorker worker = new ExportSignalWorker(sampleSource, signalFile, signalExportDescriptor, getPleaseWaitDialog());

				worker.execute();

				PleaseWaitDialog waitDialog = getPleaseWaitDialog();
				waitDialog.setActivity(_("exporting signal"));
				waitDialog.configureForDeterminate(0, minSampleCount, 0);
				waitDialog.waitAndShowDialogIn(MP5MethodDialog.this.getRootPane(), 500, worker);

				try {
					worker.get();
				} catch (InterruptedException ex) {
					// ignore
				} catch (ExecutionException ex) {
					logger.error("Worker failed to save signal", ex.getCause());
					Dialogs.showExceptionDialog((Window) null, ex.getCause());
					return;
				}

			}

			if (mP5SaveForLaterUseDescriptor.isSaveConfig()) {

				MP5ConfigCreator mp5ConfigCreator = getMp5ConfigCreator();

				MP5RuntimeParameters runtimeParameters = new MP5RuntimeParameters();

				runtimeParameters.setChannelCount(sampleSource.getChannelCount());
				runtimeParameters.setSegementSize(sampleSource.getSegmentLengthInSamples());
				runtimeParameters.setChosenChannels(null);
				runtimeParameters.setOutputDirectory(null);
				runtimeParameters.setPointsPerMicrovolt(1F);
				runtimeParameters.setSamplingFrequency(sampleSource.getSamplingFrequency());
				runtimeParameters.setSignalFile(signalFile);
				runtimeParameters.setWritingMode(MP5WritingModeType.CREATE);
				runtimeParameters.setMinOffset(1);
				runtimeParameters.setMaxOffset(((int) Math.floor(minSampleCount / sampleSource.getSegmentLengthInSamples())));

				Formatter configFormatter = mp5ConfigCreator.createConfigFormatter();

				String rawConfig = parameters.getRawConfigText();
				if (rawConfig == null) {
					mp5ConfigCreator.writeRuntimeInvariantConfig(parameters, configFormatter);
				} else {
					mp5ConfigCreator.writeRawConfig(rawConfig, configFormatter);
				}

				mp5ConfigCreator.writeRuntimeConfig(runtimeParameters, configFormatter);

				// write config
				try {
					mp5ConfigCreator.writeMp5Config(configFormatter, configFile);
				} catch (IOException ex) {
					logger.error("Failed to save config", ex);
					Dialogs.showExceptionDialog((Window) null, ex);
					return;
				}

			}

		}

	}

}

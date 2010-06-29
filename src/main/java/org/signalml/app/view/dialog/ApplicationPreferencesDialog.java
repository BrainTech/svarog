/* ApplicationPreferencesDialog.java created 2007-09-11
 *
 */
package org.signalml.app.view.dialog;

import java.awt.BorderLayout;
import java.awt.Window;
import java.io.File;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.signalml.SignalMLOperationMode;
import org.signalml.app.action.RegisterCodecAction;
import org.signalml.app.action.RemoveCodecAction;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.method.mp5.MP5ExecutorManager;
import org.signalml.app.method.mp5.MP5LocalExecutorDialog;
import org.signalml.app.method.mp5.MP5RemoteExecutorDialog;
import org.signalml.app.model.SignalMLCodecListModel;
import org.signalml.app.view.ViewerFileChooser;
import org.signalml.app.view.element.CodecManagerConfigPanel;
import org.signalml.app.view.element.MiscellaneousConfigPanel;
import org.signalml.app.view.element.SignalFFTSettingsPanel;
import org.signalml.app.view.element.SignalViewingConfigPanel;
import org.signalml.app.view.element.TaggingConfigPanel;
import org.signalml.app.view.element.ToolsConfigPanel;
import org.signalml.app.view.element.SignalZoomSettingsPanel;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.codec.SignalMLCodecSelector;
import org.signalml.exception.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** ApplicationPreferencesDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ApplicationPreferencesDialog extends AbstractDialog {

	private static final long serialVersionUID = 1L;

	private ViewerFileChooser fileChooser;
	private MP5ExecutorManager mp5ExecutorManager;
	private MP5LocalExecutorDialog mp5LocalExecutorDialog;
	private MP5RemoteExecutorDialog mp5RemoteExecutorDialog;

	private CodecManagerConfigPanel codecManagerPanel;
	private SignalViewingConfigPanel signalViewingConfigPanel;
	private TaggingConfigPanel taggingConfigPanel;
	private MiscellaneousConfigPanel miscellaneousConfigPanel;
	private SignalZoomSettingsPanel signalZoomSettingsPanel;
	private SignalFFTSettingsPanel signalFFTSettingsPanel;

	private ToolsConfigPanel toolsConfigPanel;

	private RegisterCodecDialog registerCodecDialog;
	private SignalMLCodecManager codecManager;
	private PleaseWaitDialog pleaseWaitDialog;

	private RegisterCodecAction registerCodecAction;
	private RemoveCodecAction removeCodecAction;

	private SignalMLOperationMode mode;
	private File profileDir;

	public ApplicationPreferencesDialog(MessageSourceAccessor messageSource, SignalMLOperationMode mode, Window f, boolean isModal) {
		super(messageSource, f, isModal);
		this.mode = mode;
	}

	@Override
	protected void initialize() {

		setTitle(messageSource.getMessage("preferences.title"));

		super.initialize();

		if (mode == SignalMLOperationMode.APPLICATION) {

			SignalMLCodecListModel codecListModel = new SignalMLCodecListModel();
			codecListModel.setCodecManager(codecManager);

			SignalMLCodecSelector selector = new SignalMLCodecSelector() {

				@Override
				public SignalMLCodec getSelectedCodec() {
					return (SignalMLCodec) codecManagerPanel.getCodecList().getSelectedValue();
				}

				@Override
				public void setSelectedCodec(SignalMLCodec codec) {
					codecManagerPanel.getCodecList().setSelectedValue(codec, true);
				}

			};

			registerCodecAction = new RegisterCodecAction(messageSource);
			registerCodecAction.setCodecManager(codecManager);
			registerCodecAction.setRegisterCodecDialog(getRegisterCodecDialog());
			registerCodecAction.setSelector(selector);
			registerCodecAction.setPleaseWaitDialog(getPleaseWaitDialog());
			registerCodecAction.initializeAll();

			removeCodecAction = new RemoveCodecAction(messageSource);
			removeCodecAction.setCodecManager(codecManager);
			removeCodecAction.setSelector(selector);

			codecManagerPanel.getCodecList().setModel(codecListModel);
			codecManagerPanel.getRegisterCodecButton().setAction(registerCodecAction);
			codecManagerPanel.getRemoveCodecButton().setAction(removeCodecAction);

		}

	}

	@Override
	public JComponent createInterface() {

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setBorder(new EmptyBorder(3,3,3,3));

		signalViewingConfigPanel = new SignalViewingConfigPanel(messageSource);
		taggingConfigPanel = new TaggingConfigPanel(messageSource);
		miscellaneousConfigPanel = new MiscellaneousConfigPanel(messageSource, mode);
		signalZoomSettingsPanel = new SignalZoomSettingsPanel(messageSource, false);
		signalFFTSettingsPanel = new SignalFFTSettingsPanel(messageSource, false);

		JPanel signalViewingContainPanel = new JPanel(new BorderLayout());
		signalViewingContainPanel.add(signalViewingConfigPanel, BorderLayout.NORTH);
		signalViewingContainPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

		JPanel taggingContainPanel = new JPanel(new BorderLayout());
		taggingContainPanel.add(taggingConfigPanel, BorderLayout.NORTH);
		taggingContainPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

		JPanel miscellaneousContainPanel = new JPanel(new BorderLayout());
		miscellaneousContainPanel.add(miscellaneousConfigPanel, BorderLayout.NORTH);
		miscellaneousContainPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

		JPanel zoomSettingsContainPanel = new JPanel(new BorderLayout());
		zoomSettingsContainPanel.add(signalZoomSettingsPanel, BorderLayout.NORTH);
		zoomSettingsContainPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

		JPanel signalFFTSettingsContainPanel = new JPanel(new BorderLayout());
		signalFFTSettingsContainPanel.add(signalFFTSettingsPanel, BorderLayout.NORTH);
		signalFFTSettingsContainPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

		tabbedPane.addTab(messageSource.getMessage("preferences.signalViewing"), signalViewingContainPanel);
		tabbedPane.addTab(messageSource.getMessage("preferences.zoomSettings"), zoomSettingsContainPanel);
		tabbedPane.addTab(messageSource.getMessage("preferences.signalFFTSettings"), signalFFTSettingsContainPanel);
		tabbedPane.addTab(messageSource.getMessage("preferences.tagging"), taggingContainPanel);
		tabbedPane.addTab(messageSource.getMessage("preferences.miscellaneous"), miscellaneousContainPanel);

		if (mode == SignalMLOperationMode.APPLICATION) {

			codecManagerPanel = new CodecManagerConfigPanel(messageSource);

			toolsConfigPanel = new ToolsConfigPanel(messageSource, fileChooser, mp5ExecutorManager);
			toolsConfigPanel.setMp5LocalExecutorDialog(getMp5LocalExecutorDialog());
			toolsConfigPanel.setMp5RemoteExecutorDialog(getMp5RemoteExecutorDialog());

			JPanel codecManagerContainPanel = new JPanel(new BorderLayout());
			codecManagerContainPanel.add(codecManagerPanel, BorderLayout.NORTH);
			codecManagerContainPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

			JPanel toolsContainPanel = new JPanel(new BorderLayout());
			toolsContainPanel.add(toolsConfigPanel, BorderLayout.NORTH);
			toolsContainPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

			tabbedPane.insertTab(messageSource.getMessage("preferences.tools"), null, toolsContainPanel, null, 4);
			tabbedPane.insertTab(messageSource.getMessage("preferences.codecs"), null, codecManagerContainPanel, null, 6);

		}

		return tabbedPane;

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

		ApplicationConfiguration config = (ApplicationConfiguration) model;

		// note the "save on every change" checkbox has no immediate effect on behaviour of codec manager
		if (mode == SignalMLOperationMode.APPLICATION) {
			registerCodecAction.setApplicationConfig(config);
			removeCodecAction.setApplicationConfig(config);

			toolsConfigPanel.fillPanelFromModel(config);
		}

		signalViewingConfigPanel.fillPanelFromModel(config);
		taggingConfigPanel.fillPanelFromModel(config);
		miscellaneousConfigPanel.fillPanelFromModel(config);
		signalZoomSettingsPanel.fillPanelFromModel(config.getZoomSignalSettings());
		signalFFTSettingsPanel.fillPanelFromModel(config.getSignalFFTSettings());

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

		ApplicationConfiguration config = (ApplicationConfiguration) model;

		signalViewingConfigPanel.fillModelFromPanel(config);
		taggingConfigPanel.fillModelFromPanel(config);
		miscellaneousConfigPanel.fillModelFromPanel(config);
		signalZoomSettingsPanel.fillModelFromPanel(config.getZoomSignalSettings());
		signalFFTSettingsPanel.fillModelFromPanel(config.getSignalFFTSettings());

		config.applySystemSettings();

		if (mode == SignalMLOperationMode.APPLICATION) {

			toolsConfigPanel.fillModelFromPanel(config);

			if (config.isSaveConfigOnEveryChange()) {
				try {
					codecManager.writeToPersistence(null);
				} catch (Exception ex) {
					logger.error("Failed to save codec configuration", ex);
				}
				try {
					mp5ExecutorManager.writeToPersistence(null);
				} catch (Exception ex) {
					logger.error("Failed to save mp5 executor configuration", ex);
				}
				try {
					config.writeToPersistence(null);
				} catch (Exception ex) {
					logger.error("Failed to save configuration", ex);
				}
			}

		}
	}

	@Override
	public void validateDialog(Object model, Errors errors) throws SignalMLException {
		super.validateDialog(model, errors);

		signalViewingConfigPanel.validate(errors);
		taggingConfigPanel.validate(errors);
		miscellaneousConfigPanel.validate(errors);

		errors.pushNestedPath("zoomSignalSettings");
		signalZoomSettingsPanel.validate(errors);
		errors.popNestedPath();

		errors.pushNestedPath("signalFFTSettings");
		signalFFTSettingsPanel.validatePanel(errors);
		errors.popNestedPath();

		if (mode == SignalMLOperationMode.APPLICATION) {
			toolsConfigPanel.validatePanel(errors);
		}

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return ApplicationConfiguration.class.isAssignableFrom(clazz);
	}

	public File getProfileDir() {
		return profileDir;
	}

	public void setProfileDir(File profileDir) {
		this.profileDir = profileDir;
	}

	protected RegisterCodecDialog getRegisterCodecDialog() {
		if (registerCodecDialog == null) {
			registerCodecDialog = new RegisterCodecDialog(messageSource,this,true);
			registerCodecDialog.setCodecManager(codecManager);
			registerCodecDialog.setProfileDir(profileDir);
		}
		return registerCodecDialog;
	}

	public SignalMLCodecManager getCodecManager() {
		return codecManager;
	}

	public void setCodecManager(SignalMLCodecManager codecManager) {
		this.codecManager = codecManager;
	}

	protected PleaseWaitDialog getPleaseWaitDialog() {
		if (pleaseWaitDialog == null) {
			pleaseWaitDialog = new PleaseWaitDialog(messageSource,this);
			pleaseWaitDialog.initializeNow();
		}
		return pleaseWaitDialog;
	}

	public ViewerFileChooser getFileChooser() {
		return fileChooser;
	}

	public void setFileChooser(ViewerFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	public MP5ExecutorManager getMp5ExecutorManager() {
		return mp5ExecutorManager;
	}

	public void setMp5ExecutorManager(MP5ExecutorManager mp5ExecutorManager) {
		this.mp5ExecutorManager = mp5ExecutorManager;
	}

	protected MP5LocalExecutorDialog getMp5LocalExecutorDialog() {
		if (mp5LocalExecutorDialog == null) {
			mp5LocalExecutorDialog = new MP5LocalExecutorDialog(messageSource,this,true);
			mp5LocalExecutorDialog.setFileChooser(fileChooser);
		}
		return mp5LocalExecutorDialog;
	}

	protected MP5RemoteExecutorDialog getMp5RemoteExecutorDialog() {
		if (mp5RemoteExecutorDialog == null) {
			mp5RemoteExecutorDialog = new MP5RemoteExecutorDialog(messageSource,this,true);
		}
		return mp5RemoteExecutorDialog;
	}

}

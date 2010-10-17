/* SvarogApplet.java created 2008-01-17
 *
 */

package org.signalml.applet;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.signalml.SignalMLOperationMode;
import org.signalml.app.action.selector.ActionFocusListener;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.action.selector.ViewFocusSelector;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.config.ConfigurationDefaults;
import org.signalml.app.config.GeneralConfiguration;
import org.signalml.app.config.MRUDConfiguration;
import org.signalml.app.config.MainFrameConfiguration;
import org.signalml.app.config.SignalFFTSettings;
import org.signalml.app.config.SignalMLCodecConfiguration;
import org.signalml.app.config.SignalMLCodecDescriptor;
import org.signalml.app.config.ZoomSignalSettings;
import org.signalml.app.config.preset.BookFilterPresetManager;
import org.signalml.app.config.preset.FFTSampleFilterPresetManager;
import org.signalml.app.config.preset.SignalExportPresetManager;
import org.signalml.app.document.DefaultDocumentManager;
import org.signalml.app.document.DocumentDetector;
import org.signalml.app.document.ExtensionBasedDocumentDetector;
import org.signalml.app.document.MRUDEntry;
import org.signalml.app.document.RawSignalMRUDEntry;
import org.signalml.app.document.SignalMLMRUDEntry;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodPresetManager;
import org.signalml.app.method.UnavailableMethodDescriptor;
import org.signalml.app.method.mp5.MP5ExecutorManager;
import org.signalml.app.method.mp5.MP5MethodDescriptor;
import org.signalml.app.montage.MontagePresetManager;
import org.signalml.app.task.ApplicationTaskManager;
import org.signalml.app.util.XMLUtils;
import org.signalml.app.view.View;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.applet.view.ViewerAppletPane;
import org.signalml.codec.DefaultSignalMLCodecManager;
import org.signalml.domain.montage.eeg.EegChannel;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.exception.SanityCheckException;
import org.signalml.method.DisposableMethod;
import org.signalml.method.Method;
import org.signalml.method.mp5.MP5Method;
import org.signalml.method.mp5.MP5Parameters;
import org.signalml.method.mp5.MP5RemoteTokenExecutor;
import org.signalml.method.mp5.remote.Credentials;
import org.signalml.method.mp5.remote.MP5RemoteConnector;
import org.signalml.method.mp5.remote.SharedSecretCredentials;
import org.signalml.method.mp5.remote.TestConnectionRequest;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.Util;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.Log4jConfigurer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;

/** SvarogApplet
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SvarogApplet extends JApplet implements ViewFocusSelector {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SvarogApplet.class);

	private Locale locale;
	private Preferences preferences;

	private MessageSourceAccessor messageSource;

//	@SuppressWarnings("unused")
//	private static File profileDir = null;

//	@SuppressWarnings("unused")
//	private static GeneralConfiguration config = null;

	private ApplicationConfiguration applicationConfig;

	private DefaultSignalMLCodecManager signalMLCodecManager;
	private DefaultDocumentManager documentManager;
	private DocumentDetector documentDetector;
	private ApplicationMethodManager methodManager;
	private ApplicationTaskManager taskManager;
	private ActionFocusManager actionFocusManager;
	private MontagePresetManager montagePresetManager;
	private BookFilterPresetManager bookFilterPresetManager;
	private SignalExportPresetManager signalExportPresetManager;
	private FFTSampleFilterPresetManager fftFilterPresetManager;
	private MP5ExecutorManager mp5ExecutorManager;

	private XStream streamer = null;

	private ViewerAppletPane appletPane;

	private MP5RemoteTokenExecutor tokenExecutor;

	@Override
	public void init() {

		Log4jConfigurer.setWorkingDirSystemProperty("signalml.root");

		try {
			Log4jConfigurer.initLogging("classpath:org/signalml/applet/logging/log4j_applet.properties");
		} catch (FileNotFoundException ex) {
			System.err.println("Critical error: no logging configuration");
			System.exit(1);
		}

		Util.dumpDebuggingInfo();

		preferences = Preferences.userRoot().node("org/signalml");
		String localeString = preferences.get("locale", "");
		if (localeString.isEmpty()) {
			initializeFirstTime();
		} else {
			locale = new Locale(localeString);
		}

		LocaleContextHolder.setLocale(locale);
		Locale.setDefault(locale);

		logger.debug("Locale set to [" + locale.toString() + "]");

		logger.debug("Applet starting");

		if (messageSource == null) {
			createMessageSource();
		}

		// XXX replace security manager to allow for dynamic code
		System.setSecurityManager(new SignalMLSecurityManager());

		// TODO maybe react to exceptions in the same way?
//		setupGUIExceptionHandler();

		createMainStreamer();

		createApplet();

		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					createGUI();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("createGUI didn't successfully complete");
		}

		logger.debug("Applet successfully created");

	}

	private void createMainStreamer() {

		streamer = XMLUtils.getDefaultStreamer();
		Annotations.configureAliases(
		        streamer,
		        ApplicationConfiguration.class,
		        ZoomSignalSettings.class,
		        SignalFFTSettings.class,
		        GeneralConfiguration.class,
		        MainFrameConfiguration.class,
		        SignalMLCodecConfiguration.class,
		        SignalMLCodecDescriptor.class,
		        MRUDConfiguration.class,
		        MRUDEntry.class,
		        SignalMLMRUDEntry.class,
		        RawSignalMRUDEntry.class,
		        RawSignalDescriptor.class,
		        EegChannel.class,
		        MethodPresetManager.class,
		        MP5Parameters.class
		);

		streamer.setMode(XStream.NO_REFERENCES);

	}

	private void createApplet() {

		applicationConfig = new ApplicationConfiguration();
		ConfigurationDefaults.setApplicationConfigurationDefaults(applicationConfig);

		applicationConfig.applySystemSettings();
		signalMLCodecManager = new DefaultSignalMLCodecManager();

		documentManager = new DefaultDocumentManager();

		documentDetector = new ExtensionBasedDocumentDetector();

		actionFocusManager = new ActionFocusManager();

		mp5ExecutorManager = new MP5ExecutorManager();

		tokenExecutor = new MP5RemoteTokenExecutor();
		tokenExecutor.setUrl(ConfigurationDefaults.getDefaultEegPlSignalmlWsURL());
		String userName = getParameter("userName");
		if (userName == null) {
			logger.warn("No userName paremeter");
			userName = "";
		}
		String loginTimeString = getParameter("loginTime");

		Date loginTime = null;
		if (loginTimeString == null || loginTimeString.isEmpty()) {
			logger.warn("No loginTime parameter");
			loginTime = null;
		} else {
			try {
				loginTime = Util.parseTime(loginTimeString);
				logger.info("Login time parsed as [" + loginTime + "]");
			} catch (ParseException ex) {
				logger.warn("Bad loginTime parameter [" + loginTimeString + "]", ex);
				loginTime = null;
			}
		}
		String token = getParameter("token");
		if (token == null) {
			logger.warn("No token parameter");
			token = "";
		}
		tokenExecutor.setUserName(userName);
		tokenExecutor.setLoginTime(loginTime);
		tokenExecutor.setToken(token);

		tokenExecutor.setName(messageSource.getMessage("mp5Method.config.remoteApplet.name"));

		mp5ExecutorManager.addExecutor(tokenExecutor);

		methodManager = new ApplicationMethodManager();
		methodManager.setMessageSource(messageSource);
		methodManager.setDocumentManager(documentManager);
		methodManager.setActionFocusManager(actionFocusManager);
		methodManager.setApplicationConfig(applicationConfig);
		methodManager.setMp5ExecutorManager(mp5ExecutorManager);
		methodManager.setStreamer(streamer);

		createMethods();

		taskManager = new ApplicationTaskManager();
		taskManager.setMode(SignalMLOperationMode.APPLET);
		taskManager.setMessageSource(messageSource);
		taskManager.setMethodManager(methodManager);

		montagePresetManager = new MontagePresetManager();

		bookFilterPresetManager = new BookFilterPresetManager();

		actionFocusManager.setMontagePresetManager(montagePresetManager);

		signalExportPresetManager = new SignalExportPresetManager();

		fftFilterPresetManager = new FFTSampleFilterPresetManager();

	}

	private void createMethods() {

		try {

			try {
				MP5Method mp5Method = (MP5Method) methodManager.registerMethod(MP5Method.class);
				File tempDir = new File(System.getProperty("java.io.tmpdir"));
				if (!tempDir.exists()) {
					throw new SignalMLException("No temp directory");
				}
				File mp5TempDir = new File(tempDir, "mp5");
				if (!mp5TempDir.exists()) {
					boolean mkDirOk = mp5TempDir.mkdir();
					if (!mkDirOk) {
						throw new SignalMLException("Failed to create mp5 dir [" + mp5TempDir.getAbsolutePath() + "]");
					}
				}
				mp5Method.setTempDirectory(mp5TempDir);
				mp5Method.setExecutorLocator(mp5ExecutorManager);
				MP5MethodDescriptor mp5Descriptor = new MP5MethodDescriptor(mp5Method);
				methodManager.setMethodData(mp5Method, mp5Descriptor);
			} catch (SignalMLException ex) {
				logger.error("Failed to create mp5 method", ex);
				throw ex;
			} catch (Throwable t) {
				logger.error("Serious error - failed to create mp5 method", t);
				throw t;
			}

		} catch (Throwable t) {
			UnavailableMethodDescriptor descriptor = new UnavailableMethodDescriptor(MP5MethodDescriptor.RUN_METHOD_STRING, t);
			methodManager.addUnavailableMethod(descriptor);
		}

	}

	private void createGUI() {

		logger.debug("Creating applet GUI");

		setLayout(new BorderLayout());

		ViewerElementManager elementManager = new ViewerElementManager(SignalMLOperationMode.APPLET);
		elementManager.setMessageSource(messageSource);
		elementManager.setDocumentManager(documentManager);
		elementManager.setCodecManager(signalMLCodecManager);
		elementManager.setDocumentDetector(documentDetector);
		elementManager.setApplicationConfig(applicationConfig);
		elementManager.setMethodManager(methodManager);
		elementManager.setTaskManager(taskManager);
		elementManager.setActionFocusManager(actionFocusManager);
		elementManager.setMontagePresetManager(montagePresetManager);
		elementManager.setBookFilterPresetManager(bookFilterPresetManager);
		elementManager.setSignalExportPresetManager(signalExportPresetManager);
		elementManager.setFftFilterPresetManager(fftFilterPresetManager);
		elementManager.setMp5ExecutorManager(mp5ExecutorManager);
		elementManager.setPreferences(preferences);
		elementManager.configureImportedElements();

		appletPane = new ViewerAppletPane(this);
		appletPane.setMessageSource(messageSource);
		appletPane.setElementManager(elementManager);
		appletPane.initialize();

		getRootPane().setJMenuBar(elementManager.getMenuBar());

		add(appletPane, BorderLayout.CENTER);

		logger.debug("Successfully created applet GUI");

		// TODO remove this test (or convert to legitimate feature, maybe somewhere in file menu)
		JMenuBar menuBar = elementManager.getMenuBar();
		JMenu testMenu = new JMenu("Test");

		AbstractAction testAction = new AbstractAction("Test server") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {

				try {

					MP5RemoteConnector connector = MP5RemoteConnector.getSharedInstance();

					Credentials credentials = new Credentials();

					SharedSecretCredentials sharedSecretCredentials = new SharedSecretCredentials();
					sharedSecretCredentials.setUserName(tokenExecutor.getUserName());
					sharedSecretCredentials.setLoginTime(tokenExecutor.getLoginTime());
					sharedSecretCredentials.setToken(tokenExecutor.getToken());

					credentials.setSharedSecretCredentials(sharedSecretCredentials);

					TestConnectionRequest request = new TestConnectionRequest();
					request.setCredentials(credentials);
					request.setHelloString("hello");

					connector.testConnection(tokenExecutor.getUrl(), request);

				} catch (Exception ex) {
					logger.error("Failed", ex);
					OptionPane.showMessageDialog(SvarogApplet.this, ex.getMessage(), "Failed, see console for details", OptionPane.ERROR_MESSAGE);
					return;
				}

				OptionPane.showMessage(SvarogApplet.this, "mp5Method.config.remote.testConnectionSuccess");

			}

		};

		testMenu.add(testAction);
		menuBar.add(testMenu);

	}

	@Override
	public void start() {
		logger.debug("Applet started");
		super.start();
	}

	@Override
	public void stop() {
		logger.debug("Applet stopped");
		super.stop();
	}

	@Override
	public void destroy() {

		Method[] methods = methodManager.getMethods();
		for (Method method : methods) {
			if (method instanceof DisposableMethod) {
				try {
					((DisposableMethod) method).dispose();
				} catch (SignalMLException ex) {
					logger.error("Failed to dispose method [" + method.getName() + "]", ex);
				}
			}
		}

		super.destroy();

		logger.debug("Applet destroyed");

	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	private void initializeFirstTime() {

		if (locale == null) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						locale = OptionPane.showLanguageOption();
					}
				});
			} catch (InterruptedException ex) {
				logger.error("Language choice error", ex);
				throw new SanityCheckException(ex);
			} catch (InvocationTargetException ex) {
				logger.error("Language choice error", ex);
				throw new SanityCheckException(ex);
			}
		}
		if (locale == null) {
			logger.warn("Language choice canceled");
			locale = Locale.getDefault();
		}

		preferences.put("locale", locale.toString());

	}

	private void createMessageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setCacheSeconds(-1);
		messageSource.setBasenames(new String[] {
		                                   "classpath:org/signalml/app/resource/message",
		                                   "classpath:org/signalml/resource/mp5",
		                                   "classpath:org/signalml/resource/wsmessage"
		                           });

		this.messageSource = new MessageSourceAccessor(messageSource, locale);
		OptionPane.setMessageSource(this.messageSource);
		ErrorsDialog.setStaticMessageSource(this.messageSource);
	}

	@Override
	public View getActiveView() {
		return appletPane;
	}

	@Override
	public void addActionFocusListener(ActionFocusListener listener) {
		// ignored
	}

	@Override
	public void removeActionFocusListener(ActionFocusListener listener) {
		// ignored
	}

}

/* SvarogApplication.java created 2007-09-10
 *
 */
package org.signalml.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.signalml.SignalMLOperationMode;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.config.ConfigurationDefaults;
import org.signalml.app.config.GeneralConfiguration;
import org.signalml.app.config.MRUDConfiguration;
import org.signalml.app.config.MainFrameConfiguration;
import org.signalml.app.config.SignalMLCodecConfiguration;
import org.signalml.app.config.SignalMLCodecDescriptor;
import org.signalml.app.config.ZoomSignalSettings;
import org.signalml.app.config.preset.BookFilterPresetManager;
import org.signalml.app.config.preset.FFTSampleFilterPresetManager;
import org.signalml.app.config.preset.TimeDomainSampleFilterPresetManager;
import org.signalml.app.config.preset.PredefinedTimeDomainFiltersPresetManager;
import org.signalml.app.config.preset.PresetManager;
import org.signalml.app.config.preset.SignalExportPresetManager;
import org.signalml.app.document.DefaultDocumentManager;
import org.signalml.app.document.DefaultMRUDRegistry;
import org.signalml.app.document.DocumentDetector;
import org.signalml.app.document.ExtensionBasedDocumentDetector;
import org.signalml.app.document.MRUDEntry;
import org.signalml.app.document.RawSignalMRUDEntry;
import org.signalml.app.document.SignalMLMRUDEntry;
import org.signalml.app.logging.SvarogLogger;
import org.signalml.app.method.ApplicationMethodDescriptor;
import org.signalml.app.method.ApplicationMethodManager;
import org.signalml.app.method.MethodPresetManager;
import org.signalml.app.method.UnavailableMethodDescriptor;
import org.signalml.app.method.bookaverage.BookAverageMethodDescriptor;
import org.signalml.app.method.booktotag.BookToTagMethodDescriptor;
import org.signalml.app.method.ep.EvokedPotentialMethodDescriptor;
import org.signalml.app.method.example.ExampleMethodDescriptor;
import org.signalml.app.method.mp5.MP5ApplicationData;
import org.signalml.app.method.mp5.MP5ExecutorManager;
import org.signalml.app.method.mp5.MP5MethodDescriptor;
import org.signalml.app.montage.MontagePresetManager;
import org.signalml.app.task.ApplicationTaskManager;
import org.signalml.app.util.MatlabUtil;
import org.signalml.app.util.PreferenceName;
import org.signalml.app.util.XMLUtils;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.ViewerMainFrame;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.app.view.dialog.ProfilePathDialog;
import org.signalml.app.view.dialog.SplashScreen;
import org.signalml.codec.DefaultSignalMLCodecManager;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.domain.montage.eeg.EegChannel;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.method.DisposableMethod;
import org.signalml.method.Method;
import org.signalml.method.bookaverage.BookAverageMethod;
import org.signalml.method.booktotag.BookToTagMethod;
import org.signalml.method.ep.EvokedPotentialMethod;
import org.signalml.method.ep.EvokedPotentialParameters;
import org.signalml.method.example.ExampleMethod;
import org.signalml.method.mp5.MP5Data;
import org.signalml.method.mp5.MP5Method;
import org.signalml.method.mp5.MP5Parameters;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.impl.PluginAccessClass;
import org.signalml.plugin.loader.PluginLoaderHi;
import org.signalml.util.SvarogConstants;
import org.signalml.util.Util;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Log4jConfigurer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import org.signalml.app.config.preset.StyledTagSetPresetManager;
import org.signalml.app.worker.amplifiers.AmplifierDefinitionPresetManager;
import org.signalml.app.worker.processes.OpenBCIModulePresetManager;
import org.signalml.app.worker.processes.ProcessManager;
import org.signalml.domain.montage.filter.TimeDomainSampleFilter;

/**
 * The Svarog application.
 *
 * This is a singleton.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class SvarogApplication implements java.lang.Runnable {

	private static SvarogApplication Instance = null;

	private Preferences preferences = null;
	private Locale locale = null;
	protected static final Logger logger = Logger.getLogger(SvarogApplication.class);
	public static final int INITIALIZATION_STEP_COUNT = 5;
	private File profileDir = null;
	private ApplicationConfiguration applicationConfig = null;
	private DefaultSignalMLCodecManager signalMLCodecManager = null;
	private DefaultDocumentManager documentManager = null;
	private DefaultMRUDRegistry mrudRegistry = null;
	private DocumentDetector documentDetector = null;
	private ApplicationMethodManager methodManager = null;
	private ApplicationTaskManager taskManager = null;
	private ActionFocusManager actionFocusManager = null;
	private MontagePresetManager montagePresetManager = null;
	private BookFilterPresetManager bookFilterPresetManager = null;
	private SignalExportPresetManager signalExportPresetManager = null;
	private FFTSampleFilterPresetManager fftFilterPresetManager = null;
	private AmplifierDefinitionPresetManager amplifierDefinitionPresetManager = null;
	private OpenBCIModulePresetManager openBCIModulePresetManager = null;

	/**
	 * A {@link PresetManager} managing the user-defined
	 * {@link TimeDomainSampleFilter} presets.
	 */
	private TimeDomainSampleFilterPresetManager timeDomainSampleFilterPresetManager = null;

	/**
	 * A {@link PresetManager} managing the predefined
	 * {@link TimeDomainSampleFilter TimeDomainSampleFilters}.
	 */
	private PredefinedTimeDomainFiltersPresetManager predefinedTimeDomainSampleFilterPresetManager = null;

	private StyledTagSetPresetManager styledTagSetPresetManager;

	private MP5ExecutorManager mp5ExecutorManager = null;
	private ViewerMainFrame viewerMainFrame = null;
	private XStream streamer = null;
	private SplashScreen splashScreen = null;
	private String startupDir = null;
	// this needs to be a field to allow for invokeAndWait
	private GeneralConfiguration initialConfig = null;
	private boolean molTest = false;

	/** {@link ViewerElementManager} shared instance. */
	private ViewerElementManager viewerElementManager;

	/** This static boolean indicates whether {@link #main(String[]) static void main(String[])} was already called. */
	private static boolean mainCalled = false;
	/** This boolean Indicates whether {@link #run() void run()} was already called. */
	private boolean runCalled = false;
	/** Command line arguments (as passed to {@link #main(String[]) static void main(String[])}). */
	private String[] cmdLineArgs;

	/**
	 * Returns the single SvarogApplication instance.
	 */
	public static SvarogApplication getSharedInstance() {
		return Instance;
	}

	/**
	 * The Svarog main method.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		final String errorMsg = "You will not trick Svarog this easily, hahaha! Not this time!";

		if (mainCalled) {
			throw new IllegalStateException(errorMsg);
		} else {
			synchronized (SvarogApplication.class) {
				if (mainCalled)
					throw new IllegalStateException(errorMsg);
				mainCalled = true;
			}
		}

		SvarogLogger.getSharedInstance().debug("Preparing Svarog " + SvarogConstants.VERSION);
		SvarogLogger.getSharedInstance().debugThreads();
		SvarogLogger.getSharedInstance().debugCL();

		// install security manager
		SvarogSecurityManager.install();

		// install dead thread exception handler...
		SvarogExceptionHandler.install();

		// replace AWT event queue
		SvarogAWTEventQueue.install();

		// Launch Svarog
		launchSvarog(args);

		SvarogLogger.getSharedInstance().debug("SvarogApplication.main complete!");
	}

	/**
	 * Creates the shared instance, starts it in a separate thread and waits for it to complete.
	 *
	 * @see Thread#join()
	 */
	private static void launchSvarog(String[] args) {
		// init the shared instance...
		Instance = new SvarogApplication(args);

		// start the svarog thread!
		Thread t = SvarogThreadGroup.getSharedInstance().createNewThread(getSharedInstance(), "Svarog app");
		t.start();

		while (true) {
			try {
				SvarogLogger.getSharedInstance().debug("Waiting for main Svarog thread to complete...");
				t.join();
				break;
			} catch (java.lang.InterruptedException e) {
				SvarogLogger.getSharedInstance().warning("Top level: interrupted: " + e + "; looping back...");
			}
		}

		SvarogLogger.getSharedInstance().debug("SvarogApplication.launchSvarog complete!");
	}

	/**
	 * A private constructor.
	 *
	 * @param args the command line arguments.
	 */
	private SvarogApplication(String[] args) {
		super();
		this.cmdLineArgs = args;
	}

	@Override
	/**
	 * This makes some simple checks, prints some debug information to
	 * standard error and calls {@link #_run}.
	 */
	public void run() {
		final String errorMsg = "run() already called!";

		if (runCalled) {
			throw new IllegalStateException(errorMsg);
		} else {
			synchronized (this) {
				if (runCalled)
					throw new IllegalStateException(errorMsg);
				runCalled = true;
			}
		}

		SvarogLogger.getSharedInstance().debug("Starting Svarog...");
		SvarogLogger.getSharedInstance().debugThreads();

		try {
			_run(cmdLineArgs);
		} catch(Throwable e) {
			SvarogLogger.getSharedInstance().error("uncaught exception", e);

			// also log directly to stderr in case of problems with logging
			System.err.println("unable to initialize svarog: " + e);
			System.exit(11);
		}
	}

	private void _print_help_and_exit(Options options) {
		HelpFormatter hf = new HelpFormatter();
		hf.printHelp("/usr/bin/svarog <options>", options);
		System.exit(0);
	}

	private void _run(String[] args) {
		startupDir = System.getProperty("user.dir");

		final Options options = new Options();
		options.addOption("h", "help", false, "display help");
		options.addOption("R", "reset", false, "reset workspace settings");
		options.addOption("s", "nosplash", false, "don't display splash screen");
		options.addOption("m", "moltest", false, "include test method");

		for (String arg: args)
			if (arg.equals("-h") || arg.equals("--help"))
				_print_help_and_exit(options);

		CommandLine line = null;
		try {
			line = new GnuParser().parse(options, args);
		} catch (ParseException exp) {
			System.err.println("Parsing failed. Reason: " + exp.getMessage());
			System.exit(1);
		}

		if (line.hasOption("help"))
			_print_help_and_exit(options);

		if (line.hasOption("moltest"))
			molTest = true;

		Log4jConfigurer.setWorkingDirSystemProperty("signalml.root");

		// allow for local file config
		File loggingConfig = new File(startupDir, "logging.properties");
		String loggingPath = null;
		if (loggingConfig.exists()) {
			loggingPath = "file:" + loggingConfig.getAbsolutePath();
		} else {
			loggingPath = "classpath:org/signalml/app/logging/log4j_app.properties";
		}

		try {
			Log4jConfigurer.initLogging(loggingPath);
		} catch (FileNotFoundException ex) {
			System.err.println("Critical error: no logging configuration");
			System.exit(1);
		}

		Util.dumpDebuggingInfo();

		createMainStreamer();

		preferences = Preferences.userRoot().node("org/signalml");
		boolean initialized = false;
		if (line.hasOption("reset")) {
			preferences.remove(PreferenceName.INITIALIZED.toString());
			preferences.remove(PreferenceName.PROFILE_DEFAULT.toString());
			preferences.remove(PreferenceName.PROFILE_PATH.toString());
		} else {
			initialized = preferences.getBoolean(PreferenceName.INITIALIZED.toString(), false);
		}

		if (initialized) {
			initialize();
		} else {
			initializeFirstTime(null);
			preferences.putBoolean(PreferenceName.INITIALIZED.toString(), true);
		}

		LocaleContextHolder.setLocale(locale);
		Locale.setDefault(locale);

		logger.debug("Locale set to [" + locale.toString() + "]");
		logger.debug("Application starting");

		// TODO check nested modal dialogs
		// setupGUIExceptionHandler();

		if (!line.hasOption("nosplash")) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

						@Override
						public void run() {
							splashScreen = new SplashScreen();
							splashScreen.setVisible(true);
						}
					});
			} catch (InterruptedException ex) {
				logger.error("Failed to create splash screen", ex);
				System.exit(1);
			} catch (InvocationTargetException ex) {
				logger.error("Failed to create splash screen", ex);
				System.exit(1);
			}
		}

		createApplication();

		try {
			SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						createMainFrame();
					}
				});
		} catch (InterruptedException ex) {
			logger.error("Failed to create GUI", ex);
			System.exit(1);
		} catch (InvocationTargetException ex) {
			logger.error("Failed to create GUI", ex);
			System.exit(1);
		}

		logger.debug("Application successfully created - main window is showing and should be visible soon");

		PluginLoaderHi.getInstance().loadPlugins();

		SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					viewerMainFrame.setVisible(true);
					viewerMainFrame.bootstrap();
					if (splashScreen != null) {
						splashScreen.setVisible(false);
						splashScreen.dispose();
						splashScreen = null;
					}
				}
			});

		SvarogLogger.getSharedInstance().debug("SvarogApplication._run complete!");
	}

	private void initializeFirstTime(final GeneralConfiguration suggested) {

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
				System.exit(1);
			} catch (InvocationTargetException ex) {
				logger.error("Language choice error", ex);
				System.exit(1);
			}
		}
		if (locale == null) {
			logger.error("Language choice canceled");
			System.exit(1);
		}

		boolean ok = false;
		initialConfig = null;
		do {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

						@Override
						public void run() {
							initialConfig = askForProfilePath(suggested);
						}
					});
			} catch (InterruptedException ex) {
				logger.error("Profile choice error", ex);
				System.exit(1);
			} catch (InvocationTargetException ex) {
				logger.error("Profile choice error", ex);
				System.exit(1);
			}
			initialConfig.setLocale(locale.toString());
			ok = setProfileDir(initialConfig, true);
		} while (!ok);

		preferences.putBoolean(PreferenceName.PROFILE_DEFAULT.toString(), initialConfig.isProfileDefault());
		if (initialConfig.isProfileDefault()) {
			preferences.remove(PreferenceName.PROFILE_PATH.toString());
		} else {
			preferences.put(PreferenceName.PROFILE_PATH.toString(), initialConfig.getProfilePath());
		}

		try {
			initialConfig.writeToXML(initialConfig.getStandardFile(profileDir), streamer);
		} catch (IOException ex) {
			logger.error("Failed to write configuration", ex);
		}

	}

	private void initialize() {

		GeneralConfiguration config = new GeneralConfiguration();
		ConfigurationDefaults.setGeneralConfigurationDefaults(config);

		String profileDefault = preferences.get(PreferenceName.PROFILE_DEFAULT.toString(), null);
		String profilePath = preferences.get(PreferenceName.PROFILE_PATH.toString(), null);

		if (profileDefault == null) {
			logger.error("Profile settings seem to be lost");
			initializeFirstTime(null);
			return;
		}

		boolean profileDef = Boolean.parseBoolean(profileDefault);
		config.setProfileDefault(profileDef);
		if (profileDef) {
			config.setProfilePath(null);
		} else {
			config.setProfilePath(profilePath);
		}

		boolean ok = setProfileDir(config, false);
		if (!ok) {
			logger.error("Profile settings seem to be invalid");
			initializeFirstTime(config);
			return;
		}

		GeneralConfiguration config2 = new GeneralConfiguration();
		try {
			config2.readFromXML(config2.getStandardFile(profileDir), streamer);
		} catch (FileNotFoundException ex) {
			logger.debug("Failed to read configuration - file not found, will have to reinitialize");
			initializeFirstTime(config);
			return;
		} catch (Exception ex) {
			logger.error("Failed to read configuration", ex);
			initializeFirstTime(config);
			return;
		}

		locale = new Locale(config2.getLocale());
		config.setLocale(locale.toString());

	}

	private boolean setProfileDir(GeneralConfiguration config, boolean firstTime) {

		String profilePath = null;
		if (config.isProfileDefault()) {
			profilePath = System.getProperty("user.home") + File.separator + "signalml";
			logger.debug("Setting profile path to default [" + profilePath + "]");
		} else {
			profilePath = config.getProfilePath();
			logger.debug("Setting profile path to chosen [" + profilePath + "]");
		}

		File file = (new File(profilePath)).getAbsoluteFile();
		if (!file.exists()) {
			logger.debug("Profile dir not found...");
			if (firstTime) {
				// create
				boolean ok = file.mkdirs();
				if (!ok) {
					logger.error("Failed to create profile dir");
					// return false to indicate dir invalid
					return false;
				}
			} else {
				// return false to indicate dir invalid
				return false;
			}
		}

		if (!file.isDirectory()) {
			logger.error("This is not a directory");
			return false;
		}

		if (!file.canRead() || !file.canWrite()) {
			logger.error("Selected profile path not accessible");
			return false;
		}

		profileDir = file;

		PluginLoaderHi.createInstance(profileDir);
		return true;
	}

	private GeneralConfiguration askForProfilePath(GeneralConfiguration suggested) {

		ProfilePathDialog dialog = new ProfilePathDialog(null, true);

		GeneralConfiguration model;
		if (suggested == null) {
			model = new GeneralConfiguration();
			ConfigurationDefaults.setGeneralConfigurationDefaults(model);
		} else {
			model = suggested;
		}

		boolean result = dialog.showDialog(model, 0.5, 0.2);
		if (!result) {
			// we do not allow continuation if profile selection was cancelled
			System.exit(1);
		}

		return model;

	}

	public void splash(String newMessage, boolean doStep) {
		if (splashScreen != null) {
			splashScreen.updateSplash(newMessage, doStep);
		}
	}

	private void createMainStreamer() {

		streamer = XMLUtils.getDefaultStreamer();
		Annotations.configureAliases(streamer,
					     ApplicationConfiguration.class,
					     ZoomSignalSettings.class,
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
					     MP5Parameters.class,
					     MP5Data.class,
					     MP5ApplicationData.class,
					     EvokedPotentialParameters.class
					     );

		streamer.setMode(XStream.NO_REFERENCES);

	}

	private void createApplication() {

		splash(_("Restoring configuration"), false);

		applicationConfig = new ApplicationConfiguration();
		applicationConfig.setProfileDir(profileDir);
		applicationConfig.setStreamer(streamer);
		ConfigurationDefaults.setApplicationConfigurationDefaults(applicationConfig);
		applicationConfig.maybeReadFromPersistence(
				"Application config not found - will use defaults",
				"Failed to read application configuration - will use defaults");
		applicationConfig.applySystemSettings();

		splash(_("Initializing codecs"), true);

		signalMLCodecManager = new DefaultSignalMLCodecManager();
		signalMLCodecManager.setProfileDir(profileDir);
		signalMLCodecManager.setStreamer(streamer);

		try {
			signalMLCodecManager.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("Seems like codec configuration doesn't exist - codecs will not be restored");
		} catch (Exception ex) {
			logger.error("Failed to read codec manager configuration - codecs lost", ex);
		}

		signalMLCodecManager.verify();

		splash(_("Initializing document manager"), true);

		mrudRegistry = new DefaultMRUDRegistry();
		mrudRegistry.setProfileDir(profileDir);
		mrudRegistry.setStreamer(streamer);

		try {
			mrudRegistry.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("Seems like mrud configuration doesn't exist - mruds will not be restored");
		} catch (Exception ex) {
			logger.error("Failed to read mrud codec manager configuration - mruds lost", ex);
		}

		documentManager = new DefaultDocumentManager();

		splash(_("Initializing services"), true);

		documentDetector = new ExtensionBasedDocumentDetector();

		actionFocusManager = new ActionFocusManager();

		mp5ExecutorManager = new MP5ExecutorManager();
		mp5ExecutorManager.setProfileDir(profileDir);

		try {
			mp5ExecutorManager.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("MP5 executor manager config not found - will use defaults");
		} catch (Exception ex) {
			logger.error("Failed to read MP5 executor manager configuration - will use defaults", ex);
		}

		methodManager = new ApplicationMethodManager();
		methodManager.setProfileDir(profileDir);
		methodManager.setStreamer(streamer);
		methodManager.setDocumentManager(documentManager);
		methodManager.setActionFocusManager(actionFocusManager);
		methodManager.setApplicationConfig(applicationConfig);
		methodManager.setMp5ExecutorManager(mp5ExecutorManager);

		createMethods();

		taskManager = new ApplicationTaskManager();
		taskManager.setMode(SignalMLOperationMode.APPLICATION);
		taskManager.setMethodManager(methodManager);

		splash(_("Initializing presets"), true);

		montagePresetManager = new MontagePresetManager();
		montagePresetManager.setProfileDir(profileDir);

		try {
			montagePresetManager.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("Montage preset config not found - will use defaults");
		} catch (Exception ex) {
			logger.error("Failed to read montage configuration - will use defaults", ex);
		}

		bookFilterPresetManager = new BookFilterPresetManager();
		bookFilterPresetManager.setProfileDir(profileDir);

		try {
			bookFilterPresetManager.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("Book filter preset config not found - will use defaults");
		} catch (Exception ex) {
			logger.error("Failed to read book filter configuration - will use defaults", ex);
		}

		actionFocusManager.setMontagePresetManager(montagePresetManager);

		signalExportPresetManager = new SignalExportPresetManager();
		signalExportPresetManager.setProfileDir(profileDir);

		try {
			signalExportPresetManager.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("Signal export preset config not found - will use defaults");
		} catch (Exception ex) {
			logger.error("Failed to read signal export configuration - will use defaults", ex);
		}

		fftFilterPresetManager = new FFTSampleFilterPresetManager();
		fftFilterPresetManager.setProfileDir(profileDir);

		try {
			fftFilterPresetManager.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("FFT sample filter preset config not found - will use defaults");
		} catch (Exception ex) {
			logger.error("Failed to read FFT sample filter configuration - will use defaults", ex);
		}

		amplifierDefinitionPresetManager = new AmplifierDefinitionPresetManager();
		amplifierDefinitionPresetManager.setProfileDir(profileDir);

		try {
			amplifierDefinitionPresetManager.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("Amplifier definition preset config not found - will use defaults");
		} catch (Exception ex) {
			logger.error("Failed to read amplifier definition configuration - will use defaults", ex);
		}

		openBCIModulePresetManager = new OpenBCIModulePresetManager();
		openBCIModulePresetManager.setProfileDir(profileDir);

		try {
			openBCIModulePresetManager.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("OpenBCI modules preset config not found - will use defaults");
		} catch (Exception ex) {
			logger.error("Failed to read OpenBCI modules configuration - will use defaults", ex);
		}

		timeDomainSampleFilterPresetManager = new TimeDomainSampleFilterPresetManager();
		timeDomainSampleFilterPresetManager.setProfileDir(profileDir);

		try {
			timeDomainSampleFilterPresetManager.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("Time domain sample filter preset config not found - will use defaults");
		} catch (Exception ex) {
			logger.error("Failed to read time domain sample filter configuration - will use defaults", ex);
		}

		predefinedTimeDomainSampleFilterPresetManager = new PredefinedTimeDomainFiltersPresetManager();

		try {
			predefinedTimeDomainSampleFilterPresetManager.loadDefaults();
		} catch (FileNotFoundException ex) {
			logger.error("Failed to read predefined time domain sample filters - file not found", ex);
		} catch (Exception ex) {
			logger.error("Failed to read predefined time domain sample filters", ex);
		}

		styledTagSetPresetManager = new StyledTagSetPresetManager();
		styledTagSetPresetManager.setProfileDir(profileDir);

		try {
			styledTagSetPresetManager.readFromPersistence(null);
		} catch (FileNotFoundException ex) {
			logger.debug("Styled tag set preset config not found - will use defaults");
		} catch (Exception ex) {
			logger.error("Failed to read styled tag set configuration - will use defaults", ex);
		}

		splash(null, true);

	}

	private void createMethods() {

		// Prevent Matlab from replacing L&F
		MatlabUtil.initialize();

		if (molTest) {
			ExampleMethod exampleMethod = null;
			try {

				try {
					exampleMethod = (ExampleMethod) methodManager.registerMethod(ExampleMethod.class);
					ExampleMethodDescriptor exampleMethodDescriptor =
						new ExampleMethodDescriptor(exampleMethod);
					methodManager.setMethodData(exampleMethod, exampleMethodDescriptor);
				} catch (SignalMLException ex) {
					logger.error("Failed to create example method", ex);
					throw ex;
				} catch (Throwable t) {
					logger.error("Serious error - failed to create example method", t);
					throw t;
				}

			} catch (Throwable t) {
				UnavailableMethodDescriptor descriptor =
					new UnavailableMethodDescriptor(ExampleMethodDescriptor.RUN_METHOD_STRING, t);
				methodManager.addUnavailableMethod(descriptor);
			}
		}

		MP5Method mp5Method = null;
		try {

			try {
				mp5Method = (MP5Method) methodManager.registerMethod(MP5Method.class);
				mp5Method.setTempDirectory(profileDir);
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
			UnavailableMethodDescriptor descriptor =
				new UnavailableMethodDescriptor(MP5MethodDescriptor.RUN_METHOD_STRING, t);
			methodManager.addUnavailableMethod(descriptor);
		}

		EvokedPotentialMethod evokedPotentialMethod = null;
		try {

			try {
				evokedPotentialMethod = (EvokedPotentialMethod) methodManager.registerMethod(EvokedPotentialMethod.class);
				EvokedPotentialMethodDescriptor evokedPotentialDescriptor =
					new EvokedPotentialMethodDescriptor(evokedPotentialMethod);
				methodManager.setMethodData(evokedPotentialMethod, evokedPotentialDescriptor);
			} catch (SignalMLException ex) {
				logger.error("Failed to create evoked potential method", ex);
				throw ex;
			} catch (Throwable t) {
				logger.error("Serious error - failed to create evoked potential method", t);
				throw t;
			}

		} catch (Throwable t) {
			UnavailableMethodDescriptor descriptor =
				new UnavailableMethodDescriptor(EvokedPotentialMethodDescriptor.RUN_METHOD_STRING, t);
			methodManager.addUnavailableMethod(descriptor);
		}

		BookAverageMethod bookAverageMethod = null;
		try {

			try {
				bookAverageMethod = (BookAverageMethod) methodManager.registerMethod(BookAverageMethod.class);
				BookAverageMethodDescriptor bookAverageDescriptor =
					new BookAverageMethodDescriptor(bookAverageMethod);
				methodManager.setMethodData(bookAverageMethod, bookAverageDescriptor);
			} catch (SignalMLException ex) {
				logger.error("Failed to create book average method", ex);
				throw ex;
			} catch (Throwable t) {
				logger.error("Serious error - failed to create book average method", t);
				throw t;
			}

		} catch (Throwable t) {
			UnavailableMethodDescriptor descriptor =
				new UnavailableMethodDescriptor(BookAverageMethodDescriptor.RUN_METHOD_STRING, t);
			methodManager.addUnavailableMethod(descriptor);
		}

		BookToTagMethod bookToTagMethod = null;
		try {

			try {
				bookToTagMethod = (BookToTagMethod) methodManager.registerMethod(BookToTagMethod.class);
				BookToTagMethodDescriptor bookToTagDescriptor =
					new BookToTagMethodDescriptor(bookToTagMethod);
				methodManager.setMethodData(bookToTagMethod, bookToTagDescriptor);
			} catch (SignalMLException ex) {
				logger.error("Failed to create book to tag method", ex);
				throw ex;
			} catch (Throwable t) {
				logger.error("Serious error - failed to create book to tag method", t);
				throw t;
			}

		} catch (Throwable t) {
			UnavailableMethodDescriptor descriptor =
				new UnavailableMethodDescriptor(BookToTagMethodDescriptor.RUN_METHOD_STRING, t);
			methodManager.addUnavailableMethod(descriptor);
		}

	}

	private void createMainFrame() {

		splash(_("Creating main window"), false);

		ViewerElementManager elementManager = new ViewerElementManager(SignalMLOperationMode.APPLICATION);
		elementManager.setProfileDir(profileDir);
		elementManager.setDocumentManager(documentManager);
		elementManager.setMrudRegistry(mrudRegistry);
		elementManager.setCodecManager(signalMLCodecManager);
		elementManager.setDocumentDetector(documentDetector);
		elementManager.setStreamer(streamer);
		elementManager.setApplicationConfig(applicationConfig);
		elementManager.setMethodManager(methodManager);
		elementManager.setTaskManager(taskManager);
		elementManager.setActionFocusManager(actionFocusManager);
		elementManager.setMontagePresetManager(montagePresetManager);
		elementManager.setBookFilterPresetManager(bookFilterPresetManager);
		elementManager.setSignalExportPresetManager(signalExportPresetManager);
		elementManager.setFftFilterPresetManager(fftFilterPresetManager);
		elementManager.setAmplifierDefinitionPresetManager(amplifierDefinitionPresetManager);
		elementManager.setOpenBCIModulePresetManager(openBCIModulePresetManager);
		elementManager.setTimeDomainSampleFilterPresetManager(timeDomainSampleFilterPresetManager);
		elementManager.setPredefinedTimeDomainFiltersPresetManager(predefinedTimeDomainSampleFilterPresetManager);
		elementManager.setStyledTagSetPresetManager(styledTagSetPresetManager);

		elementManager.setMp5ExecutorManager(mp5ExecutorManager);
		elementManager.setPreferences(preferences);
		elementManager.configureImportedElements();

		viewerMainFrame = new ViewerMainFrame();
		viewerMainFrame.setElementManager(elementManager);

		this.setViewerElementManager(elementManager);
		PluginAccessClass.getSharedInstance().setManager(elementManager);

		splash(null, true);

		viewerMainFrame.initialize();

	}

//	private void setupGUIExceptionHandler() {
//
//		final UncaughtExceptionHandler prevHandler = Thread.getDefaultUncaughtExceptionHandler();
//
//		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
//
//			@Override
//			public void uncaughtException(final Thread t, final Throwable e) {
//
//				logger.error("Exception caught", e);
//
//				Runnable job = new Runnable() {
//
//					@Override
//					public void run() {
//						try {
//						    SvarogLogger.getInstance().debug("Default Uncaught Exception Handler!");
//
//							// prevent the splash screen from staying on top
//							if (splashScreen != null && splashScreen.isVisible()) {
//								splashScreen.setVisible(false);
//								splashScreen.dispose();
//								splashScreen = null;
//							}
//
//							ErrorsDialog errorsDialog = new ErrorsDialog( null, true, "error.exception");
//							ResolvableException ex = new ResolvableException(e);
//							errorsDialog.showDialog(ex, true);
//						} catch (Throwable ex1) {
//							// fallback to previous handler to prevent exception loss
//							logger.error("Failed to display error dialog", ex1);
//							prevHandler.uncaughtException(t, e);
//						}
//					}
//				};
//
//				SwingUtilities.invokeLater(job);
//
//			}
//		});
//	}

	public void exit(int code) {

		logger.debug("Application stopping");

		try {
			signalMLCodecManager.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write codec manager configuration", ex);
		}

		signalMLCodecManager.cleanUp();

		try {
			mrudRegistry.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write mrud registry configuration", ex);
		}

		try {
			applicationConfig.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write application configuration", ex);
		}

		try {
			montagePresetManager.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write montage configuration", ex);
		}

		try {
			bookFilterPresetManager.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write book filter configuration", ex);
		}

		try {
			signalExportPresetManager.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write signal export configuration", ex);
		}

		try {
			fftFilterPresetManager.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write FFT sample filter configuration", ex);
		}

		try {
			amplifierDefinitionPresetManager.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write amplifier definition configuration", ex);
		}

		try {
			openBCIModulePresetManager.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write OpenBCI modules configuration", ex);
		}

		try {
			timeDomainSampleFilterPresetManager.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write time domain sample filter configuration", ex);
		}

		/*TODO: if predefined filters should be ever edited and saved
		  as presets, this lines should be uncommented.

		  try {
		  predefinedTimeDomainSampleFilterPresetManager.writeToPersistence(null);
		  } catch (Exception ex) {
		  logger.error("Failed to write predefined time domain sample filters configuration", ex);
		  }*/

		try {
			styledTagSetPresetManager.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write styled tag set configuration", ex);
		}

		try {
			mp5ExecutorManager.writeToPersistence(null);
		} catch (Exception ex) {
			logger.error("Failed to write MP5 executor manager configuration", ex);
		}

		ProcessManager.getInstance().killAll();

		Method[] methods = methodManager.getMethods();
		ApplicationMethodDescriptor descriptor;
		PresetManager presetManager;
		for (Method method : methods) {
			descriptor = methodManager.getMethodData(method);
			if (descriptor != null) {
				presetManager = descriptor.getPresetManager(methodManager, true);
				if (presetManager != null) {
					try {
						presetManager.writeToPersistence(null);
					} catch (Exception ex) {
						logger.error("Failed to write preset manager for method ["
							     + method.getName() + "]", ex);
					}
				}
			}
			if (method instanceof DisposableMethod) {
				try {
					((DisposableMethod) method).dispose();
				} catch (SignalMLException ex) {
					logger.error("Failed to dispose method [" + method.getName() + "]", ex);
				}
			}
		}

		logger.debug("Application stopped");

		System.exit(code);

	}

	// this is guaranteed not to be used in applet context
	public File getProfileDir() {
		return profileDir;
	}

	// this is guaranteed not to be used in applet context
	public SignalMLCodecManager getSignalMLCodecManager() {
		return signalMLCodecManager;
	}

	// this is guaranteed not to be used in applet context
	public String getStartupDir() {
		return startupDir;
	}

	/** {@link #elementManager} getter. */
	public ViewerElementManager getViewerElementManager() {
		return viewerElementManager;
	}

	/** {@link #elementManager} setter. */
	private void setViewerElementManager(ViewerElementManager m) {
		this.viewerElementManager = m;
	}

	/**
	 * Translates the message for the specified key using the current Svarog locale.
	 * This method is equivalent to:
	 * 
	 * <code>
	 * org.signalml.plugin.impl.SvarogAccessI18nImpl.getInstance()._(msgKey);
	 * </code>
	 * 
	 * @param msgKey English version of the message
	 * @return i18n version of the message (depending on the current Svarog locale),
	 *         or key if not found
	 */
	public static String _(String msgKey) {
		return org.signalml.plugin.impl.SvarogAccessI18nImpl.getInstance()._(msgKey);
	}

	/**
	 * Translates the message for the specified key using the current Svarog locale
	 * and renders it using actual values.
	 * 
	 * This method is equivalent to:
	 * 
	 * <code>
	 * org.signalml.plugin.impl.SvarogAccessI18nImpl.getInstance()._R(msgKey, parameters);
	 * </code>
	 * 
	 * @param msgKey English version of the message
	 * @param arguments actual values to place in the message
	 * @return i18n version of the message (depending on the current Svarog locale),
	 *         with values rendered in, or key if not found
	 */
	public static String _R(String msgKey, Object ... arguments) {
		return org.signalml.plugin.impl.SvarogAccessI18nImpl.getInstance()._R(msgKey, arguments);
	}
}

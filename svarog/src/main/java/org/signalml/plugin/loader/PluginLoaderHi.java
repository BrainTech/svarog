package org.signalml.plugin.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URLConnection;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.jfree.ui.FilesystemFilter;
import org.apache.log4j.Logger;
import org.signalml.app.view.workspace.ViewerElementManager;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.impl.PluginAccessClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * Class responsible for loading plug-ins (high level). Its main functions are:
 * <ul>
 * <li>to read (and write when the application is closing) the list of
 * directories in which the plug-ins are stored,</li>
 * <li>to read (and write when the application is closing) the
 * {@link PluginState states} (whether they should be active or not) of
 * plug-ins,</li>
 * <li>to read {@link PluginDescription descriptions} of plug-ins and check
 * if their {@link PluginDependency dependencies} are satisfied,</li>
 * <li>to load active plug-ins using {@link PluginLoaderLo class loader},</li>
 * <li>to create the {@link PluginDialog dialog} to manage plug-in options.
 * </li>
 * </ul>
 *
 * @author Marcin Szumski
 * @author Stanislaw Findeisen (Eisenbits)
 */
public class PluginLoaderHi {

	private final Logger logger = Logger.getLogger(PluginLoaderHi.class);

	/**
	 * the path to the XML file with {@link PluginState states} of
	 * plug-ins
	 */
	private final File pluginsStateFile;
	/**
	 * the path to the XML file with the list of directories where
	 * plug-ins are stored
	 */
	private final File pluginsDirectoriesFile;

	//constants for reading XML files
	private static final String XMLDirectoryNode = "directory";
	private static final String XMLDirectoriesNode = "directories";
	private static final String XMLStatesPluginsNode = "plugins";
	private static final String XMLStatesPluginNode = "plugin";
	private static final String XMLStatesPluginNameNode = "name";
	private static final String XMLStatesPluginActiveNode = "active";

	/**
	 * the shared (only) instance of this loader
	 */
	private static PluginLoaderHi sharedInstance = null;

	/**
	 * the list of directories in which plug-ins are stored
	 */
	private ArrayList<File> pluginDirs = new ArrayList<File>();

	/**
	 * the directory where default plug-ins are stored
	 */
	private ArrayList<File> globalPluginDirectories = new ArrayList<File>();

	/**
	 * list of descriptions of plug-ins.
	 * On this list are only plug-ins that has an XML description
	 * file in one of the {@link #pluginDirs directories}.
	 */
	private ArrayList<PluginDescription> descriptions = new ArrayList<PluginDescription>();

	/**
	 * HashMap that allows to access pluginDescription by the name
	 * of the plug-in
	 */
	private HashMap<String, PluginDescription> descriptionsByName = new HashMap<String, PluginDescription>();
	/**
	 * list of {@link PluginState states} of plug-ins.
	 * On this list are states stored in an {@link #pluginsStateFile XML file}
	 * with plug-in states.
	 */
	private ArrayList<PluginState> states = new ArrayList<PluginState>();

	/**
	 * HashMap that allows to access a {@link PluginState state} by the name
	 * of the plug-in
	 */
	private HashMap<String, PluginState> statesByName = new HashMap<String, PluginState>();

	/**
	 * List of plugin heads. This is created before the plugin loading process starts.
	 * Access to this field should be synchronized!
	 */
	private ArrayList<PluginHead> pluginHeads = new ArrayList<PluginHead>();

	/**
	 * Tells if the plugin loading process has already started.
	 */
	private boolean startedLoading = false;

	/**
	 * Creates the shared instance.
	 * @param profileDir the profile directory
	 */
	public static void createInstance(File profileDir) {
		if (sharedInstance == null) {
			synchronized (PluginLoaderHi.class) {
				if (sharedInstance == null)
					sharedInstance = new PluginLoaderHi(profileDir);
			}
		}
	}

	/**
	 * Returns the shared instance of this loader.
	 * @return the shared instance of this loader or null (if it is not initialized yet).
	 */
	public static PluginLoaderHi getInstance() {
		// This method is called from SvarogSecurityManager in privileged mode!
		// NEVER give control to any plugin or untrusted code from here!
		return sharedInstance;
	}

	/**
	 * Reads the {@link PluginDescription description} of the plug-in
	 * from an XML file.
	 * @param fileName the path to the XML file
	 * @return created description or null if there was an error while
	 * reading or parsing a file
	 */
	private PluginDescription readXml(String fileName) {
		try {
			return new PluginDescription(fileName);
		} catch (Exception e) {
			logger.warn("failed do read description of a plug-in from file "+fileName);
			logger.error("", e);
			return null;
		}

	}

	/**
	 * Constructor. Sets the provided profile directory.
	 * @param profileDir the profile directory of the program.
	 */
	private PluginLoaderHi(File profileDir)
	{
		super();

		this.pluginsStateFile = new File(profileDir + File.separator + "pluginsState.xml");
		this.pluginsDirectoriesFile = new File(profileDir + File.separator + "plugin-locations.xml");
		try {
			if (!readPluginDirectories())
				setDefaultPluginDir(profileDir);
			setGlobalPluginDir();
			readPluginsState(this.pluginsStateFile);
		} catch (Exception e) {
			final String errorMsg = "Failed to create loader of plug-ins";
			logger.error(errorMsg, e);
		}
	}

	final static FilenameFilter xml_file_filter =
		new FilesystemFilter("xml", "Xml File", false);

	/**
	 * Scans the given directory to find plug-ins.
	 * @param directory the directory to scan
	 */
	private void scanPluginDirectory(File directory) {
		logger.debug("scanning over dir '" + directory + "'");
		String[] filenames = directory.list(xml_file_filter);
		for (String filename: filenames) {
			logger.debug("looking at '" + filename + "'");
			final PluginDescription descr = readXml(directory
													+ File.separator + filename);
			if (descr == null || !descr.fillURL(directory)) {
				logger.warn("Skipping faulty plugin description: '" + descr + "'");
				continue;
			}

			final String pluginName = descr.getName();
			if (descriptionsByName.containsKey(pluginName)) {
				PluginDescription first = descriptionsByName.get(pluginName);
				logger.warn("Duplicate plugin: '" + first + "' and '" + descr + "'."
							+ "Skipping the latter.");
				continue;
			}

			descriptions.add(descr);
			descriptionsByName.put(pluginName, descr);

			final PluginState state = statesByName.get(descr.getName());
			if (state != null && descr.isActive()) {
				descr.setActive(state.isActive());
			}
		}
	}

	/**
	 * Adds plug-in directories for all default plug-ins, namely
	 * the directories {@code ../plugins/}*{@code /target}
	 * @param svarogDir the svarog base directory
	 */
	private void startFromSourcesAddPluginDirs(File svarogDir) {
		File pluginsDir = new File(svarogDir + File.separator + ".." + File.separator + "plugins");
		logger.info("trying to load plugins from '" + pluginsDir + "'");
		if (pluginsDir.exists() && pluginsDir.canRead() && pluginsDir.isDirectory()) {
			String[] pluginSrcDirsNames = pluginsDir.list();
			for (String dirName : pluginSrcDirsNames) {
				File dir = new File(pluginsDir, dirName);
				if (dir.isDirectory()) {
					File pluginDir = new File(dir + File.separator + "target");
					if (pluginDir.exists() && pluginDir.isDirectory() && pluginDir.canRead()) {
						globalPluginDirectories.add(pluginDir);
					}
				}
			}
		}
	}


	/**
	 * Sets the directory where the default plug-ins are stored.
	 * The location of the file depends on the fact if Svarog was started:
	 * <ul>
	 * <li>from sources,</li>
	 * <li>from jar file.</li>
	 * </ul>
	 */
	private void setGlobalPluginDir() {
		//hack to get the location of the jar file and add the global plugin directory
		URL srcURL = getClass().getProtectionDomain().getCodeSource().getLocation();
		logger.debug("svarog is loaded from '" + srcURL + "'");
		if (srcURL.toString().endsWith("/target/classes/")) {
			File svarogDirFile = _urlToFile(srcURL);
			svarogDirFile = svarogDirFile.getParentFile().getParentFile();
			startFromSourcesAddPluginDirs(svarogDirFile);
		} else {
			final URLConnection connection;
			try {
				connection = srcURL.openConnection();
			} catch (IOException ex) {
				logger.error("failed to open connection to jar", ex);
				return;
			}

			final File jarFile;
			if (connection instanceof JarURLConnection) {
				URL jarURL = ((JarURLConnection) connection).getJarFileURL();
				jarFile = _urlToFile(jarURL);
			} else {
				// e.g. file:/usr/share/java/svarog-1.1.2.jar
				jarFile = new File(srcURL.getPath());
			}

			File pluginsDir = new File(jarFile.getParentFile() + File.separator +
						   "svarog" + File.separator + "plugins");
			if (pluginsDir.exists() && pluginsDir.isDirectory() && pluginsDir.canRead()) {
				logger.info("trying to load plugins from '" + pluginsDir + "'");
				globalPluginDirectories.add(pluginsDir);
				return;
			}

			pluginsDir = new File(jarFile.getParentFile() + File.separator + "plugins");
			if (pluginsDir.exists() && pluginsDir.isDirectory() && pluginsDir.canRead()) {
				logger.info("trying to load plugins from '" + pluginsDir + "'");
				globalPluginDirectories.add(pluginsDir);
				return;
			}

			logger.warn("plugin dir not found");
		}
	}

	private File _urlToFile(URL url) {
		try {
			return new File(url.toURI());
		} catch (java.net.URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Adds the default plug-in directory based on given profile directory.
	 * Also:
	 * <ul>
	 * <li>adds plug-in directories for all default plug-ins if Svarog is
	 * started from sources,</li>
	 * <li>adds the global plug-in directory if Svarog is started from jar
	 * created during the installation,</li>
	 * </ul>
	 * @param profileDir profile directory where default plug-in folder
	 * is located
	 */
	private void setDefaultPluginDir(File profileDir) {
		File pluginDir = new File(profileDir + File.separator + "plugins");
		if (!pluginDir.exists())
			pluginDir.mkdir();
		if (pluginDir.exists() && pluginDir.isDirectory() && pluginDir.canRead()) {
			this.pluginDirs.add(pluginDir);
		}
	}

	/**
	 * Adds a "plugin options" button to the tools menu.
	 * To do it prepares a collection of plug-in states and uses it to
	 * create a dialog window which will be activated after clicking this button.
	 */
	private void addPluginOptions() {
		ViewerElementManager manager = PluginAccessClass.getManager();
		ArrayList<PluginState> existingPluginStates = new ArrayList<PluginState>();
		for (PluginDescription descr : descriptions) {
			PluginState pluginState = statesByName.get(descr.getName());
			if (pluginState == null) {
				pluginState = new PluginState(descr.getName(), descr.isActive());
				states.add(pluginState);
				statesByName.put(pluginState.getName(), pluginState);
			}
			existingPluginStates.add(pluginState);
			pluginState.setMissingDependencies(descr.findMissingDependencies(descriptions));
			pluginState.setVersion(descr.getVersion());
			pluginState.setFailedToLoad(descr.isFailedToLoad());
		}
		PluginDialog pluginDialog = new PluginDialog(manager.getDialogParent(), true, existingPluginStates,
				pluginDirs);
		PluginAction action = new PluginAction(existingPluginStates);
		action.setPluginDialog(pluginDialog);
		manager.getToolsMenu().add(action);
	}

	/**
	 * Scans the all directories in {@link #pluginDirs} to find plug-ins.
	 */
	private void scanPluginDirectories() {
		for (File plDir : pluginDirs) {
			if (plDir.exists() && plDir.canRead() && plDir.isDirectory())
				scanPluginDirectory(plDir);
		}
		for (File plDir : globalPluginDirectories) {
			if (plDir.exists() && plDir.canRead() && plDir.isDirectory())
				scanPluginDirectory(plDir);
		}

		boolean repeat = true;
		while (repeat) {
			repeat = false;
			for (PluginDescription descr : descriptions) {
				if (descr.isActive() && !descr.dependenciesSatisfied(descriptions))
					repeat = true;
			}
		}
	}

	/**
	 * Creates a new ClassLoader and loads plug-ins using it.
	 * Invokes the {@link Plugin#register(org.signalml.plugin.export.SvarogAccess)}
	 * function of every plug-in to register the plug-in.
	 * Adds a {@code addPluginOptions()} button to tools menu.
	 */
	public void loadPlugins()
	{
		synchronized (this) {
			this.startedLoading = true;
		}

		scanPluginDirectories();
		ClassLoader prevCL = Thread.currentThread().getContextClassLoader();
		sortActivePlugins();
		createPluginHeads();

		for (PluginHead head : pluginHeads) {
			PluginDescription descr = head.getDescription();
			if (! head.hasLoader()) {
				head.setLoader(new PluginLoaderLo(head, prevCL));
			}
			if (! loadPlugin(head))
				setDependentInactive(descr);
		}

		addPluginOptions();
		PluginAccessClass.setInitializationPhaseEnd();
	}

	/**
	 * Try to load a plugin.
	 * @returns true iff success
	 */
	protected boolean loadPlugin(PluginHead head) {
		final PluginDescription descr = head.getDescription();
		final PluginLoaderLo loader = head.getLoader();
		final Plugin plugin;
		try {
			logger.debug("Loading plugin " + descr.getName() +
						 " (class " + descr.getStartingClass() + ")");
			plugin = (Plugin)(loader.loadClass(descr.getStartingClass())).newInstance();
		} catch (Exception exc) {
			String errorMsg = "Failed to load plugin " + descr.getName() +
							  " from " + descr.getJarFileURL();
			logger.error(errorMsg, exc);

			descr.setActive(false);
			descr.setFailedToLoad(true);

			return false;
		}

		head.setPluginObj(plugin);

		try {
			plugin.register(new PluginAccessClass(head));
		} catch (Throwable exc) {
			String errorMsg = "Failed to initialize plugin " + descr.getName() +
							  " from " + descr.getJarFileURL();
			logger.error(errorMsg, exc);
		}

		return true;
	}

	/**
	 * Sets all plug-ins that are dependent from given to be
	 * inactive.
	 * @param description the description of the plugin
	 */
	private void setDependentInactive(PluginDescription description) {
		for (PluginDescription descr : descriptions) {
			if (descr.dependentFrom(description)) descr.setActive(false);
		}
	}

	/**
	 * Reads the remembered state of the plug-in from given XML node.
	 * @param node the node to read state from
	 */
	private void readPluginState(Node node) {
		NodeList nodeList = node.getChildNodes();
		String name = "";
		boolean active = false;
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node nodeTmp = nodeList.item(i);
			if (nodeTmp.getNodeName().equals(XMLStatesPluginNameNode))
				name = nodeTmp.getFirstChild().getNodeValue();
			else if (nodeTmp.getNodeName().equals(XMLStatesPluginActiveNode))
				active = Boolean.parseBoolean(nodeTmp.getFirstChild().getNodeValue());
		}
		PluginState state = new PluginState(name, active);
		states.add(state);
		statesByName.put(state.getName(), state);
	}

	/**
	 * Opens an XML file and returns the document element.
	 * @param file the file in which XML tree is stored
	 * @return the document element
	 * @throws ParserConfigurationException if an error occurs while
	 * creating a document builder
	 * @throws SAXException if parsing XML failed
	 * @throws IOException if I/O error occurs
	 */
	private Element openXMLDocument(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(file);
		Element element = document.getDocumentElement();
		element.normalize();
		return element;

	}

	/**
	 * Reads the remembered state of all plug-ins from given
	 * XML file.
	 * @param fileName the path to the file
	 */
	private void readPluginsState(File fileName) {
		try {
			if (fileName.exists() && fileName.canRead()) {
				Element element = openXMLDocument(fileName);
				NodeList nodeList = element.getChildNodes();
				for (int i = 0; i < nodeList.getLength(); ++i) {
					Node node = nodeList.item(i);
					if (node.getNodeName().equals(XMLStatesPluginNode))
						readPluginState(node);
				}
			} else {
				logger.debug("File with states of plugins doesn't exist. Default states are used");
			}
		} catch (Exception e) {
			logger.error("Failed to load states of plug-ins from file. All plug-ins with unloaded states will be set inacitve.");
			logger.error("", e);
		}

	}

	/**
	 * Performs operations necessary while closing the program.
	 * Writes the desired state of plug-ins to an XML file.
	 */
	public void onClose() {
		rememberPluginsState();
		savePluginDirectories();
		PluginAccessClass.onClose();
	}

	/**
	 * Creates a document used to save data in XML form
	 * @return created document
	 * @throws ParserConfigurationException if a DocumentBuilder cannot be created
	 */
	private Document createXMLDocumentToSave() throws ParserConfigurationException {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		return doc;
	}

	/**
	 * Writes the provided data to XML file of a given name.
	 * @param path the path to the file
	 * @param data the XML document to be saved
	 * @throws FileNotFoundException If the given path does not denote
	 * an existing, writable regular file and a new regular file
	 * of that name cannot be created
	 * @throws TransformerException if transformation from
	 * DOMSource to StreamResult is not possible
	 */
	private void saveToXMLFile(File path, Document data) throws FileNotFoundException, TransformerException {
		PrintStream ps = new PrintStream(path);
		StreamResult result = new StreamResult(ps);
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(data);
		trans.transform(source, result);
		ps.close();
	}

	/**
	 * Writes the desired state of plug-ins to XML file.
	 * @return true if operation successful, false otherwise
	 */
	private boolean rememberPluginsState() {
		try {
			Document doc = createXMLDocumentToSave();
			Element root = doc.createElement(XMLStatesPluginsNode);
			doc.appendChild(root);
			for (PluginState state : states) {
				Element pluginNode = doc.createElement(XMLStatesPluginNode);
				root.appendChild(pluginNode);
				Element nameNode = doc.createElement(XMLStatesPluginNameNode);
				nameNode.appendChild(doc.createTextNode(state.getName()));
				pluginNode.appendChild(nameNode);
				Element activeNode = doc.createElement(XMLStatesPluginActiveNode);
				activeNode.appendChild(doc.createTextNode(state.isActive() ? "true" : "false"));
				pluginNode.appendChild(activeNode);
			}
			saveToXMLFile(this.pluginsStateFile, doc);
			return true;
		} catch (Exception e) {
			logger.error("failed to save states of plug-ins");
			logger.error("", e);
			return false;
		}
	}

	/**
	 * Sorts plug-ins (actually their descriptions) by their dependencies
	 * (it is partial order).
	 * Plug-in {@code A} dependent from plug-in {@code B} will be always after
	 * plug-in {@code B}.
	 */
	private void sortActivePlugins() {
		ArrayList<PluginDescription> toSort = new ArrayList<PluginDescription>(descriptions);
		ArrayList<PluginDescription> sorted = new ArrayList<PluginDescription>();
		while (!toSort.isEmpty()) {
			for (PluginDescription descr : toSort) {
				if (descr.notDependentFrom(toSort)) {
					sorted.add(descr);
					toSort.remove(descr);
					break;
				}
			}
		}
		descriptions = sorted;
	}

	/**
	 * Populates {@link #pluginHeads} from {@link #descriptions}.
	 * Here we assume {@link #descriptions} are sorted!
	 */
	private void createPluginHeads() {
		ArrayList<PluginHead> hl = new ArrayList<PluginHead>();

		for (PluginDescription desc : this.descriptions) {
			if (desc.dependenciesSatisfied(descriptions) && desc.isActive()) {
				PluginHead head = new PluginHead(desc);
				List<PluginDependency> depList = desc.getDependencies();

				for (PluginDependency dep : depList) {
					String depName = dep.getName();
					PluginDescription depDesc = descriptionsByName.get(depName);
					if (null != depDesc)
						head.addDependency(depDesc.getHead());
				}
				desc.setHead(head);
				hl.add(head);
			}
		}

		synchronized (this) {
			this.pluginHeads = hl;
		}
	}

	/**
	 * Reads the names of directories in which plug-ins are stored
	 * from the XML configuration file.
	 * @return true if operation successful, false otherwise
	 */
	private boolean readPluginDirectories() {
		try {
			if (this.pluginsDirectoriesFile.exists()) {
				Element element = openXMLDocument(this.pluginsDirectoriesFile);
				NodeList nodeList = element.getChildNodes();
				for (int i = 0; i < nodeList.getLength(); ++i) {
					Node node = nodeList.item(i);
					if (node.getNodeName().equals(XMLDirectoryNode)) {
						File directoryToAdd = new File(node.getFirstChild().getNodeValue());
						pluginDirs.add(directoryToAdd);
					}
				}
				return true;
			}
		} catch (Exception e) {
			logger.error("failed to read plug-in directories from file");
			logger.error("", e);
		}
		return false;
	}

	/**
	 * Writes the names of directories in which plug-ins are stored
	 * to the XML configuration file.
	 */
	private void savePluginDirectories() {
		try {
			Document doc = createXMLDocumentToSave();
			Element root = doc.createElement(XMLDirectoriesNode);
			doc.appendChild(root);
			for (File directory : pluginDirs) {
				Element directoryNode = doc.createElement(XMLDirectoryNode);
				root.appendChild(directoryNode);
				directoryNode.appendChild(doc.createTextNode(directory.getPath()));
			}
			saveToXMLFile(this.pluginsDirectoriesFile, doc);
		} catch (Exception e) {
			logger.error("failed to save current plug-in directories");
			logger.error("", e);
		}
	}

	/**
	 * @return the pluginDirs
	 */
	public ArrayList<File> getPluginDirs() {
		ArrayList<File> tmpPluginDirs = new ArrayList<File>(pluginDirs);
		tmpPluginDirs.addAll(globalPluginDirectories);
		return tmpPluginDirs;
	}

	/**
	 * Returns true iff the plugin loading process has already started.
	 * @return {@link #startedLoading}
	 */
	public synchronized boolean hasStartedLoading() {
		return startedLoading;
	}

	public boolean hasLoaded(String className) {
		if (! hasStartedLoading())
			return false;

		ArrayList<PluginHead> heads = new ArrayList<PluginHead>();
		synchronized (this) {
			heads.addAll(this.pluginHeads);
		}
		for (PluginHead h : heads) {
			if (h.containsClass(className))
				return true;
		}
		return false;
	}
}

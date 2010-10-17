package org.signalml.plugin.loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;

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
import org.signalml.app.view.ViewerElementManager;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.impl.PluginAccessClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * Class responsible for loading plug-ins. Its main functions are
 * <ul>
 * <li>to read (and write when the application is closing) the list of
 * directories in which the plug-ins are stored,</li>
 * <li>to read (and write when the application is closing) the
 * {@link PluginState states} (whether they should be active or not) of
 * plug-ins,</li>
 * <li>to read {@link PluginDescription descriptions} of plug-ins and check
 * if their {@link PluginDependency dependencies} are satisfied,</li>
 * <li>to load active plug-ins using {@link URLClassLoader class loader},</li>
 * <li>to create the {@link PluginDialog dialog} to manage plug-in options.
 * </li>
 * </ul> 
 * @author Marcin Szumski
 */
public class PluginLoader {
	
	private static final Logger logger = Logger.getLogger(PluginLoader.class);
	
	/**
	 * the class loader created and used to load plug-ins
	 */
	public static URLClassLoader myLoader;
	
	/**
	 * the name of the XML file with {@link PluginState states} of
	 * plug-ins
	 */
	private static String pluginsStateFileName = "pluginsState.xml";
	/**
	 * the name of the XML file with the list of directories where
	 * plug-ins are stored
	 */
	private static String pluginsDirectoriesFileName = "plugin-locations.xml";
	
	//constants for reading XML files
	private static String XMLDirectoryNode = "directory";
	private static String XMLDirectoriesNode = "directories";
	private static String XMLStatesPluginsNode = "plugins";
	private static String XMLStatesPluginNode = "plugin";
	private static String XMLStatesPluginNameNode = "name";
	private static String XMLStatesPluginActiveNode = "active";
	
	/**
	 * the shared (only) instance of this loader
	 */
	private static PluginLoader sharedInstance = null;
	
	/**
	 * the list of directories in which plug-ins are stored
	 */
	private ArrayList<File> pluginDirs = new ArrayList<File>();
	
	/**
	 * the path to the profile directory
	 */
	private String profileDirPath;
	
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
	 * On this list are states stored in an {@link #pluginsStateFileName XML file}
	 * with plug-in states.
	 */
	private ArrayList<PluginState> states = new ArrayList<PluginState>();
	
	/**
	 * HashMap that allows to access a {@link PluginState state} by the name
	 * of the plug-in
	 */
	private HashMap<String, PluginState> statesByName = new HashMap<String, PluginState>();
	
	/**
	 * a list of starting classes of plug-ins
	 */
	private ArrayList<Plugin> plugins = new ArrayList<Plugin>();
	
	/**
	 * Creates the shared instance.
	 * @param profileDir the profile directory
	 */
	public static void createInstance(File profileDir){
		if (sharedInstance == null)
			sharedInstance = new PluginLoader(profileDir);
	}
	
	/**
	 * Returns the shared instance of this loader.
	 * @return the shared instance of this loader
	 */
	public static PluginLoader getInstance(){
		return sharedInstance;
	}
	
	/**
	 * Reads the {@link PluginDescription description} of the plug-in
	 * from an XML file.
	 * @param fileName the path to the XML file
	 * @return created description or null if there was an error while
	 * reading or parsing a file
	 */
	private PluginDescription readXml(String fileName){
		try {
			return new PluginDescription(fileName);
		} catch (Exception e) {
			logger.error("failed do read description of a plug-in from file "+fileName);
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Constructor. Sets the provided profile directory.
	 * @param profileDir the profile directory of the program.
	 */
	protected PluginLoader(File profileDir)
	{
		try{
			profileDirPath = profileDir.getAbsolutePath();
			if (!readPluginDirectories())
				setDefaultPluginDir();
			readPluginsState(profileDirPath.concat("\\").concat(pluginsStateFileName));
		} catch (Exception e) {
			logger.error("Failed to create loader of plug-ins");
			e.printStackTrace();
		}
	}
	
	/**
	 * Scans the given directory to find plug-ins.
	 * Returns an array of URLs to jar files with these
	 * plug-ins.
	 * @param directory the directory to scan
	 * @return an array of URLs to jar files with plug-ins
	 */
	private ArrayList<URL> scanPluginDirectory(File directory) {
		FilenameFilter filter = new FilesystemFilter("xml", "Xml File", false);
		String[] filenames = directory.list(filter);
		for (int i = 0; i < filenames.length; ++i){
			PluginDescription descr = readXml(directory.getAbsolutePath().concat("\\").concat(filenames[i]));
			if (descr != null){
				descriptions.add(descr);
				descriptionsByName.put(descr.getName(), descr);
			}
				
		}
		ArrayList<URL> urls = new ArrayList<URL>();
		for (PluginDescription descr : descriptions){
			PluginState state = statesByName.get(descr.getName());
			if (state!= null && descr.isActive()){
				descr.setActive(state.isActive());
			}
			String name = null;
			if (descr.isActive()){
				try {
					name = directory.toURI().toString();
					name = name.concat(descr.getJarFile());
					urls.add(new URL(name));
				} catch (MalformedURLException e) {
					logger.error("failed to create URL for file "+name);
					e.printStackTrace();
				}
			}
		}
		return urls;
	}
	
	/**
	 * Adds the default plug-in directory based on
	 * given profile directory.
	 * @param profileDir profile directory where default
	 * plug-in folder is located
	 */
	private void setDefaultPluginDir(){
		File pluginDirTmp = (new File(profileDirPath + "\\plugins")).getAbsoluteFile();
		if (!pluginDirTmp.exists())
			pluginDirTmp.mkdir();
		if (pluginDirTmp.exists() && pluginDirTmp.isDirectory() && pluginDirTmp.canRead()){
			pluginDirs.add(pluginDirTmp);
		}
			
	}
	
	/**
	 * Adds a "plugin options" button to the tools menu.
	 * To do it prepares a collection of plug-in states and uses it to
	 * create a dialog window which will be activated after clicking this button.
	 */
	private void addPluginOptions(){
		/*
		 * wyświetlać na czerwono pluginy niekatywne, w hincie wyświetlać czego brakuje
		 */
		ViewerElementManager manager = PluginAccessClass.getSharedInstance().getManager();
		ArrayList<PluginState> existingPluginStates = new ArrayList<PluginState>();
		for (PluginDescription descr : descriptions){
			PluginState pluginState = statesByName.get(descr.getName());
			if (pluginState == null){
				pluginState = new PluginState(descr.getName(), descr.isActive());
				states.add(pluginState);
				statesByName.put(pluginState.getName(), pluginState);
			}
			existingPluginStates.add(pluginState);
			pluginState.setMissingDependencies(descr.findMissingDependencies(descriptions));
			pluginState.setVersion(descr.getVersion());
			pluginState.setFailedToLoad(descr.isFailedToLoad());
		}
		PluginDialog pluginDialog = new PluginDialog(manager.getMessageSource(), manager.getDialogParent(), true, existingPluginStates,
				pluginDirs);
		PluginAction action = new PluginAction(existingPluginStates);
		action.setPluginDialog(pluginDialog);
		manager.getToolsMenu().add(action);
	}
	
	/**
	 * Scans the all directories in {@link #pluginDirs} to find plug-ins.
	 * Returns an array of URLs to jar files with these
	 * plug-ins.
	 * @return an array of URLs to jar files
	 */
	private URL[] scanPluginDirectories(){
		ArrayList<URL> urls = new ArrayList<URL>();
		for (File plDir : pluginDirs){
			urls.addAll(scanPluginDirectory(plDir));
		}
		
		boolean repeat = true;
		while (repeat){
			repeat = false;
			for (PluginDescription descr : descriptions){
				if (descr.isActive() && !descr.dependenciesSatisfied(descriptions))
					repeat = true;
			}
		}
		return urls.toArray(new URL[urls.size()]);
	}
	
	/**
	 * Creates a new ClassLoader and loads plug-ins using it.
	 * Invokes the {@link Plugin#register(org.signalml.plugin.export.SvarogAccess)}
	 * function of every plug-in to register the plug-in.
	 * Adds a {@code addPluginOptions()} button to tools menu.
	 */
	public void loadPlugins()
	{
		try{
			URL[] urls = scanPluginDirectories();
			if (urls != null && urls.length > 0){
				ClassLoader prevCl = Thread.currentThread().getContextClassLoader();
				myLoader = URLClassLoader.newInstance(urls, prevCl);
				sortActivePlugins();
				for (PluginDescription descr : descriptions){
					if (descr.dependenciesSatisfied(descriptions) && descr.isActive()){
						Plugin pl;
						try {
							pl = (Plugin) (myLoader.loadClass(descr.getStartingClass())).newInstance();
							pl.register(PluginAccessClass.getSharedInstance());
							descr.setPlugin(pl);
							plugins.add(pl);
						} catch (Exception e) {
							descr.setActive(false);
							descr.setFailedToLoad(true);
							setDependentInactive(descr);
							e.printStackTrace();
						}
					}
				}
			}
			addPluginOptions();
			PluginAccessClass.getSharedInstance().setInitializationPhase(false);
		} catch (Exception e) {
			logger.error("Unknown error while loading plug-ins");
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets all plug-ins that are dependent from given to be
	 * inactive.
	 * @param description the description of the plugin
	 */
	private void setDependentInactive(PluginDescription description){
		for (PluginDescription descr : descriptions){
			if (descr.dependentFrom(description)) descr.setActive(false);
		}
	}
	
	/**
	 * Reads the remembered state of the plug-in from given XML node.
	 * @param node the node to read state from
	 */
	private void readPluginState(Node node){
		NodeList nodeList = node.getChildNodes();
		String name = "";
		boolean active = false;
		for (int i = 0; i < nodeList.getLength(); ++i){
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
	private Element openXMLDocument(File file) throws ParserConfigurationException, SAXException, IOException{
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
	private void readPluginsState(String fileName){
		try {
			Element element = openXMLDocument(new File(fileName));
			NodeList nodeList = element.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); ++i){
				Node node = nodeList.item(i);
				if (node.getNodeName().equals(XMLStatesPluginNode))
					readPluginState(node);
			}
		} catch (Exception e) {
			logger.error("failed to load states of plug-ins, all plug-ins with unloaded states will be set acitve");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Performs operations necessary while closing the program.
	 * Writes the desired state of plug-ins to an XML file.
	 */
	public void onClose(){
		try {
			rememberPluginsState();
			savePluginDirectories();
			PluginAccessClass.getSharedInstance().onClose();
		} catch (Exception e) {
			logger.error("Error in plug-in interface while closing the application");
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a document used to save data in XML form
	 * @return created document
	 * @throws ParserConfigurationException if a DocumentBuilder cannot be created
	 */
	private Document createXMLDocumentToSave() throws ParserConfigurationException{
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
	private void saveToXMLFile(String path, Document data) throws FileNotFoundException, TransformerException{
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
	private boolean rememberPluginsState(){
		try {
	        Document doc = createXMLDocumentToSave();
	        Element root = doc.createElement(XMLStatesPluginsNode);
	        doc.appendChild(root);
	        for (PluginState state : states){
	        	Element pluginNode = doc.createElement(XMLStatesPluginNode);
	        	root.appendChild(pluginNode);
	        	Element nameNode = doc.createElement(XMLStatesPluginNameNode);
	        	nameNode.appendChild(doc.createTextNode(state.getName()));
	        	pluginNode.appendChild(nameNode);
	        	Element activeNode = doc.createElement(XMLStatesPluginActiveNode);
	        	activeNode.appendChild(doc.createTextNode(state.isActive() ? "true" : "false"));
	        	pluginNode.appendChild(activeNode);
	        }
	        saveToXMLFile(profileDirPath.concat("\\").concat(pluginsStateFileName), doc);
	        return true;
		} catch (Exception e) {
			logger.error("failed to save states of plug-ins");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Sorts plug-ins (actually their descriptions) by their dependencies
	 * (it is partial order).
	 * Plug-in {@code A} dependent from plug-in {@code B} will be always after 
	 * plug-in {@code B}.
	 */
	private void sortActivePlugins(){
		ArrayList<PluginDescription> toSort = new ArrayList<PluginDescription>(descriptions);
		ArrayList<PluginDescription> sorted = new ArrayList<PluginDescription>();
		while (!toSort.isEmpty()){
			for (PluginDescription descr : toSort){
				if (descr.notDependentFrom(toSort)){
					sorted.add(descr);
					toSort.remove(descr);
					break;
				}
			}
		}
		descriptions = sorted;
	}

	/**
	 * Reads the names of directories in which plug-ins are stored
	 * from the XML configuration file.
	 * @return true if operation successful, false otherwise
	 */
	private boolean readPluginDirectories(){
		try {
			File file;
			file = new File(profileDirPath.concat("\\").concat(pluginsDirectoriesFileName));
			if (file.exists()){
				Element element;
				element = openXMLDocument(file);
				NodeList nodeList = element.getChildNodes();
				for (int i = 0; i < nodeList.getLength(); ++i){
					Node node = nodeList.item(i);
					if (node.getNodeName().equals(XMLDirectoryNode)){
						File directoryToAdd = new File(node.getFirstChild().getNodeValue());
						pluginDirs.add(directoryToAdd);
					}
				}
			} else {
				setDefaultPluginDir();
			}
			return true;
		} catch (Exception e) {
			logger.error("failed to read plug-in directories from file");
			e.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * Writes the names of directories in which plug-ins are stored
	 * to the XML configuration file.
	 */
	private void savePluginDirectories(){
		try {
			Document doc = createXMLDocumentToSave();
			Element root = doc.createElement(XMLDirectoriesNode);
			doc.appendChild(root);
			for (File directory : pluginDirs){
				Element directoryNode = doc.createElement(XMLDirectoryNode);
	        	root.appendChild(directoryNode);
	        	directoryNode.appendChild(doc.createTextNode(directory.getPath()));
			}
			saveToXMLFile(profileDirPath.concat("\\").concat(pluginsDirectoriesFileName), doc);
		} catch (Exception e) {
			logger.error("failed to save current plug-in directories");
			e.printStackTrace();
		}
	}

	/**
	 * @return the pluginDirs
	 */
	public ArrayList<File> getPluginDirs() {
		return pluginDirs;
	}
}

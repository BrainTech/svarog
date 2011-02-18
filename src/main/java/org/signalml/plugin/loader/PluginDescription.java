/**
 * 
 */
package org.signalml.plugin.loader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.signalml.plugin.export.Plugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class describes the plug-in. Extends the {@link PluginState state} of
 * the plug-in.
 * Apart from parameters of the state, contains:
 * <ul>
 * <li>the string with the full name of the class,
 * that will be loaded to register the plug-in,</li>
 * <li>the name of the jar file with the plug-in,</li>
 * <li>the package that is exported by the plug-in,</li>
 * <li>the list of {@link PluginDependency dependencies} of the plug-in.</li>
 * </ul>
 * The parameters of this description are read from an XML file.
 * This description allows to:
 * <ul>
 * <li>check if its dependencies are satisfied,</li>
 * <li>find missing dependencies.</li>
 * </ul>
 * @author Marcin Szumski
 */
public class PluginDescription extends PluginState{
	
	/**
	 * the string with the full name of the class,
	 * that will be loaded to register the plug-in
	 */
	private String startingClass;
	/**
	 * the name of the jar file with the plug-in
	 */
	private String jarFile;
	/**
	 * the name of the package that is exported by the plug-in
	 */
	private String exportPackage;
	/**
	 * the loaded {@link #startingClass starting class} 
	 */
	private Plugin plugin = null;
	/**
	 * the list of {@link PluginDependency dependencies}
	 * of the plug-in
	 */
	private ArrayList<PluginDependency> dependencies = new ArrayList<PluginDependency>();
	
	/**
	 * Constructor. Parses the XML file of a given path, which
	 * contains the description of the plug-in.
	 * @param fileName the path to an XML file with the
	 * description of the plug-in 
	 * @throws ParserConfigurationException if a DocumentBuilder
     *   cannot be created
	 * @throws SAXException if the creation of document builder fails
	 * @throws IOException if an error while parsing the file occurs
	 */
	public PluginDescription(String fileName) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(new File(fileName));
		Element element = document.getDocumentElement();
		element.normalize();
	//	if (!element.hasChildNodes()) throw
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); ++i){
			Node node = nodeList.item(i);
			if (node.getNodeName().equals("name"))
				name = node.getFirstChild().getNodeValue().trim();
			else if (node.getNodeName().equals("jar-file"))
				jarFile = node.getFirstChild().getNodeValue().trim();
			else if (node.getNodeName().equals("version"))
				setVersion(node.getFirstChild().getNodeValue().trim());
			else if (node.getNodeName().equals("starting-class"))
				startingClass = node.getFirstChild().getNodeValue().trim();
			else if (node.getNodeName().equals("export-package"))
				exportPackage =node.getFirstChild().getNodeValue().trim();
			else if (node.getNodeName().equals("dependencies"))
				parseDependencies(node);
		}
	}
	
	/**
	 * Parses the subtree with dependencies starting from the given node.
	 * @param node XML node containing the dependencies of the plug-in
	 */
	private void parseDependencies(Node node){
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); ++i){
			Node dependencyNode = nodeList.item(i);
			if (dependencyNode.getNodeName().equals("dependency")){
				NodeList dependencyNodeList = dependencyNode.getChildNodes();
				String name = null;
				String minimumVersion = null;
				for (int j = 0; j < dependencyNodeList.getLength(); ++j){
					Node nodeTmp = dependencyNodeList.item(j);
					if (nodeTmp.getNodeName().equals("name"))
						name = nodeTmp.getFirstChild().getNodeValue().trim();
					else if (nodeTmp.getNodeName().equals("version"))
						minimumVersion = nodeTmp.getFirstChild().getNodeValue().trim();
				}
				if (name != null && minimumVersion != null){
					PluginDependency dependency = new PluginDependency(name, minimumVersion);
					dependencies.add(dependency);
				}
			}
		}
	}


	/**
	 * @return the full name of the starting class (class loaded to register this plug-in)
	 */
	public String getStartingClass() {
		return startingClass;
	}
	

	/**
	 * @return the name of the jar file with this plug-in
	 */
	public String getJarFile() {
		return jarFile;
	}
	
	/**
	 * @return the name of the package exported by this plug-in
	 */
	public String getExportPackage() {
		return exportPackage;
	}

	/**
	 * @param plugin the loaded plug-in
	 */
	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * @return the loaded plug-in
	 */
	public Plugin getPlugin() {
		return plugin;
	}
	
	/**
	 * Tells if all dependencies of the described plug-in are
	 * satisfied by any of the plug-ins on the list
	 * @param descriptions the list of all descriptions of plug-ins
	 * @return true if all dependencies satisfied, false otherwise
	 */
	public boolean dependenciesSatisfied(ArrayList<PluginDescription> descriptions){
		for (PluginDependency dep : dependencies){
			if (!dep.satisfied(descriptions))
			{
				setActive(false);
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Creates an arrayList containing the dependencies that are not
	 * satisfied. 
	 * @param descriptions the list of all descriptions of plug-ins
	 * @return the created array
	 */
	public ArrayList<PluginDependency> findMissingDependencies(ArrayList<PluginDescription> descriptions){
		ArrayList<PluginDependency> missingDependencies = new ArrayList<PluginDependency>();
		for (PluginDependency dep: dependencies){
			if (!dep.satisfied(descriptions)){
				missingDependencies.add(dep);
			}
		}
		return missingDependencies;
	}
	
	@Override
	public String toString(){
		return name.concat(" v").concat(versionToString());
	}
	
	/**
	 * Returns true if this plug-in is not dependent from any plug-in from the
	 * list.
	 * @param descriptions the list of plug-ins
	 * @return true if this plug-in is not dependent from any plug-in from the
	 * list, false otherwise 
	 */
	public boolean notDependentFrom(ArrayList<PluginDescription> descriptions){
		for (PluginDescription descr : descriptions){
			if (dependentFrom(descr)) return false;
		}
		return true;
	}
	
	/**
	 * Returns true if this plug-in is dependent from given plug-in.
	 * @param description the description of the plug-in
	 * @return true if this plug-in is dependent from given plug-in,
	 * false otherwise
	 */
	public boolean dependentFrom(PluginDescription description){
		for (PluginDependency dependency : dependencies){
			if (dependency.getName().equals(description.getName())) return true;
		}
		return false;
	}

}

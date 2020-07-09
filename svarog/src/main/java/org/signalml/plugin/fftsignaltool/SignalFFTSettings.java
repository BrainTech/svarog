/* SignalFFTSettings.java created 2007-12-17
 *
 */

package org.signalml.plugin.fftsignaltool;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
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
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.math.fft.WindowType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Settings how the power spectrum of the signal should be displayed.
 * For more information read them description of the parameters below.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o., Marcin Szumski
 */

public class SignalFFTSettings implements FFTWindowTypeSettings, Serializable {

	protected static final Logger logger = Logger.getLogger(SignalFFTSettings.class);

	/**
	 * the serialization constant
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * boolean which tells if the channel for which the power spectrum is
	 * calculated should be changed, when the mouse goes up or down
	 */
	private boolean channelSwitching;
	/**
	 * the size of the {@link SignalFFTPlot FFT plot}
	 */
	private Dimension plotSize;
	/**
	 * the size of the FFT window (number of samples)
	 */
	private int windowWidth;
	/**
	 * the {@link WindowType type} of the window function
	 */
	private WindowType windowType;
	/**
	 * the parameter of the window function
	 */
	private double windowParameter;
	/**
	 * boolean which tells if the axis with logarithmic scale should be used
	 * ({@code true}) or the normal scale should be used
	 */
	private boolean logarithmic;
	/**
	 * boolean which tells if the chart should be antialiased
	 */
	private boolean antialias;
	/**
	 * boolean which tells if the points should be connected using splines
	 * ({@code true}) or lines ({@code false})
	 */
	private boolean spline;
	/**
	 *  boolean which tells if the title of the plot should be displayed
	 */
	private boolean titleVisible;
	/**
	 * boolean which tells if the labels with frequencies should be displayed
	 */
	private boolean frequencyAxisLabelsVisible;
	/**
	 *  boolean which tells if the labels with power values should be displayed
	 */
	private boolean powerAxisLabelsVisible;

	/**
	 * the start of the frequencies range that is displayed
	 */
	private int visibleRangeStart = Integer.MIN_VALUE;
	/**
	 * the end of the frequencies range that is displayed
	 */
	private int visibleRangeEnd = Integer.MAX_VALUE;
	/**
	 * the maximum number of labels on the X (frequencies) axis
	 */
	private int xAxisLabelCount = Integer.MAX_VALUE;

	private boolean autoScaleYAxis = true;

	private double minPowerAxis = 0.0;

	private double maxPowerAxis = 200.00;

	public boolean isAutoScaleYAxis() {
		return autoScaleYAxis;
	}

	public void setAutoScaleYAxis(boolean autoScaleYAxis) {
		this.autoScaleYAxis = autoScaleYAxis;
	}

	public double getMinPowerAxis() {
		return minPowerAxis;
	}

	public void setMinPowerAxis(double minPowerAxis) {
		this.minPowerAxis = minPowerAxis;
	}

	public double getMaxPowerAxis() {
		return maxPowerAxis;
	}

	public void setMaxPowerAxis(double maxPowerAxis) {
		this.maxPowerAxis = maxPowerAxis;
	}

	/**
	 * Constructor. Sets the default values of the parameters.
	 */
	public SignalFFTSettings() {
		channelSwitching = false;
		plotSize = new Dimension(600, 200);
		windowWidth = 256;
		windowType = WindowType.HAMMING;
		windowParameter = 0;
		logarithmic = false;
		spline = false;
		antialias = true;

		frequencyAxisLabelsVisible = true;
		xAxisLabelCount = 16;
	}

	/**
	 * Returns the size of the {@link SignalFFTPlot FFT plot}.
	 * @return the size of the {@link SignalFFTPlot FFT plot}
	 */
	public Dimension getPlotSize() {
		return plotSize;
	}

	/**
	 * Sets the size of the {@link SignalFFTPlot FFT plot}
	 * @param size the size of the {@link SignalFFTPlot FFT plot}
	 */
	public void setPlotSize(Dimension size) {
		if (size == null) {
			throw new NullPointerException(_("No size"));
		}
		plotSize = size;
	}

	/**
	 * Returns if the channel for which the power spectrum is
	 * calculated should be changed, when the mouse goes up or down .
	 * @return if the channel for which the power spectrum is
	 * calculated should be changed, when the mouse goes up or down
	 */
	public boolean isChannelSwitching() {
		return channelSwitching;
	}

	/**
	 * Sets if the channel for which the power spectrum is
	 * calculated should be changed, when the mouse goes up or down
	 * @param channelSwitching {@code true} if the channel for which the power
	 * spectrum is calculated should be changed, when the mouse goes up or down
	 */
	public void setChannelSwitching(boolean channelSwitching) {
		this.channelSwitching = channelSwitching;
	}

	/**
	 * Returns the size of the FFT window (number of samples).
	 * @return the size of the FFT window (number of samples)
	 */
	public int getWindowWidth() {
		return windowWidth;
	}

	/**
	 * Sets the size of the FFT window (number of samples).
	 * @param width the size of the FFT window (number of samples)
	 */
	public void setWindowWidth(int width) {
		this.windowWidth = width;
	}

	@Override
	public WindowType getWindowType() {
		return windowType;
	}

	@Override
	public void setWindowType(WindowType windowType) {
		if (windowType == null) {
			throw new NullPointerException(_("No window type"));
		}
		this.windowType = windowType;
	}

	@Override
	public double getWindowParameter() {
		return windowParameter;
	}

	@Override
	public void setWindowParameter(double windowParameter) {
		this.windowParameter = windowParameter;
	}

	/**
	 * Returns if the axis with logarithmic scale should be used
	 * ({@code true}) or the normal scale should be used.
	 * @return if the axis with logarithmic scale should be used
	 */
	public boolean isLogarithmic() {
		return logarithmic;
	}

	/**
	 * Sets if the axis with logarithmic scale should be used
	 * ({@code true}) or the normal scale should be used.
	 * @param logarithmic ({@code true}) if the axis with logarithmic scale
	 * should be used or {@code false} if the normal scale should be used
	 */
	public void setLogarithmic(boolean logarithmic) {
		this.logarithmic = logarithmic;
	}

	/**
	 * Returns if the points should be connected using splines
	 * ({@code true}) or lines ({@code false}).
	 * @return if the points should be connected using splines
	 * ({@code true}) or lines ({@code false})
	 */
	public boolean isSpline() {
		return spline;
	}

	/**
	 * Sets if the points should be connected using splines
	 * ({@code true}) or lines ({@code false}).
	 * @param spline if the points should be connected using splines
	 * ({@code true}) or lines ({@code false})
	 */
	public void setSpline(boolean spline) {
		this.spline = spline;
	}

	/**
	 * Returns if the chart should be antialiased
	 * @return {@code true} if the chart should be antialiased,
	 * {@code false} otherwise
	 */
	public boolean isAntialias() {
		return antialias;
	}


	/**
	 * Sets if the chart should be antialiased.
	 * @param antialias {@code true} if the chart should be antialiased,
	 * {@code false} otherwise
	 */
	public void setAntialias(boolean antialias) {
		this.antialias = antialias;
	}

	/**
	 * Returns if the title of the plot should be displayed.
	 * @return {@code true} if the title of the plot should be displayed,
	 * {@code false} otherwise
	 */
	public boolean isTitleVisible() {
		return titleVisible;
	}

	/**
	 * Sets if the title of the plot should be displayed.
	 * @param titleVisible {@code true} if the title of the plot should be
	 * displayed, {@code false} otherwise
	 */
	public void setTitleVisible(boolean titleVisible) {
		this.titleVisible = titleVisible;
	}

	/**
	 * Returns if the labels with frequencies should be displayed.
	 * @return {@code true} if the labels with frequencies should be displayed,
	 * {@code false} otherwise
	 */
	public boolean isFrequencyAxisLabelsVisible() {
		return frequencyAxisLabelsVisible;
	}

	/**
	 * Sets if the labels with frequencies should be displayed.
	 * @param frequencyAxisLabelsVisible {@code true} if the labels with
	 * frequencies should be displayed, {@code false} otherwise
	 */
	public void setFrequencyAxisLabelsVisible(boolean frequencyAxisLabelsVisible) {
		this.frequencyAxisLabelsVisible = frequencyAxisLabelsVisible;
	}

	/**
	 * Returns if the labels with power values should be displayed.
	 * @return {@code true} if the labels with power values should be
	 * displayed, {@code false} otherwise
	 */
	public boolean isPowerAxisLabelsVisible() {
		return powerAxisLabelsVisible;
	}

	/**
	 * Sets if the labels with power values should be displayed.
	 * @param powerAxisLabelsVisible {@code true} if the labels with power
	 * values should be displayed, {@code false} otherwise
	 */
	public void setPowerAxisLabelsVisible(boolean powerAxisLabelsVisible) {
		this.powerAxisLabelsVisible = powerAxisLabelsVisible;
	}

	/**
	 * Returns the start of the frequencies range that is displayed.
	 * @return the start of the frequencies range that is displayed
	 */
	public int getVisibleRangeStart() {
		return visibleRangeStart;
	}

	/**
	 * Sets the start of the frequencies range that is displayed.
	 * @param visibleRangeStart the start of the frequencies range that is
	 * displayed
	 */
	public void setVisibleRangeStart(int visibleRangeStart) {
		this.visibleRangeStart = visibleRangeStart;
	}

	/**
	 * Returns the end of the frequencies range that is displayed.
	 * @return the end of the frequencies range that is displayed
	 */
	public int getVisibleRangeEnd() {
		return visibleRangeEnd;
	}

	/**
	 * Sets the end of the frequencies range that is displayed.
	 * @param visibleRangeEnd the end of the frequencies range that is displayed
	 */
	public void setVisibleRangeEnd(int visibleRangeEnd) {
		this.visibleRangeEnd = visibleRangeEnd;
	}

	/**
	 * Returns the maximum number of labels on the X (frequencies) axis.
	 * @return the maximum number of labels on the X (frequencies) axis
	 */
	public int getXAxisLabelCount() {
		return xAxisLabelCount;
	}

	/**
	 * Sets the maximum number of labels on the X (frequencies) axis.
	 * @param maxLabelCount the maximum number of labels on the X (frequencies)
	 * axis
	 */
	public void setXAxisLabelCount(int maxLabelCount) {
		this.xAxisLabelCount = maxLabelCount;
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
	 * Adds an XML node with the boolean value as the child of the {@code root}
	 * node in the document {@code doc} and sets its {@code value}.
	 * @param doc the document in which the nodes are located
	 * @param root the node to which the child will be added
	 * @param name the name of the child node
	 * @param value the value of the child node
	 */
	private void addBooleanNode(Document doc, Element root, String name, boolean value) {
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(Boolean.toString(value)));
		root.appendChild(node);
	}

	/**
	 * Adds an XML node with the integer value as the child of the {@code root}
	 * node in the document {@code doc} and sets its {@code value}.
	 * @param doc the document in which the nodes are located
	 * @param root the node to which the child will be added
	 * @param name the name of the child node
	 * @param value the value of the child node
	 */
	private void addIntNode(Document doc, Element root, String name, int value) {
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(Integer.toString(value)));
		root.appendChild(node);
	}

	/**
	 * Adds an XML node with the double value as the child of the {@code root}
	 * node in the document {@code doc} and sets its {@code value}.
	 * @param doc the document in which the nodes are located
	 * @param root the node to which the child will be added
	 * @param name the name of the child node
	 * @param value the value of the child node
	 */
	private void addDoubleNode(Document doc, Element root, String name, Double value) {
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value.toString()));
		root.appendChild(node);
	}

	/**
	 * Adds an XML node with the Dimension value as the child of the {@code root}
	 * node in the document {@code doc} and sets its {@code value}.
	 * @param doc the document in which the nodes are located
	 * @param root the node to which the child will be added
	 * @param name the name of the child node
	 * @param value the value of the child node
	 */
	private void addDimensionNode(Document doc, Element root, String name, Dimension value) {
		Element node = doc.createElement(name);
		addIntNode(doc, node, "height", value.height);
		addIntNode(doc, node, "width", value.width);
		root.appendChild(node);
	}

	/**
	 * Adds an XML node with the {@link WindowType} value as the child of the
	 * {@code root} node in the document {@code doc} and sets its {@code value}.
	 * @param doc the document in which the nodes are located
	 * @param root the node to which the child will be added
	 * @param name the name of the child node
	 * @param value the value of the child node
	 */
	private void addWindowTypeNode(Document doc, Element root, String name, WindowType value) {
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value.name()));
		root.appendChild(node);
	}

	/**
	 * Reads the boolean value from the given XML node.
	 * @param node the node
	 * @return the read value
	 */
	private boolean readBooleanNode(Node node) {
		return Boolean.parseBoolean(node.getFirstChild().getNodeValue());
	}

	/**
	 * Reads the integer value from the given XML node.
	 * @param node the node
	 * @return the read value
	 */
	private int readIntNode(Node node) {
		return Integer.parseInt(node.getFirstChild().getNodeValue());
	}

	/**
	 * Reads the double value from the given XML node.
	 * @param node the node
	 * @return the read value
	 */
	private double readDoubleNode(Node node) {
		return Double.parseDouble(node.getFirstChild().getNodeValue());
	}

	/**
	 * Reads the Dimension value from the given XML node.
	 * @param node the node
	 * @return the read value
	 */
	private Dimension readDimensionNode(Node node) {
		NodeList nodeList = node.getChildNodes();
		int width=0, height=0;
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node nodeTmp = nodeList.item(i);
			if (nodeTmp.getNodeName().equals("width"))
				width = readIntNode(nodeTmp);
			if (nodeTmp.getNodeName().equals("height"))
				height = readIntNode(nodeTmp);
		}
		return new Dimension(width, height);
	}

	/**
	 * Reads the {@link WindowType} value from the given XML node.
	 * @param node the node
	 * @return the read value
	 */
	private WindowType readWindowTypeNode(Node node) {
		return WindowType.valueOf(node.getFirstChild().getNodeValue());
	}

	/**
	 * Updates the fields of this object with the data read from the given
	 * XML file.
	 * @param xmlFile the XML file
	 */
	public void readFromXMLFile(File xmlFile) {
		if (!xmlFile.exists()) return;
		Element element;
		try {
			element = openXMLDocument(xmlFile);
			NodeList nodeList = element.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node nodeTmp = nodeList.item(i);
				if (nodeTmp.getNodeName().equals("antialias"))
					setAntialias(readBooleanNode(nodeTmp));
				if (nodeTmp.getNodeName().equals("channelSwitching"))
					setChannelSwitching(readBooleanNode(nodeTmp));
				if (nodeTmp.getNodeName().equals("frequencyAxisLabelsVisible"))
					setFrequencyAxisLabelsVisible(readBooleanNode(nodeTmp));
				if (nodeTmp.getNodeName().equals("logarithmic"))
					setLogarithmic(readBooleanNode(nodeTmp));
				if (nodeTmp.getNodeName().equals("powerAxisLabelsVisible"))
					setPowerAxisLabelsVisible(readBooleanNode(nodeTmp));
				if (nodeTmp.getNodeName().equals("spline"))
					setSpline(readBooleanNode(nodeTmp));
				if (nodeTmp.getNodeName().equals("titleVisible"))
					setTitleVisible(readBooleanNode(nodeTmp));
				if (nodeTmp.getNodeName().equals("maxLabelCount"))
					setXAxisLabelCount(readIntNode(nodeTmp));
				if (nodeTmp.getNodeName().equals("visibleRangeEnd"))
					setVisibleRangeEnd(readIntNode(nodeTmp));
				if (nodeTmp.getNodeName().equals("visibleRangeStart"))
					setVisibleRangeStart(readIntNode(nodeTmp));
				if (nodeTmp.getNodeName().equals("windowWidth"))
					setWindowWidth(readIntNode(nodeTmp));
				if (nodeTmp.getNodeName().equals("windowParameter"))
					setWindowParameter(readDoubleNode(nodeTmp));
				if (nodeTmp.getNodeName().equals("plotSize"))
					setPlotSize(readDimensionNode(nodeTmp));
				if (nodeTmp.getNodeName().equals("windowType"))
					setWindowType(readWindowTypeNode(nodeTmp));

				if (nodeTmp.getNodeName().equals("minYAxis"))
					setMinPowerAxis(readDoubleNode(nodeTmp));
				if (nodeTmp.getNodeName().equals("maxYAxis"))
					setMaxPowerAxis(readDoubleNode(nodeTmp));
				if (nodeTmp.getNodeName().equals("autoScaleY"))
					setAutoScaleYAxis(readBooleanNode(nodeTmp));

			}
		} catch (ParserConfigurationException e) {
			logger.error("", e);
		} catch (SAXException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
		}


	}

	/**
	 * Stores this object in the given XML file.
	 * @param xmlFile the XML file
	 */
	public void storeInXMLFile(File xmlFile) {
		try {
			Document doc = createXMLDocumentToSave();
			Element root = doc.createElement("signalFFTSettings");
			doc.appendChild(root);
			addBooleanNode(doc, root, "antialias", isAntialias());
			addBooleanNode(doc, root, "channelSwitching", isChannelSwitching());
			addBooleanNode(doc, root, "frequencyAxisLabelsVisible", isFrequencyAxisLabelsVisible());
			addBooleanNode(doc, root, "logarithmic", isLogarithmic());
			addBooleanNode(doc, root, "powerAxisLabelsVisible", isPowerAxisLabelsVisible());
			addBooleanNode(doc, root, "spline", isSpline());
			addBooleanNode(doc, root, "titleVisible", isTitleVisible());
			addBooleanNode(doc, root, "autoScaleY", isAutoScaleYAxis());
			addIntNode(doc, root, "maxLabelCount", getXAxisLabelCount());
			addIntNode(doc, root, "visibleRangeEnd", getVisibleRangeEnd());
			addIntNode(doc, root, "visibleRangeStart", getVisibleRangeStart());
			addIntNode(doc, root, "windowWidth", getWindowWidth());
			addDoubleNode(doc, root, "windowParameter", getWindowParameter());
			addDoubleNode(doc, root, "minYAxis", getMinPowerAxis());
			addDoubleNode(doc, root, "maxYAxis", getMaxPowerAxis());
			addDimensionNode(doc, root, "plotSize", getPlotSize());
			addWindowTypeNode(doc, root, "windowType", getWindowType());
			saveToXMLFile(xmlFile, doc);
		} catch (FileNotFoundException e) {
			logger.error("", e);
		} catch (TransformerException e) {
			logger.error("", e);
		} catch (ParserConfigurationException e) {
			logger.error("", e);
		}
	}

}

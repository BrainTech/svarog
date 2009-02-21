/* RawSignalDocumentBuilder.java created 2008-01-18
 * 
 */

package org.signalml.domain.signal.raw;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.signalml.exception.SanityCheckException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/** RawSignalDocumentBuilder
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RawSignalDocumentBuilder {

	protected static final Logger logger = Logger.getLogger(RawSignalDocumentBuilder.class);
	
	public static final String NAMESPACE_URI = "http://signalml.org/rawsignal";
	
	public static final String EXPORT_DATE = "rs:exportDate";
	public static final String MARKER_OFFSET = "rs:markerOffset";
	public static final String LABEL = "rs:label";
	public static final String CHANNEL_LABELS = "rs:channelLabels";
	public static final String BLOCKS_PER_PAGE = "rs:blocksPerPage";
	public static final String PAGE_SIZE = "rs:pageSize";
	public static final String BYTE_ORDER = "rs:byteOrder";
	public static final String SAMPLE_TYPE = "rs:sampleType";
	public static final String CALIBRATION = "rs:calibration";
	public static final String SAMPLE_COUNT = "rs:sampleCount";
	public static final String CHANNEL_COUNT = "rs:channelCount";
	public static final String SAMPLING_FREQUENCY = "rs:samplingFrequency";
	public static final String RAW_SIGNAL_INFO = "rs:rawSignalInfo";
	public static final String SOURCE_UID = "rs:sourceUID";
	public static final String FORMAT = "rs:format";
	public static final String SIGNALML_SIGNAL_INFO = "rs:signalmlSignalInfo";
	public static final String SOURCE_FILE_FORMAT = "rs:sourceFileFormat";
	public static final String SOURCE_FILE_NAME = "rs:sourceFileName";
	public static final String EXPORT_FILE_NAME = "rs:exportFileName";
	public static final String RAW_SIGNAL = "rs:rawSignal";
	
	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
	static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	
	private static RawSignalDocumentBuilder instance = null;	
	
	private DocumentBuilderFactory documentBuiderFactory;
	private DocumentBuilder documentBuilder;

	protected RawSignalDocumentBuilder() {

		Resource schemaResource = new ClassPathResource("org/signalml/schema/rawsignal.xsd");
		
		documentBuiderFactory = DocumentBuilderFactory.newInstance();

		documentBuiderFactory.setNamespaceAware(true);
		documentBuiderFactory.setValidating(true);
		try {
			documentBuiderFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
		} catch(IllegalArgumentException ex) {
			throw new SanityCheckException( "Failed to configure factory", ex );
		}

		try {
			documentBuiderFactory.setAttribute(JAXP_SCHEMA_SOURCE, schemaResource.getInputStream() );
		} catch (IllegalArgumentException ex) {
			throw new SanityCheckException( "Failed to configure factory", ex );
		} catch (IOException ex) {
			throw new SanityCheckException( "Failed to configure factory", ex );
		}
		
		try {
		
			documentBuilder = documentBuiderFactory.newDocumentBuilder();
			
			documentBuilder.setErrorHandler( new ErrorHandler() {
		
				@Override
				public void error(SAXParseException exception) throws SAXException {
					throw new SAXException(exception);
				}
		
				@Override
				public void fatalError(SAXParseException exception) throws SAXException {
					throw new SAXException(exception);					
				}
		
				@Override
				public void warning(SAXParseException exception) throws SAXException {
					logger.warn( "Warning on parsing XML: [" + exception.getMessage() + "] on line [" + exception.getLineNumber() );
				}
				
			});
				
		} catch (ParserConfigurationException ex) {
			throw new SanityCheckException( "Failed to create builder", ex );
		}		

	}
	
	public static RawSignalDocumentBuilder getInstance() {
		if( instance == null ) {
			instance = new RawSignalDocumentBuilder();
		}
		return instance;
	}
	
	public Document newDocument() {
		return documentBuilder.newDocument();
	}

	public Document parse(File f) throws SAXException, IOException {
		return documentBuilder.parse(f);
	}
				
}

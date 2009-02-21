/* XPathTest.java created 2008-01-29
 * 
 */

package org.signalml.test;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.signalml.app.util.SingleNameSpaceContext;
import org.signalml.domain.signal.raw.RawSignalDocumentBuilder;
import org.signalml.exception.SanityCheckException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** XPathTest
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class XPathTest {

	static final String JAXP_SCHEMA_LANGUAGE =
	    "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	static final String W3C_XML_SCHEMA =
	    "http://www.w3.org/2001/XMLSchema";
	
	static final String JAXP_SCHEMA_SOURCE =
	    "http://java.sun.com/xml/jaxp/properties/schemaSource";
	
	public static void main(String[] args) throws Exception {

		Resource schemaResource = new ClassPathResource("org/signalml/schema/rawsignal.xsd");
		
		DocumentBuilderFactory documentBuiderFactory = DocumentBuilderFactory.newInstance();
		
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
		
		DocumentBuilder documentBuilder = documentBuiderFactory.newDocumentBuilder();
		
		Document document = documentBuilder.parse( new File( "d:/temp/test.xml" ) );
				
		XPathFactory pathFactory = XPathFactory.newInstance();
		XPath path = pathFactory.newXPath();

		path.setNamespaceContext( new SingleNameSpaceContext("ns", RawSignalDocumentBuilder.NAMESPACE_URI) );
		
		Element rawSignalEl = document.getDocumentElement();
				
		Object evaluate = path.evaluate( "ns:exportFileName", rawSignalEl );		
		System.out.println( evaluate );
						
	}

}

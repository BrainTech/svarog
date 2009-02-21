/* MP5RemoteConnector.java created 2008-02-17
 * 
 */

package org.signalml.method.mp5.remote;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.castor.CastorMarshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

/** MP5RemoteConnector
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MP5RemoteConnector {

	protected static final Logger logger = Logger.getLogger(MP5RemoteConnector.class);
	
	private CastorMarshaller marshaller;	
	private SaajSoapMessageFactory messageFactory;	
	private HttpUrlConnectionMessageSender messageSender;	
	
	private WebServiceTemplate serviceTemplate;
	
	private static MP5RemoteConnector sharedInstance = null;
	
	protected MP5RemoteConnector() throws Exception {
		
		marshaller = new CastorMarshaller();
		Resource mapping = new ClassPathResource("org/signalml/method/mp5/remote/castor_mapping.xml");
		marshaller.setMappingLocation(mapping);
		marshaller.setValidating(true);
		marshaller.afterPropertiesSet();
		
		messageFactory = new SaajSoapMessageFactory();
		messageFactory.afterPropertiesSet();
		
		messageSender = new HttpUrlConnectionMessageSender();
				
		serviceTemplate = new WebServiceTemplate();
		serviceTemplate.setCheckConnectionForFault(true);
		serviceTemplate.setMarshaller(marshaller);
		serviceTemplate.setUnmarshaller(marshaller);
		serviceTemplate.setMessageFactory(messageFactory);
		serviceTemplate.setMessageSender(messageSender);
		serviceTemplate.afterPropertiesSet();
		
	}
		
	public CastorMarshaller getMarshaller() {
		return marshaller;
	}

	public static MP5RemoteConnector getSharedInstance() throws Exception {
		if( sharedInstance == null ) {
			sharedInstance = new MP5RemoteConnector();
		}
		return sharedInstance;
	}
	
	public TestConnectionResponse testConnection( String uri, TestConnectionRequest request ) {
		
		return (TestConnectionResponse) serviceTemplate.marshalSendAndReceive(uri, request);
		
	}

	public DecompositionResponse decomposition( String uri, DecompositionRequest request ) {
		
		return (DecompositionResponse) serviceTemplate.marshalSendAndReceive(uri, request);
	}
	
	public DecompositionProgressResponse decompositionProgress( String uri, DecompositionProgressRequest request ) {

		return (DecompositionProgressResponse) serviceTemplate.marshalSendAndReceive(uri, request);
				
	}

	public DecompositionAbortResponse decompositionAbort( String uri, DecompositionAbortRequest request ) {

		return (DecompositionAbortResponse) serviceTemplate.marshalSendAndReceive(uri, request);
				
	}
	
}

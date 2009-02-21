/* MP5RemoteExecutor.java created 2008-02-14
 * 
 */

package org.signalml.method.mp5;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Formatter;
import java.util.Iterator;
import java.util.UUID;
import java.util.zip.DataFormatException;

import javax.xml.transform.Source;

import org.apache.log4j.Logger;
import org.signalml.app.model.SignalExportDescriptor;
import org.signalml.domain.signal.MultichannelSegmentedSampleSource;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.domain.signal.raw.RawSignalWriter;
import org.signalml.method.ComputationException;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.method.mp5.remote.Credentials;
import org.signalml.method.mp5.remote.DecompositionRequest;
import org.signalml.method.mp5.remote.DecompositionResponse;
import org.signalml.method.mp5.remote.MP5RemoteConnector;
import org.signalml.method.mp5.remote.ResolvableFault;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.client.SoapFaultClientException;

/** MP5RemoteExecutor
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class MP5RemoteExecutor implements MP5Executor {

	protected static final Logger logger = Logger.getLogger(MP5RemoteExecutor.class);
	
	private static final String[] CODES = new String[] { "mp5Method.executor.remote" };
	
	protected String uid;
	
	protected String name;
	
	protected String url;
	protected String userName;
	
	private transient MP5ConfigCreator configCreator = new MP5ConfigCreator();
	private transient RawSignalWriter rawSignalWriter = new RawSignalWriter();	
	
	public MP5RemoteExecutor() {
		uid = UUID.randomUUID().toString();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getUID() {
		return uid;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public abstract Credentials createCredentials();

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(MP5Data data, int segment, File resultFile, MethodExecutionTracker tracker) throws ComputationException {
		
		MP5Parameters parameters = data.getParameters();
		MultichannelSegmentedSampleSource sampleSource = data.getSampleSource();

		MP5RuntimeParameters runtimeParameters = new MP5RuntimeParameters();
		
		runtimeParameters.setChannelCount( sampleSource.getChannelCount() );
		runtimeParameters.setSegementSize( sampleSource.getSegmentLength() );
		runtimeParameters.setChosenChannels(null);
		runtimeParameters.setDataFormat( MP5SignalFormatType.FLOAT );
		runtimeParameters.setFooterSize(0);
		runtimeParameters.setHeaderSize(0);
		runtimeParameters.setOutputDirectory(null);
		runtimeParameters.setPointsPerMicrovolt(1F);
		runtimeParameters.setSamplingFrequency(sampleSource.getSamplingFrequency());
		runtimeParameters.setSignalFile(new File("signal.bin"));
		runtimeParameters.setWritingMode(MP5WritingModeType.CREATE);
		runtimeParameters.setResultFileExtension( null );
		
		SignalExportDescriptor signalExportDescriptor = new SignalExportDescriptor();
		signalExportDescriptor.setSampleType( RawSignalSampleType.FLOAT );
		signalExportDescriptor.setByteOrder( RawSignalByteOrder.LITTLE_ENDIAN );
		signalExportDescriptor.setNormalize(false);
		
		Formatter configFormatter = configCreator.createConfigFormatter();
		
		String rawConfig = parameters.getRawConfigText();
		if( rawConfig == null ) {
			configCreator.writeRuntimeInvariantConfig(parameters, configFormatter);
		} else {
			configCreator.writeRawConfig(rawConfig, configFormatter);
		}
		
		configCreator.writeRuntimeConfig(runtimeParameters, configFormatter);		
		String config = configFormatter.toString();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			rawSignalWriter.writeSignal(baos, sampleSource, signalExportDescriptor, segment, null);
		} catch (IOException ex) {
			logger.error("Failed to create data buffer", ex);
			throw new ComputationException(ex);
		}
		
		DecompositionRequest request = new DecompositionRequest();
		logger.info( "New request uid [" + uid + "]" );
				
		request.setCredentials(createCredentials());
		request.setConfig(config);
		request.setBinarySignal(baos.toByteArray());
		
		MP5RemoteConnector connector = null;
		try {
			connector = MP5RemoteConnector.getSharedInstance();
		} catch (Exception ex) {
			logger.error("Failed to create remote connector", ex);
			throw new ComputationException(ex);
		}
		
		MP5RemoteController controller = new MP5RemoteController(url, request.getUid(), tracker, connector);
		Thread controllerThread = new Thread(controller);
		
		DecompositionResponse response = null;
		try { 
			controllerThread.start();
			response = connector.decomposition(url, request);
		} catch( SoapFaultClientException ex ) {
			SoapFaultDetail faultDetail = ex.getSoapFault().getFaultDetail();
			if( faultDetail != null ) {
				Iterator detailEntries = faultDetail.getDetailEntries();
				Source source = null;
				if( detailEntries.hasNext() ) {
					source = ((SoapFaultDetailElement) detailEntries.next()).getSource();
				} else {
					throw new ComputationException(ex);
				}
				Object unmarshaled;
				try {
					unmarshaled = connector.getMarshaller().unmarshal(source);
				} catch (XmlMappingException ex1) {
					throw new ComputationException(ex1);
				} catch (IOException ex1) {
					throw new ComputationException(ex1);
				}
				if( unmarshaled instanceof ResolvableFault ) {
					ResolvableFault fault = (ResolvableFault) unmarshaled;
					throw new ComputationException(fault.getMessageCode(), fault.getMessageArgumentsArray());
				} else {
					throw new ComputationException(ex);
				}
			} else {
				throw new ComputationException(ex);
			}
		} catch( Exception ex ) {
			logger.error("Exception in remote decomposition uid [" + uid + "]", ex);
			throw new ComputationException(ex);
		} finally {
			controller.shutdown();
		}
		
		logger.info( "Got response for uid [" + uid + "]" );
		
		if( response.getBook().isEmpty() ) {
			logger.info( "Response for uid [" + uid + "] is empty" );
			return false;
		}
		
		byte[] bookData = null;
		try {		
			bookData = response.getBinaryBook();
		} catch( DataFormatException ex ) {
			logger.error("Failed to decompress book", ex);
			throw new ComputationException(ex);
		}
		
		OutputStream bookOutputStream = null;
		try {
			bookOutputStream = new BufferedOutputStream( new FileOutputStream( resultFile ) );		
			bookOutputStream.write(bookData);
		} catch (IOException ex) {
			logger.error("Failed to write book file", ex);
			throw new ComputationException(ex);
		} finally {
			if( bookOutputStream != null ) {
				try {
					bookOutputStream.close();
				} catch( IOException ex ) {
					// ignore
				}
			}
		}
				
		return true;
		
	}

	@Override
	public Object[] getArguments() {
		return new Object[] { name };
	}

	@Override
	public String[] getCodes() {
		return CODES;
	}

	@Override
	public String getDefaultMessage() {
		return "MP5RemoteExecutor [" + name + "]";
	}

	@Override
	public String toString() {
		return getDefaultMessage();
	}


}

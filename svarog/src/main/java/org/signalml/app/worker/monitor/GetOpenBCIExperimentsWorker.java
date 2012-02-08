package org.signalml.app.worker.monitor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import org.signalml.ObciTester;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.document.opensignal.Amplifier;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentStatus;
import org.signalml.app.model.document.opensignal.SignalParameters;
import org.signalml.app.view.document.opensignal.elements.AmplifierChannel;
import org.signalml.app.worker.monitor.zeromq.ExperimentDescriptorJSonReader;
import org.signalml.app.worker.monitor.zeromq.Message;
import org.signalml.app.worker.monitor.zeromq.MessageType;

public class GetOpenBCIExperimentsWorker extends SwingWorker<List<ExperimentDescriptor>, Void>{

	private ApplicationConfiguration applicationConfiguration;

	public GetOpenBCIExperimentsWorker(ApplicationConfiguration applicationConfiguration) {
		this.applicationConfiguration = applicationConfiguration;
	}

	@Override
	protected List<ExperimentDescriptor> doInBackground() throws Exception {

		//TODO - tu powinno być sprawdzenie, czy openBCI daemon jest odpalony
		//jeśli nie - to odpalić
	
		/*ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket socket = context.socket(ZMQ.REQ);

		socket.connect(getAddressString()); 
		socket.send(getListExperimentsRequest(), 0);
		
		byte[] responseBytes = socket.recv(0);
		String response = new String(responseBytes);

		System.out.println("GOT RESPONSE: " + response);
		
		List<ExperimentDescriptor> result = parseListExperimentsResponse(response);
		*/
		
		String response = ObciTester.getListExperimentsResponse();
		ExperimentDescriptorJSonReader reader = new ExperimentDescriptorJSonReader();
		List<ExperimentDescriptor> result = reader.parseExperiments(response);

		return result;
	}
	
	private String getAddressString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("tcp://");
		stringBuffer.append(applicationConfiguration.getOpenBCIDaemonAddress());
		stringBuffer.append(":");
		stringBuffer.append(applicationConfiguration.getOpenBCIDaemonPort());
		
		System.out.println("address: " + stringBuffer.toString());
		
		return stringBuffer.toString();
	}
	
	private byte[] getListExperimentsRequest() {
		
		Message message = new Message(MessageType.LIST_EXPERIMENTS);
		String json = message.toJSON();
		
		System.out.println("Sending request: " + json);
		
		return json.getBytes();
	}
	
	private List<ExperimentDescriptor> parseListExperimentsResponse(String data) {
		
		
		
		return getMockExperiments();
	}
	
	private List<ExperimentDescriptor> getMockExperiments() {

		List<ExperimentDescriptor> result = new ArrayList<ExperimentDescriptor>();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//exp 1
		ExperimentDescriptor descriptor = new ExperimentDescriptor();
		
		descriptor.setName("exp1");
		Amplifier amplifier = new Amplifier();
		amplifier.setName("amp1");
		List<Float> freq = new ArrayList<Float>();
		freq.add(127.0F);
		freq.add(444F);
		amplifier.setSamplingFrequencies(freq);
		
		List<AmplifierChannel> channels = new ArrayList<AmplifierChannel>();
		AmplifierChannel channel = new AmplifierChannel(1, "ch1");
		channels.add(channel);
		channel = new AmplifierChannel(2, "ch2");
		channels.add(channel);
		channel = new AmplifierChannel(3, "ch3");
		channels.add(channel);
		channel = new AmplifierChannel(4, "ch4");
		channels.add(channel);

		amplifier.setChannels(channels);
		descriptor.setAmplifier(amplifier);
		descriptor.setStatus(ExperimentStatus.NEW);
		result.add(descriptor);
		
		//exp2
		descriptor = new ExperimentDescriptor();
		
		descriptor.setName("exp2");
		amplifier = new Amplifier();
		amplifier.setName("amp1");
		freq = new ArrayList<Float>();
		freq.add(121.0F);
		freq.add(423F);
		amplifier.setSamplingFrequencies(freq);
		
		channels = new ArrayList<AmplifierChannel>();
		channel = new AmplifierChannel(1, "chA");
		channels.add(channel);
		channel = new AmplifierChannel(2, "chB");
		channels.add(channel);
		channel = new AmplifierChannel(3, "chC");
		channels.add(channel);
		amplifier.setChannels(channels);
		descriptor.setAmplifier(amplifier);
		descriptor.setStatus(ExperimentStatus.RUNNING);
		
		SignalParameters signalParameters = new SignalParameters();
		signalParameters.setSamplingFrequency(432.3F);
		signalParameters.setBlocksPerPage(14);
		signalParameters.setChannelCount(123);
		descriptor.setSignalParameters(signalParameters);

		result.add(descriptor);
		
		return result;
	}

}

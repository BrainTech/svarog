package org.signalml.app.worker.monitor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import org.signalml.app.model.document.opensignal.AbstractOpenSignalDescriptor;
import org.signalml.app.model.document.opensignal.Amplifier;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentStatus;
import org.signalml.app.model.document.opensignal.SignalParameters;
import org.signalml.app.view.document.opensignal.elements.AmplifierChannel;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

public class GetOpenBCIExperimentsWorker extends SwingWorker<List<ExperimentDescriptor>, Void>{

	@Override
	protected List<ExperimentDescriptor> doInBackground() throws Exception {
		List<ExperimentDescriptor> result = new ArrayList<ExperimentDescriptor>();

		Thread.sleep(2000);
		
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
		
		//XStream xstream = new XStream(new JettisonMappedXmlDriver());
		XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
		xstream.processAnnotations(ExperimentDescriptor.class);
		xstream.processAnnotations(AmplifierChannel.class);
		xstream.processAnnotations(AbstractOpenSignalDescriptor.class);
		xstream.processAnnotations(ExperimentDescriptor.class);
		xstream.alias("experiments", List.class);
		System.out.println(xstream.toXML(result));
		
		return result;
	}

}

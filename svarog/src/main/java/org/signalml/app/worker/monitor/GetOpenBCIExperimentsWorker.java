package org.signalml.app.worker.monitor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import org.signalml.app.model.document.opensignal.Amplifier;
import org.signalml.app.model.document.opensignal.ExperimentDescriptor;
import org.signalml.app.model.document.opensignal.ExperimentStatus;
import org.signalml.app.model.document.opensignal.SignalParameters;
import org.signalml.app.model.signal.SignalParameterDescriptor;
import org.signalml.app.view.document.opensignal.elements.AmplifierChannels;
import org.signalml.domain.signal.raw.RawSignalByteOrder;

public class GetOpenBCIExperimentsWorker extends SwingWorker<List<ExperimentDescriptor>, Void>{

	@Override
	protected List<ExperimentDescriptor> doInBackground() throws Exception {
		List<ExperimentDescriptor> result = new ArrayList<ExperimentDescriptor>();
		
//		Thread.sleep(4000);
		
		//exp 1
		ExperimentDescriptor descriptor = new ExperimentDescriptor();
		
		descriptor.setExperimentName("exp1");
		Amplifier amplifier = new Amplifier();
		amplifier.setName("amp1");
		List<Float> freq = new ArrayList<Float>();
		freq.add(127.0F);
		freq.add(444F);
		amplifier.setAvailableSamplingFrequencies(freq);
		
		List<String> channelLabels = new ArrayList<String>();
		channelLabels.add("ch1");
		channelLabels.add("ch2");
		channelLabels.add("ch3");
		channelLabels.add("ch4");
		AmplifierChannels channels = new AmplifierChannels(channelLabels);
		amplifier.setChannels(channels);
		descriptor.setAmplifier(amplifier);
		descriptor.setExperimentStatus(ExperimentStatus.NEW);
		result.add(descriptor);
		
		//exp2
		descriptor = new ExperimentDescriptor();
		
		descriptor.setExperimentName("exp2");
		amplifier = new Amplifier();
		amplifier.setName("amp1");
		freq = new ArrayList<Float>();
		freq.add(121.0F);
		freq.add(423F);
		amplifier.setAvailableSamplingFrequencies(freq);
		
		channelLabels = new ArrayList<String>();
		channelLabels.add("chA");
		channelLabels.add("chB");
		channelLabels.add("chC");
		channels = new AmplifierChannels(channelLabels);
		amplifier.setChannels(channels);
		descriptor.setAmplifier(amplifier);
		descriptor.setExperimentStatus(ExperimentStatus.RUNNING);
		
		SignalParameters signalParameters = new SignalParameters();
		signalParameters.setSamplingFrequency(432.3F);
		signalParameters.setBlocksPerPage(14);
		signalParameters.setChannelCount(123);
		descriptor.setSignalParameters(signalParameters);

		result.add(descriptor);		
		
		return result;
	}

}

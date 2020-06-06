/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.signalml.app.worker.monitor.messages;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.*;
import org.codehaus.jackson.annotate.JsonIgnore;
import static org.signalml.app.util.i18n.SvarogI18n._R;
import org.signalml.app.worker.monitor.Impedance;
import org.signalml.app.worker.monitor.NewSamplesData;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;


public class SignalMsg extends BaseMessage{
	
	
	private List<NewSamplesData> samples;
	
	public List<NewSamplesData> getSamples()
	{
		return samples;
	}
	
	@JsonIgnore
	@Override
	public BaseMessage deseralizeData(byte[] data, MessageType type) throws OpenbciCommunicationException{
		if (type != null){
			return new SignalMsg(data);
		}
		else
			throw new OpenbciCommunicationException(_R("Unknown message type"));
	}
	
	private SignalMsg(byte[] data)
	{
		super(MessageType.AMPLIFIER_SIGNAL_MESSAGE);
		parseSamples(data);
	}

	public SignalMsg()
	{
		super(MessageType.AMPLIFIER_SIGNAL_MESSAGE);
	}

	@JsonIgnore
	private void parseSamples(byte[] sampleMsgData)
	{
		samples = new ArrayList<>();
		try {
			DataInputStream data = new DataInputStream(new ByteArrayInputStream(sampleMsgData));
			final int sampleCount = data.readUnsignedShort();
			final int channelCount = data.readUnsignedShort();

			double[] timestamps = new double[sampleCount];
			for (int sample=0; sample<sampleCount; ++sample) {
				timestamps[sample] = data.readDouble();
			}

			float[][] samplesMatrix = new float[sampleCount][channelCount];
			for (int sample=0; sample<sampleCount; ++sample) {
				for (int i=0; i<channelCount; ++i) {
					samplesMatrix[sample][i] = data.readFloat();
				}
			}

			Impedance impedance = new Impedance(data, channelCount, sampleCount);
			for (int sampleId=0; sampleId<sampleCount; ++sampleId) {
				double samplesTimestamp = timestamps[sampleId];
				NewSamplesData newSamplesPackage = new NewSamplesData(
						samplesMatrix[sampleId],
						impedance.sample(sampleId),
						samplesTimestamp
				);
				samples.add(newSamplesPackage);
			}
		} catch (Exception ex) {
			logger.error("cannot process signal message", ex);
		}
	}
}

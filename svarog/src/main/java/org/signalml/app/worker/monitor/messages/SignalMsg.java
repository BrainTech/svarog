/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.signalml.app.worker.monitor.messages;

import org.codehaus.jackson.annotate.JsonIgnore;
import static org.signalml.app.util.i18n.SvarogI18n._R;
import org.signalml.app.worker.monitor.exceptions.OpenbciCommunicationException;
import static org.signalml.app.worker.monitor.messages.LauncherMessage.logger;
import static org.signalml.app.worker.monitor.messages.LauncherMessage.parseMessageType;
import org.signalml.app.worker.monitor.NewSamplesData;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.*;


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
			SignalMsg message = new SignalMsg(data);
			return (BaseMessage)message;
		}
		else
			throw new OpenbciCommunicationException(_R("Unknown message type"));
	}
	
	public SignalMsg(byte[] data)
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
		samples = new ArrayList<NewSamplesData>();
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

			int[] impedanceFlags = new int[channelCount];
			Set<Integer> channelsWithImpedance = new HashSet<>();
			for (int channel=0; channel<channelCount; ++channel) {
				impedanceFlags[channel] = data.readShort();
				if (impedanceFlags[channel] == 2) {
					channelsWithImpedance.add(channel);
				}
			}

			if (!channelsWithImpedance.isEmpty()) {
				for (int sample=0; sample<sampleCount; ++sample) {
					Map<Integer, Float> impedanceMap = new HashMap<>();
					for (Integer channel: channelsWithImpedance) {
						impedanceMap.put(channel, data.readFloat());
					}

					double samplesTimestamp = timestamps[sample];
					NewSamplesData newSamplesPackage = new NewSamplesData(
							samplesMatrix[sample],
							impedanceFlags,
							impedanceMap,
							samplesTimestamp
					);
					samples.add(newSamplesPackage);
				}
			}
			else {
				for (int sample=0; sample<sampleCount; ++sample) {
					double samplesTimestamp = timestamps[sample];
					NewSamplesData newSamplesPackage = new NewSamplesData(
							samplesMatrix[sample],
							impedanceFlags,
							samplesTimestamp
					);
					samples.add(newSamplesPackage);
				}
			}
		} catch (Exception ex) {
			logger.error("cannot process signal message", ex);
		}
	}
}

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


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
			for (int sample=0; sample<sampleCount; ++sample) {
				float[] newSamplesArray = new float[channelCount];
				for (int i=0; i<channelCount; ++i) {
					newSamplesArray[i] = data.readFloat();
				}

				double samplesTimestamp = timestamps[sample];
				NewSamplesData newSamplesPackage = new NewSamplesData(newSamplesArray, samplesTimestamp);
				samples.add(newSamplesPackage);
			}
		} catch (Exception ex) {
			logger.error("cannot process signal message", ex);
		}
	}
}
package org.signalml.app.worker;

import org.signalml.multiplexer.protocol.SvarogConstants;
//import org.signalml.multiplexer.protocol.SvarogProtocol.CalibrationParams;
//import org.signalml.multiplexer.protocol.SvarogProtocol.SamplingFrequency;

import multiplexer.jmx.client.JmxClient;
import multiplexer.protocol.Constants;
import multiplexer.protocol.Protocol.MultiplexerMessage;

public class MultiplexerMessageFactory {

	private MultiplexerMessageFactory() {}

//	public static MultiplexerMessage createSamplingFrequencyMessage( 
//			JmxClient jmxClient, double samplingFrequency) {
//
//		MultiplexerMessage result = null;
//		MultiplexerMessage.Builder multiplexerMessageBuilder = MultiplexerMessage.newBuilder();
//		multiplexerMessageBuilder.setType( SvarogConstants.MessageTypes.SAMPLING_FREQUENCY);
//
//		SamplingFrequency.Builder samplingFrequencyBuilder = SamplingFrequency.newBuilder();
//		samplingFrequencyBuilder.setFrequency( samplingFrequency);
//		SamplingFrequency samplingFrequencyMsg = samplingFrequencyBuilder.build();
//
//		multiplexerMessageBuilder.setMessage( samplingFrequencyMsg.toByteString());
//		result = jmxClient.createMessage( multiplexerMessageBuilder);
//		return result;
//	}

//	public static MultiplexerMessage createCalibrationMessage( 
//			JmxClient jmxClient, double bias, double offset) {
//
//		MultiplexerMessage result = null;
//		MultiplexerMessage.Builder multiplexerMessageBuilder = MultiplexerMessage.newBuilder();
//		multiplexerMessageBuilder.setType( SvarogConstants.MessageTypes.CALIBRATION);
//
//		CalibrationParams.Builder calibrationBuilder = CalibrationParams.newBuilder();
//		calibrationBuilder.setGain( bias);
//		calibrationBuilder.setOffset( offset);
//		CalibrationParams calibrationMsg = calibrationBuilder.build();
//
//		multiplexerMessageBuilder.setMessage( calibrationMsg.toByteString());
//		result = jmxClient.createMessage( multiplexerMessageBuilder);
//		return result;
//	}

}

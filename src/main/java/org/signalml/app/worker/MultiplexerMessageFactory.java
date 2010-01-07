package org.signalml.app.worker;

import multiplexer.jmx.client.JmxClient;
import multiplexer.protocol.Constants;
import multiplexer.protocol.Protocol.Calibration;
import multiplexer.protocol.Protocol.MultiplexerMessage;
import multiplexer.protocol.Protocol.SamplingFrequency;

public class MultiplexerMessageFactory {

    private MultiplexerMessageFactory() {}

    public static MultiplexerMessage createSamplingFrequencyMessage( 
            JmxClient jmxClient, double samplingFrequency) {

        MultiplexerMessage result = null;
        MultiplexerMessage.Builder multiplexerMessageBuilder = MultiplexerMessage.newBuilder();
        multiplexerMessageBuilder.setType( Constants.MessageTypes.SAMPLING_FREQUENCY);

        SamplingFrequency.Builder samplingFrequencyBuilder = SamplingFrequency.newBuilder();
        samplingFrequencyBuilder.setFrequency( samplingFrequency);
        SamplingFrequency samplingFrequencyMsg = samplingFrequencyBuilder.build();

        multiplexerMessageBuilder.setMessage( samplingFrequencyMsg.toByteString());
        result = jmxClient.createMessage( multiplexerMessageBuilder);
        return result;
    }

    public static MultiplexerMessage createCalibrationMessage( 
            JmxClient jmxClient, double bias, double offset) {

        MultiplexerMessage result = null;
        MultiplexerMessage.Builder multiplexerMessageBuilder = MultiplexerMessage.newBuilder();
        multiplexerMessageBuilder.setType( Constants.MessageTypes.CALIBRATION);

        Calibration.Builder calibrationBuilder = Calibration.newBuilder();
        calibrationBuilder.setBias( bias);
        calibrationBuilder.setOffset( offset);
        Calibration calibrationMsg = calibrationBuilder.build();

        multiplexerMessageBuilder.setMessage( calibrationMsg.toByteString());
        result = jmxClient.createMessage( multiplexerMessageBuilder);
        return result;
    }

}

package org.signalml.app.worker;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import multiplexer.jmx.client.JmxClient;
import multiplexer.protocol.Protocol.MultiplexerMessage;
import multiplexer.jmx.client.SendingMethod;
import multiplexer.jmx.exceptions.NoPeerForTypeException;
import org.signalml.multiplexer.protocol.SvarogProtocol.Variable.Builder;
import org.springframework.context.support.MessageSourceAccessor;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelFuture;
import org.signalml.multiplexer.protocol.SvarogConstants;
import org.signalml.multiplexer.protocol.SvarogProtocol.Variable;

/**
 * Sends configuration to openbci
 *
 * @author Tomasz Sawicki
 */
public class BCIConfigurationWorker extends SwingWorker<WorkerResult, Integer> {

        protected static final Logger logger = Logger.getLogger(BCIConfigurationWorker.class);

        public static final String NUMBER_OF_CHANNELS = "NumOfChannels";
        public static final String GAIN = "Gain";
        public static final String OFFSET = "Offset";
        public static final String SAMPLING_RATE = "SamplingRate";
        public static final String CHANNELS_TO_RECORD = "AmplifierChannelsToRecord";
        public static final String CHANNELS_NAMES = "ChannelsNames";

        /**
         * The message source.
         */
	private MessageSourceAccessor messageSource;

        /**
         * The Jmx Client.
         */
	private JmxClient client;

        /**
         * The {@link ConfigurationData} object.
         */
	private ConfigurationData configurationData;

        /**
         * String representation of the number of channels.
         */
        private String numberOfChannels = "";

        /**
         * String representation of the gain.
         */
        private String gain = "";

        /**
         * String representation of the offset.
         */
        private String offset = "";

        /**
         * String representation of the sampling rate.
         */
        private String samplingRate = "";

        /**
         * String representation of selected channels.
         */
        private String selectedChannels = "";

        /**
         * String representation of channel labels.
         */
        private String channelLabels = "";

        /**
         * Current state.
         */
	private Integer state;

        /**
         * Default constructor.
         *
         * @param messageSource {@link #messageSource}
         * @param client {@link #client}
         * @param openMonitorDescriptor {@link #openMonitorDescriptor}        
         */
        public BCIConfigurationWorker(MessageSourceAccessor messageSource,
                                      JmxClient client,
                                      ConfigurationData configurationData) {

                this.messageSource = messageSource;
                this.client = client;
                this.configurationData = configurationData;
        }

        /**
         * Sends configuration data.
         *
         * @param dataId data identifier
         * @param value data value
         * @return WorkerResult: (true, null) if success, (false, errMsg) if not
         */
        private WorkerResult sendConfigurationData(String dataId, String value) {

                logger.info("Sending " + dataId + " data: " + value);

                Builder variableBuilder = Variable.newBuilder();
                variableBuilder.setKey(dataId);
                variableBuilder.setValue(value);
                Variable variable = variableBuilder.build();

                MultiplexerMessage.Builder messageBuilder = MultiplexerMessage.newBuilder();
                messageBuilder.setType(SvarogConstants.MessageTypes.DICT_SET_MESSAGE).setMessage(variable.toByteString());
                MultiplexerMessage msg = client.createMessage(messageBuilder);
                                                
                try {
			ChannelFuture sendingOperation = client.send( msg, SendingMethod.THROUGH_ONE);
			sendingOperation.await(1, TimeUnit.SECONDS);
			if (!sendingOperation.isSuccess()) {
				logger.info("Sending " + dataId + " data failed!");
				String info = messageSource.getMessage("action.openMonitor.configurationWorker." + dataId + ".sendingFailedMsg");
				return new WorkerResult(false, info);
			}
		}
		catch (NoPeerForTypeException e) {

			logger.info("Sending " + dataId + " data failed!");
			String info = messageSource.getMessage("action.openMonitor.configurationWorker." + dataId + ".sendingFailedMsg");
			return new WorkerResult(false, info);
		}
		catch (InterruptedException e) {

			logger.info("Sending " + dataId + " data failed!");
			String info = messageSource.getMessage("action.openMonitor.configurationWorker." + dataId + ".sendingFailedMsg");
			return new WorkerResult(false, info);
		}

                return new WorkerResult(true, null);
        }

        /**
         * Extracts all necessary data from the {@link #openMonitorDescriptor}.
         */
        private void extractData() {

                numberOfChannels = configurationData.getChannelCount().toString();
                samplingRate = configurationData.getSamplingFrequency().toString();

                float[] gainArray = configurationData.getCalibrationGain();
                float[] offsetArray = configurationData.getCalibrationOffset();
                int[] selected = configurationData.getSelectedChannelsIndecies();
                String[] labels = configurationData.getChannelLabels();

                for (int i = 0; i < configurationData.getChannelCount(); i++) {

                        gain += String.valueOf(gainArray[i]);
                        offset += String.valueOf(offsetArray[i]);
                        selectedChannels += String.valueOf(selected[i]);
                        channelLabels += labels[i];

                        if (i < (configurationData.getChannelCount() - 1)) {

                                gain += " ";
                                offset += " ";
                                selectedChannels += " ";
                                channelLabels += ";";
                        }
                }
        }

        /**
         * Sends all the data
         *
         * @return sending result
         * @throws Exception
         */
        @Override
        protected WorkerResult doInBackground() throws Exception {

                int step = 0;
                WorkerResult result;

                extractData();

                result = sendConfigurationData(NUMBER_OF_CHANNELS, numberOfChannels);
                if (!result.success)
                        return result;
                publish(++step);

                result = sendConfigurationData(GAIN, gain);
                if (!result.success)
                        return result;
                publish(++step);

                result = sendConfigurationData(OFFSET, offset);
                if (!result.success)
                        return result;
                publish(++step);

                result = sendConfigurationData(SAMPLING_RATE, samplingRate);
                if (!result.success)
                        return result;
                publish(++step);

                result = sendConfigurationData(CHANNELS_TO_RECORD, selectedChannels);
                if (!result.success)
                        return result;
                publish(++step);

                result = sendConfigurationData(CHANNELS_NAMES, channelLabels);
                if (!result.success)
                        return result;
                publish(++step);

                return new WorkerResult(true, messageSource.getMessage("action.openMonitor.configurationWorker.Success"));
        }

        /**
         * Fires a property change each time state changes.
         * Property name is "currentState" and it's values are numbers
         * growing by 1 from 1 to the total number of variables to send.
         *
         * @param states states
         */
        @Override
        protected void process(List<Integer> states) {

		for (Integer i : states) {
			Integer oldState = state;
			state = i;
			firePropertyChange("currentState", oldState, state);
		}
        }

        /**
         * Fires a property change after the work is done.
         * Property name is "sendingDone" and it's value is a {@link WorkerResult} object
         * with success info and an informative message.
         */
        @Override
        protected void done() {

                WorkerResult result = new WorkerResult(false, messageSource.getMessage("action.openMonitor.configurationWorker.GeneralError"));
                
		try {
			result = get();
		}
		catch (InterruptedException e) {
			logger.debug("get interrupted! " + e.getMessage());
			e.printStackTrace();
		}
		catch (ExecutionException e) {
			logger.debug("get failed! " + e.getMessage());
			e.printStackTrace();
		}

		firePropertyChange("sendingDone", null, result);
        }
}

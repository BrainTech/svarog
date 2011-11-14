package org.signalml.app.worker;

import static org.signalml.app.SvarogApplication._;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingWorker;
import multiplexer.jmx.client.JmxClient;
import multiplexer.protocol.Protocol.MultiplexerMessage;
import multiplexer.jmx.client.SendingMethod;
import multiplexer.jmx.exceptions.NoPeerForTypeException;
import org.signalml.multiplexer.protocol.SvarogProtocol.Variable.Builder;

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

        public static final String CURRENT_STATE = "currentState";
        public static final String SENDING_DONE = "sendingDone";

        protected static final Logger logger = Logger.getLogger(BCIConfigurationWorker.class);

        /**
         * The Jmx Client.
         */
	private JmxClient client;

        /**
         * Current state.
         */
	private Integer state;

        /**
         * Data to be sent.
         * The key is OpenBCI key, e.g. "AmplifierChannelsToRecord".
         * The value is Message String, e.g. "Fp1;Fp2;Fz".
         */
        private HashMap<String, String> data;

        /**
         * Default constructor.
         *
         * @param client {@link #client}
         * @param data {@link #data}
         */
        public BCIConfigurationWorker(
                                      JmxClient client,
                                      HashMap<String, String> data) {
                this.client = client;
                this.data = data;
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
			ChannelFuture sendingOperation = client.send(msg, SendingMethod.THROUGH_ONE);
			sendingOperation.await(1, TimeUnit.SECONDS);
			if (!sendingOperation.isSuccess()) {
				logger.info("Sending " + dataId + " data failed!");
				String info = _("Sending failed!");
				return new WorkerResult(false, info);
			}
		}
		catch (NoPeerForTypeException e) {

			logger.info("Sending " + dataId + " data failed!");
			String info = _("Sending failed!");
			return new WorkerResult(false, info);
		}
		catch (InterruptedException e) {

			logger.info("Sending " + dataId + " data failed!");
			String info = _("Sending failed!");
			return new WorkerResult(false, info);
		}

                return new WorkerResult(true, null);
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

                for (String key : data.keySet()) {

                        result = sendConfigurationData(key, data.get(key));
                        if (!result.success)
                                return result;
                        publish(++step);
                }

                return new WorkerResult(true, _("Configuration sent!"));
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
			firePropertyChange(CURRENT_STATE, oldState, state);
		}
        }

        /**
         * Fires a property change after the work is done.
         * Property name is "sendingDone" and it's value is a {@link WorkerResult} object
         * with success info and an informative message.
         */
        @Override
        protected void done() {

                WorkerResult result = new WorkerResult(false, _("Sending failed!"));
                
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

		firePropertyChange(SENDING_DONE, null, result);
        }
}
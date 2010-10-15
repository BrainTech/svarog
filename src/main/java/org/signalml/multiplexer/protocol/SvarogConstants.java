package org.signalml.multiplexer.protocol;

import java.util.*;

public class SvarogConstants implements multiplexer.jmx.tools.rulesconsts.ConstantsPack {

	public static class PeerTypes implements multiplexer.jmx.tools.rulesconsts.PeerTypes {

		public static final PeerTypes instance = new PeerTypes();

		public final static int PYTHON_TEST_SERVER = 106;
		public final static int PYTHON_TEST_CLIENT = 107;
		public final static int LOG_STREAMER = 108;
		public final static int LOG_COLLECTOR = 109;
		public final static int EVENTS_COLLECTOR = 110;
		public final static int LOG_RECEIVER_EXAMPLE = 111;
		public final static int AMPLIFIER = 112;
		public final static int SIGNAL_CATCHER = 113;
		public final static int MONITOR = 114;
		public final static int HASHTABLE = 115;
		public final static int FILTER = 116;
		public final static int ANALYSIS = 117;
		public final static int LOGIC = 118;
		public final static int STREAM_RECEIVER = 119;
		public final static int DIODE = 120;
		public final static int DIODE_CATCHER = 121;
		public final static int SUPER_DIODE = 122;
		public final static int SIGNAL_STREAMER = 123;
		public final static int SIGNAL_SAVER = 124;
		public final static int SIGNAL_SAVER_CONTROL = 125;
		public final static int UGM = 126;
		public final static int PINGER = 127;
		public final static int TAGS_SENDER = 128;
		public final static int TAGS_RECEIVER = 129;
		public final static int CALIBRATOR = 130;
		public final static int TAG_CATCHER = 131;

		private static class ConstantsByNameMapHolder {
			public final static Map<String, Integer> map;
			static {
				Map<String, Integer> tmp = new HashMap<String, Integer>();
				tmp.put("PYTHON_TEST_SERVER", PYTHON_TEST_SERVER);
				tmp.put("PYTHON_TEST_CLIENT", PYTHON_TEST_CLIENT);
				tmp.put("LOG_STREAMER", LOG_STREAMER);
				tmp.put("LOG_COLLECTOR", LOG_COLLECTOR);
				tmp.put("EVENTS_COLLECTOR", EVENTS_COLLECTOR);
				tmp.put("LOG_RECEIVER_EXAMPLE", LOG_RECEIVER_EXAMPLE);
				tmp.put("AMPLIFIER", AMPLIFIER);
				tmp.put("SIGNAL_CATCHER", SIGNAL_CATCHER);
				tmp.put("MONITOR", MONITOR);
				tmp.put("HASHTABLE", HASHTABLE);
				tmp.put("FILTER", FILTER);
				tmp.put("ANALYSIS", ANALYSIS);
				tmp.put("LOGIC", LOGIC);
				tmp.put("STREAM_RECEIVER", STREAM_RECEIVER);
				tmp.put("DIODE", DIODE);
				tmp.put("DIODE_CATCHER", DIODE_CATCHER);
				tmp.put("SUPER_DIODE", SUPER_DIODE);
				tmp.put("SIGNAL_STREAMER", SIGNAL_STREAMER);
				tmp.put("SIGNAL_SAVER", SIGNAL_SAVER);
				tmp.put("SIGNAL_SAVER_CONTROL", SIGNAL_SAVER_CONTROL);
				tmp.put("UGM", UGM);
				tmp.put("PINGER", PINGER);
				tmp.put("TAGS_SENDER", TAGS_SENDER);
				tmp.put("TAGS_RECEIVER", TAGS_RECEIVER);
				tmp.put("CALIBRATOR", CALIBRATOR);
				tmp.put("TAG_CATCHER", TAG_CATCHER);
				map = Collections.unmodifiableMap(tmp);
			}
		}

		private static class ConstantsNamesMapHolder {
			public final static Map<Integer, String> map;
			static {
				Map<Integer, String> tmp = new HashMap<Integer, String>();
				tmp.put(PYTHON_TEST_SERVER, "PYTHON_TEST_SERVER");
				tmp.put(PYTHON_TEST_CLIENT, "PYTHON_TEST_CLIENT");
				tmp.put(LOG_STREAMER, "LOG_STREAMER");
				tmp.put(LOG_COLLECTOR, "LOG_COLLECTOR");
				tmp.put(EVENTS_COLLECTOR, "EVENTS_COLLECTOR");
				tmp.put(LOG_RECEIVER_EXAMPLE, "LOG_RECEIVER_EXAMPLE");
				tmp.put(AMPLIFIER, "AMPLIFIER");
				tmp.put(SIGNAL_CATCHER, "SIGNAL_CATCHER");
				tmp.put(MONITOR, "MONITOR");
				tmp.put(HASHTABLE, "HASHTABLE");
				tmp.put(FILTER, "FILTER");
				tmp.put(ANALYSIS, "ANALYSIS");
				tmp.put(LOGIC, "LOGIC");
				tmp.put(STREAM_RECEIVER, "STREAM_RECEIVER");
				tmp.put(DIODE, "DIODE");
				tmp.put(DIODE_CATCHER, "DIODE_CATCHER");
				tmp.put(SUPER_DIODE, "SUPER_DIODE");
				tmp.put(SIGNAL_STREAMER, "SIGNAL_STREAMER");
				tmp.put(SIGNAL_SAVER, "SIGNAL_SAVER");
				tmp.put(SIGNAL_SAVER_CONTROL, "SIGNAL_SAVER_CONTROL");
				tmp.put(UGM, "UGM");
				tmp.put(PINGER, "PINGER");
				tmp.put(TAGS_SENDER, "TAGS_SENDER");
				tmp.put(TAGS_RECEIVER, "TAGS_RECEIVER");
				tmp.put(CALIBRATOR, "CALIBRATOR");
				tmp.put(TAG_CATCHER, "TAG_CATCHER");
				map = Collections.unmodifiableMap(tmp);
			}
		}

		public Map<String, Integer> getConstantsByName() {
			return ConstantsByNameMapHolder.map;
		};

		public Map<Integer, String> getConstantsNames() {
			return ConstantsNamesMapHolder.map;
		};
	}

	public multiplexer.jmx.tools.rulesconsts.PeerTypes getPeerTypes() {
		return PeerTypes.instance;
	}

	public static class MessageTypes implements multiplexer.jmx.tools.rulesconsts.MessageTypes {

		public static final MessageTypes instance = new MessageTypes();

		public final static int PYTHON_TEST_REQUEST = 110;
		public final static int PYTHON_TEST_RESPONSE = 111;
		public final static int PICKLE_RESPONSE = 112;
		public final static int LOGS_STREAM = 115;
		public final static int LOGS_STREAM_RESPONSE = 116;
		public final static int SEARCH_COLLECTED_LOGS_REQUEST = 117;
		public final static int SEARCH_COLLECTED_LOGS_RESPONSE = 118;
		public final static int REPLAY_EVENTS_REQUEST = 126;
		public final static int AMPLIFIER_SIGNAL_MESSAGE = 129;
		public final static int FILTERED_SIGNAL_MESSAGE = 130;
		public final static int SIGNAL_CATCHER_REQUEST_MESSAGE = 131;
		public final static int SIGNAL_CATCHER_RESPONSE_MESSAGE = 132;
		public final static int DICT_GET_REQUEST_MESSAGE = 133;
		public final static int DICT_GET_RESPONSE_MESSAGE = 134;
		public final static int DICT_SET_MESSAGE = 135;
		public final static int DECISION_MESSAGE = 136;
		public final static int DIODE_MESSAGE = 137;
		public final static int DIODE_REQUEST = 138;
		public final static int DIODE_RESPONSE = 139;
		public final static int P300_DECISION_MESSAGE = 140;
		public final static int SSVEP_DECISION_MESSAGE = 141;
		public final static int SWITCH_MODE = 142;
		public final static int STREAMED_SIGNAL_MESSAGE = 143;
		public final static int SIGNAL_STREAMER_START = 144;
		public final static int SIGNAL_STREAMER_STOP = 145;
		public final static int SAMPLING_FREQUENCY = 146;
		public final static int CALIBRATION = 147;
		public final static int SIGNAL_SAVER_CONTROL_MESSAGE = 148;
		public final static int UGM_UPDATE_MESSAGE = 149;
		public final static int TAG = 150;
		public final static int DIODE_UPDATE_MESSAGE = 151;
		public final static int TAG_CATCHER_REQUEST_MESSAGE = 152;
		public final static int TAG_CATCHER_RESPONSE_MESSAGE = 153;

		private static class ConstantsByNameMapHolder {
			public final static Map<String, Integer> map;
			static {
				Map<String, Integer> tmp = new HashMap<String, Integer>();
				tmp.put("PYTHON_TEST_REQUEST", PYTHON_TEST_REQUEST);
				tmp.put("PYTHON_TEST_RESPONSE", PYTHON_TEST_RESPONSE);
				tmp.put("PICKLE_RESPONSE", PICKLE_RESPONSE);
				tmp.put("LOGS_STREAM", LOGS_STREAM);
				tmp.put("LOGS_STREAM_RESPONSE", LOGS_STREAM_RESPONSE);
				tmp.put("SEARCH_COLLECTED_LOGS_REQUEST", SEARCH_COLLECTED_LOGS_REQUEST);
				tmp.put("SEARCH_COLLECTED_LOGS_RESPONSE", SEARCH_COLLECTED_LOGS_RESPONSE);
				tmp.put("REPLAY_EVENTS_REQUEST", REPLAY_EVENTS_REQUEST);
				tmp.put("AMPLIFIER_SIGNAL_MESSAGE", AMPLIFIER_SIGNAL_MESSAGE);
				tmp.put("FILTERED_SIGNAL_MESSAGE", FILTERED_SIGNAL_MESSAGE);
				tmp.put("SIGNAL_CATCHER_REQUEST_MESSAGE", SIGNAL_CATCHER_REQUEST_MESSAGE);
				tmp.put("SIGNAL_CATCHER_RESPONSE_MESSAGE", SIGNAL_CATCHER_RESPONSE_MESSAGE);
				tmp.put("DICT_GET_REQUEST_MESSAGE", DICT_GET_REQUEST_MESSAGE);
				tmp.put("DICT_GET_RESPONSE_MESSAGE", DICT_GET_RESPONSE_MESSAGE);
				tmp.put("DICT_SET_MESSAGE", DICT_SET_MESSAGE);
				tmp.put("DECISION_MESSAGE", DECISION_MESSAGE);
				tmp.put("DIODE_MESSAGE", DIODE_MESSAGE);
				tmp.put("DIODE_REQUEST", DIODE_REQUEST);
				tmp.put("DIODE_RESPONSE", DIODE_RESPONSE);
				tmp.put("P300_DECISION_MESSAGE", P300_DECISION_MESSAGE);
				tmp.put("SSVEP_DECISION_MESSAGE", SSVEP_DECISION_MESSAGE);
				tmp.put("SWITCH_MODE", SWITCH_MODE);
				tmp.put("STREAMED_SIGNAL_MESSAGE", STREAMED_SIGNAL_MESSAGE);
				tmp.put("SIGNAL_STREAMER_START", SIGNAL_STREAMER_START);
				tmp.put("SIGNAL_STREAMER_STOP", SIGNAL_STREAMER_STOP);
				tmp.put("SAMPLING_FREQUENCY", SAMPLING_FREQUENCY);
				tmp.put("CALIBRATION", CALIBRATION);
				tmp.put("SIGNAL_SAVER_CONTROL_MESSAGE", SIGNAL_SAVER_CONTROL_MESSAGE);
				tmp.put("UGM_UPDATE_MESSAGE", UGM_UPDATE_MESSAGE);
				tmp.put("TAG", TAG);
				tmp.put("DIODE_UPDATE_MESSAGE", DIODE_UPDATE_MESSAGE);
				tmp.put("TAG_CATCHER_REQUEST_MESSAGE", TAG_CATCHER_REQUEST_MESSAGE);
				tmp.put("TAG_CATCHER_RESPONSE_MESSAGE", TAG_CATCHER_RESPONSE_MESSAGE);
				map = Collections.unmodifiableMap(tmp);
			}
		}

		private static class ConstantsNamesMapHolder {
			public final static Map<Integer, String> map;
			static {
				Map<Integer, String> tmp = new HashMap<Integer, String>();
				tmp.put(PYTHON_TEST_REQUEST, "PYTHON_TEST_REQUEST");
				tmp.put(PYTHON_TEST_RESPONSE, "PYTHON_TEST_RESPONSE");
				tmp.put(PICKLE_RESPONSE, "PICKLE_RESPONSE");
				tmp.put(LOGS_STREAM, "LOGS_STREAM");
				tmp.put(LOGS_STREAM_RESPONSE, "LOGS_STREAM_RESPONSE");
				tmp.put(SEARCH_COLLECTED_LOGS_REQUEST, "SEARCH_COLLECTED_LOGS_REQUEST");
				tmp.put(SEARCH_COLLECTED_LOGS_RESPONSE, "SEARCH_COLLECTED_LOGS_RESPONSE");
				tmp.put(REPLAY_EVENTS_REQUEST, "REPLAY_EVENTS_REQUEST");
				tmp.put(AMPLIFIER_SIGNAL_MESSAGE, "AMPLIFIER_SIGNAL_MESSAGE");
				tmp.put(FILTERED_SIGNAL_MESSAGE, "FILTERED_SIGNAL_MESSAGE");
				tmp.put(SIGNAL_CATCHER_REQUEST_MESSAGE, "SIGNAL_CATCHER_REQUEST_MESSAGE");
				tmp.put(SIGNAL_CATCHER_RESPONSE_MESSAGE, "SIGNAL_CATCHER_RESPONSE_MESSAGE");
				tmp.put(DICT_GET_REQUEST_MESSAGE, "DICT_GET_REQUEST_MESSAGE");
				tmp.put(DICT_GET_RESPONSE_MESSAGE, "DICT_GET_RESPONSE_MESSAGE");
				tmp.put(DICT_SET_MESSAGE, "DICT_SET_MESSAGE");
				tmp.put(DECISION_MESSAGE, "DECISION_MESSAGE");
				tmp.put(DIODE_MESSAGE, "DIODE_MESSAGE");
				tmp.put(DIODE_REQUEST, "DIODE_REQUEST");
				tmp.put(DIODE_RESPONSE, "DIODE_RESPONSE");
				tmp.put(P300_DECISION_MESSAGE, "P300_DECISION_MESSAGE");
				tmp.put(SSVEP_DECISION_MESSAGE, "SSVEP_DECISION_MESSAGE");
				tmp.put(SWITCH_MODE, "SWITCH_MODE");
				tmp.put(STREAMED_SIGNAL_MESSAGE, "STREAMED_SIGNAL_MESSAGE");
				tmp.put(SIGNAL_STREAMER_START, "SIGNAL_STREAMER_START");
				tmp.put(SIGNAL_STREAMER_STOP, "SIGNAL_STREAMER_STOP");
				tmp.put(SAMPLING_FREQUENCY, "SAMPLING_FREQUENCY");
				tmp.put(CALIBRATION, "CALIBRATION");
				tmp.put(SIGNAL_SAVER_CONTROL_MESSAGE, "SIGNAL_SAVER_CONTROL_MESSAGE");
				tmp.put(UGM_UPDATE_MESSAGE, "UGM_UPDATE_MESSAGE");
				tmp.put(TAG, "TAG");
				tmp.put(DIODE_UPDATE_MESSAGE, "DIODE_UPDATE_MESSAGE");
				tmp.put(TAG_CATCHER_REQUEST_MESSAGE, "TAG_CATCHER_REQUEST_MESSAGE");
				tmp.put(TAG_CATCHER_RESPONSE_MESSAGE, "TAG_CATCHER_RESPONSE_MESSAGE");
				map = Collections.unmodifiableMap(tmp);
			}
		}

		public Map<String, Integer> getConstantsByName() {
			return ConstantsByNameMapHolder.map;
		};

		public Map<Integer, String> getConstantsNames() {
			return ConstantsNamesMapHolder.map;
		};
	}

	public multiplexer.jmx.tools.rulesconsts.MessageTypes getMessageTypes() {
		return MessageTypes.instance;
	}
}

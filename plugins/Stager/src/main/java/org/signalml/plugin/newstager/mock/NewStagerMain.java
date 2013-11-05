package org.signalml.plugin.newstager.mock;

import java.io.File;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.signalml.app.document.BookDocument;
import org.signalml.codec.SignalMLCodecReader;
import org.signalml.codec.XMLSignalMLCodec;
import org.signalml.domain.book.StandardBook;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.plugin.io.FastMultichannelSampleSource;
import org.signalml.plugin.newstager.data.NewStagerConstants;
import org.signalml.plugin.newstager.data.NewStagerData;
import org.signalml.plugin.newstager.data.NewStagerFASPThreshold;
import org.signalml.plugin.newstager.data.NewStagerFixedParameters;
import org.signalml.plugin.newstager.data.NewStagerParameterThresholds;
import org.signalml.plugin.newstager.data.NewStagerParameters;
import org.signalml.plugin.newstager.data.NewStagerRules;
import org.signalml.plugin.newstager.data.logic.NewStagerMgrData;
import org.signalml.plugin.newstager.logic.mgr.NewStagerComputationMgr;

import pl.edu.fuw.MP.Core.Utils;

class Tracker implements MethodExecutionTracker {

	@Override
	public boolean isRequestingAbort() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestingSuspend() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int[] getTickerLimits() {
		return new int[0];
	}

	@Override
	public void setTickerLimits(int[] initial) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTickerLimit(int index, int limit) {
		// TODO Auto-generated method stub

	}

	@Override
	public int[] getTickers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetTickers() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTickers(int[] current) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTicker(int index, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tick(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tick(int index, int step) {
		// TODO Auto-generated method stub

	}

	@Override
	public Integer getExpectedSecondsUntilComplete(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMessage(String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}

public class NewStagerMain {

	protected static final Logger logger = Logger.getLogger(NewStagerMain.class);

	private static String codecSourceFile = "../../specs/EASYS.xml";
	// private static String bookFilePath = "../../../../book_20sec_a1.3_smp.b";
	private static String bookFilePath = "E:/nowedane/inb14_20sec_a1.3_smp.b";
	private static String sourceSignalFilePath = "E:/nowedane/inb14.d";
	// private static String sourceSignalFilePath = "../../../../inb14.d";

	private static String PATH = "E:/";

	private static float FREQUENCY = 128.0f;
	private static int OFFSET_DIMENSION = 20;

	public static void main(String[] args) {

		XMLSignalMLCodec codec;
		try {
			codec = new XMLSignalMLCodec(
				new File(codecSourceFile).getAbsoluteFile(), null);
			SignalMLCodecReader reader = codec.createReader();
			reader.open(sourceSignalFilePath);
			FastMultichannelSampleSource source = new FastMultichannelSampleSource(
				reader);

			NewStagerComputationMgr mgr = new NewStagerComputationMgr();
			NewStagerData stagerData = new NewStagerData();
			stagerData.setSampleSource(source);
			stagerData.setProjectPath(PATH);

			HashMap<String, Integer> channelMap = new HashMap<String, Integer>();

			channelMap.put("A2", 22);
			channelMap.put("A1", 21);
			channelMap.put("C4", 11);
			channelMap.put("C3", 9);
			channelMap.put("F8", 7);
			channelMap.put("F7", 3);
			channelMap.put("T3", 8);
			channelMap.put("EOGL", 24);
			channelMap.put("T4", 12);
			channelMap.put("EOGP", 23);
			channelMap.put("F4", 6);
			channelMap.put("Fp1", 0);
			channelMap.put("F3", 4);
			channelMap.put("ECG", 26);
			channelMap.put("Fp2", 2);
			channelMap.put("EMG", 25);

			stagerData.setChannelMap(channelMap);

			Utils.loggingFlag = false;

			BookDocument doc = new BookDocument(new File(bookFilePath));
			doc.openDocument();
			StandardBook b = doc.getBook();

			NewStagerFASPThreshold alphaThreshold = NewStagerFASPThreshold
													.CreateThreshold(5.0, Double.POSITIVE_INFINITY, 8.0, 12.0,
															1.5, Double.POSITIVE_INFINITY, null, null);
			NewStagerFASPThreshold deltaThreshold = NewStagerFASPThreshold
													.CreateThreshold(65.0, Double.POSITIVE_INFINITY, 0.2, 4.0,
															0.5, 6.0, null, null);
			NewStagerFASPThreshold spindleThreshold = NewStagerFASPThreshold
					.CreateThreshold(12.0, Double.POSITIVE_INFINITY, 11.0,
									 15.0, 0.4, 2.5, null, null);
			NewStagerFASPThreshold thetaThreshold = NewStagerFASPThreshold
													.CreateThreshold(30.0, Double.POSITIVE_INFINITY, 4.0, 8.0,
															0.1, Double.POSITIVE_INFINITY, null, null);
			NewStagerFASPThreshold KCThreshold = NewStagerFASPThreshold
												 .CreateThreshold(100.0, Double.POSITIVE_INFINITY, 0.03,
														 2.5, 0.3, 1.5, -0.5, 0.5);

			NewStagerParameterThresholds thresholds = new NewStagerParameterThresholds(
				-1111d, 40, 300, 50, 100, 20, alphaThreshold, deltaThreshold,
				spindleThreshold, thetaThreshold, KCThreshold);

			stagerData.setParameters(new NewStagerParameters(bookFilePath,
									 NewStagerRules.RK, true, true, true, thresholds));
			stagerData.setFixedParameters(new NewStagerFixedParameters(1.0d, 1.0d,
										  0.75d, 0.5d, -0.85d, -0.7d));

			mgr.compute(
				new NewStagerMgrData(stagerData, new NewStagerConstants(b
									 .getSamplingFrequency(), (int) b.getCalibration(),
									 b.getSegmentCount(),
									 NewStagerConstants.DEFAULT_MUSCLE_THRESHOLD,
									 NewStagerConstants.DEFAULT_MUSCLE_THRESHOLD_RATE,
									 NewStagerConstants.DEFAULT_AMPLITUDE_A,
									 NewStagerConstants.DEFAULT_AMPLITUDE_B,
									 NewStagerConstants.DEFAULT_ALPHA_OFFSET,
									 NewStagerConstants.DEFAULT_DELTA_OFFSET,
									 NewStagerConstants.DEFAULT_SPINDLE_OFFSET)),
				new Tracker());

		} catch (Exception e) {
			logger.error("", e);
		}

		System.exit(0);
	}
}

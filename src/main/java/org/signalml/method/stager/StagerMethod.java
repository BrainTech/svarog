/* StagerMethod.java created 2008-02-08
 *
 */

package org.signalml.method.stager;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerFactory;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.signalml.app.model.SignalExportDescriptor;
import org.signalml.app.view.signal.SampleSourceUtils;
import org.signalml.domain.book.BookFormatException;
import org.signalml.domain.signal.AbstractSignalWriterMonitor;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.SignalWriterMonitor;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.domain.signal.raw.RawSignalWriter;
import org.signalml.exception.SignalMLException;
import org.signalml.method.AbstractMethod;
import org.signalml.method.ComputationException;
import org.signalml.method.DisposableMethod;
import org.signalml.method.InitializingMethod;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.method.TrackableMethod;
import org.signalml.util.ResolvableString;
import org.signalml.util.Util;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

import pl.edu.fuw.MP.MPBookStore;
import stager_mbfj.Stager_mbfj;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import com.mathworks.toolbox.javabuilder.MWStructArray;
import com.thoughtworks.xstream.XStream;

/** StagerMethod
 *
 * @author Oskar Kapala &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */

public class StagerMethod extends AbstractMethod implements TrackableMethod, InitializingMethod, DisposableMethod {

	protected static final Logger logger = Logger.getLogger(StagerMethod.class);

	private static final String UID = "3c5b3e3d-c6b5-467b-8c20-fee30874889c";
	private static final String NAME = "stager";
	private static final int[] VERSION = new int[] {5,0};

	private RawSignalWriter rawSignalWriter = new RawSignalWriter();

//	private stager2MBfJclass solver = null;
	private Stager_mbfj solver = null;

	private double delta_3_value = 0.0;

	String folRob;

	private XStream streamer;

	private boolean[] notTrackable;

	@Override
	public final void initialize() throws SignalMLException {

		String transformerFactoryClassName = TransformerFactory.newInstance().getClass().getName();
		logger.debug("Default class name [" + transformerFactoryClassName + "]");

		try {
			this.solver= new Stager_mbfj();
		} catch (MWException e) {
			logger.warn("Couldn't initialize Stager, this functionality will be broken", e);
			throw new SignalMLException(e);
		} finally {
			System.setProperty("javax.xml.transform.TransformerFactory", transformerFactoryClassName);
		}

	}

	@Override
	public final void dispose() throws SignalMLException {
		if (solver != null) {
			solver.dispose();
		}
	}

	private final Object[] runStagerML(StagerData data, final MethodExecutionTracker tracker) throws ComputationException {

		notTrackable = new boolean[getTickerCount()];
		for (int i = 0; i < notTrackable.length; i++) {
			notTrackable[i] = false;
		}

		Object[] result = null;

		folRob = (new File(data.getProjectPath(), data.getPatientName())).getAbsolutePath();

		MultichannelSampleSource sampleSource = data.getSampleSource();

		if (solver == null) {
			throw new ComputationException("Artifact solver is not avaible");
		}

		try {

			File processed = new File(folRob,".processed");

			File signalFile = new File(new File(data.getProjectPath(), data.getPatientName()).getAbsolutePath() ,"signalml_stager.dat");
			data.setSignalPath(signalFile.getAbsolutePath());

			if (!(processed.exists() && signalFile.exists())) {

				processed.delete();

				synchronized (tracker) {
					tracker.setMessage(new ResolvableString("artifactMethod.message.creatingFiles"));
					tracker.setTickerLimit(0, 2);
					tracker.setTicker(0, 0);
				}

//				createIntermediateFiles(data);

				synchronized (tracker) {
					tracker.setMessage(new ResolvableString("artifactMethod.message.writingSignalFile"));
					tracker.setTickerLimit(1, SampleSourceUtils.getMinSampleCount(sampleSource));
					tracker.setTicker(1,0);
				}

				SignalWriterMonitor signalWriterMonitor = new AbstractSignalWriterMonitor() {
					@Override
					public void setProcessedSampleCount(int processedCount) {
						// FIXME modify proper tracker
						tracker.setTicker(1, processedCount);
					}

					@Override
					public boolean isRequestingAbort() {
						// local abort flag is ignored
						return tracker.isRequestingAbort();
					}
				};

				SignalExportDescriptor signalExportDescriptor = new SignalExportDescriptor();
				signalExportDescriptor.setSampleType(RawSignalSampleType.FLOAT);
				signalExportDescriptor.setByteOrder(RawSignalByteOrder.LITTLE_ENDIAN);
				signalExportDescriptor.setNormalize(false);

				rawSignalWriter.writeSignal(signalFile, sampleSource, signalExportDescriptor, signalWriterMonitor);

				processed.createNewFile();

				tracker.tick(0);

			} else {
				Log.debug("File: "+data.getSignalPath()+" will be used for futher processing");
				synchronized (tracker) {
					tracker.setTickerLimit(0, 1);
					tracker.setTicker(0,0);
				}
			}

			if (tracker.isRequestingAbort()) {
				return null;
			}

			synchronized (tracker) {
				notTrackable[1] = true;
				tracker.tick(0);
				tracker.setMessage(new ResolvableString("stagerMethod.message.staging"));
				tracker.setTickerLimit(1, 1);
				tracker.setTicker(1, 0);
			}

			String book = data.getParameters().getBookFilePath();

			double Fs = data.getSampleSource().getSamplingFrequency();

			MPBookStore bookStore = new MPBookStore();
			bookStore.Open(book);
			double epochSIZE = bookStore.getDimBase() / Fs;

			String[] CElecNames = {"F7", "F8", "T3", "T4", "EOGP", "EOGL", "Fp1", "Fp2", "F3", "F4", "C3", "C4", "EKG", "EMG", "A1", "A2"};
			MWStructArray CElec = new MWStructArray(1, 1, CElecNames);
			double value =0;
			for (String elecName : CElecNames) {
				if (elecName.compareTo("EKG") == 0) {
					value = (double) data.getKeyChannelMap().get("ECG") + 1;
				} else {
					value = (double) data.getKeyChannelMap().get(elecName) + 1;
				}
				CElec.set(elecName, 1, new MWNumericArray(Double.valueOf(value), MWClassID.DOUBLE));
			}

			int rules;
			if (data.getParameters().getRules().hashCode() == SleepStagingRules.RK.hashCode()) {
				rules=1;
			} else {
				rules=-1;
			}

//			%BASIC:
			Double Adelta = null;
			Adelta = data.getParameters().getDeltaAmplitude().getMinWithUnlimited();

			Double Aspin = null;
			Aspin = data.getParameters().getSpindleAmplitude().getMinWithUnlimited();

			Double Aalpha = null;
			Aalpha=data.getParameters().getAlphaAmplitude().getMinWithUnlimited();

//			%opcja zapisu wynikow posrednich:
			int primaryTags=0;
			if (data.getParameters().isPrimaryHypnogram()) {
				primaryTags=1;
			}
//
//			%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//
//			%ADVANCED:
//			delta=[0.2 4 65 -1 0.5 6]; %swa- przedzialy parametrow ([Fmin Fmax],[Amin Amax],[Dmin Dmax])
			double[] delta =data.getParameters().getDeltaParameterArray();
			if (Adelta != null) {
				delta[2] = Adelta;
			}

//			theta=[4 8 30 -1 0.1 -1]; %teta -przedzialy parametrow ([Fmin Fmax],[Amin Amax],[Dmin Dmax])
			double[] theta=data.getParameters().getThetaParameterArray();


//			alpha=[8 12 4 -1 1.5 -1]; %alfa - przedzialy parametrow ([Fmin Fmax],[Amin Amax],[Dmin Dmax])
			double[] alpha=data.getParameters().getAlphaParameterArray();
			if (Aalpha != null) {
				alpha[2] = Aalpha;
			}

//			spindle=[11 15 13 -1 0.4 2.5]; %wrzeciona - przedzialy parametrow ([Fmin Fmax],[Amin Amax],[Dmin Dmax])
			double[] spindle=data.getParameters().getSpindleParameterArray();
			if (Aspin != null) {
				spindle[2]=Aspin;
			}

//			KC=[0.03 2.5 100 -1 0.3 1.5 -0.5 0.5]; %KC - przedzialy parametrow, PHASE poza tym przedzialem!!([Fmin Fmax],[Amin Amax],[Dmin Dmax], [P1 P2])
			double[] KC = data.getParameters().getKComplexParameterArray();

//			%suwaki:
//			toneEMG=25; %toneEMG treshold
			double toneEMG = data.getParameters().getEmgToneThreshold();

//			MTeeg=40; eeg=1;%MTeeg treshold lub wylaczone MTeeg - eeg=0;
			int eeg=0;
			double MTeeg = 0;
			if (data.getParameters().isMtEegThresholdEnabled()) {
				eeg=1;
				MTeeg = data.getParameters().getMtEegThreshold();
			}

//			MTemg=300;      %MTemg treshold
			int emg=0;
			double MTemg = 0;
			double MTemg_tone = 0;
			if (data.getParameters().isMtArtifactsThresholdEnabled()) {
				emg=1;
				MTemg = data.getParameters().getMtEmgThreshold();
				MTemg_tone = data.getParameters().getMtToneEmgThreshold();
			}

//			MTemg_tone=50;  %MTemg treshold filtrowany

//			defl_rems=100; %deflection rems treshold;
//			defl_sems=20;  %deflection sems treshold;
			double defl_rems = data.getParameters().getRemEogDeflectionThreshold();
			double defl_sems = data.getParameters().getSemEogDeflectionThreshold();

//			%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

//			%UKRYTE [swa_WIDTH_COEFF, alpha_perc1, alpha_perc2, corr_coeff_rems, corr_coeff_sems]
//			ukryte=[1, 0.75, 0.5, -0.85 -0.7];
//			%swa_WIDTH_COEFF=1;
//			%alpha_percent1=0.75;
//			%alpha_percent2=0.5;
//			%corr_coeff_rems=-0.85
//			%corr_coeff_sems=-0.7
//			double[] ukryte={1d, 0.75d, 0.5d, -0.85d, -0.7d};
			double[] ukryte = {
				data.getFixedParameters().getSwaWidthCoeff(),
				data.getFixedParameters().getAlphaPerc1(),
				data.getFixedParameters().getAlphaPerc2(),
				data.getFixedParameters().getCorrCoeffRems(),
				data.getFixedParameters().getCorrCoeffSems()
			};

			delta_3_value = (delta.length > 2) ? delta[2] : -1;

			MWNumericArray NumKan = new MWNumericArray(Double.valueOf(sampleSource.getChannelCount()), MWClassID.DOUBLE);

//			[delta_thr,alpha_thr,spindle_thr,EMG_tone]
			result = solver.stager_wrapper(4, folRob, data.getSignalPath(),
			                               book, delta, spindle, alpha, theta, KC, toneEMG, MTeeg, eeg, emg,
			                               MTemg, MTemg_tone, defl_rems, defl_sems, ukryte, primaryTags,rules, CElec, epochSIZE, Fs, NumKan);

			logger.debug("Output from stager2 " + result[0]);

			tracker.tick(0);

		} catch (MWException e) {
			throw new ComputationException("Stager faild", e);
		} catch (IOException ex) {
			logger.error("Failed to create data file", ex);


			throw new ComputationException(ex);
		} catch (BookFormatException e) {
			logger.error("Failed to read data book file", e);
			throw new ComputationException(e);
		}

		return result;
	}

	@Override
	public Object doComputation(Object oData, MethodExecutionTracker tracker) throws ComputationException {

		StagerData data = (StagerData) oData;

		tracker.setMessage(new ResolvableString("stagerMethod.message.staging"));

		Object[] result;

		try {
			result = runStagerML(data, tracker);
		} catch (Exception e) {
			throw new ComputationException("Sleep staging failed",  e);
		}

		StagerResult stagerResult = new StagerResult();

//		[delta_thr,alpha_thr,spindle_thr,EMG_tone]
		stagerResult.setDeltaThr(((MWNumericArray) result[0]).getDouble(1));
		stagerResult.setAlphaThr(((MWNumericArray) result[1]).getDouble(1));
		stagerResult.setSpindleThr(((MWNumericArray) result[2]).getDouble(1));
		stagerResult.setEmgTone(((MWNumericArray) result[3]).getDouble(1));

		{
			File bookFile = new File(data.getParameters().getBookFilePath());
			File resultFile = new File(folRob,
			                           Util.getFileNameWithoutExtension(bookFile) +
			                           "_hypnos_a" +
			                           String.format("%3.2f", new Object[] { delta_3_value }) +
			                           ".tag"
			                          );

			logger.info("Expected result file path: "+resultFile.getAbsolutePath());

			stagerResult.setTagFile(resultFile);
		}

		return stagerResult;
	}

	@Override
	public void validate(Object dataObj, Errors errors) {
		super.validate(dataObj, errors);
		if (!errors.hasErrors()) {
			StagerData data = (StagerData) dataObj;
			data.validate(errors);
		}
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Object createData() {
		return new StagerData();
	}

	@Override
	public boolean supportsDataClass(Class<?> clazz) {
		return StagerData.class.isAssignableFrom(clazz);
	}

	@Override
	public Class<?> getResultClass() {
		return StagerResult.class;
	}

	@Override
	public String getUID() {
		return UID;
	}

	@Override
	public int[] getVersion() {
		return VERSION;
	}

	@Override
	public int getTickerCount() {
		return 2;
	}

	@Override
	public String getTickerLabel(MessageSourceAccessor messageSource, int ticker) {

		if (ticker == 0) {
			return messageSource.getMessage("stagerMethod.stepTicker");
		} else if (ticker == 1) {
			return messageSource.getMessage("stagerMethod.progressTicker");
		} else {
			throw new IndexOutOfBoundsException("No ticker [" + ticker + "]");
		}

	}

	public XStream getStreamer() {
		return streamer;
	}

	public void setStreamer(XStream streamer) {
		this.streamer = streamer;
	}

	public boolean[] getForceNotTrackable() {
		return notTrackable;
	}

}

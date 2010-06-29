/* ArtifactMethod.java created 2007-11-01
 *
 */

package org.signalml.method.artifact;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.transform.TransformerFactory;

import org.apache.log4j.Logger;
import org.signalml.app.model.SignalExportDescriptor;
import org.signalml.app.util.XMLUtils;
import org.signalml.app.view.signal.SampleSourceUtils;
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
import org.signalml.method.ProgressMonitor;
import org.signalml.method.TrackableMethod;
import org.signalml.method.iterator.IterableMethod;
import org.signalml.method.iterator.IterableParameter;
import org.signalml.util.ResolvableString;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

import artifact_mbfj.Artifact_mbfj;

import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import com.mathworks.toolbox.javabuilder.MWStructArray;
import com.thoughtworks.xstream.XStream;

/**
 * ArtifactMethod
 *
 * @author Oskar Kapala &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ArtifactMethod extends AbstractMethod implements TrackableMethod, IterableMethod, InitializingMethod, DisposableMethod {

	protected static final Logger logger = Logger.getLogger(ArtifactMethod.class);

	private static final String UID = "a3530db2-cfa5-4eed-be1f-dafc81ee4995";
	private static final String NAME = "artifact";
	private static final int[] VERSION = new int[] { 4, 0 };

	private RawSignalWriter rawSignalWriter = new RawSignalWriter();

	private Artifact_mbfj solver = null;

	private XStream streamer;

	@Override
	public final void initialize() throws SignalMLException {

		String transformerFactoryClassName = TransformerFactory.newInstance().getClass().getName();
		logger.debug("Default class name [" + transformerFactoryClassName + "]");

		try {
			this.solver = new Artifact_mbfj();
		} catch (MWException e) {
			logger.warn("Couldn't initialize Artifact, this functionality will be broken", e);
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

	private final boolean runArtifactML(ArtifactData data, final MethodExecutionTracker tracker)
	throws ComputationException {

		ProgressMonitor progressMonitor = null;

		if (solver == null) {
			throw new ComputationException("Artifact solver is not avaible");
		}

		String projectPath = data.getProjectPath();
		String pacjent = data.getPatientName();

		int [] wektorProcTmp = data.getParameters().getChosenArtifactTypes();
		MWNumericArray wektorProc = new MWNumericArray(wektorProcTmp , MWClassID.DOUBLE);

		String[] eegdNames = { "Header", "Footer", "NumOfSamples", "Fs", "Format", "NumKan", "EEGChannelsVector" };
		MWStructArray eegd = new MWStructArray(1, 1, eegdNames);

		MultichannelSampleSource sampleSource = data.getSampleSource();

		int sampleCount = SampleSourceUtils.getMinSampleCount(sampleSource);
		int channelCount = sampleSource.getChannelCount();

		eegd.set("Header", 1, new MWNumericArray(Double.valueOf(0), MWClassID.DOUBLE));
		eegd.set("Footer", 1, new MWNumericArray(Double.valueOf(0), MWClassID.DOUBLE));

		eegd.set("NumOfSamples", 1, new MWNumericArray(Double.valueOf(sampleCount), MWClassID.DOUBLE));

		eegd.set("Fs", 1, new MWNumericArray(Double.valueOf(sampleSource.getSamplingFrequency()), MWClassID.DOUBLE));

		// Signal format; now is fixed
		eegd.set("Format", 1, "float");

		eegd.set("NumKan", 1, new MWNumericArray(Double.valueOf(channelCount), MWClassID.DOUBLE));

		int[] eegChannelInt = new int[data.getEegChannels().size()];
		int i = 0;
		for (int eegChannel : data.getEegChannels()) {
			eegChannelInt[i++] = eegChannel + 1;
		}
		eegd.set("EEGChannelsVector", 1, eegChannelInt);


		if (!data.isProcessedProject()) {

			synchronized (tracker) {
				tracker.setMessage(new ResolvableString("artifactMethod.message.creatingFiles"));
				tracker.setTickerLimit(0, 3);
				tracker.setTicker(0, 0);
			}

			createIntermediateFiles(data);

			synchronized (tracker) {
				tracker.setMessage(new ResolvableString("artifactMethod.message.writingSignalFile"));
				tracker.setTickerLimit(1, sampleCount);
				tracker.setTicker(1,0);
			}

			SignalWriterMonitor signalWriterMonitor = new AbstractSignalWriterMonitor() {
				@Override
				public void setProcessedSampleCount(int processedCount) {
					tracker.setTicker(1, processedCount);
				}

				@Override
				public boolean isRequestingAbort() {
					// local abort flag is ignored
					return tracker.isRequestingAbort();
				}
			};

			File signalFile = (new File(data.getSignalPath())).getAbsoluteFile();

			SignalExportDescriptor signalExportDescriptor = new SignalExportDescriptor();
			signalExportDescriptor.setSampleType(RawSignalSampleType.FLOAT);
			signalExportDescriptor.setByteOrder(RawSignalByteOrder.LITTLE_ENDIAN);
			signalExportDescriptor.setNormalize(false);

			try {
				rawSignalWriter.writeSignal(signalFile, sampleSource, signalExportDescriptor, signalWriterMonitor);
			} catch (IOException ex) {
				logger.error("Failed to create data file", ex);
				throw new ComputationException(ex);
			}

			if (tracker.isRequestingAbort()) {
				return false;
			}

			synchronized (tracker) {
				tracker.tick(0);
				tracker.setMessage(new ResolvableString("artifactMethod.message.processing"));
				tracker.setTickerLimit(1, 1);
				tracker.setTicker(1, 0);
			}

			String plik = data.getSignalPath();

			String[] CElecNames = {"F7", "F8", "T3", "T4", "EOGP", "EOGL", "Fp1", "Fp2", "F3", "F4", "C3", "C4", "EKG"};
			MWStructArray CElec = new MWStructArray(1, 1, CElecNames);
			double value;
			for (String elecName : CElecNames) {
				if (elecName.compareTo("EKG") == 0) {
					value = (double) data.getKeyChannelMap().get("ECG") + 1;
				} else {
					value = (double) data.getKeyChannelMap().get(elecName) + 1;
				}
				CElec.set(elecName, 1, new MWNumericArray(Double.valueOf(value), MWClassID.DOUBLE));
			}

			MWNumericArray powerFreq = new MWNumericArray(Double.valueOf(data.getParameters().getPowerGridFrequency()), MWClassID.DOUBLE);

			File progresLogFile = new File((new File(projectPath, pacjent)).getAbsolutePath(), "progress.txt");
			File stopFile = new File((new File(projectPath, pacjent)).getAbsolutePath(), pacjent+".stop");

			progressMonitor = new ProgressMonitor(progresLogFile, stopFile, tracker);
			Thread monitor = new Thread(progressMonitor);

			try {

				data.setProcessedProject(false);

				monitor.start();

				Object[] result = solver.procesuj_wrapper(1, plik, eegd, wektorProc, CElec, projectPath, pacjent, powerFreq);

				Integer returnValue = ((MWNumericArray) result[0]).getInt(1);
				if (returnValue.intValue() == 0) {
					data.setProcessedProject(true);
				}

			} catch (MWException e) {
				throw new ComputationException("Procesuj failed", e);
			} finally {
				progressMonitor.shutdown();
			}

			try {
				XMLUtils.objectToFile(data, data.getProjectFile(), streamer);
			} catch (IOException ex) {
				logger.error("Failed to write project", ex);
				throw new ComputationException(ex);
			}

			// mark signal file for deletion on exit
			signalFile.deleteOnExit();

			tracker.tick(0);

		} else {
			synchronized (tracker) {
				tracker.setTickerLimit(0, 1);
				tracker.setTicker(0,0);
			}
		}

		if (tracker.isRequestingAbort()) {
			return false;
		}

		if (data.isProcessedProject()) {
			synchronized (tracker) {
				tracker.setMessage(new ResolvableString("artifactMethod.message.tagging"));
				tracker.setTickerLimit(1, 1); // TODO change
				tracker.setTicker(1, 0);
			}

			MWNumericArray wylKanTab = new MWNumericArray(data.getExcludedChannels(), MWClassID.DOUBLE);

			float[] wekAnalSample = data.getParameters().getSensitivities();
			MWNumericArray wekAnal = new MWNumericArray(wekAnalSample, MWClassID.DOUBLE);

			try {
				String referenceTag = "";
				String optimizationType = "";


				solver.taguj_wrapper(1, eegd, wylKanTab, wektorProc, wekAnal, ".tag", projectPath, pacjent, referenceTag, optimizationType);
			} catch (MWException e) {
				throw new ComputationException("Taguj failed",e);
			}

			tracker.tick(0);

		}
		return true;
	}

	@Override
	public Object doComputation(Object oData, final MethodExecutionTracker tracker) throws ComputationException {

		ArtifactData data = (ArtifactData) oData;

		String folRob = data.getProjectPath();
		String pacjent = data.getPatientName();

		// put lock file into work directory
		File lockFile = new File(new File(folRob, pacjent).getAbsoluteFile(), pacjent + ".lock");

		if (lockFile.exists()) {
			throw new ComputationException("Another Articact Detection is already running");
		} else {
			try {
				lockFile.createNewFile();
			} catch (IOException e) {
				throw new ComputationException("Another Articact Detection is already running", e);
			}
		}

		try {
			boolean ok = runArtifactML(data, tracker);
			if (!ok) {
				return null;
			}
		} catch (Exception e) {
			throw new ComputationException("Artifact detection failed",  e);
		}
		finally {
			lockFile.delete();
		}

		tracker.tick(0); // TODO change

		tracker.setMessage(new ResolvableString("artifactMethod.message.finishing"));

		ArtifactResult result = new ArtifactResult();
		File resultFile = new File(new File(folRob, pacjent).getAbsoluteFile(), pacjent + ".tag");
		result.setTagFile(resultFile);

		tracker.setMessage(new ResolvableString("artifactMethod.message.finished"));

		return result;

	}

	private void createIntermediateFiles(ArtifactData data) throws ComputationException {

		String singalPath = data.getSignalPath();
		File signalFile = null;
		if (singalPath != null) {
			signalFile = (new File(singalPath)).getAbsoluteFile();
		}
		if (signalFile == null) {
			try {
				signalFile = File.createTempFile("signalml_artifact_", ".dat", new File(data.getProjectPath(), data.getPatientName()));
				signalFile.deleteOnExit();
			} catch (IOException ex) {
				logger.error("Failed to create signal file", ex);
				throw new ComputationException(ex);
			}
			data.setSignalPath(signalFile.getAbsolutePath());
		}

	}

	@Override
	public void validate(Object dataObj, Errors errors) {
		super.validate(dataObj, errors);
		if (!errors.hasErrors()) {
			ArtifactData data = (ArtifactData) dataObj;
			data.validate(errors);
		}
	}

	@Override
	public String getUID() {
		return UID;
	}

	@Override
	public String getName() {
		return NAME;
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
			return messageSource.getMessage("artifactMethod.stepTicker");
		} else if (ticker == 1) {
			return messageSource.getMessage("artifactMethod.progressTicker");
		} else {
			throw new IndexOutOfBoundsException("No ticker [" + ticker + "]");
		}

	}

	@Override
	public Object createData() {
		return new ArtifactData();
	}

	@Override
	public Class<?> getResultClass() {
		return ArtifactResult.class;
	}

	@Override
	public boolean supportsDataClass(Class<?> clazz) {
		return ArtifactData.class.isAssignableFrom(clazz);
	}

	@Override
	public IterableParameter[] getIterableParameters(Object dataObj) {

		ArtifactData data = (ArtifactData) dataObj;

		LinkedList<IterableSensitivity> list = new LinkedList<IterableSensitivity>();

		ArtifactParameters parameters = data.getParameters();
		int[] chosenArtifactTypes = parameters.getChosenArtifactTypes();

		for (int i = 0; i < chosenArtifactTypes.length; i++) {
			if (chosenArtifactTypes[i] != 0) {
				list.add(new IterableSensitivity(parameters, ArtifactType.values()[i]));
			}
		}

		IterableParameter[] arr = new IterableParameter[list.size()];
		list.toArray(arr);
		return arr;

	}

	@Override
	public Object digestIterationResult(int iteration, Object resultObj) {

		ArtifactResult result = (ArtifactResult) resultObj;

		File tagFile = result.getTagFile();

		File iterationTagFile = new File(tagFile.getParentFile(), "iteration_" + iteration + "_" + tagFile.getName());

		tagFile.renameTo(iterationTagFile);
		result.setTagFile(iterationTagFile);

		return result;

	}

	public XStream getStreamer() {
		return streamer;
	}

	public void setStreamer(XStream streamer) {
		this.streamer = streamer;
	}

}

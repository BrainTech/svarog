package org.signalml.plugin.newartifact.method;

import static org.signalml.plugin.newartifact.NewArtifactPlugin._;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.LinkedList;
import java.util.List;

import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.method.ComputationException;
import org.signalml.method.MethodExecutionTracker;
import org.signalml.method.TrackableMethod;
import org.signalml.method.iterator.IterableMethod;
import org.signalml.method.iterator.IterableParameter;
import org.signalml.plugin.data.PluginConfigForMethod;
import org.signalml.plugin.exception.PluginException;
import org.signalml.plugin.method.PluginAbstractMethod;
import org.signalml.plugin.newartifact.NewArtifactPlugin;
import org.signalml.plugin.newartifact.data.NewArtifactConstants;
import org.signalml.plugin.newartifact.data.NewArtifactData;
import org.signalml.plugin.newartifact.data.NewArtifactParameters;
import org.signalml.plugin.newartifact.data.NewArtifactResult;
import org.signalml.plugin.newartifact.data.NewArtifactType;
import org.signalml.plugin.newartifact.data.NewIterableSensitivity;
import org.signalml.plugin.newartifact.data.mgr.NewArtifactMgrData;
import org.signalml.plugin.newartifact.logic.mgr.NewArtifactComputationMgr;
import org.signalml.plugin.tool.PluginResourceRepository;
import org.springframework.validation.Errors;

public class NewArtifactMethod extends PluginAbstractMethod implements
	TrackableMethod, IterableMethod {

	private static final String UID = "a3530db2-cfa5-4eed-be1f-dafc81ee4225";

	private static final int[] VERSION = new int[] { 1, 0 };

	private final int BLOCK_LENGTH_IN_SECONDS = 4;
	private final int SMALL_BLOCK_LENGTH_IN_SECONDS = 1;
	private final int TAIL_LENGTH_IN_SECONDS = 2;
	private final float SMALL_TAIL_LENGTH_IN_SECONDS = 0.25f;
	private final float SLOPE_LENGTH_IN_SECONDS = 0.033f;

	@Override
	public Object doComputation(Object data, MethodExecutionTracker tracker)
	throws ComputationException {
		NewArtifactData artifactData = (NewArtifactData) data;

		File patientTagFile = new File(new File(artifactData.getProjectPath(),
												artifactData.getPatientName()).getAbsolutePath(),
									   artifactData.getPatientName() + ".lock");

		FileChannel channel;
		try {
			channel = new RandomAccessFile(patientTagFile, "rw").getChannel();
		} catch (FileNotFoundException e) {
			throw new ComputationException(e);
		}

		FileLock lock = null;
		try {
			lock = channel.tryLock();
			if (lock == null) {
				throw new ComputationException(
					"Another Articact Detection is already running");
			}

			NewArtifactComputationMgr mgr = new NewArtifactComputationMgr();

			return mgr.compute(
					   new NewArtifactMgrData(artifactData, this
											  .getArtifactConstants(artifactData)), tracker);

		} catch (IOException e) {
			throw new ComputationException(e);
		} finally {
			if (lock != null) {
				try {
					lock.release();
					channel.close();
					patientTagFile.delete();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}

	private NewArtifactConstants getArtifactConstants(
		NewArtifactData artifactData) {
		MultichannelSampleSource sampleSource = artifactData.getSampleSource();
		return new NewArtifactConstants(sampleSource.getChannelCount(),
										sampleSource.getSamplingFrequency(), artifactData
										.getParameters().getPowerGridFrequency(),
										this.BLOCK_LENGTH_IN_SECONDS,
										this.SMALL_BLOCK_LENGTH_IN_SECONDS,
										this.TAIL_LENGTH_IN_SECONDS, this.SMALL_TAIL_LENGTH_IN_SECONDS,
										this.SLOPE_LENGTH_IN_SECONDS);
	}

	@Override
	public int getTickerCount() {
		return 1;
	}

	@Override
	public void validate(Object dataObj, Errors errors) {
		super.validate(dataObj, errors);
		if (!errors.hasErrors()) {
			NewArtifactData data = (NewArtifactData) dataObj;
			data.validate(errors);
		}
	}

	@Override
	public String getTickerLabel(int ticker) {
		if (ticker == 0) {
			return _("Artifact detection");
		} else {
			throw new IndexOutOfBoundsException("No ticker [" + ticker + "]");
		}
	}

	@Override
	public IterableParameter[] getIterableParameters(Object dataObj) {

		NewArtifactData data = (NewArtifactData) dataObj;

		List<NewIterableSensitivity> list = new LinkedList<NewIterableSensitivity>();

		NewArtifactParameters parameters = data.getParameters();
		int[] chosenArtifactTypes = parameters.getChosenArtifactTypes();

		for (int i = 0; i < chosenArtifactTypes.length; i++) {
			if (chosenArtifactTypes[i] != 0) {
				list.add(new NewIterableSensitivity(parameters, NewArtifactType
													.values()[i]));
			}
		}

		IterableParameter[] arr = new IterableParameter[list.size()];
		list.toArray(arr);
		return arr;

	}

	@Override
	public Object digestIterationResult(int iteration, Object resultObj) {

		NewArtifactResult result = (NewArtifactResult) resultObj;

		File tagFile = result.getTagFile();

		File iterationTagFile = new File(tagFile.getParentFile(), "iteration_"
										 + iteration + "_" + tagFile.getName());

		tagFile.renameTo(iterationTagFile);
		result.setTagFile(iterationTagFile);

		return result;

	}

	@Override
	public Object createData() {
		return null;
	}

	/*
	 * @Override public Object createData(PluginMethodManager manager) {
	 * ExportedSignalDocument signalDocument; try { signalDocument =
	 * manager.getSvarogAccess() .getSignalAccess().getActiveSignalDocument(); }
	 * catch (NoActiveObjectException e) { signalDocument = null; }
	 *
	 * if (signalDocument == null) {
	 * OptionPane.showNoActiveSignal(manager.getSvarogAccess()
	 * .getGUIAccess().getManager().getOptionPaneParent()); return null; }
	 *
	 * NewArtifactApplicationData data = new NewArtifactApplicationData();
	 * data.setSignalDocument(signalDocument);
	 * data.setSignalAccess(manager.getSvarogAccess().getSignalAccess());
	 *
	 * return data; }
	 */

	@Override
	public String getName() {
		try {
			return ((PluginConfigForMethod) PluginResourceRepository
					.GetResource("config", NewArtifactPlugin.class)).getMethodConfig().getMethodName();
		} catch (PluginException e) {
			return "";
		}
	}

	@Override
	public Class<?> getResultClass() {
		return NewArtifactResult.class;
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
	public boolean supportsDataClass(Class<?> clazz) {
		return NewArtifactData.class.isAssignableFrom(clazz);
	}
}

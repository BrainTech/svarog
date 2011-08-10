package org.signalml.plugin.newartifact.logic.mgr;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.plugin.exception.PluginThreadRuntimeException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.io.IPluginDataSourceReader;
import org.signalml.plugin.newartifact.data.NewArtifactAlgorithmWorkerData;
import org.signalml.plugin.newartifact.io.INewArtifactAlgorithmWriter;
import org.signalml.plugin.newartifact.logic.algorithm.INewArtifactAlgorithm;
import org.signalml.plugin.newartifact.logic.algorithm.NewArtifactAlgorithmData;

public class NewArtifactAlgorithmWorker implements Runnable {
	protected static final Logger logger = Logger
					       .getLogger(NewArtifactAlgorithmWorker.class);

	private final NewArtifactAlgorithmFactory algorithmFactory;
	private final IPluginDataSourceReader dataSource;
	private final INewArtifactAlgorithmWriter resultWriter;
	private final NewArtifactAlgorithmWorkerData workerData;

	public NewArtifactAlgorithmWorker(
		IPluginDataSourceReader dataSource,
		NewArtifactAlgorithmFactory algorithmFactory,
		INewArtifactAlgorithmWriter resultWriter,
		NewArtifactAlgorithmWorkerData workerData) {
		this.algorithmFactory = algorithmFactory;
		this.dataSource = dataSource;
		this.resultWriter = resultWriter;
		this.workerData = workerData;
	}

	@Override
	public void run() {
		try {
			INewArtifactAlgorithm algorithm = this.algorithmFactory.createAlgorithm();
			final int channelCount = this.workerData.constants.channelCount;
			final int blockLength = this.workerData.constants.getBlockLengthWithPadding();

			double buffer[][] = new double[channelCount][blockLength];
			NewArtifactAlgorithmData data =
				new NewArtifactAlgorithmData(this.workerData.constants,
							     this.workerData.artifactData.getParameters(),
							     this.workerData.artifactData.getKeyChannelMap(),
							     buffer);

			this.resultWriter.write(algorithm.computeHead(data));
			while (this.dataSource.hasMoreSamples()) {
				this.dataSource.getSample(buffer);
				this.resultWriter.write(algorithm.compute(data));
			}
			this.resultWriter.write(algorithm.computeTail(data));
		} catch (IOException e) {
			throw new PluginThreadRuntimeException(e);
		} catch (SignalMLException e) {
			throw new PluginThreadRuntimeException(e);
		} catch (InterruptedException e) {
			logger.warn("Worker thread interrupted", e);
			return;
		}
	}
}

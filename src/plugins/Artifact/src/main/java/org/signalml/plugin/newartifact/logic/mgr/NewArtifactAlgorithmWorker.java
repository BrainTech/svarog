package org.signalml.plugin.newartifact.logic.mgr;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.io.IPluginDataSourceReader;
import org.signalml.plugin.newartifact.data.NewArtifactAlgorithmWorkerData;
import org.signalml.plugin.newartifact.io.INewArtifactAlgorithmWriter;
import org.signalml.plugin.newartifact.logic.algorithm.INewArtifactAlgorithm;
import org.signalml.plugin.newartifact.logic.algorithm.NewArtifactAlgorithmData;
import org.signalml.plugin.newartifact.method.NewArtifactMethod;

public class NewArtifactAlgorithmWorker extends Thread {
	protected static final Logger logger = Logger
					       .getLogger(NewArtifactMethod.class);

	private final NewArtifactAlgorithmFactory algorithmFactory;
	private final IPluginDataSourceReader dataSource;
	private final INewArtifactAlgorithmWriter resultWriter;
	private final NewArtifactAlgorithmWorkerData workerData;

	public NewArtifactAlgorithmWorker(
		IPluginDataSourceReader dataSource,
		NewArtifactAlgorithmFactory algorithmFactory,
		INewArtifactAlgorithmWriter resultWriter,
		NewArtifactAlgorithmWorkerData workerData) {
		super();
		this.setName("ArtifactAlgorithmWorker");
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
			while (dataSource.hasMoreSamples()) {
				dataSource.getSample(buffer);
				this.resultWriter.write(algorithm.compute(data));
			}
			this.resultWriter.write(algorithm.computeTail(data));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SignalMLException e) {
			e.printStackTrace();
		}
	}
}

package org.signalml.plugin.newartifact.io;

import java.util.Arrays;

import org.signalml.domain.signal.samplesource.MultichannelSampleSource;
import org.signalml.plugin.newartifact.data.INewArtifactSignalReaderWorkerData;
import org.signalml.plugin.newartifact.data.NewArtifactConstants;


public class NewArtifactSignalReaderWorker implements Runnable {
	private INewArtifactSignalReaderWorkerData data;

	public NewArtifactSignalReaderWorker(INewArtifactSignalReaderWorkerData data) {
		this.data = data;
	}

	@Override
	public void run() {
		boolean isInterrupted = false;
		try {
			MultichannelSampleSource source = this.data.getSignalSource();
			NewArtifactConstants constants = this.data.getArtifactConstants();
			int blockLengthWithPadding = constants.getBlockLengthWithPadding();
			int tailLength = constants.getPaddingLength();
			int blockLength = constants.getBlockLength();
			int channelCount = source.getChannelCount();
			int sampleCount[] = new int[channelCount];
			int toRead[] = new int[channelCount];
			int start = blockLength - tailLength;
			int channelsLeft = channelCount;


			for (int i = 0; i < channelCount; ++i) {
				sampleCount[i] = source.getSampleCount(i);
				toRead[i] = blockLengthWithPadding;
			}

			while (channelsLeft > 0) {
				double buffer[][] = this.data.getWritableBuffer();
				if (buffer == null || buffer.length == 0) {
					return;
				}

				for (int i = 0; i < channelCount; ++i) {
					if (toRead[i] > 0) {
						source.getSamples(i, buffer[i], start, toRead[i], 0);
					} else {
						Arrays.fill(buffer[i], 0.0);
					}
				}
				this.data.markBufferAsReady(buffer);
				start += blockLength;

				for (int i = 0; i < channelCount; ++i) {
					if (toRead[i] > 0 && start + blockLength + toRead[i] > sampleCount[i]) {
						toRead[i] = 0;
						channelsLeft--;
					}
				}
			}
		} catch (InterruptedException e) {
			isInterrupted = true;
		} finally {
			if (!isInterrupted) {
				try {
					this.data.finalizeBuffers();
				} catch (InterruptedException e) {
					//do nothing
				}
			}
		}
	}
}

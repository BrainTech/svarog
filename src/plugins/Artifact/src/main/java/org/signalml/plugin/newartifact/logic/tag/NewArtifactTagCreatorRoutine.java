package org.signalml.plugin.newartifact.logic.tag;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;

import org.signalml.method.ComputationException;
import org.signalml.plugin.io.IPluginTagWriter;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagData;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagResult;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagRoutineData;
import org.signalml.plugin.newartifact.io.INewArtifactDataReader;
import org.signalml.plugin.newartifact.logic.tag.creators.INewArtifactTagCreator;

public class NewArtifactTagCreatorRoutine implements
	Callable<NewArtifactTagResult> {

	private final NewArtifactTagRoutineData data;
	private final INewArtifactDataReader reader;
	private final INewArtifactTagCreator tagCreator;
	private final IPluginTagWriter writer;

	public NewArtifactTagCreatorRoutine(NewArtifactTagRoutineData data,
					    INewArtifactDataReader reader, INewArtifactTagCreator tagCreator,
					    IPluginTagWriter writer) {
		this.data = data;
		this.reader = reader;
		this.tagCreator = tagCreator;
		this.writer = writer;

	}

	@Override
	public NewArtifactTagResult call() throws Exception {
		double source[][];
		try {
			source = this.reader.read();
		} catch (IOException e) {
			throw new ComputationException(e);
		}
		NewArtifactTagResult result = this.tagCreator
					      .tag(new NewArtifactTagData(source, this.data.constants,
							      this.data.parameters, this.data.eegChannels,
							      this.data.excludedChannels));
		this.writer.writeTags(Arrays.asList(result.tagGroup));
		return result;
	}

}

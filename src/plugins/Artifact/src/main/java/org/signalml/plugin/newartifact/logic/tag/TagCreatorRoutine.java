package org.signalml.plugin.newartifact.logic.tag;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.signalml.method.ComputationException;
import org.signalml.plugin.newartifact.io.INewArtifactDataReader;
import org.signalml.plugin.newartifact.io.INewArtifactTagWriter;
import org.signalml.plugin.newartifact.logic.tag.creators.INewArtifactTagCreator;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagData;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagResult;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagRoutineData;

public class TagCreatorRoutine implements Callable<NewArtifactTagResult> {

	private final NewArtifactTagRoutineData data;
	private final INewArtifactDataReader reader;
	private final INewArtifactTagCreator tagCreator;
	private final INewArtifactTagWriter writer;

	public TagCreatorRoutine(NewArtifactTagRoutineData data,
				 INewArtifactDataReader reader,
				 INewArtifactTagCreator tagCreator,
				 INewArtifactTagWriter writer) {
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
		NewArtifactTagResult result = this.tagCreator.tag(new NewArtifactTagData(source,
					      this.data.constants,
					      this.data.parameters,
					      this.data.eegChannels,
					      this.data.excludedChannels));
		this.writer.writeTag(result);
		return result;
	}

}

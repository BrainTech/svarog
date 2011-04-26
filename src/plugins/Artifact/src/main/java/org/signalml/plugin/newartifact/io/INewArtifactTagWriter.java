package org.signalml.plugin.newartifact.io;

import java.io.IOException;

import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagResult;

public interface INewArtifactTagWriter {

	public static int MAX_TAG_COUNT = 16380;

	void writeTag(NewArtifactTagResult result) throws IOException, SignalMLException;

}

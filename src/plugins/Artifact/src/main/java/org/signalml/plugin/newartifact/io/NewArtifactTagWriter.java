package org.signalml.plugin.newartifact.io;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.KeyStroke;

import org.signalml.app.document.TagDocument;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.newartifact.data.NewArtifactTagWriterConfig;
import org.signalml.plugin.newartifact.data.tag.NewArtifactTagResult;

public class NewArtifactTagWriter implements INewArtifactTagWriter {

	private final File outputFile;
	private final NewArtifactTagWriterConfig config;

	private final static Color FILL_COLOR = new Color(0xc0c0c0);
	private final static Color OUTLINE_COLOR = new Color(0x808080);

	public NewArtifactTagWriter(File outputFile,
				    NewArtifactTagWriterConfig config) {
		this.outputFile = outputFile;
		this.config = config;
	}

	@Override
	public void writeTag(NewArtifactTagResult result) throws IOException,
		SignalMLException {
		TagStyle style = new TagStyle(SignalSelectionType.BLOCK, result.name,
					      result.description, FILL_COLOR, OUTLINE_COLOR, 1,
					      null, //solid
					      this.createKeyStroke(result),
					      false);
		LinkedHashMap<String, TagStyle> styles = new LinkedHashMap<String, TagStyle>();
		styles.put(result.name, style);

		Collection<Tag> blocks = this.createBlockTags(result, style);
		TreeSet<Tag> tags = new TreeSet<Tag>(blocks);

		StyledTagSet tagSet = new StyledTagSet(styles, tags,
						       this.config.pageSize,
						       (int)(this.config.pageSize / result.tagStretchFactor));
		TagDocument document = new TagDocument(tagSet);
		document.setBackingFile(this.outputFile);
		document.saveDocument();
	}

	private KeyStroke createKeyStroke(NewArtifactTagResult result) {
		String name = result.name;
		if (name == null) {
			return null;
		} else {
			return name.length() == 1 ? KeyStroke.getKeyStroke(name.charAt(0)) : KeyStroke.getKeyStroke("typed " + name);
		}
	}

	private Collection<Tag> createBlockTags(NewArtifactTagResult result,
						TagStyle style) {
		List<Tag> l = new LinkedList<Tag>();
		for (Integer j : result.sortedTags) {
			l.add(new Tag(style, j * result.tagStretchFactor,
				      result.tagStretchFactor));
		}
		return l;
	}

}

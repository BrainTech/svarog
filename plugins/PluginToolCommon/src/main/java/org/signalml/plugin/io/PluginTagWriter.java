package org.signalml.plugin.io;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.KeyStroke;

import org.signalml.app.document.TagDocument;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.domain.tag.TagStyles;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.data.io.PluginTagWriterConfig;
import org.signalml.plugin.data.tag.IPluginTagDef;
import org.signalml.plugin.data.tag.PluginTagDefRangeComparator;
import org.signalml.plugin.data.tag.PluginTagGroup;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;

public class PluginTagWriter implements IPluginTagWriter {

	private final static Color FILL_COLOR = new Color(0xc0c0c0);
	private final static Color OUTLINE_COLOR = new Color(0x808080);

	private final File outputFile;
	private final PluginTagWriterConfig config;

	public PluginTagWriter(File outputFile, PluginTagWriterConfig config) {
		this.outputFile = outputFile;
		this.config = config;
	}

	@Override
	public void writeTags(Collection<PluginTagGroup> tags) throws IOException,
		SignalMLException {

		TagStyles styles = this.createStyles(tags);
		TreeSet<Tag> documentTags = new TreeSet<Tag>(this.createTags(tags, styles));

		float pageSize = this.config.pageSize;
		float stretchFactor = -1;

		for (PluginTagGroup tagGroup : tags) {
			if (stretchFactor == -1) {
				stretchFactor = tagGroup.stretchFactor;
			} else {
				if (stretchFactor != tagGroup.stretchFactor) {
					throw new SanityCheckException("Inconsistent stretchFactor " + tagGroup + " (should be: " + stretchFactor + " )");
				}
			}

		}

		StyledTagSet tagSet = new StyledTagSet(styles, documentTags, pageSize,
						       (int)(pageSize / stretchFactor));
		TagDocument document = new TagDocument(tagSet);
		document.setBackingFile(this.outputFile);
		document.saveDocument();
	}

	private TagStyles createStyles(
		Collection<PluginTagGroup> tags) {
		TagStyles styles = new TagStyles();

		for (PluginTagGroup tagGroup : tags) {
			TagStyle style = new TagStyle(SignalSelectionType.typeByName(tagGroup.type.getName()),
						      tagGroup.name, tagGroup.description, FILL_COLOR,
						      OUTLINE_COLOR, 1, null, // solid
						      this.createKeyStroke(tagGroup), false);
			styles.addStyle(style);
		}

		return styles;
	}

	private KeyStroke createKeyStroke(PluginTagGroup tagGroup) {
		String name = tagGroup.name;
		if (name == null) {
			return null;
		} else {
			return name.length() == 1 ? KeyStroke.getKeyStroke(name.charAt(0))
			       : KeyStroke.getKeyStroke("typed " + name);
		}
	}

	private Collection<Tag> createTags(Collection<PluginTagGroup> tags,
					   TagStyles tagStyles) {
		List<Tag> l = new LinkedList<Tag>();
		for (PluginTagGroup tagGroup : tags) {
			TagStyle style = tagStyles.getStyle(tagGroup.name);
			if (style == null) {
				style = TagStyle.getDefault();
			}

			TreeSet<IPluginTagDef> sortedTags = new TreeSet<IPluginTagDef>(
				new PluginTagDefRangeComparator());
			sortedTags.addAll(tagGroup.tags);

			for (IPluginTagDef tag : sortedTags) {
				l.add(new Tag(style, (float) tag.getOffset(), (float) tag
					      .getLength(), tag.getChannel()));
			}
		}
		return l;
	}

}

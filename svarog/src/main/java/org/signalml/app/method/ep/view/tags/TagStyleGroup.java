package org.signalml.app.method.ep.view.tags;

import java.util.ArrayList;
import java.util.List;

import org.signalml.plugin.export.signal.TagStyle;

public class TagStyleGroup {

	private List<TagStyle> tagStyles = new ArrayList<TagStyle>();

	public TagStyleGroup() {
	}

	public void addTagStyle(TagStyle tagStyle) {
		if (!contains(tagStyle))
			tagStyles.add(tagStyle);
	}

	protected boolean contains(TagStyle tagStyle) {
		for (TagStyle addedTagStyle: tagStyles) {
			if (addedTagStyle.equals(tagStyle))
				return true;
		}
		return false;
	}

	public List<TagStyle> getTagStyles() {
		return tagStyles;
	}

	public int getNumberOfTagStyles() {
		return tagStyles.size();
	}

}

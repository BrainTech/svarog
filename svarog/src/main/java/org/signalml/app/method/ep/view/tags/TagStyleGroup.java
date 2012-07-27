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

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TagStyleGroup))
			return false;

		TagStyleGroup otherGroup = (TagStyleGroup) obj;
		if (otherGroup.getNumberOfTagStyles() != getNumberOfTagStyles())
			return false;

		for (int i = 0; i < otherGroup.getNumberOfTagStyles(); i++) {
			if (!otherGroup.getTagStyles().get(i).equals(getTagStyles().get(i)))
				return false;
		}
		return true;
	}

}

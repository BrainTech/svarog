package org.signalml.app.method.ep.view.tags;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a group of tag style names.
 * (This functionality is needed for evoked potentials
 * averaging.)
 *
 * @author Piotr Szachewicz
 */
@XStreamAlias("tagStyleGroup")
public class TagStyleGroup {

	private List<String> tagStyleNames = new ArrayList<String>();

	public TagStyleGroup() {
	}

	public TagStyleGroup(String tagName) {
		addTagStyle(tagName);
	}

	public void addTagStyle(String tagStyle) {
		if (!contains(tagStyle)) {
			tagStyleNames.add(tagStyle);
		}
	}

	protected boolean contains(String tagStyle) {
		for (String addedTagStyle: tagStyleNames) {
			if (addedTagStyle.equals(tagStyle))
				return true;
		}
		return false;
	}

	public List<String> getTagStyleNames() {
		return tagStyleNames;
	}

	public int getNumberOfTagStyles() {
		return tagStyleNames.size();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TagStyleGroup))
			return false;

		TagStyleGroup otherGroup = (TagStyleGroup) obj;
		if (otherGroup.getNumberOfTagStyles() != getNumberOfTagStyles())
			return false;

		for (int i = 0; i < otherGroup.getNumberOfTagStyles(); i++) {
			if (!otherGroup.getTagStyleNames().get(i).equals(getTagStyleNames().get(i)))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tagStyleNames.size(); i++) {
			sb.append(tagStyleNames.get(i));
			if (i < tagStyleNames.size()-1)
				sb.append(" & ");
		}
		return sb.toString();
	}

}

/* TagStyleEvent.java created 2007-10-01
 *
 */

package org.signalml.domain.tag;

import java.util.EventObject;

import org.signalml.plugin.export.signal.TagStyle;

/**
 * This class represents an event of adding, removing or changing the
 * {@link TagStyle tag style} in a {@link StyledTagSet StyledTagSet}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStyleEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link TagStyle tag style} that has changed
	 */
	private TagStyle tagStyle;

	/**
	 * the index of {@link TagStyle tag style} in an array of tag styles
	 * of a given type (in the {@link StyledTagSet set})
	 */
	private int inTypeIndex;

	/**
	 * Constructor. Creates an event associated with adding, removing or
	 * changing the {@link StyledTagSet set} in a {@link StyledTagSet set}.
	 * @param source a set with which the event is associated
	 * @param tagStyle the tag style that has changed
	 * @param inTypeIndex the index of the style in an array of tag styles
	 * of a given type
	 */
	public TagStyleEvent(Object source, TagStyle tagStyle, int inTypeIndex) {
		super(source);
		this.tagStyle = tagStyle;
		this.inTypeIndex = inTypeIndex;
	}

	/**
	 * Returns the {@link StyledTagSet set} associated with the event.
	 * @return the tag style associated with the event
	 */
	public TagStyle getTagStyle() {
		return tagStyle;
	}

	/**
	 * Returns the index of a style in an array of tag styles of a given
	 * type (in the StyledTagSet)
	 * @return the index of a style in an array of tag styles of a given
	 * type (in the StyledTagSet)
	 */
	public int getInTypeIndex() {
		return inTypeIndex;
	}

}

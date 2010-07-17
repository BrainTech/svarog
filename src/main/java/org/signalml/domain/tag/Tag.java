/* Tag.java created 2007-09-28
 *
 */

package org.signalml.domain.tag;

import java.beans.IntrospectionException;
import java.util.LinkedList;
import java.util.List;

import org.signalml.app.model.ChannelPropertyEditor;
import org.signalml.app.model.LabelledPropertyDescriptor;
import org.signalml.app.model.PropertyProvider;
import org.signalml.domain.signal.SignalSelection;
import org.signalml.domain.signal.SignalSelectionType;
import org.springframework.context.MessageSourceResolvable;

/**
 * This class represents a tagged {@link SignalSelection selection} of a signal.
 * Contains the {@link TagStyle style} and annotation of this selection.
 * Allows to compare tagged selections using its left end, length and the
 * number of channel
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class Tag extends SignalSelection implements Comparable<Tag>, Cloneable, MessageSourceResolvable, PropertyProvider {

    //TODO (can't extend SignalSelection due to strange xstream behaviour)
	private static final long serialVersionUID = 1L;

        /**
         * Style of tagged selection
         */
	private TagStyle style;

        /*
         * String with annotation of a tagged selection
         */
	private String annotation;

        /**
         * Constructor. Creates a tagged selection from a given selection with
         * a given style and annotation
         * @param style the style of the tagged selection
         * @param signalSelection a signal selection to be copied/used
         * @param annotation an annotation of a tagged selection
         */
	public Tag(TagStyle style, SignalSelection signalSelection, String annotation) {
		super(style.getType(), signalSelection.getPosition(), signalSelection.getLength(), signalSelection.getChannel());
		this.style = style;
		this.annotation = annotation;
	}

        /**
         * Constructor. Creates a tagged selection with a given style, starting
         * position, length, annotation and the number of channel
         * @param style the style of the tagged selection
         * @param position the position from which the selection starts
         * @param length the length of the selection
         * @param channel a number of a channel which this selection should
         * concern. CHANNEL_NULL if selection should concern all channels
         * @param annotation an annotation of a tagged selection
         */
	public Tag(TagStyle style, float position, float length, int channel, String annotation) {
		super(style.getType(), position, length, channel);
		this.style = style;
		this.annotation = annotation;
	}

        /**
         * Constructor. Creates a tagged selection with a given style, starting
         * position and length, but without any annotation.
         * Selection will concern all channels
         * @param style the style of the tagged selection
         * @param position the position from which the selection starts
         * @param length the length of the selection
         */
	public Tag(TagStyle style, float position, float length) {
		super((style != null ? style.getType() : SignalSelectionType.CHANNEL), position, length);
		this.style = style;
	}

        /**
         * Constructor. Creates a tagged selection with a given style, starting
         * position, length and the number of channel, but without any annotation.
         * @param style the style of the tagged selection
         * @param position the position from which the selection starts
         * @param length the length of the selection
         * @param channel a number of a channel which this selection should
         */
	public Tag(TagStyle style, float position, float length, int channel) {
		super(style.getType(), position, length, channel);
		this.style = style;
	}

        /**
         * Copy constructor
         * @param tag the tagged selection to be copied
         */
	public Tag(Tag tag) {
		this(tag.style, tag.position, tag.length, tag.channel, tag.annotation);
	}

        /**
         * Returns the style of the current object
         * @return the style of the current object
         */
	public TagStyle getStyle() {
		return style;
	}

        /**
         * Returns the annotation of the current object
         * @return the annotation of the current object
         */
	public String getAnnotation() {
		return annotation;
	}

        /**
         * Sets the annotation of the current object
         * @param annotation  the annotation to be set
         */
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

        /**
         * Returns whether the current object is a marker
         * @return true if the current object is a marker, false otherwise
         */
	public boolean isMarker() {
		return (style != null ? style.isMarker() : false);
	}

        /**
         * Compares the current object to given.
         * To compare uses the starting position, length and the number
         * of channel which this selection concerns (in this order)
         * @param t a tagged selection to be compared with the current object
         * @return $gt 0 if the current object is grater then given
         * $lt 0 if a given object is grater then current
         * 0 if selections are equal
         */
	@Override
	public int compareTo(Tag t) {

		float test = position - t.position;
		if (test == 0) {
			test = length - t.length;
			if (test == 0) {
				test = channel - t.channel;
				if (((int) test) == 0) {
					if (style != null && t.style != null) {
						return style.compareTo(t.style);
					} else {
						return 0;
					}
				}
			}
		}
		return (int) Math.signum(test);

	}

        /**
         * Checks if the current object is equal to given.
         * Uses {@link compareTo() compareTo()}
         * @param obj an object to be compared with current
         * @return true if a given object is equal to current, false otherwise
         */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Tag)) {
			return false;
		}
		return (this.compareTo((Tag) obj) == 0);
	}

        /**
         * Creates a copy of the current object
         * @return a copy of the current object
         */
	@Override
	protected Tag clone() {
		return new Tag(style,position,length,channel,annotation);
	}

	@Override
	public String toString() {
		return style.getName() + ": " + position + " -> " + (position+length);
	}

	@Override
	public Object[] getArguments() {
		return new Object[] {
		               style.getDescriptionOrName(),
		               position,
		               length,
		               position+length,
		               channel
		       };
	}

	@Override
	public String[] getCodes() {
		return new String[] {
		               (channel == CHANNEL_NULL ? "tagWithoutChannel" : "tagWithChannel")
		       };
	}

	@Override
	public String getDefaultMessage() {
		return toString();
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		List<LabelledPropertyDescriptor> list = new LinkedList<LabelledPropertyDescriptor>();

		list.add(new LabelledPropertyDescriptor("property.tag.style", "style", Tag.class, "getStyle", null));
		list.add(new LabelledPropertyDescriptor("property.tag.position", "position", Tag.class));
		list.add(new LabelledPropertyDescriptor("property.tag.length", "length", Tag.class));
		if (channel != CHANNEL_NULL) {
			LabelledPropertyDescriptor channel = new LabelledPropertyDescriptor("property.tag.channel", "channel", Tag.class);
			channel.setPropertyEditorClass(ChannelPropertyEditor.class);
			list.add(channel);
		}
		list.add(new LabelledPropertyDescriptor("property.tag.annotation", "annotation", Tag.class));

		return list;

	}

}

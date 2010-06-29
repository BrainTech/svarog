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

/** Tag
 *
 * (can't extend SignalSelection due to strange xstream behaviour)
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class Tag extends SignalSelection implements Comparable<Tag>, Cloneable, MessageSourceResolvable, PropertyProvider {

	private static final long serialVersionUID = 1L;

	private TagStyle style;
	private String annotation;

	public Tag(TagStyle style, SignalSelection signalSelection, String annotation) {
		super(style.getType(), signalSelection.getPosition(), signalSelection.getLength(), signalSelection.getChannel());
		this.style = style;
		this.annotation = annotation;
	}

	public Tag(TagStyle style, float position, float length, int channel, String annotation) {
		super(style.getType(), position, length, channel);
		this.style = style;
		this.annotation = annotation;
	}

	public Tag(TagStyle style, float position, float length) {
		super((style != null ? style.getType() : SignalSelectionType.CHANNEL), position, length);
		this.style = style;
	}

	public Tag(TagStyle style, float position, float length, int channel) {
		super(style.getType(), position, length, channel);
		this.style = style;
	}

	public Tag(Tag tag) {
		this(tag.style, tag.position, tag.length, tag.channel, tag.annotation);
	}

	public TagStyle getStyle() {
		return style;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public boolean isMarker() {
		return (style != null ? style.isMarker() : false);
	}

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

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Tag)) {
			return false;
		}
		return (this.compareTo((Tag) obj) == 0);
	}

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

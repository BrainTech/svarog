/* TagStyle.java created 2007-09-28
 *
 */

package org.signalml.domain.tag;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.beans.IntrospectionException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.KeyStroke;

import org.signalml.app.model.LabelledPropertyDescriptor;
import org.signalml.app.model.PropertyProvider;
import org.signalml.domain.signal.SignalSelectionType;
import org.springframework.context.MessageSourceResolvable;

/**
 * This class describes the style of a {@link Tag tagged selection}.
 * It contains the name, description, visual style of selection, and key
 * used to start creating a selection of a given style.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStyle implements Serializable, Comparable<TagStyle>, MessageSourceResolvable, PropertyProvider {

	private static final long serialVersionUID = 1L;

        /**
         * Type of a selection
         */
	private SignalSelectionType type;

        /**
         * Name of a style
         */
	private String name;

        /**
         * Description of a style
         */
	private String description;

        /**
         * Colour of the fill of the selection
         */
	private Color fillColor;

	// composite not used, not written to XML, so not propagated

        /**
         * Colour of the outline of the selection
         */
	private Color outlineColor;

        /**
         * Width of the outline
         */
	private float outlineWidth;

        /**
         * The array representing the dashing pattern for the outline
         */
	private float[] outlineDash;

        /**
         * The key that will be used to start creation of a selection of
         * a given type
         */
	private KeyStroke keyStroke;

        /**
         * Stroke for the outline
         */
	private Stroke outlineStroke;


        /**
         * Tells whether the selection is a marker
         */
	private boolean marker = false;

        /**
         * Default style of a selection of a page
         */
	private static final TagStyle defaultPageStyle = new TagStyle(SignalSelectionType.PAGE, "?", "Unknown", Color.RED, Color.RED.darker(), 1F, null, null, false);

        /**
         * Default style of a selection of a block
         */
	private static final TagStyle defaultBlockStyle = new TagStyle(SignalSelectionType.BLOCK, "?", "Unknown", Color.RED, Color.RED.darker(), 1F, null, null, false);

        /**
         * Default style of a custom selection of a single channel
         */
	private static final TagStyle defaultChannelStyle = new TagStyle(SignalSelectionType.CHANNEL, "?", "Unknown", Color.RED, Color.RED.darker(), 1F, null, null, false);

        /**
         * Constructor. Creates a style of a tagged selection for a selection
         * of a given type.
         * @param type the type of a selection
         */
	public TagStyle(SignalSelectionType type) {
		this.type = type;
	}

        /**
         * Copy constructor
         * @param style the object to be copied
         */
	public TagStyle(TagStyle style) {
		this.type = style.type;
		this.name = style.name;
		this.description = style.description;
		this.fillColor = style.fillColor;
		this.outlineColor = style.outlineColor;
		this.outlineWidth = style.outlineWidth;
		this.outlineDash = style.outlineDash;
		this.keyStroke = style.keyStroke;
		this.marker = style.marker;
	}

        /**
         * Constructor. Creates a style for a tagged selection using
         * given parameters.
         * @param type the type of a selection
         * @param name the name of a style
         * @param description description of a style
         * @param fillColor the colour of the fill of the selection
         * @param outlineColor the colour of the outline of the selection
         * @param outlineWidth the width of the outline of the selection
         * @param outlineDash the array representing the dashing pattern
         * for the outline
         * @param keyStroke the key that will be used to start creation
         * of a selection of a given type
         * @param marker true if the selection is a marker, false otherwise
         */
	public TagStyle(SignalSelectionType type, String name, String description, Color fillColor, Color outlineColor, float outlineWidth, float[] outlineDash, KeyStroke keyStroke, boolean marker) {
		this.type = type;
		this.name = name;
		this.description = description;
		this.fillColor = fillColor;
		this.outlineColor = outlineColor;
		this.outlineWidth = outlineWidth;
		this.outlineDash = outlineDash;
		this.keyStroke = keyStroke;
		this.marker = marker;
	}

        /**
         * Constructor. Creates a style for a tagged selection using
         * given parameters.
         * @param type the type of a selection
         * @param name the name of a style
         * @param description description of a style
         * @param fillColor the colour of the fill of the selection
         * @param outlineColor the colour of the outline of the selection
         * @param outlineWidth the width of the outline of the selection
         */
	public TagStyle(SignalSelectionType type, String name, String description, Color fillColor, Color outlineColor, float outlineWidth) {
		this.type = type;
		this.name = name;
		this.description = description;
		this.fillColor = fillColor;
		this.outlineColor = outlineColor;
		this.outlineWidth = outlineWidth;
	}

        /**
         * Sets parameters of a style to given values
         * @param name the name of a style
         * @param description description of a style
         * @param fillColor the colour of the fill of the selection
         * @param outlineColor the colour of the outline of the selection
         * @param outlineWidth the width of the outline of the selection
         * @param outlineDash the array representing the dashing pattern
         * for the outline
         * @param keyStroke the key that will be used to start creation
         * of a selection of a given type
         * @param marker true if the selection is a marker, false otherwise
         */
	public void setParameters(String name, String description, Color fillColor, Color outlineColor, float outlineWidth, float[] outlineDash, KeyStroke keyStroke, boolean marker) {
		this.name = name;
		this.description = description;
		this.fillColor = fillColor;
		this.outlineColor = outlineColor;
		this.outlineWidth = outlineWidth;
		this.outlineDash = outlineDash;
		this.keyStroke = keyStroke;
		this.marker = marker;

		outlineStroke = null;
	}

        /**
         * Copies parameters of a given TagStyle to the current object
         * @param style TagStyle object which parameters will be copied
         */
	public void copyFrom(TagStyle style) {
		this.name = style.name;
		this.description = style.description;
		this.fillColor = style.fillColor;
		this.outlineColor = style.outlineColor;
		this.outlineWidth = style.outlineWidth;
		this.outlineDash = style.outlineDash;
		this.keyStroke = style.keyStroke;
		this.marker = style.marker;

		outlineStroke = null;
	}

        /**
         * Returns the type of a selection
         * @return the type of a selection
         */
	public SignalSelectionType getType() {
		return type;
	}

        /**
         * Returns the name of a style
         * @return the name of a style
         */
	public String getName() {
		return name;
	}

        /**
         * Sets the name of a style
         * @param name the name to be set
         */
	public void setName(String name) {
		this.name = name;
	}

        /**
         * Returns the description of a style
         * @return the description of a style
         */
	public String getDescription() {
		return description;
	}

        /**
         * Sets the description of a style
         * @param description the description of a style
         */
	public void setDescription(String description) {
		this.description = description;
	}

        /**
         * Returns the description of a style or, if it doesn't exist, the name
         * @return the description of a style or, if it doesn't exist, the name
         */
	public String getDescriptionOrName() {
		return ((description != null && !description.isEmpty()) ? description : name);
	}

        /**
         * Returns the colour of the fill of the selection
         * @return the colour of the fill of the selection
         */
	public Color getFillColor() {
		return fillColor;
	}

        /**
         * Sets the colour of the fill of the selection
         * @param fillColor the colour of the fill of the selection
         */
	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

        /**
         * Returns the colour of the outline of the selection
         * @return the colour of the outline of the selection
         */
	public Color getOutlineColor() {
		return outlineColor;
	}

        /**
         * Sets the colour of the outline of the selection
         * @param outlineColor the colour of the outline of the selection
         */
	public void setOutlineColor(Color outlineColor) {
		this.outlineColor = outlineColor;
	}

        /**
         * Returns the width of the outline
         * @return the width of the outline
         */
	public float getOutlineWidth() {
		return outlineWidth;
	}

        /**
         * Sets the width of the outline
         * @param outlineWidth the width of the outline
         */
	public void setOutlineWidth(float outlineWidth) {
		this.outlineWidth = outlineWidth;
		outlineStroke = null;
	}

        /**
         * Returns the array representing the dashing pattern for the outline
         * @return the array representing the dashing pattern for the outline
         */
	public float[] getOutlineDash() {
		return outlineDash;
	}

        /**
         * Sets the array representing the dashing pattern for the outline
         * @param outlineDash the array representing the dashing pattern
         * for the outline
         */
	public void setOutlineDash(float[] outlineDash) {
		this.outlineDash = outlineDash;
		outlineStroke = null;
	}

        /**
         * Returns the stroke for the outline
         * @return the stroke for the outline
         */
	public Stroke getOutlineStroke() {
		if (outlineStroke == null) {
			outlineStroke = new BasicStroke(outlineWidth,BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, outlineDash, 0.0f);
		}
		return outlineStroke;
	}

        /**
         * Returns the key that will be used to start creation of a selection of
         * a given type
         * @return the key that will be used to start creation of a selection of
         * a given type
         */
	public KeyStroke getKeyStroke() {
		return keyStroke;
	}

        /**
         * Sets the key that will be used to start creation of a selection of
         * a given type
         * @param keyStroke  the key that will be used to start creation
         * of a selection of a given type
         */
	public void setKeyStroke(KeyStroke keyStroke) {
		this.keyStroke = keyStroke;
	}

        /**
         * Returns if the selection is a marker
         * @return true if the selection is a marker, false otherwise
         */
	public boolean isMarker() {
		return marker;
	}

        /**
         * Sets if the selection is a marker
         * @param marker true if the selection is a marker, false otherwise
         */
	public void setMarker(boolean marker) {
		this.marker = marker;
	}

        /**
         * Returns the default style for a selection
         * @return the default style for a selection
         */
	public static TagStyle getDefault() {
		return defaultPageStyle;
	}

        /**
         * Returns the default style for a selection of a page
         * @return the default style for a selection of a page
         */
	public static TagStyle getDefaultPage() {
		return defaultPageStyle;
	}

        /**
         * Returns the default style for a selection of a block
         * @return the default style for a selection of a block
         */
	public static TagStyle getDefaultBlock() {
		return defaultBlockStyle;
	}

        /**
         * Returns the default style for a selection of a part of a channel
         * @return the default style for a selection of a part of a channel
         */
	public static TagStyle getDefaultChannel() {
		return defaultChannelStyle;
	}

	@Override
	public Object[] getArguments() {
		return new Object[] { name, (description != null ? description : name) };
	}

	@Override
	public String[] getCodes() {
		return new String[] { "tagStyle" };
	}

	@Override
	public String getDefaultMessage() {
		return "Style: " + name;
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		List<LabelledPropertyDescriptor> list = new LinkedList<LabelledPropertyDescriptor>();

		list.add(new LabelledPropertyDescriptor("property.tagStyle.type", "type", TagStyle.class, "getType", null));
		list.add(new LabelledPropertyDescriptor("property.tagStyle.name", "name", TagStyle.class));
		list.add(new LabelledPropertyDescriptor("property.tagStyle.description", "description", TagStyle.class));
		list.add(new LabelledPropertyDescriptor("property.tagStyle.fillColor", "fillColor", TagStyle.class));
		list.add(new LabelledPropertyDescriptor("property.tagStyle.outlineColor", "outlineColor", TagStyle.class));
		list.add(new LabelledPropertyDescriptor("property.tagStyle.outlineWidth", "outlineWidth", TagStyle.class));
		list.add(new LabelledPropertyDescriptor("property.tagStyle.outlineDash", "outlineDash", TagStyle.class));
		list.add(new LabelledPropertyDescriptor("property.tagStyle.keyStroke", "keyStroke", TagStyle.class));

		if (getType() == SignalSelectionType.CHANNEL) {

			list.add(new LabelledPropertyDescriptor("property.tagStyle.marker", "marker", TagStyle.class));

		}

		return list;

	}

        /**
         * Compares a given TagStyle object to the current object using
         * the names of objects
         * @param o TagStyle object to be compared to the current object
         * @return the effect of comparison of objects names
         */
	@Override
	public int compareTo(TagStyle o) {
		return name.compareTo(o.name);
	}

        /**
         * Returns the name of the current object
         * @return the name of the current object
         */
	@Override
	public String toString() {
		return getName();
	}

}

/* TagStyle.java created 2007-09-28
 *
 */

package org.signalml.plugin.export.signal;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.beans.IntrospectionException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.KeyStroke;

import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.app.model.components.PropertyProvider;
import org.signalml.domain.montage.MontageChannel;
import org.signalml.plugin.export.signal.tagStyle.TagStyleAttributes;
import org.springframework.context.MessageSourceResolvable;

/**
 * This class describes the style of a {@link Tag tagged selection}.
 * It contains the name, description, visual style of selection, and the key
 * used to start creating a selection of a given style.
 * Styles can be compared (it is done by comparing their names).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStyle implements Serializable, Comparable<TagStyle>, MessageSourceResolvable, PropertyProvider, ExportedTagStyle {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link SignalSelectionType type} of the {@link Tag selection}
	 */
	private SignalSelectionType type;

	/**
	 * the name of this style
	 */
	private String name;

	/**
	 * the description of this style
	 */
	private String description;

	/**
	 * Colour of the fill of the {@link Tag selection}
	 */
	private Color fillColor;

	// composite not used, not written to XML, so not propagated

	/**
	 * Colour of the outline of the {@link Tag selection}
	 */
	private Color outlineColor;

	/**
	 * width of the outline
	 */
	private float outlineWidth;

	/**
	 * the array representing the dashing pattern for the outline
	 */
	private float[] outlineDash;

	/**
	 * the key that will be used to start creation of a
	 * {@link Tag selection} of a given type
	 */
	private KeyStroke keyStroke;

	/**
	 * stroke for the outline
	 */
	private Stroke outlineStroke;


	/**
	 * tells whether the {@link Tag selection} is a marker
	 */
	private boolean marker = false;

	/**
	 * default style of a {@link Tag selection} of a page
	 */
	private static final TagStyle defaultPageStyle = new TagStyle(SignalSelectionType.PAGE, "?", "Unknown", Color.RED, Color.RED.darker(), 1F, null, null, false);

	/**
	 * Default style of a {@link Tag selection} of a block
	 */
	private static final TagStyle defaultBlockStyle = new TagStyle(SignalSelectionType.BLOCK, "?", "Unknown", Color.RED, Color.RED.darker(), 1F, null, null, false);

	/**
	 * Default style of a custom {@link Tag selection} of a single
	 * {@link MontageChannel channel}
	 */
	private static final TagStyle defaultChannelStyle = new TagStyle(SignalSelectionType.CHANNEL, "?", "Unknown", Color.RED, Color.RED.darker(), 1F, null, null, false);
	/**
	 * The definitions of the attributes which can be set for tags having this style.
	 */
	private TagStyleAttributes attributesDefinitions = new TagStyleAttributes();

	/**
	 * Defines if this tag style should be visible - if so, it will be
	 * rendered in the signal view.
	 */
	private boolean visible = true;

	/**
	 * Constructor. Creates a style of a {@link Tag tagged selection}
	 * for a selection of a given {@link SignalSelectionType type}.
	 * @param type the type of a selection
	 */
	public TagStyle(SignalSelectionType type) {
		this.type = type;
	}

	/**
	 * Copy constructor.
	 * @param style the style to be copied
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
		this.attributesDefinitions = style.attributesDefinitions;
		this.visible = style.visible;
	}

	/**
	 * Copy constructor. Creates a tag style
	 * based on an object of a given interface
	 * @param style the style which parameters are to be copied
	 */
	public TagStyle(ExportedTagStyle style) {
		this.type = SignalSelectionType.typeByName(style.getType().getName());
		this.name = style.getName();
		this.description = style.getDescription();
		this.fillColor = style.getFillColor();
		this.outlineColor = style.getOutlineColor();
		this.outlineWidth = style.getOutlineWidth();
		this.outlineDash = style.getOutlineDash();
		this.keyStroke = style.getKeyStroke();
		this.marker = style.isMarker();
                this.attributesDefinitions = style.getAttributesDefinitions();
                this.visible = style.getIsVisible();

	}

	/**
	 * Constructor. Creates a style for a {@link Tag tagged selection} using
	 * given parameters.
	 * @param type the {@link SignalSelectionType type} of a selection
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
	 * Constructor. Creates a style for a {@link Tag tagged selection} using
	 * given parameters.
	 * @param type the {@link SignalSelectionType type} of a selection
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
	 * Sets parameters of this style to given values
	 * @param name the name of a style
	 * @param description description of this style
	 * @param fillColor the colour of the fill of the {@link Tag selection}
	 * @param outlineColor the colour of the outline of the selection
	 * @param outlineWidth the width of the outline of the selection
	 * @param outlineDash the array representing the dashing pattern
	 * for the outline
	 * @param keyStroke the key that will be used to start creation
	 * of a selection of this style
	 * @param marker true if the selection is a marker, false otherwise
	 */
	public void setParameters(String name, String description, Color fillColor, Color outlineColor, float outlineWidth, float[] outlineDash, KeyStroke keyStroke, boolean marker, boolean visible) {
		this.name = name;
		this.description = description;
		this.fillColor = fillColor;
		this.outlineColor = outlineColor;
		this.outlineWidth = outlineWidth;
		this.outlineDash = outlineDash;
		this.keyStroke = keyStroke;
		this.marker = marker;
		this.visible = visible;

		outlineStroke = null;
	}

	/**
	 * Copies parameters of a given TagStyle to this style.
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
		this.attributesDefinitions = style.attributesDefinitions;
		this.visible = style.visible;

		outlineStroke = null;
	}

	/**
	 * Returns the {@link SignalSelectionType type} of a
	 * {@link Tag selection}.
	 * @return the type of a selection
	 */
	@Override
	public SignalSelectionType getType() {
		return type;
	}

	/**
	 * Returns the name of this style.
	 * @return the name of this style
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this style.
	 * @param name the name to be set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the description of this style.
	 * @return the description of this style
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of this style.
	 * @param description the description of this style
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the description of this style or, if it doesn't exist, the name
	 * @return the description of this style or, if it doesn't exist, the name
	 */
	@Override
	public String getDescriptionOrName() {
		return ((description != null && !description.isEmpty()) ? description : name);
	}

	/**
	 * Returns the colour of the fill of the {@link Tag selection}.
	 * @return the colour of the fill of the selection
	 */
	@Override
	public Color getFillColor() {
		return fillColor;
	}

	/**
	 * Sets the colour of the fill of the {@link Tag selection}.
	 * @param fillColor the colour of the fill of the selection
	 */
	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	/**
	 * Returns the colour of the outline of the selection
	 * @return the colour of the outline of the selection
	 */
	@Override
	public Color getOutlineColor() {
		return outlineColor;
	}

	/**
	 * Sets the colour of the outline of the {@link Tag selection}.
	 * @param outlineColor the colour of the outline of the selection
	 */
	public void setOutlineColor(Color outlineColor) {
		this.outlineColor = outlineColor;
	}

	/**
	 * Returns the width of the outline.
	 * @return the width of the outline
	 */
	@Override
	public float getOutlineWidth() {
		return outlineWidth;
	}

	/**
	 * Sets the width of the outline.
	 * @param outlineWidth the width of the outline
	 */
	public void setOutlineWidth(float outlineWidth) {
		this.outlineWidth = outlineWidth;
		outlineStroke = null;
	}

	/**
	 * Returns the array representing the dashing pattern for the outline.
	 * @return the array representing the dashing pattern for the outline
	 */
	@Override
	public float[] getOutlineDash() {
		return outlineDash;
	}

	/**
	 * Sets the array representing the dashing pattern for the outline.
	 * @param outlineDash the array representing the dashing pattern
	 * for the outline
	 */
	public void setOutlineDash(float[] outlineDash) {
		this.outlineDash = outlineDash;
		outlineStroke = null;
	}

	/**
	 * Returns the stroke for the outline.
	 * @return the stroke for the outline
	 */
	@Override
	public Stroke getOutlineStroke() {
		if (outlineStroke == null) {
			outlineStroke = new BasicStroke(outlineWidth,BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, outlineDash, 0.0f);
		}
		return outlineStroke;
	}

	/**
	 * Returns the key that will be used to start creation of a
	 * {@link Tag selection} of this style.
	 * @return the key that will be used to start creation of a selection of
	 * this style
	 */
	@Override
	public KeyStroke getKeyStroke() {
		return keyStroke;
	}

	/**
	 * Sets the key that will be used to start creation of
	 * a {@link Tag selection} of this style.
	 * @param keyStroke the key that will be used to start creation
	 * of a selection of this style
	 */
	public void setKeyStroke(KeyStroke keyStroke) {
		this.keyStroke = keyStroke;
	}

	/**
	 * Returns if the {@link Tag selection} is a marker.
	 * @return true if the selection is a marker, false otherwise
	 */
	@Override
	public boolean isMarker() {
		return marker;
	}
        
        @Override
        /**
	 * Returns the definitions of the attributes which can be set for tags
	 * having this style.
	 * @return the tag style attributes definitions
	 */
        public TagStyleAttributes getAttributesDefinitions(){
            return attributesDefinitions;
        }


	/**
	 * Sets if the {@link Tag selection} is a marker.
	 * @param marker true if the selection is a marker, false otherwise
	 */
	public void setMarker(boolean marker) {
		this.marker = marker;
	}

	/**
	 * Returns the default style for a {@link Tag selection}.
	 * @return the default style for a selection
	 */
	public static TagStyle getDefault() {
		return defaultPageStyle;
	}

	/**
	 * Returns the default style for a {@link Tag selection} of a page.
	 * @return the default style for a selection of a page
	 */
	public static TagStyle getDefaultPage() {
		return defaultPageStyle;
	}

	/**
	 * Returns the default style for a {@link Tag selection} of a block.
	 * @return the default style for a selection of a block
	 */
	public static TagStyle getDefaultBlock() {
		return defaultBlockStyle;
	}

	/**
	 * Returns the default style for a {@link Tag selection} of a part
	 * of a channel.
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
		return _("Style:") + " " + name;
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		List<LabelledPropertyDescriptor> list = new LinkedList<LabelledPropertyDescriptor>();

		list.add(new LabelledPropertyDescriptor(_("type"), "type", TagStyle.class, "getType", null));
		list.add(new LabelledPropertyDescriptor(_("name"), "name", TagStyle.class));
		list.add(new LabelledPropertyDescriptor(_("description"), "description", TagStyle.class));
		list.add(new LabelledPropertyDescriptor(_("fill color"), "fillColor", TagStyle.class));
		list.add(new LabelledPropertyDescriptor(_("outline color"), "outlineColor", TagStyle.class));
		list.add(new LabelledPropertyDescriptor(_("outline width"), "outlineWidth", TagStyle.class));
		list.add(new LabelledPropertyDescriptor(_("outline dash"), "outlineDash", TagStyle.class));
		list.add(new LabelledPropertyDescriptor(_("key stroke"), "keyStroke", TagStyle.class));
		list.add(new LabelledPropertyDescriptor(_("visibility"), "visibility", TagStyle.class));

		if (getType() == SignalSelectionType.CHANNEL) {

			list.add(new LabelledPropertyDescriptor(_("marker"), "marker", TagStyle.class));

		}

		return list;

	}

	/**
	 * Compares a given TagStyle to this style using their the names.
	 * @param o TagStyle the style to be compared to this style
	 * @return the effect of comparison of styles names
	 */
	@Override
	public int compareTo(TagStyle o) {
		return name.compareTo(o.name);
	}

	/**
	 * Returns the name of this style.
	 * @return the name of this style
	 */
	@Override
	public String toString() {
		return getName();
	}

	public boolean equals(ExportedTagStyle style) {
		if (this.type != SignalSelectionType.typeByName(style.getType().getName())) return false;
		if (this.name != style.getName()) return false;
		if (this.description != style.getDescription()) return false;
		if (this.fillColor != style.getFillColor()) return false;
		if (this.outlineColor != style.getOutlineColor()) return false;
		if (this.outlineWidth != style.getOutlineWidth()) return false;
		if (this.outlineDash != style.getOutlineDash()) return false;
		if (this.keyStroke != style.getKeyStroke()) return false;
		if (this.marker != style.isMarker()) return false;
		return true;
	}




	/**
	 * Sets the definitions of the attributes which can be set for tags
	 * having this style.
	 * @param attributes the attributes definitions for this tag style.
	 */
	public void setAttributesDefinitions(TagStyleAttributes attributes) {
		this.attributesDefinitions = attributes;
	}

	/**
	 * Sets the tag style visibility in the signal view.
	 * @param visible determines if the tags described by this tag style
	 * should be rendered in the signal view.
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Returns if tags having this tag style should be visible or not.
	 * @return true if this tags described by this tag style should
	 * be rendered in the signal view, false otherwise.
	 */
	public boolean isVisible() {
		return visible;
	}
        
        @Override
        public boolean getIsVisible() {
            return visible;
        }


}

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

/** TagStyle
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagStyle implements Serializable, Comparable<TagStyle>, MessageSourceResolvable, PropertyProvider {

	private static final long serialVersionUID = 1L;

	private SignalSelectionType type;
	
	private String name;
	
	private String description;
		
	private Color fillColor;
	
	// composite not used, not written to XML, so not propagated
	
	private Color outlineColor;
	
	private float outlineWidth;
	
	private float[] outlineDash;
	
	private KeyStroke keyStroke;

	private Stroke outlineStroke;
	
	private boolean marker = false;
	
	private static final TagStyle defaultPageStyle = new TagStyle(SignalSelectionType.PAGE, "?", "Unknown", Color.RED, Color.RED.darker(), 1F, null, null, false);
	private static final TagStyle defaultBlockStyle = new TagStyle(SignalSelectionType.BLOCK, "?", "Unknown", Color.RED, Color.RED.darker(), 1F, null, null, false);
	private static final TagStyle defaultChannelStyle = new TagStyle(SignalSelectionType.CHANNEL, "?", "Unknown", Color.RED, Color.RED.darker(), 1F, null, null, false);

	public TagStyle(SignalSelectionType type) {
		this.type = type;
	}
	
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
	
	public TagStyle(SignalSelectionType type, String name, String description, Color fillColor, Color outlineColor, float outlineWidth) {
		this.type = type;
		this.name = name;
		this.description = description;
		this.fillColor = fillColor;
		this.outlineColor = outlineColor;
		this.outlineWidth = outlineWidth;
	}
	
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
	
	public SignalSelectionType getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDescriptionOrName() {
		return ( (description != null && !description.isEmpty()) ? description : name );
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public Color getOutlineColor() {
		return outlineColor;
	}

	public void setOutlineColor(Color outlineColor) {
		this.outlineColor = outlineColor;
	}

	public float getOutlineWidth() {
		return outlineWidth;
	}

	public void setOutlineWidth(float outlineWidth) {
		this.outlineWidth = outlineWidth;
		outlineStroke = null;
	}

	public float[] getOutlineDash() {
		return outlineDash;
	}

	public void setOutlineDash(float[] outlineDash) {
		this.outlineDash = outlineDash;
		outlineStroke = null;
	}

	public Stroke getOutlineStroke() {
		if( outlineStroke == null ) {
			outlineStroke = new BasicStroke(outlineWidth,BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, outlineDash, 0.0f);
		}
		return outlineStroke;
	}

	public KeyStroke getKeyStroke() {
		return keyStroke;
	}

	public void setKeyStroke(KeyStroke keyStroke) {
		this.keyStroke = keyStroke;
	}
		
	public boolean isMarker() {
		return marker;
	}

	public void setMarker(boolean marker) {
		this.marker = marker;
	}

	public static TagStyle getDefault() {
		return defaultPageStyle;
	}

	public static TagStyle getDefaultPage() {
		return defaultPageStyle;
	}

	public static TagStyle getDefaultBlock() {
		return defaultBlockStyle;
	}

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
		
		list.add( new LabelledPropertyDescriptor("property.tagStyle.type", "type", TagStyle.class, "getType", null) );
		list.add( new LabelledPropertyDescriptor("property.tagStyle.name", "name", TagStyle.class) );
		list.add( new LabelledPropertyDescriptor("property.tagStyle.description", "description", TagStyle.class) );
		list.add( new LabelledPropertyDescriptor("property.tagStyle.fillColor", "fillColor", TagStyle.class) );
		list.add( new LabelledPropertyDescriptor("property.tagStyle.outlineColor", "outlineColor", TagStyle.class) );
		list.add( new LabelledPropertyDescriptor("property.tagStyle.outlineWidth", "outlineWidth", TagStyle.class) );
		list.add( new LabelledPropertyDescriptor("property.tagStyle.outlineDash", "outlineDash", TagStyle.class) );
		list.add( new LabelledPropertyDescriptor("property.tagStyle.keyStroke", "keyStroke", TagStyle.class) );
		
		if( getType() == SignalSelectionType.CHANNEL ) {
		
			list.add( new LabelledPropertyDescriptor("property.tagStyle.marker", "marker", TagStyle.class) );
			
		}
		
		return list;

	}

	@Override
	public int compareTo(TagStyle o) {
		return name.compareTo(o.name);
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}

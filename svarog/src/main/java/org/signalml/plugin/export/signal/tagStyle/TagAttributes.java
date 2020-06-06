package org.signalml.plugin.export.signal.tagStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the list of tag attributes values.
 *
 * @author Piotr Szachewicz
 */
public class TagAttributes {

	/**
	 * The values of tag attributes.
	 */
	private List<TagAttributeValue> attributes = new ArrayList<>();

	/**
	 * Adds a new attribute.
	 * @param attributeDefinition new attribute definition
	 * @param attributeValue new attribute value
	 */
	public void addAttribute(TagStyleAttributeDefinition attributeDefinition, String attributeValue) {

		TagAttributeValue value = new TagAttributeValue(attributeDefinition, attributeValue);
		attributes.add(value);
	}

	/**
	 * Returns the list of attributes values.
	 * @return the list of attributes values
	 */
	public List<TagAttributeValue> getAttributesList() {
		return attributes;
	}

	/**
	 * Adds a new attribute to this TagAttributes.
	 * @param attributeValue the {@link TagAttributeValue} of a new attribute
	 */
	public void addAttribute(TagAttributeValue attributeValue) {
		attributes.add(attributeValue);
	}

	/**
	 * Returns the attribute value having the given name.
	 * @param code the name of the attribute to be returned.
	 * @return the attribute of having the given name, null if no attribute
	 * of that name is found
	 */
	public TagAttributeValue getAttribute(String code) {
		for (TagAttributeValue value: attributes) {
			if (code.equals(value.getAttributeDefinition().getCode())) {
				return value;
			}
		}
		return null;
	}

	@Override
	public TagAttributes clone() {
		TagAttributes tagAttributes = new TagAttributes();
		for (TagAttributeValue tagAttributeValue: attributes) {
			tagAttributes.addAttribute(tagAttributeValue.clone());
		}
		return tagAttributes;
	}

}

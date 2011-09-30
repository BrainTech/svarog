/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.signalml.plugin.export.signal.tagStyle;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Piotr Szachewicz
 */
public class TagAttributes {

	private List<TagAttributeValue> attributes = new ArrayList<TagAttributeValue>();

	public void addAttribute(TagStyleAttributeDefinition attributeDefinition, String attributeValue) {

		TagAttributeValue value = new TagAttributeValue(attributeDefinition, attributeValue);
		attributes.add(value);
	}

	public List<TagAttributeValue> getAttributesList() {
		return attributes;
	}

	public void addAttribute(TagAttributeValue attributeValue) {
		attributes.add(attributeValue);
	}

	public TagAttributeValue getAttribute(String code) {
		for (TagAttributeValue value: attributes) {
			if (code.equals(value.getAttributeDefinition().getCode())) {
				return value;
			}
		}
		return null;
	}

	public TagAttributes clone() {
		TagAttributes tagAttributes = new TagAttributes();
		for (TagAttributeValue tagAttributeValue: attributes) {
			tagAttributes.addAttribute(tagAttributeValue.clone());
		}
		return tagAttributes;
	}

}

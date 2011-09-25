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
public class TagStyleAttributes {

	private List<TagStyleAttributeDefinition> attributes;

	public TagStyleAttributes() {
		attributes = new ArrayList<TagStyleAttributeDefinition>();
	}

	public void addAttributeDefinition(TagStyleAttributeDefinition definition) {
		attributes.add(definition);
	}

	public TagStyleAttributeDefinition getAttributeDefinition(String attributeCode) {
		for (TagStyleAttributeDefinition attribute: attributes) {
			if (attribute.getCode().equals(attributeCode))
				return attribute;
		}
		return null;
	}

	public boolean isAttributeDefined(String attributeCode) {
		if (getAttributeDefinition(attributeCode) == null)
			return false;
		return true;
	}

	public List<TagStyleAttributeDefinition> getAttributesDefinitionsList() {
		return attributes;
	}

	public int getSize() {
		return attributes.size();
	}

}

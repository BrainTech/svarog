package org.signalml.plugin.export.signal.tagStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * The attributes definitions defined for the given tag style.
 *
 * @author Piotr Szachewicz
 */
public class TagStyleAttributes {

	/**
	 * The list of attributes definitions for the given style.
	 */
	private List<TagStyleAttributeDefinition> attributes;

	/**
	 * Constructor.
	 */
	public TagStyleAttributes() {
		attributes = new ArrayList<>();
	}

	/**
	 * Removes the attribute definition having the given index.
	 * @param index index of the attribute definition to be removed
	 */
	public void removeAttributeDefinition(int index) {
		attributes.remove(index);
	}

	/**
	 * Adds an attribute definition to the list.
	 * @param definition attribute definition to be added
	 */
	public void addAttributeDefinition(TagStyleAttributeDefinition definition) {
		attributes.add(definition);
	}

	/**
	 * Returns the attribute definition having the given index.
	 * @param index the index of the attribute definition to be returned
	 * @return the attribute definition of the given index
	 */
	public TagStyleAttributeDefinition getAttributeDefinition(int index) {
		return attributes.get(index);
	}

	/**
	 * Returns the attribute definition having the given name.
	 * @param attributeCode the name of the attribute definition to be returned
	 * @return the attribute definition having the given name
	 */
	public TagStyleAttributeDefinition getAttributeDefinition(String attributeCode) {
		for (TagStyleAttributeDefinition attribute: attributes) {
			if (attribute.getCode().equals(attributeCode))
				return attribute;
		}
		return null;
	}

	/**
	 * Returns whether an attribute definition having the given name
	 * exists.
	 * @param attributeCode the name of the attribute
	 * @return true if an attribute definition for this name exists, false
	 * otherwise
	 */
	public boolean isAttributeDefined(String attributeCode) {
		if (getAttributeDefinition(attributeCode) == null)
			return false;
		return true;
	}

	/**
	 * Returns the list of all attributes definitions.
	 * @return the list of all attribute definitions
	 */
	public List<TagStyleAttributeDefinition> getAttributesDefinitionsList() {
		return attributes;
	}

	/**
	 * Returns the number of attributes definitions.
	 * @return the number of attributes definitions
	 */
	public int getSize() {
		return attributes.size();
	}

	@Override
	public TagStyleAttributes clone() {

		TagStyleAttributes newStyleAttributes = new TagStyleAttributes();
		for (TagStyleAttributeDefinition definition: attributes) {
			newStyleAttributes.addAttributeDefinition(definition.clone());
		}
		return newStyleAttributes;
	}

}

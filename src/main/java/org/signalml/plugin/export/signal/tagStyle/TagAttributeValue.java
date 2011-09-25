/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.signalml.plugin.export.signal.tagStyle;

/**
 *
 * @author Piotr Szachewicz
 */
public class TagAttributeValue {

	private TagStyleAttributeDefinition attributeDefinition;
	private String attributeValue;

	public TagAttributeValue(TagStyleAttributeDefinition attributeDefinition, String attributeValue) {
		this.attributeDefinition = attributeDefinition;
		this.attributeValue = attributeValue;
	}

	public TagStyleAttributeDefinition getAttributeDefinition() {
		return attributeDefinition;
	}

	public void setAttributeDefinition(TagStyleAttributeDefinition attributeDefinition) {
		this.attributeDefinition = attributeDefinition;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	protected TagAttributeValue clone() {
		TagAttributeValue value = new TagAttributeValue(attributeDefinition, attributeValue);
		return value;
	}

}

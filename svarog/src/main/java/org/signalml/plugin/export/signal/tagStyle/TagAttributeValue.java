package org.signalml.plugin.export.signal.tagStyle;

/**
 * The value of the tag attribute. Contains also the reference to the
 * tag style attribute definition that should be used to render this attribute.
 *
 * @author Piotr Szachewicz
 */
public class TagAttributeValue {

	/**
	 * The definition of this tag attribute.
	 */
	private TagStyleAttributeDefinition attributeDefinition;
	/**
	 * The value of this tag attribute.
	 */
	private String attributeValue;

	/**
	 * Constructor.
	 * @param attributeDefinition the definition of this tag attribute.
	 * @param attributeValue the value of this tag attribute
	 */
	public TagAttributeValue(TagStyleAttributeDefinition attributeDefinition, String attributeValue) {
		this.attributeDefinition = attributeDefinition;
		this.attributeValue = attributeValue;
	}

	/**
	 * Returns the definition of this tag attribute.
	 * @return the definition of this tag attribute
	 */
	public TagStyleAttributeDefinition getAttributeDefinition() {
		return attributeDefinition;
	}

	/**
	 * Sets the definition of this tag attribute.
	 * @param attributeDefinition
	 */
	public void setAttributeDefinition(TagStyleAttributeDefinition attributeDefinition) {
		this.attributeDefinition = attributeDefinition;
	}

	/**
	 * Returns the value of this tag attribute.
	 * @return the value of this tag attribute
	 */
	public String getAttributeValue() {
		return attributeValue;
	}

	/**
	 * Sets the value of this tag attribute.
	 * @param attributeValue
	 */
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	/**
	 * Makes a copy of this tag attribute value.
	 * @return a copy of this tag attribute value.
	 */
	@Override
	protected TagAttributeValue clone() {
		TagAttributeValue value = new TagAttributeValue(attributeDefinition, attributeValue);
		return value;
	}

}

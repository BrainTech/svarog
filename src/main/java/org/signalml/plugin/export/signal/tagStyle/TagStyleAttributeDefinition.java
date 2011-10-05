package org.signalml.plugin.export.signal.tagStyle;

/**
 * The definition of the tag style attribute. Consists of an attribute code, displayName
 * and a field indicating whether this attribute should be rendered.
 *
 * @author Piotr Szachewicz
 */
public class TagStyleAttributeDefinition {

	/**
	 * The name of the attribute.
	 */
	private String code;
	/**
	 * The rendered name of the attribute.
	 */
	private String displayName;
	/**
	 * True, if attributes described by this definition should be rendered
	 * on tags.
	 */
	private boolean visible;

	/**
	 * Constructor.
	 * @param code the name of the tag style attribute definition
	 * @param displayName the display name of the attribute definition
	 * @param visible true if the attributes values having this
	 * definition should be rendered on tags.
	 */
	public TagStyleAttributeDefinition(String code, String displayName, boolean visible) {
		this.code = code;
		this.displayName = displayName;
		this.visible = visible;
	}

	/**
	 * Returns the name of this attribute.
	 * @return the name of this attribute
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets the name of this attribute.
	 * @param code the name of this attribute
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Returns the display name of this attribute.
	 * @return the display name of this attribute
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Sets the display name of this attribute.
	 * @param displayName display name to be set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Returns whether attributes having this definition should be visible.
	 * @return true if attribute should be visible, false otherwise
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Sets whether attributes having this definition should be visible.
	 * @param visible true if attribute should be visible, false otherwise
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Copies all data from the given {@link TagStyleAttributeDefinition}.
	 * @param otherDefinition the definition from which data will be copied.
	 */
	public void copyFrom(TagStyleAttributeDefinition otherDefinition) {
		this.code = otherDefinition.code;
		this.displayName = otherDefinition.displayName;
		this.visible = otherDefinition.visible;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TagStyleAttributeDefinition)) {
			return false;
		}

		TagStyleAttributeDefinition other = (TagStyleAttributeDefinition) obj;
		if (!areObjectsEqual(other.code, this.code)) {
			return false;
		}
		if (!areObjectsEqual(other.displayName, this.displayName)) {
			return false;
		}
		if (!areObjectsEqual(other.visible, this.visible)) {
			return false;
		}
		return true;
	}

	/**
	 * A method checking whether objects are equal concerning that
	 * the examined objects could be null.
	 * @param s1 object 1
	 * @param s2 object 2
	 * @return true if both objects are null or if both objects are equal.
	 */
	protected boolean areObjectsEqual(Object s1, Object s2) {
		if (s1 == null && s2 == null) {
			return true;
		}
		if (s1 == null && s2 != null) {
			return false;
		}
		if (s1 != null && s2 == null) {
			return false;
		}

		if (s1.equals(s2)) {
			return true;
		}
		return false;
	}

	@Override
	protected TagStyleAttributeDefinition clone() {
		TagStyleAttributeDefinition definition = new TagStyleAttributeDefinition(this.code, this.displayName, this.visible);
		return definition;
	}
}

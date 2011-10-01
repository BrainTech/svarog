/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.signalml.plugin.export.signal.tagStyle;

/**
 *
 * @author Piotr Szachewicz
 */
public class TagStyleAttributeDefinition {

	private String code;
	private String displayName;
	private boolean visible;

	public TagStyleAttributeDefinition(String code, String displayName, boolean visible) {
		this.code = code;
		this.displayName = displayName;
		this.visible = visible;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

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

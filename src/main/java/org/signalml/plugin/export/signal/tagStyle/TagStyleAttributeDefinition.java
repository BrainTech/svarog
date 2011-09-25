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

}

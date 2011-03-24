/* AmplifierChannel.java created 2011-03-23
 *
 */

package org.signalml.app.view.opensignal.elements;

/**
 *
 * @author Piotr Szachewicz
 */
public class AmplifierChannel {

	private Boolean selected = Boolean.TRUE;
	private int number;
	private String name;

	public AmplifierChannel(int number, String channelName) {
		this.number = number;
		this.name = channelName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public Boolean isSelected() {
		return selected;
	}

	void setSelected(Boolean selected) {
		this.selected = selected;
	}

}

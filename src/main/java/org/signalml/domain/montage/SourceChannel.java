/* MontageChannel.java created 2007-10-23
 *
 */

package org.signalml.domain.montage;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/** MontageChannel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("sourcechannel")
public class SourceChannel implements Serializable {

	private static final long serialVersionUID = 1L;

	private int channel;
	private String label;
	private Channel function;

	protected SourceChannel() {
	}

	public SourceChannel(int channel, String label, Channel function) {
		this.channel = channel;
		setLabel(label);
		setFunction(function);
	}

	public SourceChannel(SourceChannel sourceChannel) {
		this.channel = sourceChannel.channel;
		this.label = sourceChannel.label;
		this.function = sourceChannel.function;
	}

	public int getChannel() {
		return channel;
	}

	public String getLabel() {
		return label;
	}

	public Channel getFunction() {
		return function;
	}

	public void setLabel(String label) {
		if (label == null) {
			throw new NullPointerException("Null label");
		}
		if (label.isEmpty()) {
			throw new IllegalArgumentException("Empty label");
		}
		this.label = label;
	}

	public void setFunction(Channel function) {
		if (function == null) {
			throw new NullPointerException("Null function");
		}
		this.function = function;
	}

}

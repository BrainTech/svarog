package org.signalml.app.view.document.monitor.signalchecking;

/**
 * The state of a channel - whether it is valid, and some additional channel data.
 *
 * @author Tomasz Sawicki
 */
public class ChannelState {

	/**
	 * Whether channel is valid.
	 */
	private boolean valid;
	/**
	 * {@link AdditionalChannelData}.
	 */
	private AdditionalChannelData additionalChannelData;

	/**
	 * Returns {@link #additionalChannelData}.
	 * @return {@link #additionalChannelData}
	 */
	public AdditionalChannelData getAdditionalChannelData() {
		return additionalChannelData;
	}

	/**
	 * Returns {@link #valid}.
	 * @return {@link #valid}
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Default constructor.
	 * @param valid {@link #valid}
	 * @param additionalChannelData {@link #additionalChannelData}
	 */
	public ChannelState(boolean valid, AdditionalChannelData additionalChannelData) {
		this.valid = valid;
		this.additionalChannelData = additionalChannelData;
	}
}
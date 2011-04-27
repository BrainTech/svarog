/* VisualReferenceChannel.java created 2007-11-30
 *
 */

package org.signalml.app.view.montage;

/** VisualReferenceChannel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class VisualReferenceChannel {

	private int primaryChannel;
	private String label;

	public VisualReferenceChannel(int primaryChannel) {
		this.primaryChannel = primaryChannel;
	}

	public int getPrimaryChannel() {
		return primaryChannel;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}

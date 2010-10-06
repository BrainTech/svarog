/* SignalToolForwardingMouseAdapter.java created 2007-11-08
 *
 */

package org.signalml.app.view.signal;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;

/** SignalToolForwardingMouseAdapter
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalToolForwardingMouseAdapter extends MouseAdapter {

	private boolean mustSupportColumnHeader;
	private boolean mustSupportRowHeader;

	private SelectTagSignalTool selectTagSignalTool;
	private SignalTool signalTool;

	public SignalToolForwardingMouseAdapter() {
		this(false,false);
	}

	public SignalToolForwardingMouseAdapter(boolean mustSupportColumnHeader, boolean mustSupportRowHeader) {
		this.mustSupportColumnHeader = mustSupportColumnHeader;
		this.mustSupportRowHeader = mustSupportRowHeader;
	}

	public boolean isMustSupportColumnHeader() {
		return mustSupportColumnHeader;
	}

	public boolean isMustSupportRowHeader() {
		return mustSupportRowHeader;
	}

	public SignalTool getSignalTool() {
		return signalTool;
	}

	public void setSignalTool(SignalTool signalTool) {
		this.signalTool = signalTool;
	}

	public SelectTagSignalTool getSelectTagSignalTool() {
		return selectTagSignalTool;
	}

	public void setSelectTagSignalTool(SelectTagSignalTool selectTagSignalTool) {
		this.selectTagSignalTool = selectTagSignalTool;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if ((! (signalTool instanceof SelectionSignalTool)) && (SwingUtilities.isMiddleMouseButton(e) || (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()))) {
		    // TODO signalTool.mousePressed(e) ??
			selectTagSignalTool.mousePressed(e);
		} else {
			if (signalTool != null) {
				if (mustSupportColumnHeader && !signalTool.supportsColumnHeader()) {
					return;
				}
				if (mustSupportRowHeader && !signalTool.supportsRowHeader()) {
					return;
				}
				signalTool.mousePressed(e);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (signalTool != null) {
			if (mustSupportColumnHeader && !signalTool.supportsColumnHeader()) {
				return;
			}
			if (mustSupportRowHeader && !signalTool.supportsRowHeader()) {
				return;
			}
			signalTool.mouseReleased(e);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (signalTool != null) {
			if (mustSupportColumnHeader && !signalTool.supportsColumnHeader()) {
				return;
			}
			if (mustSupportRowHeader && !signalTool.supportsRowHeader()) {
				return;
			}
			signalTool.mouseClicked(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (signalTool != null) {
			if (mustSupportColumnHeader && !signalTool.supportsColumnHeader()) {
				return;
			}
			if (mustSupportRowHeader && !signalTool.supportsRowHeader()) {
				return;
			}
			signalTool.mouseEntered(e);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (signalTool != null) {
			if (mustSupportColumnHeader && !signalTool.supportsColumnHeader()) {
				return;
			}
			if (mustSupportRowHeader && !signalTool.supportsRowHeader()) {
				return;
			}
			signalTool.mouseExited(e);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (signalTool != null) {
			if (mustSupportColumnHeader && !signalTool.supportsColumnHeader()) {
				return;
			}
			if (mustSupportRowHeader && !signalTool.supportsRowHeader()) {
				return;
			}
			signalTool.mouseDragged(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (signalTool != null) {
			if (mustSupportColumnHeader && !signalTool.supportsColumnHeader()) {
				return;
			}
			if (mustSupportRowHeader && !signalTool.supportsRowHeader()) {
				return;
			}
			signalTool.mouseMoved(e);
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (signalTool != null) {
			if (mustSupportColumnHeader && !signalTool.supportsColumnHeader()) {
				return;
			}
			if (mustSupportRowHeader && !signalTool.supportsRowHeader()) {
				return;
			}
			signalTool.mouseWheelMoved(e);
		}
	}

}

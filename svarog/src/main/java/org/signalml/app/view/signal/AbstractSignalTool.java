package org.signalml.app.view.signal;

import org.signalml.plugin.export.signal.SignalTool;
import org.signalml.plugin.export.view.ExportedSignalView;

/**
 * Abstract implementation of the {@link SignalTool} which is similar to
 * {@link org.signalml.plugin.export.signal.AbstractSignalTool} but contains
 * {@link SignalView} instead of {@link ExportedSignalView}.
 * <p>
 * From this class inherit signal tools internal in Svarog (and only these
 * tools, as SignalView is not in the plug-in interface.
 *
 * @author Marcin Szumski
 */
public abstract class AbstractSignalTool extends
	org.signalml.plugin.export.signal.AbstractSignalTool {

	/**
	 * the {@link SignalView}
	 */
	private SignalView signalView;

	/**
	 * Constructs a new empty SignalTool.
	 */
	public AbstractSignalTool() {

	}

	/**
	 * Copy constructor. Sets {@link SignalView}.
	 * @param signalView the signal view to set
	 */
	public AbstractSignalTool(SignalView signalView) {
		super(signalView);
		this.signalView = signalView;
	}

	/**
	 * Returns the {@link SignalView} with which this tool is associated.
	 * @return the {@link SignalView} with which this tool is associated
	 */
	protected SignalView getSignalView() {
		return signalView;
	}

	/**
	 * Sets the {@link SignalView} with which this tool is associated.
	 * @param signalView the {@link SignalView} with which this tool is
	 * associated
	 */
	public void setSignalView(SignalView signalView) {
		this.signalView = signalView;
	}

	/**
	 * Sets the signal view with which this tool is associated.
	 * This function should not be used and {@link RuntimeException} will
	 * be thrown if the {@link ExportedSignalView} is not of type
	 * {@link SignalView}.
	 * @param signalView the {@link SignalView} with which this tool is
	 * associated
	 */
	public void setSignalView(ExportedSignalView signalView) {
		if (!(signalView instanceof SignalView)) throw new RuntimeException("ExportedSignalView for SignalTools internal in Svarog must be of type SignalView");
		this.signalView = (SignalView) signalView;
	}

}

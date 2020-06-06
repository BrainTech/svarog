/* SignalParametersPanel.java created 2007-09-17
 *
 */
package org.signalml.app.view.preferences;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.apache.log4j.Logger;
import org.signalml.app.view.signal.PagingParametersPanel;
import org.signalml.app.view.signal.RequiredSignalParametersPanel;

/**
 * Panel which allows to select some parameters of the signal. Contains two
 * sub-panels:
 * <ul>
 * <li>the {@link #getRequiredSignalParamersPanel() panel} with standard
 * parameters of the signal (sampling frequency, number of channels,
 * calibration),</li>
 * <li>the {@link #getPagingSignalParamersPanel() panel} with sizes of
 * a block and a page of the signal.</li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalParametersPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SignalParametersPanel.class);

	/**
	 * the {@link RequiredSignalParametersPanel panel} with standard parameters
	 * of the signal (sampling frequency, number of channels, calibration)
	 */
	private RequiredSignalParametersPanel requiredSignalParametersPanel;
	/**
	 * the {@link PagingParametersPanel panel} with sizes of a block and a
	 * page of the signal
	 */
	private PagingParametersPanel pagingParametersPanel;

	/**
	 * Constructor. Initializes the panel.
	 */
	public SignalParametersPanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with BorderLayout and two sub-panels:
	 * <ul>
	 * <li>the {@link #getRequiredSignalParamersPanel() panel} with standard
	 * parameters of the signal (sampling frequency, number of channels,
	 * calibration),</li>
	 * <li>the {@link #getPagingSignalParamersPanel() panel} with sizes of
	 * a block and a page of the signal.</li></ul>
	 */
	private void initialize() {

		setLayout(new BorderLayout());

		add(getRequiredSignalParamersPanel(), BorderLayout.CENTER);
		add(getPagingSignalParamersPanel(), BorderLayout.SOUTH);

	}

	/**
	 * Returns the {@link RequiredSignalParametersPanel panel} with standard parameters
	 * of the signal (sampling frequency, number of channels, calibration).
	 * If the panel doesn't exist it is created.
	 * @return the panel with standard parameters of the signal
	 */
	public RequiredSignalParametersPanel getRequiredSignalParamersPanel() {
		if (requiredSignalParametersPanel == null) {
			requiredSignalParametersPanel = new RequiredSignalParametersPanel();
		}
		return requiredSignalParametersPanel;
	}

	/**
	 * Returns the {@link PagingParametersPanel panel} with sizes of a block and a
	 * page of the signal
	 * If the panel doesn't exist it is created.
	 * @return the panel with sizes of a block and a page of the signal
	 */
	public PagingParametersPanel getPagingSignalParamersPanel() {
		if (pagingParametersPanel == null) {
			pagingParametersPanel = new PagingParametersPanel();
		}
		return pagingParametersPanel;
	}

}

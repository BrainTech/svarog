/* SignalParametersPanel.java created 2007-09-17
 *
 */
package org.signalml.app.view.element;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;

/** SignalParametersPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalParametersPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SignalParametersPanel.class);

	private MessageSourceAccessor messageSource;

	private RequiredSignalParametersPanel requiredSignalParametersPanel;
	private PagingParametersPanel pagingParametersPanel;

	/**
	 * This is the default constructor
	 */
	public SignalParametersPanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 *
	 */
	private void initialize() {

		setLayout(new BorderLayout());

		add(getRequiredSignalParamersPanel(), BorderLayout.CENTER);
		add(getPagingSignalParamersPanel(), BorderLayout.SOUTH);

	}

	public RequiredSignalParametersPanel getRequiredSignalParamersPanel() {
		if (requiredSignalParametersPanel == null) {
			requiredSignalParametersPanel = new RequiredSignalParametersPanel(messageSource);
		}
		return requiredSignalParametersPanel;
	}

	public PagingParametersPanel getPagingSignalParamersPanel() {
		if (pagingParametersPanel == null) {
			pagingParametersPanel = new PagingParametersPanel(messageSource);
		}
		return pagingParametersPanel;
	}

}

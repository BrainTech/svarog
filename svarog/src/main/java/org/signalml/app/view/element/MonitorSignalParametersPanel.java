package org.signalml.app.view.element;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.OpenMonitorDescriptor;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * This panel is responsible for displaying and setting monitor signal parameters.
 * Dispayed information include number of channels and sampling frequency.
 * Page size can be set using this panel.
 */
public class MonitorSignalParametersPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private ApplicationConfiguration applicationConfiguration;
	private MessageSourceAccessor messageSource;

	private JTextField samplingField;
	private JTextField channelCountField;
	private JTextField pageSizeField;

	/**
	 * Constructor.
	 * @param messageSource the message source accessor capable of resolving
	 * localized message codes
	 * @param applicationConfiguration the configuration of Svarog
	 */
	public MonitorSignalParametersPanel(MessageSourceAccessor messageSource, ApplicationConfiguration applicationConfiguration) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {

		JLabel samplingLabel = new JLabel(messageSource.getMessage("openMonitor.samplingLabel"));
		JLabel channelCountLabel = new JLabel(messageSource.getMessage("openMonitor.channelCountLabel"));

		JLabel pageSizeLabel = new JLabel(messageSource.getMessage("openMonitor.pageSizeLabel"));

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);

		layout.setAutoCreateGaps(true);

		layout.setAutoCreateContainerGaps(true);

		CompoundBorder border = new CompoundBorder(
			new TitledBorder(messageSource.getMessage("openMonitor.signalParametersPanelTitle")),
			new EmptyBorder(3, 3, 3, 3));
		setBorder(border);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(layout.createParallelGroup().addComponent(samplingLabel).addComponent(channelCountLabel).addComponent(pageSizeLabel));
		hGroup.addGroup(layout.createParallelGroup().addComponent(getSamplingField()).addComponent(getChannelCountField()).addComponent(getPageSizeField()));

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
			addComponent(samplingLabel).addComponent(getSamplingField()));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
			addComponent(channelCountLabel).addComponent(getChannelCountField()));
		vGroup.addGap(50);
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
			addComponent(pageSizeLabel).addComponent(getPageSizeField()));

		layout.setVerticalGroup(vGroup);

	}

	protected JTextField getSamplingField() {
		if (samplingField == null) {
			samplingField = new JTextField();
			samplingField.setEditable(false);
		}
		return samplingField;
	}

	protected JTextField getChannelCountField() {
		if (channelCountField == null) {
			channelCountField = new JTextField();
			channelCountField.setEditable(false);
		}
		return channelCountField;
	}

	protected JTextField getPageSizeField() {
		if (pageSizeField == null) {
			pageSizeField = new JTextField();
		}
		return pageSizeField;
	}

	/**
	 * Fills the fields of this panel from the given model.
	 * @param openMonitorDescriptor the model from which this dialog will be
	 * filled.
	 */
	public void fillPanelFromModel(OpenMonitorDescriptor openMonitorDescriptor) {

		Float pageSize = openMonitorDescriptor.getPageSize();

		if (
			(getPageSizeField().getText().isEmpty() && applicationConfiguration != null)
			|| (pageSize == 0))
			pageSize = applicationConfiguration.getMonitorPageSize();
		if (pageSize != null) {
			String pageString = Double.toString(pageSize);
			getPageSizeField().setText(pageString);
		}

		Float freq = openMonitorDescriptor.getSamplingFrequency();
		if (freq != null) {
			getSamplingField().setText(freq.toString());
		} else {
			getSamplingField().setText("");
		}

		Integer channelCount = openMonitorDescriptor.getChannelCount();
		if (channelCount == null) {
			channelCount = 0;
		}
		getChannelCountField().setText(channelCount.toString());

	}

	/**
	 * Fills the model with the data from this panel (user input).
	 * @param openMonitorDescriptor the model to be filled.
	 */
	public void fillModelFromPanel(OpenMonitorDescriptor openMonitorDescriptor) {
		openMonitorDescriptor.setPageSize(Float.parseFloat(getPageSizeField().getText()));
	}

	/**
	 * Sets the {@link ApplicationConfiguration} for this panel.
	 * @param applicationConfiguration a Svarog configuration to be used with
	 * this panel to get the default values for some fields.
	 */
	public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
		this.applicationConfiguration = applicationConfiguration;
	}

}

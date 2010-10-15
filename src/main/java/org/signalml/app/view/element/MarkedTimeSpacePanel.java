/* MarkedTimeSpacePanel.java created 2008-01-25
 *
 */
package org.signalml.app.view.element;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.view.tag.TagIconProducer;
import org.signalml.app.view.tag.TagStyleListCellRenderer;
import org.signalml.domain.signal.space.MarkerTimeSpace;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.domain.tag.TagStyle;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** MarkedTimeSpacePanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MarkedTimeSpacePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(MarkedTimeSpacePanel.class);

	private MessageSourceAccessor messageSource;

	private JPanel markerPanel;
	private JPanel settingsPanel;

	private JComboBox markerChannelComboBox;
	private JComboBox markerStyleComboBox;

	private JSpinner secondsBeforeSpinner;
	private JSpinner secondsAfterSpinner;

	private String[] channels;
	private TagStyle[] markerStyles;

	private TagStyleListCellRenderer markerStyleCellRenderer;

	public MarkedTimeSpacePanel(MessageSourceAccessor messageSource) {
		super();
		this.messageSource = messageSource;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		setBorder(new EmptyBorder(3,3,3,3));

		add(getMarkerPanel(), BorderLayout.CENTER);
		add(getSettingsPanel(), BorderLayout.SOUTH);

	}

	public JPanel getMarkerPanel() {
		if (markerPanel == null) {

			markerPanel = new JPanel();
			markerPanel.setBorder(new CompoundBorder(
			                              new TitledBorder(messageSource.getMessage("signalSpace.markedTimeSpace.markerPanel.title")),
			                              new EmptyBorder(3,3,3,3)
			                      ));

			GroupLayout layout = new GroupLayout(markerPanel);
			markerPanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			JLabel markerChannelLabel = new JLabel(messageSource.getMessage("signalSpace.markedTimeSpace.markerChannel"));
			JLabel markerStyleLabel = new JLabel(messageSource.getMessage("signalSpace.markedTimeSpace.markerStyle"));

			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

			hGroup.addGroup(
			        layout.createParallelGroup()
			        .addComponent(markerChannelLabel)
			        .addComponent(markerStyleLabel)
			);

			hGroup.addGroup(
			        layout.createParallelGroup()
			        .addComponent(getMarkerChannelComboBox())
			        .addComponent(getMarkerStyleComboBox())
			);

			layout.setHorizontalGroup(hGroup);

			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(markerChannelLabel)
					.addComponent(getMarkerChannelComboBox())
				);

			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(markerStyleLabel)
					.addComponent(getMarkerStyleComboBox())
				);

			layout.setVerticalGroup(vGroup);

		}
		return markerPanel;
	}

	public JComboBox getMarkerChannelComboBox() {
		if (markerChannelComboBox == null) {
			markerChannelComboBox = new JComboBox(new Object[0]);
			markerChannelComboBox.setPreferredSize(new Dimension(200,25));
		}
		return markerChannelComboBox;
	}

	public JComboBox getMarkerStyleComboBox() {
		if (markerStyleComboBox == null) {
			markerStyleComboBox = new JComboBox(new Object[0]);
			markerStyleComboBox.setPreferredSize(new Dimension(200,25));

			markerStyleComboBox.setRenderer(getMarkerStyleCellRenderer());
		}
		return markerStyleComboBox;
	}

	public TagStyleListCellRenderer getMarkerStyleCellRenderer() {
		if (markerStyleCellRenderer == null) {
			markerStyleCellRenderer = new TagStyleListCellRenderer(messageSource);
		}
		return markerStyleCellRenderer;
	}

	public JPanel getSettingsPanel() {
		if (settingsPanel == null) {

			settingsPanel = new JPanel();

			settingsPanel.setBorder(new CompoundBorder(
			                                new TitledBorder(messageSource.getMessage("signalSpace.markedTimeSpace.settingsPanel.title")),
			                                new EmptyBorder(3,3,3,3)
			                        ));

			GroupLayout layout = new GroupLayout(settingsPanel);
			settingsPanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			JLabel secondsBeforeLabel = new JLabel(messageSource.getMessage("signalSpace.markedTimeSpace.secondsBefore"));
			JLabel secondsAfterLabel = new JLabel(messageSource.getMessage("signalSpace.markedTimeSpace.secondsAfter"));

			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

			hGroup.addGroup(
			        layout.createParallelGroup()
			        .addComponent(secondsBeforeLabel)
			        .addComponent(secondsAfterLabel)
			);

			hGroup.addGroup(
			        layout.createParallelGroup(Alignment.TRAILING)
			        .addComponent(getSecondsBeforeSpinner())
			        .addComponent(getSecondsAfterSpinner())
			);

			layout.setHorizontalGroup(hGroup);

			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(secondsBeforeLabel)
					.addComponent(getSecondsBeforeSpinner())
				);

			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(secondsAfterLabel)
					.addComponent(getSecondsAfterSpinner())
				);

			layout.setVerticalGroup(vGroup);
		}
		return settingsPanel;
	}

	public JSpinner getSecondsBeforeSpinner() {
		if (secondsBeforeSpinner == null) {
			secondsBeforeSpinner = new JSpinner(new SpinnerNumberModel(1.0,0.0,3600,0.1));
			Dimension fixedSize = new Dimension(200,25);
			secondsBeforeSpinner.setPreferredSize(fixedSize);
		}
		return secondsBeforeSpinner;
	}

	public JSpinner getSecondsAfterSpinner() {
		if (secondsAfterSpinner == null) {
			secondsAfterSpinner = new JSpinner(new SpinnerNumberModel(1.0,0.0,3600,0.1));
			Dimension fixedSize = new Dimension(200,25);
			secondsAfterSpinner.setPreferredSize(fixedSize);
		}
		return secondsAfterSpinner;
	}

	public void fillPanelFromModel(SignalSpace space) {

		MarkerTimeSpace markerTimeSpace = space.getMarkerTimeSpace();
		JComboBox channelBox = getMarkerChannelComboBox();
		JComboBox styleBox = getMarkerStyleComboBox();
		if (markerTimeSpace == null) {

			if (channelBox.getModel().getSize() > 0) {
				channelBox.setSelectedIndex(0);
			}
			if (styleBox.getModel().getSize() > 0) {
				styleBox.setSelectedIndex(0);
			}

			getSecondsAfterSpinner().setValue(new Double(1));
			getSecondsBeforeSpinner().setValue(new Double(0));

		} else {

			if (channels != null) {
				int markerChannel = markerTimeSpace.getMarkerChannel();
				if (markerChannel >= channels.length) {
					markerChannel = 0;
				}

				channelBox.setSelectedIndex(markerChannel);
			}

			if (markerStyles != null) {
				String markerStyleName = markerTimeSpace.getMarkerStyleName();
				if (markerStyleName != null) {
					for (int i=0; i<markerStyles.length; i++) {
						if (markerStyleName.equals(markerStyles[i].getName())) {
							styleBox.setSelectedItem(markerStyles[i]);
							break;
						}
					}
				} else {
					channelBox.setSelectedIndex(0);
				}
			}

			getSecondsBeforeSpinner().setValue(markerTimeSpace.getSecondsBefore());
			getSecondsAfterSpinner().setValue(markerTimeSpace.getSecondsAfter());

		}

	}

	public void fillModelFromPanel(SignalSpace space) {

		MarkerTimeSpace markerTimeSpace = space.getMarkerTimeSpace();
		if (markerTimeSpace == null) {
			markerTimeSpace = new MarkerTimeSpace();
		}

		if (channels != null) {
			markerTimeSpace.setMarkerChannel(getMarkerChannelComboBox().getSelectedIndex());
		}

		if (markerStyles != null) {
			markerTimeSpace.setMarkerStyleName(((TagStyle) getMarkerStyleComboBox().getSelectedItem()).getName());
		}

		markerTimeSpace.setSecondsBefore(((Double) getSecondsBeforeSpinner().getValue()).doubleValue());
		markerTimeSpace.setSecondsAfter(((Double) getSecondsAfterSpinner().getValue()).doubleValue());

		space.setMarkerTimeSpace(markerTimeSpace);

	}

	public String[] getChannels() {
		return channels;
	}

	public void setChannels(String[] labels) {
		if (this.channels != labels) {
			this.channels = labels;

			getMarkerChannelComboBox().setModel(new DefaultComboBoxModel(labels));

		}
	}

	public TagStyle[] getMarkerStyles() {
		return markerStyles;
	}

	public void setMarkerStyles(TagStyle[] markerStyles) {
		if (this.markerStyles != markerStyles) {
			this.markerStyles = markerStyles;

			if (markerStyles == null) {
				getMarkerStyleComboBox().setModel(new DefaultComboBoxModel(new TagStyle[0]));
			} else {
				getMarkerStyleComboBox().setModel(new DefaultComboBoxModel(markerStyles));
			}

		}
	}

	public void setTagIconProducer(TagIconProducer tagIconProducer) {
		getMarkerStyleCellRenderer().setTagIconProducer(tagIconProducer);
	}

	public void setConstraints(SignalSpaceConstraints constraints) {

		setChannels(constraints.getSourceChannels());
		setTagIconProducer(constraints.getTagIconProducer());
		setMarkerStyles(constraints.getMarkerStyles());

	}

	public void validatePanel(Errors errors) {

		double secondsBefore = ((Double) getSecondsBeforeSpinner().getValue()).doubleValue();
		double secondsAfter = ((Double) getSecondsAfterSpinner().getValue()).doubleValue();

		if (secondsBefore == 0 && secondsAfter == 0) {
			errors.reject("error.signalSpace.noSeconds");
		}

	}

}

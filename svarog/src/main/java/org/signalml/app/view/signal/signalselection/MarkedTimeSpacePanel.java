/* MarkedTimeSpacePanel.java created 2008-01-25
 *
 */
package org.signalml.app.view.signal.signalselection;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.apache.log4j.Logger;
import org.signalml.app.model.components.validation.ValidationErrors;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.tag.TagIconProducer;
import org.signalml.app.view.tag.TagStyleListCellRenderer;
import org.signalml.domain.signal.space.MarkerTimeSpace;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.plugin.export.signal.TagStyle;



/**
 * Panel that allows the user to select the parameters of
 * {@link MarkerTimeSpace}.
 * Contains two sub-panels:
 * <ul>
 * <li>{@link #getMarkerPanel() marker panel} which allows to select the
 * channel from the list of their names and the {@link TagStyle style} of
 * the marker,</li>
 * <li>{@link #getSettingsPanel() settings panel} which allows to select
 * how many seconds before and after the marker should be taken.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MarkedTimeSpacePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(MarkedTimeSpacePanel.class);

	/**
	 * the panel which allows to select the
	 * channel from the list of their names and the {@link TagStyle style} of
	 * the marker
	 */
	private JPanel markerPanel;

	/**
	 * the panel which allows to select how many seconds before and after the
	 * marker should be taken
	 */
	private JPanel settingsPanel;

	/**
	 * the combo-box with the list of names of signal channels
	 */
	private JComboBox markerChannelComboBox;

	/**
	 * the combo-box with the list of {@link TagStyle#isMarker() marker}
	 * {@link TagStyle styles}
	 */
	private JComboBox markerStyleComboBox;

	/**
	 * the spinner which allows to select when, comparing to the marker tag,
	 * the time selection starts.
	 */
	private JSpinner startTimeSpinner;
	/**
	 * the spinner which allows to select how many seconds the time selection
	 * lasts.
	 */
	private JSpinner lengthSpinner;

	/**
	 * the names of channels,
	 * at index {@code i} - the name of the channel of index {@code i}
	 */
	private String[] channels;

	/**
	 * the array of {@link TagStyle#isMarker() marker}
	 * {@link TagStyle styles}
	 */
	private TagStyle[] markerStyles;

	/**
	 * the renderer of the elements of {@link #markerStyleComboBox}
	 */
	private TagStyleListCellRenderer markerStyleCellRenderer;

	/**
	 * Constructor. Initializes the panel.
	 */
	public MarkedTimeSpacePanel() {
		super();
		initialize();
	}

	/**
	 * Adds two sub-panels to this panel (from top to bottom):
	 * <ul>
	 * <li>{@link #markerPanel},</li>
	 * <li>{@link #settingsPanel}.</li>
	 * </ul>
	 */
	private void initialize() {

		setLayout(new BorderLayout());

		setBorder(new EmptyBorder(3,3,3,3));

		add(getMarkerPanel(), BorderLayout.CENTER);
		add(getSettingsPanel(), BorderLayout.SOUTH);

	}

	/**
	 * Returns the marker panel with the group layout.
	 * The panel contains two groups:
	 * <ul>
	 * <li>horizontal group which has two sub-groups: one for labels and one
	 * for combo boxes. This group positions the elements in two columns.</li>
	 * <li>vertical group which has 2 sub-groups - one for every row:
	 * <ul>
	 * <li>label and combo-box which allows to select the channel from the list
	 * of the names of channels,</li>
	 * <li>label and combo-box which allows to select the marker
	 * {@link TagStyle style},</li>
	 * </ul>
	 * This group positions elements in rows.</li>
	 * </ul>
	 * If the panel doesn't exist it is created
	 * @return the marker panel
	 */
	public JPanel getMarkerPanel() {
		if (markerPanel == null) {

			markerPanel = new JPanel();
			markerPanel.setBorder(new CompoundBorder(
									  new TitledBorder(_("Marker channel & style")),
									  new EmptyBorder(3,3,3,3)
								  ));

			GroupLayout layout = new GroupLayout(markerPanel);
			markerPanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			JLabel markerChannelLabel = new JLabel(_("Marker channel"));
			JLabel markerStyleLabel = new JLabel(_("Marker style"));

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

	/**
	 * Returns the combo-box which allows to select the channel from the list
	 * of the names of channels.
	 * @return the combo-box which allows to select the channel from the list
	 * of the names of channels
	 */
	public JComboBox getMarkerChannelComboBox() {
		if (markerChannelComboBox == null) {
			markerChannelComboBox = new JComboBox(new Object[0]);
			markerChannelComboBox.setPreferredSize(new Dimension(200,25));
		}
		return markerChannelComboBox;
	}

	/**
	 * Returns the combo-box which allows to select the marker
	 * {@link TagStyle style}
	 * @return the combo-box which allows to select the marker style
	 */
	public JComboBox getMarkerStyleComboBox() {
		if (markerStyleComboBox == null) {
			markerStyleComboBox = new JComboBox(new Object[0]);
			markerStyleComboBox.setPreferredSize(new Dimension(200,25));

			markerStyleComboBox.setRenderer(getMarkerStyleCellRenderer());
		}
		return markerStyleComboBox;
	}

	/**
	 * Returns the renderer of the elements of {@link #getMarkerStyleComboBox()
	 * marker style combo-box}.
	 * @return the renderer of the elements of marker style combo-box
	 */
	public TagStyleListCellRenderer getMarkerStyleCellRenderer() {
		if (markerStyleCellRenderer == null) {
			markerStyleCellRenderer = new TagStyleListCellRenderer();
		}
		return markerStyleCellRenderer;
	}

	/**
	 * Returns the settings panel with the group layout.
	 * The panel contains two groups:
	 * <ul>
	 * <li>horizontal group which has two sub-groups: one for labels and one
	 * for spinners. This group positions the elements in two columns.</li>
	 * <li>vertical group which has 2 sub-groups - one for every row:
	 * <ul>
	 * <li>label and spinner which allows to select the number of seconds
	 * before the marker that should be used,</li>
	 * <li>label and spinner which allows to select the number of seconds
	 * after the marker that should be used,</li>
	 * </ul>
	 * This group positions elements in rows.</li>
	 * </ul>
	 * If the panel doesn't exist it is created
	 * @return the settings panel
	 */
	public JPanel getSettingsPanel() {
		if (settingsPanel == null) {

			settingsPanel = new JPanel();

			settingsPanel.setBorder(new CompoundBorder(
										new TitledBorder(_("Settings")),
										new EmptyBorder(3,3,3,3)
									));

			GroupLayout layout = new GroupLayout(settingsPanel);
			settingsPanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			JLabel startTimeLabel = new JLabel(_("Start time"));
			JLabel segmentLengthLabel = new JLabel(_("Length"));

			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

			hGroup.addGroup(
				layout.createParallelGroup()
				.addComponent(startTimeLabel)
				.addComponent(segmentLengthLabel)
			);

			hGroup.addGroup(
				layout.createParallelGroup(Alignment.TRAILING)
				.addComponent(getStartTimeSpinner())
				.addComponent(getLengthSpinner())
			);

			layout.setHorizontalGroup(hGroup);

			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

			vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(startTimeLabel)
				.addComponent(getStartTimeSpinner())
			);

			vGroup.addGroup(
				layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(segmentLengthLabel)
				.addComponent(getLengthSpinner())
			);

			layout.setVerticalGroup(vGroup);
		}
		return settingsPanel;
	}

	public JSpinner getStartTimeSpinner() {
		if (startTimeSpinner == null) {
			startTimeSpinner = new JSpinner(new SpinnerNumberModel(1.0,-3600.0,3600,0.1));
			Dimension fixedSize = new Dimension(200,25);
			startTimeSpinner.setPreferredSize(fixedSize);
		}
		return startTimeSpinner;
	}

	public JSpinner getLengthSpinner() {
		if (lengthSpinner == null) {
			lengthSpinner = new JSpinner(new SpinnerNumberModel(1.0,0.1,3600,0.1));
			Dimension fixedSize = new Dimension(200,25);
			lengthSpinner.setPreferredSize(fixedSize);
		}
		return lengthSpinner;
	}

	/**
	 * Fills the fields of this panel using the {@link MarkerTimeSpace} from
	 * given {@link SignalSpace}:
	 * <ul>
	 * <li>if the MarkerTimeSpace is {@code null} fields are set to default values:
	 * <ul><li>first entry of combo boxes is set to be active,</li>
	 * <li>number of seconds before marker is set to {@code 0} and after the
	 * marker to {@code 1},</li></ul>
	 * <li>if {@link #getChannels() channels} exist the
	 * {@link MarkerTimeSpace#getMarkerChannel() active} one is set,</li>
	 * <li>if {@link #getMarkerStyles() marker styles} exist the
	 * {@link MarkerTimeSpace#getMarkerStyleName() active} one is set,</li>
	 * </li>number of seconds {@link MarkerTimeSpace#getStartTime() before}
	 * and {@link MarkerTimeSpace#getSegmentLength() after} the marker is set
	 * to spinners.</li>
	 * </ul>
	 * @param space the signal space
	 */
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

			getLengthSpinner().setValue(new Double(1));
			getStartTimeSpinner().setValue(new Double(0));

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

			getStartTimeSpinner().setValue(markerTimeSpace.getStartTime());
			getLengthSpinner().setValue(markerTimeSpace.getSegmentLength());

		}

	}

	/**
	 * Stores the user input from this dialog to the {@link SignalSpace signal
	 * space}:
	 * <ul>
	 * <li>if the {@link MarkerTimeSpace} {@link SignalSpace#getMarkerTimeSpace()
	 * in} signal space doesn't exist it is created,</li>
	 * <li>sets the {@link MarkerTimeSpace#setMarkerChannel(int) marker channel}
	 * and the {@link MarkerTimeSpace#setMarkerStyleName(String) name} of the
	 * {@link TagStyle style}</li>
	 * <li>sets the number of seconds
	 * {@link MarkerTimeSpace#setStartTime(double) before} and
	 * {@link MarkerTimeSpace#setSegmentLength(double) after} the marker.</li>
	 * </ul>
	 * @param space the singal space in which the data will be stored
	 */
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

		markerTimeSpace.setStartTime(((Double) getStartTimeSpinner().getValue()).doubleValue());
		markerTimeSpace.setSegmentLength(((Double) getLengthSpinner().getValue()).doubleValue());

		space.setMarkerTimeSpace(markerTimeSpace);

	}

	/**
	 * Returns the array with the names of channels,
	 * at index {@code i} - the name of the channel of index {@code i}.
	 * @return the array with the names of channels
	 */
	public String[] getChannels() {
		return channels;
	}

	/**
	 * Sets  the array with the names of channels,
	 * at index {@code i} - the name of the channel of index {@code i}.
	 * @param labels the array with the names of channels
	 */
	public void setChannels(String[] labels) {
		if (this.channels != labels) {
			this.channels = labels;

			getMarkerChannelComboBox().setModel(new DefaultComboBoxModel(labels));

		}
	}

	/**
	 * Returns the array of {@link TagStyle#isMarker() marker}
	 * {@link TagStyle styles}.
	 * @return the array of marker styles
	 */
	public TagStyle[] getMarkerStyles() {
		return markerStyles;
	}

	/**
	 * Sets the array of {@link TagStyle#isMarker() marker}
	 * {@link TagStyle styles}.
	 * @param markerStyles the array of marker styles
	 */
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

	/**
	 * Sets the {@link TagIconProducer producer} of icons for {@link TagStyle
	 * tag styles}.
	 * @param tagIconProducer the producer of icons for tag styles
	 */
	public void setTagIconProducer(TagIconProducer tagIconProducer) {
		getMarkerStyleCellRenderer().setTagIconProducer(tagIconProducer);
	}

	/**
	 * Sets the {@link SignalSpaceConstraints parameters} of the signal:
	 * <ul>
	 * <li>the {@link #setChannels(String[]) names} of channels,</li>
	 * <li>the {@link #setMarkerStyles(TagStyle[]) styles} of markers,</li>
	 * <li>the {@link #setTagIconProducer(TagIconProducer) producer} of icons
	 * for these styles.</li>
	 * </ul>
	 * @param constraints the parameters of the signal
	 */
	public void setConstraints(SignalSpaceConstraints constraints) {

		setChannels(constraints.getSourceChannels());
		setTagIconProducer(constraints.getTagIconProducer());
		setMarkerStyles(constraints.getMarkerStyles());

	}

	/**
	 * Validates this panel.
	 * @param errors the object in which the errors are stored.
	 */
	public void validatePanel(ValidationErrors errors) {
	}

}

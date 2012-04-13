package org.signalml.plugin.exampleplugin;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InvalidClassException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.signal.ExportedSignalSelectionType;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagDocument;
import org.signalml.plugin.export.signal.ExportedTagStyle;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.plugin.export.view.AbstractDialog;

/**
 * This dialog allows the user to create a custom (precise) tag.
 * Contains fields that allow to specify parameters of the tag:
 * <ul>
 * <li>the {@link ExportedSignalSelectionType type} of the
 * {@link ExportedTag tag},</li>
 * <li>the {@link ExportedTagStyle style} of the tag,</li>
 * <li>the number of the channel (which is active only if the type
 * of the tag is {@code CHANNEL},</li>
 * <li>the position where the tag starts (seconds),</li>
 * <li>the length of the tag.</li>
 * </ul>
 *
 * @author Marcin Szumski
 */
public class PreciseTagDialog extends AbstractDialog implements ActionListener, ChangeListener {

	private static final long serialVersionUID = 1L;

	/**
	 * the logger that is consistent with logging framework of Svarog;
	 * requires {@code log4j.jar} as the library
	 */
	private static final Logger logger = Logger.getLogger(PreciseTagDialog.class);

	/**
	 * the {@link SvarogAccessSignal access} to signal options
	 */
	private SvarogAccessSignal signalAccess;

	/**
	 * the combo-box in which the possible {@link ExportedSignalSelectionType
	 * types} of the tag are displayed (BLOCK, PAGE, CHANNEL)
	 */
	private JComboBox typesBox;

	/**
	 * the combo-box in which the {@link ExportedTagStyle styles} of the
	 * currently active {@link ExportedSignalSelectionType type} are displayed
	 */
	private JComboBox stylesBox = new JComboBox();

	/**
	 * the combo-box in which the names of channels of the signal are displayed
	 * if the currently selected {@link ExportedSignalSelectionType type}
	 * is a CHANNEL
	 */
	private JComboBox channelBox = new JComboBox();

	/**
	 * the spinner with the position (in time - seconds) where the tag should
	 * start;
	 * <p>
	 * value of this spinner must be within {@code [0,length]}, where
	 * {@code length} is the length of the shortest channel of the signal;
	 * <p>
	 * step of this spinner depends on the selected
	 * {@link ExportedSignalSelectionType type}: for {@code BLOCK} it is the
	 * size of the block, for {@code PAGE} it is the size of the page, and for
	 * {@code CHANNEL} it is 1
	 */
	private SpinnerNumberModel startSpinnerModel;

	/**
	 * the spinner with the length (seconds) of the {@link ExportedTag tag};
	 * <p>
	 * value of this spinner must be within {@code [0,length-start]}, where
	 * {@code length} is the length of the shortest channel of the signal and
	 * {@code start} is the {@link #startSpinnerModel position} where tag
	 * starts
	 * <p>
	 * step of this spinner depends on the selected
	 * {@link ExportedSignalSelectionType type}: for {@code BLOCK} it is the
	 * size of the block, for {@code PAGE} it is the size of the page, and for
	 * {@code CHANNEL} it is 1
	 */
	private SpinnerNumberModel lengthSpinnerModel;

	/**
	 * the {@link ExportedTagDocument document} to which the created
	 * {@link ExportedTag tag} is to be added
	 */
	private ExportedTagDocument tagDocument = null;

	/**
	 * the {@link ExportedSignalDocument document} with the signal
	 */
	private ExportedSignalDocument signalDocument = null;

	/**
	 * Constructor. Sets {@link SvarogAccessSignal signal access}.
	 * @param signalAccess access to set
	 */
	public PreciseTagDialog(SvarogAccessSignal signalAccess) {
		this.signalAccess = signalAccess;
	}

	/**
	 * Creates the {@link #typesBox}.
	 * @return the created types box
	 */
	private JComboBox selectionTypesBox() {
		String[] types = new String[] {
			ExportedSignalSelectionType.BLOCK,
			ExportedSignalSelectionType.CHANNEL,
			ExportedSignalSelectionType.PAGE
		};

		typesBox = new JComboBox(types);
		typesBox.addActionListener(this);
		return typesBox;
	}

	/**
	 * Creates a panel with BorderLayout and the CompoundBorder with the given
	 * text.
	 * @param name the text to be set on the border
	 * @return the created panel
	 */
	private JPanel createNamedPanel(String name) {
		JPanel panel = new JPanel(new BorderLayout());
		CompoundBorder cb = new CompoundBorder(
			new TitledBorder(name),
			null
		);
		panel.setBorder(cb);
		return panel;
	}

	/**
	 * Creates a panel with the {@link #typesBox} and the border with
	 * text {@code "type"}.
	 * @return the created panel
	 */
	private JPanel createTypesPanel() {
		JPanel panel = createNamedPanel("type");
		panel.add(selectionTypesBox());
		return panel;
	}

	/**
	 * Creates a panel with the {@link #stylesBox} and the border with
	 * text {@code "style"}.
	 * @return the created panel
	 */
	private JPanel createStylesPanel() {
		JPanel panel = createNamedPanel("style");
		panel.add(stylesBox);
		return panel;
	}

	/**
	 * Creates a panel with the {@link #channelBox} and the border with
	 * text {@code "channel"}.
	 * @return the created panel
	 */
	private JPanel createChannelPanel() {
		JPanel panel = createNamedPanel("channel");
		panel.add(channelBox);
		return panel;
	}

	/**
	 * Creates a panel with the spinner with model {@link #startSpinnerModel}
	 * and the border with text {@code "position"}.
	 * @return the created panel
	 */
	private JPanel createStartPanel() {
		JPanel panel = createNamedPanel("position");
		startSpinnerModel = new SpinnerNumberModel(0.0, 0.0, getSignalDocument().getMinSignalLength(), 1);
		startSpinnerModel.addChangeListener(this);
		JSpinner startSpinner = new JSpinner(startSpinnerModel);
		panel.add(startSpinner);
		return panel;
	}

	/**
	 * Creates a panel with the spinner with model {@link #lengthSpinnerModel}
	 * and the border with text {@code "length"}.
	 * @return the created panel
	 */
	private JPanel createLengthPanel() {
		JPanel panel = createNamedPanel("length");
		lengthSpinnerModel = new SpinnerNumberModel(1.0, 0.0, getSignalDocument().getMinSignalLength(), 1);
		lengthSpinnerModel.addChangeListener(this);
		JSpinner lenghtSpinner = new JSpinner(lengthSpinnerModel);
		panel.add(lenghtSpinner);
		return panel;
	}

	/**
	 * Creates a panel with box layout that contains sub-panels which allow to
	 * select:
	 * <ul>
	 * <li>the {@link ExportedSignalSelectionType type} of the
	 * {@link ExportedTag tag}</li>
	 * <li>the {@link ExportedTagStyle style} of the tag</li>
	 * <li>the number of the channel (which is active only if the type
	 * of the tag is {@code CHANNEL}</li>
	 * <li>the position where the tag starts (seconds)</li>
	 * <li>the length of the tag</li>
	 * </ul>
	 */
	@Override
	protected JComponent createInterface() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		panel.add(createTypesPanel());
		JPanel styleAndChannelPanel = new JPanel();
		styleAndChannelPanel.setLayout(new BoxLayout(styleAndChannelPanel, BoxLayout.LINE_AXIS));
		styleAndChannelPanel.add(createStylesPanel());
		styleAndChannelPanel.add(createChannelPanel());
		panel.add(styleAndChannelPanel);
		JPanel positionAndLengthPanel = new JPanel();
		positionAndLengthPanel.setLayout(new BoxLayout(positionAndLengthPanel, BoxLayout.LINE_AXIS));
		positionAndLengthPanel.add(createStartPanel());
		positionAndLengthPanel.add(createLengthPanel());
		panel.add(positionAndLengthPanel);


		updateStylesBox();
		updateChannelBox();
		updateStartSpinner();
		updateLenghtSpinner();
		return panel;
	}

	/**
	 * There is no model for this dialog, so always true is returned.
	 */
	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.view.AbstractDialog#fillDialogFromModel(java.lang.Object)
	 */
	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		//nothing to do
	}

	/**
	 * Reads the parameters of the {@link Tag tag} and based on them creates a tag.
	 * Adds the created style to the {@link ExportedSignalDocument signal document}.
	 * No model is used.
	 */
	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		Set<ExportedTagStyle> styles = getTagDocument().getTagStyles();
		String selectedStyleName = (String) stylesBox.getSelectedItem();
		ExportedTagStyle selectedStyle = null;
		for (ExportedTagStyle style : styles) {
			if (style.getName().equals(selectedStyleName)) {
				selectedStyle = style;
			}
		}
		double length = (Double) lengthSpinnerModel.getValue();
		double position = (Double) startSpinnerModel.getValue();
		int channel = channelBox.getSelectedIndex();
		ExportedTag tag = new Tag(new TagStyle(selectedStyle), (float) position, (float) length, channel);
		try {
			signalAccess.addTagToDocument(tagDocument, tag);
		} catch (InvalidClassException e) {
			logger.error("Tag document has not valid type. Shouldn't occur");
		} catch (IllegalArgumentException e) {
			logger.error("There is no such style in document. Shouldn't occur");
		}
	}

	/**
	 * Updates the list of {@link ExportedTagStyle styles} in
	 * the {@link #stylesBox}.
	 * Removes all styles and adds those that have the currently selected
	 * {@link ExportedSignalSelectionType type}.
	 */
	private void updateStylesBox() {
		stylesBox.removeAllItems();
		String type = (String) typesBox.getSelectedItem();
		Set<ExportedTagStyle> styles = getTagDocument().getTagStyles();
		Set<ExportedTagStyle> selectedStyles = new HashSet<ExportedTagStyle>();
		for (ExportedTagStyle style : styles) {
			if (style.getType().getName().equals(type))
				selectedStyles.add(style);
		}
		for (ExportedTagStyle style : selectedStyles) {
			stylesBox.addItem(style.getName());
		}
	}

	/**
	 * If the currently selected {@link ExportedSignalSelectionType type}
	 * is a {@code CHANNEL} adds the names of channels to {@link #channelBox}
	 * and sets it to be enabled.
	 * If the currently selected type is not a {@code CHANNEL} sets
	 * {@link #channelBox} to be disabled.
	 */
	private void updateChannelBox() {
		channelBox.removeAllItems();
		String type = (String) typesBox.getSelectedItem();
		if (type.equals(ExportedSignalSelectionType.CHANNEL)) {
			channelBox.setEnabled(true);
			int channelCount = getSignalDocument().getChannelCount();
			List<String> labels = getSignalDocument().getSourceChannelLabels();
			for (int i = 0; i < channelCount; ++i) {
				channelBox.addItem(labels.get(i));
			}
		} else
			channelBox.setEnabled(false);
	}

	/**
	 * Called when the selected value of the {@code typesBox}
	 * has changed.
	 * Updates the styles box, the channel box, the start spinner and
	 * the length spinner.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		updateStylesBox();
		updateChannelBox();
		updateStartSpinner();
		updateLenghtSpinner();
	}

	/**
	 * Updates the {@link #lengthSpinnerModel}.
	 * Sets:
	 * <ul>
	 * <li>the maximal value of the spinner to be {@code maximum - position},
	 * where {@code maximum} is the length of the signal and {@code position}
	 * is the position where the tag starts,</li>
	 * <li>the value of the spinner, if the current value is larger then
	 * the maximal value of the spinner (after the change)</li>
	 * <li>the value of the spinner to the multiple of block/page size
	 * (the largest multiple that is less then the current value) if the
	 * currently selected type is {@code PAGE/BLOCK}</li>
	 * <li>the step of the spinner to the size of block/page if the
	 * currently selected type is {@code PAGE/BLOCK} or to 1 if
	 * the type is {@code CHANNEL}</li>
	 * </ul>
	 */
	private void updateLenghtSpinner() {
		double position = (Double) startSpinnerModel.getValue();
		double length = (Double) lengthSpinnerModel.getValue();
		double maximum = (Double) startSpinnerModel.getMaximum();
		String type = (String) typesBox.getSelectedItem();
		if (length + position > maximum) {
			length = maximum - position;
		}
		if (type.equals(ExportedSignalSelectionType.PAGE)) {
			float pageSize = getSignalDocument().getPageSize();
			int numberOfPages = (int)(length / pageSize);
			length = numberOfPages * pageSize;
			lengthSpinnerModel.setStepSize(pageSize);
		} else if (type.equals(ExportedSignalSelectionType.BLOCK)) {
			float blockSize = getSignalDocument().getBlockSize();
			int numberOfPages = (int)(length / blockSize);
			length = numberOfPages * blockSize;
			lengthSpinnerModel.setStepSize(blockSize);
		} else {
			lengthSpinnerModel.setStepSize(1);
		}
		lengthSpinnerModel.setValue(length);
		lengthSpinnerModel.setMaximum(maximum - position);
	}

	/**
	 * Updates the {@link #startSpinnerModel}.
	 * Sets:
	 * <ul>
	 * <li>the value of the spinner to the multiple of block/page size
	 * (the largest multiple that is less then the current value) if the
	 * currently selected type is {@code PAGE/BLOCK}</li>
	 * <li>the step of the spinner to the size of block/page if the
	 * currently selected type is {@code PAGE/BLOCK} or to 1 if
	 * the type is {@code CHANNEL}</li>
	 * </ul>
	 */
	private void updateStartSpinner() {
		double position = (Double) startSpinnerModel.getValue();
		String type = (String) typesBox.getSelectedItem();
		if (type.equals(ExportedSignalSelectionType.PAGE)) {
			float pageSize = getSignalDocument().getPageSize();
			int numberOfAPage = (int)(position / pageSize);
			position = pageSize*numberOfAPage;
			startSpinnerModel.setStepSize(pageSize);
		} else if (type.equals(ExportedSignalSelectionType.BLOCK)) {
			float blockSize = getSignalDocument().getBlockSize();
			int numberOfABlock = (int)(position / blockSize);
			position = blockSize*numberOfABlock;
			startSpinnerModel.setStepSize(blockSize);
		} else {
			startSpinnerModel.setStepSize(1);
		}
		startSpinnerModel.setValue(position);

	}

	/**
	 * Called when the value of {@code startSpinnerModel} changes.
	 * Updates the length spinner.
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		updateLenghtSpinner();
	}


	/**
	 * Getter for {@link #signalDocument}.
	 * If the document is {@code null} the active document is set
	 * as a value.
	 * @return the signalDocument
	 */
	private ExportedSignalDocument getSignalDocument() {
		if (signalDocument == null) {
			try {
				signalDocument = signalAccess.getActiveSignalDocument();
			} catch (NoActiveObjectException e) {
				logger.error("no active tag or signal document while there should be one");
			}
		}
		return signalDocument;
	}


	/**
	 * Getter for {@link #tagDocument}.
	 * If the document is {@code null} the active tag document is set
	 * as a value.
	 * @return the tagDocument
	 */
	private ExportedTagDocument getTagDocument() {
		if (tagDocument == null) {
			try {
				tagDocument = signalAccess.getActiveTagDocument();
			} catch (NoActiveObjectException e) {
				logger.error("no active tag or signal document while there should be one");
			}
		}
		return tagDocument;
	}

}

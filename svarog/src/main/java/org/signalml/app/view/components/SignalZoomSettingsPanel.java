/* ZoomSettingsPanel.java created 2007-12-17
 *
 */
package org.signalml.app.view.components;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.config.ZoomSignalSettings;
import org.signalml.app.model.components.validation.ValidationErrors;

import org.springframework.validation.Errors;

/**
 * Panel which allows to select how the zoomed signal should be displayed.
 * Contains 3 sub-panels:
 * <ul>
 * <li>the panel with radio buttons which allows to select the zoom factor,
 * </li><li>the panel with radio buttons which allows to select the size of
 * the window with zoomed signal,</li>
 * <li>the panel with the check-box which tells if the displayed channel
 * should be changed when mouse goes up or down.</li></ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalZoomSettingsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SignalZoomSettingsPanel.class);

	/**
	 * the possible zoom factors
	 */
	private float[] possibleZoomFactors = new float[] { 2, 4, 6, 8 };
	/**
	 * the possible sizes of the zoom window
	 */
	private int[] possibleZoomSizes = new int[] { 100, 200, 400, 600 };

	/**
	 * the array with radio buttons for possible zoom factors
	 */
	private JRadioButton[] zoomFactorRadioButtons;
	/**
	 * the array with radio buttons for possible sizes of the zoom window
	 */
	private JRadioButton[] zoomSizeRadioButtons;
	/**
	 * the check-box which tells if the displayed channel should be changed
	 * when mouse goes up or down
	 */
	private JCheckBox channelSwitchingCheckBox;

	/**
	 * the group of radio buttons with possible zoom factors
	 */
	private ButtonGroup factorButtonGroup;
	/**
	 * the group of radio buttons with possible sizes of the zoom window 
	 */
	private ButtonGroup sizeButtonGroup;

	/**
	 * {@code true} if the panel has a cross which closes it,
	 * {@code false} otherwise
	 */
	private boolean hasCloseCross;

	/**
	 * Constructor. Initializes the panel.
	 * @param hasCloseCross {@code true} if the panel should has a cross which
	 * closes it, {@code false} otherwise
	 */
	public SignalZoomSettingsPanel(boolean hasCloseCross) {
		super();
		this.hasCloseCross = hasCloseCross;
		initialize();
	}

	/**
	 * Initializes this panel with GridLayout and 3 sub-panels (from top to
	 * bottom):
	 * <ul>
	 * <li>the panel with radio buttons which allows to select the zoom factor,
	 * </li><li>the panel with radio buttons which allows to select the size of
	 * the window with zoomed signal,</li>
	 * <li>the panel with the check-box which tells if the displayed channel
	 * should be changed when mouse goes up or down.</li></ul>
	 */
	private void initialize() {

		factorButtonGroup = new ButtonGroup();
		sizeButtonGroup = new ButtonGroup();

		setLayout(new GridLayout(3, 1, 3, 3));

		JPanel factorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));

		CompoundBorder border = new CompoundBorder(
		        new TitledCrossBorder(_("Zoom factor"), hasCloseCross),
		        new EmptyBorder(3,3,3,3)
		);
		factorPanel.setBorder(border);

		Dimension radioSize = new Dimension(50,20);

		zoomFactorRadioButtons = new JRadioButton[possibleZoomFactors.length];
		int i;
		for (i=0; i<possibleZoomFactors.length; i++) {
			zoomFactorRadioButtons[i] = new JRadioButton(Float.toString(possibleZoomFactors[i]));
			zoomFactorRadioButtons[i].setPreferredSize(radioSize);
			factorButtonGroup.add(zoomFactorRadioButtons[i]);
			factorPanel.add(zoomFactorRadioButtons[i]);
		}

		zoomFactorRadioButtons[0].setSelected(true);

		JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));

		border = new CompoundBorder(
		        new TitledBorder(_("Zoom window size")),
		        new EmptyBorder(3,3,3,3)
		);
		sizePanel.setBorder(border);

		zoomSizeRadioButtons = new JRadioButton[possibleZoomSizes.length];
		for (i=0; i<possibleZoomSizes.length; i++) {
			zoomSizeRadioButtons[i] = new JRadioButton(Integer.toString(possibleZoomSizes[i]));
			zoomSizeRadioButtons[i].setPreferredSize(radioSize);
			sizeButtonGroup.add(zoomSizeRadioButtons[i]);
			sizePanel.add(zoomSizeRadioButtons[i]);
		}

		zoomSizeRadioButtons[0].setSelected(true);

		JPanel switchingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));

		border = new CompoundBorder(
		        new TitledBorder(_("Channel switching")),
		        new EmptyBorder(3,3,3,3)
		);
		switchingPanel.setBorder(border);

		channelSwitchingCheckBox = new JCheckBox(_("Switch channels as mouse moves"));
		switchingPanel.add(channelSwitchingCheckBox);

		add(factorPanel);
		add(sizePanel);
		add(switchingPanel);

		Dimension size = getPreferredSize();
		if (size.width < 150) {
			size.width = 150;
		}
		setPreferredSize(size);

	}

	/**
	 * Fills the fields of this panel using given {@link ZoomSignalSettings
	 * settings}:
	 * <ul>
	 * <li>the {@link ZoomSignalSettings#getFactor() zoom factor},</li>
	 * <li>the {@link ZoomSignalSettings#getZoomSize() size} of a window
	 * with a zoomed signal,</li>
	 * <li>the boolean if the channel should be
	 * {@link ZoomSignalSettings#isChannelSwitching() switched}
	 * when the mouse goes up or down.</li>
	 * @param settings the settings of signal zooming
	 */
	public void fillPanelFromModel(ZoomSignalSettings settings) {

		float factor = settings.getFactor();
		int i;
		for (i=0; i<possibleZoomFactors.length; i++) {
			if (factor == possibleZoomFactors[i]) {
				zoomFactorRadioButtons[i].setSelected(true);
				break;
			}
		}
		Dimension size = settings.getZoomSize();
		for (i=0; i<possibleZoomSizes.length; i++) {
			if (size.width == possibleZoomSizes[i]) {
				zoomSizeRadioButtons[i].setSelected(true);
				break;
			}
		}
		channelSwitchingCheckBox.setSelected(settings.isChannelSwitching());

	}

	/**
	 * Stores the user input in the given {@link ZoomSignalSettings settings}:
	 * <ul>
	 * <li>the {@link ZoomSignalSettings#getFactor() zoom factor},</li>
	 * <li>the {@link ZoomSignalSettings#getZoomSize() size} of a window
	 * with a zoomed signal,</li>
	 * <li>the boolean if the channel should be
	 * {@link ZoomSignalSettings#isChannelSwitching() switched} when the mouse
	 * goes up or down.</li>
	 * @param settings the settings of signal zooming
	 */
	public void fillModelFromPanel(ZoomSignalSettings settings) {

		int i;
		for (i=0; i<possibleZoomFactors.length; i++) {
			if (zoomFactorRadioButtons[i].isSelected()) {
				settings.setFactor(possibleZoomFactors[i]);
				break;
			}
		}
		for (i=0; i<possibleZoomSizes.length; i++) {
			if (zoomSizeRadioButtons[i].isSelected()) {
				settings.setZoomSize(new Dimension(possibleZoomSizes[i], possibleZoomSizes[i]));
				break;
			}
		}
		settings.setChannelSwitching(channelSwitchingCheckBox.isSelected());


	}

	/**
	 * Validates this panel. This panel is always valid.
	 * @param errors the object in which the errors should be stored
	 */
	public void validate(ValidationErrors errors) {
		// do nothing
	}

}

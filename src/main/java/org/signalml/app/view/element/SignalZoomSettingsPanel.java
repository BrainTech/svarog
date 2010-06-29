/* ZoomSettingsPanel.java created 2007-12-17
 *
 */
package org.signalml.app.view.element;

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
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** ZoomSettingsPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalZoomSettingsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SignalZoomSettingsPanel.class);

	private MessageSourceAccessor messageSource;

	private float[] possibleZoomFactors = new float[] { 2, 4, 6, 8 };
	private int[] possibleZoomSizes = new int[] { 100, 200, 400, 600 };

	private JRadioButton[] zoomFactorRadioButtons;
	private JRadioButton[] zoomSizeRadioButtons;
	private JCheckBox channelSwitchingCheckBox;

	private ButtonGroup factorButtonGroup;
	private ButtonGroup sizeButtonGroup;

	private boolean hasCloseCross;

	public SignalZoomSettingsPanel(MessageSourceAccessor messageSource, boolean hasCloseCross) {
		super();
		this.messageSource = messageSource;
		this.hasCloseCross = hasCloseCross;
		initialize();
	}

	private void initialize() {

		factorButtonGroup = new ButtonGroup();
		sizeButtonGroup = new ButtonGroup();

		setLayout(new GridLayout(3, 1, 3, 3));

		JPanel factorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));

		CompoundBorder border = new CompoundBorder(
		        new TitledCrossBorder(messageSource.getMessage("zoomSettings.factorTitle"), hasCloseCross),
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
		        new TitledBorder(messageSource.getMessage("zoomSettings.sizeTitle")),
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
		        new TitledBorder(messageSource.getMessage("zoomSettings.switchingTitle")),
		        new EmptyBorder(3,3,3,3)
		);
		switchingPanel.setBorder(border);

		channelSwitchingCheckBox = new JCheckBox(messageSource.getMessage("zoomSettings.channelSwitching"));
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

	public void validate(Errors errors) {
		// do nothing
	}

}

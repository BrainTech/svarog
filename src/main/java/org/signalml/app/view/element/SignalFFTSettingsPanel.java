/* SignalFFTSettingsPanel.java created 2007-12-17
 *
 */
package org.signalml.app.view.element;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.signalml.app.config.SignalFFTSettings;
import org.signalml.exception.SanityCheckException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/**
 * SignalFFTSettingsPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe
 *         Sp. z o.o.
 */
public class SignalFFTSettingsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(SignalFFTSettingsPanel.class);

	private MessageSourceAccessor messageSource;

	private int[] possibleWindowWidths = new int[] { 64, 128, 256, 512, 1024, 2048 };
	private int[] possiblePlotWidths = new int[] { 300, 400, 500, 600, 700, 800 };
	private int[] possiblePlotHeights = new int[] { 200, 300, 400, 500, 600, 700 };

	private FFTWindowTypePanel fftWindowTypePanel;

	private JRadioButton customWindowWidthRadioButton;
	private JRadioButton[] windowWidthRadioButtons;
	private JRadioButton[] plotWidthRadioButtons;
	private JRadioButton[] plotHeightRadioButtons;

	private JCheckBox channelSwitchingCheckBox;
	private JCheckBox logarithmicCheckBox;
	private JCheckBox splineCheckBox;
	private JCheckBox antialiasCheckBox;
	private JCheckBox titleVisibleCheckBox;
	private JCheckBox frequencyAxisLabelsVisibleCheckBox;
	private JCheckBox powerAxisLabelsVisibleCheckBox;

	private JTextField customWindowWidthTextField;

	private JTextField visibleRangeStartTextField;
	private JTextField visibleRangeEndTextField;
	private JTextField maxLabelCountTextField;
	private JCheckBox scaleToFFTViewCheckBox;

	private ButtonGroup windowWidthButtonGroup;
	private ButtonGroup plotWidthButtonGroup;
	private ButtonGroup plotHeightButtonGroup;

	private String cachedWindowWidth = null;

	private boolean hasCloseCross;

	public SignalFFTSettingsPanel(MessageSourceAccessor messageSource, boolean hasCloseCross) {
		super();
		this.messageSource = messageSource;
		this.hasCloseCross = hasCloseCross;
		initialize();
	}

	private void initialize() {

		windowWidthButtonGroup = new ButtonGroup();
		plotWidthButtonGroup = new ButtonGroup();
		plotHeightButtonGroup = new ButtonGroup();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JPanel windowWidthPanel = new JPanel(new GridLayout(3, 3, 3, 3));

		CompoundBorder border = new CompoundBorder(new TitledCrossBorder(messageSource
		                .getMessage("signalFFTSettings.windowWidthTitle"), hasCloseCross), new EmptyBorder(3, 3, 3, 3));
		windowWidthPanel.setBorder(border);

		ItemListener cachingListener = new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
				if (selected) {
					cachedWindowWidth = ((JRadioButton) e.getItem()).getText();
				}
			}

		};

		windowWidthRadioButtons = new JRadioButton[possibleWindowWidths.length];
		int i;
		for (i = 0; i < possibleWindowWidths.length; i++) {
			windowWidthRadioButtons[i] = new JRadioButton(Integer.toString(possibleWindowWidths[i]));
			windowWidthButtonGroup.add(windowWidthRadioButtons[i]);
			windowWidthPanel.add(windowWidthRadioButtons[i]);
			windowWidthRadioButtons[i].addItemListener(cachingListener);
		}

		windowWidthRadioButtons[0].setSelected(true);

		customWindowWidthRadioButton = new JRadioButton(messageSource.getMessage("signalFFTSettings.customWindowWidth"));
		windowWidthButtonGroup.add(customWindowWidthRadioButton);
		windowWidthPanel.add(customWindowWidthRadioButton);

		customWindowWidthTextField = new JTextField("");
		customWindowWidthTextField.setPreferredSize(new Dimension(80, 25));
		customWindowWidthTextField.setEnabled(false);

		windowWidthPanel.add(customWindowWidthTextField);

		customWindowWidthRadioButton.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
				if (selected) {
					if (customWindowWidthTextField.getText().isEmpty()) {
						if (cachedWindowWidth == null) {
							customWindowWidthTextField.setText(windowWidthRadioButtons[0].getText());
						} else {
							customWindowWidthTextField.setText(cachedWindowWidth);
						}
					}
				}
				customWindowWidthTextField.setEnabled(selected);
			}

		});

		JPanel fftViewPanel = new JPanel(new GridLayout(2, 2, 3, 3));
		{
			border = new CompoundBorder(new TitledBorder(messageSource.getMessage("signalFFTSettings.fftViewTitle")),
			                            new EmptyBorder(3, 3, 3, 3));
			fftViewPanel.setBorder(border);

			JLabel label = new JLabel(messageSource.getMessage("signalFFTSettings.fftViewRange"));
			fftViewPanel.add(label);

			InputVerifier intInputVerifier = new InputVerifier() {
				@Override
				public boolean verify(JComponent input) {
					try {
						String text = ((JTextField) input).getText().trim();
						if (text.length() == 0 || Integer.parseInt(text) >= 0)
							return true;
						else {
							JOptionPane.showMessageDialog(input.getParent(), messageSource
							                              .getMessage("signalFFTSettings.fftPositiveIntegerRequired"));
							return false;
						}
					} catch (NumberFormatException nfe) {
						JOptionPane.showMessageDialog(input.getParent(), messageSource
						                              .getMessage("signalFFTSettings.fftPositiveIntegerRequired"));
						return false;
					}
				}
			};

			JPanel rangePanel = new JPanel();
			visibleRangeStartTextField = new JTextField();
			visibleRangeStartTextField.setColumns(5);
			visibleRangeStartTextField.setInputVerifier(intInputVerifier);
			visibleRangeEndTextField = new JTextField();
			visibleRangeEndTextField.setColumns(5);
			visibleRangeEndTextField.setInputVerifier(intInputVerifier);
			rangePanel.add(visibleRangeStartTextField);
			rangePanel.add(new JLabel("-"));
			rangePanel.add(visibleRangeEndTextField);
			fftViewPanel.add(rangePanel);

			JPanel countPanel = new JPanel();

			label = new JLabel(messageSource.getMessage("signalFFTSettings.fftViewLabelCountTitle"));
			countPanel.add(label);

			maxLabelCountTextField = new JTextField();
			maxLabelCountTextField.setColumns(5);
			maxLabelCountTextField.setInputVerifier(intInputVerifier);
			countPanel.add(maxLabelCountTextField);
			fftViewPanel.add(countPanel);

			scaleToFFTViewCheckBox = new JCheckBox(messageSource.getMessage("signalFFTSettings.fftAutoScaleToView"));
			fftViewPanel.add(scaleToFFTViewCheckBox);

		}

		JPanel plotWidthPanel = new JPanel(new GridLayout(1, 4, 3, 3));

		border = new CompoundBorder(new TitledBorder(messageSource.getMessage("signalFFTSettings.plotWidthTitle")),
		                            new EmptyBorder(3, 3, 3, 3));
		plotWidthPanel.setBorder(border);

		Dimension radioSize = new Dimension(50, 20);

		plotWidthRadioButtons = new JRadioButton[possiblePlotWidths.length];
		for (i = 0; i < possiblePlotWidths.length; i++) {
			plotWidthRadioButtons[i] = new JRadioButton(Integer.toString(possiblePlotWidths[i]));
			plotWidthRadioButtons[i].setPreferredSize(radioSize);
			plotWidthButtonGroup.add(plotWidthRadioButtons[i]);
			plotWidthPanel.add(plotWidthRadioButtons[i]);
		}

		plotWidthRadioButtons[0].setSelected(true);

		JPanel plotHeightPanel = new JPanel(new GridLayout(1, 4, 3, 3));

		border = new CompoundBorder(new TitledBorder(messageSource.getMessage("signalFFTSettings.plotHeightTitle")),
		                            new EmptyBorder(3, 3, 3, 3));
		plotHeightPanel.setBorder(border);

		plotHeightRadioButtons = new JRadioButton[possiblePlotHeights.length];
		for (i = 0; i < possiblePlotHeights.length; i++) {
			plotHeightRadioButtons[i] = new JRadioButton(Integer.toString(possiblePlotHeights[i]));
			plotHeightRadioButtons[i].setPreferredSize(radioSize);
			plotHeightButtonGroup.add(plotHeightRadioButtons[i]);
			plotHeightPanel.add(plotHeightRadioButtons[i]);
		}

		plotHeightRadioButtons[0].setSelected(true);

		fftWindowTypePanel = new FFTWindowTypePanel(messageSource, false);

		JPanel optionsPanel = new JPanel(new GridLayout(4, 2, 3, 3));

		border = new CompoundBorder(new TitledBorder(messageSource.getMessage("signalFFTSettings.optionsTitle")),
		                            new EmptyBorder(3, 3, 3, 3));
		optionsPanel.setBorder(border);

		channelSwitchingCheckBox = new JCheckBox(messageSource.getMessage("signalFFTSettings.channelSwitching"));
		logarithmicCheckBox = new JCheckBox(messageSource.getMessage("signalFFTSettings.logarithmic"));
		antialiasCheckBox = new JCheckBox(messageSource.getMessage("signalFFTSettings.antialias"));
		splineCheckBox = new JCheckBox(messageSource.getMessage("signalFFTSettings.spline"));
		titleVisibleCheckBox = new JCheckBox(messageSource.getMessage("signalFFTSettings.titleVisible"));
		frequencyAxisLabelsVisibleCheckBox = new JCheckBox(messageSource
		                .getMessage("signalFFTSettings.frequencyAxisLabelsVisible"));
		powerAxisLabelsVisibleCheckBox = new JCheckBox(messageSource
		                .getMessage("signalFFTSettings.powerAxisLabelsVisible"));

		optionsPanel.add(channelSwitchingCheckBox);
		optionsPanel.add(logarithmicCheckBox);
		optionsPanel.add(antialiasCheckBox);
		optionsPanel.add(splineCheckBox);
		optionsPanel.add(frequencyAxisLabelsVisibleCheckBox);
		optionsPanel.add(powerAxisLabelsVisibleCheckBox);
		optionsPanel.add(titleVisibleCheckBox);

		add(windowWidthPanel);
		add(fftViewPanel);
		add(plotWidthPanel);
		add(plotHeightPanel);
		add(fftWindowTypePanel);
		add(optionsPanel);

		Dimension size = getPreferredSize();
		if (size.width < 150) {
			size.width = 150;
		}
		setPreferredSize(size);

	}

	public void fillPanelFromModel(SignalFFTSettings settings) {

		int windowWidth = settings.getWindowWidth();
		int i;
		boolean found = false;
		for (i = 0; i < possibleWindowWidths.length; i++) {
			if (windowWidth == possibleWindowWidths[i]) {
				windowWidthRadioButtons[i].setSelected(true);
				found = true;
				break;
			}
		}
		if (!found) {
			customWindowWidthRadioButton.setSelected(true);
			customWindowWidthTextField.setText(Integer.toString(windowWidth));
		}

		if (settings.getVisibleRangeStart() > Integer.MIN_VALUE) {
			visibleRangeStartTextField.setText("" + settings.getVisibleRangeStart());
		} else {
			visibleRangeStartTextField.setText("");
		}

		if (settings.getVisibleRangeEnd() < Integer.MAX_VALUE) {
			visibleRangeEndTextField.setText("" + settings.getVisibleRangeEnd());
		} else {
			visibleRangeEndTextField.setText("");
		}

		if (settings.getMaxLabelCount() < Integer.MAX_VALUE) {
			maxLabelCountTextField.setText("" + settings.getMaxLabelCount());
		} else {
			maxLabelCountTextField.setText("");
		}

		scaleToFFTViewCheckBox.setSelected(settings.isScaleToView());

		Dimension plotSize = settings.getPlotSize();
		for (i = 0; i < possiblePlotWidths.length; i++) {
			if (plotSize.width == possiblePlotWidths[i]) {
				plotWidthRadioButtons[i].setSelected(true);
				break;
			}
		}
		for (i = 0; i < possiblePlotHeights.length; i++) {
			if (plotSize.height == possiblePlotHeights[i]) {
				plotHeightRadioButtons[i].setSelected(true);
				break;
			}
		}

		fftWindowTypePanel.fillPanelFromModel(settings);

		channelSwitchingCheckBox.setSelected(!settings.isChannelSwitching()); // note
		// the
		// description
		// of
		// this
		// field
		// causes
		// a
		// yes/no
		// meaning
		// inversion
		logarithmicCheckBox.setSelected(settings.isLogarithmic());
		antialiasCheckBox.setSelected(settings.isAntialias());
		splineCheckBox.setSelected(settings.isSpline());
		titleVisibleCheckBox.setSelected(settings.isTitleVisible());
		frequencyAxisLabelsVisibleCheckBox.setSelected(settings.isFrequencyAxisLabelsVisible());
		powerAxisLabelsVisibleCheckBox.setSelected(settings.isPowerAxisLabelsVisible());

	}

	public void fillModelFromPanel(SignalFFTSettings settings) {

		int i;
		boolean found = false;
		for (i = 0; i < possibleWindowWidths.length; i++) {
			if (windowWidthRadioButtons[i].isSelected()) {
				settings.setWindowWidth(possibleWindowWidths[i]);
				found = true;
				break;
			}
		}
		if (!found) {
			if (customWindowWidthRadioButton.isSelected()) {
				settings.setWindowWidth(Integer.parseInt(customWindowWidthTextField.getText()));
			} else {
				throw new SanityCheckException("Nothing selected");
			}
		}

		String val = visibleRangeStartTextField.getText().trim();
		if (val.length() > 0) {
			try {
				settings.setVisibleRangeStart(Integer.parseInt(val));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				settings.setVisibleRangeStart(Integer.MIN_VALUE);

			}
		} else {
			settings.setVisibleRangeStart(Integer.MIN_VALUE);
		}
		val = visibleRangeEndTextField.getText().trim();
		if (val.length() > 0) {
			try {
				settings.setVisibleRangeEnd(Integer.parseInt(val));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				settings.setVisibleRangeEnd(Integer.MAX_VALUE);

			}
		} else {
			settings.setVisibleRangeEnd(Integer.MAX_VALUE);
		}
		val = maxLabelCountTextField.getText().trim();
		if (val.length() > 0) {
			try {
				settings.setMaxLabelCount(Integer.parseInt(val));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				settings.setMaxLabelCount(Integer.MAX_VALUE);
			}
		} else {
			settings.setMaxLabelCount(Integer.MAX_VALUE);
		}
		settings.setScaleToView(scaleToFFTViewCheckBox.isSelected());

		Dimension plotSize = new Dimension(600, 200);
		for (i = 0; i < possiblePlotWidths.length; i++) {
			if (plotWidthRadioButtons[i].isSelected()) {
				plotSize.width = possiblePlotWidths[i];
				break;
			}
		}
		for (i = 0; i < possiblePlotHeights.length; i++) {
			if (plotHeightRadioButtons[i].isSelected()) {
				plotSize.height = possiblePlotHeights[i];
				break;
			}
		}

		settings.setPlotSize(plotSize);

		fftWindowTypePanel.fillModelFromPanel(settings);

		settings.setChannelSwitching(!channelSwitchingCheckBox.isSelected()); // note
		// the
		// description
		// of
		// this
		// field
		// causes
		// a
		// yes/no
		// meaning
		// inversion
		settings.setLogarithmic(logarithmicCheckBox.isSelected());
		settings.setAntialias(antialiasCheckBox.isSelected());
		settings.setSpline(splineCheckBox.isSelected());
		settings.setTitleVisible(titleVisibleCheckBox.isSelected());
		settings.setFrequencyAxisLabelsVisible(frequencyAxisLabelsVisibleCheckBox.isSelected());
		settings.setPowerAxisLabelsVisible(powerAxisLabelsVisibleCheckBox.isSelected());

	}

	public void validatePanel(Errors errors) {

		if (customWindowWidthRadioButton.isSelected()) {
			try {
				int windowWidth = Integer.parseInt(customWindowWidthTextField.getText());
				if (windowWidth < 8) {
					errors.rejectValue("windowWidth", "signalFFTSettings.error.badWindowWidth");
				}
			} catch (NumberFormatException ex) {
				errors.rejectValue("windowWidth", "signalFFTSettings.error.badWindowWidth");
			}
		}

		fftWindowTypePanel.validatePanel(errors);

	}

}

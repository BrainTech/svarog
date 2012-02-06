/* SignalViewingConfigPanel.java created 2007-11-17
 *
 */
package org.signalml.app.view.components;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.components.validation.ValidationErrors;
import org.signalml.app.view.signal.SignalColor;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.tag.TagPaintMode;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.signal.SignalChecksum;
import org.signalml.plugin.export.signal.Tag;

import org.springframework.validation.Errors;

/**
 * Panel which allows to select how the signal should be displayed by default.
 * Contains 3 sub-panels (from top to bottom):
 * <ul>
 * <li>the {@link #getGeneralPanel() panel} with general options of
 * signal viewing,</li>
 * <li>the {@link #getPlotOptionsPanel() panel} with the options of
 * the {@link SignalPlot},</li>
 * <li>the {@link #getScalesPanel() panel} which allows to scale the
 * displayed signal.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalViewingConfigPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * the check-box which tells if the signal should be shifted one page
	 * forward when right-clicked
	 */
	private JCheckBox rightClickPagesForwardCheckBox;
	
	/**
	 * the check-box which tells if the default {@link Montage montage} should
	 * be loaded automatically when the signal is opened
	 */
	private JCheckBox autoLoadDefaultMontageCheckBox;
	
	/**
	 * the check-box which tells if the {@link SignalChecksum checksum} should
	 * be pre-calculated
	 */
	private JCheckBox precalculateSignalChecksumsCheckBox;

	/**
	 * the check-box which tells if the signal should be antialiased by
	 * default
	 */
	private JCheckBox antialiasedCheckBox;
	/**
	 * the check-box which tells if the values should be clamped by default
	 */
	private JCheckBox clampedCheckBox;
	/**
	 * the check-box which tells if the channels outside the screen should
	 * be drawn by default
	 */
	private JCheckBox offscreenChannelsDrawnCheckBox;
	/**
	 * the check-box which tells if the tool-tips should by default appear
	 * when mouse cursor is over {@link Tag tags}
	 */
	private JCheckBox tagToolTipsVisibleCheckBox;

	/**
	 * the check-box which tells if the lines marking the end of a page
	 * should be shown on the plot by default
	 */
	private JCheckBox pageLinesVisibleCheckBox;
	/**
	 * the check-box which tells if the lines marking the end of a block
	 * should be shown on the plot by default
	 */
	private JCheckBox blockLinesVisibleCheckBox;
	/**
	 * the check-box which tells if the lines indicating the center of channels
	 * should be shown on the plot by default
	 */
	private JCheckBox channelLinesVisibleCheckBox;

	/**
	 * the combo-box with the mode in which the tags should be painted
	 * (Overlay, XOR, Alpha 50%, Alpha 80%) by default
	 */
	private JComboBox tagPaintModeComboBox;
	/**
	 * the combo-box with possible default colors of the signal
	 */
	private JComboBox signalColorComboBox;
	/**
	 * the check-box which tells if the signal should be by default displayed
	 * in the {@link Graphics#setXORMode(java.awt.Color) XOR mode}
	 */
	private JCheckBox signalXORCheckBox;

	/**
	 * the spinner with the minimal height of the single channel (in pixels)
	 */
	private JSpinner minChannelHeightSpinner;
	/**
	 * the spinner with the maximal height of the single channel (in pixels)
	 */
	private JSpinner maxChannelHeightSpinner;
	/**
	 * the spinner with the minimal value of the scale (%)
	 */
	private JSpinner minValueScaleSpinner;
	/**
	 * the spinner with the maximal value of the scale (%)
	 */
	private JSpinner maxValueScaleSpinner;
	/**
	 * the spinner with the minimal number of pixels per sample
	 */
	private JSpinner minTimeScaleSpinner;
	/**
	 * the spinner with the maximal number of pixels per sample
	 */
	private JSpinner maxTimeScaleSpinner;

	/**
	 * the panel with general options of signal viewing - see 
	 * {@link #getGeneralPanel()}
	 */
	private JPanel generalPanel;
	
	/**
	 * the panel with the options of the {@link SignalPlot} - see
	 * {@link #getPlotOptionsPanel()}
	 */
	private JPanel plotOptionsPanel;
	
	/**
	 * the panel which allows to scale the displayed signal -
	 * see {@link #getScalesPanel()}
	 */
	private JPanel scalesPanel;

	/**
	 * Constructor. Initializes the panel.
	 */
	public SignalViewingConfigPanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with border layout and three sub-panels
	 * (from top to bottom):
	 * <ul>
	 * <li>the {@link #getGeneralPanel() panel} with general options of
	 * signal viewing,</li>
	 * <li>the {@link #getPlotOptionsPanel() panel} with the options of
	 * the {@link SignalPlot},</li>
	 * <li>the {@link #getScalesPanel() panel} which allows to scale the
	 * displayed signal.</li>
	 * </ul>
	 */
	private void initialize() {

		setBorder(new EmptyBorder(3,3,3,3));
		setLayout(new BorderLayout());

		add(getGeneralPanel(), BorderLayout.NORTH);
		add(getPlotOptionsPanel(), BorderLayout.CENTER);
		add(getScalesPanel(), BorderLayout.SOUTH);
	}

	/**
	 * Returns the combo-box with the mode in which the tags should be painted
	 * (Overlay, XOR, Alpha 50%, Alpha 80%) by default
	 * If the combo-box doesn't exist it is created and
	 * {@link TagPaintMode#values() filled.}
	 * @return the combo-box with the mode in which the tags should be painted
	 */
	public JComboBox getTagPaintModeComboBox() {
		if (tagPaintModeComboBox == null) {
			tagPaintModeComboBox = new ResolvableComboBox();
			tagPaintModeComboBox.setModel(new DefaultComboBoxModel(TagPaintMode.values()));
		}
		return tagPaintModeComboBox;
	}

	/**
	 * Returns the combo-box with possible default colors of the signal.
	 * If the combo-box doesn't exist it is created and
	 * {@link SignalColor#values() filled.}
	 * @return the combo-box with possible default colors of the signal
	 */
	public JComboBox getSignalColorComboBox() {
		if (signalColorComboBox == null) {
			signalColorComboBox = new ResolvableComboBox();
			signalColorComboBox.setModel(new DefaultComboBoxModel(SignalColor.values()));
		}
		return signalColorComboBox;
	}

	/**
	 * Returns the check-box which tells if the signal should be by default
	 * displayed in the {@link Graphics#setXORMode(java.awt.Color) XOR mode}.
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box which tells if the signal should be by default
	 * displayed in the XOR mode
	 */
	public JCheckBox getSignalXORCheckBox() {
		if (signalXORCheckBox == null) {
			signalXORCheckBox = new JCheckBox(_("Default signal XOR"));
			signalXORCheckBox.setHorizontalTextPosition(SwingConstants.LEFT);
		}
		return signalXORCheckBox;
	}

	/**
	 * Returns the check-box which tells if the signal should be shifted one page
	 * forward when right-clicked.
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box which tells if the signal should be shifted one page
	 * forward when right-clicked
	 */
	public JCheckBox getRightClickPagesForwardCheckBox() {
		if (rightClickPagesForwardCheckBox == null) {
			rightClickPagesForwardCheckBox = new JCheckBox(_("Right click signal to shift page (hold down SHIFT for popup menu)"));
		}
		return rightClickPagesForwardCheckBox;
	}

	/**
	 * Returns the check-box which tells if the default {@link Montage montage}
	 * should be loaded automatically when the signal is opened.
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box which tells if the default montage should
	 * be loaded automatically when the signal is opened
	 */
	public JCheckBox getAutoLoadDefaultMontageCheckBox() {
		if (autoLoadDefaultMontageCheckBox == null) {
			autoLoadDefaultMontageCheckBox = new JCheckBox(_("Automatically load default montage when opening signals (if defined)"));
		}
		return autoLoadDefaultMontageCheckBox;
	}

	/**
	 * Returns the check-box which tells if the {@link SignalChecksum checksum}
	 * should be pre-calculated.
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box which tells if the checksum should be
	 * pre-calculated
	 */
	public JCheckBox getPrecalculateSignalChecksumsCheckBox() {
		if (precalculateSignalChecksumsCheckBox == null) {
			precalculateSignalChecksumsCheckBox = new JCheckBox(_("Pre-calculate checksums for opened signals"));
		}
		return precalculateSignalChecksumsCheckBox;
	}

	/**
	 * Returns the check-box which tells if the {@link SignalChecksum checksum}
	 * should be pre-calculated.
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box which tells if the checksum should
	 * be pre-calculated
	 */
	public JCheckBox getAntialiasedCheckBox() {
		if (antialiasedCheckBox == null) {
			antialiasedCheckBox = new JCheckBox(_("Default antialiasing"));
		}
		return antialiasedCheckBox;
	}

	/**
	 * Returns the check-box which tells if the values should be clamped
	 * by default.
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box which tells if the values should be clamped
	 * by default
	 */
	public JCheckBox getClampedCheckBox() {
		if (clampedCheckBox == null) {
			clampedCheckBox = new JCheckBox(_("Default clamp values"));
		}
		return clampedCheckBox;
	}

	/**
	 * Returns the check-box which tells if the channels outside the screen
	 * should be drawn by default.
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box which tells if the channels outside the screen
	 * should be drawn by default
	 */
	public JCheckBox getOffscreenChannelsDrawnCheckBox() {
		if (offscreenChannelsDrawnCheckBox == null) {
			offscreenChannelsDrawnCheckBox = new JCheckBox(_("Default draw offscreen channels"));
		}
		return offscreenChannelsDrawnCheckBox;
	}

	/**
	 * Returns the check-box which tells if the tool-tips should by default
	 * appear when mouse cursor is over a {@link Tag tag}.
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box which tells if the tool-tips should by default
	 * appear when mouse cursor is over a tag
	 */
	public JCheckBox getTagToolTipsVisibleCheckBox() {
		if (tagToolTipsVisibleCheckBox == null) {
			tagToolTipsVisibleCheckBox = new JCheckBox(_("Default tag tool tips"));
		}
		return tagToolTipsVisibleCheckBox;
	}

	/**
	 * Returns the check-box which tells if the lines marking the end of a page
	 * should be shown on the plot by default.
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box which tells if the lines marking the end of a page
	 * should be shown on the plot by default
	 */
	public JCheckBox getPageLinesVisibleCheckBox() {
		if (pageLinesVisibleCheckBox == null) {
			pageLinesVisibleCheckBox = new JCheckBox(_("Default page lines"));
		}
		return pageLinesVisibleCheckBox;
	}

	/**
	 * Returns the check-box which tells if the lines marking the end of a
	 * block should be shown on the plot by default.
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box which tells if the lines marking the end of a
	 * block should be shown on the plot by default
	 */
	public JCheckBox getBlockLinesVisibleCheckBox() {
		if (blockLinesVisibleCheckBox == null) {
			blockLinesVisibleCheckBox = new JCheckBox(_("Default block lines"));
		}
		return blockLinesVisibleCheckBox;
	}

	/**
	 * Returns the check-box which tells if the lines indicating the center
	 * of channels should be shown on the plot by default.
	 * If the check-box doesn't exist, it is created.
	 * @return the check-box which tells if the lines indicating the center
	 * of channels should be shown on the plot by default
	 */
	public JCheckBox getChannelLinesVisibleCheckBox() {
		if (channelLinesVisibleCheckBox == null) {
			channelLinesVisibleCheckBox = new JCheckBox(_("Default channel lines"));
		}
		return channelLinesVisibleCheckBox;
	}

	/**
	 * Returns the spinner with the minimal height of the single channel
	 * (in pixels).
	 * If the spinner doesn't exist it is created.
	 * @return the spinner with the minimal height of the single channel
	 * (in pixels)
	 */
	public JSpinner getMinChannelHeightSpinner() {
		if (minChannelHeightSpinner == null) {
			minChannelHeightSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 10));
			minChannelHeightSpinner.setPreferredSize(new Dimension(80,25));
		}
		return minChannelHeightSpinner;
	}

	/**
	 * Returns the spinner with the maximal height of the single channel
	 * (in pixels).
	 * If the spinner doesn't exist it is created.
	 * @return the spinner with the maximal height of the single channel
	 * (in pixels)
	 */
	public JSpinner getMaxChannelHeightSpinner() {
		if (maxChannelHeightSpinner == null) {
			maxChannelHeightSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 1000, 10));
			maxChannelHeightSpinner.setPreferredSize(new Dimension(80,25));
		}
		return maxChannelHeightSpinner;
	}

	/**
	 * Returns the spinner with the minimal value of the scale (%).
	 * If the spinner doesn't exist it is created.
	 * @return the spinner with the minimal value of the scale (%)
	 */
	public JSpinner getMinValueScaleSpinner() {
		if (minValueScaleSpinner == null) {
			minValueScaleSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 10));
			minValueScaleSpinner.setPreferredSize(new Dimension(80,25));
		}
		return minValueScaleSpinner;
	}

	/**
	 * Returns the spinner with the maximal value of the scale (%).
	 * If the spinner doesn't exist it is created.
	 * @return the spinner with the maximal value of the scale (%)
	 */
	public JSpinner getMaxValueScaleSpinner() {
		if (maxValueScaleSpinner == null) {
			maxValueScaleSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 10));
			maxValueScaleSpinner.setPreferredSize(new Dimension(80,25));
		}
		return maxValueScaleSpinner;
	}

	/**
	 * Returns the spinner with the minimal number of pixels per sample.
	 * If the spinner doesn't exist it is created.
	 * @return the spinner with the minimal number of pixels per sample
	 */
	public JSpinner getMinTimeScaleSpinner() {
		if (minTimeScaleSpinner == null) {
			minTimeScaleSpinner = new JSpinner(new SpinnerNumberModel(0.01, 0.01, 1, 0.01));
			minTimeScaleSpinner.setPreferredSize(new Dimension(80,25));
		}
		return minTimeScaleSpinner;
	}

	/**
	 * Returns the spinner with the maximal number of pixels per sample.
	 * If the spinner doesn't exist it is created.
	 * @return the spinner with the maximal number of pixels per sample
	 */
	public JSpinner getMaxTimeScaleSpinner() {
		if (maxTimeScaleSpinner == null) {
			maxTimeScaleSpinner = new JSpinner(new SpinnerNumberModel(0.01, 0.01, 1, 0.01));
			maxTimeScaleSpinner.setPreferredSize(new Dimension(80,25));
		}
		return maxTimeScaleSpinner;
	}

	/**
	 * Returns the panel with general options of signal viewing.
	 * If the panel doesn't exist it is created with 3 check-boxes:
	 * <ul>
	 * <li>the {@link #getAutoLoadDefaultMontageCheckBox() check-box} if the
	 * {@link Montage montage} should be loaded by default,</li>
	 * <li>the {@link #getRightClickPagesForwardCheckBox() check-box} if the
	 * right click should shift the page,</li>
	 * <li>the {@link #getPrecalculateSignalChecksumsCheckBox() check-box} if
	 * the {@link SignalChecksum checksums} should be pre-calculated.</li></ul>
	 * @return the panel with general options of signal viewing
	 */
	public JPanel getGeneralPanel() {
		if (generalPanel == null) {
			generalPanel = new JPanel();
			generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.Y_AXIS));
			generalPanel.setBorder(new CompoundBorder(
			                               new TitledBorder(_("General")),
			                               new EmptyBorder(3,3,3,3)
			                       ));

			generalPanel.add(getAutoLoadDefaultMontageCheckBox());
			generalPanel.add(getRightClickPagesForwardCheckBox());
			generalPanel.add(getPrecalculateSignalChecksumsCheckBox());
		}
		return generalPanel;
	}

	/**
	 * Returns the panel with the options of the {@link SignalPlot}.
	 * If the panel doens't exist it is created.
	 * Contains two sub-panels:
	 * <ul>
	 * <li>the panel with check-boxes on the left:
	 * <ul>
	 * <li>the {@link #getPageLinesVisibleCheckBox() check-box} if the page
	 * lines should be visible,</li>
	 * <li>the {@link #getBlockLinesVisibleCheckBox() check-box} if the block
	 * lines should be visible,</li>
	 * <li>the {@link #getChannelLinesVisibleCheckBox() check-box} if the
	 * channel lines should be visible,</li>
	 * <li>the {@link #getTagToolTipsVisibleCheckBox() check-box} if the
	 * tool-tips for tags should be shown,</li>
	 * <li>the {@link #getAntialiasedCheckBox() check-box} if the signal
	 * should be antialiased,</li>
	 * <li>the {@link #getClampedCheckBox() check-box} if the values should
	 * be clamped,</li>
	 * <li>the {@link #getOffscreenChannelsDrawnCheckBox() check-box} if
	 * the channels outside the screen should be drawn,</li>
	 * </ul></li>
	 * <li>the panel on the right with group layout and two sub-groups:
	 * <ul>
	 * <li>horizontal group which has two sub-groups: one for labels and one
	 * for combo-boxes.
	 * This group positions the elements in two columns.</li>
	 * <li>vertical group which has 3 sub-groups - one for every row:
	 * <ul>
	 * <li>the label and the {@link #getTagPaintModeComboBox() combo-box}
	 * with the mode in which the tags should be painted</li>
	 * <li>the label and the {@link #getSignalColorComboBox() combo-box} with
	 * possible colors of the signal,</li>
	 * <li>the label and the {@link #getSignalXORCheckBox() check-box} if the
	 * signal should be displayed in XOR mode,</li></ul>
	 * This group positions elements in rows.</li>
	 * </ul></li></ul>
	 * @return the panel with the options of the SignalPlot
	 */
	public JPanel getPlotOptionsPanel() {
		if (plotOptionsPanel == null) {
			plotOptionsPanel = new JPanel(new BorderLayout());
			plotOptionsPanel.setBorder(new CompoundBorder(
			                                   new TitledBorder(_("Plot options")),
			                                   new EmptyBorder(3,3,3,3)
			                           ));

			JPanel plotOptionsLeftPanel = new JPanel(new GridLayout(7, 1, 0, 0));

			plotOptionsLeftPanel.add(getPageLinesVisibleCheckBox());
			plotOptionsLeftPanel.add(getBlockLinesVisibleCheckBox());
			plotOptionsLeftPanel.add(getChannelLinesVisibleCheckBox());
			plotOptionsLeftPanel.add(getTagToolTipsVisibleCheckBox());
			plotOptionsLeftPanel.add(getAntialiasedCheckBox());
			plotOptionsLeftPanel.add(getClampedCheckBox());
			plotOptionsLeftPanel.add(getOffscreenChannelsDrawnCheckBox());

			JPanel plotOptionsRightPanel = new JPanel();
			plotOptionsRightPanel.setBorder(new EmptyBorder(3,3,3,3));

			GroupLayout layout = new GroupLayout(plotOptionsRightPanel);
			plotOptionsRightPanel.setLayout(layout);

			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			JLabel tagPaintModeLabel = new JLabel(_("Default tag paint mode"));
			JLabel signalColorLabel = new JLabel(_("Default signal color"));
			JLabel signalXORLabel = new JLabel("");

			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

			hGroup.addGroup(
			        layout.createParallelGroup(Alignment.LEADING)
			        .addComponent(tagPaintModeLabel)
			        .addComponent(signalColorLabel)
			        .addComponent(signalXORLabel)
			);

			hGroup.addGroup(
			        layout.createParallelGroup(Alignment.TRAILING)
			        .addComponent(getTagPaintModeComboBox())
			        .addComponent(getSignalColorComboBox())
			        .addComponent(getSignalXORCheckBox())
			);

			layout.setHorizontalGroup(hGroup);

			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(tagPaintModeLabel)
					.addComponent(getTagPaintModeComboBox())
				);
			
			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(signalColorLabel)
					.addComponent(getSignalColorComboBox())
				);

			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(signalXORLabel)
					.addComponent(getSignalXORCheckBox())
				);

			layout.setVerticalGroup(vGroup);

			plotOptionsPanel.add(plotOptionsLeftPanel, BorderLayout.WEST);
			plotOptionsPanel.add(plotOptionsRightPanel, BorderLayout.CENTER);
		}
		return plotOptionsPanel;
	}

	/**
	 * Returns the panel which allows to scale the displayed signal.
	 * If the panel doesn't exist it is created with group layout and two
	 * groups:
	 * <ul>
	 * <li>horizontal group which has 5 sub-groups:
	 * <ul><li>1 - for descriptive labels,</li>
	 * <li>2 - for {@code min} labels,</li
	 * <li>3 - for minimum spinners,</li>
	 * <li>4 - for {@code max} labels,</li>
	 * <li>5 - for maximum spinners,</li></ul>
	 * This group positions the elements in two columns.</li>
	 * <li>vertical group which has 3 sub-groups - one for every row:
	 * <ul>
	 * <li>the labels and spinners ({@link #getMinChannelHeightSpinner() min}
	 * and {@link #getMaxChannelHeightSpinner() max}) with the channel height,
	 * </li>
	 * <li>the labels and spinners ({@link #getMinValueScaleSpinner() min}
	 * and {@link #getMaxValueScaleSpinner() max}) with the value scale,</li>
	 * <li>the labels and spinners ({@link #getMinTimeScaleSpinner() min}
	 * and {@link #getMaxTimeScaleSpinner() max}) with the number of pixels per
	 * sample.</li></ul>
	 * This group positions elements in rows.</li>
	 * </ul>
	 * @return the panel which allows to scale the displayed signal
	 */
	public JPanel getScalesPanel() {
		if (scalesPanel == null) {

			scalesPanel = new JPanel();
			scalesPanel.setBorder(new CompoundBorder(
			                              new TitledBorder(_("Scale limits (changes do not affect signals already open)")),
			                              new EmptyBorder(3,6,3,6)
			                      ));

			GroupLayout layout = new GroupLayout(scalesPanel);
			scalesPanel.setLayout(layout);

			layout.setAutoCreateContainerGaps(false);
			layout.setAutoCreateGaps(true);

			JLabel channelHeightLabel = new JLabel(_("Channel height [px]"));
			JLabel minChannelHeightLabel = new JLabel(_("min"));
			JLabel maxChannelHeightLabel = new JLabel(_("max"));
			JLabel valueScaleLabel = new JLabel(_("Value scale [%]"));
			JLabel minValueScaleLabel = new JLabel(_("min"));
			JLabel maxValueScaleLabel = new JLabel(_("max"));
			JLabel timeScaleLabel = new JLabel(_("Time scale [px/sample]"));
			JLabel minTimeScaleLabel = new JLabel(_("min"));
			JLabel maxTimeScaleLabel = new JLabel(_("max"));

			GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

			hGroup.addGroup(
			        layout.createParallelGroup(Alignment.LEADING)
			        .addComponent(channelHeightLabel)
			        .addComponent(valueScaleLabel)
			        .addComponent(timeScaleLabel)
			);

			hGroup.addGroup(
			        layout.createParallelGroup(Alignment.LEADING)
			        .addComponent(minChannelHeightLabel)
			        .addComponent(minValueScaleLabel)
			        .addComponent(minTimeScaleLabel)
			);

			hGroup.addGroup(
			        layout.createParallelGroup(Alignment.TRAILING)
			        .addComponent(getMinChannelHeightSpinner())
			        .addComponent(getMinValueScaleSpinner())
			        .addComponent(getMinTimeScaleSpinner())
			);

			hGroup.addGroup(
			        layout.createParallelGroup(Alignment.LEADING)
			        .addComponent(maxChannelHeightLabel)
			        .addComponent(maxValueScaleLabel)
			        .addComponent(maxTimeScaleLabel)
			);

			hGroup.addGroup(
			        layout.createParallelGroup(Alignment.TRAILING)
			        .addComponent(getMaxChannelHeightSpinner())
			        .addComponent(getMaxValueScaleSpinner())
			        .addComponent(getMaxTimeScaleSpinner())
			);

			layout.setHorizontalGroup(hGroup);

			GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(channelHeightLabel)
					.addComponent(minChannelHeightLabel)
					.addComponent(getMinChannelHeightSpinner())
					.addComponent(maxChannelHeightLabel)
					.addComponent(getMaxChannelHeightSpinner())
				);
			
			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(valueScaleLabel)
					.addComponent(minValueScaleLabel)
					.addComponent(getMinValueScaleSpinner())
					.addComponent(maxValueScaleLabel)
					.addComponent(getMaxValueScaleSpinner())
				);

			vGroup.addGroup(
					layout.createParallelGroup(Alignment.BASELINE)
					.addComponent(timeScaleLabel)
					.addComponent(minTimeScaleLabel)
					.addComponent(getMinTimeScaleSpinner())
					.addComponent(maxTimeScaleLabel)
					.addComponent(getMaxTimeScaleSpinner())
				);

			layout.setVerticalGroup(vGroup);

		}
		return scalesPanel;
	}

	/**
	 * Fills all the fields of this panel from the given
	 * {@link ApplicationConfiguration configuration} of Svarog.
	 * @param applicationConfig the configuration of Svarog
	 */
	public void fillPanelFromModel(ApplicationConfiguration applicationConfig) {

		getRightClickPagesForwardCheckBox().setSelected(applicationConfig.isRightClickPagesForward());
		getAutoLoadDefaultMontageCheckBox().setSelected(applicationConfig.isAutoLoadDefaultMontage());
		getPrecalculateSignalChecksumsCheckBox().setSelected(applicationConfig.isPrecalculateSignalChecksums());

		getPageLinesVisibleCheckBox().setSelected(applicationConfig.isPageLinesVisible());
		getBlockLinesVisibleCheckBox().setSelected(applicationConfig.isBlockLinesVisible());
		getChannelLinesVisibleCheckBox().setSelected(applicationConfig.isChannelLinesVisible());
		getTagToolTipsVisibleCheckBox().setSelected(applicationConfig.isTagToolTipsVisible());
		getAntialiasedCheckBox().setSelected(applicationConfig.isAntialiased());
		getClampedCheckBox().setSelected(applicationConfig.isClamped());
		getOffscreenChannelsDrawnCheckBox().setSelected(applicationConfig.isOffscreenChannelsDrawn());

		getTagPaintModeComboBox().setSelectedItem(applicationConfig.getTagPaintMode());
		getSignalColorComboBox().setSelectedItem(applicationConfig.getSignalColor());
		getSignalXORCheckBox().setSelected(applicationConfig.isSignalXOR());

		getMinChannelHeightSpinner().setValue(applicationConfig.getMinChannelHeight());
		getMaxChannelHeightSpinner().setValue(applicationConfig.getMaxChannelHeight());

		getMinValueScaleSpinner().setValue(applicationConfig.getMinValueScale());
		getMaxValueScaleSpinner().setValue(applicationConfig.getMaxValueScale());

		getMinTimeScaleSpinner().setValue(applicationConfig.getMinTimeScale());
		getMaxTimeScaleSpinner().setValue(applicationConfig.getMaxTimeScale());

	}

	/**
	 * Writes the values of the fields from this panel to the
	 * {@link ApplicationConfiguration configuration} of Svarog
	 * @param applicationConfig the configuration of Svarog
	 */
	public void fillModelFromPanel(ApplicationConfiguration applicationConfig) {

		applicationConfig.setRightClickPagesForward(getRightClickPagesForwardCheckBox().isSelected());
		applicationConfig.setAutoLoadDefaultMontage(getAutoLoadDefaultMontageCheckBox().isSelected());
		applicationConfig.setPrecalculateSignalChecksums(getPrecalculateSignalChecksumsCheckBox().isSelected());

		applicationConfig.setPageLinesVisible(getPageLinesVisibleCheckBox().isSelected());
		applicationConfig.setBlockLinesVisible(getBlockLinesVisibleCheckBox().isSelected());
		applicationConfig.setChannelLinesVisible(getChannelLinesVisibleCheckBox().isSelected());
		applicationConfig.setTagToolTipsVisible(getTagToolTipsVisibleCheckBox().isSelected());
		applicationConfig.setAntialiased(getAntialiasedCheckBox().isSelected());
		applicationConfig.setClamped(getClampedCheckBox().isSelected());
		applicationConfig.setOffscreenChannelsDrawn(getOffscreenChannelsDrawnCheckBox().isSelected());

		applicationConfig.setTagPaintMode((TagPaintMode) getTagPaintModeComboBox().getSelectedItem());
		applicationConfig.setSignalColor((SignalColor) getSignalColorComboBox().getSelectedItem());
		applicationConfig.setSignalXOR(getSignalXORCheckBox().isSelected());

		int min = ((Number) getMinChannelHeightSpinner().getValue()).intValue();
		int max = ((Number) getMaxChannelHeightSpinner().getValue()).intValue();
		int temp;
		if (min > max) {
			temp = min;
			min = max;
			max = temp;
		}
		applicationConfig.setMinChannelHeight(min);
		applicationConfig.setMaxChannelHeight(max);

		min = ((Number) getMinValueScaleSpinner().getValue()).intValue();
		max = ((Number) getMaxValueScaleSpinner().getValue()).intValue();
		if (min > max) {
			temp = min;
			min = max;
			max = temp;
		}
		applicationConfig.setMinValueScale(min);
		applicationConfig.setMaxValueScale(max);

		double minD = ((Number) getMinTimeScaleSpinner().getValue()).doubleValue();
		double maxD = ((Number) getMaxTimeScaleSpinner().getValue()).doubleValue();
		if (minD > maxD) {
			double tempD = minD;
			minD = maxD;
			maxD = tempD;
		}
		applicationConfig.setMinTimeScale(minD);
		applicationConfig.setMaxTimeScale(maxD);

	}

	/**
	 * Validates this panel. This panel is always valid.
	 * @param errors the object in which the errors should be stored
	 */
	public void validate(ValidationErrors errors) {
		// do nothing
	}

}

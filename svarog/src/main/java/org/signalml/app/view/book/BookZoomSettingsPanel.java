/* BookZoomSettingsPanel.java created 2007-12-17
 *
 */
package org.signalml.app.view.book;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.ParseException;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.signalml.app.view.book.tools.ZoomBookTool;
import org.signalml.domain.book.StandardBookSegment;

import org.springframework.validation.Errors;

/**
 * Panel to set how the {@link BookView} should be zoomed.
 * Contains two sub-panels:
 * <ul>
 * <li>the panel with ranges of position and frequency that should be
 * displayed,</li>
 * <li>the panel with check-box if the zoom ratio should be preserved
 * on drag.</li>
 * </ul>
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookZoomSettingsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(BookZoomSettingsPanel.class);

	/**
	 * the model for {@link #minPositionSpinner}
	 */
	private SpinnerNumberModel minPositionModel;
	/**
	 * the model for {@link #maxPositionSpinner}
	 */
	private SpinnerNumberModel maxPositionModel;
	/**
	 * the model for {@link #minFrequencySpinner}
	 */
	private SpinnerNumberModel minFrequencyModel;
	/**
	 * the model for {@link #maxFrequencySpinner}
	 */
	private SpinnerNumberModel maxFrequencyModel;

	/**
	 * the spinner for the minimal displayed position (in points)
	 */
	private JSpinner minPositionSpinner;
	/**
	 * the spinner for the maximal displayed position (in points)
	 */
	private JSpinner maxPositionSpinner;
	/**
	 * the spinner for the minimal displayed frequency TODO jednostka
	 */
	private JSpinner minFrequencySpinner;
	/**
	 * the spinner for the maximal displayed frequency TODO jednostka
	 */
	private JSpinner maxFrequencySpinner;

	/**
	 * the check-box if the zoom ratio should be preserved
	 * TODO probably not used
	 */
	private JCheckBox preserveRatioCheckBox;

	/**
	 * the maximal value of the position that can be set in the spinners
	 */
	private double positionLimit;
	/**
	 * the maximal value of the frequency that can be set in the spinners
	 */
	private double frequencyLimit;

	/**
	 * Constructor. Sets the source of messages, if this panel should contain
	 * the closing cross and initializes this panel
	 * @param hasCloseCross @code true} if this panel should contain a cross
	 * which closes this panel, {@code false} otherwise
	 */
	public BookZoomSettingsPanel() {
		super();
		initialize();
	}

	/**
	 * Initializes this panel with two sub-panels:
	 * <ul>
	 * <li>the panel with ranges of position and frequency that should be
	 * displayed,</li>
	 * <li>the panel with check-box if the zoom ratio should be preserved
	 * on drag.</li>
	 * </ul>
	 */
	private void initialize() {

		setLayout(new BorderLayout());

		JPanel rangePanel = new JPanel();

		rangePanel.setBorder(new CompoundBorder(
								 new TitledBorder(_("Range")),
								 new EmptyBorder(3,3,3,3)
							 ));

		GroupLayout layout = new GroupLayout(rangePanel);
		rangePanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel positionLabel = new JLabel(_("Position"));
		JLabel frequencyLabel = new JLabel(_("Frequency"));

		JLabel minPositionLabel = new JLabel(_("min"));
		JLabel minFrequencyLabel = new JLabel(_("min"));

		JLabel maxPositionLabel = new JLabel(_("max"));
		JLabel maxFrequencyLabel = new JLabel(_("max"));

		Component positionGlue = Box.createHorizontalGlue();
		Component frequencyGlue = Box.createHorizontalGlue();

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(positionLabel)
			.addComponent(frequencyLabel)
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(minPositionLabel)
			.addComponent(minFrequencyLabel)
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(getMinPositionSpinner())
			.addComponent(getMinFrequencySpinner())
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(positionGlue)
			.addComponent(frequencyGlue)
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(maxPositionLabel)
			.addComponent(maxFrequencyLabel)
		);

		hGroup.addGroup(
			layout.createParallelGroup()
			.addComponent(getMaxPositionSpinner())
			.addComponent(getMaxFrequencySpinner())
		);

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE)
			.addComponent(positionLabel)
			.addComponent(minPositionLabel)
			.addComponent(getMinPositionSpinner())
			.addComponent(positionGlue)
			.addComponent(maxPositionLabel)
			.addComponent(getMaxPositionSpinner())
		);

		vGroup.addGroup(
			layout.createParallelGroup(Alignment.BASELINE)
			.addComponent(frequencyLabel)
			.addComponent(minFrequencyLabel)
			.addComponent(getMinFrequencySpinner())
			.addComponent(frequencyGlue)
			.addComponent(maxFrequencyLabel)
			.addComponent(getMaxFrequencySpinner())
		);

		layout.setVerticalGroup(vGroup);

		JPanel preservationPanel = new JPanel(new FlowLayout());

		preservationPanel.setBorder(new CompoundBorder(
										new TitledBorder(_("Ratio preservation")),
										new EmptyBorder(3,3,3,3)
									));

		preservationPanel.add(getPreserveRatioCheckBox());

		add(rangePanel, BorderLayout.CENTER);
		add(preservationPanel, BorderLayout.SOUTH);
		Dimension size = getPreferredSize();
		if (size.width < 150) {
			size.width = 150;
		}
		setPreferredSize(size);

	}

	/**
	 * Returns the model for the {@link #getMinPositionSpinner() minimum
	 * position spinner}.
	 * If the model doesn't exist it is created with parameters:
	 * <ul>
	 * <li>minimum {@code = 0},</li>
	 * <li>maximum {@code = 19.99},</li>
	 * <li>step {@code = 0.01},</li>
	 * <li>value {@code = 0}</li></ul>
	 * @return the model for the minimum position spinner
	 */
	public SpinnerNumberModel getMinPositionModel() {
		if (minPositionModel == null) {
			minPositionModel = new SpinnerNumberModel(0.0, 0.0, 20.0-0.01, 0.01);
		}
		return minPositionModel;
	}

	/**
	 * Returns the minimum position spinner.
	 * If the spinner doesn't exist it is created:
	 * <ul>
	 * <li>with the specified {@link #getMinPositionModel() model},</li>
	 * <li>to have specified size (80x25 pixel),</li>
	 * <li>with the listener which updates the value of the {@link
	 * #getMaxPositionSpinner() maximum position spinner} to be at least
	 * {@code 0.01} larger then the value of this spinner.</li></ul>
	 * @return the minimum position spinner
	 */
	public JSpinner getMinPositionSpinner() {
		if (minPositionSpinner == null) {
			minPositionSpinner = new JSpinner(getMinPositionModel());

			Dimension spinnerSize = new Dimension(80,25);
			minPositionSpinner.setPreferredSize(spinnerSize);
			minPositionSpinner.setMinimumSize(spinnerSize);
			minPositionSpinner.setMaximumSize(spinnerSize);

			minPositionSpinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {

					double value = ((Number) minPositionSpinner.getValue()).doubleValue();

					double otherValue = ((Number) getMaxPositionSpinner().getValue()).doubleValue();

					if ((value+0.01) > otherValue) {
						getMaxPositionSpinner().setValue(value + 0.01);
					}

				}

			});

			minPositionSpinner.setEditor(new JSpinner.NumberEditor(minPositionSpinner, "0.00"));
			minPositionSpinner.setFont(minPositionSpinner.getFont().deriveFont(Font.PLAIN));

		}
		return minPositionSpinner;
	}

	/**
	 * Returns the model for the {@link #getMaxPositionSpinner() maximum
	 * position spinner}.
	 * If the model doesn't exist it is created with parameters:
	 * <ul>
	 * <li>minimum {@code = 0.01},</li>
	 * <li>maximum {@code = 20},</li>
	 * <li>step {@code = 0.01},</li>
	 * <li>value {@code = 20}</li></ul>
	 * @return the model for the minimum position spinner
	 */
	public SpinnerNumberModel getMaxPositionModel() {
		if (maxPositionModel == null) {
			maxPositionModel = new SpinnerNumberModel(20.0, 0.01, 20.0, 0.01);
		}
		return maxPositionModel;
	}

	/**
	 * Returns the maximum position spinner.
	 * If the spinner doesn't exist it is created:
	 * <ul>
	 * <li>with the specified {@link #getMaxPositionModel() model},</li>
	 * <li>to have specified size (80x25 pixel),</li>
	 * <li>with the listener which updates the value of the {@link
	 * #getMinPositionSpinner() maximum position spinner} to be at least
	 * {@code 0.01} smaller then the value of this spinner.</li></ul>
	 * @return the maximum position spinner
	 */
	public JSpinner getMaxPositionSpinner() {
		if (maxPositionSpinner == null) {
			maxPositionSpinner = new JSpinner(getMaxPositionModel());

			Dimension spinnerSize = new Dimension(80,25);
			maxPositionSpinner.setPreferredSize(spinnerSize);
			maxPositionSpinner.setMinimumSize(spinnerSize);
			maxPositionSpinner.setMaximumSize(spinnerSize);

			maxPositionSpinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {

					double value = ((Number) maxPositionSpinner.getValue()).doubleValue();

					double otherValue = ((Number) getMinPositionSpinner().getValue()).doubleValue();

					if ((value-0.01) < otherValue) {
						getMinPositionSpinner().setValue(value - 0.01);
					}

				}

			});

			maxPositionSpinner.setEditor(new JSpinner.NumberEditor(maxPositionSpinner, "0.00"));
			maxPositionSpinner.setFont(maxPositionSpinner.getFont().deriveFont(Font.PLAIN));

		}
		return maxPositionSpinner;
	}

	/**
	 * Returns the model for the {@link #getMinFrequencySpinner() minimum
	 * frequency spinner}.
	 * If the model doesn't exist it is created with parameters:
	 * <ul>
	 * <li>minimum {@code = 0},</li>
	 * <li>maximum {@code 63.99},</li>
	 * <li>step {@code = 0.01},</li>
	 * <li>value {@code = 0}</li></ul>
	 * @return the model for the minimum position spinner
	 */
	public SpinnerNumberModel getMinFrequencyModel() {
		if (minFrequencyModel == null) {
			minFrequencyModel = new SpinnerNumberModel(0.0, 0.0, 64.0-0.01, 0.01);
		}
		return minFrequencyModel;
	}

	/**
	 * Returns the minimum frequency spinner.
	 * If the spinner doesn't exist it is created:
	 * <ul>
	 * <li>with the specified {@link #getMinFrequencyModel() model},</li>
	 * <li>to have specified size (80x25 pixel),</li>
	 * <li>with the listener which updates the value of the {@link
	 * #getMaxFrequencySpinner() maximum frequency spinner} to be at least
	 * {@code 0.01} larger then the value of this spinner.</li></ul>
	 * @return the minimum frequency spinner
	 */
	public JSpinner getMinFrequencySpinner() {
		if (minFrequencySpinner == null) {
			minFrequencySpinner = new JSpinner(getMinFrequencyModel());

			Dimension spinnerSize = new Dimension(80,25);
			minFrequencySpinner.setPreferredSize(spinnerSize);
			minFrequencySpinner.setMinimumSize(spinnerSize);
			minFrequencySpinner.setMaximumSize(spinnerSize);

			minFrequencySpinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {

					double value = ((Number) minFrequencySpinner.getValue()).doubleValue();

					double otherValue = ((Number) getMaxFrequencySpinner().getValue()).doubleValue();

					if ((value+0.01) > otherValue) {
						getMaxFrequencySpinner().setValue(value + 0.01);
					}

				}

			});

			minFrequencySpinner.setEditor(new JSpinner.NumberEditor(minFrequencySpinner, "0.00"));
			minFrequencySpinner.setFont(minFrequencySpinner.getFont().deriveFont(Font.PLAIN));

		}
		return minFrequencySpinner;
	}

	/**
	 * Returns the model for the {@link #getMaxPositionSpinner() maximum
	 * position spinner}.
	 * If the model doesn't exist it is created with parameters:
	 * <ul>
	 * <li>minimum {@code = 0.01},</li>
	 * <li>maximum {@code = 64},</li>
	 * <li>step {@code = 0.01},</li>
	 * <li>value {@code = 64}</li></ul>
	 * @return the model for the minimum position spinner
	 */
	public SpinnerNumberModel getMaxFrequencyModel() {
		if (maxFrequencyModel == null) {
			maxFrequencyModel = new SpinnerNumberModel(64.0, 0.01, 64.0, 0.01);
		}
		return maxFrequencyModel;
	}

	/**
	 * Returns the maximum frequency spinner.
	 * If the spinner doesn't exist it is created:
	 * <ul>
	 * <li>with the specified {@link #getMaxFrequencyModel() model},</li>
	 * <li>to have specified size (80x25 pixel),</li>
	 * <li>with the listener which updates the value of the {@link
	 * #getMinFrequencySpinner() minimum frequency spinner} to be at least
	 * {@code 0.01} smaller then the value of this spinner.</li></ul>
	 * @return the maximum frequency spinner
	 */
	public JSpinner getMaxFrequencySpinner() {
		if (maxFrequencySpinner == null) {
			maxFrequencySpinner = new JSpinner(getMaxFrequencyModel());

			Dimension spinnerSize = new Dimension(80,25);
			maxFrequencySpinner.setPreferredSize(spinnerSize);
			maxFrequencySpinner.setMinimumSize(spinnerSize);
			maxFrequencySpinner.setMaximumSize(spinnerSize);

			maxFrequencySpinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {

					double value = ((Number) maxFrequencySpinner.getValue()).doubleValue();

					double otherValue = ((Number) getMinFrequencySpinner().getValue()).doubleValue();

					if ((value-0.01) < otherValue) {
						getMinFrequencySpinner().setValue(value - 0.01);
					}

				}

			});

			maxFrequencySpinner.setEditor(new JSpinner.NumberEditor(maxFrequencySpinner, "0.00"));
			maxFrequencySpinner.setFont(maxFrequencySpinner.getFont().deriveFont(Font.PLAIN));

		}
		return maxFrequencySpinner;
	}

	/**
	 * Returns the check-box if the zoom ratio should be preserved.
	 * If the button doesn't exist it is created.
	 * @return the check-box if the zoom ratio should be preserved
	 */
	public JCheckBox getPreserveRatioCheckBox() {
		if (preserveRatioCheckBox == null) {
			preserveRatioCheckBox = new JCheckBox(_("Preserve scale ratio on drag"));
		}
		return preserveRatioCheckBox;
	}

	/**
	 * Using the given {@link BookView model} fills the fields of this panel:
	 * <ul>
	 * <li>the maximum of {@link #getMinPositionModel() minimum} and {@link
	 * #getMaxPositionModel() maximum} position model,</li>
	 * <li>the maximum of {@link #getMinFrequencyModel() minimum} and {@link
	 * #getMaxFrequencyModel() maximum} frequency model,</li>
	 * <li>the state of {@link #getPreserveRatioCheckBox() preserve zoom ratio
	 * check-box}.</li>
	 * </ul>
	 * @param view the model
	 */
	public void fillPanelFromModel(BookView view) {

		BookPlot plot = view.getPlot();
		StandardBookSegment segment = plot.getSegment();

		if (segment == null) {
			return;
		}

		positionLimit = segment.getSegmentLength();
		frequencyLimit = view.getDocument().getBook().getSamplingFrequency()/2;

		getMinPositionModel().setMaximum(positionLimit - 0.01);
		getMaxPositionModel().setMaximum(positionLimit);

		getMinFrequencyModel().setMaximum(frequencyLimit - 0.01);
		getMaxFrequencyModel().setMaximum(frequencyLimit);

		getMinPositionSpinner().setValue(plot.getMinPosition());
		getMaxPositionSpinner().setValue(plot.getMaxPosition());

		getMinFrequencySpinner().setValue(plot.getMinFrequency());
		getMaxFrequencySpinner().setValue(plot.getMaxFrequency());

		getPreserveRatioCheckBox().setSelected(view.getZoomBookTool().isPreserveRatio());

	}

	/**
	 * Stores the values of the spinners in the {@link BookView model}.
	 * In order to do it
	 * <ul>
	 * <li>commits the edited values to all spinners,</li>
	 * <li>sets the {@link BookPlot#setZoom(double, double, double, double)
	 * zoom} in the {@link BookPlot book plot},</li>
	 * <li>sets the {@link ZoomBookTool#setPreserveRatio(boolean) preserve
	 * ratio} in the {@link ZoomBookTool}.</li></ul>
	 * @param view the model
	 */
	public void fillModelFromPanel(BookView view) {

		BookPlot plot = view.getPlot();
		StandardBookSegment segment = plot.getSegment();

		if (segment == null) {
			return;
		}

		// XXX update broken spinners...
		try {
			getMinPositionSpinner().commitEdit();
		} catch (ParseException ex) {
			JComponent editor = getMinPositionSpinner().getEditor();
			if (editor instanceof DefaultEditor) {
				((DefaultEditor) editor).getTextField().setValue(getMinPositionSpinner().getValue());
			}
		}

		try {
			getMaxPositionSpinner().commitEdit();
		} catch (ParseException ex) {
			JComponent editor = getMaxPositionSpinner().getEditor();
			if (editor instanceof DefaultEditor) {
				((DefaultEditor) editor).getTextField().setValue(getMaxPositionSpinner().getValue());
			}
		}

		try {
			getMinFrequencySpinner().commitEdit();
		} catch (ParseException ex) {
			JComponent editor = getMinFrequencySpinner().getEditor();
			if (editor instanceof DefaultEditor) {
				((DefaultEditor) editor).getTextField().setValue(getMinFrequencySpinner().getValue());
			}
		}

		try {
			getMaxFrequencySpinner().commitEdit();
		} catch (ParseException ex) {
			JComponent editor = getMaxFrequencySpinner().getEditor();
			if (editor instanceof DefaultEditor) {
				((DefaultEditor) editor).getTextField().setValue(getMaxFrequencySpinner().getValue());
			}
		}

		double minPosition = ((Number) getMinPositionSpinner().getValue()).doubleValue();
		double maxPosition = ((Number) getMaxPositionSpinner().getValue()).doubleValue();
		double minFrequency = ((Number) getMinFrequencySpinner().getValue()).doubleValue();
		double maxFrequency = ((Number) getMaxFrequencySpinner().getValue()).doubleValue();

		plot.setZoom(minPosition, maxPosition, minFrequency, maxFrequency);

		view.getZoomBookTool().setPreserveRatio(getPreserveRatioCheckBox().isSelected());

	}

	/**
	 * Validates this panel. This panel is always valid.
	 * @param errors the variable in which errors are stored
	 */
	public void validate(Errors errors) {
		// do nothing
	}

}

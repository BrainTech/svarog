/* BookZoomSettingsPanel.java created 2007-12-17
 *
 */
package org.signalml.app.view.element;

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
import org.signalml.app.view.book.BookPlot;
import org.signalml.app.view.book.BookView;
import org.signalml.domain.book.StandardBookSegment;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.Errors;

/** BookZoomSettingsPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookZoomSettingsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(BookZoomSettingsPanel.class);

	private MessageSourceAccessor messageSource;

	private SpinnerNumberModel minPositionModel;
	private SpinnerNumberModel maxPositionModel;
	private SpinnerNumberModel minFrequencyModel;
	private SpinnerNumberModel maxFrequencyModel;

	private JSpinner minPositionSpinner;
	private JSpinner maxPositionSpinner;
	private JSpinner minFrequencySpinner;
	private JSpinner maxFrequencySpinner;

	private JCheckBox preserveRatioCheckBox;

	private double positionLimit;
	private double frequencyLimit;
	private boolean hasCloseCross;

	public BookZoomSettingsPanel(MessageSourceAccessor messageSource, boolean hasCloseCross) {
		super();
		this.messageSource = messageSource;
		this.hasCloseCross = hasCloseCross;
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());

		JPanel rangePanel = new JPanel();

		rangePanel.setBorder(new CompoundBorder(
		                             new TitledCrossBorder(messageSource.getMessage("bookZoomSettings.rangeTitle"), hasCloseCross),
		                             new EmptyBorder(3,3,3,3)
		                     ));

		GroupLayout layout = new GroupLayout(rangePanel);
		rangePanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);

		JLabel positionLabel = new JLabel(messageSource.getMessage("bookZoomSettings.position"));
		JLabel frequencyLabel = new JLabel(messageSource.getMessage("bookZoomSettings.frequency"));

		JLabel minPositionLabel = new JLabel(messageSource.getMessage("bookZoomSettings.min"));
		JLabel minFrequencyLabel = new JLabel(messageSource.getMessage("bookZoomSettings.min"));

		JLabel maxPositionLabel = new JLabel(messageSource.getMessage("bookZoomSettings.max"));
		JLabel maxFrequencyLabel = new JLabel(messageSource.getMessage("bookZoomSettings.max"));

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

		preservationPanel.setBorder( new CompoundBorder(
				new TitledBorder( messageSource.getMessage("bookZoomSettings.preserveTitle") ),
				new EmptyBorder( 3,3,3,3 )					
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

	public SpinnerNumberModel getMinPositionModel() {
		if (minPositionModel == null) {
			minPositionModel = new SpinnerNumberModel(0.0, 0.0, 20.0-0.01, 0.01);
		}
		return minPositionModel;
	}

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

	public SpinnerNumberModel getMaxPositionModel() {
		if (maxPositionModel == null) {
			maxPositionModel = new SpinnerNumberModel(20.0, 0.01, 20.0, 0.01);
		}
		return maxPositionModel;
	}

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

	public SpinnerNumberModel getMinFrequencyModel() {
		if (minFrequencyModel == null) {
			minFrequencyModel = new SpinnerNumberModel(0.0, 0.0, 64.0-0.01, 0.01);
		}
		return minFrequencyModel;
	}

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

	public SpinnerNumberModel getMaxFrequencyModel() {
		if (maxFrequencyModel == null) {
			maxFrequencyModel = new SpinnerNumberModel(64.0, 0.01, 64.0, 0.01);
		}
		return maxFrequencyModel;
	}

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

	public JCheckBox getPreserveRatioCheckBox() {
		if (preserveRatioCheckBox == null) {
			preserveRatioCheckBox = new JCheckBox(messageSource.getMessage("bookZoomSettings.preserveRatio"));
		}
		return preserveRatioCheckBox;
	}

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

	public void validate(Errors errors) {
		// do nothing
	}

}

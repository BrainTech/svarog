/* FrequencyResponseChartPanelsWithMaximumFrequencySpinner.java created 2011-02-06
 *
 */
package org.signalml.app.view.montage.filters.charts.elements;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.chart.ChartPanel;

/**
 *
 * @author Piotr Szachewicz
 */
public class FilterResponseChartPanelsWithGraphScaleSpinner extends JPanel {

	public static double MINIMUM_SPINNER_VALUE = 0.25;
	public static double SPINNER_STEP_SIZE = 0.25;
	double maximumSpinnerValue = 64.0;
	JSpinner graphScaleSpinner;
	JPanel graphSpinnerPanel;
	List<ResponseChartPanel> chartPanels;
	protected String spinnerLabelText;

	public FilterResponseChartPanelsWithGraphScaleSpinner(List<ResponseChartPanel> chartPanels, String spinnerLabelText) {
		this.chartPanels = chartPanels;
		this.spinnerLabelText = spinnerLabelText;
		createInterface(chartPanels);
		updateMaximumDomainAxisValueForGraphs();
	}

	public void setMaximumSpinnerValue(double maximumSpinnerValue) {
		this.maximumSpinnerValue = maximumSpinnerValue;

		getGraphScaleSpinner().setModel(createSpinnerNumberModel());
		graphScaleSpinner.setEditor(new JSpinner.NumberEditor(graphScaleSpinner, "0.00"));
		updateMaximumDomainAxisValueForGraphs();
	}

	protected void createInterface(List<ResponseChartPanel> chartPanels) {
		this.setLayout(new BorderLayout(5, 5));

		JPanel chartGroupPanel = new JPanel(new GridLayout(chartPanels.size(), 1, 5, 5));
		for (ChartPanel chartPanel : chartPanels) {
			chartGroupPanel.add(chartPanel);
		}

		this.add(chartGroupPanel, BorderLayout.CENTER);
		this.add(getGraphScaleSpinnerPanel(), BorderLayout.SOUTH);
	}

	public JPanel getGraphScaleSpinnerPanel() {
		if (graphSpinnerPanel == null) {
			graphSpinnerPanel = new JPanel();
			graphSpinnerPanel.setLayout(new BoxLayout(graphSpinnerPanel, BoxLayout.X_AXIS));
			//graphSpinnerPanel.add(new JLabel(messageSource.getMessage("editSampleFilter.graphSpinnerLabel")));
			graphSpinnerPanel.add(new JLabel(spinnerLabelText));
			graphSpinnerPanel.add(Box.createHorizontalStrut(5));
			graphSpinnerPanel.add(Box.createHorizontalGlue());
			graphSpinnerPanel.add(getGraphScaleSpinner());
		}
		return graphSpinnerPanel;
	}

	public JSpinner getGraphScaleSpinner() {

		if (graphScaleSpinner == null) {
			graphScaleSpinner = new JSpinner(createSpinnerNumberModel());
			graphScaleSpinner.setPreferredSize(new Dimension(80, 25));

			graphScaleSpinner.setEditor(new JSpinner.NumberEditor(graphScaleSpinner, "0.00"));
			graphScaleSpinner.setFont(graphScaleSpinner.getFont().deriveFont(Font.PLAIN));

			graphScaleSpinner.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					updateMaximumDomainAxisValueForGraphs();
				}
			});

		}
		return graphScaleSpinner;

	}

	public SpinnerNumberModel createSpinnerNumberModel() {
		double currentSpinnerValue = maximumSpinnerValue;
		return new SpinnerNumberModel(currentSpinnerValue, MINIMUM_SPINNER_VALUE, maximumSpinnerValue, SPINNER_STEP_SIZE);
	}

	protected void updateMaximumDomainAxisValueForGraphs() {
		for (ResponseChartPanel chartPanel : chartPanels) {
			chartPanel.setMaximumDomainAxisValue(getSpinnerValue());
		}
	}

	public double getSpinnerValue() {
		return ((Number) getGraphScaleSpinner().getValue()).doubleValue();
	}
}

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
import org.signalml.app.view.montage.filters.DoubleSpinner;

/**
 * This class represents a panel which contains one or few filter response charts
 * and a spinner which controls the maximum value shown on the x-axis.
 *
 * @author Piotr Szachewicz
 */
public class FilterResponseChartPanelsWithGraphScaleSpinner extends JPanel {

	/**
	 * The minimum value to be shown on the graphScaleSpinner.
	 */
	public static double MINIMUM_SPINNER_VALUE = 0.25;

	/**
	 * The graphScaleSpinner step size.
	 */
	public static double SPINNER_STEP_SIZE = 0.25;

	/**
	 * The maximum value which can be set using the graphScaleSpinner.
	 */
	double maximumSpinnerValue = 64.0;

	/**
	 * The spinner used to set the maximum value shown on the x-axes.
	 */
	DoubleSpinner graphScaleSpinner;

	/**
	 * The panel which contains the graphScaleSpinner and its label.
	 */
	JPanel graphSpinnerPanel;

	/**
	 * The list containing all of the chart panels which should be shown
	 * on this chart panel.
	 */
	List<ResponseChartPanel> chartPanels;

	/**
	 * The label text which describes the graphScaleSpinner.
	 */
	protected String spinnerLabelText;

	/**
	 * Constructor.
	 * @param chartPanels the list of chart panels to be shown
	 * @param spinnerLabelText the label text which describes the graph scale spinner.
	 */
	public FilterResponseChartPanelsWithGraphScaleSpinner(List<ResponseChartPanel> chartPanels, String spinnerLabelText) {
		this.chartPanels = chartPanels;
		this.spinnerLabelText = spinnerLabelText;
		createInterface(chartPanels);
		updateMaximumDomainAxisValueForGraphs();
	}

	/**
	 * Sets the maximum value which can be set by the graph scale spinner.
	 * @param maximumSpinnerValue maximum value which can be set
	 */
	public void setMaximumSpinnerValue(double maximumSpinnerValue) {
		this.maximumSpinnerValue = maximumSpinnerValue;

		getGraphScaleSpinner().setModel(createSpinnerNumberModel());
		graphScaleSpinner.setEditor(new JSpinner.NumberEditor(graphScaleSpinner, "0.00"));
		updateMaximumDomainAxisValueForGraphs();
	}

	/**
	 * Sets the current value which is set on the graph scale spinner.
	 * @param spinnerValue current spinner value
	 */
	public void setCurrentSpinnerValue(double spinnerValue) {
		getGraphScaleSpinner().setValue(spinnerValue);
	}

	/**
	 * Returns the value set on the graph scale spinner.
	 * @return the value set on the graph scale spinner
	 */
	public double getGraphScaleSpinnerValue() {
		return getGraphScaleSpinner().getValue();
	}

	/**
	 * Creates the interface for this panel.
	 * @param chartPanels the list of chart panels to be shown on this panel
	 */
	private void createInterface(List<ResponseChartPanel> chartPanels) {
		this.setLayout(new BorderLayout(5, 5));

		JPanel chartGroupPanel = new JPanel(new GridLayout(chartPanels.size(), 1, 5, 5));
		for (ChartPanel chartPanel : chartPanels) {
			chartGroupPanel.add(chartPanel);
		}

		this.add(chartGroupPanel, BorderLayout.CENTER);
		this.add(getGraphScaleSpinnerPanel(), BorderLayout.SOUTH);
	}

	/**
	 * Returns the panel containing the graphScaleSpinner and its label.
	 * @return the graph scale spinner panel
	 */
	private JPanel getGraphScaleSpinnerPanel() {
		if (graphSpinnerPanel == null) {
			graphSpinnerPanel = new JPanel();
			graphSpinnerPanel.setLayout(new BoxLayout(graphSpinnerPanel, BoxLayout.X_AXIS));
			graphSpinnerPanel.add(new JLabel(spinnerLabelText));
			graphSpinnerPanel.add(Box.createHorizontalStrut(5));
			graphSpinnerPanel.add(Box.createHorizontalGlue());
			graphSpinnerPanel.add(getGraphScaleSpinner());
		}
		return graphSpinnerPanel;
	}

	/**
	 * Returns the graphScaleSpinner used in this panel.
	 * @return graphScaleSpinner used
	 */
	private DoubleSpinner getGraphScaleSpinner() {

		if (graphScaleSpinner == null) {
			graphScaleSpinner = new DoubleSpinner(createSpinnerNumberModel());
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

	/**
	 * Creates the {@link SpinnerNumberModel} which can be used for the
	 * graphScaleSpinner.
	 * @return a number model for the graph scale spinner
	 */
	public SpinnerNumberModel createSpinnerNumberModel() {
		return new SpinnerNumberModel(MINIMUM_SPINNER_VALUE, MINIMUM_SPINNER_VALUE, maximumSpinnerValue, SPINNER_STEP_SIZE);
	}

	/**
	 * Updates the maximum domain axis value for all charts contained in this
	 * panel to the value set on the graphScaleSpinner.
	 */
	protected void updateMaximumDomainAxisValueForGraphs() {
		for (ResponseChartPanel chartPanel : chartPanels) {
			chartPanel.setMaximumDomainAxisValue(getGraphScaleSpinner().getValue());
		}
	}

}

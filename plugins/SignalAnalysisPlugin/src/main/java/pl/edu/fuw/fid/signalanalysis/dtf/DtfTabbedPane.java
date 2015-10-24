package pl.edu.fuw.fid.signalanalysis.dtf;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.jfree.data.xy.XYSeries;

/**
 * @author ptr@mimuw.edu.pl
 */
public class DtfTabbedPane extends JTabbedPane {

	private final ArModel[] models;
	private final int spectrumSize;
	private final XYSeries[][] series;

	private int order = 0;
	private boolean normalized = true;

	private void refreshData() {
		if (order > 0) {
			ArModel theModel = models[order-1];
			int C = theModel.getChannelCount();
			ArModelData[][] data = theModel.computeSpectralData(spectrumSize, normalized);
			for (int i=0; i<C; ++i) for (int j=0; j<C; ++j) {
				ArModelData single = data[j][i]; // display is transposed
				series[i][j].clear();
				for (int f=0; f<single.length; ++f) {
					series[i][j].add(single.freqcs[f], single.values[f], f==single.length-1);
				}
			}
		}
	}

	public DtfTabbedPane(XYSeriesWithLegend[] criteria, String[] channels, ArModel[] models, int spectrumSize) {
		final int C = channels.length;

		this.models = models;
		this.spectrumSize = spectrumSize;
		this.series = new XYSeries[C][C];
		for (int i=0; i<C; ++i) {
			for (int j=0; j<C; ++j) {
				String label = (i == j)
					? "spectrum of "+channels[i]
					: channels[j]+"→"+channels[i]; // display is transposed
				series[i][j] = new XYSeries(label);
				series[i][j].setDescription(label);
			}
		}

		int maxOrder = models.length + 1;
		final DtfOrderCriteriaPanel criteriaPanel = DtfOrderCriteriaPanel.create(maxOrder, criteria);
		final DtfNormRadioPanel normPanel = new DtfNormRadioPanel();
		final DtfPlotsPanel plotsPanel = new DtfPlotsPanel(series);

		final JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(normPanel, BorderLayout.NORTH);
		mainPanel.add(plotsPanel, BorderLayout.CENTER);

		criteriaPanel.setListener(new DtfOrderSelectionListener() {
			@Override
			public void modelOrderSelected(int order) {
				boolean first = (DtfTabbedPane.this.order == 0);
				DtfTabbedPane.this.order = order;
				refreshData();
				plotsPanel.rescaleCharts();
				if (first) {
					plotsPanel.showAllCharts();
				}
			}
		});

		normPanel.setListener(new DtfNormSelectionListener() {
			@Override
			public void normalizedChanged(boolean normalized) {
				DtfTabbedPane.this.normalized = normalized;
				refreshData();
				plotsPanel.rescaleCharts();
			}
		});

		add("Model order", criteriaPanel);
		add("Transfer functions", mainPanel);
	}

}

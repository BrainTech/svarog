package pl.edu.fuw.fid.signalanalysis.dtf;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.jfree.data.xy.XYSeries;
import org.signalml.app.view.montage.visualreference.VisualReferenceModel;
import org.signalml.domain.montage.Montage;
import static org.signalml.plugin.i18n.PluginI18n._;
import static org.signalml.plugin.i18n.PluginI18n._R;

/**
 * Tabbed display of the results from the DTF method.
 * Consists of three tabs:<ul>
 * <li>model order selection,</li>
 * <li>charts of the transfer functions,</li>
 * <li>visualization of the spatial connectivity.</li>
 * </ul>
 *
 * @author ptr@mimuw.edu.pl
 */
public class DtfTabbedPane extends JTabbedPane {

	private final ArModel[] models;
	private final int spectrumSize;
	private final XYSeries[][] series;
	private final DtfArrowsDisplay arrowsDisplay;

	private int order = 0;
	private boolean normalized = true;
	private Double freqMin, freqMax;

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
			RealMatrix transfer = null;
			if (freqMin != null && freqMax != null && freqMin <= freqMax) {
				transfer = new Array2DRowRealMatrix(C, C);
				RealMatrix counts = new Array2DRowRealMatrix(C, C);
				for (int i=0; i<C; ++i) for (int j=0; j<C; ++j) {
					ArModelData single = data[j][i]; // display is transposed
					for (int f=0; f<single.length; ++f) {
						if (freqMin <= single.freqcs[f] && single.freqcs[f] <= freqMax) {
							transfer.addToEntry(i, j, single.values[f]);
							counts.addToEntry(i, j, 1.0);
						}
					}
				}
				for (int i=0; i<C; ++i) for (int j=0; j<C; ++j) {
					double count = counts.getEntry(i, j);
					if (count > 0) {
						transfer.setEntry(i, j, transfer.getEntry(i, j) / count);
					}
				}
			}
			arrowsDisplay.setTransferData(transfer);
		}
	}

	public DtfTabbedPane(XYSeriesWithLegend[] criteria, String[] channels, ArModel[] models, int spectrumSize, Montage sources) {
		final int C = channels.length;

		this.models = models;
		this.spectrumSize = spectrumSize;
		this.series = new XYSeries[C][C];
		for (int i=0; i<C; ++i) {
			for (int j=0; j<C; ++j) {
				String label = (i == j)
					? _R("spectrum of {0}", channels[i])
					: channels[j]+"→"+channels[i]; // display is transposed
				series[i][j] = new XYSeries(label);
				series[i][j].setDescription(label);
			}
		}
		this.freqMin = 0.0;
		this.freqMax = 0.5 * models[0].getSamplingFrequency();

		int maxOrder = models.length + 1;
		final DtfOrderCriteriaPanel criteriaPanel = DtfOrderCriteriaPanel.create(maxOrder, criteria);
		final DtfNormRadioPanel normPanel = new DtfNormRadioPanel();
		final DtfPlotsPanel plotsPanel = new DtfPlotsPanel(series);

		final JButton copyModelDataButton = new JButton(_("copy model coefficients to clipboard"));
		final JPanel copyModelDataPanel = new JPanel(new BorderLayout());
		copyModelDataButton.setEnabled(false);
		copyModelDataButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int order = DtfTabbedPane.this.order;
				String output = DtfTabbedPane.this.models[order-1].exportCoefficients();
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(output), null);
			}
		});
		copyModelDataPanel.add(copyModelDataButton, BorderLayout.EAST);

		VisualReferenceModel visualModel = new VisualReferenceModel();
		visualModel.setMontage(sources);
		arrowsDisplay = new DtfArrowsDisplay(visualModel);
		final JScrollPane arrowsPane = new JScrollPane(arrowsDisplay, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		arrowsPane.setPreferredSize(new Dimension(740, 440));
		arrowsDisplay.setViewport(arrowsPane.getViewport());

		DtfFrequencyRangePanel frequencyPanel = new DtfFrequencyRangePanel(freqMin, freqMax);

		final JPanel orderPanel = new JPanel(new BorderLayout());
		orderPanel.add(copyModelDataPanel, BorderLayout.NORTH);
		orderPanel.add(criteriaPanel, BorderLayout.CENTER);

		final JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(normPanel, BorderLayout.NORTH);
		mainPanel.add(plotsPanel, BorderLayout.CENTER);

		final JPanel visualPanel = new JPanel(new BorderLayout());
		visualPanel.add(frequencyPanel, BorderLayout.NORTH);
		visualPanel.add(arrowsPane, BorderLayout.CENTER);

		criteriaPanel.setListener(new DtfOrderSelectionListener() {
			@Override
			public void modelOrderSelected(int order) {
				boolean first = (DtfTabbedPane.this.order == 0);
				DtfTabbedPane.this.order = order;
				refreshData();
				plotsPanel.rescaleCharts();
				if (first) {
					plotsPanel.showAllCharts();
					copyModelDataButton.setEnabled(true);
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

		frequencyPanel.setListener(new DtfFrequencyRangeListener() {
			@Override
			public void frequencyRangeChanged(Double freqMin, Double freqMax) {
				DtfTabbedPane.this.freqMin = freqMin;
				DtfTabbedPane.this.freqMax = freqMax;
				refreshData();
			}
		});

		add(_("Model order"), orderPanel);
		add(_("Transfer functions"), mainPanel);
		add(_("Transfer graph"), visualPanel);
	}

}

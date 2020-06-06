package org.signalml.app.method.ep;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import org.signalml.app.view.common.components.panels.AbstractPanel;
import org.signalml.method.ep.EvokedPotentialResult;

/** EvokedPotentialGraphPanel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class EvokedPotentialGraphPanel extends AbstractPanel {

	private static final long serialVersionUID = 1L;

	public static enum ChartType {
		NORMAL,
		STRIPPED,
		TOP,
		BOTTOM
	}

	EvokedPotentialResult result;

	private EvokedPotentialChart[] chartPanels;
	double[] timeValues;

	double globalMin;
	double globalMax;

	public EvokedPotentialGraphPanel() {
		super();
	}

	public EvokedPotentialResult getResult() {
		return result;
	}

	public void setResult(EvokedPotentialResult result) {
		if (this.result != result) {
			this.result = result;
			this.removeAll();
			createCharts();

			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			for (EvokedPotentialChart chartPanel: chartPanels) {
				add(chartPanel);
			}

			revalidate();
			repaint();
		}
	}

	private void createCharts() {

		int channel, e;

		int channelCount = result.getChannelCount();
		int sampleCount = result.getSampleCount();

		chartPanels = new EvokedPotentialChart[channelCount];

		globalMin = Double.MAX_VALUE;
		globalMax = -Double.MAX_VALUE;

		for (double[][] samples: result.getAverageSamples()) {

			for (channel=0; channel<channelCount; channel++) {
				for (e=0; e<sampleCount; e++) {

					if (samples[channel][e] < globalMin) {
						globalMin = samples[channel][e];
					}
					if (samples[channel][e] > globalMax) {
						globalMax = samples[channel][e];
					}

				}

			}
		}

		if (globalMin == globalMax) {
			//if all the samples are equal to 0, we want the graphs to show it with some
			//margins
			globalMin -= 100.0;
			globalMax += 100.0;
		}

		timeValues = new double[sampleCount];
		float samplingFrequency = result.getSamplingFrequency();

		for (e=0; e<sampleCount; e++) {
			timeValues[e] = (((double) e) / samplingFrequency) + result.getStartTime();
		}

		for (channel=0; channel<channelCount; channel++) {
			List<double[]> channelSamples = new ArrayList<>();

			for (int i = 0; i < result.getAverageSamples().size(); i++) {
				double[][] data = result.getAverageSamples().get(i);
				channelSamples.add(data[channel]);
			}

			ChartType chartType;
			if (channel == channelCount-1) {
				chartType = ChartType.BOTTOM;
			}
			else if (channel == 0 && channelCount != 1) {
				chartType = ChartType.TOP;
			} else {
				chartType = ChartType.STRIPPED;
			}

			chartPanels[channel] = new EvokedPotentialChart(timeValues, channelSamples, globalMin, globalMax,
					result.getLabels()[channel], chartType, result);

		}

	}

	public void savePanelToFile(File file) throws IOException {
		BufferedImage bi = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.createGraphics();
		this.paint(g);
		g.dispose();
		ImageIO.write(bi,"png", file);
	}

}

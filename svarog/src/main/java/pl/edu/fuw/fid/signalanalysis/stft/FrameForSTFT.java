package pl.edu.fuw.fid.signalanalysis.stft;

import pl.edu.fuw.fid.signalanalysis.waveform.ImageChart;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Random;
import javax.swing.event.ChangeEvent;
import org.jfree.chart.ChartPanel;
import org.signalml.app.view.book.wignermap.WignerMapPalette;
import org.signalml.math.fft.WindowType;
import pl.edu.fuw.fid.signalanalysis.SignalAnalysisTools;
import pl.edu.fuw.fid.signalanalysis.SingleSignal;
import pl.edu.fuw.fid.signalanalysis.waveform.ImageChartPanel;
import pl.edu.fuw.fid.signalanalysis.waveform.ImageChartPanelListener;
import pl.edu.fuw.fid.signalanalysis.waveform.SignalChart;
import pl.edu.fuw.fid.signalanalysis.NonInteractiveChartPanel;
import pl.edu.fuw.fid.signalanalysis.waveform.TimeFrequency;

/**
 * User interface for interactive Short-Time Fourier Transform.
 *
 * @author ptr@mimuw.edu.pl
 */
public class FrameForSTFT extends javax.swing.JFrame {

	private boolean initialized = false;

    /**
     * Creates new form FrameForSTFT
     */
    public FrameForSTFT() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        settingsPanel = new javax.swing.JPanel();
        windowPanel = new javax.swing.JPanel();
        windowTypeComboBox = new javax.swing.JComboBox<>();
        windowTypeLabel = new javax.swing.JLabel();
        windowSizeComboBox = new javax.swing.JComboBox<>();
        windowSizeLabel = new javax.swing.JLabel();
        padToHeightCheckBox = new javax.swing.JCheckBox();
        frequencyPanel = new javax.swing.JPanel();
        maxFrequencySlider = new javax.swing.JSlider();
        palettePanel = new javax.swing.JPanel();
        paletteComboBox = new javax.swing.JComboBox<>();
        invertCheckBox = new javax.swing.JCheckBox();
        mainPanel = new javax.swing.JPanel();
        mainSignalPanel = new javax.swing.JPanel();
        mainImagePanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Short-Time Fourier Transform");
        setMinimumSize(new java.awt.Dimension(600, 300));

        windowPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Window"));

        windowTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BARTLETT", "GAUSSIAN", "HAMMING", "HANN", "RECTANGULAR", "WELCH" }));

        windowTypeLabel.setLabelFor(windowTypeComboBox);
        windowTypeLabel.setText("Type:");

        windowSizeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "32", "64", "128", "256", "512", "1024" }));

        windowSizeLabel.setLabelFor(windowSizeComboBox);
        windowSizeLabel.setText("Size:");

        padToHeightCheckBox.setText("pad to chart height");

        javax.swing.GroupLayout windowPanelLayout = new javax.swing.GroupLayout(windowPanel);
        windowPanel.setLayout(windowPanelLayout);
        windowPanelLayout.setHorizontalGroup(
            windowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(windowPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(windowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(windowPanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(padToHeightCheckBox)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(windowPanelLayout.createSequentialGroup()
                        .addGroup(windowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(windowTypeLabel)
                            .addComponent(windowSizeLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                        .addGroup(windowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(windowSizeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(windowTypeComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        windowPanelLayout.setVerticalGroup(
            windowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(windowPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(windowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(windowTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(windowTypeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(windowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(windowSizeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(windowSizeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(padToHeightCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        frequencyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Max frequency (Hz)"));

        maxFrequencySlider.setMajorTickSpacing(10);
        maxFrequencySlider.setMaximum(64);
        maxFrequencySlider.setMinorTickSpacing(5);
        maxFrequencySlider.setValue(64);

        javax.swing.GroupLayout frequencyPanelLayout = new javax.swing.GroupLayout(frequencyPanel);
        frequencyPanel.setLayout(frequencyPanelLayout);
        frequencyPanelLayout.setHorizontalGroup(
            frequencyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frequencyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(maxFrequencySlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        frequencyPanelLayout.setVerticalGroup(
            frequencyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frequencyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(maxFrequencySlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        palettePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Palette"));

        paletteComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "RAINBOW", "GRAYSCALE" }));

        invertCheckBox.setText("invert colours");

        javax.swing.GroupLayout palettePanelLayout = new javax.swing.GroupLayout(palettePanel);
        palettePanel.setLayout(palettePanelLayout);
        palettePanelLayout.setHorizontalGroup(
            palettePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(palettePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(palettePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(palettePanelLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(invertCheckBox)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(palettePanelLayout.createSequentialGroup()
                        .addComponent(paletteComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        palettePanelLayout.setVerticalGroup(
            palettePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(palettePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(paletteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(invertCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout settingsPanelLayout = new javax.swing.GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(windowPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(frequencyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(palettePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        settingsPanelLayout.setVerticalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addComponent(windowPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(frequencyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(palettePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(settingsPanel, java.awt.BorderLayout.LINE_START);

        mainSignalPanel.setMaximumSize(new java.awt.Dimension(2147483647, 100));
        mainSignalPanel.setMinimumSize(new java.awt.Dimension(0, 100));
        mainSignalPanel.setLayout(new java.awt.BorderLayout());

        mainImagePanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainImagePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
            .addComponent(mainSignalPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addComponent(mainImagePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainSignalPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Create and display the form */
		java.awt.EventQueue.invokeLater(() -> {
			Random random = new Random();
			final double[] samples = random.doubles(1280).toArray();

			SingleSignal signal = new SingleSignal() {
				@Override
				public void getSamples(int start, int length, double[] buffer) {
					for (int i=0; i<length; ++i) {
						int j = start + i - 1280;
						buffer[i] = j>=0 && j<samples.length ? samples[j]-0.5 : 0.0;
					}
				}
				@Override
				public double getSamplingFrequency() {
					return 128.0;
				}
			};

			FrameForSTFT frame = new FrameForSTFT();
			frame.initialize(signal, 10.0, 20.0, 64.0);
			frame.setVisible(true);
		});
	}

    public void initialize(SingleSignal signal, double tMin, double tMax, double fMax) {
		if (initialized) {
			return;
		}
		initialized = true;

		int maxFrequency = (int) Math.round(fMax);
		maxFrequencySlider.setMaximum(maxFrequency);
		maxFrequencySlider.setValue(maxFrequency);

		ImageRendererForSTFT renderer = new ImageRendererForSTFT(signal);
		updatePadToHeight(renderer);
		updatePalette(renderer);
		updateWindowLength(renderer);
		updateWindowType(renderer);
		renderer.setPadToHeight(false);

		ImageChart imageChart = new ImageChart(renderer, tMin, tMax, 0, fMax);
		final ImageChartPanel imagePanel = new ImageChartPanel(imageChart);
		imageChart.setOnComputationFinished(() -> {
			repaintChartPanel(imagePanel);
		});

		SignalChart signalChart = new SignalChart(signal, tMin, tMax);
		final ChartPanel signalPanel = new NonInteractiveChartPanel(signalChart);
		signalPanel.setMinimumDrawHeight(100);

		imagePanel.setListener(new ImageChartPanelListener() {
			@Override
			public void mouseExited() {
				signalChart.clearWaveform();
			}

			@Override
			public void mouseMoved(double dx, double dy) {
				Integer ws = SignalAnalysisTools.parsePositiveInteger(windowSizeComboBox.getSelectedItem());
				WindowType wt = WindowType.valueOf(windowTypeComboBox.getSelectedItem().toString());
				TimeFrequency tf = renderer.getTimeFrequency(dx, dy);
				if (tf != null) {
					signalChart.setWaveform(new SineWaveform(tf.f, 0.5*ws/signal.getSamplingFrequency()), wt, tf.t, tf.v);
				}
			}
		});

		maxFrequencySlider.addChangeListener((ChangeEvent ev) -> {
			imageChart.setMaxFrequency(maxFrequencySlider.getValue(), false);
		});
		padToHeightCheckBox.addActionListener((ActionEvent ev) -> {
			updatePadToHeight(renderer);
			repaintChartPanel(imagePanel);
		});
		paletteComboBox.addActionListener((ActionEvent ev) -> {
			updatePalette(renderer);
			repaintChartPanel(imagePanel);
		});
		invertCheckBox.addActionListener((ActionEvent ev) -> {
			updatePalette(renderer);
			repaintChartPanel(imagePanel);
		});
		windowSizeComboBox.addActionListener((ActionEvent ev) -> {
			updateWindowLength(renderer);
			repaintChartPanel(imagePanel);
		});
		windowTypeComboBox.addActionListener((ActionEvent ev) -> {
			updateWindowType(renderer);
			repaintChartPanel(imagePanel);
		});

		mainImagePanel.add(imagePanel, BorderLayout.CENTER);
		mainSignalPanel.add(signalPanel, BorderLayout.CENTER);
    }

	private void updatePadToHeight(ImageRendererForSTFT renderer) {
		renderer.setPadToHeight(padToHeightCheckBox.isSelected());
	}

	private void updatePalette(ImageRendererForSTFT renderer) {
		renderer.setPaletteType(WignerMapPalette.valueOf(paletteComboBox.getSelectedItem().toString()));
		renderer.setInverted(invertCheckBox.isSelected());
	}

	private void updateWindowLength(ImageRendererForSTFT renderer) {
		renderer.setWindowLength(SignalAnalysisTools.parsePositiveInteger(windowSizeComboBox.getSelectedItem()));
	}

	private void updateWindowType(ImageRendererForSTFT renderer) {
		renderer.setWindowType(WindowType.valueOf(windowTypeComboBox.getSelectedItem().toString()));
	}

	private void repaintChartPanel(ChartPanel chartPanel) {
		chartPanel.setRefreshBuffer(true);
		chartPanel.repaint();
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel frequencyPanel;
    private javax.swing.JCheckBox invertCheckBox;
    private javax.swing.JPanel mainImagePanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel mainSignalPanel;
    private javax.swing.JSlider maxFrequencySlider;
    private javax.swing.JCheckBox padToHeightCheckBox;
    private javax.swing.JComboBox<String> paletteComboBox;
    private javax.swing.JPanel palettePanel;
    private javax.swing.JPanel settingsPanel;
    private javax.swing.JPanel windowPanel;
    private javax.swing.JComboBox<String> windowSizeComboBox;
    private javax.swing.JLabel windowSizeLabel;
    private javax.swing.JComboBox<String> windowTypeComboBox;
    private javax.swing.JLabel windowTypeLabel;
    // End of variables declaration//GEN-END:variables
}

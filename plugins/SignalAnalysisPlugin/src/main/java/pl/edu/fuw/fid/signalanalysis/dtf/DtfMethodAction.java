package pl.edu.fuw.fid.signalanalysis.dtf;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.SingularMatrixException;
import org.signalml.app.document.signal.SignalDocument;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.view.signal.signalselection.ChannelSpacePanel;
import org.signalml.domain.montage.Montage;
import org.signalml.domain.montage.MontageMismatchException;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.space.SignalSpaceConstraints;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import pl.edu.fuw.fid.signalanalysis.MultiSignal;
import pl.edu.fuw.fid.signalanalysis.MultiStoredSignal;

/**
 * @author ptr@mimuw.edu.pl
 */
public class DtfMethodAction extends AbstractSignalMLAction {

	private static final int SPECTRUM_SIZE = 100;
	private static final String TITLE = "Directed Transfer Function";

	private final SvarogAccessGUI guiAccess;
	private final SvarogAccessSignal signalAccess;

	public DtfMethodAction(SvarogAccessGUI guiAccess, SvarogAccessSignal signalAccess) {
		super();
		this.guiAccess = guiAccess;
		this.signalAccess = signalAccess;
		this.setText(TITLE);
	}

	private void initFX(JFXPanel fxPanel, ArModel model, String[] channelNames) {
		final GridPane pane = new GridPane();
		final StackPane stack = new StackPane(pane);

		final int C = model.getChannelCount();
		final double nyquist = 0.5 * model.getSamplingFrequency();
		final NumberAxis axMaster = new NumberAxis(0, nyquist, 10.0);
		final NumberAxis ayMaster = new NumberAxis(0, 1.0, 0.1);
		final LineChart master = new LineChart(axMaster, ayMaster);
		master.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				stack.getChildren().setAll(pane);
			}
		});

		List<XYChart.Data<Number,Number>>[][] values = new LinkedList[C][C];
		for (int i=0; i<C; ++i) for (int j=0; j<C; ++j) {
			values[i][j] = new LinkedList<XYChart.Data<Number,Number>>();
		}
		for (int f=0; f<SPECTRUM_SIZE; ++f) {
			double freq = f * nyquist / SPECTRUM_SIZE;
			RealMatrix H = model.computeTransferMatrix(freq, true);
			for (int i=0; i<C; ++i) for (int j=0; j<C; ++j) {
				double value = H.getEntry(i, j);
				values[i][j].add(new XYChart.Data<Number, Number>(freq, value));
			}
		}
		for (int i=0; i<C; ++i) for (int j=0; j<C; ++j) {
			final int fi=i, fj=j;
			XYChart.Series<Number,Number> serie = new XYChart.Series<Number,Number>(FXCollections.observableArrayList(values[i][j]));
			final ObservableList<XYChart.Series<Number,Number>> data = FXCollections.observableArrayList(serie);
			final NumberAxis ax = new NumberAxis(0, nyquist, 10.0);
			final NumberAxis ay = new NumberAxis(0, 1.0, 0.1);
			final LineChart chart = new LineChart(ax, ay, data);
			final String title = channelNames[i]+"→"+channelNames[j];
			chart.setCreateSymbols(false);
			chart.setLegendVisible(false);
			chart.setMinSize(50, 50);
			chart.setTitle(title);
			pane.add(chart, i, j);
			chart.setOnMouseClicked(new EventHandler<MouseEvent>() {
				private boolean zoom = false;
				@Override
				public void handle(MouseEvent event) {
					zoom = ! zoom;
					if (zoom) {
						stack.getChildren().setAll(chart);
						chart.setTitle(title+" (click again to view all)");
					} else {
						stack.getChildren().setAll(pane);
						chart.setTitle(title);
						pane.add(chart, fi, fj);
					}
				}
			});
		}

		Scene scene = new Scene(stack);
		fxPanel.setScene(scene);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			SignalDocument signalDocument = (SignalDocument) signalAccess.getActiveSignalDocument();
			SignalView signalView = (SignalView) signalDocument.getDocumentView();
			SignalSpaceConstraints signalSpaceConstraints = signalView.createSignalSpaceConstraints();

			JSpinner orderSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
			JPanel bottomPanel = new JPanel(new BorderLayout());
			bottomPanel.add(new JLabel("AR model order:"), BorderLayout.WEST);
			bottomPanel.add(orderSpinner, BorderLayout.CENTER);

			JPanel panel = new JPanel(new BorderLayout());
			ChannelSpacePanel channelPanel = new ChannelSpacePanel();
			channelPanel.setConstraints(signalSpaceConstraints);
			panel.add(channelPanel, BorderLayout.CENTER);
			panel.add(bottomPanel, BorderLayout.SOUTH);
			
			int result = JOptionPane.showConfirmDialog(guiAccess.getDialogParent(), panel, TITLE, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {

				int[] selectedChannels = channelPanel.getChannelList().getSelectedIndices();
				if (selectedChannels.length == 0) {
					JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "Select at least one channel.", "Try again", JOptionPane.WARNING_MESSAGE);
					return;
				}

				SignalProcessingChain signalChain = SignalProcessingChain.createFilteredChain(signalDocument.getSampleSource());
				Montage oldMontage = signalDocument.getMontage();
				if (oldMontage != null) {
					signalChain.applyMontageDefinition(oldMontage);
				}

				final String[] channelNames = new String[selectedChannels.length];
				for (int c=0; c<selectedChannels.length; ++c) {
					channelNames[c] = signalChain.getOutput().getLabel(selectedChannels[c]);
				}

				MultiSignal data = new MultiStoredSignal(signalChain.getOutput(), selectedChannels);
				int order = (Integer) orderSpinner.getValue();
				final ArModel model = ArModel.compute(data, order);

				JFrame frame = new JFrame("FX");
				frame.setSize(800, 600);
				final JFXPanel fxPanel = new JFXPanel();
				frame.add(fxPanel);
				frame.setVisible(true);

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						initFX(fxPanel, model, channelNames);
					}
				});
			}
		} catch (SingularMatrixException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "Cannot compute DTF. Lag autocorrelation matrix is singular.", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (MontageMismatchException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "Montage mismatch.", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (NoActiveObjectException ex) {
			JOptionPane.showMessageDialog(guiAccess.getDialogParent(), "Choose an active signal first.", "Error", JOptionPane.WARNING_MESSAGE);
		}
	}

}

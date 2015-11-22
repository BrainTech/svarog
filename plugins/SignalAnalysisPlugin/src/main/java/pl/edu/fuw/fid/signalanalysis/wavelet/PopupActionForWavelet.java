package pl.edu.fuw.fid.signalanalysis.wavelet;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.ExportedSignalSelection;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/**
 * @author ptr@mimuw.edu.pl
 */
public class PopupActionForWavelet extends AbstractSignalMLAction {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PopupActionForWavelet.class);

	private static final String TITLE = "Wavelet Transform";

	private final SvarogAccessSignal signalAccess;

	private void initFX(JFXPanel fxPanel, ExportedSignalSelection selection) throws IOException, NoActiveObjectException {
		PaneForWavelet pane = new PaneForWavelet(signalAccess, selection);
        Scene scene = new Scene(pane.getPane(), 500, 300);
		fxPanel.setScene(scene);
	}

	public PopupActionForWavelet(SvarogAccessSignal signalAccess) {
		super();
		this.signalAccess = signalAccess;
		setText(TITLE);
	}

	private ExportedSignalSelection getActiveSelection() {
		try {
			return signalAccess.getActiveSelection();
		} catch (NoActiveObjectException ex) {
			return null;
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// This method is invoked on Swing thread
		final ExportedSignalSelection selection = getActiveSelection();
		if (selection == null || selection.getChannel() < 0) {
			JOptionPane.showMessageDialog(null, "Select valid single-channel signal fragment.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JFrame frame = new JFrame(TITLE);
		frame.setSize(800, 600);
		final JFXPanel fxPanel = new JFXPanel();
		frame.add(fxPanel);
		frame.setVisible(true);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					initFX(fxPanel, selection);
				} catch (NoActiveObjectException ex) {
					logger.error("could not access signal selection", ex);
				} catch (IOException ex) {
					logger.error("could not initialize plugin", ex);
				}
			}
		});
	}

}

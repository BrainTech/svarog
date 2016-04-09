package pl.edu.fuw.fid.signalanalysis.stft;

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
 * Action performed when user requests interactive Short-Time Fourier Transform
 * computation on selected signal fragment.
 *
 * @author ptr@mimuw.edu.pl
 */
public class PopupActionForSTFT extends AbstractSignalMLAction {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PopupActionForSTFT.class);

	private static final String TITLE = "Short-Time Fourier Transform";

	private final SvarogAccessSignal signalAccess;

	private void initFX(JFXPanel fxPanel, ExportedSignalSelection selection) throws IOException, NoActiveObjectException {
		PaneForSTFT pane = new PaneForSTFT(signalAccess, selection);
        Scene scene = new Scene(pane.getPane(), 500, 300);
		fxPanel.setScene(scene);
	}

	public PopupActionForSTFT(SvarogAccessSignal signalAccess) {
		super();
		this.signalAccess = signalAccess;
		setText("from selection");
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
		if (selection == null) {
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

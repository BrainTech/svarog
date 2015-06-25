package pl.edu.fuw.fid.signalanalysis.stft;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javax.swing.JFrame;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/**
 * @author ptr@mimuw.edu.pl
 */
public class PopupActionForSTFT extends AbstractSignalMLAction {

	private final SvarogAccessSignal signalAccess;

	private void initFX(JFXPanel fxPanel) throws NoActiveObjectException, IOException {
		PaneForSTFT pane = new PaneForSTFT(signalAccess);
        Scene scene = new Scene(pane.getPane(), 500, 300);
		fxPanel.setScene(scene);
	}

	public PopupActionForSTFT(SvarogAccessSignal signalAccess) {
		super();
		this.signalAccess = signalAccess;
		setText("Short-Time Fourier Transform");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// This method is invoked on Swing thread
		JFrame frame = new JFrame("FX");
		frame.setSize(500, 500);
		final JFXPanel fxPanel = new JFXPanel();
		frame.add(fxPanel);
		frame.setVisible(true);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					initFX(fxPanel);
				} catch (NoActiveObjectException ex) {
					Logger.getLogger(PopupActionForSTFT.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IOException ex) {
					Logger.getLogger(PopupActionForSTFT.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		});
	}

}

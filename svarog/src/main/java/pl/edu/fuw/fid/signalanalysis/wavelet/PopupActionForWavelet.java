package pl.edu.fuw.fid.signalanalysis.wavelet;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.signal.ChannelSamples;
import org.signalml.plugin.export.signal.ExportedSignalSelection;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.AbstractSignalMLAction;
import pl.edu.fuw.fid.signalanalysis.SimpleSingleSignal;
import pl.edu.fuw.fid.signalanalysis.SingleSignal;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * Action performed when user requests interactive Wavelet Transform
 * computation on selected signal fragment.
 *
 * @author ptr@mimuw.edu.pl
 */
public class PopupActionForWavelet extends AbstractSignalMLAction {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PopupActionForWavelet.class);

	private static final String TITLE = _("Wavelet Transform");

	private final SvarogAccessSignal signalAccess;

	public PopupActionForWavelet(SvarogAccessSignal signalAccess) {
		super();
		this.signalAccess = signalAccess;
		setText(_("from selection"));
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
			JOptionPane.showMessageDialog(null, _("Select valid single-channel signal fragment."), _("Error"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		final int selectedChannel = Math.max(selection.getChannel(), 0);
		final ChannelSamples samples;
		try {
			samples = signalAccess.getActiveProcessedSignalSamples(selectedChannel);
		} catch (NoActiveObjectException ex) {
			logger.error("could not access signal selection", ex);
			return;
		}

		double tMin = selection.getPosition();
		double tMax = selection.getEndPosition();

		SingleSignal signal = new SimpleSingleSignal(samples);

		FrameForWavelet frame = new FrameForWavelet();
		frame.initialize(signal, tMin, tMax, signal.getSamplingFrequency() / 2);
		frame.setVisible(true);
	}

}

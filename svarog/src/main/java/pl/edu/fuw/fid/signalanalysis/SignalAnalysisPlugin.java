package pl.edu.fuw.fid.signalanalysis;

import javax.swing.JMenu;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import pl.edu.fuw.fid.signalanalysis.dtf.DtfMethodAction;
import pl.edu.fuw.fid.signalanalysis.ica.DescribeComponentsAction;
import pl.edu.fuw.fid.signalanalysis.ica.IcaMethodAction;
import pl.edu.fuw.fid.signalanalysis.stft.PopupActionForSTFT;
import pl.edu.fuw.fid.signalanalysis.ica.ZeroMethodAction;
import pl.edu.fuw.fid.signalanalysis.stft.AveragedStftDialog;
import pl.edu.fuw.fid.signalanalysis.stft.ImageRendererForSTFT;
import pl.edu.fuw.fid.signalanalysis.waveform.AveragedBaseAction;
import pl.edu.fuw.fid.signalanalysis.wavelet.AveragedWaveletDialog;
import pl.edu.fuw.fid.signalanalysis.wavelet.ImageRendererForWavelet;
import pl.edu.fuw.fid.signalanalysis.wavelet.PopupActionForWavelet;
import static org.signalml.app.util.i18n.SvarogI18n._;

/**
 * Main class for the Signal Analysis plugin.
 *
 * @author ptr@mimuw.edu.pl
 */
public class SignalAnalysisPlugin implements Plugin {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SignalAnalysisPlugin.class);

	private SvarogAccessGUI guiAccess;
	private SvarogAccessSignal signalAccess;

	@Override
	public void register(SvarogAccess access) {
		guiAccess = access.getGUIAccess();
		signalAccess = access.getSignalAccess();

		JMenu icaMenu = new JMenu(_("Independent Component Analysis"));
		icaMenu.add(new IcaMethodAction(guiAccess, signalAccess));
		icaMenu.add(new DescribeComponentsAction(guiAccess, signalAccess));
		icaMenu.add(new ZeroMethodAction(guiAccess, signalAccess));

		guiAccess.addSubmenuToToolsMenu(icaMenu);
		guiAccess.addButtonToToolsMenu(new DtfMethodAction(guiAccess, signalAccess));

		JMenu stftMenu = new JMenu(_("Short-Time Fourier Transform"));
		stftMenu.add(new PopupActionForSTFT(signalAccess));
		try {
			stftMenu.add(new AveragedBaseAction(guiAccess, signalAccess, AveragedStftDialog.class, ImageRendererForSTFT.class));
		} catch (Exception ex) {
			logger.error(ex);
		}
		guiAccess.addSubmenuToToolsMenu(stftMenu);

		JMenu wtMenu = new JMenu(_("Wavelet Transform"));
		wtMenu.add(new PopupActionForWavelet(signalAccess));
		try {
			wtMenu.add(new AveragedBaseAction(guiAccess, signalAccess, AveragedWaveletDialog.class, ImageRendererForWavelet.class));
		} catch (Exception ex) {
			logger.error(ex);
		}
		guiAccess.addSubmenuToToolsMenu(wtMenu);
	}

}

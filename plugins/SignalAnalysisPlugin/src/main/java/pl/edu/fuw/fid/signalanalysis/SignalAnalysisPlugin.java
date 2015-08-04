package pl.edu.fuw.fid.signalanalysis;

import java.io.File;
import org.fuin.utils4j.Utils4J;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import pl.edu.fuw.fid.signalanalysis.stft.PopupActionForSTFT;
import pl.edu.fuw.fid.signalanalysis.wavelet.PopupActionForWavelet;

/**
 * @author ptr@mimuw.edu.pl
 */
public class SignalAnalysisPlugin implements Plugin {

	private SvarogAccessGUI guiAccess;

	private SvarogAccessSignal signalAccess;

	@Override
	public void register(SvarogAccess access) {
		Utils4J.addToClasspath("file:///"+System.getProperty("java.home")+ File.separator+"lib"+File.separator+"jfxrt.jar");
		guiAccess = access.getGUIAccess();
		signalAccess = access.getSignalAccess();

		guiAccess.addButtonToSignalPlotPopupMenu(new PopupActionForSTFT(signalAccess));
		guiAccess.addButtonToSignalPlotPopupMenu(new PopupActionForWavelet(signalAccess));
	}

}

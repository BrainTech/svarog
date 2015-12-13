package pl.edu.fuw.fid.signalanalysis;

import java.io.File;
import javax.swing.JMenu;
import org.fuin.utils4j.Utils4J;
import org.signalml.plugin.export.Plugin;
import org.signalml.plugin.export.SvarogAccess;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.view.SvarogAccessGUI;
import pl.edu.fuw.fid.signalanalysis.dtf.DtfMethodAction;
import pl.edu.fuw.fid.signalanalysis.ica.DescribeComponentsAction;
import pl.edu.fuw.fid.signalanalysis.ica.IcaMethodAction;
import pl.edu.fuw.fid.signalanalysis.stft.PopupActionForSTFT;
import pl.edu.fuw.fid.signalanalysis.wavelet.PopupActionForWavelet;
import pl.edu.fuw.fid.signalanalysis.ica.ZeroMethodAction;
import pl.edu.fuw.fid.signalanalysis.stft.AveragedStftDialog;
import pl.edu.fuw.fid.signalanalysis.stft.ImageRendererForSTFT;
import pl.edu.fuw.fid.signalanalysis.waveform.AveragedBaseAction;
import pl.edu.fuw.fid.signalanalysis.wavelet.AveragedWaveletDialog;
import pl.edu.fuw.fid.signalanalysis.wavelet.ImageRendererForWavelet;

/**
 * @author ptr@mimuw.edu.pl
 */
public class SignalAnalysisPlugin implements Plugin {

	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SignalAnalysisPlugin.class);

	private SvarogAccessGUI guiAccess;
	private SvarogAccessSignal signalAccess;

	private void addToClasspathIfExists(String relativePath) {
		String path = System.getProperty("java.home")+File.separator+relativePath;
		if (new File(path).isFile()) {
			Utils4J.addToClasspath("file:///"+path);
		}
	}

	@Override
	public void register(SvarogAccess access) {
		// for java-7-oracle
		addToClasspathIfExists("lib"+File.separator+"jfxrt.jar");
		// for java-8-oracle
		addToClasspathIfExists("lib"+File.separator+"ext"+File.separator+"jfxrt.jar");

		guiAccess = access.getGUIAccess();
		signalAccess = access.getSignalAccess();

		JMenu icaMenu = new JMenu("Independent Component Analysis");
		icaMenu.add(new IcaMethodAction(guiAccess, signalAccess));
		icaMenu.add(new DescribeComponentsAction(guiAccess, signalAccess));
		icaMenu.add(new ZeroMethodAction(guiAccess, signalAccess));

		guiAccess.addSubmenuToToolsMenu(icaMenu);
		guiAccess.addButtonToToolsMenu(new DtfMethodAction(guiAccess, signalAccess));

		JMenu stftMenu = new JMenu("Short-Time Fourier Transform");
		stftMenu.add(new PopupActionForSTFT(signalAccess));
		try {
			stftMenu.add(new AveragedBaseAction(guiAccess, signalAccess, AveragedStftDialog.class, ImageRendererForSTFT.class));
		} catch (Exception ex) {
			logger.error(ex);
		}
		guiAccess.addSubmenuToToolsMenu(stftMenu);

		JMenu wtMenu = new JMenu("Wavelet Transform");
		wtMenu.add(new PopupActionForWavelet(signalAccess));
		try {
			wtMenu.add(new AveragedBaseAction(guiAccess, signalAccess, AveragedWaveletDialog.class, ImageRendererForWavelet.class));
		} catch (Exception ex) {
			logger.error(ex);
		}
		guiAccess.addSubmenuToToolsMenu(wtMenu);
	}

}

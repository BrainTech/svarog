package org.signalml.method.mp5;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.signalml.app.util.i18n.SvarogI18n._;

@XStreamAlias("mp5bundledexecutor")
public class MP5BundledProcessExecutor extends MP5LocalProcessExecutor {

	public MP5BundledProcessExecutor() {
		super();
		setName(_("execute from bundled executor"));

	}

	@Override
	public void setMp5ExecutablePath(String mp5ExecutablePath) {
	}

	@Override
	public String getMp5ExecutablePath() {
		File jar = new File(MP5LocalProcessExecutor.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		String jarDir = jar.getParentFile().getPath();
		// we need to do this, otherwise paths with spaces will have strange symbols, breaking MP executable look up
		try {
			jarDir = URLDecoder.decode(jarDir, "utf-8");
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(MP5BundledProcessExecutor.class.getName()).log(Level.SEVERE, null, ex);
		}
		jarDir = new File(jarDir).getPath();
		String executor_folder;
		String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);

		String bits = System.getProperty("sun.arch.data.model");

		if ((OS.contains("mac")) || (OS.contains("darwin"))) {
			executor_folder = "/mp/osx64/empi-osx64";
		} else if (OS.contains("win")) {

			executor_folder = "/mp/win" + bits + "/empi-win" + bits + ".exe";

		} else if (OS.indexOf("nux") >= 0) {
			executor_folder = "/mp/linux/empi-lin" + bits;
		} else {
			executor_folder = "";
		}

		return jarDir + executor_folder;
	}

}

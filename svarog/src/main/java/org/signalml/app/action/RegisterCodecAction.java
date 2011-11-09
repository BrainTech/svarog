/* RegisterCodecAction.java created 2007-09-19
 *
 */
package org.signalml.app.action;

import static org.signalml.app.SvarogApplication._;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.RegisterCodecDescriptor;
import org.signalml.app.util.XmlFileFilter;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.app.view.dialog.PleaseWaitDialog;
import org.signalml.app.view.dialog.RegisterCodecDialog;
import org.signalml.app.worker.CreateCodecReaderWorker;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.codec.SignalMLCodecReader;
import org.signalml.codec.SignalMLCodecSelector;
import org.signalml.codec.XMLSignalMLCodec;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/** RegisterCodecAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RegisterCodecAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(RegisterCodecAction.class);

	private SignalMLCodecManager codecManager;
	private RegisterCodecDialog registerCodecDialog;
	private PleaseWaitDialog pleaseWaitDialog;
	private SignalMLCodecSelector selector;
	private ApplicationConfiguration applicationConfig;

	public  RegisterCodecAction() {
		super();
		setText(_("Register new codec"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Register codec");

		RegisterCodecDescriptor model = new RegisterCodecDescriptor();

		boolean ok = registerCodecDialog.showDialog(model, true);
		if (!ok) {
			return;
		}

		createCodec(model);

	}

	public void initializeAll() {

		if (codecManager.getCodecCount() == 0) {

			File specsDir = new File(System.getProperty("user.dir"),"specs");

			if (specsDir.isDirectory()) {

				Log.debug("Registering all available codecs in scpec directory");

				File[] files = specsDir.listFiles(new XmlFileFilter());

				for (File file : files) {

					Log.debug("Registering codec: " + file);

					register(file);
				}
			} else {

				Log.debug("No such direcoty: " + specsDir);

				specsDir.mkdir();

				String urlBaseName = "http://eeg.pl:8080/applet/specs";

				List<String> codecsNameList = new LinkedList<String>();

				codecsNameList.add("EASYS.xml");
				codecsNameList.add("RAW.xml");
				codecsNameList.add("EDF.xml");

				URL url = null;

				for (String codecName : codecsNameList) {

					try {
						url = new URL(urlBaseName+"/"+codecName);

						InputStream inStream = url.openStream();

						File file = new File(specsDir, codecName);

						FileOutputStream fos = new FileOutputStream(file);

						byte[] buffer = new byte[1024];
						int bytesRead;

						while ((bytesRead = inStream.read(buffer)) != -1) {
							fos.write(buffer,0,bytesRead);
						}

						fos.close();
						inStream.close();

						register(file);

					} catch (MalformedURLException e) {
						Log.error("Cannot download codec from server: " + urlBaseName, e);
					} catch (IOException e) {
						Log.error("Cannot download codec from server: " + urlBaseName, e);
					}

				}
			}
		}
	}

	private void register(File file) {

		RegisterCodecDescriptor model = new RegisterCodecDescriptor();

		model.setSourceFile(file);
		model.setFormatName(file.getName().toString().replaceAll(".xml", ""));

		try {
			model.setCodec(new XMLSignalMLCodec(file, null));
		} catch (Exception e) {
			Log.debug("Not a proper codec descriptor");
		}

		createCodec(model);
	}

	private void createCodec(RegisterCodecDescriptor model) {

		// try to create codec to be sure that it works

		SignalMLCodec codec = model.getCodec();

		CreateCodecReaderWorker worker = new CreateCodecReaderWorker(codec, pleaseWaitDialog);

		worker.execute();

		pleaseWaitDialog.setActivity(_("creating codec reader"));
		pleaseWaitDialog.configureForIndeterminateSimulated();
		pleaseWaitDialog.waitAndShowDialogIn(null, 0, worker);

		SignalMLCodecReader reader = null;
		try {
			reader = worker.get();
		} catch (InterruptedException ex) {
			// ignore
		} catch (ExecutionException ex) {
			logger.error("Exception during worker exectution", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex.getCause());
			return;
		}

		if (reader == null) {
			logger.error("Failed to create codec");
			OptionPane.showError(null, "error.codecCompilationFailed");
			return;
		}

		String formatName = model.getFormatName();

		SignalMLCodec oldCodec = codecManager.getCodecForFormat(formatName);
		if (oldCodec != null) {
			codecManager.removeSignalMLCodec(oldCodec);
		}

		codec.setFormatName(formatName);
		codecManager.registerSignalMLCodec(codec);

		if (selector != null) {
			selector.setSelectedCodec(codec);
		}

		if (applicationConfig.isSaveConfigOnEveryChange()) {
			try {
				codecManager.writeToPersistence(null);
			} catch (IOException ex) {
				logger.error("Failed to save codec configuration", ex);
			}
		}

	}

	public SignalMLCodecManager getCodecManager() {
		return codecManager;
	}

	public void setCodecManager(SignalMLCodecManager codecManager) {
		this.codecManager = codecManager;
	}

	public RegisterCodecDialog getRegisterCodecDialog() {
		return registerCodecDialog;
	}

	public void setRegisterCodecDialog(RegisterCodecDialog registerCodecDialog) {
		this.registerCodecDialog = registerCodecDialog;
	}

	public SignalMLCodecSelector getSelector() {
		return selector;
	}

	public void setSelector(SignalMLCodecSelector selector) {
		this.selector = selector;
	}

	public PleaseWaitDialog getPleaseWaitDialog() {
		return pleaseWaitDialog;
	}

	public void setPleaseWaitDialog(PleaseWaitDialog pleaseWaitDialog) {
		this.pleaseWaitDialog = pleaseWaitDialog;
	}

	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

}

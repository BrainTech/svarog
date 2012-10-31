/* RegisterCodecAction.java created 2007-09-19
 *
 */
package org.signalml.app.action.document;

import static org.signalml.app.util.i18n.SvarogI18n._;
import static org.signalml.app.util.i18n.SvarogI18n._R;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.signalml.app.SvarogApplication;
import org.signalml.app.config.ApplicationConfiguration;
import org.signalml.app.model.document.RegisterCodecDescriptor;
import org.signalml.app.util.XmlFileFilter;
import org.signalml.app.view.common.dialogs.OptionPane;
import org.signalml.app.view.common.dialogs.PleaseWaitDialog;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.app.view.signal.signalml.RegisterCodecDialog;
import org.signalml.app.worker.document.CreateCodecReaderWorker;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.codec.SignalMLCodecReader;
import org.signalml.codec.SignalMLCodecSelector;
import org.signalml.codec.XMLSignalMLCodec;
import org.signalml.codec.StaticCodec;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/** RegisterCodecAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RegisterCodecAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(RegisterCodecAction.class);

	private static boolean initialized = false;

	private SignalMLCodecManager codecManager;
	private RegisterCodecDialog registerCodecDialog;
	private PleaseWaitDialog pleaseWaitDialog;
	private SignalMLCodecSelector selector;

	public RegisterCodecAction() {
		super();
		setText(_("Register new codec"));
		codecManager = SvarogApplication.getSharedInstance().getSignalMLCodecManager();
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Register codec");

		RegisterCodecDescriptor model = new RegisterCodecDescriptor();

		boolean ok = registerCodecDialog.showDialog(model, true);
		if (!ok)
			return;

		createCodec(model);
	}

	public void initializeAll() {

		if (initialized)
			return;
		initialized = true;

		File specsDir = new File(System.getProperty("user.dir"), "specs");

		if (!specsDir.isDirectory()) {
			logger.warn("Spec directory is not a directory: " + specsDir);
			return;
		}

		logger.debug("Registering static codecs");
		register(org.signalml.codec.precompiled.EASYS.class, "precompiled");
		register(org.signalml.codec.precompiled.EDF.class, "precompiled");
		register(org.signalml.codec.precompiled.M4D.class, "precompiled");

		logger.debug("Registering all available codecs in spec directory");
		File[] files = specsDir.listFiles(new XmlFileFilter());
		for (File file : files)
			register(file);
	}

	private void register(File file) {
		logger.info("Registering codec: " + file);

		try {
			jsignalml.compiler.CompiledClass<? extends jsignalml.Source> klass
				= loadFromFile("compiled", file);
			this.register(klass.theClass(), "compiled");
		} catch(Exception e) {
			logger.error("Failed to compile file " + file + ": " + e);
			OptionPane.showError(null, _R("Failed to compile file {0}: {1}", file, e));
		}
	}

	private void register(Class<? extends jsignalml.Source> source, String message) {
		logger.info("Registering codec: " + source.getCanonicalName());

		SignalMLCodec codec = new StaticCodec(source);

		RegisterCodecDescriptor model = new RegisterCodecDescriptor();
		model.setCodec(codec);
		model.setSourceFile(new File(""));
		model.setFormatName(codec.getFormatName() + " [" + message + "]");
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
			Dialogs.showExceptionDialog((Window) null, ex);
			return;
		}

		if (reader == null) {
			logger.error("Failed to compile the codec");
			OptionPane.showError(null, _("Failed to compile the codec"));
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

		if (getApplicationConfig().isSaveConfigOnEveryChange()) {
			try {
				codecManager.writeToPersistence(null);
			} catch (IOException ex) {
				logger.error("Failed to save codec configuration", ex);
			}
		}

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
		return SvarogApplication.getApplicationConfiguration();
	}

	// XXX: find a home for this function. Should it go to jsignalml?
	public static
		jsignalml.compiler.CompiledClass<? extends jsignalml.Source> loadFromFile(String pkg, File file)
		throws java.io.IOException,
			   java.lang.ClassNotFoundException,
			   org.xml.sax.SAXException
	{
			jsignalml.JavaClassGen gen =
				jsignalml.CodecParser.generateFromFile(file, "org.signalml.codec." + pkg, false);
			String name = gen.getFullClassName();
			CharSequence code = gen.getSourceCode();
			jsignalml.compiler.CompiledClass<jsignalml.Source> klass =
				jsignalml.compiler.CompiledClass.newCompiledClass(name, code);
			logger.info("class " + name + " has been sourced");
			return klass;
	}
}

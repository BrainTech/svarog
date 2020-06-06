/* RemoveCodecAction.java created 2007-09-19
 *
 */
package org.signalml.app.action.signal;

import java.awt.event.ActionEvent;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.signalml.app.config.ApplicationConfiguration;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.codec.SignalMLCodecSelector;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/** RemoveCodecAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class RemoveCodecAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(RemoveCodecAction.class);

	private SignalMLCodecManager codecManager;
	private SignalMLCodecSelector selector;
	private ApplicationConfiguration applicationConfig;

	public RemoveCodecAction() {
		super();
		setText(_("Remove codec"));
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Remove codec");

		SignalMLCodec codec = selector.getSelectedCodec();
		if (codec == null) {
			return;
		}

		int index = codecManager.getIndexOfCodec(codec);
		if (index < 0) {
			return;
		}

		codecManager.removeSignalMLCodecAt(index);
		int count = codecManager.getCodecCount();
		if (count > 0) {
			if (index >= count) {
				index = count - 1;
			}
			selector.setSelectedCodec(codecManager.getCodecAt(index));
		} else {
			selector.setSelectedCodec(null);
		}

		if (applicationConfig.isSaveConfigOnEveryChange()) {
			try {
				codecManager.writeToPersistence(null);
			} catch (IOException ex) {
				logger.error("Failed to save codec configuration", ex);
			}
		}

	}


	@Override
	public void setEnabledAsNeeded() {
		setEnabled(selector != null && selector.getSelectedCodec() != null);
	}

	public SignalMLCodecManager getCodecManager() {
		return codecManager;
	}

	public void setCodecManager(SignalMLCodecManager codecManager) {
		this.codecManager = codecManager;
	}

	public SignalMLCodecSelector getSelector() {
		return selector;
	}

	public void setSelector(SignalMLCodecSelector selector) {
		if (this.selector != selector) {
			this.selector = selector;
			setEnabledAsNeeded();
		}
	}

	public ApplicationConfiguration getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(ApplicationConfiguration applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

}

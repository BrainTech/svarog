package org.signalml.app.action.document.monitor;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import multiplexer.jmx.client.JmxClient;

import org.apache.log4j.Logger;
import org.signalml.app.view.workspace.ViewerElementManager;

// TODO dodać rozłączanie tag recordera
public class DisconnectMultiplexerAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private ViewerElementManager elementManager;

	protected static final Logger logger = Logger.getLogger(DisconnectMultiplexerAction.class);

	public DisconnectMultiplexerAction(ViewerElementManager elementManager) {
		this.elementManager = elementManager;
		this.putValue(NAME, _("Disconnect"));
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		
		setEnabled(false);

		logger.info("Disconnecting multiplexer...");

		try {
			JmxClient jmxClient = elementManager.getJmxClient();
			if (jmxClient != null)
				jmxClient.shutdown();
			elementManager.setJmxClient(null);
		}
		catch (InterruptedException e) {
			logger.error("shutdown failed! " + e.getMessage());
			e.printStackTrace();
		}

		logger.info("Multiplexer disconnected!");

		firePropertyChange( "disconnected", Boolean.FALSE, Boolean.TRUE);
	}

}

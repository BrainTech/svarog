/* OpenBCISignalSourcePanel.java created 2011-03-06
 *
 */

package org.signalml.app.view.opensignal;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.plugin.export.SignalMLException;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class OpenBCISignalSourcePanel extends AbstractSignalSourcePanel {

	public OpenBCISignalSourcePanel(MessageSourceAccessor messageSource, ViewerElementManager viewerElementManager) {
		super(messageSource, viewerElementManager);
	}

	@Override
	protected JPanel createLeftColumnPanel() {
		JPanel leftColumnPanel = new JPanel();
		leftColumnPanel.setLayout(new BorderLayout());

		return leftColumnPanel;
	}

	@Override
	protected JPanel createRightColumnPanel() {
		//return new JPanel();
		JPanel rightColumnPanel = new JPanel(new BorderLayout());
		rightColumnPanel.add(getTestPanel(), BorderLayout.CENTER);
		return rightColumnPanel;
	}

	protected JPanel getTestPanel() {
		JPanel panel = new JPanel();
		panel.add(new JLabel("open bci signal"));
		return panel;
	}

        @Override
        public void fillPanelFromModel(Object model) throws SignalMLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void fillModelFromPanel(Object model) throws SignalMLException {
                throw new UnsupportedOperationException("Not supported yet.");
        }

	@Override
	public int getNumberOfChannels() {
		return 4;
	}

}

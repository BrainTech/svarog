/* AmplifierSignalSourcePanel.java created 2011-03-06
 *
 */

package org.signalml.app.view.opensignal;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.signalml.app.view.ViewerElementManager;
import org.springframework.context.support.MessageSourceAccessor;

/**
 *
 * @author Piotr Szachewicz
 */
public class AmplifierSignalSourcePanel extends AbstractSignalSourcePanel {

	public AmplifierSignalSourcePanel(MessageSourceAccessor messageSource, ViewerElementManager viewerElementManager) {
		super(messageSource, viewerElementManager);
	}

	@Override
	protected JPanel createLeftColumnPanel() {
		JPanel leftColumnPanel = new JPanel();
		leftColumnPanel.setLayout(new BorderLayout());

		leftColumnPanel.add(getSignalSourceSelectionPanel(), BorderLayout.NORTH);
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
		panel.add(new JLabel("amplifier signal"));
		return panel;
	}


}

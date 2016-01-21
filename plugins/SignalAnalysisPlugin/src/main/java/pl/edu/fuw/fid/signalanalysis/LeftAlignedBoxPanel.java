package pl.edu.fuw.fid.signalanalysis;

import java.awt.Component;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @author ptr@mimuw.edu.pl
 */
public class LeftAlignedBoxPanel extends JPanel {

	public LeftAlignedBoxPanel() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	}

	@Override
	public Component add(Component comp) {
		if (comp instanceof JComponent) {
			((JComponent) comp).setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		return super.add(comp);
	}

}

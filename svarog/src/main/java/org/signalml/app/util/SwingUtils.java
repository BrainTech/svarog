/* ButtonUtils.java created 2007-10-25
 *
 */

package org.signalml.app.util;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.signalml.app.view.common.components.CompactButton;
import org.signalml.app.view.common.dialogs.AbstractDialog;

import org.springframework.core.io.ClassPathResource;

/** ButtonUtils
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class SwingUtils {

	protected static final Logger logger = Logger.getLogger(SwingUtils.class);

	public static void makeButtonsSameSize(JButton[] buttons) {

		if (buttons.length < 2) {
			return; // nothing to do
		}

		Dimension size = buttons[0].getPreferredSize();
		int maxWidth = size.width;
		int maxHeight = size.height;
		int i;

		for (i=1; i<buttons.length; i++) {
			size = buttons[i].getPreferredSize();
			if (size.width > maxWidth) {
				maxWidth = size.width;
			}
			if (size.height > maxHeight) {
				maxHeight = size.height;
			}
		}

		size = new Dimension(maxWidth,maxHeight);

		for (i=0; i<buttons.length; i++) {
			buttons[i].setPreferredSize(size);
			buttons[i].setMinimumSize(size);
			buttons[i].setMaximumSize(size);
		}

	}

	// it seems JSpinner has a bug and doesn't remove it's lesteners from
	// its old models...
	public static void replaceSpinnerModel(JSpinner spinner, SpinnerModel spinnerModel) {
		SpinnerModel oldSpinnerModel = spinner.getModel();
		spinner.setModel(spinnerModel);
		if (oldSpinnerModel instanceof SpinnerNumberModel) {
			SwingUtils.dereferenceSpinnerNumberModel((SpinnerNumberModel) oldSpinnerModel);
		}
	}

	public static void dereferenceSpinnerNumberModel(SpinnerNumberModel model) {
		for (ChangeListener listener : model.getChangeListeners()) {
			model.removeChangeListener(listener);
		}
	}

	public static CompactButton createFieldHelpButton(AbstractDialog owner, URL helpURL) {

		CompactButton button = new CompactButton("", IconUtils.loadClassPathIcon("org/signalml/app/icon/help.png"));
		button.setToolTipText(_("Display context help for this field"));
		ActionListener contextHelpAction = owner.createContextHelpAction(helpURL);

		button.addActionListener(contextHelpAction);

		return button;

	}

	public static CompactButton createFieldHelpButton(AbstractDialog owner, String path) {

		try {

			int index = path.lastIndexOf('#');

			String anchor;
			String mainPath;

			if (index >= 0) {
				mainPath = path.substring(0, index);
				anchor = path.substring(index);
			} else {
				mainPath = path;
				anchor = null;
			}

			URL contextHelpURL = (new ClassPathResource(mainPath)).getURL();
			contextHelpURL = new URL(contextHelpURL.toExternalForm() + anchor);
			return createFieldHelpButton(owner, contextHelpURL);
		} catch (IOException ex) {
			logger.error("Failed to get help URL", ex);
			return createFieldHelpButton(owner, (URL) null);
		}

	}
}

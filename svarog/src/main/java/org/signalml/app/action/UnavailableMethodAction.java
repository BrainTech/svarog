/* UnavailableMethodAction.java created 2008-03-03
 *
 */
package org.signalml.app.action;

import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import org.signalml.app.method.UnavailableMethodDescriptor;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.app.view.common.dialogs.errors.Dialogs;
import org.signalml.plugin.export.view.AbstractSignalMLAction;

/** UnavailableMethodAction
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class UnavailableMethodAction extends AbstractSignalMLAction {

	private static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(UnavailableMethodAction.class);

	private UnavailableMethodDescriptor descriptor;

	public UnavailableMethodAction(UnavailableMethodDescriptor descriptor) {
		this.descriptor = descriptor;
		String name = null;
		String iconPath = null;
		if (descriptor != null) {
			name = descriptor.getName();
			iconPath = descriptor.getIconPath();
		}
		if (name != null && !name.isEmpty()) {
			setText(name);
		} else {
			setText(_("Unavailable method"));
		}
		if (iconPath != null && !iconPath.isEmpty()) {
			setIconPath(iconPath);
		} else {
			setIconPath("org/signalml/app/icon/unavailablemethod.png");
		}
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		logger.debug("Unavailable method");
		Dialogs.showExceptionDialog(descriptor.getException());

	}

}

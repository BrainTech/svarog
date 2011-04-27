/* TagEraserMenuItem.java created 2007-11-21
 *
 */

package org.signalml.app.view.tag;

import javax.swing.Action;
import javax.swing.JMenuItem;

import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.signal.TagStyle;
import org.springframework.context.support.MessageSourceAccessor;

/** TagEraserMenuItem
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagEraserMenuItem extends JMenuItem implements TagStyleSelector {

	private static final long serialVersionUID = 1L;

	public TagEraserMenuItem(Action action, MessageSourceAccessor messageSource) {
		super(action);
		setText(messageSource.getMessage("signalView.eraser"));
		setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/eraser.png"));
	}

	@Override
	public TagStyle getTagStyle() {
		return null;
	}

}

/* TagEraserToggleButton.java created 2007-11-21
 *
 */

package org.signalml.app.view.tag;

import static org.signalml.app.SvarogApplication._;
import java.awt.Dimension;

import javax.swing.JToggleButton;

import org.signalml.app.util.IconUtils;
import org.signalml.plugin.export.signal.TagStyle;

/** TagEraserToggleButton
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagEraserToggleButton extends JToggleButton implements TagStyleSelector {

	private static final long serialVersionUID = 1L;

	private static final Dimension PREFERRED_SIZE = new Dimension(28,28);

	public TagEraserToggleButton() {
		setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/eraser.png"));
		setToolTipText(_("Erase tags"));
	}

	@Override
	public TagStyle getTagStyle() {
		return null;
	}

	@Override
	public Dimension getPreferredSize() {
		return PREFERRED_SIZE;
	}

}

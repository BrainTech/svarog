/* TagEraserToggleButton.java created 2007-11-21
 *
 */

package org.signalml.app.view.tag;

import java.awt.Dimension;

import javax.swing.JToggleButton;

import org.signalml.app.util.IconUtils;
import org.signalml.domain.tag.TagStyle;
import org.springframework.context.support.MessageSourceAccessor;

/** TagEraserToggleButton
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagEraserToggleButton extends JToggleButton implements TagStyleSelector {

	private static final long serialVersionUID = 1L;

	private static final Dimension PREFERRED_SIZE = new Dimension(28,28);

	public TagEraserToggleButton(MessageSourceAccessor messageSource) {
		setIcon(IconUtils.loadClassPathIcon("org/signalml/app/icon/eraser.png"));
		setToolTipText(messageSource.getMessage("signalView.eraserToolTip"));
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

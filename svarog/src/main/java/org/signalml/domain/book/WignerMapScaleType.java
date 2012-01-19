/* WignerMapScaleType.java created 2008-03-03
 *
 */

package org.signalml.domain.book;

import static org.signalml.app.util.i18n.SvarogI18n._;

import org.signalml.app.view.I18nMessage;

/** WignerMapScaleType
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public enum WignerMapScaleType implements I18nMessage {

	NORMAL {
		public String i18n() { return _("Linear"); }
	},
	LOG {
		public String i18n() { return _("Logarithmic"); }
	},
	SQRT {
		public String i18n() { return _("Square root"); }
	},
	;

	public abstract String i18n();
}

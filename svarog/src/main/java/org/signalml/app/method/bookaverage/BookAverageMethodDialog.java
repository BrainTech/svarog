/* BookAverageMethodDialog.java created 2007-10-22
 *
 */

package org.signalml.app.method.bookaverage;

import static org.signalml.app.SvarogI18n._;
import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.signalml.app.util.IconUtils;
import org.signalml.app.view.dialog.AbstractDialog;
import org.signalml.method.bookaverage.BookAverageData;
import org.signalml.plugin.export.SignalMLException;

/** BookAverageMethodDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookAverageMethodDialog extends AbstractDialog  {

	private static final long serialVersionUID = 1L;

	// FIXME do

	public BookAverageMethodDialog(Window window) {
		super(window,true);
	}

	@Override
	protected void initialize() {
		setTitle(_("Configure book averaging"));
		setIconImage(IconUtils.loadClassPathImage(BookAverageMethodDescriptor.ICON_PATH));
		setResizable(false);
		super.initialize();
	}

	@Override
	public JComponent createInterface() {

		JPanel interfacePanel = new JPanel(new BorderLayout(3,3));
		interfacePanel.setBorder(new CompoundBorder(
		                                 new TitledBorder(_("Configure book averaging")),
		                                 new EmptyBorder(3,3,3,3)
		                         ));

		return interfacePanel;

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {

	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {

	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return BookAverageData.class.isAssignableFrom(clazz);
	}

}

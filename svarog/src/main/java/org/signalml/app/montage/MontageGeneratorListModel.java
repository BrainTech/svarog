/* ReferenceGeneratorListModel.java created 2007-10-24
 *
 */

package org.signalml.app.montage;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.signalml.domain.signal.SignalTypeConfigurer;
import org.signalml.util.ResolvableString;

/** ReferenceGeneratorListModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageGeneratorListModel extends AbstractListModel implements ComboBoxModel {

	private static final long serialVersionUID = 1L;

	private static final ResolvableString NO_GENERATOR = new ResolvableString("montageGenerator.none");

	private SignalTypeConfigurer configurer;

	private Object selectedItem;

	public MontageGeneratorListModel() {
	}

	@Override
	public Object getSelectedItem() {
		return selectedItem;
	}

	@Override
	public void setSelectedItem(Object anItem) {
		selectedItem = anItem;
	}

	@Override
	public int getSize() {
		if (configurer == null) {
			return 1;
		}
		return 1 + configurer.getMontageGeneratorCount();
	}

	@Override
	public Object getElementAt(int index) {
		if (index == 0) {
			return NO_GENERATOR;
		}
		return configurer.getMontageGeneratorAt(index-1);
	}

	public SignalTypeConfigurer getConfigurer() {
		return configurer;
	}

	public void setConfigurer(SignalTypeConfigurer configurer) {
		if (this.configurer != configurer) {
			this.configurer = configurer;
			fireContentsChanged(this, 0, getSize()-1);
		}
	}

}

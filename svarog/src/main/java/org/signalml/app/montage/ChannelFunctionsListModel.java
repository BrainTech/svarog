/* ChannelListModel.java created 2007-11-24
 *
 */

package org.signalml.app.montage;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.signalml.domain.montage.IChannelFunction;
import org.signalml.domain.signal.SignalTypeConfigurer;

/** ChannelListModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ChannelFunctionsListModel extends AbstractListModel implements ComboBoxModel {

	private static final long serialVersionUID = 1L;

	private SignalTypeConfigurer configurer;
	private IChannelFunction[] values;

	private Object selectedItem;

	public ChannelFunctionsListModel() {
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
			return 0;
		}
		return values.length;
	}

	@Override
	public Object getElementAt(int index) {
		return values[index];
	}

	public SignalTypeConfigurer getConfigurer() {
		return configurer;
	}

	public void setConfigurer(SignalTypeConfigurer configurer) {
		if (this.configurer != configurer) {
			this.configurer = configurer;
			if (configurer != null) {
				values = configurer.getAvailableFunctions();
			}
			selectedItem = null;
			fireContentsChanged(this, 0, getSize()-1);
		}
	}

}

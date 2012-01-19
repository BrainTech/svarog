/* ReferenceGeneratorListModel.java created 2007-10-24
 *
 */

package org.signalml.app.model.montage;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import org.signalml.domain.montage.generators.IMontageGenerator;
import org.signalml.domain.montage.system.EegSystem;

import org.signalml.util.ResolvableString;

/** ReferenceGeneratorListModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class MontageGeneratorListModel extends AbstractListModel implements ComboBoxModel {

	private static final long serialVersionUID = 1L;

	public static final ResolvableString NO_GENERATOR = new ResolvableString("montageGenerator.none");

	private Object selectedItem = NO_GENERATOR;
	/**
	 * The currently selected {@link EegSystem} for which the list of
	 * {@link IMontageGenerator MontageGenerators} is shown.
	 */
	private EegSystem eegSystem;

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
		if (eegSystem == null) {
			return 1;
		}
		return 1 + eegSystem.getNumberOfMontageGenerators();
	}

	@Override
	public Object getElementAt(int index) {
		if (index == 0) {
			return NO_GENERATOR;
		}
		return eegSystem.getMontageGeneratorAt(index-1);
	}

	/**
	 * Sets the {@link EegSystem} for which the list of available
	 * {@link IMontageGenerator MontageGenerators} will be shown.
	 * @param eegSystem the new {@link EegSystem}
	 */
	public void setEegSystem(EegSystem eegSystem) {
		if (this.eegSystem != eegSystem) {
			this.eegSystem = eegSystem;
			fireContentsChanged(this, 0, getSize()-1);
		}

	}

}

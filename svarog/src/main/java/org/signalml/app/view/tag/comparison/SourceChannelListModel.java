/* SourceChannelListModel.java created 2007-12-04
 *
 */

package org.signalml.app.view.tag.comparison;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.signalml.domain.tag.TagComparisonResults;

/** SourceChannelListModel
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SourceChannelListModel extends AbstractListModel implements ComboBoxModel {

	private static final long serialVersionUID = 1L;

	private TagComparisonResults results;
	private String[] channels;

	private Object selectedItem;

	public SourceChannelListModel() {
		super();
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
		if (results == null) {
			return 0;
		}
		return channels.length;
	}

	@Override
	public Object getElementAt(int index) {
		return channels[index];
	}

	public TagComparisonResults getResults() {
		return results;
	}

	public void setResults(TagComparisonResults results) {
		if (this.results != results) {
			this.results = results;
			if (results != null) {
				channels = results.getChannelNames();
			} else {
				channels = new String[0];
			}
			if (channels.length > 0) {
				if (selectedItem == null) {
					selectedItem = channels[0];
				} else {
					int i;
					for (i=0; i<channels.length; i++) {
						if (selectedItem.equals(channels[i])) {
							selectedItem = channels[i];
							break;
						}
					}
					if (i == channels.length) {
						selectedItem = channels[0];
					}
				}
			} else {
				selectedItem = null;
			}
			fireContentsChanged(this, 0, getSize()-1);
		}
	}

}

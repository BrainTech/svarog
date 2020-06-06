/* BookChannelTreeNode.java created 2008-02-23
 *
 */

package org.signalml.app.model.book;

import java.beans.IntrospectionException;
import java.util.LinkedList;
import java.util.List;
import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.app.model.components.PropertyProvider;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.signalml.domain.book.StandardBook;
import org.springframework.context.MessageSourceResolvable;

/** BookChannelTreeNode
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookChannelTreeNode implements PropertyProvider, MessageSourceResolvable {

	private StandardBook book;

	private int channelIndex;
	private String label;

	public BookChannelTreeNode(StandardBook book, int channelIndex) {
		this.book = book;
		this.channelIndex = channelIndex;
		this.label = book.getChannelLabel(channelIndex);
	}

	public StandardBook getBook() {
		return book;
	}

	public int getChannelIndex() {
		return channelIndex;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		LinkedList<LabelledPropertyDescriptor> list = new LinkedList<>();

		list.add(new LabelledPropertyDescriptor(_("channel index"), "channelIndex", BookChannelTreeNode.class, "getChannelIndex", null));
		list.add(new LabelledPropertyDescriptor(_("label"), "label", BookChannelTreeNode.class, "getLabel", null));

		return list;

	}

	@Override
	public Object[] getArguments() {
		return new Object[] { new Integer(channelIndex), label };
	}

	@Override
	public String[] getCodes() {
		return new String[] { "bookTree.channel" };
	}

	@Override
	public String getDefaultMessage() {
		return "BookChannel ???";
	}

}

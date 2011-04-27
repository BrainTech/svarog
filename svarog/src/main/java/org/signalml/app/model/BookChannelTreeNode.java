/* BookChannelTreeNode.java created 2008-02-23
 *
 */

package org.signalml.app.model;

import java.beans.IntrospectionException;
import java.util.LinkedList;
import java.util.List;

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

		LinkedList<LabelledPropertyDescriptor> list = new LinkedList<LabelledPropertyDescriptor>();

		list.add(new LabelledPropertyDescriptor("property.bookChannel.channelIndex", "channelIndex", BookChannelTreeNode.class, "getChannelIndex", null));
		list.add(new LabelledPropertyDescriptor("property.bookChannel.label", "label", BookChannelTreeNode.class, "getLabel", null));

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

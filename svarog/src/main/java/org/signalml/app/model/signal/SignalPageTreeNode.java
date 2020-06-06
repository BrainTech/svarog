/* SignalPageTreeNode.java created 2007-10-06
 *
 */

package org.signalml.app.model.signal;

import java.beans.IntrospectionException;
import java.util.LinkedList;
import java.util.List;
import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.app.model.components.PropertyProvider;
import static org.signalml.app.util.i18n.SvarogI18n._;
import org.springframework.context.MessageSourceResolvable;

/** SignalPageTreeNode
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalPageTreeNode implements PropertyProvider, MessageSourceResolvable {

	private int page;
	private float size;
	private float startTime;
	private float endTime;

	public SignalPageTreeNode(int page, float size, float startTime, float endTime) {
		this.page = page;
		this.size = size;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public int getPage() {
		return page;
	}

	public float getSize() {
		return size;
	}

	public float getStartTime() {
		return startTime;
	}

	public float getEndTime() {
		return endTime;
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		LinkedList<LabelledPropertyDescriptor> list = new LinkedList<LabelledPropertyDescriptor>();

		list.add(new LabelledPropertyDescriptor(_("page"), "page", SignalPageTreeNode.class, "getPage", null));
		list.add(new LabelledPropertyDescriptor(_("size"), "size", SignalPageTreeNode.class, "getSize", null));
		list.add(new LabelledPropertyDescriptor(_("start time"), "startTime", SignalPageTreeNode.class, "getStartTime", null));
		list.add(new LabelledPropertyDescriptor(_("end time"), "endTime", SignalPageTreeNode.class, "getEndTime", null));

		return list;

	}

	@Override
	public Object[] getArguments() {
		return new Object[] { new Integer(page), new Float(size), new Float(startTime), new Float(endTime) };
	}

	@Override
	public String[] getCodes() {
		return new String[] { "signalTree.page" };
	}

	@Override
	public String getDefaultMessage() {
		return "Page ???";
	}

}

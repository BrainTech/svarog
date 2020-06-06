/* BookSegmentTreeNode.java created 2008-02-23
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
import org.signalml.domain.book.StandardBookSegment;
import org.springframework.context.MessageSourceResolvable;

/** BookSegmentTreeNode
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookSegmentTreeNode implements PropertyProvider, MessageSourceResolvable {

	private StandardBook book;
	private StandardBookSegment segment;

	private int channelIndex;
	private int segmentIndex;
	private float size;
	private float startTime;
	private float endTime;

	private int atomCount;

	public BookSegmentTreeNode(StandardBook book, int channelIndex, int segmentIndex) {
		this.book = book;
		this.channelIndex = channelIndex;
		this.segmentIndex = segmentIndex;
		this.segment = book.getSegmentAt(segmentIndex, channelIndex);
		this.size = segment.getSegmentTimeLength();
		this.startTime = segment.getSegmentTime();
		this.endTime = startTime + size;
		this.atomCount = segment.getAtomCount();
	}

	public StandardBook getBook() {
		return book;
	}

	public StandardBookSegment getSegment() {
		return segment;
	}

	public int getChannelIndex() {
		return channelIndex;
	}

	public int getSegmentIndex() {
		return segmentIndex;
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

	public int getAtomCount() {
		return atomCount;
	}

	public boolean hasSignal() {
		return segment.hasSignal();
	}

	public int getSignalLength() {
		if (segment.hasSignal()) {
			return segment.getSignalSamples().length;
		} else {
			return 0;
		}
	}

	public float getSignalEnergy() {
		return segment.getSignalEnergy();
	}

	public float getDecompositionEnergy() {
		return segment.getDecompositionEnergy();
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		LinkedList<LabelledPropertyDescriptor> list = new LinkedList<>();

		list.add(new LabelledPropertyDescriptor(_("channel index"), "channelIndex", BookSegmentTreeNode.class, "getChannelIndex", null));
		list.add(new LabelledPropertyDescriptor(_("segment index"), "segmentIndex", BookSegmentTreeNode.class, "getSegmentIndex", null));
		list.add(new LabelledPropertyDescriptor(_("size"), "size", BookSegmentTreeNode.class, "getSize", null));
		list.add(new LabelledPropertyDescriptor(_("start time"), "startTime", BookSegmentTreeNode.class, "getStartTime", null));
		list.add(new LabelledPropertyDescriptor(_("end time"), "endTime", BookSegmentTreeNode.class, "getEndTime", null));
		list.add(new LabelledPropertyDescriptor(_("atom count"), "atomCount", BookSegmentTreeNode.class, "getAtomCount", null));
		list.add(new LabelledPropertyDescriptor(_("has signal"), "signal", BookSegmentTreeNode.class, "hasSignal", null));
		list.add(new LabelledPropertyDescriptor(_("signal length"), "signalLength", BookSegmentTreeNode.class, "getSignalLength", null));
		list.add(new LabelledPropertyDescriptor(_("signal energy"), "signalEnergy", BookSegmentTreeNode.class, "getSignalEnergy", null));
		list.add(new LabelledPropertyDescriptor(_("decomposition energy"), "decompositionEnergy", BookSegmentTreeNode.class, "getDecompositionEnergy", null));

		return list;

	}

	@Override
	public Object[] getArguments() {
		return new Object[] { new Integer(segmentIndex), new Float(getSize()), new Float(getStartTime()), new Float(getEndTime()) };
	}

	@Override
	public String[] getCodes() {
		return new String[] { "bookTree.segment" };
	}

	@Override
	public String getDefaultMessage() {
		return "BookSegement ???";
	}

}

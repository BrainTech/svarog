/* BookChannelTreeNode.java created 2008-02-23
 *
 */

package org.signalml.app.model;

import java.beans.IntrospectionException;
import java.util.LinkedList;
import java.util.List;

import org.signalml.domain.book.BookAtomType;
import org.signalml.domain.book.StandardBookAtom;
import org.springframework.context.MessageSourceResolvable;

/** BookChannelTreeNode
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookAtomTreeNode implements PropertyProvider, MessageSourceResolvable {

	private StandardBookAtom atom;
	private int index;

	public BookAtomTreeNode(StandardBookAtom atom, int index) {
		this.atom = atom;
		this.index = index;
	}

	public StandardBookAtom getAtom() {
		return atom;
	}

	public int getIndex() {
		return index;
	}

	public int getIteration() {
		return atom.getIteration();
	}

	public float getAmplitude() {
		return atom.getAmplitude();
	}

	public float getFrequency() {
		return atom.getHzFrequency();
	}

	public float getModulus() {
		return atom.getModulus();
	}

	public float getPhase() {
		return atom.getPhase();
	}

	public float getPosition() {
		return atom.getTimePosition();
	}

	public float getScale() {
		return atom.getTimeScale();
	}

	public BookAtomType getType() {
		return BookAtomType.valueOf(atom.getType());
	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		LinkedList<LabelledPropertyDescriptor> list = new LinkedList<LabelledPropertyDescriptor>();

		list.add(new LabelledPropertyDescriptor("property.bookAtom.iteration", "iteration", BookAtomTreeNode.class, "getIteration", null));
		list.add(new LabelledPropertyDescriptor("property.bookAtom.type", "type", BookAtomTreeNode.class, "getType", null));
		list.add(new LabelledPropertyDescriptor("property.bookAtom.modulus", "modulus", BookAtomTreeNode.class, "getModulus", null));
		list.add(new LabelledPropertyDescriptor("property.bookAtom.amplitude", "amplitude", BookAtomTreeNode.class, "getAmplitude", null));
		list.add(new LabelledPropertyDescriptor("property.bookAtom.position", "position", BookAtomTreeNode.class, "getPosition", null));
		list.add(new LabelledPropertyDescriptor("property.bookAtom.scale", "scale", BookAtomTreeNode.class, "getScale", null));
		list.add(new LabelledPropertyDescriptor("property.bookAtom.frequency", "frequency", BookAtomTreeNode.class, "getFrequency", null));
		list.add(new LabelledPropertyDescriptor("property.bookAtom.phase", "phase", BookAtomTreeNode.class, "getPhase", null));

		return list;

	}

	@Override
	public Object[] getArguments() {
		return new Object[] { new Integer(getIteration()), new Float(atom.getModulus()), new Float(atom.getTimePosition()), new Float(atom.getHzFrequency()) };
	}

	@Override
	public String[] getCodes() {
		return new String[] { "bookTree.atom" };
	}

	@Override
	public String getDefaultMessage() {
		return "BookAtom ???";
	}

}

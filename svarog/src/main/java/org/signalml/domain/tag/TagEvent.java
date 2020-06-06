/* TagEvent.java created 2007-10-01
 *
 */

package org.signalml.domain.tag;

import java.util.EventObject;
import org.signalml.plugin.export.signal.Tag;

/**
 * This class represents an event of adding, removing or changing the
 * {@link Tag tag} in a {@link StyledTagSet StyledTagSet}.
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class TagEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	/**
	 * the {@link Tag tag} that has changed
	 */
	private Tag tag;

	/**
	 * the position in time (seconds) that starts the interval in which
	 * {@link Tag tag} changes took place
	 */
	private double affectedRegionStart;

	/**
	* the position in time (seconds) that ends the interval in which
	* {@link Tag tag} changes took place
	*/
	private double affectedRegionEnd;

	/**
	 * Constructor. Creates an event associated with adding, removing or
	 * changing the {@link Tag tag} in a {@link StyledTagSet set}.
	 * @param source a set with which the event is associated
	 * @param tag the tag that has changed
	 * @param d the position that starts the interval in
	 * which tag changes took place
	 * @param e the position that ends the interval in
	 * which tag changes took place
	 */
	public TagEvent(Object source, Tag tag, double d, double e) {
		super(source);
		this.tag = tag;
		this.affectedRegionStart = d;
		this.affectedRegionEnd = e;
	}

	/**
	 * Returns the {@link Tag tag} associated with the event.
	 * @return the tag associated with the event
	 */
	public Tag getTag() {
		return tag;
	}

	/**
	 * Returns the position in time that starts the interval in which
	 * {@link Tag tag} changes took place.
	 * @return the position in time that starts the interval in which
	 * tag changes took place
	 */
	public double getAffectedRegionStart() {
		return affectedRegionStart;
	}

	/**
	 * Returns the position in time that ends the interval in which
	 * {@link Tag tag} changes took place.
	 * @return the position in time that ends the interval in which
	 * tag changes took place
	 */
	public double getAffectedRegionEnd() {
		return affectedRegionEnd;
	}

}

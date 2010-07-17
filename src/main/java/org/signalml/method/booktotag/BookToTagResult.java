/* BookToTagResult.java created 2008-03-26
 *
 */

package org.signalml.method.booktotag;

import org.signalml.domain.tag.StyledTagSet;

/** BookToTagResult
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class BookToTagResult {

	private StyledTagSet tagSet;

        /**
         * Returns instance of StyledTagSet as result of computation.
         * @return instance of StyledTagSet as result of computation
         */
	public StyledTagSet getTagSet() {
		return tagSet;
	}

        /**
         * Sets instance of StyledTagSet as result of computation.
         * @param tagSet instance of StyledTagSet to be set as result of computation
         */
	public void setTagSet(StyledTagSet tagSet) {
		this.tagSet = tagSet;
	}

}

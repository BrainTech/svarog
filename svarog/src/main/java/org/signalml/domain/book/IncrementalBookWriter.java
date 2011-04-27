/* IncrementalBookWriter.java created 2008-02-16
 *
 */

package org.signalml.domain.book;

import java.io.IOException;

/** IncrementalBookWriter
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface IncrementalBookWriter {

	/** Should append a segment to the end of the open file. The implementing class
	 *  should keep track of the number or segments being added.
	 *
	 * @param segments
	 * @throws IOException
	 */
	void writeSegment(StandardBookSegment[] segments) throws IOException;


	/** XXXYYY Added for test
	 *
	 * @param segment
	 * @throws IOException
	 */
	void writeSegment(StandardBookSegmentWriter segment) throws IOException;


	/** Should update any header information in the file (i.e. write actual number of
	 *  segments written if needed) and then close the file.
	 *
	 * @throws IOException
	 */
	void close() throws IOException;

}

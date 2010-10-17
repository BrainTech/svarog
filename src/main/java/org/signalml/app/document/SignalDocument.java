/* SignalDocument.java created 2007-09-20
 *
 */

package org.signalml.app.document;

import java.util.List;

import org.signalml.domain.montage.Montage;
import org.signalml.domain.signal.OriginalMultichannelSampleSource;
import org.signalml.domain.signal.SignalChecksum;
import org.signalml.domain.signal.SignalType;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.signal.ExportedSignalDocument;

/** SignalDocument
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public interface SignalDocument extends ExportedSignalDocument {

	public static final String PAGE_SIZE_PROPERTY = "pageSize";
	public static final String BLOCKS_PER_PAGE_PROPERTY = "blocksPerPage";
	public static final String MONTAGE_PROPERTY = "montage";
	public static final String ACTIVE_TAG_PROPERTY = "activeTag";
	public static final String TAG_DOCUMENTS_PROPERTY = "tagDocuments";

	SignalType getType();

	String getFormatName();

	float getPageSize();
	void setPageSize(float pageSize);

	int getBlocksPerPage();
	void setBlocksPerPage(int blocksPerPage);

	public float getBlockSize();

	float getSamplingFrequency();
	public int getChannelCount();

	public float getMinSignalLength();
	public float getMaxSignalLength();

	public int getPageCount();
	public int getBlockCount();

	OriginalMultichannelSampleSource getSampleSource();

	List<TagDocument> getTagDocuments();
	void addTagDocument(TagDocument document);
	void removeTagDocument(TagDocument document);

	TagDocument getActiveTag();
	void setActiveTag(TagDocument document);

	SignalChecksum[] getChecksums(String[] types, SignalChecksumProgressMonitor monitor) throws SignalMLException;

	Montage getMontage();
	void setMontage(Montage montage);
	String getMontageInfo();


}

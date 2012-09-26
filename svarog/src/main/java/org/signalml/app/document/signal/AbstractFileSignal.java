/* AbstractReaderDocument.java created 2007-09-20
 *
 */

package org.signalml.app.document.signal;

import static org.signalml.app.util.i18n.SvarogI18n._;

import java.beans.IntrospectionException;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.signalml.app.document.FileBackedDocument;
import org.signalml.app.model.components.LabelledPropertyDescriptor;
import org.signalml.app.worker.signal.SignalChecksumWorker;
import org.signalml.domain.signal.SignalChecksum;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.util.Util;

/**
 * Abstract implementation of {@link FileBackedDocument}.
 * Apart from what can be found in {@link AbstractSignal}, contains
 * the {@link #getBackingFile() backing file} and implements the calculation
 * of the {@link SignalChecksum signal checksums} (using the provided
 * {@link SignalChecksumWorker worker}).
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractFileSignal extends AbstractSignal implements FileBackedDocument {

	/**
	 * the file with which this document is backed
	 */
	protected File backingFile = null;

	/**
	 * {@code HashMap} associating the names of the types of
	 * {@link SignalChecksum checksums} with the calculated checksums
	 * for this signal
	 */
	protected HashMap<String,SignalChecksum> checksums = new HashMap<String,SignalChecksum>();

	/**
	 * the {@link SignalChecksumWorker worker} responsible for calculating
	 * the {@link SignalChecksum checksums}
	 */
	protected volatile SignalChecksumWorker precalculatingWorker;

	/**
	 * Constructor.
	 */
	public AbstractFileSignal() {
		super();
	}

	@Override
	public void closeDocument() throws SignalMLException {
		super.closeDocument();
		if (precalculatingWorker != null) {
			if (!precalculatingWorker.isDone()) {
				precalculatingWorker.cancel(true);
			}
			precalculatingWorker = null;
		}
	}

	@Override
	public File getBackingFile() {
		return backingFile;
	}

	@Override
	public void setBackingFile(File backingFile) {
		this.backingFile = backingFile;
	}

	/**
	 * Returns the {@link SignalChecksumWorker worker} responsible for
	 * calculating the {@link SignalChecksum checksums}.
	 * @return the worker responsible for calculating the checksums
	 */
	public SignalChecksumWorker getPrecalculatingWorker() {
		return precalculatingWorker;
	}

	/**
	 * Sets the {@link SignalChecksumWorker worker} responsible for
	 * calculating the {@link SignalChecksum checksums}.
	 * @param precalculatingWorker the worker responsible for calculating the
	 * checksums
	 */
	public void setPrecalculatingWorker(SignalChecksumWorker precalculatingWorker) {
		this.precalculatingWorker = precalculatingWorker;
	}

	@Override
	public String getName() {
		return (backingFile != null ? backingFile.getName() : "");
	}

	@Override
	public SignalChecksum[] getChecksums(String[] types, SignalChecksumProgressMonitor monitor) throws SignalMLException {

		synchronized (checksums) {
			SignalChecksum[] checksumArr = new SignalChecksum[types.length];
			int[] missingIdx = new int[types.length];
			String[] missing = new String[types.length];
			int missingCnt = 0;
			SignalChecksum checksum;
			int i;
			for (i=0; i<types.length; i++) {
				checksum = checksums.get(types[i]);
				if (checksum == null) {
					missing[missingCnt] = types[i];
					missingIdx[missingCnt] = i;
					missingCnt++;
				} else {
					checksumArr[i] = checksum;
				}
			}
			if (missingCnt == 0) {
				return checksumArr;
			}
			String[] missingTypes = Arrays.copyOf(missing, missingCnt);

			SignalChecksum[] results = Util.getSignalChecksums(backingFile, missingTypes, monitor);
			for (i=0; i<results.length; i++) {
				checksums.put(results[i].getMethod(), results[i]);
				checksumArr[missingIdx[i]] = results[i];
			}

			return checksumArr;
		}

	}

	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {

		List<LabelledPropertyDescriptor> list = super.getPropertyList();

		list.add(new LabelledPropertyDescriptor(_("backing file"), "backingFile", AbstractFileSignal.class));

		return list;

	}

}

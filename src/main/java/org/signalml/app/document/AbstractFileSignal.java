/* AbstractReaderDocument.java created 2007-09-20
 * 
 */

package org.signalml.app.document;

import java.beans.IntrospectionException;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.signalml.app.model.LabelledPropertyDescriptor;
import org.signalml.app.worker.SignalChecksumWorker;
import org.signalml.domain.signal.SignalChecksum;
import org.signalml.domain.signal.SignalType;
import org.signalml.exception.SignalMLException;
import org.signalml.util.Util;

/** AbstractReaderDocument
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public abstract class AbstractFileSignal extends AbstractSignal implements FileBackedDocument {

	protected File backingFile = null;
	protected HashMap<String,SignalChecksum> checksums = new HashMap<String,SignalChecksum>();
	protected volatile SignalChecksumWorker precalculatingWorker;
	
	public AbstractFileSignal(SignalType type) {
		super(type);
	}

	@Override
	public void closeDocument() throws SignalMLException {
		super.closeDocument();
		if( precalculatingWorker != null ) {
			if( !precalculatingWorker.isDone() ) {
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
	
	public SignalChecksumWorker getPrecalculatingWorker() {
		return precalculatingWorker;
	}

	public void setPrecalculatingWorker(SignalChecksumWorker precalculatingWorker) {
		this.precalculatingWorker = precalculatingWorker;
	}

	@Override
	public String getName() {
		return ( backingFile != null ? backingFile.getName() : "" );
	}
	
	@Override
	public SignalChecksum[] getChecksums(String[] types, SignalChecksumProgressMonitor monitor) throws SignalMLException {

		synchronized( checksums ) {
			SignalChecksum[] checksumArr = new SignalChecksum[types.length];
			int[] missingIdx = new int[types.length];
			String[] missing = new String[types.length];
			int missingCnt = 0;
			SignalChecksum checksum;
			int i;
			for( i=0; i<types.length; i++ ) {
				checksum = checksums.get(types[i]);
				if( checksum == null ) {
					missing[missingCnt] = types[i];
					missingIdx[missingCnt] = i;
					missingCnt++;
				} else {
					checksumArr[i] = checksum;
				}
			}
			if( missingCnt == 0 ) {
				return checksumArr;
			}
			String[] missingTypes = Arrays.copyOf(missing, missingCnt);		

			SignalChecksum[] results = Util.getSignalChecksums(backingFile, missingTypes, monitor);
			for( i=0; i<results.length; i++ ) {
				checksums.put(results[i].getMethod(), results[i]);
				checksumArr[missingIdx[i]] = results[i];
			}
			
			return checksumArr;
		}
		
	}
	
	@Override
	public List<LabelledPropertyDescriptor> getPropertyList() throws IntrospectionException {
		
		List<LabelledPropertyDescriptor> list = super.getPropertyList();
		
		list.add( new LabelledPropertyDescriptor("property.document.backingFile", "backingFile", AbstractFileSignal.class) );
				
		return list;
		
	}
			
}

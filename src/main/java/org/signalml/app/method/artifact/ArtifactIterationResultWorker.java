/* ArtifactIterationResultWorker.java created 2008-03-11
 * 
 */

package org.signalml.app.method.artifact;

import java.util.List;

import javax.swing.SwingWorker;

import org.signalml.app.document.TagDocument;
import org.signalml.app.view.dialog.PleaseWaitDialog;
import org.signalml.app.view.signal.SampleSourceUtils;
import org.signalml.domain.roc.RocData;
import org.signalml.domain.roc.RocDataPoint;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.tag.LegacyTagImporter;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.exception.SignalMLException;
import org.signalml.method.artifact.ArtifactData;
import org.signalml.method.artifact.ArtifactResult;
import org.signalml.method.iterator.MethodIteratorData;
import org.signalml.method.iterator.MethodIteratorResult;

/** ArtifactIterationResultWorker
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ArtifactIterationResultWorker extends SwingWorker<RocData,Integer> {

	private LegacyTagImporter legacyTagImporter;
	private MethodIteratorData data;
	private MethodIteratorResult result;
	private TagDocument expertTag;
	
	private PleaseWaitDialog pleaseWaitDialog;
	
	public ArtifactIterationResultWorker(LegacyTagImporter legacyTagImporter, MethodIteratorData data, MethodIteratorResult result, TagDocument expertTag, PleaseWaitDialog pleaseWaitDialog) {
		this.legacyTagImporter = legacyTagImporter;
		this.data = data;
		this.result = result;
		this.expertTag = expertTag;
		this.pleaseWaitDialog = pleaseWaitDialog;
	}

	@Override
	protected RocData doInBackground() throws SignalMLException {

		RocData rocData = RocData.createForMethodIteratorData(data);		
		int size = result.size();

		ArtifactResult iterationResult;
		TagDocument tag = null;
		StyledTagSet tagSet = null;
		ArtifactComparison comparison;
		
		ArtifactData subjectData = (ArtifactData) data.getSubjectMethodData();
		MultichannelSampleSource sampleSource = subjectData.getSampleSource();
		int minSampleCount = SampleSourceUtils.getMinSampleCount( sampleSource );
		float totalLength = ((float) minSampleCount) / sampleSource.getSamplingFrequency();
		
		for( int i=0; i<size; i++ ) {
			
			iterationResult = (ArtifactResult) result.getResultAt(i);
			
			tagSet = legacyTagImporter.importLegacyTags(iterationResult.getTagFile(), sampleSource.getSamplingFrequency());
			tag = new TagDocument( tagSet );

			comparison = new ArtifactComparison( totalLength, tag, expertTag );
			
			RocDataPoint point = new RocDataPoint( result.getParameterValuesAt(i), comparison.getTruePositive(), comparison.getTrueNegative(), comparison.getFalsePositive(), comparison.getFalseNegative()  );
			rocData.add(point);
			
		}
		
		return rocData;
				
	}	
	
	@Override
	protected void done() {
		if( pleaseWaitDialog != null ) {
			synchronized( pleaseWaitDialog ) {
				pleaseWaitDialog.releaseIfOwnedBy(this);
			}
		}
	}
	
	@Override
	protected void process(List<Integer> chunks) {
		if( pleaseWaitDialog != null && !chunks.isEmpty() ) {
			synchronized( pleaseWaitDialog ) {
				pleaseWaitDialog.setProgress( (int) chunks.get(chunks.size()-1) );
			}
		}
	}
			
}

/* ArtifactApplicationData.java created 2007-11-02
 * 
 */

package org.signalml.app.method.artifact;

import java.util.ArrayList;
import java.util.Map;

import org.signalml.app.document.SignalDocument;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalView;
import org.signalml.domain.montage.Channel;
import org.signalml.domain.montage.ChannelType;
import org.signalml.domain.montage.SourceMontage;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.space.ChannelSpaceType;
import org.signalml.domain.signal.space.SegmentedSampleSourceFactory;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.TimeSpaceType;
import org.signalml.exception.SignalMLException;
import org.signalml.method.artifact.ArtifactData;
import org.signalml.method.artifact.ArtifactSignalFormat;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/** ArtifactApplicationData
 *
 * 
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
@XStreamAlias("artifactdata")
public class ArtifactApplicationData extends ArtifactData {

	private static final long serialVersionUID = 1L;

	@XStreamOmitField
	private SignalDocument signalDocument;

	private SourceMontage montage;
	
	@XStreamOmitField
	private boolean existingProject;
	
	public ArtifactApplicationData() {
		super();
	}
	
	public SignalDocument getSignalDocument() {
		return signalDocument;
	}

	public void setSignalDocument(SignalDocument signalDocument) {
		
		this.signalDocument = signalDocument;
		
		montage = new SourceMontage( (SourceMontage) signalDocument.getMontage() );
		
	}
		
	public SourceMontage getMontage() {
		return montage;
	}	

	public void setMontage(SourceMontage montage) {
		this.montage = montage;
	}

	public boolean isExistingProject() {
		return existingProject;
	}

	public void setExistingProject(boolean existingProject) {
		this.existingProject = existingProject;
	}
	
	public void calculate() throws SignalMLException {
				
    	int i;

    	Map<String,Integer> keyChannelMap = getKeyChannelMap();
    	ArrayList<Integer> eegChannels = getEegChannels();    		
    	Map<String,Integer> channelMap = getChannelMap();
    	
    	keyChannelMap.clear();
    	eegChannels.clear();
    	channelMap.clear();
    				
    	int cnt = montage.getSourceChannelCount();
    	Channel function;
    	for( i=0; i<cnt; i++ ) {
    		channelMap.put( montage.getSourceChannelLabelAt(i), i );
    		function = montage.getSourceChannelFunctionAt(i);
    		if( function != null ) {
    			if( function.getType() == ChannelType.PRIMARY ) {
    				eegChannels.add(i);
    			}
    			if( ArtifactData.keyChannelSet.contains(function) ) {
    				keyChannelMap.put( function.getName(), i );
    			}
    		}
    	}
		
		SignalView signalView = (SignalView) signalDocument.getDocumentView();
		SignalPlot plot = signalView.getMasterPlot();
		
		SignalProcessingChain signalChain = plot.getSignalChain();		
		SignalProcessingChain copyChain = signalChain.createRawLevelCopyChain();

		SignalSpace signalSpace = new SignalSpace();
		signalSpace.setChannelSpaceType( ChannelSpaceType.WHOLE_SIGNAL );
		signalSpace.setTimeSpaceType( TimeSpaceType.WHOLE_SIGNAL );
		signalSpace.setWholeSignalCompletePagesOnly(false);
		
		SegmentedSampleSourceFactory factory = SegmentedSampleSourceFactory.getSharedInstance();
		MultichannelSampleSource sampleSource = factory.getContinuousSampleSource(copyChain, signalSpace, null, plot.getPageSize(), plot.getBlockSize());
				
		setSampleSource(sampleSource);
		
		// TODO rethink this cast - what is the data type in matlab code?
		setPageSize((int) signalDocument.getPageSize());
		setBlocksPerPage(signalDocument.getBlocksPerPage());
		setSignalFormat(ArtifactSignalFormat.FLOAT);
    	    			
	}
		
}

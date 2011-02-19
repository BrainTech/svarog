/**
 * 
 */
package org.signalml.plugin.impl;

import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.nio.ByteOrder;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.signalml.app.action.selector.ActionFocusManager;
import org.signalml.app.document.DocumentFlowIntegrator;
import org.signalml.app.document.DocumentManager;
import org.signalml.app.document.ManagedDocumentType;
import org.signalml.app.document.SignalDocument;
import org.signalml.app.document.TagDocument;
import org.signalml.app.model.OpenDocumentDescriptor;
import org.signalml.app.model.OpenSignalDescriptor;
import org.signalml.app.model.OpenSignalDescriptor.OpenSignalMethod;
import org.signalml.app.model.SignalExportDescriptor;
import org.signalml.app.model.SignalParameterDescriptor;
import org.signalml.app.view.ViewerElementManager;
import org.signalml.app.view.dialog.ErrorsDialog;
import org.signalml.app.view.dialog.OptionPane;
import org.signalml.app.view.dialog.PleaseWaitDialog;
import org.signalml.app.view.signal.PositionedTag;
import org.signalml.app.view.signal.SampleSourceUtils;
import org.signalml.app.view.signal.SignalPlot;
import org.signalml.app.view.signal.SignalScanResult;
import org.signalml.app.view.signal.SignalView;
import org.signalml.app.worker.ExportSignalWorker;
import org.signalml.app.worker.ScanSignalWorker;
import org.signalml.codec.SignalMLCodec;
import org.signalml.codec.SignalMLCodecManager;
import org.signalml.codec.XMLSignalMLCodec;
import org.signalml.codec.generator.xml.XMLCodecException;
import org.signalml.domain.signal.MultichannelSampleSource;
import org.signalml.domain.signal.OriginalMultichannelSampleSource;
import org.signalml.domain.signal.SignalProcessingChain;
import org.signalml.domain.signal.raw.RawSignalByteOrder;
import org.signalml.domain.signal.raw.RawSignalDescriptor;
import org.signalml.domain.signal.raw.RawSignalSampleType;
import org.signalml.domain.signal.space.ChannelSpace;
import org.signalml.domain.signal.space.ChannelSpaceType;
import org.signalml.domain.signal.space.SegmentedSampleSourceFactory;
import org.signalml.domain.signal.space.SignalSourceLevel;
import org.signalml.domain.signal.space.SignalSpace;
import org.signalml.domain.signal.space.TimeSpaceType;
import org.signalml.domain.tag.LegacyTagImporter;
import org.signalml.domain.tag.StyledTagSet;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.signal.Document;
import org.signalml.plugin.export.signal.ExportedRawSignalSampleType;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.signal.ExportedSignalSelection;
import org.signalml.plugin.export.signal.ExportedSignalSelectionType;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagDocument;
import org.signalml.plugin.export.signal.SignalSamples;
import org.signalml.plugin.export.signal.SignalSelection;
import org.signalml.plugin.export.signal.SignalSelectionType;
import org.signalml.plugin.export.signal.SvarogAccessSignal;
import org.signalml.plugin.export.signal.Tag;
import org.signalml.plugin.export.signal.TagStyle;
import org.signalml.plugin.export.signal.TemporaryFile;
import org.signalml.plugin.export.view.DocumentView;
import org.signalml.plugin.export.view.ExportedSignalPlot;
import org.signalml.plugin.loader.PluginLoader;

/**
 * Implementation of {@link SvarogAccessSignal} interface.
 * Contains only two fields:
 * <ul>
 * <li>the {@link ViewerElementManager element manager} - allows to access
 * elements of Svarog. It is used to open documents,
 * {@link #addCodec(File, String) add codecs} and return
 * {@link #getProfileDirectory() profile directory}.</li>
 * <li>the {@link ActionFocusManager focus manager} - allows to get active
 * {@link SignalDocument signal} and {@link TagDocument documents}, used in
 * every function where active element is needed.</li>
 * </ul>
 * @author Marcin Szumski
 */
public class SignalsAccessImpl implements SvarogAccessSignal {

	protected static final Logger logger = Logger.getLogger(SignalsAccessImpl.class);
	
	/**
	 * the manager of the elements of Svarog
	 */
	private ViewerElementManager manager;
	/**
	 * informs which objects are focused
	 */
	private ActionFocusManager focusManager;
	
	/**
	 * Returns the output of signal samples.
	 * @return the output of signal samples
	 * @throws NoActiveObjectException if there is no active signal plot
	 */
	private MultichannelSampleSource getOutput() throws NoActiveObjectException{
		SignalPlot plot = getFocusManager().getActiveSignalPlot();
		if (null == plot) throw new NoActiveObjectException("no active signal plot");
		MultichannelSampleSource output = plot.getSignalOutput();
		if (output == null) throw new RuntimeException("output of signal samples is null");
		return output;
	}
	
	/**
	 * Returns samples for the given source of samples and the given channel.
	 * @param source the source of samples
	 * @param channel the number of the channel (from 0 to {@code source.getChannelCount()-1})
	 * @return samples for the given source of samples and the given channel
	 * @throws IndexOutOfBoundsException if the index of a channel is out of range
	 */
	private ChannelSamplesImpl getSamplesFromSource(MultichannelSampleSource source, int channel) throws IndexOutOfBoundsException{
		indexInBounds(source, channel);
		int sz = source.getSampleCount(channel);
		double[] samples = new double[sz];
		source.getSamples(channel, samples, 0, sz, 0);
		ChannelSamplesImpl channelSamples = new ChannelSamplesImpl(samples, channel, source.getSamplingFrequency(), source.getLabel(channel));
		return channelSamples;
	}
	
    /**
     * Returns samples for the given source of samples and the given channel.
     * @param source the source of samples
     * @param channel the number of the channel (from 0 to {@code source.getChannelCount()-1})
     * @param signalOffset the position (in time) in the signal starting
     * from which samples will be returned
     * @param count the number of samples to be returned
     * @return samples for the given source of samples and the given channel
     * @throws IndexOutOfBoundsException if the index of a channel is out of range
     */
    private ChannelSamplesImpl getSamplesFromSource(MultichannelSampleSource source, int channel, int signalOffset, int count) throws IndexOutOfBoundsException{
        indexInBounds(source, channel);
        double[] samples = new double[count];
        source.getSamples(channel, samples, signalOffset, count, 0);
        return new ChannelSamplesImpl(samples, channel, source.getSamplingFrequency(), source.getLabel(channel));
    }
    
	/**
	 * Checks if the index of the channel is in range from 0 to
	 * {@code source.getChannelCount()-1}. Throws exception if not.
	 * @param source the source of samples
	 * @param channel the index of the channel
	 * @throws IndexOutOfBoundsException if the index of a channel is out of range
	 */
	private void indexInBounds(MultichannelSampleSource source, int channel) throws IndexOutOfBoundsException{
		if ((channel < 0) || (channel >= source.getChannelCount()))
			throw new IndexOutOfBoundsException("index "+ channel + "is out of range ["+ "0, " + (source.getChannelCount()-1) + "]");
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getActiveProcessedSignalSamples(int)
	 */
	@Override
	public ChannelSamplesImpl getActiveProcessedSignalSamples(int channel) throws NoActiveObjectException, IndexOutOfBoundsException {
		MultichannelSampleSource output = getOutput();
		ChannelSamplesImpl channelSamples = getSamplesFromSource(output, channel);
		return channelSamples;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getActiveProcessedSignalSamples()
	 */
	@Override
	public SignalSamples getActiveProcessedSignalSamples() throws NoActiveObjectException {
		MultichannelSampleSource output = getOutput();
		int numberOfChannels = output.getChannelCount();
		SignalSamplesImpl signalSamples = new SignalSamplesImpl();
		for (int i = 0; i < numberOfChannels; ++i){
			ChannelSamplesImpl channelSamples = getActiveProcessedSignalSamples(i);
			if (null == channelSamples) throw new RuntimeException();
			signalSamples.addChannelSamples(channelSamples);
		}
		return signalSamples;
	}

	/**
	 * 
	 * @return the list of documents in the signal. may be empty
	 */
	private ArrayList<SignalDocument> getSignalDocuments(){
		DocumentManager documentManager = manager.getDocumentManager();
		int numberOfDocuments = documentManager.getDocumentCount();
		ArrayList<SignalDocument> documents = new ArrayList<SignalDocument>();
		for (int i = 0; i < numberOfDocuments; ++i ){
			Document documentTmp = documentManager.getDocumentAt(i);
			if (documentTmp instanceof SignalDocument){
				documents.add((SignalDocument) documentTmp);				
			}
		}
		return documents;
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getProcessedSignalSamplesForAllSignals()
	 */
	@Override
	public SignalSamples[] getProcessedSignalSamplesForAllSignals() {
		ArrayList<SignalDocument> documents = getSignalDocuments();
		ArrayList<SignalSamplesImpl> signalsSamples = new ArrayList<SignalSamplesImpl>();
		for (SignalDocument signalDocument : documents){
			SignalSamplesImpl samplesTmp;
			try {
				samplesTmp = getProcessedSignalSamplesFromDocument(signalDocument);
				signalsSamples.add(samplesTmp);
			} catch (InvalidClassException e) {
				//never happens
			} 
			
		}
		return signalsSamples.toArray(new SignalSamplesImpl[signalsSamples.size()]);
	}

	/**
	 * Returns the source of unprocessed signal samples for the active signal.
	 * @return the source of unprocessed signal samples for the active signal
	 * @throws NoActiveObjectException if there is no active signal
	 */
	private OriginalMultichannelSampleSource getOriginalSource() throws NoActiveObjectException{
		SignalPlot plot = getFocusManager().getActiveSignalPlot();
		if (null == plot) throw new NoActiveObjectException("no active signal plot");
		OriginalMultichannelSampleSource originalSource = plot.getSignalSource();
		if (originalSource == null) throw new RuntimeException("original source of samples is null");
		return originalSource;
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getActiveRawSignalSamples(int)
	 */
	@Override
	public ChannelSamplesImpl getActiveRawSignalSamples(int channel) throws NoActiveObjectException, IndexOutOfBoundsException {
		MultichannelSampleSource source = getOriginalSource();
		ChannelSamplesImpl channelSamples = getSamplesFromSource(source, channel);
		return channelSamples;
	}

    /* (non-Javadoc)
     * @see org.signalml.plugin.export.PluginAccessSignal#getActiveRawSignalSamples(int)
     */
    @Override
    public ChannelSamplesImpl getActiveRawSignalSamples(int channel, int signalOffset, int count) throws NoActiveObjectException, IndexOutOfBoundsException {
        MultichannelSampleSource source = getOriginalSource();
        ChannelSamplesImpl channelSamples = getSamplesFromSource(source, channel, signalOffset, count);
        return channelSamples;
    }
    
    @Override
    public ChannelSamplesImpl getActiveProcessedSignalSamples(int channel, int signalOffset, int count) throws NoActiveObjectException, IndexOutOfBoundsException {
    	MultichannelSampleSource source = getOutput();
    	ChannelSamplesImpl channelSamples = getSamplesFromSource(source, channel, signalOffset, count);
        return channelSamples;
    }
    
    @Override
    public ChannelSamplesImpl getRawSignalSamplesFromDocument(ExportedSignalDocument document, int channel, int signalOffset, int count) throws InvalidClassException, IndexOutOfBoundsException {
    	MultichannelSampleSource source = getOriginalSourceFromDocument(document);
    	ChannelSamplesImpl channelSamples = getSamplesFromSource(source, channel, signalOffset, count);
        return channelSamples;
    }
    
    @Override
    public ChannelSamplesImpl getProcessedSignalSamplesFromDocument(ExportedSignalDocument document, int channel, int signalOffset, int count) throws InvalidClassException, IndexOutOfBoundsException {
		MultichannelSampleSource source = getOutputFromDocument(document);
		ChannelSamplesImpl channelSamples = getSamplesFromSource(source, channel, signalOffset, count);
        return channelSamples;
    }
    
    /**
     * Returns samples for the given source of samples, the given channel and
     * the specified period of time.
     * @param source the source of samples
     * @param channel the number of the channel (from 0 to {@code
     * source.getChannelCount()-1})
     * @param signalOffsetTime the position (in time in seconds) in the signal
     * starting from which samples will be returned
     * @param length the length of the part of the signal which should be returned
     * @return samples for the given source of samples and the given channel
     * @throws IndexOutOfBoundsException if the index of a channel is out of range
     */
    private ChannelSamplesImpl getSamplesFromSource(MultichannelSampleSource source, int channel, float signalOffsetTime, float length) throws IndexOutOfBoundsException {
    	ChannelSamplesImpl channelSamples = getSamplesFromSource(source, channel, (int) (signalOffsetTime*source.getSamplingFrequency()), (int) (length*source.getSamplingFrequency()));
        return channelSamples;
    }
    
    @Override
    public ChannelSamplesImpl getActiveProcessedSignalSamples(int channel, float signalOffsetTime, float length) throws NoActiveObjectException, IndexOutOfBoundsException {
    	MultichannelSampleSource source = getOutput();
    	ChannelSamplesImpl channelSamples = getSamplesFromSource(source, channel, signalOffsetTime, length);
        return channelSamples;
    }
    
    @Override
    public ChannelSamplesImpl getActiveRawSignalSamples(int channel, float signalOffsetTime, float length) throws NoActiveObjectException, IndexOutOfBoundsException {
    	MultichannelSampleSource source = getOriginalSource();
    	ChannelSamplesImpl channelSamples = getSamplesFromSource(source, channel, signalOffsetTime, length);
        return channelSamples;
    }
    
    @Override
    public ChannelSamplesImpl getRawSignalSamplesFromDocument(ExportedSignalDocument document, int channel, float signalOffsetTime, float length) throws InvalidClassException, IndexOutOfBoundsException {
    	MultichannelSampleSource source = getOriginalSourceFromDocument(document);
    	ChannelSamplesImpl channelSamples = getSamplesFromSource(source, channel, signalOffsetTime, length);
        return channelSamples;
    }
    
    @Override
    public ChannelSamplesImpl getProcessedSignalSamplesFromDocument(ExportedSignalDocument document, int channel, float signalOffsetTime, float length) throws InvalidClassException, IndexOutOfBoundsException {
		MultichannelSampleSource source = getOutputFromDocument(document);
		ChannelSamplesImpl channelSamples = getSamplesFromSource(source, channel, signalOffsetTime, length);
        return channelSamples;
    }
    

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getActiveRawSignalSamples()
	 */
	@Override
	public SignalSamples getActiveRawSignalSamples() throws NoActiveObjectException {
		MultichannelSampleSource source = getOutput();
		int numberOfChannels = source.getChannelCount();
		SignalSamplesImpl signalSamples = new SignalSamplesImpl();
		for (int i = 0; i < numberOfChannels; ++i){
			ChannelSamplesImpl channelSamples = getActiveRawSignalSamples(i);
			if (null == channelSamples) throw new RuntimeException("Channel samples are null");
			signalSamples.addChannelSamples(channelSamples);
		}
		return signalSamples;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getActiveSignalSamplesForAllSignals()
	 */
	@Override
	public SignalSamples[] getRawSignalSamplesForAllSignals() {
		ArrayList<SignalDocument> documents = getSignalDocuments();
		if (documents == null) return null;
		ArrayList<SignalSamplesImpl> signalsSamples = new ArrayList<SignalSamplesImpl>();
		for (SignalDocument signalDocument : documents){
			SignalSamplesImpl samplesTmp;
			try {
				samplesTmp = getRawSignalSamplesFromDocument(signalDocument);
				signalsSamples.add(samplesTmp);
			} catch (InvalidClassException e) {
				//never happens
			}
		}
		return signalsSamples.toArray(new SignalSamplesImpl[signalsSamples.size()]);
	}

	/**
	 * Returns the {@link OriginalMultichannelSampleSource original source}
	 * of samples (source of raw samples) for a given
	 * {@link ExportedSignalDocument signal document}.
	 * @param document the signal document
	 * @return the original source of samples
	 * @throws InvalidClassException if document is not of type {@link SignalDocument}
	 */
	private MultichannelSampleSource getOriginalSourceFromDocument(ExportedSignalDocument document) throws InvalidClassException{
		SignalPlot signalPlot = getSignalPlotFromDocument(document);
		if (signalPlot == null) throw new RuntimeException("signal plot is null");
		MultichannelSampleSource source = signalPlot.getSignalSource();	
		if (source == null) throw new RuntimeException("source of samples is null");
		return source;
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getRawSignalSamplesFromDocument(org.signalml.plugin.export.ExportedSignalDocument, int)
	 */
	@Override
	public ChannelSamplesImpl getRawSignalSamplesFromDocument(ExportedSignalDocument document, int channel) throws InvalidClassException, IndexOutOfBoundsException {
		MultichannelSampleSource source = getOriginalSourceFromDocument(document);
		ChannelSamplesImpl channelSamples = getSamplesFromSource(source, channel);
		return channelSamples;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getRawSignalSamplesFromDocument(org.signalml.plugin.export.ExportedSignalDocument)
	 */
	@Override
	public SignalSamplesImpl getRawSignalSamplesFromDocument(ExportedSignalDocument document) throws InvalidClassException {
		MultichannelSampleSource source = getOriginalSourceFromDocument(document);
		int numberOfChannels = source.getChannelCount();
		SignalSamplesImpl signalSamples = new SignalSamplesImpl();
		for (int i = 0; i < numberOfChannels; ++i){
			try {
				signalSamples.addChannelSamples(getRawSignalSamplesFromDocument(document, i));
			} catch (IndexOutOfBoundsException e){
				throw new RuntimeException("incorrect index of a channel passed");
			}
		}
		return signalSamples;
	}

	
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getTagsFromActiveDocument()
	 */
	@Override
	public Set<ExportedTag> getTagsFromActiveDocument() throws NoActiveObjectException {
		TagDocument tagDocument = getFocusManager().getActiveTagDocument();
		if (tagDocument == null) throw new NoActiveObjectException("no active tag document");
		return new TreeSet<ExportedTag>(tagDocument.getSetOfTags());
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getTagsFromAllDocumentsAssociatedWithAcitiveSignal()
	 */
	@Override
	public List<ExportedTag> getTagsFromAllDocumentsAssociatedWithAcitiveSignal() throws NoActiveObjectException {
		SignalDocument signalDocument = getFocusManager().getActiveSignalDocument();
		if (null == signalDocument) throw new NoActiveObjectException("no active signal document");
		try {
			return getTagsFromSignalDocument(signalDocument);
		} catch (InvalidClassException e) {
			throw new RuntimeException("getActiveSignalDocument() didn't return an object of class SignalDocument");
		}
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getTagsFromAllDocuments()
	 */
	@Override
	public List<ExportedTag> getTagsFromAllDocuments() {
		ArrayList<SignalDocument> signalDocuments = getSignalDocuments();
		List<ExportedTag> tags = new ArrayList<ExportedTag>();
		for (SignalDocument signalDocument : signalDocuments){
			try {
				List<ExportedTag> tagsTmp = getTagsFromSignalDocument(signalDocument);
				tags.addAll(tagsTmp);
			} catch (InvalidClassException e) {
				//never occurs
			}
		}
		return tags;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getActiveTag()
	 */
	@Override
	public ExportedTag getActiveTag() throws NoActiveObjectException {
		SignalDocument signalDocument = getFocusManager().getActiveSignalDocument();
		if (signalDocument ==  null) throw new NoActiveObjectException("no active singal document");
		DocumentView documentView = signalDocument.getDocumentView();
		if (!(documentView instanceof SignalView)) throw new RuntimeException("signal documetn didn't return a valid SignalView");
		SignalView signalView = (SignalView) documentView;
		PositionedTag positionedTag = signalView.getActiveTag();
		if (positionedTag == null) throw new NoActiveObjectException("no active tag");
		return positionedTag.getTag();
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getTagsFromSignalDocument(org.signalml.plugin.export.ExportedSignalDocument)
	 */
	@Override
	public List<ExportedTag> getTagsFromSignalDocument(ExportedSignalDocument document) throws InvalidClassException {
		if (!(document instanceof SignalDocument)) throw new InvalidClassException("document is not of type SignalDocument = was not returned from Svarog");
		SignalDocument signalDocument = (SignalDocument) document;
		List<TagDocument> tagDocuments = signalDocument.getTagDocuments();
		List<ExportedTag> tags = new ArrayList<ExportedTag>();
		for (TagDocument tagDocument : tagDocuments){
			tags.addAll(tagDocument.getSetOfTags());
		}
		return tags;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getActiveSelection()
	 */
	@Override
	public ExportedSignalSelection getActiveSelection() throws NoActiveObjectException {
		SignalPlot signalPlot = getFocusManager().getActiveSignalPlot();
		if (signalPlot == null) throw new NoActiveObjectException("no active signal plot");
		SignalView signalView = signalPlot.getView();
		ExportedSignalSelection selection = signalView.getSignalSelection();
		if (selection == null) throw new NoActiveObjectException("no active signal selection");
		return selection;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#addTagToActiveTagDocument(org.signalml.plugin.export.Tag)
	 */
	@Override
	public void addTagToActiveTagDocument(ExportedTag tag) throws NoActiveObjectException, IllegalArgumentException {
		TagDocument tagDocument = getFocusManager().getActiveTagDocument();
		try {
			addTagToDocument(tagDocument, tag);
		} catch (InvalidClassException e) {
			throw new RuntimeException("FocusManager.getActiveTagDocument() didn't return a valid TagDocument object");
		}
	}

	/**
	 * In the given document finds the {@link TagStyle style} that is equal to
	 * the style of the provided {@link ExportedTag tag}. 
	 * @param tagDocument the document in which we are looking for a style
	 * @param tag the tag for which the actual style is to be found
	 * @return the style that is equal to the style of the provided tag
	 * @throws IllegalArgumentException if there is no such tag style in
	 * the document
	 */
	private TagStyle findStyle(TagDocument tagDocument, ExportedTag tag) throws IllegalArgumentException{
		StyledTagSet tagSet = tagDocument.getTagSet();
		Set<TagStyle> styles = tagSet.getStyles();
		TagStyle style = null;
		for (TagStyle styleTmp : styles){
			if (styleTmp.equals(tag.getStyle())){
				style = styleTmp;
				break;
			}
		}
		if (style == null) throw new IllegalArgumentException("tag style is not in the document");
		return style;
	}
	
	/**
	 * From the given {@link ExportedTag exported tag} creates a collection of
	 * {@link Tag tags} by splitting it to separate blocks/pages (only if
	 * exported tag has a {@link ExportedSignalSelectionType type}
	 * {@code BLOCK} or {@code PAGE}).
	 * If the exported tag has a type {@code CHANNEL} the collection contains
	 * only one tag created from it
	 * @param tag the tag to be converted
	 * @param tagDocument the document to which the tag will be added;
	 * used to get styles and lengths of blocks/pages.
	 * @return the collection of tags
	 */
	private Collection<Tag> splitTag(ExportedTag tag, TagDocument tagDocument){
		float blockOrPageLength = 0;
		Collection<Tag> tags = new ArrayList<Tag>();
		TagStyle style = findStyle(tagDocument, tag);
		if (tag.getType().getName().equals(ExportedSignalSelectionType.BLOCK)){
			blockOrPageLength = tagDocument.getBlockSize();
		} else if (tag.getType().getName().equals(ExportedSignalSelectionType.PAGE)){
			blockOrPageLength = tagDocument.getPageSize();
		} else {
			Tag trueTag = new Tag(style, tag.getPosition(), tag.getLength(), tag.getChannel(), tag.getAnnotation());
			tags.add(trueTag);
			return tags;
		}
		int startBlockOrPage = tag.getStartSegment(blockOrPageLength);
		int endBlockOrPage = tag.getEndSegment(blockOrPageLength);
		for (int i=startBlockOrPage; i<endBlockOrPage; i++) {
			Tag trueTag = new Tag(style, i*blockOrPageLength, blockOrPageLength, SignalSelection.CHANNEL_NULL, tag.getAnnotation());
			tags.add(trueTag);
		}
		return tags;
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#addTagToDocument(org.signalml.plugin.export.ExportedTagDocument, org.signalml.plugin.export.Tag)
	 */
	@Override
	public void addTagToDocument(ExportedTagDocument document, ExportedTag tag) throws InvalidClassException, IllegalArgumentException {
		if (!(document instanceof TagDocument)) throw new InvalidClassException("document is not of type TagDocument => was not returned from Svarog");
		TagDocument tagDocument = (TagDocument) document;
		StyledTagSet tagSet = tagDocument.getTagSet();
		TagStyle style = findStyle(tagDocument, tag);
		ExportedSignalSelectionType type = tag.getType();
		if (!(style.getType().getName().equals(type.getName()))) throw new IllegalArgumentException("tag style is not in the document");
		Collection<Tag> tags = splitTag(tag, tagDocument);
		for (Tag trueTag : tags)
			tagSet.replaceSameTypeTags(trueTag);
		tagDocument.setSaved(false);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getTagDocumentsFromActiveSignal()
	 */
	@Override
	public ExportedTagDocument[] getTagDocumentsFromActiveSignal() throws NoActiveObjectException {
		SignalDocument signalDocument = getFocusManager().getActiveSignalDocument();
		if (null == signalDocument) throw new NoActiveObjectException("no active signal document");
		List<TagDocument> tagDocuments = signalDocument.getTagDocuments();
		return tagDocuments.toArray(new TagDocument[tagDocuments.size()]);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getActiveTagDocument()
	 */
	@Override
	public ExportedTagDocument getActiveTagDocument() throws NoActiveObjectException {
		ExportedTagDocument exportedTagDocument = getFocusManager().getActiveTagDocument();
		if (exportedTagDocument == null) throw new NoActiveObjectException("no active tag document");
		return exportedTagDocument;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getActiveDocument()
	 */
	@Override
	public Document getActiveDocument() throws NoActiveObjectException {
		Document document = getFocusManager().getActiveDocument();
		if (document == null) throw new NoActiveObjectException("no active document");
		return document;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#openSignal(java.lang.String)
	 */
	@Override
	public void openRawSignal(File file, float samplingFrequency, int channelCount, ExportedRawSignalSampleType sampleType, ByteOrder byteOrder, float calibration, float pageSize, int blocksPerPage)
	throws SignalMLException, IOException {
		OpenDocumentDescriptor ofd = new OpenDocumentDescriptor();
		ofd.setMakeActive(true);
		if (file == null) throw new NullPointerException("file can not be null");
		if (!file.exists()) throw new IOException("file doesn't exist");
		if (!file.canRead()) throw new IOException("can not access file");
		ofd.setFile(file);
		ofd.setType(ManagedDocumentType.SIGNAL);
		OpenSignalDescriptor osd = ofd.getSignalOptions();
		osd.setMethod(OpenSignalMethod.RAW);
		RawSignalDescriptor descriptor = new RawSignalDescriptor();
		descriptor.setSamplingFrequency(samplingFrequency);
		descriptor.setChannelCount(channelCount);
		descriptor.setSampleType(getSampleType(sampleType));
		descriptor.setByteOrder(getByteOrder(byteOrder));
		descriptor.setCalibration(calibration);
		descriptor.setPageSize(pageSize);
		descriptor.setBlocksPerPage(blocksPerPage);
		osd.setRawSignalDescriptor(descriptor);
		DocumentFlowIntegrator documentFlowIntegrator = manager.getDocumentFlowIntegrator();
		if (!documentFlowIntegrator.maybeOpenDocument(ofd))
			throw new SignalMLException("failed to open document");
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#openTagDocument(java.lang.String, org.signalml.plugin.export.ExportedSignalDocument)
	 */
	@Override
	public void openTagDocument(File file, ExportedSignalDocument document)
	throws SignalMLException, IOException {
		if (file == null) throw new NullPointerException("file can not be null");
		if (!(document instanceof SignalDocument)) throw new InvalidClassException("document is not of type SignalDocument = was not returned from Svarog");
		SignalDocument signalDocument = (SignalDocument) document;
		if (!file.exists()) throw new IOException("file doesn't exist");
		if (!file.canRead()) throw new IOException("can not access file");
		OpenDocumentDescriptor ofd = new OpenDocumentDescriptor();
		ofd.setType(ManagedDocumentType.TAG);
		ofd.setMakeActive(true);
		
		boolean legTag = true;
		LegacyTagImporter importer = new LegacyTagImporter();
		StyledTagSet tagSet = null;
		try {
			tagSet = importer.importLegacyTags(file, signalDocument.getSamplingFrequency());
		} catch (SignalMLException ex) {
			legTag = false;
		}

		TagDocument tagDocument = null;
		try {
			tagDocument = new TagDocument(tagSet);
		} catch (SignalMLException ex) {
			legTag = false;
		}

		if (legTag) {
			ofd.getTagOptions().setExistingDocument(tagDocument);
		} else {
			ofd.setFile(file);
		}

		ofd.getTagOptions().setParent(signalDocument);

		DocumentFlowIntegrator documentFlowIntegrator = manager.getDocumentFlowIntegrator();
		//if (documentFlowIntegrator == null) return false;
		if (!documentFlowIntegrator.maybeOpenDocument(ofd))
			throw new SignalMLException("failed to open document");
	}


	/**
	 * @param manager the manager to set
	 */
	public void setManager(ViewerElementManager manager) {
		this.manager = manager;
	}
	
	/**
	 * Returns a {@link SignalPlot signal plot} for a {@link SignalView view}
	 * associated with a given document. 
	 * @param document the document with the signal. Must be of type SignalDocument
	 * @return a plot for a view associated with a given document
	 * @throws InvalidClassException if document is not of type SignalDocument
	 */
	private SignalPlot getSignalPlotFromDocument(ExportedSignalDocument document) throws InvalidClassException{
		if (!(document instanceof SignalDocument)) throw new InvalidClassException("document is not of type SignalDocument => was not returned from Svarog");
		SignalDocument signalDocument = (SignalDocument) document;
		DocumentView documentView = signalDocument.getDocumentView();
		if (!(documentView instanceof SignalView)) throw new RuntimeException("view is not of type SignalView");
		SignalView signalView = (SignalView) documentView;
		SignalPlot signalPlot = signalView.getMasterPlot();
		if (signalPlot == null) throw new RuntimeException("no master plot for signal view");
		return signalPlot;
	}
	
	/**
	 * Returns the output of signal samples associated with a given document.
	 * @param document the document with the signal. Must be of type SignalDocument
	 * @return the output of signal samples associated with a given document
	 * @throws InvalidClassException if document is not of type SignalDocument
	 */
	private MultichannelSampleSource getOutputFromDocument(ExportedSignalDocument document) throws InvalidClassException{
		SignalPlot signalPlot = getSignalPlotFromDocument(document);
		MultichannelSampleSource output = signalPlot.getSignalOutput();	
		if (output == null) throw new RuntimeException("no output of signal samples for a signal plot");
		return output;
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getProcessedSignalSamplesFromDocument(org.signalml.plugin.export.ExportedSignalDocument, int)
	 */
	@Override
	public ChannelSamplesImpl getProcessedSignalSamplesFromDocument(ExportedSignalDocument document, int channel) throws InvalidClassException, IndexOutOfBoundsException {
		MultichannelSampleSource output = getOutputFromDocument(document);
		ChannelSamplesImpl channelSamples = getSamplesFromSource(output, channel);
		return channelSamples;
	}
	
	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.PluginAccessSignal#getProcessedSignalSamplesFromDocument(org.signalml.plugin.export.ExportedSignalDocument)
	 */
	@Override
	public SignalSamplesImpl getProcessedSignalSamplesFromDocument(ExportedSignalDocument document) throws InvalidClassException {
		MultichannelSampleSource output = getOutputFromDocument(document);
		int numberOfChannels = output.getChannelCount();
		SignalSamplesImpl signalSamples = new SignalSamplesImpl();
		for (int i = 0; i < numberOfChannels; ++i){
			signalSamples.addChannelSamples(getProcessedSignalSamplesFromDocument(document, i));
		}
		return signalSamples;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.signal.PluginAccessSignal#openSignalWithCodec(java.io.File, java.lang.String)
	 */
	@Override
	public void openSignalWithCodec(File file, String codecFormatName, float pageSize, int blocksPerPage)
	throws IOException, IllegalArgumentException, SignalMLException {
		OpenDocumentDescriptor ofd = new OpenDocumentDescriptor();
		ofd.setMakeActive(true);
		ofd.setFile(file);
		ofd.setType(ManagedDocumentType.SIGNAL);
		OpenSignalDescriptor osd = ofd.getSignalOptions();
		osd.setMethod(OpenSignalMethod.USE_SIGNALML);
		SignalParameterDescriptor spd = osd.getParameters();
		SignalMLCodecManager codecManager = manager.getCodecManager();
		if (file == null) throw new NullPointerException("file can not be null");
		if (!file.exists()) throw new IOException("file doesn't exist");
		if (!file.canRead()) throw new IOException("can not access file");
		SignalMLCodec codec = codecManager.getCodecForFormat(codecFormatName);
		if (codec == null) throw new IllegalArgumentException("codec of this name doesn't exist");
		osd.setCodec(codec);
		if (spd.isBlocksPerPageEditable()){
			spd.setBlocksPerPage(blocksPerPage);
		}
		if (spd.isPageSizeEditable()){
			spd.setPageSize(pageSize);
		}
		DocumentFlowIntegrator documentFlowIntegrator = manager.getDocumentFlowIntegrator();
		if (!documentFlowIntegrator.maybeOpenDocument(ofd))
			throw new SignalMLException("failed to open document");
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.signal.PluginAccessSignal#addCodec(java.io.File, java.lang.String)
	 */
	@Override
	public void addCodec(File codecFile, String codecFormatName) throws IOException {
		SignalMLCodecManager codecManager = manager.getCodecManager();
		if (codecFile == null) throw new NullPointerException("file can not be null");
		if (!codecFile.exists()) throw new IOException("file doesn't exist");
		if (!codecFile.canRead()) throw new IOException("can not access file");
		SignalMLCodec codec;
		try {
			codec = new XMLSignalMLCodec(codecFile, getProfileDirectory());
		} catch (XMLCodecException e) {
			throw new IOException("failed to read codec");
		}
		codec.setFormatName(codecFormatName);
		codecManager.registerSignalMLCodec(codec);
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.signal.PluginAccessSignal#openBook(java.io.File)
	 */
	@Override
	public void openBook(File file) throws IOException, SignalMLException {
		OpenDocumentDescriptor ofd = new OpenDocumentDescriptor();
		if (file == null) throw new NullPointerException("file can not be null");
		if (!file.exists()) throw new IOException("file doesn't exist");
		if (!file.canRead()) throw new IOException("can not access file");
		ofd.setMakeActive(true);
		ofd.setFile(file);
		ofd.setType(ManagedDocumentType.BOOK);
		DocumentFlowIntegrator documentFlowIntegrator = manager.getDocumentFlowIntegrator();
		if (!documentFlowIntegrator.maybeOpenDocument(ofd))
			throw new SignalMLException("failed to open book document");
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.signal.PluginAccessSignal#getProfileDirectory()
	 */
	@Override
	public File getProfileDirectory() {
		return new File(manager.getProfileDir().getAbsolutePath());
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.signal.PluginAccessSignal#getPluginDirectory()
	 */
	@Override
	public File[] getPluginDirectories() {
		PluginLoader loader = PluginLoader.getInstance();
		ArrayList<File> files = loader.getPluginDirs();
		if (files == null) throw new RuntimeException("no profile directories stored");
		File[] filesArray = new File[files.size()];
		int i = 0;
		for (File file : files){
			filesArray[i++] = new File(file.getAbsolutePath());
		}
		return filesArray;
	}

	/**
	 * @return the focusManager
	 */
	private ActionFocusManager getFocusManager() {
		if (focusManager == null) focusManager = manager.getActionFocusManager();
		return focusManager;
	}

	/* (non-Javadoc)
	 * @see org.signalml.plugin.export.signal.SvarogAccessSignal#getActiveSignalDocument()
	 */
	@Override
	public ExportedSignalDocument getActiveSignalDocument()
			throws NoActiveObjectException {
		Document document = getActiveDocument();
		if (!(document instanceof ExportedSignalDocument)) throw new NoActiveObjectException("no active signal document");
		return (ExportedSignalDocument) document;
	}

	/**
	 * Obtains {@link RawSignalByteOrder} for a given {@link ByteOrder}
	 * @param order the ByteOrder
	 * @return the RawSignalByteOrder
	 * @throws InvalidClassException if there is no such RawSignalByteOrder
	 */
	private RawSignalByteOrder getByteOrder(ByteOrder order) throws InvalidClassException {
		RawSignalByteOrder[] rawByteOrders = RawSignalByteOrder.values();
		for (RawSignalByteOrder rawByteOrder : rawByteOrders) {
			if (rawByteOrder.getByteOrder().equals(order)) return rawByteOrder;
		}
		throw new InvalidClassException("No such byte order");
	}
	
	/**
	 * Obtains {@link RawSignalSampleType} for a given
	 * {@link ExportedRawSignalSampleType}.
	 * @param sampleType the {@link ExportedRawSignalSampleType}
	 * @return the RawSignalSampleType
	 * @throws InvalidParameterException if there is no type
	 */
	private RawSignalSampleType getSampleType(ExportedRawSignalSampleType sampleType) {
		switch (sampleType) {
		case DOUBLE:
			return RawSignalSampleType.DOUBLE;
		case FLOAT:
			return RawSignalSampleType.FLOAT;
		case INT:
			return RawSignalSampleType.INT;
		case SHORT:
			return RawSignalSampleType.SHORT;
		default:
			throw new InvalidParameterException("such type doesn't exist");
		}
	}
	
	@Override
	public void exportSignal(float position, float length, int[] channels, SignalSourceLevel level,
			ExportedRawSignalSampleType sampleType, ByteOrder byteOrder, ExportedSignalPlot plot, File file) throws InvalidClassException, SignalMLException {
		if (!(plot instanceof SignalPlot)) 
			throw new InvalidClassException("document is not of type SignalPlot => was not returned from Svarog");
		SignalPlot masterPlot = (SignalPlot) plot;
		
		RawSignalByteOrder rawByteOrder = getByteOrder(byteOrder);
		
		SignalExportDescriptor descriptor = new SignalExportDescriptor();
		SignalSpace signalSpace = new SignalSpace();
		descriptor.setSignalSpace(signalSpace);
		
		ChannelSpace channelSpace = new ChannelSpace(channels);
		signalSpace.setChannelSpace(channelSpace);
		SignalSelection selection = new SignalSelection(SignalSelectionType.CHANNEL, position, length);
		signalSpace.setSelectionTimeSpace(selection);
		signalSpace.setChannelSpaceType(ChannelSpaceType.SELECTED);
		signalSpace.setSignalSourceLevel(level);
		signalSpace.setTimeSpaceType(TimeSpaceType.SELECTION_BASED);
		
		descriptor.setByteOrder(rawByteOrder);
		descriptor.setBlockSize(masterPlot.getBlockSize());
		descriptor.setPageSize(masterPlot.getPageSize());
		
		RawSignalSampleType rawSignalSampleType = getSampleType(sampleType);
		descriptor.setSampleType(rawSignalSampleType);
		descriptor.setNormalize(false);
		
		
		SignalProcessingChain signalChain;
		try {
			signalChain = masterPlot.getSignalChain().createLevelCopyChain(signalSpace.getSignalSourceLevel());
		} catch (SignalMLException ex) {
			logger.error("Failed to create subchain", ex);
			ErrorsDialog.showImmediateExceptionDialog((Window) null, ex);
			return;
		}
		
		SegmentedSampleSourceFactory factory = SegmentedSampleSourceFactory.getSharedInstance();
		MultichannelSampleSource sampleSource = factory.getContinuousSampleSource(signalChain, signalSpace, descriptor.getTagSet(), descriptor.getPageSize(), descriptor.getBlockSize());
		
		PleaseWaitDialog pleaseWaitDialog = new PleaseWaitDialog(manager.getMessageSource(), manager.getDialogParent());
		pleaseWaitDialog.initializeNow();
		
		if (rawSignalSampleType == RawSignalSampleType.INT || rawSignalSampleType == RawSignalSampleType.SHORT) {
			// normalization - check signal half-amplitude maximum
			ScanSignalWorker scanWorker = new ScanSignalWorker(sampleSource, pleaseWaitDialog);

			scanWorker.execute();

			pleaseWaitDialog.setActivity(manager.getMessageSource().getMessage("activity.scanningSignal"));
			pleaseWaitDialog.configureForDeterminate(0, SampleSourceUtils.getMaxSampleCount(sampleSource), 0);
			pleaseWaitDialog.waitAndShowDialogIn(manager.getDialogParent(), 500, scanWorker);

			SignalScanResult signalScanResult = null;
			try {
				signalScanResult = scanWorker.get();
			} catch (InterruptedException ex) {
				// ignore
			} catch (ExecutionException ex) {
				logger.error("Worker failed to save", ex.getCause());
				ErrorsDialog.showImmediateExceptionDialog((Window) null, ex.getCause());
				return;
			}

			double maxSignalAbsValue = Math.max(Math.abs(signalScanResult.getMaxSignalValue()), Math.abs(signalScanResult.getMinSignalValue()));
			double maxTypeAbsValue = 0;

			if (rawSignalSampleType == RawSignalSampleType.INT) {
				maxTypeAbsValue = Math.min((Integer.MAX_VALUE-1), -(Integer.MIN_VALUE+1));
			} else {
				maxTypeAbsValue = Math.min((Short.MAX_VALUE-1), -(Short.MIN_VALUE+1));
			}

			boolean normalize = descriptor.isNormalize();
			if (!normalize) {

				// check if normalization needs to be forced
				if (maxTypeAbsValue < Math.ceil(maxSignalAbsValue)) {

					int ans = OptionPane.showNormalizationUnavoidable(manager.getOptionPaneParent());
					if (ans != OptionPane.OK_OPTION) {
						return;
					}

					normalize = true;
					descriptor.setNormalize(normalize);

				}

			}

			if (normalize) {

				descriptor.setNormalizationFactor(maxTypeAbsValue / maxSignalAbsValue);

			}

		}

		int minSampleCount = SampleSourceUtils.getMinSampleCount(sampleSource);

		ExportSignalWorker worker = new ExportSignalWorker(sampleSource, file, descriptor, pleaseWaitDialog);

		worker.execute();

		pleaseWaitDialog.setActivity(manager.getMessageSource().getMessage("activity.exportingSignal"));
		pleaseWaitDialog.configureForDeterminate(0, minSampleCount, 0);
		pleaseWaitDialog.waitAndShowDialogIn(manager.getOptionPaneParent(), 500, worker);

		try {
			worker.get();
		} catch (InterruptedException ex) {
			// ignore
		} catch (ExecutionException ex) {
			logger.error("Worker failed to save", ex.getCause());
			throw new SignalMLException("failed to export signal", ex);
		}

		
	}
	
	@Override
	public File getTemporaryFile(String extension) throws IOException {
		File profileDirectory = manager.getProfileDir().getAbsoluteFile();
		File tempDirectory = new File(profileDirectory, "temp");
		if (tempDirectory.exists() && !tempDirectory.isDirectory())
			throw new IOException("can not create the directory for temporary files");
		if (!tempDirectory.exists())
			tempDirectory.mkdir();
		File tempFile = File.createTempFile("temp", extension, tempDirectory);
		TemporaryFile temporaryFile = new TemporaryFile(tempFile.getAbsolutePath());
		temporaryFile.deleteOnExit();
		return temporaryFile;
	}

}

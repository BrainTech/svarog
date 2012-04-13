/**
 *
 */
package org.signalml.plugin.export.signal;

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Set;

import org.signalml.app.document.TagDocument;
import org.signalml.codec.SignalMLCodec;
import org.signalml.domain.signal.space.SignalSourceLevel;
import org.signalml.plugin.export.NoActiveObjectException;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.ExportedSignalPlot;

/**
 * This interface allows to access a part of Svarog logic, namely:
 * <ul>
 * <li>return signal samples:
 * <ul>
 * <li>either for the active signal or for the signal from the given document,</li>
 * <li>either from a {@link ChannelSamples single channel} or
 * from {@link SignalSamples all channels} in the signal,</li>
 * <li>either processed (after the montage and filtering) or raw (unprocessed).</li>
 * </ul></li>
 * <li>return {@link Tag tags}:
 * <ul>
 * <li>from active or specified tag document</li>
 * <li>from all tag documents dependent from the active or selected signal,</li>
 * <li>from all tag documents</li>
 * <li>the active tag</li>
 * </ul></li>
 * <li>return active {@link ExportedSignalDocument signal} and
 * {@link ExportedTagDocument tag} documents or just active {@link Document}</li>
 * <li>open:
 * <ul>
 * <li>a book file and add it as a tab</li>
 * <li>a signal file and add it as a tab:
 * <ul><li>using codec</li>
 * <li>as raw signal</li></ul>
 * <li>a file with {@link SignalMLCodec codec} and register a new codec,</li>
 * <li>a file with tags and add it to the active or specified signal.</li>
 * </ul></li>
 * </ul>
 *
 * @author Marcin Szumski
 */
public interface SvarogAccessSignal {

	/**
	 * Returns the processed signal samples (output of the SignalProcessingChain)
	 * for the active signal and the given channel.
	 * @param channel the index of the channel
	 * (range from 0 to {@code getActiveSignalDocument().getChannelCount()-1})
	 * @return the signal samples
	 * @throws NoActiveObjectException if there is no active signal
	 * @throws IndexOutOfBoundsException if the index of a channel is out of range
	 */
	ChannelSamples getActiveProcessedSignalSamples(int channel) throws NoActiveObjectException, IndexOutOfBoundsException;

	/**
	 * Returns the processed signal samples (output of the SignalProcessingChain)
	 * for all channels of the active signal.
	 * @return the signal samples
	 * @throws NoActiveObjectException if there is no active signal
	 */
	SignalSamples getActiveProcessedSignalSamples() throws NoActiveObjectException;

	/**
	 * Returns the processed signal samples (output of the SignalProcessingChain)
	 * for all channels of all signals.
	 * @return the signal samples
	 */
	SignalSamples[] getProcessedSignalSamplesForAllSignals();

	/**
	 * Returns the unprocessed signal samples (raw from the source)
	 * for the active signal and the given channel.
	 * @param channel the index of the channel
	 * (range from 0 to {@code getActiveSignalDocument().getChannelCount()-1})
	 * @return the signal samples
	 * @throws NoActiveObjectException if there is no active signal
	 * @throws IndexOutOfBoundsException if the index of a channel is out of range
	 */
	ChannelSamples getActiveRawSignalSamples(int channel) throws NoActiveObjectException, IndexOutOfBoundsException;

	/**
	 * Returns the unprocessed signal samples (raw from the source)
	 * for the active signal and the given channel.
	 * @param channel the index of the channel
	 * (range from 0 to {@code getActiveSignalDocument().getChannelCount()-1})
	 * @param signalOffset the number of the sample in the signal starting
	 * from which samples will be returned
	 * @param count the number of samples to be returned
	 * @return the signal samples
	 * @throws NoActiveObjectException if there is no active signal
	 * @throws IndexOutOfBoundsException if the index of a channel is out of range
	 */
	ChannelSamples getActiveRawSignalSamples(int channel, int signalOffset, int count) throws NoActiveObjectException, IndexOutOfBoundsException;

	/**
	 * Returns the unprocessed signal samples (raw from the source)
	 * for all channels of the active signal.
	 * @return the signal samples
	 * @throws NoActiveObjectException if there is no active signal
	 */
	SignalSamples getActiveRawSignalSamples() throws NoActiveObjectException;

	/**
	 * Returns the unprocessed signal samples (raw from the source)
	 * for all channels of all signals.
	 * @return the signal samples
	 */
	SignalSamples[] getRawSignalSamplesForAllSignals();


	/**
	 * Returns the unprocessed signal samples (raw from the source)
	 * for the signal from the given document and the given channel.
	 * @param document the document with the signal. Must be returned from this
	 * SvarogAcces (actually be of type SignalDocument - internal to Svarog).
	 * @param channel the index of the channel
	 * (range from 0 to {@code document.getChannelCount()-1})
	 * @return the signal samples
	 * @throws InvalidClassException if document is not returned from
	 * this SvarogAccess (not of type SignalDocument - internal to Svarog)
	 * @throws IndexOutOfBoundsException if the index of a channel is out of range
	 */
	ChannelSamples getRawSignalSamplesFromDocument(ExportedSignalDocument document, int channel) throws InvalidClassException, IndexOutOfBoundsException;

	/**
	 * Returns the unprocessed signal samples (raw from the source)
	 * for all channels of the signal from the given document.
	 * @param document the document with the signal. Must be returned from this
	 * SvarogAcces (actually be of type SignalDocument - internal to Svarog).
	 * @return the signal samples
	 * @throws InvalidClassException if document is not returned from
	 * this SvarogAccess (not of type SignalDocument - internal to Svarog)
	 */
	SignalSamples getRawSignalSamplesFromDocument(ExportedSignalDocument document) throws InvalidClassException;

	/**
	 * Returns the processed signal samples
	 * for the signal from the given document and the given channel.
	 * @param document the document with the signal. Must be returned from this
	 * SvarogAcces (actually be of type SignalDocument - internal to Svarog).
	 * @param channel the index of the channel
	 * (range from 0 to {@code document.getChannelCount()-1})
	 * @return the signal samples
	 * @throws InvalidClassException if document is not returned from
	 * this SvarogAccess (not of type SignalDocument - internal to Svarog)
	 * @throws IndexOutOfBoundsException if the index of a channel is out of range
	 */
	ChannelSamples getProcessedSignalSamplesFromDocument(ExportedSignalDocument document, int channel) throws InvalidClassException, IndexOutOfBoundsException;

	/**
	 * Returns the processed signal samples
	 * for all channels of the signal from the given document.
	 * @param document the document with the signal. Must be returned from this
	 * SvarogAcces (actually be of type SignalDocument - internal to Svarog).
	 * @return the signal samples
	 * @throws InvalidClassException if document is not returned from
	 * this SvarogAccess (not of type SignalDocument - internal to Svarog)
	 */
	SignalSamples getProcessedSignalSamplesFromDocument(ExportedSignalDocument document) throws InvalidClassException;

	/**
	* Returns the processed signal samples
	* for the active signal and the given channel.
	* @param channel the index of the channel
	* (range from 0 to {@code getActiveSignalDocument().getChannelCount()-1})
	* @param signalOffset the number of the sample in the signal starting
	* from which samples will be returned
	* @param count the number of samples to be returned
	* @return the signal samples
	* @throws NoActiveObjectException if there is no active signal
	* @throws IndexOutOfBoundsException if the index of a channel is out of range
	*/
	ChannelSamples getActiveProcessedSignalSamples(int channel, int signalOffset, int count) throws NoActiveObjectException, IndexOutOfBoundsException;

	/**
	* Returns the unprocessed signal samples (raw from the source)
	* for the signal from the given document and the given channel.
	* @param document the document with the signal. Must be returned from this
	* SvarogAcces (actually be of type SignalDocument - internal to Svarog).
	* @param channel the index of the channel
	* (range from 0 to {@code getActiveSignalDocument().getChannelCount()-1})
	* @param signalOffset the number of the sample in the signal starting
	* from which samples will be returned
	* @param count the number of samples to be returned
	* @return the signal samples
	* @throws InvalidClassException  if document is not returned from
	* this SvarogAccess (not of type SignalDocument - internal to Svarog)
	* @throws IndexOutOfBoundsException if the index of a channel is out of range
	*/
	ChannelSamples getRawSignalSamplesFromDocument(ExportedSignalDocument document, int channel, int signalOffset, int count) throws InvalidClassException, IndexOutOfBoundsException;

	/**
	* Returns the processed signal samples
	* for the signal from the given document and the given channel.
	* @param document the document with the signal. Must be returned from this
	* SvarogAcces (actually be of type SignalDocument - internal to Svarog).
	* @param channel the index of the channel
	* (range from 0 to {@code getActiveSignalDocument().getChannelCount()-1})
	* @param signalOffset the number of the sample in the signal starting
	* from which samples will be returned
	* @param count the number of samples to be returned
	* @return the signal samples
	* @throws InvalidClassException  if document is not returned from
	* this SvarogAccess (not of type SignalDocument - internal to Svarog)
	* @throws IndexOutOfBoundsException if the index of a channel is out of range
	*/
	ChannelSamples getProcessedSignalSamplesFromDocument(ExportedSignalDocument document, int channel, int signalOffset, int count) throws InvalidClassException, IndexOutOfBoundsException;

	/**
	 * Returns the processed signal samples
	 * for the active signal and the given channel.
	 * @param channel the index of the channel
	 * (range from 0 to {@code getActiveSignalDocument().getChannelCount()-1})
	 * @param signalOffsetTime the position in the signal (in time domain) starting
	 * from which samples will be returned
	 * @param length the length of the part of the signal which should be returned
	 * @return the signal samples
	 * @throws NoActiveObjectException if there is no active signal
	 * @throws IndexOutOfBoundsException if the index of a channel is out of range
	 */
	ChannelSamples getActiveProcessedSignalSamples(int channel, float signalOffsetTime, float length) throws NoActiveObjectException, IndexOutOfBoundsException;

	/**
	 * Returns the unprocessed signal samples (raw from the source)
	 * for the active signal and the given channel.
	 * @param channel the index of the channel
	 * (range from 0 to {@code getActiveSignalDocument().getChannelCount()-1})
	 * @param signalOffsetTime the position in the signal (in time domain) starting
	 * from which samples will be returned
	 * @param length the length of the part of the signal which should be returned
	 * @return the signal samples
	 * @throws NoActiveObjectException if there is no active signal
	 * @throws IndexOutOfBoundsException if the index of a channel is out of range
	 */
	ChannelSamples getActiveRawSignalSamples(int channel, float signalOffsetTime, float length) throws NoActiveObjectException, IndexOutOfBoundsException;

	/**
	 * Returns the unprocessed signal samples (raw from the source)
	 * for the signal from the given document and the given channel.
	 * @param document the document with the signal. Must be returned from this
	 * SvarogAcces (actually be of type SignalDocument - internal to Svarog).
	 * @param channel the index of the channel
	 * (range from 0 to {@code getActiveSignalDocument().getChannelCount()-1})
	 * @param signalOffsetTime the position in the signal (in time domain) starting
	 * from which samples will be returned
	 * @param length the length of the part of the signal which should be returned
	 * @return the signal samples
	 * @throws InvalidClassException  if document is not returned from
	 * this SvarogAccess (not of type SignalDocument - internal to Svarog)
	 * @throws IndexOutOfBoundsException if the index of a channel is out of range
	 */
	ChannelSamples getRawSignalSamplesFromDocument(ExportedSignalDocument document, int channel, float signalOffsetTime, float length) throws InvalidClassException, IndexOutOfBoundsException;

	/**
	 * Returns the processed signal samples
	 * for the signal from the given document and the given channel.
	 * @param document the document with the signal. Must be returned from this
	 * SvarogAcces (actually be of type SignalDocument - internal to Svarog).
	 * @param channel the index of the channel
	 * (range from 0 to {@code getActiveSignalDocument().getChannelCount()-1})
	 * @param signalOffsetTime the position in the signal (in time domain) starting
	 * from which samples will be returned
	 * @param length the length of the part of the signal which should be returned
	 * @return the signal samples
	 * @throws InvalidClassException  if document is not returned from
	 * this SvarogAccess (not of type SignalDocument - internal to Svarog)
	 * @throws IndexOutOfBoundsException if the index of a channel is out of range
	 */
	ChannelSamples getProcessedSignalSamplesFromDocument(ExportedSignalDocument document, int channel, float signalOffsetTime, float length) throws InvalidClassException, IndexOutOfBoundsException;



	/**
	 * Returns all {@link Tag tags} associated with the active tag
	 * document.
	 * @return the set of tags
	 * @throws NoActiveObjectException if there is no active tag document
	 */
	Set<ExportedTag> getTagsFromActiveDocument() throws NoActiveObjectException;

	/**
	 * Returns all {@link Tag tags} associated with the active signal.
	 * @return the set of tags
	 * @throws NoActiveObjectException if there is no active signal
	 */
	List<ExportedTag> getTagsFromAllDocumentsAssociatedWithAcitiveSignal() throws NoActiveObjectException;

	/**
	 * Returns all opened {@link Tag tags} (no matter with which
	 * document are associated).
	 * @return the list of tags
	 */
	List<ExportedTag> getTagsFromAllDocuments();

	/**
	 * Returns the active {@link Tag tag}.
	 * @return the active tag
	 * @throws NoActiveObjectException if there is no active tag
	 */
	ExportedTag getActiveTag() throws NoActiveObjectException;

	/**
	 * Returns {@link Tag tags} associated with the given {@link ExportedSignalDocument}.
	 * @param document the signalDocument. Must be returned from this
	 * SvarogAcces (actually be of type SignalDocument - internal to Svarog).
	 * @return the list of tags
	 * @throws InvalidClassException if document is not returned from
	 * this SvarogAccess (not of type SignalDocument - internal to Svarog)
	 */
	List<ExportedTag> getTagsFromSignalDocument(ExportedSignalDocument document) throws InvalidClassException;


	/**
	 * Returns the active {@link SignalSelection selection} of the part of the active signal.
	 * @return the active selection
	 * @throws NoActiveObjectException if there is no active selection
	 */
	ExportedSignalSelection getActiveSelection() throws NoActiveObjectException;

	/**
	 * Adds the {@link Tag tag} created form given {@link ExportedTag}
	 * to the active {@link TagDocument}.
	 * The tag has to have a {@link ExportedTagStyle} that exists in the active
	 * tag document (the set of styles can be obtained with
	 * {@code getActiveTagDocument().getTagStyles()}).
	 * @param tag the tag to be added
	 * @throws NoActiveObjectException if there is no active tag document
	 * @throws IllegalArgumentException if there is no such tag style (as the
	 * style of the tag) in the tag document
	 */
	void addTagToActiveTagDocument(ExportedTag tag) throws NoActiveObjectException, IllegalArgumentException;

	/**
	 * Adds the {@link Tag tag} created form given {@link ExportedTag}
	 * to the given {@link TagDocument}.
	 * The tag has to have a {@link ExportedTagStyle} that exists in the
	 * tag document (the set of styles can be obtained with
	 * {@code document.getTagStyles()}).
	 * @param document the document the tag will be added to. Must be returned from this
	 * SvarogAcces (actually be of type TagDocument - internal to Svarog).
	 * @param tag the tag to be added
	 * @throws InvalidClassException if document is not returned from
	 * this SvarogAccess (not of type TagDocument - internal to Svarog)
	 * @throws IllegalArgumentException if there is no such tag style (as the
	 * style of the tag) in the tag document
	 */
	void addTagToDocument(ExportedTagDocument document, ExportedTag tag) throws InvalidClassException, IllegalArgumentException;

	/**
	 * Returns the array of {@link TagDocument tag documents} associated with the currently
	 * active signal.
	 * @return the array of tag documents
	 * @throws NoActiveObjectException if there is no active signal
	 */
	ExportedTagDocument[] getTagDocumentsFromActiveSignal() throws NoActiveObjectException;

	/**
	 * Returns the active {@link TagDocument}.
	 * @return the active tag document
	 * @throws NoActiveObjectException if there is no active tag document
	 */
	ExportedTagDocument getActiveTagDocument() throws NoActiveObjectException;

	/**
	 * Returns the active "main" document (that is either a
	 * {@link ExportedSignalDocument signal} or a book;
	 * or something else if it will be added).
	 * @return the active document
	 * @throws NoActiveObjectException if there is no active document
	 */
	Document getActiveDocument() throws NoActiveObjectException;


	/**
	 * Returns the active {@link ExportedSignalDocument}.
	 * @return the active signal document
	 * @throws NoActiveObjectException if there is no active signal document
	 */
	ExportedSignalDocument getActiveSignalDocument() throws NoActiveObjectException;

	/**
	 * Opens the signal from the given file as the raw signal.
	 * Probably some additional parameters will be added.
	 * @param file the file with the signal
	 * @param samplingFrequency the number of samples per second
	 * @param channelCount the number of channels in the signal
	 * @param sampleType the {@link ExportedRawSignalSampleType type} of samples
	 * @param byteOrder the {@link ByteOrder order} of bits in the file
	 * @param calibration constant by which numbers from file are multiplied to get a physical value
	 * @param pageSize the length of the page (in seconds)
	 * @param blocksPerPage the number of blocks per page
	 * @throws SignalMLException if opening the file fails
	 * @throws IOException if opening the file fails due to IO error
	 */
	void openRawSignal(File file, float samplingFrequency, int channelCount, ExportedRawSignalSampleType sampleType, ByteOrder byteOrder, float calibration, float pageSize, int blocksPerPage) throws SignalMLException, IOException;

	/**
	 * Opens the signal from the given file using the given {@link SignalMLCodec codec}.
	 * @param file the file with the signal
	 * @param codecFormatName the name of the codec
	 * @param pageSize the length of the page (in seconds)
	 * @param blocksPerPage the number of blocks per page
	 * @throws IOException if opening the file fails due to IO error
	 * @throws IllegalArgumentException if there is no codec of that format name
	 * @throws SignalMLException if opening the file fails
	 */
	void openSignalWithCodec(File file, String codecFormatName, float pageSize, int blocksPerPage)
	throws IOException, IllegalArgumentException, SignalMLException;

	/**
	 * Adds a new {@link SignalMLCodec} codec to the set of codecs.
	 * @param codecFile the file with codec
	 * @param codecFormatName the name that will be used as codec's format name,
	 * null if the filename (without ".xml") should be used.
	 * @throws IOException if IO error occurs while opening the file
	 */
	void addCodec(File codecFile, String codecFormatName) throws IOException;

	/**
	 * Opens the tag document from the given file and adds it to the given signal document.
	 * @param file file with the tag
	 * @param signalDocument the signal document. Must be returned from this
	 * SvarogAcces (actually be of type SignalDocument - internal to Svarog).
	 * @param activeFlag flag indicating if opened document should be active
	 * @throws InvalidClassException if document is not returned from
	 * this SvarogAccess (not of type SignalDocument - internal to Svarog)
	 * @throws SignalMLException if opening the file fails
	 * @throws IOException if opening the file fails due to IO error
	 */
	void openTagDocument(File file, ExportedSignalDocument signalDocument, boolean activeFlag) throws InvalidClassException, SignalMLException, IOException;

	/**
	 * Opens the book from the given file.
	 * @param file the file with the book
	 * @throws IOException if opening the file fails due to IO error
	 * @throws SignalMLException if opening the file fails
	 */
	void openBook(File file) throws IOException, SignalMLException;

	/**
	 * Export the part of the signal to the given file.
	 * @param position the position in signal (seconds) where the exported
	 * part starts
	 * @param length the length (seconds) of the exported part of the signal
	 * @param channels the array with indexes of the channels that should be
	 * exported (from {@code 0} to {@code SignalDocument.getChannelCount()-1})
	 * @param level the {@link SignalSourceLevel level} of processing of
	 * the signal
	 * @param sampleType the {@link ExportedRawSignalSampleType type} of samples
	 * (short, int, float, double)
	 * @param byteOrder the {@link ByteOrder order} of bytes in which
	 * the signal should be written
	 * @param plot the {@link ExportedSignalPlot plot} for the
	 * signal. Must be returned from this
	 * SvarogAcces (actually be of type SignalPlot - internal to Svarog).
	 * @param file the file to which the signal will be exported
	 * @throws InvalidClassException if document is not returned from
	 * this SvarogAccess (not of type SignalDocument - internal to Svarog)
	 * @throws SignalMLException if the export failed due to other reasons
	 */
	void exportSignal(float position, float length, int[] channels, SignalSourceLevel level, ExportedRawSignalSampleType sampleType, ByteOrder byteOrder, ExportedSignalPlot plot, File file) throws InvalidClassException, SignalMLException;

	/**
	 * Creates and returns a temporary file.
	 * The file is created in the {@code %profile-directory%/temp}.
	 * When the File object is destroyed (by the Garbage Collector)
	 * the file is removed from disk.
	 * NOTE: it would be a good practice if you removed your temporary files
	 * when you finish using them. Especially if you use a lot of them or
	 * big ones.
	 * @param extension the suffix to the file name (for example {@code ".bin",
	 * ".xml"})
	 * @return the created temporary file
	 * @throws IOException if the file couldn't be created
	 */
	File getTemporaryFile(String extension) throws IOException;

}

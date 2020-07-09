/**
 *
 */
package org.signalml.plugin.export.view;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.InvalidClassException;
import javax.swing.JRootPane;
import javax.swing.JViewport;
import org.signalml.exception.SanityCheckException;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.signal.ExportedSignalSelection;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagDocument;
import org.signalml.plugin.export.signal.ExportedTagStyle;

/**
 * Interface for a plot with a signal.
 * Allows to:
 * <ul>
 * <li>move signal in the view one page/block forward or backward,</li>
 * <li>convert pixels to number of the channel, number of the page/block,
 * point in time and value and vice versa,</li>
 * <li>return a tag or selection,</li>
 * <li>create a tag or selection and set it,</li>
 * <li>return basic parameters of the signal, such as
 * 	<ul>
 * 	<li>length of block/page,</li>
 * 	<li>number of channels,</li>
 * 	<li>length of the signal (in the number of samples and in seconds),</li>
 * 	</ul>
 * <li>return the view and the document with which this plot is associated.</li>
 * </ul>
 * @author Marcin Szumski
 */
public interface ExportedSignalPlot {

	int SCALE_TO_SIGNAL_GAP = 0;
	int COMPARISON_STRIP_HEIGHT = 14;
	int COMPARISON_STRIP_MARGIN = 3;
	String TIME_ZOOM_FACTOR_PROPERTY = "timeZoomFactor";
	String VOLTAGE_ZOOM_FACTOR_PROPERTY = "voltageZoomFactor";
	String PIXEL_PER_CHANNEL_PROPERTY = "pixelPerChannel";

	/**
	 * Returns the number of pixels the view has to be moved right so that
	 * the next page (then this that is now first in view) is first in the view.
	 * @param position the view coordinates that appear in the upper left
	 * hand corner of the viewport
	 * @return the number of pixels the view has to be moved right
	 */
	int getPageForwardSkip(Point position);

	/**
	 * Returns the number of pixels the view has to be moved left so that
	 * the previous page (then this that is now first in view) is first in
	 * the view.
	 * @param position the view coordinates that appear in the upper left
	 * hand corner of the viewport
	 * @return the number of pixels the view has to be moved left
	 */
	int getPageBackwardSkip(Point position);

	/**
	 * Returns the number of pixels the view has to be moved right so that
	 * the next block (then this that is now first in view) is first in
	 * the view.
	 * @param position the view coordinates that appear in the upper left
	 * hand corner of the viewport
	 * @return the number of pixels the view has to be moved right
	 */
	int getBlockForwardSkip(Point position);

	/**
	 * Returns the number of pixels the view has to be moved left so that
	 * the previous block (then this that is now first in view) is first in
	 * the view.
	 * @param position the view coordinates that appear in the upper left
	 * hand corner of the viewport
	 * @return the number of pixels the view has to be moved left
	 */
	int getBlockBackwardSkip(Point position);

	/**
	 * Moves the view left so that the next page (then this that is now first
	 * in view) is first in the view.
	 */
	void pageForward();

	/**
	 * Moves the view right so that the previous page (then this that is now
	 * first in view) is first in the view.
	 */
	void pageBackward();

	/**
	 * Converts position in the view (pixel) to the position in signal
	 * (point in time and value at this point).
	 * @param p the position in the view
	 * @return point in time (first dimension) and value at this point (second
	 * dimension)
	 */
	Point2D.Float toSignalSpace(Point p);

	/**
	 * Converts the position in signal (point in time and value at this point)
	 * to the position in the view (pixel).
	 * @param p point in time (first dimension) and value at this point (second
	 * dimension)
	 * @return the position in the view
	 */
	Point toPixelSpace(Point2D.Float p);

	/**
	 * Returns the number of the page in which the given point is located.
	 * @param p the point in the view
	 * @return the number of the page (from 0 to {@link #getPageCount()} - 1)
	 */
	int toPageSpace(Point p);

	/**
	 * Returns the number of the block in which the given point is located.
	 * @param p the point in the view
	 * @return the number of the block (from 0 to {@link #getBlockCount()}-1)
	 */
	int toBlockSpace(Point p);

	/**
	 * Returns the point in time which is represented by a given point in the
	 * view.
	 * @param p the point in the view
	 * @return the point in time
	 */
	float toTimeSpace(Point p);

	/**
	 * Returns the number of the sample that is projected in the view as
	 * the last before given point.
	 * @param p the point in the view
	 * @return the number of the sample
	 */
	int toSampleSpace(Point p);

	/**
	 * Returns the value of the signal that is projected by the given point
	 * in the view
	 * @param p the point in the view
	 * @return the value of the signal
	 */
	float toValueSpace(Point p);

	/**
	 * Converts the point in time to the first coordinate of the pixel
	 * @param time the point in time
	 * @return the first coordinate of the pixel
	 */
	int timeToPixel(double time);

	/**
	 * Returns the second coordinate of the pixel that represents the point
	 * in the middle between channels {@code channel-1} and {@code channel}.
	 * @param channel the number of the channel
	 * @return the second coordinate of the pixel
	 */
	int channelToPixel(int channel);

	/**
	 * Returns the number of the channel in which the given point is located.
	 * Point is located in channel {@code n} if its second coordinate is
	 * between {@code #channelToPixel(n)} and {@code #channelToPixel(n+1)}.
	 * @param p the point in the view
	 * @return the number of the channel in which the given point is located
	 */
	int toChannelSpace(Point p);

	/**
	 * Creates the {@link ExportedSignalSelection signal selection} that is
	 * a page selection and contains pages from {@code fromPage} to
	 * {@code toPage} (or from {@code toPage} to {@code fromPage} if
	 * {@code fromPage > toPage}.
	 * @param fromPage the first page in the selection
	 * @param toPage the last page in the selection
	 * @return the created signal selection
	 */
	ExportedSignalSelection getPageSelection(int fromPage, int toPage);

	/**
	 * Creates the {@link ExportedSignalSelection signal selection} that is
	 * a block selection and contains blocks from {@code fromBlock} to
	 * {@code toBlock} (or from {@code toBlock} to {@code fromBlock} if
	 * {@code fromBlock > toBlock}.
	 * @param fromBlock the first block in the selection
	 * @param toBlock the last block in the selection
	 * @return the created signal selection
	 */
	ExportedSignalSelection getBlockSelection(int fromBlock, int toBlock);

	/**
	 * Creates the {@link ExportedSignalSelection signal selection} that is
	 * a channel selection and contains the time between {@code fromPosition}
	 * and {@code toBlock} (order of borders doesn't matter).
	 * @param fromPosition the beginning of the selection
	 * @param toPosition the end of the selection
	 * @param channel the number of the selected channel
	 * @return the created signal selection
	 */
	ExportedSignalSelection getChannelSelection(float fromPosition, float toPosition,
			int channel);

	/**
	 * Creates a {@link ExportedTag tag} based on the given selection
	 * in the given style and puts it in the given
	 * {@link ExportedTagDocument document}.
	 * Depending on the type of the selection calls:
	 * <ul>
	 * <li>{@link #tagBlockSelection(ExportedTagDocument, ExportedTagStyle,
	 * ExportedSignalSelection, boolean)}</li>
	 * <li>{@link #tagChannelSelection(ExportedTagDocument, ExportedTagStyle,
	 * ExportedSignalSelection, boolean)}</li>
	 * <li>{@link #tagPageSelection(ExportedTagDocument, ExportedTagStyle,
	 * ExportedSignalSelection, boolean)}</li>
	 * </ul>
	 * @param tagDocument the document in which the tag(s) should be put
	 * @param style the style of the tag
	 * @param selection the selection based on which the tag will be created
	 * @param selectNew true if the newly created tag should be set as active
	 * @throws InvalidClassException if the {@code tagDocument} is not returned
	 * from Svarog (is not of type {@code TagDocument}}.
	 */
	void tagSelection(ExportedTagDocument tagDocument, ExportedTagStyle style,
					  ExportedSignalSelection selection, boolean selectNew) throws InvalidClassException;

	/**
	 * Removes from the given {@link ExportedTagDocument tag document}
	 * {@link ExportedTag tagged selections} that intersect with
	 * a given selection and are of the same type as given.
	 * @param tagDocument the document from which the tags are to be removed
	 * @param selection the selection to which tagged selections will be
	 * compared
	 * @throws InvalidClassException if the {@code tagDocument} is not returned
	 * from Svarog (is not of type {@code TagDocument}}.
	 */
	void eraseTagsFromSelection(ExportedTagDocument tagDocument,
								ExportedSignalSelection selection) throws InvalidClassException;

	/**
	 * Creates a page {@link ExportedTag tag} based on the given page selection
	 * in the given page style and puts it in the given
	 * {@link ExportedTagDocument document}.
	 * @param tagDocument the document in which the tag(s) should be put
	 * @param style the style of the tag
	 * @param selection the selection based on which the tag will be created
	 * @param selectNew true if the newly created tag should be set as active
	 * @throws InvalidClassException if the {@code tagDocument} is not returned
	 * from Svarog (is not of type {@code TagDocument}}.
	 * @throws SanityCheckException if the {@code selection} is not a page
	 * selection or if the {@code style} is not a page style
	 */
	void tagPageSelection(ExportedTagDocument tagDocument, ExportedTagStyle style,
						  ExportedSignalSelection selection, boolean selectNew) throws InvalidClassException, SanityCheckException;

	/**
	 * Creates a block {@link ExportedTag tag} based on the given block selection
	 * in the given block style and puts it in the given
	 * {@link ExportedTagDocument document}.
	 * @param tagDocument the document in which the tag(s) should be put
	 * @param style the style of the tag
	 * @param selection the selection based on which the tag will be created
	 * @param selectNew true if the newly created tag should be set as active
	 * @throws InvalidClassException if the {@code tagDocument} is not returned
	 * from Svarog (is not of type {@code TagDocument}}.
	 * @throws SanityCheckException if the {@code selection} is not a block
	 * selection or if the {@code style} is not a block style
	 */
	void tagBlockSelection(ExportedTagDocument tagDocument, ExportedTagStyle style,
						   ExportedSignalSelection selection, boolean selectNew) throws InvalidClassException, SanityCheckException;

	/**
	 * Creates a channel {@link ExportedTag tag} based on the given channel
	 * selection in the given channel style and puts it in the given
	 * {@link ExportedTagDocument document}.
	 * @param tagDocument the document in which the tag(s) should be put
	 * @param style the style of the tag
	 * @param selection the selection based on which the tag will be created
	 * @param selectNew true if the newly created tag should be set as active
	 * @throws InvalidClassException if the {@code tagDocument} is not returned
	 * from Svarog (is not of type {@code TagDocument}}.
	 * @throws SanityCheckException if the {@code selection} is not a channel
	 * selection or if the {@code style} is not a channel style
	 */
	void tagChannelSelection(ExportedTagDocument tagDocument, ExportedTagStyle style,
							 ExportedSignalSelection selection, boolean selectNew) throws InvalidClassException, SanityCheckException;

	/**
	 * Returns the height (in pixel) of one channel in the view.
	 * @return the height (in pixel) of one channel in the view
	 */
	int getPixelPerChannel();

	/**
	 * Returns if the signal is antialiased.
	 * @return true if the signal is antialiased, false otherwise
	 */
	boolean isAntialiased();

	/**
	 * Returns the width (in pixels) of one second of the signal.
	 * @return the width (in pixels) of one second of the signal
	 */
	double getPixelPerSecond();

	/**
	 * Returns the width (in pixels) of one block of the signal.
	 * @return the width (in pixels) of one block of the signal
	 */
	double getPixelPerBlock();

	/**
	 * Returns the width (in pixels) of one page of the signal.
	 * @return the width (in pixels) of one page of the signal
	 */
	double getPixelPerPage();

	/**
	 * Returns the height (in pixels) of one point of value.
	 * @return the height (in pixels) of one point of value
	 */
	double getPixelPerValue();

	/**
	 * Returns the number of channels in the signal.
	 * @return the number of channels in the signal
	 */
	int getChannelCount();

	/**
	 * Returns the number of pages in the signal.
	 * @return the number of pages in the signal
	 */
	int getPageCount();

	/**
	 * Returns the number of blocks in the signal.
	 * @return the number of blocks in the signal
	 */
	int getBlockCount();

	/**
	 * Returns the length of the longest channel (in seconds).
	 * @return the length of the longest channel (in seconds)
	 */
	float getMaxTime();

	/**
	 * Returns the number of blocks in a page.
	 * @return the number of blocks in a page
	 */
	int getBlocksPerPage();

	/**
	 * Returns the length of the page in seconds.
	 * @return the length of the page in seconds
	 */
	float getPageSize();

	/**
	 * Returns the length of the block in seconds.
	 * @return the length of the block in seconds
	 */
	float getBlockSize();

	/**
	 * Returns the number of samples in the longest channel.
	 * @return the number of samples in the longest channel
	 */
	int getMaxSampleCount();

	/**
	 * Returns the {@link ExportedSignalDocument document} with which this plot
	 * is associated.
	 * @return the document with which this plot is associated
	 */
	ExportedSignalDocument getDocument();

	/**
	 * Returns the {@link ExportedSignalView view} with which this plot
	 * is associated.
	 * @return the view with which this plot is associated
	 */
	ExportedSignalView getView();

	/**
	 * Returns the plot from which this plot is dependent.
	 * @return the plot from which this plot is dependent
	 */
	ExportedSignalPlot getMasterPlot();

	/**
	 * Returns the width and height of this component.
	 * @return the width and height of this component
	 */
	Dimension getSize();

	/**
	 * Returns the number of pixels per sample.
	 * @return the number of pixels per sample
	 */
	double getTimeZoomFactor();

	/**
	 * Forwards the <code>scrollRectToVisible()</code> message to the
	 * parent. Components that can service
	 * the request, such as <code>JViewport</code>,
	 * override this method and perform the scrolling.
	 *
	 * @param aRect the visible <code>Rectangle</code>
	 * @see JViewport
	 */
	public void scrollRectToVisible(Rectangle aRect);

	/**
	 * Returns the <code>JRootPane</code> ancestor for this component.
	 *
	 * @return the <code>JRootPane</code> that contains this component,
	 *		or <code>null</code> if no <code>JRootPane</code> is found
	 */
	public JRootPane getRootPane();

	/**
	 * Returns the number of samples per second.
	 * @return the number of samples per second
	 */
	public float getSamplingFrequency();
}
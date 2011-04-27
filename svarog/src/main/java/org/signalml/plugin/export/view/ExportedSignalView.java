/**
 * 
 */
package org.signalml.plugin.export.view;

import java.io.InvalidClassException;

import org.signalml.app.view.signal.SignalPlot;
import org.signalml.plugin.export.signal.ExportedSignalDocument;
import org.signalml.plugin.export.signal.ExportedSignalSelection;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagStyle;
import org.signalml.plugin.export.signal.SignalSelectionType;

/**
 * Interface for a signal view that allows to:
 * <ul>
 * <li>return active {@link ExportedPositionedTag tag}, {@link ExportedTagStyle
 * tag style} and {@link ExportedSignalPlot signal plot},</li>
 * <li>return the {@link ExportedSignalSelection selection} of the signal,</li>
 * <li>set a selection and an active tag,</li>
 * <li>return currently selected tag {@link SignalSelectionType type} and
 * {@link ExportedTagStyle style},</li>
 * <li>return the main (master) signal plot,</li>
 * <li>return the {@link ExportedSignalDocument document} with which this view
 * is associated.</li>
 * </ul>
 * @author Marcin Szumski
 */
public interface ExportedSignalView {

	/**
	 * Returns the currently selected (active) {@link ExportedTagStyle style}.
	 * @return the currently selected (active) style
	 */
	ExportedTagStyle getActiveTagStyle();

	/**
	 * Returns the currently selected (active) {@link ExportedTag tag}.
	 * @return the currently selected (active) tag
	 */
	ExportedPositionedTag getActiveTag();

	/**
	 * Returns the active {@link ExportedSignalPlot signal plot}.
	 * @return the active signal plot
	 */
	ExportedSignalPlot getActiveSignalPlot();

	/**
	 * Returns the {@link ExportedSignalSelection selection} of the signal.
	 * @return the current selection of the signal
	 */
	ExportedSignalSelection getSignalSelection();

	/**
	 * Sets the active {@link ExportedSignalSelection selection} in
	 * a {@link ExportedSignalPlot plot}.
	 * There can be only one active selection in all plots,
	 * so both the selection and the plot are remembered. 
	 * Repaints the selection on old and new plot.
	 * @param plot the plot in which the selection is located
	 * @param signalSelection the signal selection
	 * @throws InvalidClassException if the plot was not returned from Svarog
	 * (it is not of type {@link SignalPlot}).
	 */
	void setSignalSelection(ExportedSignalPlot plot, ExportedSignalSelection signalSelection) throws InvalidClassException;

	/**
	 * Sets that there is no active {@link ExportedSignalSelection selection}.
	 */
	void clearSignalSelection();

	/**
	 * Returns the currently selected (active) {@link ExportedTag tag}.
	 * @return the currently selected (active) tag
	 */
	ExportedPositionedTag getTagSelection();

	/**
	 * Sets the active {@link ExportedPositionedTag tag selection} in
	 * a {@link ExportedSignalPlot plot}.
	 * There can be only one active tag selection in all plots,
	 * so both the selection and the plot are remembered. 
	 * Repaints the selection on old and new plot.
	 * @param plot the plot in which the tag selection is located
	 * @param tagSelection the tag selection
	 * @throws InvalidClassException if the plot was not returned from Svarog
	 * (it is not of type {@link SignalPlot}).
	 */
	void setTagSelection(ExportedSignalPlot plot, ExportedPositionedTag tagSelection) throws InvalidClassException;

	/**
	 * Sets that there is no active {@link ExportedPositionedTag tag selection}.
	 */
	void clearTagSelection();

	/**
	 * Returns the {@link ExportedSignalDocument document} with which this view is associated.
	 * @return the document with which this view is associated.
	 */
	ExportedSignalDocument getDocument();

	/**
	 * Returns the main (first) {@link ExportedSignalPlot signal plot}.
	 * @return the main (first) signal plot.
	 */
	ExportedSignalPlot getMasterPlot();

	/**
	 * Returns the {@link SignalSelectionType type} of the currently active
	 * tagging tool (the type in which the user can create tags).  
	 * @return the type of the currently active tagging tool or null if tagging signal
	 * tool is not active
	 */
	SignalSelectionType getCurrentTagType();

	/**
	 * Returns the currently selected {@link ExportedTagStyle style} of tags for
	 * a given {@link SignalSelectionType type} of tags (page, block channel).
	 * @param type the type for which the style is to be returned
	 * @return the currently selected style of tags for a given type of tags
	 * (page, block channel) or null if there is no style selected
	 */
	ExportedTagStyle getCurrentTagStyle(SignalSelectionType type);

}
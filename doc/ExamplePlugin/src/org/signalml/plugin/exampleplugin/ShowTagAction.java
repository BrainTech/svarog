/**
 * 
 */
package org.signalml.plugin.exampleplugin;

import javax.swing.AbstractAction;

import org.signalml.plugin.export.signal.ExportedSignalSelectionType;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagStyle;
import org.signalml.plugin.export.signal.SvarogAccessSignal;

/**
 * Abstract class to show a description of a tag/tags.
 * Allows to create a string that describes the given tag.
 * @author Marcin Szumski
 */
public abstract class ShowTagAction extends AbstractAction {

	
	private static final long serialVersionUID = 1L;
	
	/**
	 * the {@link SvarogAccessSignal access} to signal options 
	 */
	protected SvarogAccessSignal signalAccess;
	
	/**
	 * Constructor. Sets {@link SvarogAccessSignal signal access}.
	 * @param signalAccess access to set
	 */
	public ShowTagAction(SvarogAccessSignal signalAccess, String name) {
		super(name);
		this.signalAccess = signalAccess;
	}
	
	/**
	 * Creates a string that describes the given {@link ExportedTag tag}.
	 * Description contains {@link ExportedSignalSelectionType type}
	 * (BLOCK, PAGE, CHANNEL), {@link ExportedTagStyle style}, number of the
	 * channel (if it is a channel selection), position where tag starts and
	 * the length.
	 * @param tag the tag to be described
	 * @return the created description
	 */
	protected String tagToString(ExportedTag tag){
		String text = new String();
		text += "type: ";
		text += tag.getType().getName();
		text += ", style: ";
		text += tag.getStyle().getName();
		if (tag.getType().isChannel()){
			text+= ", channel: ";
			text+= tag.getChannel();
		}
		text += ",start: ";
		text += tag.getPosition();
		text += ",length: ";
		text += tag.getLength();
		text += "\n";
		return text;
	}
	
	

}

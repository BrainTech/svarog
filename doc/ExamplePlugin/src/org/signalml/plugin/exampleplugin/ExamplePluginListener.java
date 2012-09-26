package org.signalml.plugin.exampleplugin;

import javax.swing.JOptionPane;

import org.signalml.plugin.export.change.SvarogActiveTagEvent;
import org.signalml.plugin.export.change.SvarogTagEvent;
import org.signalml.plugin.export.change.SvarogTagListenerWithAcitve;
import org.signalml.plugin.export.change.SvarogTagStyleEvent;
import org.signalml.plugin.export.change.SvarogTagStyleListener;
import org.signalml.plugin.export.signal.ExportedTag;
import org.signalml.plugin.export.signal.ExportedTagDocument;
import org.signalml.plugin.export.signal.ExportedTagStyle;

/**
 * Listener for changes associated with {@link ExportedTag tags} and
 * {@link ExportedTagStyle tag styles} in Svarog.
 * When the change occurs, this class creates a pop-up with the description
 * of the change and of the changed elements.
 *
 * @author Marcin Szumski
 */
public class ExamplePluginListener implements SvarogTagListenerWithAcitve,
	SvarogTagStyleListener {

	/**
	 * Creates a string that describes the given {@link ExportedTagStyle
	 * tag style}. Description contains name, type (BLOCK, PAGE, CHANNEL),
	 * colour of the fill and description.
	 * @param event the {@link SvarogTagStyleEvent event} that contains
	 * a style
	 * @param text the string that will be added at the beginning of the
	 * created description
	 * @return the created description
	 */
	private String createTagStyleString(SvarogTagStyleEvent event, String text) {
		ExportedTagStyle style = event.getTagStyle();
		text += "\nname: " + style.getName();
		text += "\ntype: " + style.getType().getName();
		text += "\ncolor: " + style.getFillColor();
		text += "\ndescription: " + style.getDescription();
		return text;
	}

	/**
	 * Shows the pop-up with the added tag style.
	 */
	@Override
	public void tagStyleAdded(SvarogTagStyleEvent e) {
		String text = createTagStyleString(e, "Tag style added:");
		JOptionPane.showMessageDialog(null, text);
	}

	/**
	 * Shows the pop-up with the removed tag style.
	 */
	@Override
	public void tagStyleRemoved(SvarogTagStyleEvent e) {
		String text = createTagStyleString(e, "Tag style removed:");
		JOptionPane.showMessageDialog(null, text);
	}

	/**
	 * Shows the pop-up with the new value of the changed tag style.
	 */
	@Override
	public void tagStyleChanged(SvarogTagStyleEvent e) {
		String text = createTagStyleString(e, "Tag style changed. New value:");
		JOptionPane.showMessageDialog(null, text);
	}

	/**
	 * Creates a string that describes the given {@link ExportedTag tag}.
	 * Description contains type(BLOCK, PAGE, CHANNEL), style, number of the
	 * channel (if it is a channel selection), position where tag starts and
	 * the length.
	 * @param tag the tag to be described
	 * @return the created description
	 */
	private String tagToStirng(ExportedTag tag) {
		String text = new String();
		text += "\ntype: " + tag.getType().getName();
		text += "\nstyle: " + tag.getStyle().getName();
		if (tag.getType().isChannel()) {
			text+= "\nchannel: " + tag.getChannel();
		}
		text += "\nstart: " + tag.getPosition();
		text += "\nlength: " + tag.getLength();
		return text;
	}

	/**
	 * Creates a string that describes the given {@link ExportedTag tag}.
	 * Description consists of the {@link #tagToStirng(ExportedTag) description
	 * of the tag} and the description of the {@link ExportedTagDocument document}
	 * in which this tag is located (name of the document, size of the block
	 * and the page of the signal).
	 * @param event the {@link SvarogTagEvent event} that contains both tag
	 * and document
	 * @param text the string that will be added at the beginning of the
	 * created description
	 * @return the created description
	 */
	private String createTagString(SvarogTagEvent event, String text) {
		ExportedTag tag = event.getTag();
		ExportedTagDocument document = event.getDocument();
		text += tagToStirng(tag);
		text += "\n";
		text += "\nFor document:";
		text += "\nname: " + document.getName();
		text += "\nblock size: " + document.getBlockSize();
		text += "\npage size: " + document.getPageSize();
		return text;

	}

	/**
	 * Shows the pop-up with the description of the added {@link ExportedTag
	 * tag} and the document to which this tag was added.
	 */
	@Override
	public void tagAdded(SvarogTagEvent e) {
		String text = createTagString(e, "Tag added:");
		JOptionPane.showMessageDialog(null, text);

	}

	/**
	 * Shows the pop-up with the description of the removed {@link ExportedTag
	 * tag} and the document from which this tag was removed.
	 */
	@Override
	public void tagRemoved(SvarogTagEvent e) {
		String text = createTagString(e, "Tag removed:");
		JOptionPane.showMessageDialog(null, text);
	}

	/**
	 * Shows the pop-up with the description of the changed {@link ExportedTag
	 * tag} and the document in which this tag is located.
	 */
	@Override
	public void tagChanged(SvarogTagEvent e) {
		String text = createTagString(e, "Tag changed:");
		JOptionPane.showMessageDialog(null, text);

	}

	/**
	 * Shows the pop-up with the description of the active {@link ExportedTag
	 * tag} and the tag that was active before this change.
	 */
	@Override
	public void activeTagChanged(SvarogActiveTagEvent e) {
		String text = new String();
		text += "Active tag changed:";
		text += "\nNew tag:";
		if (e.getTag() != null)
			text += tagToStirng(e.getTag());
		else
			text += "\nnull";
		text += "\n\nOld tag:";
		if (e.getOldTag() != null)
			text += tagToStirng(e.getOldTag());
		else
			text += "\nnull";
		JOptionPane.showMessageDialog(null, text);
	}

}

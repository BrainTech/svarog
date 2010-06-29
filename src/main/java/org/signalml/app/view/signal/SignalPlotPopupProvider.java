/* SignalPlotPopupProvider.java created 2007-10-16
 *
 */

package org.signalml.app.view.signal;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;

import org.signalml.app.action.EditTagAnnotationAction;
import org.signalml.app.action.PreciseSelectionAction;
import org.signalml.app.action.RemoveTagAction;
import org.signalml.app.action.TagSelectionAction;
import org.signalml.app.document.TagDocument;
import org.signalml.app.view.tag.TagIconProducer;
import org.signalml.app.view.tag.TagStyleMenu;
import org.signalml.domain.signal.SignalSelection;
import org.springframework.context.support.MessageSourceAccessor;

/** SignalPlotPopupProvider
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class SignalPlotPopupProvider {

	private RemoveTagAction removeTagAction;
	private PreciseSelectionAction preciseSelectionAction;
	private TagSelectionAction tagSelectionAction;
	private EditTagAnnotationAction editTagAnnotationAction;

	private TagIconProducer tagIconProducer;

	private SignalPlot plot;
	private JPopupMenu plotPopupMenu;
	private JPopupMenu columnHeaderPopupMenu;

	private TagStyleMenu tagStyleMenu;

	private MessageSourceAccessor messageSource;

	public SignalPlotPopupProvider(SignalPlot plot) {
		this.plot = plot;
	}

	public JPopupMenu getPlotPopupMenu() {

		if (plotPopupMenu == null) {
			plotPopupMenu = new JPopupMenu();
			tagStyleMenu = new TagStyleMenu(tagSelectionAction, tagIconProducer);
			tagStyleMenu.setMessageSource(messageSource);
			plotPopupMenu.add(tagStyleMenu);
			plotPopupMenu.addSeparator();
			plotPopupMenu.add(editTagAnnotationAction);
			plotPopupMenu.add(removeTagAction);
			plotPopupMenu.addSeparator();
			plotPopupMenu.add(preciseSelectionAction);
		}

		boolean tagStyleMenuEnabled = false;
		TagDocument tagDocument = plot.getDocument().getActiveTag();
		if (tagDocument != null) {
			SignalSelection selection = plot.getView().getSignalSelection(plot);
			if (selection != null) {
				tagStyleMenu.setTagSet(tagDocument.getTagSet());
				tagStyleMenu.setType(selection.getType());
				tagStyleMenuEnabled = true;
			}
		}
		tagStyleMenu.setEnabled(tagStyleMenuEnabled);

		return plotPopupMenu;
	}

	public MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}

	public JPopupMenu getColumnHeaderPopupMenu() {

		if (columnHeaderPopupMenu == null) {
			columnHeaderPopupMenu = new JPopupMenu();
			columnHeaderPopupMenu.add(editTagAnnotationAction);
			columnHeaderPopupMenu.add(removeTagAction);
			columnHeaderPopupMenu.addSeparator();
			JCheckBoxMenuItem compactMenuItem = new JCheckBoxMenuItem(plot.getSignalPlotColumnHeader().getSetCompactAction());
			columnHeaderPopupMenu.add(compactMenuItem);
		}

		return columnHeaderPopupMenu;
	}

	public TagIconProducer getTagIconProducer() {
		return tagIconProducer;
	}

	public void setTagIconProducer(TagIconProducer tagIconProducer) {
		this.tagIconProducer = tagIconProducer;
	}

	public RemoveTagAction getRemoveTagAction() {
		return removeTagAction;
	}

	public void setRemoveTagAction(RemoveTagAction removeTagAction) {
		this.removeTagAction = removeTagAction;
	}

	public PreciseSelectionAction getPreciseSelectionAction() {
		return preciseSelectionAction;
	}

	public void setPreciseSelectionAction(PreciseSelectionAction preciseSelectionAction) {
		this.preciseSelectionAction = preciseSelectionAction;
	}

	public TagSelectionAction getTagSelectionAction() {
		return tagSelectionAction;
	}

	public void setTagSelectionAction(TagSelectionAction tagSelectionAction) {
		this.tagSelectionAction = tagSelectionAction;
	}

	public EditTagAnnotationAction getEditTagAnnotationAction() {
		return editTagAnnotationAction;
	}

	public void setEditTagAnnotationAction(EditTagAnnotationAction editTagAnnotationAction) {
		this.editTagAnnotationAction = editTagAnnotationAction;
	}

}

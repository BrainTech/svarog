/* ActiveTagPopupDialog.java created 2007-10-14
 *
 */

package org.signalml.app.view.signal.popup;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.signalml.app.document.TagDocument;
import org.signalml.app.view.element.TitledCrossBorder;
import org.signalml.app.view.signal.SignalView;
import org.signalml.plugin.export.SignalMLException;
import org.signalml.plugin.export.view.AbstractPopupDialog;
import org.springframework.context.support.MessageSourceAccessor;

/** ActiveTagPopupDialog
 *
 *
 * @author Michal Dobaczewski &copy; 2007-2008 CC Otwarte Systemy Komputerowe Sp. z o.o.
 */
public class ActiveTagPopupDialog extends AbstractPopupDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private SignalView signalView;

	private ButtonGroup buttonGroup;
	private JRadioButton[] radioButtons;
	private TagDocument[] tagDocuments;
	private Map<TagDocument,JRadioButton> tagToButtonMap;
	private Map<ButtonModel,TagDocument> buttonToTagMap;

	public ActiveTagPopupDialog(MessageSourceAccessor messageSource, Window w, boolean isModal) {
		super(messageSource, w, isModal);
	}

	public SignalView getSignalView() {
		return signalView;
	}

	public void setSignalView(SignalView signalView) {
		this.signalView = signalView;
	}

	@Override
	public JComponent createInterface() {

		buttonGroup = new ButtonGroup();
		tagToButtonMap = new HashMap<TagDocument, JRadioButton>();
		buttonToTagMap = new HashMap<ButtonModel, TagDocument>();

		JPanel interfacePanel = new JPanel();

		List<TagDocument> tags = signalView.getDocument().getTagDocuments();
		int cnt = tags.size();
		tagDocuments = new TagDocument[cnt];
		tags.toArray(tagDocuments);

		interfacePanel.setLayout(new GridLayout(cnt, 1, 3, 3));

		CompoundBorder cb = new CompoundBorder(
		        new TitledCrossBorder(messageSource.getMessage("activeTag.title"), true),
		        new EmptyBorder(3,3,3,3)
		);
		interfacePanel.setBorder(cb);

		radioButtons = new JRadioButton[cnt];

		JRadioButton button;
		for (int i=0; i<cnt; i++) {
			String message;
			if (tagDocuments[i].getBackingFile() == null) {
				message = messageSource.getMessage("activeTag.newTag", new Object[] { tagDocuments[i].getName() });
			} else {
				message = messageSource.getMessage("activeTag.tag", new Object[] { tagDocuments[i].getName() });
			}
			button = new JRadioButton(message);
			buttonGroup.add(button);
			tagToButtonMap.put(tagDocuments[i], button);
			buttonToTagMap.put(button.getModel(), tagDocuments[i]);
			interfacePanel.add(button);
			button.addActionListener(this);
			radioButtons[i] = button;
		}

		Dimension size = interfacePanel.getPreferredSize();
		if (size.width < 150) {
			size.width = 150;
		}
		interfacePanel.setPreferredSize(size);

		return interfacePanel;

	}

	@Override
	public void fillDialogFromModel(Object model) throws SignalMLException {
		TagDocument document = signalView.getDocument().getActiveTag();
		if (signalView.isComparingTags()) {
			TagDocument[] comparedTags = signalView.getComparedTags();
			for (int i=0; i<radioButtons.length; i++) {
				if (tagDocuments[i] == comparedTags[0] || tagDocuments[i] == comparedTags[1]) {
					radioButtons[i].setEnabled(true);
				} else {
					radioButtons[i].setEnabled(false);
				}
			}
		} else {
			for (int i=0; i<radioButtons.length; i++) {
				radioButtons[i].setEnabled(true);
			}
		}
		if (document != null) {
			JRadioButton button = tagToButtonMap.get(document);
			if (button != null) {
				buttonGroup.setSelected(button.getModel(), true);
			}
		}
	}

	@Override
	public void fillModelFromDialog(Object model) throws SignalMLException {
		ButtonModel buttonModel = buttonGroup.getSelection();
		if (buttonModel != null) {
			TagDocument document = buttonToTagMap.get(buttonModel);
			if (document != null) {
				signalView.getDocument().setActiveTag(document);
			}
		}
	}

	@Override
	public boolean supportsModelClass(Class<?> clazz) {
		return (clazz == null);
	}

	@Override
	public boolean isControlPanelEquipped() {
		return false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getOkAction().actionPerformed(new ActionEvent(this, 0, "ok"));
	}

}
